package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.dao.DataItemDao;
import njgis.opengms.portal.dao.ModelItemDao;
import njgis.opengms.portal.dao.UserDao;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.Localization;
import njgis.opengms.portal.entity.doo.modelItem.ModelItemRelate;
import njgis.opengms.portal.entity.doo.modelItem.ModelRelation;
import njgis.opengms.portal.entity.dto.modelItem.ModelItemAddDTO;
import njgis.opengms.portal.entity.dto.modelItem.ModelItemFindDTO;
import njgis.opengms.portal.entity.dto.modelItem.ModelItemResultDTO;
import njgis.opengms.portal.entity.dto.modelItem.ModelItemUpdateDTO;
import njgis.opengms.portal.entity.po.DataItem;
import njgis.opengms.portal.entity.po.ModelItem;
import njgis.opengms.portal.entity.po.User;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.utils.ImageUtils;
import njgis.opengms.portal.utils.ResultUtils;
import njgis.opengms.portal.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * @ClassName ModelItemService
 * @Description 模型条目业务层
 * @Author Kai
 * @Date 2019/2/21
 * @Version 1.0.0
 */

@Service
public class ModelItemService {

    @Value("${resourcePath}")
    private String resourcePath;

    @Value("${htmlLoadPath}")
    private String htmlLoadPath;

    @Value(value = "Public,Discoverable")
    private List<String> itemStatusVisible;

    @Autowired
    ModelItemDao modelItemDao;

    @Autowired
    ModelItemService modelItemService;

    @Autowired
    ModelClassificationService modelClassificationService;

    @Autowired
    DataItemDao dataItemDao;

    @Autowired
    UserDao userDao;

    @Autowired
    UserService userService;

    /**
     * @Description 添加模型条目
     * @param modelItemAddDTO 条目信息
     * @param email 条目作者
     * @Return njgis.opengms.portal.entity.po.ModelItem
     * @Author kx
     * @Date 2021/7/7
     **/
    public ModelItem insert(ModelItemAddDTO modelItemAddDTO, String email) {

        ModelItem modelItem = new ModelItem();
        BeanUtils.copyProperties(modelItemAddDTO, modelItem);
        Date now = new Date();
        modelItem.setCreateTime(now);
        modelItem.setLastModifyTime(now);
        modelItem.setStatus(modelItemAddDTO.getStatus());
        modelItem.setMetadata(modelItemAddDTO.getMetaData());
        modelItem.setAuthor(email);
        modelItem.setId(UUID.randomUUID().toString());
        String name = modelItemAddDTO.getName();
        modelItem.setAccessId(Utils.generateAccessId(name,modelItemDao.findAllByAccessIdContains(name),false));
        //localization图片本地化存储
        List<Localization> localizationList = modelItem.getLocalizationList();
        for(int l = 0;l<localizationList.size();l++){
            Localization localization = localizationList.get(l);
            localization.setDescription(ImageUtils.saveBase64Image(localization.getDescription(),modelItem.getId(),resourcePath,htmlLoadPath));
            localizationList.set(l,localization);
        }
        modelItem.setLocalizationList(localizationList);

        String path="/modelItem/" + UUID.randomUUID().toString() + ".jpg";

        if(modelItemAddDTO.getUploadImage()!=null){
            String[] strs=modelItemAddDTO.getUploadImage().split(",");
            if(strs.length>1) {
                String imgStr = modelItemAddDTO.getUploadImage().split(",")[1];
                ImageUtils.base64StrToImage(imgStr, resourcePath + path);
                modelItem.setImage(path);
            }
            else {
                modelItem.setImage("");
            }
        }else{
            modelItem.setImage(null);
        }

        ModelItemRelate modelItemRelate=new ModelItemRelate();

        modelItem.setRelate(new ModelItemRelate());
        modelItemDao.insert(modelItem);
        //用户条目计数加一
        userService.ItemCountPlusOne(email,ItemTypeEnum.ModelItem);

        return modelItem;

    }

    /**
     * @Description 根据id删除模型条目，删除之前
     * @param id
     * @param email
     * @Return int
     * @Author kx
     * @Date 2021/7/7
     **/

