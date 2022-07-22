package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.dao.ConceptualModelDao;
import njgis.opengms.portal.dao.ModelItemDao;
import njgis.opengms.portal.entity.doo.AuthorInfo;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.Localization;
import njgis.opengms.portal.entity.doo.data.SimpleFileInfo;
import njgis.opengms.portal.entity.doo.model.ModelItemRelate;
import njgis.opengms.portal.entity.dto.model.ConceptualModelResultDTO;
import njgis.opengms.portal.entity.po.ConceptualModel;
import njgis.opengms.portal.entity.po.ModelItem;
import njgis.opengms.portal.entity.po.Version;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.enums.OperationEnum;
import njgis.opengms.portal.utils.ArrayUtils;
import njgis.opengms.portal.utils.MxGraphUtils;
import njgis.opengms.portal.utils.ResultUtils;
import njgis.opengms.portal.utils.Utils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import static njgis.opengms.portal.utils.Utils.saveFiles;

/**
 * @Description Conceptual Model Service
 * @Author kx
 * @Date 21/10/12
 * @Version 1.0.0
 */
@Slf4j
@Service
public class ConceptualModelService {

    @Autowired
    UserService userService;

    @Autowired
    GenericService genericService;

    @Autowired
    ConceptualModelDao conceptualModelDao;

    @Autowired
    ModelItemDao modelItemDao;

    @Autowired
    VersionService versionService;

    @Autowired
    NoticeService noticeService;

    @Value("${htmlLoadPath}")
    String htmlLoadPath;

    @Value("${resourcePath}")
    String resourcePath;

    @Autowired
    RedisService redisService;

    /**
     * @Description 获取模型信息
     * @param id
     * @Return com.alibaba.fastjson.JSONObject
     * @Author kx
     * @Date 21/10/14
     **/
    public ConceptualModelResultDTO getInfo(String id){
        ConceptualModel conceptualModel=conceptualModelDao.findFirstById(id);
        ConceptualModelResultDTO conceptualModelResultDTO=new ConceptualModelResultDTO();
        BeanUtils.copyProperties(conceptualModel,conceptualModelResultDTO);

        //相关模型信息
        List<String> relatedModelItems = conceptualModel.getRelatedModelItems();
        conceptualModelResultDTO.setRelatedModelItemInfoList(genericService.getRelatedModelInfoList(relatedModelItems));


        //资源信息
        List<SimpleFileInfo> simpleFileInfoList = genericService.getSimpleFileInfoList(conceptualModel.getImageList());
        conceptualModelResultDTO.setResourceJson(simpleFileInfoList);

        return conceptualModelResultDTO;
    }

    public ModelAndView getPage(ConceptualModel conceptualModelInfo) {
        conceptualModelInfo=(ConceptualModel)genericService.recordViewCount(conceptualModelInfo);

        conceptualModelDao.save(conceptualModelInfo);

        //排序
        List<Localization> locals = conceptualModelInfo.getLocalizationList();
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

        //语言列表
        List<String> languageList = new ArrayList<>();
        for(Localization local:locals){
            languageList.add(local.getLocalName());
        }

        //时间
        Date date = conceptualModelInfo.getCreateTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateResult = simpleDateFormat.format(date);

        String lastModifyTime = simpleDateFormat.format(conceptualModelInfo.getLastModifyTime());

        //用户信息
        JSONObject userJson = userService.getItemUserInfoByEmail(conceptualModelInfo.getAuthor());

        //修改者信息
        String lastModifier = conceptualModelInfo.getLastModifier();
        JSONObject modifierJson = null;
        if (lastModifier != null) {
            modifierJson = userService.getItemUserInfoByEmail(lastModifier);
        }

        //authorship
        String authorshipString="";
        List<AuthorInfo> authorshipList=conceptualModelInfo.getAuthorships();
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

        //relateModelItemList
        List<String> modelItemIdList = conceptualModelInfo.getRelatedModelItems();



        JSONArray modelItemInfoList = ArrayUtils.parseListToJSONArray(genericService.getRelatedModelInfoList(modelItemIdList));

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("conceptual_model");
        modelAndView.addObject("itemInfo", conceptualModelInfo);
        modelAndView.addObject("detailLanguage",detailLanguage);
        modelAndView.addObject("languageList", languageList);
        modelAndView.addObject("date", dateResult);
        modelAndView.addObject("year", calendar.get(Calendar.YEAR));
        modelAndView.addObject("user", userJson);
        modelAndView.addObject("authorship", authorshipString);
        modelAndView.addObject("loadPath", htmlLoadPath);
        modelAndView.addObject("lastModifier", modifierJson);
        modelAndView.addObject("lastModifyTime", lastModifyTime);
//        modelAndView.addObject("relateModelItem",modelItemInfo);
        modelAndView.addObject("relateModelItemList",modelItemInfoList);//之前只关联一个modelitem,现在改为多个
        modelAndView.addObject("modularType", ItemTypeEnum.ConceptualModel);
        return modelAndView;
    }

