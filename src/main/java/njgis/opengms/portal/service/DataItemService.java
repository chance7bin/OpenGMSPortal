package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.PortalApplication;
import njgis.opengms.portal.dao.*;
import njgis.opengms.portal.entity.doo.AuthorInfo;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.Localization;
import njgis.opengms.portal.entity.doo.base.PortalItem;
import njgis.opengms.portal.entity.doo.data.InvokeService;
import njgis.opengms.portal.entity.dto.ResultDTO;
import njgis.opengms.portal.entity.dto.SpecificFindDTO;
import njgis.opengms.portal.entity.dto.data.dataItem.DataItemDTO;
import njgis.opengms.portal.entity.dto.dataItem.DataItemFindDTO;
import njgis.opengms.portal.entity.po.*;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.enums.OperationEnum;
import njgis.opengms.portal.utils.ResultUtils;
import njgis.opengms.portal.utils.Utils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/04
 */
@Slf4j
@Service
public class DataItemService {

    @Autowired
    DataItemDao dataItemDao;

    @Autowired
    UserService userService;

    @Autowired
    ModelItemDao modelItemDao;

    @Autowired
    UserDao userDao;

    @Autowired
    DataCategorysDao dataCategorysDao;

    @Autowired
    DataHubDao dataHubDao;

    @Autowired
    DataMethodDao dataMethodDao;

    @Autowired
    ClassificationDao classificationDao;

    @Autowired
    GenericService genericService;

    @Autowired
    VersionService versionService;

    @Autowired
    NoticeService noticeService;

    @Value("${htmlLoadPath}")
    private String htmlLoadPath;

    @Value("${resourcePath}")
    private String resourcePath;

    @Autowired
    RedisService redisService;

    public JsonResult getItems(SpecificFindDTO dataItemFindDTO){
        return ResultUtils.success(genericService.searchItems(dataItemFindDTO, ItemTypeEnum.DataItem));
    }

    public ModelAndView getPage(DataItem dataItem, GenericItemDao genericItemDao){
        ModelAndView view = new ModelAndView();

        dataItem = (DataItem)genericService.recordViewCount(dataItem);
        genericItemDao.save(dataItem);

        //用户信息

        JSONObject userJson = userService.getItemUserInfoByEmail(dataItem.getAuthor());

        //authorship
        List<AuthorInfo> authorshipList= dataItem.getAuthorships();
        String authorshipString = getAuthorshipString(authorshipList);

        //related models
        List<String> relatedModels= dataItem.getRelatedModels();
        JSONArray modelItemArray = getModelItemArray(relatedModels);

        ArrayList<String> fileName = new ArrayList<>();
        if (null!=dataItem.getDataType()&&dataItem.getDataType().equals("DistributedNode")){
            fileName.add(dataItem.getName());
        }
        //设置远程数据内容
        List<InvokeService> invokeServices = dataItem.getInvokeServices();


        //排序
        List<Localization> locals = dataItem.getLocalizationList();
        JSONObject localization = getLocalizationList(locals);
        String detailResult = localization.getString("detailResult");
        String detailLanguage = localization.getString("detailLanguage");

        //语言列表
        List<String> languageList = getLanguageList(locals);

        List<String> classificationsList = dataItem.getClassifications();
        List<Classification> classifications = getClassifications(classificationsList);


        view.setViewName("data_item_info");
        view.addObject("datainfo", dataItem);
//        view.addObject("itemInfo", dataItem);
        view.addObject("user",userJson);
        view.addObject("classifications",classifications);
        view.addObject("relatedModels",modelItemArray);
        view.addObject("authorships",authorshipString);
        view.addObject("fileName",fileName);//后期应该是放该name下的所有数据
        view.addObject("distributeData", invokeServices);//存放远程节点信息
        //多语言description
        view.addObject("detailLanguage",detailLanguage);
        view.addObject("itemType","Data");
        view.addObject("languageList",languageList);
        view.addObject("itemInfo",dataItem);
        view.addObject("detail",detailResult);
        view.addObject("history",false);

        //修改者信息
        String lastModifier=dataItem.getLastModifier();
        JSONObject modifierJson=null;
        if(lastModifier!=null){
            modifierJson = userService.getItemUserInfoByEmail(lastModifier);
        }
        view.addObject("lastModifier", modifierJson);

        return view;
    }