    public JsonResult delete(String id, String email){
        ModelItem modelItem=modelItemDao.findFirstById(id);
        if(!modelItem.getAuthor().equals(email)) {
            return ResultUtils.error(-1, "access denied");
        } else if(modelItem!=null){
            //删除图片
            String image=modelItem.getImage();
            if(image.contains("/modelItem/")) {
                //删除旧图片
                File file=new File(resourcePath+modelItem.getImage());
                if(file.exists()&&file.isFile())
                    file.delete();
            }

            //删除与之关联模型中的记录
            List<ModelRelation> relatedModelList=modelItem.getRelate().getModelRelationList();
            for(int i=0;i<relatedModelList.size();i++){
                ModelItem modelItem1 = modelItemDao.findFirstById(relatedModelList.get(i).getOid());
                List<ModelRelation> relatedModelList1 = modelItem1.getRelate().getModelRelationList();
                for(int j = 0;j<relatedModelList1.size();j++){
                    if(relatedModelList1.get(j).getOid().equals(modelItem.getId())){
                        modelItem1.getRelate().getModelRelationList().remove(j);
                        modelItemDao.save(modelItem1);
                    }
                }
            }

            //TODO 同时删除conceptualModel logicalModel computableModel中的关联记录

            //删除与之关联数据中的记录
            List<String> relatedData=modelItem.getRelate().getDataItems();
            for(int i=0;i<relatedData.size();i++){
                DataItem dataItem=dataItemDao.findFirstById(relatedData.get(i));
                dataItem.getRelatedModels().remove(id);
                dataItemDao.save(dataItem);
            }

            //TODO 同时删除datahub dataApplication中的关联记录

            modelItemDao.delete(modelItem);
            userService.ItemCountMinusOne(email, ItemTypeEnum.ModelItem);
            return ResultUtils.success();
        }
        else{
            return ResultUtils.error(-2, "Error");
        }
    }