    /**
     * @Description 根据id获取概念模型详情页面
     * @param id
     * @Return org.springframework.web.servlet.ModelAndView
     * @Author kx
     * @Date 21/10/12
     **/
    public ModelAndView getPage(String id) {
        //条目信息
        ConceptualModel conceptualModelInfo = conceptualModelDao.findFirstById(id);
        return getPage(conceptualModelInfo);
    }

    /**
     * @Description 创建概念模型
     * @param files 图片文件
     * @param jsonObject 概念模型描述信息
     * @param email 创建者email
     * @Return com.alibaba.fastjson.JSONObject
     * @Author kx
     * @Date 21/10/13
     **/
    public JSONObject insert(List<MultipartFile> files, JSONObject jsonObject, String email) {
        JSONObject result = new JSONObject();
        ConceptualModel conceptualModel = new ConceptualModel();

        String path = resourcePath + "/conceptualModel";
        List<String> images = new ArrayList<>();
        String contentType = jsonObject.getString("contentType");


        switch (contentType){
            case "Image":{
                saveFiles(files, path, email, "/conceptualModel",images);
                if (images.size() == 0){
                    result.put("code", -1);
                    return result;
                }
                break;
            }
            case "MxGraph":{
                String name = new Date().getTime() + "_MxGraph.png";
                MxGraphUtils mxGraphUtils = new MxGraphUtils();
                try {
                    String filename = null;
                    filename = mxGraphUtils.exportImage(jsonObject.getInteger("w"), jsonObject.getInteger("h"), jsonObject.getString("xml"), path + "/" + email + "/", name);
                    File image = new File(filename);
                    if(image.exists()) {
                        images.add("/conceptualModel" + "/" + email + "/" + name);
                    }
                } catch (Exception e) {
                    // e.printStackTrace();
                    log.error(e.getMessage());
                    result.put("code", -2);
                }

                break;
            }
            default:{
                result.put("code", -1);
                return result;
            }
        }

        try {

            conceptualModel.setImageList(images);
            conceptualModel.setStatus(jsonObject.getString("status"));
            conceptualModel.setName(jsonObject.getString("name"));

            // conceptualModel.setLocalizationList(ArrayUtils.parseJSONArrayToList(jsonObject.getJSONArray("localizationList"),Localization.class));
            // 初始化localization
            Localization localization = new Localization();
            localization.setLocalCode("en");
            localization.setLocalName("English");
            localization.setName(jsonObject.getString("name"));
            localization.setDescription(jsonObject.getString("detail"));
            List<Localization> list = new ArrayList<>();
            list.add(localization);
            conceptualModel.setLocalizationList(list);

            conceptualModel.setAuthorships(ArrayUtils.parseJSONArrayToList(jsonObject.getJSONArray("authorships"),AuthorInfo.class));
            // conceptualModel.setRelatedModelItems(jsonObject.getJSONArray("relatedModelItems").toJavaList(String.class));
            JSONArray re = jsonObject.getJSONArray("relatedModelItems");
            List<String> reList = re == null ? null : re.toJavaList(String.class);
            conceptualModel.setRelatedModelItems(reList);
            // conceptualModel.setRelatedModelItems(Arrays.asList(jsonObject.getString("relateModelItem")));
            conceptualModel.setOverview(jsonObject.getString("description"));
            conceptualModel.setContentType(jsonObject.getString("contentType"));
            conceptualModel.setCXml(jsonObject.getString("cXml"));
            conceptualModel.setSvg(jsonObject.getString("svg"));
            conceptualModel.setAuthor(email);
//                if (isAuthor) {
//                    conceptualModel.setRealAuthor(null);
//                } else {
//                    AuthorInfo authorInfo = new AuthorInfo();
//                    authorInfo.setName(jsonObject.getJSONObject("author").getString("name"));
//                    authorInfo.setIns(jsonObject.getJSONObject("author").getString("ins"));
//                    authorInfo.setEmail(jsonObject.getJSONObject("author").getString("email"));
//                    conceptualModel.setRealAuthor(authorInfo);
//                }
            Date now = new Date();
            conceptualModel.setCreateTime(now);
            conceptualModel.setLastModifyTime(now);

            //联动与逻辑模型相关的模型关联信息
            List<String> relatedModelItems = conceptualModel.getRelatedModelItems();
            for(int i=0;i<relatedModelItems.size();i++) {
                String id = relatedModelItems.get(i);
                ModelItem modelItem = modelItemDao.findFirstById(id);
                ModelItemRelate modelItemRelate = modelItem.getRelate();
                if (modelItemRelate == null){
                    modelItemRelate = new ModelItemRelate();
                }
                modelItemRelate.getConceptualModels().add(conceptualModel.getId());
                modelItem.setRelate(modelItemRelate);
                // modelItemDao.save(modelItem);
                redisService.saveItem(modelItem,ItemTypeEnum.ModelItem);
            }

            conceptualModelDao.insert(conceptualModel);

            // userService.ItemCountPlusOne(email, ItemTypeEnum.ConceptualModel);
            userService.updateUserResourceCount(email,ItemTypeEnum.ConceptualModel,"add");


            result.put("code", 1);
            result.put("id", conceptualModel.getId());
        } catch (Exception e) {
            // e.printStackTrace();
            log.error(e.getMessage());
            result.put("code", -2);
        }


        return result;
    }

