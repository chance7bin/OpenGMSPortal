package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.dao.LogicalModelDao;
import njgis.opengms.portal.dao.ModelItemDao;
import njgis.opengms.portal.entity.doo.AuthorInfo;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.Localization;
import njgis.opengms.portal.entity.doo.data.SimpleFileInfo;
import njgis.opengms.portal.entity.doo.model.ModelItemRelate;
import njgis.opengms.portal.entity.dto.model.LogicalModelResultDTO;
import njgis.opengms.portal.entity.po.LogicalModel;
import njgis.opengms.portal.entity.po.ModelItem;
import njgis.opengms.portal.enums.ItemTypeEnum;
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
 * @Description Logical Model Service
 * @Author kx
 * @Date 21/10/12
 * @Version 1.0.0
 */
@Service
public class LogicalModelService {

    @Autowired
    UserService userService;

    @Autowired
    GenericService genericService;

    @Autowired
    LogicalModelDao logicalModelDao;

    @Autowired
    ModelItemDao modelItemDao;

    @Value("${htmlLoadPath}")
    String htmlLoadPath;

    @Value("${resourcePath}")
    String resourcePath;

    /**
     * @Description 获取模型信息
     * @param id
     * @Return com.alibaba.fastjson.JSONObject
     * @Author kx
     * @Date 21/10/14
     **/
    public LogicalModelResultDTO getInfo(String id){
        LogicalModel logicalModel= logicalModelDao.findFirstById(id);
        LogicalModelResultDTO logicalModelResultDTO=new LogicalModelResultDTO();
        BeanUtils.copyProperties(logicalModel,logicalModelResultDTO);

        //相关模型信息
        List<String> relatedModelItems = logicalModel.getRelatedModelItems();
        logicalModelResultDTO.setRelatedModelItemInfoList(genericService.getRelatedModelInfoList(relatedModelItems));


        //资源信息
        List<SimpleFileInfo> simpleFileInfoList = genericService.getSimpleFileInfoList(logicalModel.getImageList());
        logicalModelResultDTO.setResourceJson(simpleFileInfoList);

        return logicalModelResultDTO;
    }

    /**
     * @Description 根据id获取逻辑模型详情页面
     * @param id
     * @Return org.springframework.web.servlet.ModelAndView
     * @Author kx
     * @Date 21/10/12
     **/
    public ModelAndView getPage(String id) {
        //条目信息
        LogicalModel logicalModelInfo = logicalModelDao.findFirstById(id);

        logicalModelInfo=(LogicalModel)genericService.recordViewCount(logicalModelInfo);

        logicalModelDao.save(logicalModelInfo);

        //排序
        List<Localization> locals = logicalModelInfo.getLocalizationList();
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
        Date date = logicalModelInfo.getCreateTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateResult = simpleDateFormat.format(date);

        String lastModifyTime = simpleDateFormat.format(logicalModelInfo.getLastModifyTime());

        //用户信息
        JSONObject userJson = userService.getItemUserInfoByEmail(logicalModelInfo.getAuthor());

        //修改者信息
        String lastModifier = logicalModelInfo.getLastModifier();
        JSONObject modifierJson = null;
        if (lastModifier != null) {
            modifierJson = userService.getItemUserInfoByEmail(lastModifier);
        }

        //authorship
        String authorshipString="";
        List<AuthorInfo> authorshipList=logicalModelInfo.getAuthorships();
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
        List<String> modelItemIdList = logicalModelInfo.getRelatedModelItems();
        JSONArray modelItemInfoList = ArrayUtils.parseListToJSONArray(genericService.getRelatedModelInfoList(modelItemIdList));


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("conceptual_model");
        modelAndView.addObject("itemInfo", logicalModelInfo);
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

        return modelAndView;
    }