    /**
     * @Description 根据传入的id返回dataItem的详情界面
     * @Param [id]
     * @return org.springframework.web.servlet.ModelAndView
     **/
    public ModelAndView getPage(String id, GenericItemDao genericItemDao){


        // DataItem dataItem;
        // try {
        //     dataItem =  (DataItem) genericService.getById(id,genericItemDao);
        // }catch (MyException e){
        //     view.setViewName("error/404");
        //     return view;
        // }

        DataItem dataItem =  (DataItem) genericService.getById(id,genericItemDao);

        return getPage(dataItem, genericItemDao);

    }

    // public List<String> getClassifications(List<String> classificationsList) {
    //     List<String> classifications = new ArrayList<>();
    //     for (String classification : classificationsList) {
    //         classifications.add(classificationDao.findFirstById(classification).getNameEn());
    //     }
    //     return classifications;
    // }
    public List<Classification> getClassifications(List<String> classificationsList) {
        List<Classification> classifications = new ArrayList<>();
        for (String classification : classificationsList) {
            classifications.add(classificationDao.findFirstById(classification));
        }
        return classifications;
    }


    public List<String> getLanguageList(List<Localization> locals) {
        List<String> languageList = new ArrayList<>();
        for(Localization local: locals){
            languageList.add(local.getLocalName());
        }
        return languageList;
    }