    /**
     * @Description 信息更新
     * @param files 图片文件
     * @param jsonObject 概念模型描述信息
     * @param email 创建者email
     * @Return com.alibaba.fastjson.JSONObject
     * @Author kx
     * @Date 21/10/14
     **/
    public JSONObject update(List<MultipartFile> files, JSONObject jsonObject, String email) {
        ConceptualModel conceptualModel = conceptualModelDao.findFirstById(jsonObject.getString("id"));
        String author0 = conceptualModel.getAuthor();
        List<String> versions = conceptualModel.getVersions();
        String originalItemName = conceptualModel.getName();
        JSONObject result = new JSONObject();
        if (!conceptualModel.isLock()) {

            if (author0.equals(email)) {
                if (versions == null || versions.size() == 0) {

                    Version version = versionService.addVersion(conceptualModel, email, originalItemName);

                    versions = new ArrayList<>();
                    versions.add(version.getId());
                    conceptualModel.setVersions(versions);
                }
            }else{
                conceptualModel.setLock(true);
                // conceptualModelDao.save(conceptualModel);
                redisService.saveItem(conceptualModel,ItemTypeEnum.ConceptualModel);
            }

            String path = resourcePath + "/conceptualModel";
            List<String> images = new ArrayList<>();

            try {
                JSONArray resources = jsonObject.getJSONArray("resources");
                List<String> oldImages = conceptualModel.getImageList();
                for (Object object : resources) {
                    JSONObject json = (JSONObject) JSONObject.toJSON(object);
                    String pa = json.getString("path");
                    for (String imagePath : oldImages) {
                        if (pa.equals(imagePath)) {
                            images.add(imagePath);
                            break;
                        }
                    }
                }
                if (files.size() > 0) {
                    saveFiles(files, path, email, "/conceptualModel",images);
                    if (images.size() == 0) {
                        result.put("code", -1);
                    }
                }

                if(jsonObject.getString("contentType").equals("MxGraph")) {
                    String name = new Date().getTime() + "_MxGraph.png";
                    MxGraphUtils mxGraphUtils = new MxGraphUtils();
                    mxGraphUtils.exportImage(jsonObject.getInteger("w"), jsonObject.getInteger("h"), jsonObject.getString("xml"), path + "/" + email + "/", name);
                    images=new ArrayList<>();
                    images.add("/conceptualModel" + "/" + email + "/"+ name);
                }

                conceptualModel.setImageList(images);
                conceptualModel.setStatus(jsonObject.getString("status"));
                conceptualModel.setName(jsonObject.getString("name"));
                // conceptualModel.setLocalizationList(ArrayUtils.parseJSONArrayToList(jsonObject.getJSONArray("localizationList"),Localization.class));
                // conceptualModel.setAuthorships(ArrayUtils.parseJSONArrayToList(jsonObject.getJSONArray("authorship"),AuthorInfo.class));
                // conceptualModel.setRelatedModelItems(jsonObject.getJSONArray("relatedModelItems").toJavaList(String.class));
                Localization localization = new Localization();
                localization.setLocalCode("en");
                localization.setLocalName("English");
                localization.setName(jsonObject.getString("name"));
                localization.setDescription(jsonObject.getString("detail"));
                List<Localization> list = new ArrayList<>();
                list.add(localization);
                conceptualModel.setLocalizationList(list);
                conceptualModel.setAuthorships(ArrayUtils.parseJSONArrayToList(jsonObject.getJSONArray("authorships"),AuthorInfo.class));
                // conceptualModel.setRelatedModelItems(Arrays.asList(jsonObject.getString("relateModelItem")));
                // conceptualModel.setRelatedModelItems(jsonObject.getJSONArray("relatedModelItems").toJavaList(String.class));
                JSONArray re = jsonObject.getJSONArray("relatedModelItems");
                List<String> reList = re == null ? null : re.toJavaList(String.class);
                conceptualModel.setRelatedModelItems(reList);
                conceptualModel.setOverview(jsonObject.getString("description"));
                conceptualModel.setContentType(jsonObject.getString("contentType"));
                conceptualModel.setCXml(jsonObject.getString("cXml"));
                conceptualModel.setSvg(jsonObject.getString("svg"));
                Date curDate = new Date();
                conceptualModel.setLastModifyTime(curDate);
                conceptualModel.setLastModifier(author0);

                Version version_new = versionService.addVersion(conceptualModel, email, originalItemName);
                if (author0.equals(email)) {
                    versions.add(version_new.getId());
                    conceptualModel.setVersions(versions);
                    // conceptualModelDao.save(conceptualModel);
                    redisService.saveItem(conceptualModel, ItemTypeEnum.ConceptualModel);
                    result.put("method", "update");
                    result.put("id", conceptualModel.getId());

                } else {
                    // 发送通知
                    noticeService.sendNoticeContainsAllAdmin(email, conceptualModel.getAuthor(), conceptualModel.getAdmins() ,ItemTypeEnum.Version,version_new, OperationEnum.Edit);

                    result.put("method", "version");
                    result.put("versionId", version_new.getId());
                    return result;

                }

            } catch (Exception e) {
                // e.printStackTrace();
                log.error(e.getMessage());
                result.put("code", -2);
            }

            return result;
        } else {
            return null;
        }
    }