    /**
     * @Description 根据查询条件查询符合条件的模型条目
     * @param modelItemFindDTO
     * @Return com.alibaba.fastjson.JSONObject
     * @Author kx
     * @Date 2021/7/7
     **/
    public JSONObject query(ModelItemFindDTO modelItemFindDTO, Boolean containPrivate) {
        JSONObject queryResult = new JSONObject();

        //查询条件梳理
        int page = modelItemFindDTO.getPage();
        int pageSize = modelItemFindDTO.getPageSize();
        String searchText = modelItemFindDTO.getSearchText();
        String sortField = modelItemFindDTO.getSortField();
        String authorEmail = modelItemFindDTO.getAuthorEmail();

        Sort sort = Sort.by(modelItemFindDTO.getAsc() ? Sort.Direction.ASC : Sort.Direction.DESC, sortField);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

        //取出要查询的所有分类
        List<String> classIdList = modelItemFindDTO.getClassifications();
        classIdList = modelClassificationService.getAllClsIdByClsList(classIdList);

        //根据不同的查询字段进行查询
        Page<ModelItemResultDTO> modelItemPage = null;
        //若未指定author，则查询全部公开的条目
        if(authorEmail == null || authorEmail.trim().equals("")) {
            //未指定分类
            if(classIdList.size()==0) {
                switch (modelItemFindDTO.getQueryField()) {
                    case "Name":
                        modelItemPage = modelItemDao.findByNameContainsIgnoreCaseAndStatusIn(searchText, itemStatusVisible, pageable);
                        break;
                    case "Keyword":
                        modelItemPage = modelItemDao.findByKeywordsIgnoreCaseInAndStatusIn(searchText, itemStatusVisible, pageable);
                        break;
                    case "Content":
                        modelItemPage = modelItemDao.findByOverviewContainsIgnoreCaseAndLocalizationDescriptionAndStatusIn(searchText, itemStatusVisible, pageable);
                        break;
                    case "Contributor":
                        List<User> userList = userDao.findAllByNameContainsIgnoreCase(searchText);
                        List<String> emailList = new ArrayList<>();
                        for (User user : userList) {
                            emailList.add(user.getEmail());
                        }
                        modelItemPage = modelItemDao.findByAuthorInAndStatusInOrContributorsInAndStatusIn(emailList, itemStatusVisible, emailList, itemStatusVisible, pageable);
                        break;
                }
            } else{ //获取某一分类下所有条目
                modelItemPage = modelItemDao.findByClassificationsInAndStatusIn(classIdList, itemStatusVisible, pageable);
            }
        }else{
            //指定查询某一用户公开条目
            if(!containPrivate){
                switch (modelItemFindDTO.getQueryField()) {
                    case "Name":
                        modelItemPage = modelItemDao.findByNameContainsIgnoreCaseAndAuthorAndStatusIn(searchText, authorEmail, itemStatusVisible, pageable);
                        break;
                    case "Keyword":
                        modelItemPage = modelItemDao.findByKeywordsIgnoreCaseInAndAuthorAndStatusIn(searchText, authorEmail, itemStatusVisible, pageable);
                        break;
                    case "Content":
                        modelItemPage = modelItemDao.findByOverviewContainsIgnoreCaseAndLocalizationDescriptionAndAuthorAndStatusIn(searchText, authorEmail, itemStatusVisible, pageable);
                        break;
                }
            }else{//查询某一用户所有条目
                switch (modelItemFindDTO.getQueryField()) {
                    case "Name":
                        modelItemPage = modelItemDao.findByNameContainsIgnoreCase(searchText, pageable);
                        break;
                    case "Keyword":
                        modelItemPage = modelItemDao.findByKeywordsIgnoreCaseIn(searchText, pageable);
                        break;
                    case "Content":
                        modelItemPage = modelItemDao.findByOverviewContainsIgnoreCaseAndLocalizationDescription(searchText, pageable);
                        break;
                }
            }

        }

        //获取模型条目的创建者信息
        List<ModelItemResultDTO> modelItems = modelItemPage.getContent();
        JSONArray users = new JSONArray();
        for (int i = 0; i < modelItems.size(); i++) {
            ModelItemResultDTO modelItem = modelItems.get(i);
            String image = modelItem.getImage();
            if (!image.equals("")) {
                modelItem.setImage(htmlLoadPath + image);
            }

            JSONObject userObj = new JSONObject();
            User user = userDao.findFirstByEmail(modelItems.get(i).getAuthor());
            userObj.put("accessId", user.getAccessId());
            userObj.put("image", user.getAvatar().equals("") ? "" : htmlLoadPath + user.getAvatar());
            userObj.put("name", user.getName());

            users.add(userObj);

        }

        queryResult.put("list", modelItems);
        queryResult.put("total", modelItemPage.getTotalElements());
        queryResult.put("pages", modelItemPage.getTotalPages());
        queryResult.put("users", users);

        return queryResult;

    }


//    public JSONObject update(ModelItemUpdateDTO modelItemUpdateDTO, String uid){
//        ModelItem modelItem=modelItemDao.findFirstById(modelItemUpdateDTO.getOriginId());
//
//        String author=modelItem.getAuthor();
//        String authorUserName = author;
//        if(!modelItem.isLock()) {
//            if (author.equals(uid)) {
//
//                List<String> versions = modelItem.getVersions();
//                if (versions == null || versions.size() == 0) {
//                    ModelItemVersion modelItemVersionOri = new ModelItemVersion();
//                    BeanUtils.copyProperties(modelItem, modelItemVersionOri, "id");
//                    modelItemVersionOri.setOid(UUID.randomUUID().toString());
//                    modelItemVersionOri.setOriginOid(modelItem.getOid());
//                    modelItemVersionOri.setVerNumber((long) 0);
//                    modelItemVersionOri.setVerStatus(2);
//                    modelItemVersionOri.setModifier(modelItem.getAuthor());
//                    modelItemVersionOri.setModifyTime(modelItem.getCreateTime());
//                    modelItemVersionDao.insert(modelItemVersionOri);
//
//                    versions = new ArrayList<>();
//                    versions.add(modelItemVersionOri.getOid());
//                    modelItem.setVersions(versions);
//                }
//
//                BeanUtils.copyProperties(modelItemUpdateDTO, modelItem);
//                //判断是否为新图片
//                String uploadImage = modelItemUpdateDTO.getUploadImage();
//                if (uploadImage != null && !uploadImage.contains("/modelItem/") && !uploadImage.equals("")) {
//                    //删除旧图片
//                    File file = new File(resourcePath + modelItem.getImage());
//                    if (file.exists() && file.isFile())
//                        file.delete();
//                    //添加新图片
//                    String path = "/modelItem/" + UUID.randomUUID().toString() + ".jpg";
//                    String imgStr = uploadImage.split(",")[1];
//                    Utils.base64StrToImage(imgStr, resourcePath + path);
//                    modelItem.setImage(path);
//                }
//                Date curDate = new Date();
//                modelItem.setLastModifyTime(curDate);
//                modelItem.setLastModifier(author);
//
//                List<Localization> localizationList = modelItem.getLocalizationList();
//                for(int l = 0;l<localizationList.size();l++){
//                    Localization localization = localizationList.get(l);
//                    localization.setDescription(Utils.saveBase64Image(localization.getDescription(),modelItem.getOid(),resourcePath,htmlLoadPath));
//                    localizationList.set(l,localization);
//                }
//                modelItem.setLocalizationList(localizationList);
//
//                ModelItemVersion modelItemVersion = new ModelItemVersion();
//                BeanUtils.copyProperties(modelItem,modelItemVersion,"id");
//                modelItemVersion.setOriginOid(modelItem.getOid());
//                modelItemVersion.setOid(UUID.randomUUID().toString());
//                modelItemVersion.setModifier(author);
//                modelItemVersion.setModifyTime(curDate);
//                modelItemVersion.setVerNumber(curDate.getTime());
//                modelItemVersion.setVerStatus(2);
//                modelItemVersion.setCreator(author);
//                modelItemVersionDao.insert(modelItemVersion);
//
//
//                versions.add(modelItemVersion.getOid());
//                modelItem.setVersions(versions);
//
//                modelItemDao.save(modelItem);
//
//                JSONObject result = new JSONObject();
//                result.put("method", "update");
//                result.put("oid", modelItem.getOid());
//
//                return result;
//            } else {
//
//                ModelItemVersion modelItemVersion = new ModelItemVersion();
//                BeanUtils.copyProperties(modelItemUpdateDTO, modelItemVersion, "id");
//
//                String uploadImage = modelItemUpdateDTO.getUploadImage()==null?"":modelItemUpdateDTO.getUploadImage();
//                if(uploadImage.equals("")){
//                    modelItemVersion.setImage("");
//                }
//                else if (!uploadImage.contains("/modelItem/") && !uploadImage.equals("")) {
//                    String path = "/modelItem/" + UUID.randomUUID().toString() + ".jpg";
//                    String imgStr = uploadImage.split(",")[1];
//                    Utils.base64StrToImage(imgStr, resourcePath + path);
//                    modelItemVersion.setImage(path);
//                }
//                else{
//                    String[] names=uploadImage.split("modelItem");
//                    modelItemVersion.setImage("/modelItem/"+names[1]);
//                }
//
//                modelItemVersion.setOriginOid(modelItem.getOid());
//                modelItemVersion.setOid(UUID.randomUUID().toString());
//                modelItemVersion.setModifier(uid);
//                Date curDate = new Date();
//                modelItemVersion.setModifyTime(curDate);
//                modelItemVersion.setVerNumber(curDate.getTime());
//                modelItemVersion.setVerStatus(0);
//                userService.messageNumPlusPlus(authorUserName);
//
//                List<Localization> localizationList = modelItemVersion.getLocalizationList();
//                for(int l = 0;l<localizationList.size();l++){
//                    Localization localization = localizationList.get(l);
//                    localization.setDescription(Utils.saveBase64Image(localization.getDescription(),modelItemVersion.getOid(),resourcePath,htmlLoadPath));
//                    localizationList.set(l,localization);
//                }
//                modelItemVersion.setLocalizationList(localizationList);
//
//                modelItemVersion.setCreator(author);
//                modelItemVersionDao.insert(modelItemVersion);
//
//                modelItem.setLock(true);
//                modelItemDao.save(modelItem);
//
//                JSONObject result = new JSONObject();
//                result.put("method", "version");
//                result.put("oid", modelItemVersion.getOid());
//
//                return result;
//            }
//        }
//        else{
//
//            return null;
//        }
//    }




}