    /**
     * @Description 创建逻辑模型
     * @param files 图片文件
     * @param jsonObject 逻辑模型描述信息
     * @param email 创建者email
     * @Return com.alibaba.fastjson.JSONObject
     * @Author kx
     * @Date 21/10/13
     **/
    public JSONObject insert(List<MultipartFile> files, JSONObject jsonObject, String email) {
        JSONObject result = new JSONObject();
        LogicalModel logicalModel = new LogicalModel();

        List<String> images = new ArrayList<>();
        String path = resourcePath + "/logicalModel";
        String contentType = jsonObject.getString("contentType");
        switch (contentType){
            case "Image":{
                saveFiles(files, path, email, "/logicalModel",images);
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
                        images.add("/logicalModel" + "/" + email + "/" + name);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
            //初始化逻辑模型，填入基本信息
            logicalModel.setImageList(images);
            logicalModel.setStatus(jsonObject.getString("status"));
            logicalModel.setName(jsonObject.getString("name"));
            // if (jsonObject.getJSONArray("localizationList") != null){
            //     logicalModel.setLocalizationList(ArrayUtils.parseJSONArrayToList(jsonObject.getJSONArray("localizationList"),Localization.class));
            // }

            // 初始化localization
            Localization localization = new Localization();
            localization.setLocalCode("en");
            localization.setLocalName("English");
            localization.setName(jsonObject.getString("name"));
            localization.setDescription(jsonObject.getString("detail"));
            List<Localization> list = new ArrayList<>();
            list.add(localization);
            logicalModel.setLocalizationList(list);

            logicalModel.setAuthorships(ArrayUtils.parseJSONArrayToList(jsonObject.getJSONArray("authorship"),AuthorInfo.class));
            // logicalModel.setRelatedModelItems(jsonObject.getJSONArray("relatedModelItems").toJavaList(String.class));
            logicalModel.setRelatedModelItems(Arrays.asList(jsonObject.getString("relateModelItem")));
            logicalModel.setOverview(jsonObject.getString("description"));
            logicalModel.setContentType(jsonObject.getString("contentType"));
            logicalModel.setCXml(jsonObject.getString("cXml"));
            logicalModel.setSvg(jsonObject.getString("svg"));
            logicalModel.setAuthor(email);

//                if (isAuthor) {
//                    logicalModel.setRealAuthor(null);
//                } else {
//                    AuthorInfo authorInfo = new AuthorInfo();
//                    authorInfo.setName(jsonObject.getJSONObject("author").getString("name"));
//                    authorInfo.setIns(jsonObject.getJSONObject("author").getString("ins"));
//                    authorInfo.setEmail(jsonObject.getJSONObject("author").getString("email"));
//                    logicalModel.setRealAuthor(authorInfo);
//                }
            Date now = new Date();
            logicalModel.setCreateTime(now);
            logicalModel.setLastModifyTime(now);

            //联动与逻辑模型相关的模型关联信息
            List<String> relatedModelItems = logicalModel.getRelatedModelItems();
            for(int i=0;i<relatedModelItems.size();i++) {
                String id = relatedModelItems.get(i);
                ModelItem modelItem = modelItemDao.findFirstById(id);
                ModelItemRelate modelItemRelate = modelItem.getRelate();
                if (modelItemRelate == null){
                    modelItemRelate = new ModelItemRelate();
                }
                modelItemRelate.getLogicalModels().add(logicalModel.getId());
                modelItem.setRelate(modelItemRelate);
                modelItemDao.save(modelItem);
            }


            logicalModelDao.insert(logicalModel);

            // userService.ItemCountPlusOne(email, ItemTypeEnum.LogicalModel);
            userService.updateUserResourceCount(email,ItemTypeEnum.LogicalModel,"add");


            result.put("code", 1);
            result.put("id", logicalModel.getId());
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", -2);
        }
        return result;
    }

    /**
     * @Description 信息更新
     * @param files 图片文件
     * @param jsonObject 逻辑模型描述信息
     * @param email 创建者email
     * @Return com.alibaba.fastjson.JSONObject
     * @Author kx
     * @Date 21/10/14
     **/
    public JSONObject update(List<MultipartFile> files, JSONObject jsonObject, String email) {
        LogicalModel logicalModel_ori = logicalModelDao.findFirstById(jsonObject.getString("id"));
        String author0 = logicalModel_ori.getAuthor();
        LogicalModel logicalModel = new LogicalModel();
        BeanUtils.copyProperties(logicalModel_ori, logicalModel);

        JSONObject result = new JSONObject();
        if (!logicalModel_ori.isLock()) {

            String path = resourcePath + "/logicalModel";
            List<String> images = new ArrayList<>();

            try {
                JSONArray resources = jsonObject.getJSONArray("resources");
                List<String> oldImages = logicalModel.getImageList();
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
                    saveFiles(files, path, email, "/logicalModel",images);
                    if (images.size() == 0) {
                        result.put("code", -1);
                    }
                }

                if(jsonObject.getString("contentType").equals("MxGraph")) {
                    String name = new Date().getTime() + "_MxGraph.png";
                    MxGraphUtils mxGraphUtils = new MxGraphUtils();
                    mxGraphUtils.exportImage(jsonObject.getInteger("w"), jsonObject.getInteger("h"), jsonObject.getString("xml"), path + "/" + email + "/", name);
                    images=new ArrayList<>();
                    images.add("/logicalModel" + "/" + email + "/"+ name);
                }

                logicalModel.setImageList(images);
                logicalModel.setStatus(jsonObject.getString("status"));
                logicalModel.setName(jsonObject.getString("name"));
                logicalModel.setLocalizationList(ArrayUtils.parseJSONArrayToList(jsonObject.getJSONArray("localizationList"),Localization.class));
                logicalModel.setAuthorships(ArrayUtils.parseJSONArrayToList(jsonObject.getJSONArray("authorship"),AuthorInfo.class));
                logicalModel.setRelatedModelItems(jsonObject.getJSONArray("relatedModelItems").toJavaList(String.class));
                logicalModel.setOverview(jsonObject.getString("description"));
                logicalModel.setContentType(jsonObject.getString("contentType"));
                logicalModel.setCXml(jsonObject.getString("cXml"));
                logicalModel.setSvg(jsonObject.getString("svg"));
                logicalModel.setAuthor(email);

                Date now = new Date();

                String authorUserName = author0;
                if (author0.equals(email)) {
                    logicalModel.setLastModifyTime(now);
                    logicalModelDao.save(logicalModel);

                    result.put("method", "update");
                    result.put("code", 1);
                    result.put("id", logicalModel.getId());

                } else {
                    //TODO 逻辑模型版本
//                    LogicalModelVersion logicalModelVersion = new LogicalModelVersion();
//                    BeanUtils.copyProperties(logicalModel, logicalModelVersion, "id");
//                    logicalModelVersion.setId(UUID.randomUUID().toString());
//                    logicalModelVersion.setOriginOid(logicalModel_ori.getId());
//                    logicalModelVersion.setModifier(uid);
//                    logicalModelVersion.setVerNumber(now.getTime());
//                    logicalModelVersion.setVerStatus(0);
//                    userService.noticeNumPlusPlus(authorUserName);
//                    logicalModelVersion.setModifyTime(now);
//                    logicalModelVersion.setCreator(author0);
//
//                    logicalModelVersionDao.save(logicalModelVersion);
//
//                    logicalModel_ori.setLock(true);
//                    logicalModelDao.save(logicalModel_ori);
//
//                    result.put("method", "version");
//                    result.put("code", 0);
//                    result.put("id", logicalModelVersion.getId());


                    return result;

                }

            } catch (Exception e) {
                e.printStackTrace();
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
        LogicalModel logicalModel = logicalModelDao.findFirstById(modelId);
        if(!logicalModel.getAuthor().equals(email))
            return ResultUtils.error(-2, "author not matches!");
        if (logicalModel != null) {
            //图片删除
            List<String> images = logicalModel.getImageList();
            for (int i = 0; i < images.size(); i++) {
                String path = resourcePath + images.get(i);
                Utils.deleteFile(path);
            }
            //条目删除
            logicalModelDao.delete(logicalModel);
            userService.ItemCountMinusOne(email, ItemTypeEnum.LogicalModel);

            //模型条目关联删除
            List<String> relatedModelItems = logicalModel.getRelatedModelItems();
            for (int i = 0;i<relatedModelItems.size();i++) {
                String modelItemId = relatedModelItems.get(i);
                ModelItem modelItem = modelItemDao.findFirstById(modelItemId);
                List<String> logicalModelIds = modelItem.getRelate().getLogicalModels();
                for (String id : logicalModelIds
                        ) {
                    if (id.equals(logicalModel.getId())) {
                        logicalModelIds.remove(id);
                        break;
                    }
                }
                modelItem.getRelate().setLogicalModels(logicalModelIds);
                modelItemDao.save(modelItem);
            }

            return ResultUtils.success();
        } else {
            return ResultUtils.error();
        }
    }


}