    /**
     * @Description 条目删除
     * @param modelId
     * @param email
     * @Return int
     * @Author kx
     * @Date 21/10/14
     **/
    public JsonResult delete(String modelId, String email) {
        ConceptualModel conceptualModel = null;
        try {
            conceptualModel = conceptualModelDao.findFirstById(modelId);
        } catch (Exception e){
            return ResultUtils.error("根据id查找模型出错");
            // throw new RuntimeException(e);
        }
        if (conceptualModel == null){
            return ResultUtils.error(-2, "author not matches!");
        }

        // if(!conceptualModel.getAuthor().equals(email))
        //     return ResultUtils.error(-2, "author not matches!");
        if (conceptualModel != null) {
            //图片删除
            List<String> images = conceptualModel.getImageList();
            for (int i = 0; i < images.size(); i++) {
                String path = resourcePath + images.get(i);
                Utils.deleteFile(path);
            }


            //模型条目关联删除
            List<String> relatedModelItems = conceptualModel.getRelatedModelItems();
            for (int i = 0;i<relatedModelItems.size();i++) {
                String modelItemId = relatedModelItems.get(i);
                ModelItem modelItem = modelItemDao.findFirstById(modelItemId);
                if (modelItem == null)
                    continue;
                List<String> conceptualModelIds = modelItem.getRelate().getConceptualModels();
                for (String id : conceptualModelIds
                        ) {
                    if (id.equals(conceptualModel.getId())) {
                        conceptualModelIds.remove(id);
                        break;
                    }
                }
                modelItem.getRelate().setConceptualModels(conceptualModelIds);
                // modelItemDao.save(modelItem);
                redisService.saveItem(modelItem,ItemTypeEnum.ModelItem);
            }

            //条目删除
            // conceptualModelDao.delete(conceptualModel);
            redisService.deleteItem(conceptualModel, ItemTypeEnum.ConceptualModel);
            userService.updateUserResourceCount(email, ItemTypeEnum.ConceptualModel);


            return ResultUtils.success();
        } else {
            return ResultUtils.error();
        }
    }

}
