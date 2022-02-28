package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.dao.ConceptualModelDao;
import njgis.opengms.portal.dao.ModelItemDao;
import njgis.opengms.portal.entity.doo.AuthorInfo;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.Localization;
import njgis.opengms.portal.entity.doo.data.SimpleFileInfo;
import njgis.opengms.portal.entity.doo.model.ModelItemRelate;
import njgis.opengms.portal.entity.dto.SpecificFindDTO;
import njgis.opengms.portal.entity.dto.model.ConceptualModelResultDTO;
import njgis.opengms.portal.entity.po.ConceptualModel;
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
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;

import static njgis.opengms.portal.utils.Utils.saveFiles;

/**
 * @Description Conceptual Model Service
 * @Author kx
 * @Date 21/10/12
 * @Version 1.0.0
 */
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

        conceptualModelInfo=(ConceptualModel)genericService.recordViewCount(conceptualModelInfo);

        conceptualModelDao.save(conceptualModelInfo);

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
        saveFiles(files, path, email, "/conceptualModel",images);
        if (images.size() == 0) {
            result.put("code", -1);
        } else {
            try {
                //初始化概念模型，填入基本信息
                if(jsonObject.getString("contentType").equals("MxGraph")) {
                    String name = new Date().getTime() + "_MxGraph.png";
                    MxGraphUtils mxGraphUtils = new MxGraphUtils();
                    String filename = mxGraphUtils.exportImage(jsonObject.getInteger("w"), jsonObject.getInteger("h"), jsonObject.getString("xml"), path + "/" + email + "/", name);
                    File image = new File(filename);
                    if(image.exists()) {
                        images.add("/conceptualModel" + "/" + email + "/" + name);
                    }
                }
                conceptualModel.setImageList(images);
                conceptualModel.setStatus(jsonObject.getString("status"));
                conceptualModel.setName(jsonObject.getString("name"));
                conceptualModel.setLocalizationList(ArrayUtils.parseJSONArrayToList(jsonObject.getJSONArray("localizationList"),Localization.class));
                conceptualModel.setAuthorships(ArrayUtils.parseJSONArrayToList(jsonObject.getJSONArray("authorship"),AuthorInfo.class));
                conceptualModel.setRelatedModelItems(jsonObject.getJSONArray("relatedModelItems").toJavaList(String.class));
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
                conceptualModelDao.insert(conceptualModel);

                userService.ItemCountPlusOne(email, ItemTypeEnum.ConceptualModel);

                //联动与概念模型相关的模型关联信息
                List<String> relatedModelItems = conceptualModel.getRelatedModelItems();
                for(int i=0;i<relatedModelItems.size();i++) {
                    String id = relatedModelItems.get(i);
                    ModelItem modelItem = modelItemDao.findFirstById(id);
                    ModelItemRelate modelItemRelate = modelItem.getRelate();
                    modelItemRelate.getConceptualModels().add(conceptualModel.getId());
                    modelItem.setRelate(modelItemRelate);
                    modelItemDao.save(modelItem);
                }

                result.put("code", 1);
                result.put("id", conceptualModel.getId());
            } catch (Exception e) {
                e.printStackTrace();
                result.put("code", -2);
            }
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
        ConceptualModel conceptualModel_ori = conceptualModelDao.findFirstById(jsonObject.getString("id"));
        String author0 = conceptualModel_ori.getAuthor();
        ConceptualModel conceptualModel = new ConceptualModel();
        BeanUtils.copyProperties(conceptualModel_ori, conceptualModel);

        JSONObject result = new JSONObject();
        if (!conceptualModel_ori.isLock()) {

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
                conceptualModel.setLocalizationList(ArrayUtils.parseJSONArrayToList(jsonObject.getJSONArray("localizationList"),Localization.class));
                conceptualModel.setAuthorships(ArrayUtils.parseJSONArrayToList(jsonObject.getJSONArray("authorship"),AuthorInfo.class));
                conceptualModel.setRelatedModelItems(jsonObject.getJSONArray("relatedModelItems").toJavaList(String.class));
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

                String authorUserName = author0;
                if (author0.equals(email)) {
                    conceptualModel.setLastModifyTime(now);
                    conceptualModelDao.save(conceptualModel);

                    result.put("method", "update");
                    result.put("code", 1);
                    result.put("id", conceptualModel.getId());

                } else {
                    //TODO 概念模型版本
//                    ConceptualModelVersion conceptualModelVersion = new ConceptualModelVersion();
//                    BeanUtils.copyProperties(conceptualModel, conceptualModelVersion, "id");
//                    conceptualModelVersion.setId(UUID.randomUUID().toString());
//                    conceptualModelVersion.setOriginOid(conceptualModel_ori.getId());
//                    conceptualModelVersion.setModifier(uid);
//                    conceptualModelVersion.setVerNumber(now.getTime());
//                    conceptualModelVersion.setVerStatus(0);
//                    userService.noticeNumPlusPlus(authorUserName);
//                    conceptualModelVersion.setModifyTime(now);
//                    conceptualModelVersion.setCreator(author0);
//
//                    conceptualModelVersionDao.save(conceptualModelVersion);
//
//                    conceptualModel_ori.setLock(true);
//                    logicalModelDao.save(conceptualModel_ori);
//
//                    result.put("method", "version");
//                    result.put("code", 0);
//                    result.put("id", conceptualModelVersion.getId());


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
        ConceptualModel conceptualModel = conceptualModelDao.findFirstById(modelId);
        if(!conceptualModel.getAuthor().equals(email))
            return ResultUtils.error(-2, "author not matches!");
        if (conceptualModel != null) {
            //图片删除
            List<String> images = conceptualModel.getImageList();
            for (int i = 0; i < images.size(); i++) {
                String path = resourcePath + images.get(i);
                Utils.deleteFile(path);
            }
            //条目删除
            conceptualModelDao.delete(conceptualModel);
            userService.ItemCountMinusOne(email, ItemTypeEnum.ConceptualModel);

            //模型条目关联删除
            List<String> relatedModelItems = conceptualModel.getRelatedModelItems();
            for (int i = 0;i<relatedModelItems.size();i++) {
                String modelItemId = relatedModelItems.get(i);
                ModelItem modelItem = modelItemDao.findFirstById(modelItemId);
                List<String> conceptualModelIds = modelItem.getRelate().getConceptualModels();
                for (String id : conceptualModelIds
                        ) {
                    if (id.equals(conceptualModel.getId())) {
                        conceptualModelIds.remove(id);
                        break;
                    }
                }
                modelItem.getRelate().setConceptualModels(conceptualModelIds);
                modelItemDao.save(modelItem);
            }

            return ResultUtils.success();
        } else {
            return ResultUtils.error();
        }
    }

}