    public JSONObject getLocalizationList(List<Localization> locals){
        Collections.sort(locals);
        String detailResult = "";
        String detailLanguage = "";
        //先找中英文描述
        for(Localization localization:locals){
            String local = localization.getLocalCode();
            if(local.equals("en")||local.equals("zh")||local.contains("en-")||local.contains("zh-")){
                String localDesc = localization.getDescription();
                if(localDesc!=null&&!localDesc.equals("")) {
                    detailLanguage = localization.getLocalName();
                    detailResult = localization.getDescription();
                    break;
                }
            }
        }
        //如果没有中英文，则使用其他语言描述
        if(detailResult.equals("")){
            for(Localization localization:locals){
                String localDesc = localization.getDescription();
                if(localDesc!=null&&!localDesc.equals("")) {
                    detailLanguage = localization.getLocalName();
                    detailResult = localization.getDescription();
                    break;
                }
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("detailResult",detailResult);
        jsonObject.put("detailLanguage",detailLanguage);
        return jsonObject;
    }

    public JSONArray getModelItemArray(List<String> relatedModels) {
        JSONArray modelItemArray=new JSONArray();

        if(relatedModels!=null) {
            for (String mid : relatedModels) {
                try {
                    ModelItem modelItem = modelItemDao.findFirstById(mid);
                    JSONObject modelItemJson = new JSONObject();
                    modelItemJson.put("name", modelItem.getName());
                    modelItemJson.put("id", modelItem.getId());
                    modelItemJson.put("overview", modelItem.getOverview());
                    modelItemJson.put("image", modelItem.getImage().equals("") ? null : htmlLoadPath + modelItem.getImage());
                    modelItemArray.add(modelItemJson);
                }
                catch (Exception e){
                    log.error(e.getMessage());
                    // e.printStackTrace();
                }
            }
        }
        return modelItemArray;
    }

    public String getAuthorshipString(List<AuthorInfo> authorshipList) {
        String authorshipString="";

        if(authorshipList!=null){
            for (AuthorInfo author:authorshipList
            ) {
                if(authorshipString.equals("")){
                    authorshipString+=author.getName();
                }
                else{
                    authorshipString+=", "+author.getName();
                }

            }
        }
        return authorshipString;
    }


    /**
     * 获取当前条目的远程数据信息
     * @return 成功失败或者远程数据信息
     */
    public List<InvokeService> getDistributeDataInfo(@PathVariable(value = "dataItemId") String dataItemId){
        DataItem dataItem = dataItemDao.findFirstById(dataItemId);
        if (dataItem == null){
            dataItem = dataHubDao.findFirstById(dataItemId);
        }
        if (dataItem == null)
            return null;
        return dataItem.getInvokeServices();
    }

    /**
     * @Description 获取与数据条目相关的模型
     * @Param [id]
     * @return com.alibaba.fastjson.JSONArray
     **/
    public JSONArray getRelation(String id) {

        JSONArray result = new JSONArray();
        DataItem dataItem = dataItemDao.findFirstById(id);
        List<String> relatedModels = dataItem.getRelatedModels();

        for (String modelId : relatedModels) {
            ModelItem modelItem = modelItemDao.findFirstById(modelId);
            JSONObject object = new JSONObject();
            object.put("id", modelItem.getId());
            object.put("name", modelItem.getName());
            User user = userDao.findFirstByEmail(modelItem.getAuthor());
            object.put("author", user.getName());
            object.put("author_email", user.getEmail());
            result.add(object);
        }

        return result;
    }


    /**
     * @Description 设置与数据条目相关的模型
     * @Author bin
     * @Param [id, relations]
     * @return java.lang.String
     **/
    public JsonResult setRelation(String id, List<String> relations,String email) {

        DataItem item = dataItemDao.findFirstById(id);

        // List<String> relationDelete = new ArrayList<>();
        // for (int i = 0; i < dataItem.getRelatedModels().size(); i++) {
        //     relationDelete.add(dataItem.getRelatedModels().get(i));
        // }
        // List<String> relationAdd = new ArrayList<>();
        // for (int i = 0; i < relations.size(); i++) {
        //     relationAdd.add(relations.get(i));
        // }
        //
        // for (int i = 0; i < relationDelete.size(); i++) {
        //     for (int j = 0; j < relationAdd.size(); j++) {
        //         if (relationDelete.get(i).equals(relationAdd.get(j))) {
        //             relationDelete.set(i, "");
        //             relationAdd.set(j, "");
        //             break;
        //         }
        //     }
        // }
        //
        // for (int i = 0; i < relationDelete.size(); i++) {
        //     String model_id = relationDelete.get(i);
        //     if (!model_id.equals("")) {
        //         ModelItem modelItem = modelItemDao.findFirstById(model_id);
        //         // TODO 为什么Private就可以加进去了
        //         if(modelItem.getStatus().equals("Private")){
        //             relations.add(modelItem.getId());
        //             continue;
        //         }
        //         if (modelItem.getRelate().getDataItems() != null) {
        //             modelItem.getRelate().getDataItems().remove(id);
        //             modelItemDao.save(modelItem);
        //         }
        //     }
        // }
        //
        // for (int i = 0; i < relationAdd.size(); i++) {
        //     String model_id = relationAdd.get(i);
        //     if (!model_id.equals("")) {
        //         ModelItem modelItem = modelItemDao.findFirstById(model_id);
        //         if (modelItem.getRelate().getDataItems() != null) {
        //             modelItem.getRelate().getDataItems().add(id);
        //         } else {
        //             List<String> relatedData = new ArrayList<>();
        //             relatedData.add(id);
        //             modelItem.getRelate().setDataItems(relatedData);
        //         }
        //         modelItemDao.save(modelItem);
        //     }
        // }

        List<String> versions = item.getVersions();
        String originalItemName = item.getName();
        JSONObject result = new JSONObject();
        if (!item.isLock()){

            String author = item.getAuthor();
            Date now = new Date();

            //如果修改者不是作者的话把该条目锁住送去审核
            //提前单独判断的原因是对item统一修改后里面的值已经是新的了，再保存就没效果了
            if (!author.equals(email)){
                item.setLock(true);
                // dataItemDao.save(item);
                redisService.saveItem(item,ItemTypeEnum.DataItem);
            } else {
                if (versions == null || versions.size() == 0) {
                    Version version = versionService.addVersion(item, email, originalItemName);
                    versions = new ArrayList<>();
                    versions.add(version.getId());
                    item.setVersions(versions);
                }
            }

            List<String> oriRelatedModels = item.getRelatedModels();
            item.setRelatedModels(relations);
            item.setLastModifyTime(now);
            item.setLastModifier(email);


            Version new_version = versionService.addVersion(item, email,originalItemName);
            if (author.equals(email)){
                versions.add(new_version.getId());
                item.setVersions(versions);
                updateModelRelate(relations, oriRelatedModels,ItemTypeEnum.DataItem,id);
                item.setRelatedModels(relations);
                // dataItemDao.save(item);
                redisService.saveItem(item, ItemTypeEnum.DataItem);
                result.put("type","suc");

                return ResultUtils.success(result);
            }else {

                //发送通知
                List<String> recipientList = new ArrayList<>();
                recipientList.add(author);
                recipientList = noticeService.addItemAdmins(recipientList,item.getAdmins());
                recipientList = noticeService.addPortalAdmins(recipientList);
                recipientList = noticeService.addPortalRoot(recipientList);
                noticeService.sendNoticeContains(email, OperationEnum.Edit,ItemTypeEnum.Version,new_version,recipientList);
                result.put("type","version");
                return ResultUtils.success(result);
            }


        } else {
            result.put("type","version");
            return ResultUtils.success(result);
        }

    }


    //当数据条目更新与模型的关系时，同时更新模型的关系
    public void updateModelRelate(List<String> newRelations, List<String> oriRelations, ItemTypeEnum itemType, String itemId){

        switch (itemType){
            case DataItem:{

                //添加关联
                for (String relation : newRelations) {

                    if (!oriRelations.contains(relation)){
                        ModelItem item = modelItemDao.findFirstById(relation);
                        List<String> dataItems = item.getRelate().getDataItems();
                        if (!dataItems.contains(itemId)){
                            dataItems.add(itemId);
                            // modelItemDao.save(item);
                            redisService.saveItem(item,ItemTypeEnum.ModelItem);
                        }
                    }
                }

                //删除关联
                for (String ori : oriRelations) {

                    if (!newRelations.contains(ori)){
                        ModelItem item = modelItemDao.findFirstById(ori);
                        List<String> dataItems = item.getRelate().getDataItems();
                        if (dataItems.contains(itemId)){
                            dataItems.remove(itemId);
                            // modelItemDao.save(item);
                            redisService.saveItem(item,ItemTypeEnum.ModelItem);
                        }
                    }

                }


                break;
            }
            case DataHub:{

                //添加关联
                for (String relation : newRelations) {

                    if (!oriRelations.contains(relation)){
                        ModelItem item = modelItemDao.findFirstById(relation);
                        List<String> dataHubs = item.getRelate().getDataHubs();
                        if (!dataHubs.contains(itemId)){
                            dataHubs.add(itemId);
                            // modelItemDao.save(item);
                            redisService.saveItem(item,ItemTypeEnum.ModelItem);
                        }
                    }
                }

                //删除关联
                for (String ori : oriRelations) {

                    if (!newRelations.contains(ori)){
                        ModelItem item = modelItemDao.findFirstById(ori);
                        List<String> dataHubs = item.getRelate().getDataHubs();
                        if (dataHubs.contains(itemId)){
                            dataHubs.remove(itemId);
                            // modelItemDao.save(item);
                            redisService.saveItem(item,ItemTypeEnum.ModelItem);
                        }
                    }

                }

                break;
            }
        }

    }


    /**
     * 新增dataItem、dataHub数据条目
     * @param dataItemAddDTO
     * @param email
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult insert(DataItemDTO dataItemAddDTO, String email, ItemTypeEnum type) {
        try {
            PortalItem item = null;
            JSONObject daoFactory = genericService.daoFactory(type);
            if (type.getText().equals("dataItem")){
                item = new DataItem();
            }
            else if (type.getText().equals("dataHub")){
                item = new DataHub();
            }
            else {
                return ResultUtils.error(-1, "Created failed!");
            }


            BeanUtils.copyProperties(dataItemAddDTO, item);
            Date now = new Date();
            // oid字段已被废弃
            // item.setId(UUID.randomUUID().toString());
            item.setCreateTime(now);
            item.setAuthor(email);
            //设置dataItem的图片path以及存储图片
            String uuid = UUID.randomUUID().toString();
            String path = "/static/repository/dataItem/" + uuid + ".jpg";
            String savePath = "/repository/dataItem/" + uuid + ".jpg";
            String[] strs = dataItemAddDTO.getUploadImage().split(",");
            if (strs.length > 1) {
                String imgStr = dataItemAddDTO.getUploadImage().split(",")[1];
                Utils.base64StrToImage(imgStr, resourcePath + savePath);
                item.setImage(path);
            } else {
                item.setImage("");
            }
            item.setLastModifyTime(now);

            // 调整的字段
            Localization localization = new Localization();
            localization.setLocalCode("en");
            localization.setLocalName("English");
            localization.setName(dataItemAddDTO.getName());
            localization.setDescription(dataItemAddDTO.getDetail());
            List<Localization> list = new ArrayList<>();
            list.add(localization);
            item.setLocalizationList(list);


            ((GenericItemDao)daoFactory.get("itemDao")).insert(item);

            userService.updateUserResourceCount(email,type,"add");

            // 这个是新增dataCategorys表记录的，该表已被废弃
            // CategoryAddDTO categoryAddDTO = new CategoryAddDTO();
            // categoryAddDTO.setId(dataItem.getId());
            // categoryAddDTO.setCate(dataItem.getClassifications());
            // categoryAddDTO.setDataType("Url");
            // addCateId(categoryAddDTO);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id",item.getId());
            return ResultUtils.success(jsonObject);
        }catch (Exception e){
            return ResultUtils.error("Created failed!");
        }
    }


    /**
     * 得到分类树数据
     * @param
     * @return com.alibaba.fastjson.JSONArray
     * @Author bin
     **/
    public JSONArray createTreeNew(){
        JSONArray result = new JSONArray();
        List<Classification> grandpas = classificationDao.findAllByParentId(null);
        for (Classification grandpa:grandpas){
            JSONObject oneLevel = new JSONObject();
            //找出当前grandpa的所有子集
            List<Classification> fathers = classificationDao.findAllByParentId(grandpa.getId());
            JSONArray fathersArray = new JSONArray();//用于存储父类的所有的键值对
            for (Classification father:fathers){
                JSONObject twoLevel = new JSONObject();
                List<Classification> sons = classificationDao.findAllByParentId(father.getId());
                JSONArray sonsArray = new JSONArray();
                for (Classification son:sons){
                    JSONObject threeLevel = new JSONObject();
                    threeLevel.put(son.getNameEn(), son.getId());
                    sonsArray.add(threeLevel);
                }
                twoLevel.put(father.getNameEn(), sonsArray);
                fathersArray.add(twoLevel);
            }
            oneLevel.put(grandpa.getNameEn(),fathersArray);
            result.add(oneLevel);
        }

        return result;
    }

    /**
     * 根据id得到item
     * @param dataId
     * @return com.alibaba.fastjson.JSONObject
     * @Author bin
     **/
    public JSONObject getItemByDataId(String dataId, ItemTypeEnum type){

        JSONObject daoFactory = genericService.daoFactory(type);

        DataItem dataItem =  (DataItem) ((GenericItemDao)daoFactory.get("itemDao")).findFirstById(dataId);

        JSONObject jsonObject = new JSONObject();
        if(dataItem == null)
            jsonObject.put("noResult",1);
        else{
            List<String> cates = new ArrayList<>();
            cates = dataItem.getClassifications();
            List<String> categories = new ArrayList<>();
            for(String cate : cates){
                Classification c = classificationDao.findFirstById(cate);
                categories.add(c.getNameEn());
            }

            JSONObject obj =(JSONObject) JSON.toJSON(dataItem);
            obj.put("categories",categories);
            jsonObject.put("result",obj);
        }

        return jsonObject;
    }


    /**
     * 修改item条目
     * @param dataItemUpdateDTO
     * @param email 根据email判断是本人编辑还是非本人编辑
     * @return com.alibaba.fastjson.JSONObject
     * @Author bin
     **/
    public JSONObject updateDataItem(DataItemDTO dataItemUpdateDTO, String email, String id){

        // JSONObject dao = genericService.daoFactory(ItemTypeEnum.DataItem);
        return updateItem(dataItemUpdateDTO,email,ItemTypeEnum.DataItem,id);

    }



    /**
     * 修改dataItem、datahub通用方法
     * @param dataItemUpdateDTO
     * @param email
     * @param type
     * @return com.alibaba.fastjson.JSONObject
     * @Author bin
     **/
    public JSONObject updateItem(DataItemDTO dataItemUpdateDTO, String email, ItemTypeEnum type, String id){
        GenericItemDao genericItemDao = (GenericItemDao)genericService.daoFactory(type);

        JSONObject result = new JSONObject();
        PortalItem item = (PortalItem) genericItemDao.findFirstById(id);
        String originalItemName = item.getName();
        List<String> versions = item.getVersions();

        if (!item.isLock()){
            String author = item.getAuthor();
            Date now = new Date();

            //如果修改者不是作者的话把该条目锁住送去审核
            //提前单独判断的原因是对item统一修改后里面的值已经是新的了，再保存就没效果了
            if (!author.equals(email)){
                item.setLock(true);
                // genericItemDao.save(item);
                redisService.saveItem(item,type);
            } else {
                if (versions == null || versions.size() == 0) {

                    Version version = versionService.addVersion(item, email, originalItemName);

                    versions = new ArrayList<>();
                    versions.add(version.getId());
                    item.setVersions(versions);
                }
            }

            // 更新localization
            if (item.getLocalizationList() == null || item.getLocalizationList().size() == 0) {
                Localization localization = new Localization("en", "English", item.getName(), dataItemUpdateDTO.getDetail());
                List<Localization> localizationList = new ArrayList<>();
                localizationList.add(localization);
                item.setLocalizationList(localizationList);
            }
            else {
                item.getLocalizationList().get(0).setDescription(dataItemUpdateDTO.getDetail());
            }

            // 拷贝的时候忽略author字段，因为前端没有传来author
            BeanUtils.copyProperties(dataItemUpdateDTO,item,"localizationList");
            String uploadImage = dataItemUpdateDTO.getUploadImage();
            if (uploadImage != null && !uploadImage.contains("/dataItem/") && !uploadImage.equals("")){
                //删除旧图片
                File file = new File(resourcePath + item.getImage());
                if (file.exists()&&file.isFile())
                    file.delete();
                //添加新图片
                String uuid = UUID.randomUUID().toString();
                String path = "/static/repository/dataItem/" + uuid + ".jpg";//入库
                String path1 = "/repository/dataItem/" + uuid + ".jpg";//存储
                String imgStr = uploadImage.split(",")[1];
                Utils.base64StrToImage(imgStr, resourcePath + path1);
                item.setImage(path);
            }
            item.setLastModifyTime(now);
            item.setLastModifier(email);

            Version new_version = versionService.addVersion(item, email,originalItemName);
            if (author.equals(email)){
                versions.add(new_version.getId());
                item.setVersions(versions);

                // genericItemDao.save(item);
                redisService.saveItem(item,type);

                result.put("method", "update");
                result.put("id",item.getId());
                return result;
            }else {

                //发送通知
                List<String> recipientList = new ArrayList<>();
                recipientList.add(author);
                recipientList = noticeService.addItemAdmins(recipientList,item.getAdmins());
                recipientList = noticeService.addPortalAdmins(recipientList);
                recipientList = noticeService.addPortalRoot(recipientList);
                noticeService.sendNoticeContains(email, OperationEnum.Edit,ItemTypeEnum.Version,new_version,recipientList);
                result.put("method", "version");
                result.put("versionId", new_version.getId());
                return result;
            }
        } else {
            return null;
        }
    }

    /**
     * 用户拿到上传的所有条目
     * @param author
     * @param page
     * @param pagesize
     * @param asc
     * @return org.springframework.data.domain.Page<njgis.opengms.portal.entity.po.DataItem>
     * @Author bin
     **/
    public Page<ResultDTO> getUsersUploadData(String author, Integer page, Integer pagesize, Integer asc) {

        boolean as = false;
        if (asc == 1)
            as = true;

        Sort sort = Sort.by(as ? Sort.Direction.ASC : Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(page - 1, pagesize, sort);
        return dataItemDao.findByAuthor(pageable, author);

    }

    /**
     * 删除dataItem
     * @param id
     * @param email
     * @return int
     * @Author bin
     **/
    public JsonResult delete(String id,String email){
        DataItem data = new DataItem();
        data = (DataItem) genericService.getById(id,dataItemDao);

        if(!data.getAuthor().equals(email))
            return ResultUtils.error("Unauthorized");

        // List<String> relatedModels = data.getRelatedModels();
        // if (relatedModels!=null)
        //     for (int i = 0; i < relatedModels.size(); i++) {
        //         ModelItem modelItem = modelItemDao.findFirstByOid(relatedModels.get(i));
        //         modelItem.getRelatedData().remove(id);
        //         modelItemDao.save(modelItem);
        //     }

        try {
            // dataItemDao.deleteById(id);
            redisService.deleteItem(data, ItemTypeEnum.DataItem);
            userService.updateUserResourceCount(email, ItemTypeEnum.DataItem, "delete");
        }catch (Exception e){
            return ResultUtils.error("delete error");
        }

        return ResultUtils.success();
    }

    /**
     * 根据条目名和当前用户得到数据
     * @param findDTO
     * @param email
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult searchByNameAndAuthor(SpecificFindDTO findDTO,String email){

        return ResultUtils.success(genericService.searchItemsByUser(findDTO, ItemTypeEnum.DataItem, email));

    }


    /**
     * 添加相关模型
     * @param id
     * @param relatedModels
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult addRelatedModels(String id, List<String> relatedModels) {


        DataItem dataItem = dataItemDao.findFirstById(id);

        dataItem.setRelatedModels(relatedModels);

        // dataItemDao.save(dataItem);
        redisService.saveItem(dataItem,ItemTypeEnum.DataItem);

        return ResultUtils.success();
    }

    //用户创建dataitem页面
    public JsonResult addDataItemByUser(String id) throws IOException {
        //生成静态html
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");//模板所在目录，相对于当前classloader的classpath。
        resolver.setSuffix(".html");//模板文件后缀
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolver);

        String path;
        path = PortalApplication.class.getClassLoader().getResource("").getPath();

        File dataitemfile = new File(path + "/templates/dataItems");

        if (!dataitemfile.exists()) {
            dataitemfile.mkdir();
        }

        Context context = new Context();
        context.setVariable("datainfo", ResultUtils.success(dataItemDao.findFirstById(id)));

        FileWriter writer = new FileWriter(path + "/templates/dataItems/" + id + ".html");
        templateEngine.process("data_item_info", context, writer);

        writer.flush();
        writer.close();

        return ResultUtils.success();


    }

    //get related models
    public List<Map<String, String>> getRelatedModels(String id) {


//            DataItem dataItem=getById(id);
        DataItem dataItem = dataItemDao.findFirstById(id);

        List<String> relatedModels = dataItem.getRelatedModels();


        if (relatedModels == null) {
            List<Map<String, String>> list = new ArrayList<>();
            return list;

        }
        List<Map<String, String>> data = new ArrayList<>();

        ModelItem modelItem;

        Map<String, String> modelsInfo;

        for (int i = 0; i < relatedModels.size(); i++) {
            //只取三个
            if (i == 3) {
                break;
            }


            modelItem = modelItemDao.findFirstById(relatedModels.get(i));

            modelsInfo = new HashMap<>();
            modelsInfo.put("name", modelItem.getName());
            modelsInfo.put("id", modelItem.getId());
            modelsInfo.put("overview", modelItem.getOverview());

            data.add(modelsInfo);

        }


        return data;
    }


    //getAllRelatedModels
    public List<Map<String, String>> getAllRelatedModels(String id, Integer more) {


//            DataItem dataItem=getById(id);
        DataItem dataItem = dataItemDao.findFirstById(id);
        List<Map<String, String>> data = new ArrayList<>();
        List<String> relatedModels = dataItem.getRelatedModels();
        ModelItem modelItem;
        Map<String, String> modelsInfo;
        if (relatedModels == null) {
            modelsInfo = new HashMap<>();
            modelsInfo.put("all", "all");
            data.add(modelsInfo);
            return data;
        }

        if (more - 5 > relatedModels.size() || more - 5 == relatedModels.size()) {
            modelsInfo = new HashMap<>();
            modelsInfo.put("all", "all");
            data.add(modelsInfo);
            return data;
        }

        for (int i = more - 5; i < more && i < relatedModels.size(); i++) {
            //只取三个

            modelItem = new ModelItem();

            modelItem = modelItemDao.findFirstById(relatedModels.get(i));

            modelsInfo = new HashMap<>();
            modelsInfo.put("name", modelItem.getName());
            modelsInfo.put("id", modelItem.getId());
            modelsInfo.put("overview", modelItem.getOverview());

            data.add(modelsInfo);

        }
        return data;

    }


    public Page<DataItem> searchFromAllData(DataItemFindDTO dataItemFindDTO) {
        Sort sort = Sort.by(dataItemFindDTO.getAsc() ? Sort.Direction.ASC : Sort.Direction.DESC, "createDate");
        Pageable pageable = PageRequest.of(dataItemFindDTO.getPage() - 1, dataItemFindDTO.getPageSize(), sort);
        String se = dataItemFindDTO.getSearchContent().get(0);

        return dataItemDao.findByNameContainingOrOverviewContainingOrKeywordsContaining(pageable, se, se, se);

    }

}
