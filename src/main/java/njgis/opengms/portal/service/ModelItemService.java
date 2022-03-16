package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.dao.*;
import njgis.opengms.portal.entity.doo.AuthorInfo;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.Localization;
import njgis.opengms.portal.entity.doo.base.PortalItem;
import njgis.opengms.portal.entity.doo.model.ModelItemRelate;
import njgis.opengms.portal.entity.doo.model.ModelRelation;
import njgis.opengms.portal.entity.doo.model.modelItemVersion;
import njgis.opengms.portal.entity.dto.model.modelItem.ModelItemAddDTO;
import njgis.opengms.portal.entity.dto.model.modelItem.ModelItemUpdateDTO;
import njgis.opengms.portal.entity.po.*;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.enums.OperationEnum;
import njgis.opengms.portal.enums.RelationTypeEnum;
import njgis.opengms.portal.utils.ImageUtils;
import njgis.opengms.portal.utils.ResultUtils;
import njgis.opengms.portal.utils.Utils;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.management.relation.Relation;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
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
    GenericService genericService;

    @Autowired
    ModelItemDao modelItemDao;

    @Autowired
    ModelItemService modelItemService;

    @Autowired
    VersionService versionService;

    @Autowired
    ModelClassificationService modelClassificationService;

    @Autowired
    ClassificationDao classificationDao;

    @Autowired
    DataItemDao dataItemDao;

    @Autowired
    ConceptualModelDao conceptualModelDao;

    @Autowired
    LogicalModelDao logicalModelDao;

    @Autowired
    ComputableModelDao computableModelDao;

    @Autowired
    ConceptDao conceptDao;

    @Autowired
    SpatialReferenceDao spatialReferenceDao;

    @Autowired
    TemplateDao templateDao;

    @Autowired
    UnitDao unitDao;

    @Autowired
    UserDao userDao;

    @Autowired
    UserService userService;

    @Autowired
    ArticleDao articleDao;

    @Autowired
    NoticeService noticeService;


    public ModelAndView getPage(PortalItem portalItem) {
        ModelAndView modelAndView=new ModelAndView();

        ModelItem modelInfo = (ModelItem) genericService.recordViewCount(portalItem);

        modelItemDao.save(modelInfo);

        // List<String> classifications = new ArrayList<>();
        // for (String classification : modelInfo.getClassifications()) {
        //     // classifications.add(classificationDao.findFirstById(classification).getNameEn());
        //     // 拿到该分类的id
        //     classifications.add(classificationDao.findFirstById(classification).getId());
        // }

        // 直接拿classification的id就可以了
        // JSONArray classResult = modelClassificationService.getClassifications(classifications);
        JSONArray classResult = modelClassificationService.getClassifications(modelInfo.getClassifications());

        //详情页面
//        String detailResult;
//        String model_detailDesc=modelInfo.getDetail();
//        detailResult = commonService.getDetail(model_detailDesc);

        //排序
        List<Localization> locals = modelInfo.getLocalizationList();
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
        Date date=modelInfo.getCreateTime();
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        String dateResult=simpleDateFormat.format(date);


        //relate
        ModelItemRelate modelItemRelate=modelInfo.getRelate();
        List<ModelRelation> modelItems = modelInfo.getRelate().getModelRelationList();
        List<String> conceptual=modelItemRelate.getConceptualModels();
        List<String> computable=modelItemRelate.getComputableModels();
        List<String> logical=modelItemRelate.getLogicalModels();
        List<String> concepts=modelItemRelate.getConcepts();
        List<String> spatialReferences=modelItemRelate.getSpatialReferences();
        List<String> templates=modelItemRelate.getTemplates();
        List<String> units=modelItemRelate.getUnits();
        List<Map<String,String>> dataSpaceFiles=modelItemRelate.getDataSpaceFiles();
        List<Map<String,String>> exLinks=modelItemRelate.getExLinks();

        JSONArray modelItemArray=new JSONArray();
        if(modelItems!=null) {
            for (int i = 0; i < modelItems.size(); i++) {
                String idNew = modelItems.get(i).getModelId();
                ModelItem modelItemNew = modelItemDao.findFirstById(idNew);
                if (modelItemNew.getStatus().equals("Private")) {
                    continue;
                }
                JSONObject modelItemJson = new JSONObject();
                modelItemJson.put("name", modelItemNew.getName());
                modelItemJson.put("id", modelItemNew.getId());
                modelItemJson.put("relation",modelItems.get(i).getRelation().getText());
                modelItemJson.put("overview", modelItemNew.getOverview());
                modelItemJson.put("image", modelItemNew.getImage().equals("") ? null : htmlLoadPath + modelItemNew.getImage());
                modelItemArray.add(modelItemJson);
            }
        }
        JSONArray conceptualArray=new JSONArray();
        for(int i=0;i<conceptual.size();i++){
            String id=conceptual.get(i);
            ConceptualModel conceptualModel=conceptualModelDao.findFirstById(id);
            if(conceptualModel.getStatus().equals("Private")){
                continue;
            }
            JSONObject conceptualJson = new JSONObject();
            conceptualJson.put("name",conceptualModel.getName());
            conceptualJson.put("id",conceptualModel.getId());
            conceptualJson.put("overview",conceptualModel.getOverview());
            conceptualJson.put("image", conceptualModel.getImageList().size() == 0 ? null : htmlLoadPath + conceptualModel.getImageList().get(0));
            conceptualArray.add(conceptualJson);
        }

        JSONArray logicalArray=new JSONArray();
        for(int i=0;i<logical.size();i++){
            String id=logical.get(i);
            LogicalModel logicalModel=logicalModelDao.findFirstById(id);
            if(logicalModel.getStatus().equals("Private")){
                continue;
            }
            JSONObject logicalJson = new JSONObject();
            logicalJson.put("name",logicalModel.getName());
            logicalJson.put("id",logicalModel.getId());
            logicalJson.put("overview",logicalModel.getOverview());
            logicalJson.put("image", logicalModel.getImageList().size() == 0 ? null : htmlLoadPath + logicalModel.getImageList().get(0));
            logicalArray.add(logicalJson);
        }

        JSONArray computableArray=new JSONArray();
        for(int i=0;i<computable.size();i++){
            String id=computable.get(i);
            ComputableModel computableModel=computableModelDao.findFirstById(id);
            if(computableModel.getStatus().equals("Private")){
                continue;
            }
            JSONObject computableJson = new JSONObject();
            computableJson.put("name",computableModel.getName());
            computableJson.put("id",computableModel.getId());
            computableJson.put("overview",computableModel.getOverview());
            computableJson.put("contentType",computableModel.getContentType());
            computableArray.add(computableJson);
        }

        JSONArray conceptArray=new JSONArray();
        if(concepts!=null) {
            for (int i = 0; i < concepts.size(); i++) {
                String id = concepts.get(i);
                Concept concept = conceptDao.findFirstById(id);
                if(concept.getStatus().equals("Private")){
                    continue;
                }
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("name", concept.getName());
                jsonObj.put("id", concept.getId());
//                jsonObj.put("alias", concept.getAlias());
                String desc = "";
                List<Localization> localizationList = concept.getLocalizationList();
                for(int j=0;j<localizationList.size();j++){
                    String description = localizationList.get(j).getDescription();
                    if(description!=null&&!description.equals("")){
                        desc = description;
                        break;
                    }
                }
                jsonObj.put("overview", desc);
//                jsonObj.put("description_ZH", concept.getDescription_ZH());
//                jsonObj.put("description_EN", concept.getDescription_EN());
                conceptArray.add(jsonObj);
            }
        }

        JSONArray spatialReferenceArray=new JSONArray();
        if(spatialReferences!=null) {
            for (int i = 0; i < spatialReferences.size(); i++) {
                String id = spatialReferences.get(i);
                SpatialReference spatialReference = spatialReferenceDao.findFirstById(id);
                if(spatialReference.getStatus().equals("Private")){
                    continue;
                }
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("name", spatialReference.getName());
                jsonObj.put("id", spatialReference.getId());
                jsonObj.put("wkname", spatialReference.getWkname());
                jsonObj.put("overview", spatialReference.getOverview());
                spatialReferenceArray.add(jsonObj);
            }
        }

        JSONArray templateArray=new JSONArray();
        if(templates!=null) {
            for (int i = 0; i < templates.size(); i++) {
                String id = templates.get(i);
                Template template = templateDao.findFirstById(id);
                if(template.getStatus().equals("Private")){
                    continue;
                }
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("name", template.getName());
                jsonObj.put("id", template.getId());
                jsonObj.put("overview", template.getOverview());
                jsonObj.put("type", template.getType());
                templateArray.add(jsonObj);
            }
        }

        JSONArray unitArray=new JSONArray();
        if(units!=null) {
            for (int i = 0; i < units.size(); i++) {
                String id = units.get(i);
                Unit unit = unitDao.findFirstById(id);
                if(unit.getStatus().equals("Private")){
                    continue;
                }
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("name", unit.getName());
                jsonObj.put("id", unit.getId());

                jsonObj.put("overview", unit.getOverview());
//                jsonObj.put("description_EN", unit.getDescription_EN());
                unitArray.add(jsonObj);
            }
        }

        JSONArray dataItemArray=new JSONArray();
        List<String> dataItems=modelInfo.getRelate().getDataItems();
        if(dataItems!=null){
            for(String dataId:dataItems){
                DataItem dataItem=dataItemDao.findFirstById(dataId);
                if(dataItem.getStatus().equals("Private")){
                    continue;
                }
                JSONObject dataJson=new JSONObject();
                dataJson.put("name",dataItem.getName());
                dataJson.put("id",dataItem.getId());
                dataJson.put("overview",dataItem.getOverview());
                dataItemArray.add(dataJson);
            }
        }

        //TODO dataHubs dataMethod

        JSONObject relationJson = new JSONObject();
        relationJson.put("modelItems",modelItemArray);
        relationJson.put("conceptualModels",conceptualArray);
        relationJson.put("logicalModels",logicalArray);
        relationJson.put("computableModels",computableArray);

        relationJson.put("dataItems",dataItemArray);

        relationJson.put("concepts",conceptArray);
        relationJson.put("spatialReferences",spatialReferenceArray);
        relationJson.put("templates",templateArray);
        relationJson.put("units",unitArray);

        relationJson.put("exLinks",exLinks);
        relationJson.put("dataSpaceFiles",dataSpaceFiles);


        //用户信息
        JSONObject userJson = userService.getItemUserInfoByEmail(modelInfo.getAuthor());

        //修改者信息
        String lastModifier=modelInfo.getLastModifier();
        JSONObject modifierJson=null;
        if(lastModifier!=null){
            modifierJson = userService.getItemUserInfoByEmail(lastModifier);
        }

        String lastModifyTime=simpleDateFormat.format(modelInfo.getLastModifyTime());


        //图片路径
        String image=modelInfo.getImage();
        if(!image.equals("")){
            modelInfo.setImage(htmlLoadPath+image);
        }

        //meta keywords
        List<String> keywords=modelInfo.getKeywords();
        String meta_keywords="";
        if(keywords.size()!=0) {
            meta_keywords = keywords.toString().replace("[", ", ").replace("]", "");
        }

        //authorship
        String authorshipString="";
        List<AuthorInfo> authorshipList=modelInfo.getAuthorships();
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

        modelAndView.setViewName("model_item_info");
        modelAndView.addObject("itemInfo",modelInfo);
        modelAndView.addObject("itemType","Model");
        modelAndView.addObject("metaKeywords",meta_keywords);
        modelAndView.addObject("classifications",classResult);
        modelAndView.addObject("detailLanguage",detailLanguage);
        modelAndView.addObject("languageList", languageList);
//        modelAndView.addObject("description",modelInfo.getOverview());
        modelAndView.addObject("detail",detailResult);
        modelAndView.addObject("date",dateResult);
        modelAndView.addObject("year",calendar.get(Calendar.YEAR));
        modelAndView.addObject("relation",relationJson);
        modelAndView.addObject("user", userJson);
        modelAndView.addObject("authorship", authorshipString);
        modelAndView.addObject("lastModifier", modifierJson);
        modelAndView.addObject("lastModifyTime", lastModifyTime);
        modelAndView.addObject("references", getReferences(modelInfo.getId()));

        return modelAndView;
    }

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
                ModelItem modelItem1 = modelItemDao.findFirstById(relatedModelList.get(i).getModelId());
                List<ModelRelation> relatedModelList1 = modelItem1.getRelate().getModelRelationList();
                for(int j = 0;j<relatedModelList1.size();j++){
                    if(relatedModelList1.get(j).getModelId().equals(modelItem.getId())){
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
     // * @Description 根据查询条件查询符合条件的模型条目
     // * @param modelItemFindDTO
     // * @Return com.alibaba.fastjson.JSONObject
     // * @Author kx
     // * @Date 2021/7/7
     **/
/*    public JSONObject query(ModelItemFindDTO modelItemFindDTO, Boolean containPrivate) {
        JSONObject queryResult = new JSONObject();

        //查询条件梳理
        int page = modelItemFindDTO.getPage()-1;
        int pageSize = modelItemFindDTO.getPageSize();
        String searchText = modelItemFindDTO.getSearchText();
        String sortField = modelItemFindDTO.getSortField();
        String authorEmail = modelItemFindDTO.getAuthorEmail();

        Sort sort = Sort.by(modelItemFindDTO.getAsc() ? Sort.Direction.ASC : Sort.Direction.DESC, sortField);
        Pageable pageable = PageRequest.of(page, pageSize, sort);

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
                        modelItemPage = modelItemDao.findByNameContainsIgnoreCaseAndAuthor(searchText, authorEmail, pageable);
                        break;
                    case "Keyword":
                        modelItemPage = modelItemDao.findByKeywordsIgnoreCaseInAndAuthor(searchText, authorEmail, pageable);
                        break;
                    case "Content":
                        modelItemPage = modelItemDao.findByOverviewContainsIgnoreCaseAndLocalizationDescriptionAndAuthor(searchText, authorEmail, pageable);
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

    }*/


    public JSONObject update(ModelItemUpdateDTO modelItemUpdateDTO, String email){
        ModelItem modelItem=modelItemDao.findFirstById(modelItemUpdateDTO.getOriginId());
        List<String> versions = modelItem.getVersions();
        String author=modelItem.getAuthor();
        String authorUserName = author;
        if(!modelItem.isLock()) {
            if (author.equals(email)) {
                if (versions == null || versions.size() == 0) {
//                    ModelItemVersion modelItemVersionOri = new ModelItemVersion();
//                    BeanUtils.copyProperties(modelItem, modelItemVersionOri, "id");
//                    modelItemVersionOri.setId(UUID.randomUUID().toString());
//                    modelItemVersionOri.setOriginOid(modelItem.getId());
//                    modelItemVersionOri.setVerNumber((long) 0);
//                    modelItemVersionOri.setVerStatus(2);
//                    modelItemVersionOri.setModifier(modelItem.getAuthor());
//                    modelItemVersionOri.setModifyTime(modelItem.getCreateTime());
//                    modelItemVersionDao.insert(modelItemVersionOri);

                    Version version = versionService.addVersion(modelItem, email, modelItem.getName());


                    versions = new ArrayList<>();
                    versions.add(version.getId());
                    modelItem.setVersions(versions);
                }
            }else{
                modelItem.setLock(true);
                modelItemDao.save(modelItem);
            }

            BeanUtils.copyProperties(modelItemUpdateDTO, modelItem, Utils.getNullPropertyNames(modelItemUpdateDTO));
            //判断是否为新图片
            String uploadImage = modelItemUpdateDTO.getUploadImage();
            if (uploadImage != null && !uploadImage.contains("/modelItem/") && !uploadImage.equals("")) {
                //删除旧图片
                File file = new File(resourcePath + modelItem.getImage());
                if (file.exists() && file.isFile())
                    file.delete();
                //添加新图片
                String path = "/modelItem/" + UUID.randomUUID().toString() + ".jpg";
                String imgStr = uploadImage.split(",")[1];
                Utils.base64StrToImage(imgStr, resourcePath + path);
                modelItem.setImage(path);
            }
            Date curDate = new Date();
            modelItem.setLastModifyTime(curDate);
            modelItem.setLastModifier(author);

            List<Localization> localizationList = modelItem.getLocalizationList();
            for(int l = 0;l<localizationList.size();l++){
                Localization localization = localizationList.get(l);
                localization.setDescription(ImageUtils.saveBase64Image(localization.getDescription(),modelItem.getId(),resourcePath,htmlLoadPath));
                localizationList.set(l,localization);
            }
            modelItem.setLocalizationList(localizationList);

            Version version_new = versionService.addVersion(modelItem, email, modelItem.getName());
            if (author.equals(email)) {
                versions.add(version_new.getId());
                modelItem.setVersions(versions);

                modelItemDao.save(modelItem);

                JSONObject result = new JSONObject();
                result.put("method", "update");
                result.put("id", modelItem.getId());

                return result;
            } else {

                // 发送通知
                noticeService.sendNoticeContainsAllAdmin(email, modelItem.getAuthor(), modelItem.getAdmins(), version_new.getId(), OperationEnum.Edit);

                JSONObject result = new JSONObject();
                result.put("method", "version");
                result.put("id", version_new.getId());

                return result;
            }
        }
        else{
            return null;
        }
    }


    /**
     * @Description 获取模型贡献者信息
     * @param id 模型id
     * @Return com.alibaba.fastjson.JSONArray
     * @Author kx
     * @Date 22/2/25
     **/
    public JSONArray getContributors(String id){

        ModelItem modelItem;

        if(id.matches("[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}")) {
            modelItem = modelItemDao.findFirstById(id);
        }else{
            modelItem = modelItemDao.findFirstByAccessId(id);
        }

        List<String> contributors = modelItem.getContributors();
        JSONArray jsonArray = new JSONArray();

        if(contributors!=null&&contributors.size()>0){
            for(String contributor : contributors){

                JSONObject jsonObject = new JSONObject();
                User user = userDao.findFirstByEmail(contributor);
                jsonObject.put("name",user.getName());
                jsonObject.put("userId",user.getAccessId());
                jsonObject.put("email",user.getEmail());
                jsonObject.put("image",user.getAvatar().equals("")?"":htmlLoadPath+user.getAvatar());

                jsonArray.add(jsonObject);

            }

        }

        return jsonArray;
    }

    /**
     * @Description 获取模型分类信息
     * @param id
     * @Return java.util.List<java.lang.String>
     * @Author kx
     * @Date 22/3/1
     **/
    public List<String> getClassifications(String id){
        ModelItem modelItem = modelItemDao.findFirstById(id);
        return modelItem.getClassifications();
    }

    public List<String> getAlias(String id){
        ModelItem modelItem = modelItemDao.findFirstById(id);
        return modelItem.getAlias();
    }

    public List<Localization> getLocalizationList(String id){
        ModelItem modelItem = modelItemDao.findFirstById(id);
        return modelItem.getLocalizationList();
    }

    public JSONArray getReferences(String id){
        ModelItem modelItem = modelItemDao.findFirstById(id);
        List<String> reference_ids = modelItem.getReferences();
        JSONArray references = new JSONArray();
        for(int i=0;i<reference_ids.size();i++) {
            Article article = articleDao.findFirstById(reference_ids.get(i));
            references.add(JSONObject.toJSONString(article));
//            System.out.println(references.get(i));
        }
        return references;
    }


    public String getDetailByLanguage(String id, String language){
        ModelItem modelItem = modelItemDao.findFirstById(id);
        List<Localization> localizationList = modelItem.getLocalizationList();
        for(Localization localization : localizationList){
            if(localization.getLocalName().equals(language)){
                return localization.getDescription();
            }
        }
        return null;
    }

    public JSONArray getRelation(String modelId,String type){

        JSONArray result=new JSONArray();
        ModelItem modelItem=modelItemDao.findFirstById(modelId);
        ModelItemRelate relation=modelItem.getRelate();
        List<String> list=new ArrayList<>();

        switch (type){
            case "dataItem":
                list=relation.getDataItems();
                if(list!=null){
                    for(String id:list){
                        DataItem dataItem=dataItemDao.findFirstById(id);
                        if(dataItem.getStatus().equals("Private")){
                            continue;
                        }
                        JSONObject item=new JSONObject();
                        item.put("id",dataItem.getId());
                        item.put("name",dataItem.getName());
                        User user=userDao.findFirstByEmail(dataItem.getAuthor());
                        item.put("author_name",user.getName());
                        item.put("author_email", user.getEmail());
                        result.add(item);
                    }
                }
                break;
            case "modelItem":
                List<ModelRelation> modelRelationList = relation.getModelRelationList();
                if (modelRelationList != null) {
                    for (ModelRelation modelRelation : modelRelationList) {
                        ModelItem modelItem1 = modelItemDao.findFirstById(modelRelation.getModelId());
                        if (modelItem1.getStatus().equals("Private")) {
                            continue;
                        }
                        JSONObject item = new JSONObject();
                        item.put("id", modelItem1.getId());
                        item.put("name", modelItem1.getName());
                        item.put("relation", modelRelation.getRelation().getText());
                        item.put("author_name", userDao.findFirstByEmail(modelItem1.getAuthor()).getName());
                        item.put("author_email", modelItem1.getAuthor());
                        result.add(item);
                    }
                }
                break;
            case "conceptualModel":
                list=relation.getConceptualModels();
                if(list!=null) {
                    for (String id : list) {
                        ConceptualModel conceptualModel = conceptualModelDao.findFirstById(id);
                        if(conceptualModel.getStatus().equals("Private")){
                            continue;
                        }
                        JSONObject item = new JSONObject();
                        item.put("id", conceptualModel.getId());
                        item.put("name", conceptualModel.getName());
                        item.put("author_name", userDao.findFirstByEmail(conceptualModel.getAuthor()).getName());
                        item.put("author_email", conceptualModel.getAuthor());
                        result.add(item);
                    }
                }
                break;
            case "logicalModel":
                list=relation.getLogicalModels();
                if(list!=null) {
                    for (String id : list) {
                        LogicalModel logicalModel = logicalModelDao.findFirstById(id);
                        if(logicalModel.getStatus().equals("Private")){
                            continue;
                        }
                        JSONObject item = new JSONObject();
                        item.put("id", logicalModel.getId());
                        item.put("name", logicalModel.getName());
                        item.put("author_name", userDao.findFirstByEmail(logicalModel.getAuthor()).getName());
                        item.put("author_email", logicalModel.getAuthor());
                        result.add(item);
                    }
                }
                break;
            case "computableModel":
                list=relation.getComputableModels();
                if(list!=null) {
                    for (String id : list) {
                        ComputableModel computableModel = computableModelDao.findFirstById(id);
                        if(computableModel.getStatus().equals("Private")){
                            continue;
                        }
                        JSONObject item = new JSONObject();
                        item.put("id", computableModel.getId());
                        item.put("name", computableModel.getName());
                        item.put("author_name", userDao.findFirstByEmail(computableModel.getAuthor()).getName());
                        item.put("author_email", computableModel.getAuthor());
                        result.add(item);
                    }
                }
                break;
            case "concept":
                list=relation.getConcepts();
                if(list!=null) {
                    for (String id : list) {
                        Concept concept = conceptDao.findFirstById(id);
                        if(concept.getStatus().equals("Private")){
                            continue;
                        }
                        JSONObject item = new JSONObject();
                        item.put("id", concept.getId());
                        item.put("name", concept.getName());
                        item.put("author_name", userDao.findFirstByEmail(concept.getAuthor()).getName());
                        item.put("author_email", concept.getAuthor());
                        result.add(item);
                    }
                }
                break;
            case "spatialReference":
                list=relation.getSpatialReferences();
                if(list!=null) {
                    for (String id : list) {
                        SpatialReference spatialReference = spatialReferenceDao.findFirstById(id);
                        if(spatialReference.getStatus().equals("Private")){
                            continue;
                        }
                        JSONObject item = new JSONObject();
                        item.put("id", spatialReference.getId());
                        item.put("name", spatialReference.getName());
                        item.put("author_name", userDao.findFirstByEmail(spatialReference.getAuthor()).getName());
                        item.put("author_email", spatialReference.getAuthor());
                        result.add(item);
                    }
                }
                break;
            case "template":
                list=relation.getTemplates();
                if(list!=null) {
                    for (String id : list) {
                        Template template = templateDao.findFirstById(id);
                        if(template.getStatus().equals("Private")){
                            continue;
                        }
                        JSONObject item = new JSONObject();
                        item.put("id", template.getId());
                        item.put("name", template.getName());
                        item.put("author_name", userDao.findFirstByEmail(template.getAuthor()).getName());
                        item.put("author_email", template.getAuthor());
                        result.add(item);
                    }
                }
                break;
            case "unit":
                list=relation.getUnits();
                if(list!=null) {
                    for (String id : list) {
                        Unit unit = unitDao.findFirstById(id);
                        if(unit.getStatus().equals("Private")){
                            continue;
                        }
                        JSONObject item = new JSONObject();
                        item.put("id", unit.getId());
                        item.put("name", unit.getName());
                        item.put("author_name", userDao.findFirstByEmail(unit.getAuthor()).getName());
                        item.put("author_email", unit.getAuthor());
                        result.add(item);
                    }
                }
                break;

        }

        return result;
    }

    public JSONObject getRelationGraph(String oid, Boolean isFull){
        JSONObject result = new JSONObject();

        JSONArray nodes = new JSONArray();
        JSONArray links = new JSONArray();

        ModelItem modelItem = modelItemDao.findFirstById(oid);
        JSONObject node = new JSONObject();
        String name = modelItem.getName();
//        int start = name.indexOf("(");
//        int end = name.indexOf(")");
//        if(name.length()>0&&start!=-1&&end!=-1) {
//            name = name.substring(0, start).trim() + " " + name.substring(end + 1, name.length() - 1);
//        }
        node.put("name", name);
        node.put("oid",modelItem.getId());
        node.put("img", modelItem.getImage().equals("")?"":"/static"+modelItem.getImage());
        node.put("overview", modelItem.getOverview());
        node.put("type","model");
        nodes.add(node);
        List<ModelRelation> modelRelationList = modelItem.getRelate().getModelRelationList();

        addNodeAndLink(0,modelRelationList,nodes,links,isFull);

        result.put("nodes",nodes);
        result.put("links",links);

        return result;
    }

    public JSONObject getFullRelationGraph(){
        JSONObject result = new JSONObject();

        try {

            JSONArray nodes = new JSONArray();
            JSONArray links = new JSONArray();

            List<ModelItem> modelItemList = modelItemDao.findAll();
            for (int i = 0; i < modelItemList.size(); i++) {
                ModelItem modelItem = modelItemList.get(i);
                if(modelItem.getRelate().getModelRelationList().size()==0){
                    continue;
                }

                Boolean exist = false;
                for (int j = 0; j < nodes.size(); j++) {
                    JSONObject node = nodes.getJSONObject(j);
//                    System.out.println(modelItem.getOid() + " " + j);
//                    System.out.println(node);
//                    System.out.println(modelItem.getOid());
                    if (node.getString("type").equals("model") && node.getString("name").equals(modelItem.getName())) {
                        exist = true;
                        break;
                    }
                }

                if (!exist) {
                    JSONObject node = new JSONObject();
                    node.put("name", modelItem.getName());
                    node.put("oid", modelItem.getId());
                    node.put("img", modelItem.getImage().equals("") ? "" : "/static" + modelItem.getImage());
                    node.put("overview", modelItem.getOverview());
                    node.put("type", "model");
                    nodes.add(node);
                    List<ModelRelation> modelRelationList = modelItem.getRelate().getModelRelationList();

                    addNodeAndLink(nodes.size()-1, modelRelationList, nodes, links, true);
                }
            }

            result.put("nodes", nodes);
            result.put("links", links);
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    public JSONObject refreshFullRelationGraph(){
        JSONObject jsonResult = getFullRelationGraph();
        try {
            String fileName = resourcePath + "/cacheFile/modelRelationGraph.json";
            File file = new File(fileName);
            if(!file.exists()){
                file.createNewFile();
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
            out.write(jsonResult.toString());
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonResult;
    }

    private void addNodeAndLink(int oriNum, List<ModelRelation> modelRelationList, JSONArray nodes, JSONArray links, Boolean fullLevel) {
        for (int i = 0; i < modelRelationList.size(); i++) {
            ModelRelation modelRelation = modelRelationList.get(i);
            String relateOid = modelRelation.getModelId();
            ModelItem modelItem_relation = modelItemDao.findFirstById(relateOid);

            Boolean exist = false;
            int n = 0;
            for (; n < nodes.size(); n++) {
                JSONObject node = nodes.getJSONObject(n);
                try {
                    if (modelItem_relation.getName().equals(node.getString("name"))) {
                        exist = true;
                        break;
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

            int modelNodeNum;
            if (exist) {
                modelNodeNum = n;
            } else {
                JSONObject node = new JSONObject();
                node.put("name", modelItem_relation.getName());
                node.put("oid", modelItem_relation.getId());
                node.put("img", modelItem_relation.getImage().equals("") ? "" : "/static" + modelItem_relation.getImage());
                node.put("overview", modelItem_relation.getOverview());
                node.put("type", "model");
                nodes.add(node);
                modelNodeNum = nodes.size() - 1;
            }


            JSONObject link = new JSONObject();
            RelationTypeEnum relationType = modelRelation.getRelation();
            if (fullLevel) {
                link.put("ori", oriNum);
                link.put("tar", modelNodeNum);
                if (relationType.getNumber() == 5 || relationType.getNumber() == 1 || relationType.getNumber() == 6) {
                    relationType = RelationTypeEnum.getOpposite(relationType.getNumber());
                    link.put("ori", modelNodeNum);
                    link.put("tar", oriNum);
                }
            } else {
                link.put("ori", modelNodeNum);
                link.put("tar", oriNum);
            }

            link.put("relation", relationType.getText());
            link.put("type", "model");
            links.add(link);

//            List<Reference> referenceList = modelItem_relation.getReferences();
//            for(int j = 0;j<referenceList.size();j++){
//                Reference reference = referenceList.get(j);
//
//                Boolean refFind = false;
//                int r = 0;
//                for(;r<nodes.size();r++){
//                    JSONObject node = nodes.getJSONObject(r);
//                    if(node.getString("type").equals("ref") && reference.getTitle().equals(node.getString("name"))){
//                        refFind = true;
//                        break;
//                    }
//                }
//                int refNum;
//                if(refFind){
//                    refNum=r;
//                }else{
//                    JSONObject node = new JSONObject();
//                    node.put("name",reference.getTitle());
//                    node.put("author",reference.getAuthor());
//                    node.put("journal",reference.getJournal());
//                    node.put("link", reference.getLinks());
//                    node.put("type","ref");
//                    nodes.add(node);
//                    refNum = nodes.size()-1;
//                }
//                link = new JSONObject();
//                link.put("ori", modelNodeNum);
//                link.put("tar", refNum);
//                link.put("type", "ref");
//                links.add(link);
//            }

            //多层遍历
            if (fullLevel) {
                List<ModelRelation> subModelRelationList = modelItem_relation.getRelate().getModelRelationList();
                for (int k = 0; k < subModelRelationList.size(); k++) {
                    ModelRelation subModelRelation = subModelRelationList.get(k);
                    for (int m = 0; m < nodes.size(); m++) {
                        JSONObject node_exist = nodes.getJSONObject(m);
                        if (subModelRelation.getModelId().equals(node_exist.getString("oid"))) {
                            subModelRelationList.remove(k);
                            k--;
                            break;
                        }
                    }
                }
                if (subModelRelationList.size() != 0) {
                    addNodeAndLink(modelNodeNum, subModelRelationList, nodes, links, true);
                }
            }
        }
    }

    public JSONArray getRelatedResources(String oid){

        JSONArray result=new JSONArray();
        ModelItem modelItem=modelItemDao.findFirstById(oid);
        ModelItemRelate relation=modelItem.getRelate();
        List<String> list=new ArrayList<>();

        list=relation.getConcepts();
        if(list!=null) {
            for (String id : list) {
                Concept concept = conceptDao.findFirstById(id);
                if(concept.getStatus().equals("Private")){
                    continue;
                }
                JSONObject item = new JSONObject();
                item.put("id", concept.getId());
                item.put("name", concept.getName());
                item.put("author", userService.getByEmail(concept.getAuthor()).getName());
                item.put("author_uid", concept.getAuthor());
                item.put("type", "concept");
                result.add(item);
            }
        }

        list=relation.getSpatialReferences();
        if(list!=null) {
            for (String id : list) {
                SpatialReference spatialReference = spatialReferenceDao.findFirstById(id);
                if(spatialReference.getStatus().equals("Private")){
                    continue;
                }
                JSONObject item = new JSONObject();
                item.put("id", spatialReference.getId());
                item.put("name", spatialReference.getName());
                item.put("author", userService.getByEmail(spatialReference.getAuthor()).getName());
                item.put("author_uid", spatialReference.getAuthor());
                item.put("type", "spatialReference");
                result.add(item);
            }
        }

        list=relation.getTemplates();
        if(list!=null) {
            for (String id : list) {
                Template template = templateDao.findFirstById(id);
                if(template.getStatus().equals("Private")){
                    continue;
                }
                JSONObject item = new JSONObject();
                item.put("id", template.getId());
                item.put("name", template.getName());
                item.put("author", userService.getByEmail(template.getAuthor()).getName());
                item.put("author_uid", template.getAuthor());
                item.put("type", "template");
                result.add(item);
            }
        }

        list=relation.getUnits();
        if(list!=null) {
            for (String id : list) {
                Unit unit = unitDao.findFirstById(id);
                if(unit.getStatus().equals("Private")){
                    continue;
                }
                JSONObject item = new JSONObject();
                item.put("id", unit.getId());
                item.put("name", unit.getName());
                item.put("author", userService.getByEmail(unit.getAuthor()).getName());
                item.put("author_uid", unit.getAuthor());
                item.put("type", "unit");
                result.add(item);
            }
        }

        List<Map<String,String>> mapList = new ArrayList<>();
        mapList=relation.getDataSpaceFiles();
        if(mapList!=null) {
            for (Map<String,String> ele : mapList) {
                JSONObject item = new JSONObject();
                item.put("id", ele.get("id"));
                item.put("name", ele.get("name"));
                item.put("url", ele.get("url"));
                item.put("type", "dataSpaceFile");
                result.add(item);
            }
        }

        mapList=relation.getExLinks();
        if(mapList!=null) {
            for (Map<String,String> ele : mapList) {
                JSONObject item = new JSONObject();
                item.put("id", ele.get("id"));
                item.put("name", ele.get("name"));
                item.put("content", ele.get("content"));
                item.put("type","exLink");
                result.add(item);
            }
        }


        return result;
    }

    public String updateClassifications(String modelId, List<String> classi, String email){

        ModelItem modelItem = modelItemDao.findFirstById(modelId);

        String author = modelItem.getAuthor();

        if(author.equals(email)) {
            modelItem.setClassifications(classi);
            modelItemDao.save(modelItem);

            return "suc";
        }else{
            ModelItemUpdateDTO modelItemUpdateDTO = new ModelItemUpdateDTO();
            modelItemUpdateDTO.setOriginId(modelItem.getId());

            modelItemUpdateDTO.setClassifications(classi);

            modelItemService.update(modelItemUpdateDTO,email);

            return "version";
        }

    }

    public String updateAlias(String modelId, List<String> aliasList, String email){

        ModelItem modelItem = modelItemDao.findFirstById(modelId);

        String author = modelItem.getAuthor();

        if(author.equals(email)) {
            modelItem.setAlias(aliasList);
            modelItemDao.save(modelItem);

            return "suc";
        }else{
            ModelItemUpdateDTO modelItemUpdateDTO = new ModelItemUpdateDTO();
            modelItemUpdateDTO.setOriginId(modelItem.getId());
            modelItemUpdateDTO.setAlias(aliasList);

            modelItemService.update(modelItemUpdateDTO,email);

            return "version";
        }

    }

    public String updateLocalizations(String modelId, List<Localization> localizations, String email){

        ModelItem modelItem = modelItemDao.findFirstById(modelId);

        String author = modelItem.getAuthor();

        if(author.equals(email)) {
            modelItem.setLocalizationList(localizations);
            modelItemDao.save(modelItem);

            return "suc";
        }else{
            ModelItemUpdateDTO modelItemUpdateDTO = new ModelItemUpdateDTO();
            modelItemUpdateDTO.setOriginId(modelItem.getId());

            modelItemUpdateDTO.setLocalizationList(localizations);

            modelItemService.update(modelItemUpdateDTO,email);

            return "version";
        }

    }

    public String updateReferences(String modelId, List<Article> references, String email){

        ModelItem modelItem = modelItemDao.findFirstById(modelId);

        String author = modelItem.getAuthor();

        List<String> reference_ids = new ArrayList<>();
        //参考文献插入数据库
        for(int i = 0; i < references.size(); i++){
            Article reference = references.get(i);
            List<Article> articles = articleDao.findAllByTitle(reference.getTitle());
            Boolean find = false;
            for(Article art : articles){
                if(reference.isSame(art)){
                    find = true;
                    reference_ids.add(art.getId());
                    break;
                }
            }
            if(!find){
                reference_ids.add(reference.getId());
                articleDao.insert(reference);
            }
        }

        if(author.equals(email)) {
            modelItem.setReferences(reference_ids);
            modelItemDao.save(modelItem);

            return "suc";
        }else{
            ModelItemUpdateDTO modelItemUpdateDTO = new ModelItemUpdateDTO();
            modelItemUpdateDTO.setOriginId(modelItem.getId());

            modelItemUpdateDTO.setReferences(reference_ids);

            modelItemService.update(modelItemUpdateDTO,email);

            return "version";
        }

    }

    public JSONObject setRelation(String modelId,String type,List<String> relations,String user){

        ModelItem modelItem=modelItemDao.findFirstById(modelId);
        ModelItemRelate relate=modelItem.getRelate();

        List<String> relationDelete=new ArrayList<>();//要被删除的关系
        List<String> relationAdd=new ArrayList<>();//要添加的关系

        switch (type){
            case "dataItem":

                for(int i=0;i<relate.getDataItems().size();i++){
                    relationDelete.add(relate.getDataItems().get(i));
                }

                for(int i=0;i<relations.size();i++){
                    relationAdd.add(relations.get(i));
                }

                //筛选出要删除和要添加的条目
                for(int i=0;i<relationDelete.size();i++){
                    for(int j=0;j<relationAdd.size();j++){
                        if(relationDelete.get(i).equals(relationAdd.get(j))){
                            relationDelete.set(i,"");
                            relationAdd.set(j,"");
                            break;
                        }
                    }
                }
                //找到对应条目，删除关联
                for(int i=0;i<relationDelete.size();i++){
                    String id=relationDelete.get(i);
                    if(!id.equals("")) {
                        DataItem dataItem = dataItemDao.findFirstById(id);
                        if(dataItem.getStatus().equals("Private")){
                            relations.add(dataItem.getId());
                            continue;
                        }
                        if(dataItem.getRelatedModels()!=null) {
                            dataItem.getRelatedModels().remove(modelId);
                            dataItemDao.save(dataItem);
                        }
                    }
                }
                //找到对应条目，添加关联
                for(int i=0;i<relationAdd.size();i++){
                    String id=relationAdd.get(i);
                    if(!id.equals("")) {
                        DataItem dataItem = dataItemDao.findFirstById(id);
                        if(dataItem.getRelatedModels()!=null) {
                            dataItem.getRelatedModels().add(modelId);
                        }
                        else{
                            List<String> relatedModels=new ArrayList<>();
                            relatedModels.add(modelId);
                            dataItem.setRelatedModels(relatedModels);
                        }
                        dataItemDao.save(dataItem);
                    }
                }

                relate.setDataItems(relations);

                break;
            case "conceptualModel":
                relate.setConceptualModels(relations);
                break;
            case "logicalModel":
                relate.setLogicalModels(relations);
                break;
            case "computableModel":
                relate.setComputableModels(relations);
                break;
            case "concept":
                relate.setConcepts(relations);
                break;
            case "spatialReference":
                relate.setSpatialReferences(relations);
                break;
            case "template":
                relate.setTemplates(relations);
                break;
            case "unit":
                relate.setUnits(relations);
                break;
        }

        if(!user.equals(modelItem.getAuthor())){
            ModelItemUpdateDTO modelItemUpdateDTO = new ModelItemUpdateDTO();
            modelItemUpdateDTO.setOriginId(modelItem.getId());
            modelItemUpdateDTO.setRelate(relate);

            update(modelItemUpdateDTO,user);

            JSONObject result = new JSONObject();
            result.put("type","version");
            return result;
        }else{

            modelItem.setRelate(relate);

            modelItemDao.save(modelItem);

            JSONObject result = new JSONObject();
            result.put("type","suc");
            return result;
        }
    }

    public JSONObject setModelRelation(String id, List<ModelRelation> modelRelationListNew,String user) {
        ModelItem modelItem = modelItemDao.findFirstById(id);
        ModelItemRelate modelItemRelate = modelItem.getRelate();
        List<ModelRelation> modelRelationListOld = modelItemRelate.getModelRelationList();

        List<ModelRelation> relationIntersection = new ArrayList<>();

        if(!user.equals(modelItem.getAuthor())){
            ModelItemUpdateDTO modelItemUpdateDTO = new ModelItemUpdateDTO();
            modelItemUpdateDTO.setOriginId(modelItem.getId());
            modelItemRelate.setModelRelationList(modelRelationListNew);
            modelItemUpdateDTO.setRelate(modelItemRelate);//

            update(modelItemUpdateDTO,user);

            JSONObject result = new JSONObject();
            result.put("type","version");

            return result;
        }else {
            for (int i = 0; i < modelRelationListNew.size(); i++) {
                ModelRelation modelRelationNew = modelRelationListNew.get(i);
                for (int j = 0; j < modelRelationListOld.size(); j++) {
                    ModelRelation modelRelationOld = modelRelationListOld.get(j);
                    if (modelRelationNew.getModelId().equals(modelRelationOld.getModelId())) {
                        relationIntersection.add(modelRelationListNew.get(i));
                        if(modelRelationNew.getRelation()!=modelRelationOld.getRelation()){

                            ModelItem modelItem1 = modelItemDao.findFirstById(modelRelationNew.getModelId());
                            for(int k = 0;k< modelItem1.getRelate().getModelRelationList().size();k++){
                                if(modelItem1.getRelate().getModelRelationList().get(k).getModelId().equals(id)){
                                    modelItem1.getRelate().getModelRelationList().get(k).setRelation(RelationTypeEnum.getOpposite(modelRelationNew.getRelation().getNumber()));
                                }
                            }
                            modelItemDao.save(modelItem1);
                        }
                        break;
                    }
                }
            }

            for (int i = 0; i < modelRelationListNew.size(); i++) {
                ModelRelation modelRelation = modelRelationListNew.get(i);
                boolean exist = false;
                for (int j = 0; j < relationIntersection.size(); j++) {
                    if (modelRelation.getModelId().equals(relationIntersection.get(j).getModelId())) {
                        exist = true;
                        break;
                    }
                }
                if (!exist) {

                    ModelRelation modelRelation1 = new ModelRelation();
                    modelRelation1.setModelId(id);
                    modelRelation1.setRelation(RelationTypeEnum.getOpposite(modelRelation.getRelation().getNumber()));
                    ModelItem modelItem1 = modelItemDao.findFirstById(modelRelation.getModelId());
                    modelItem1.getRelate().getModelRelationList().add(modelRelation1);
                    modelItemDao.save(modelItem1);
                }
            }

            for (int i = 0; i < modelRelationListOld.size(); i++) {
                ModelRelation modelRelation = modelRelationListOld.get(i);
                boolean exist = false;
                for (int j = 0; j < relationIntersection.size(); j++) {
                    if (modelRelation.getModelId().equals(relationIntersection.get(j).getModelId())) {
                        exist = true;
                        break;
                    }
                }
                if (!exist) {

                    ModelItem modelItem1 = modelItemDao.findFirstById(modelRelation.getModelId());
                    if(modelItem1.getStatus().equals("Private")){
                        modelRelationListNew.add(modelRelation);
                        continue;
                    }
                    List<ModelRelation> modelRelationList = modelItem1.getRelate().getModelRelationList();
                    for (ModelRelation modelRelation1 : modelRelationList) {
                        if (modelRelation1.getModelId().equals(id)) {
                            modelItem1.getRelate().getModelRelationList().remove(modelRelation1);
                            break;
                        }
                    }
                    modelItemDao.save(modelItem1);

                }
            }

            modelItem.getRelate().setModelRelationList(modelRelationListNew);

            modelItemDao.save(modelItem);

            JSONArray modelItemArray = new JSONArray();

            for (int i = 0; i < modelItem.getRelate().getModelRelationList().size(); i++) {
                String oidNew = modelItem.getRelate().getModelRelationList().get(i).getModelId();
                ModelItem modelItemNew = modelItemDao.findFirstById(oidNew);
                JSONObject modelItemJson = new JSONObject();
                modelItemJson.put("name", modelItemNew.getName());
                modelItemJson.put("oid", modelItemNew.getId());
                modelItemJson.put("description", modelItemNew.getOverview());
                modelItemJson.put("image", modelItemNew.getImage().equals("") ? null : htmlLoadPath + modelItemNew.getImage());
                modelItemJson.put("relation", modelItem.getRelate().getModelRelationList().get(i).getRelation().getText());
                modelItemArray.add(modelItemJson);
            }

            JSONObject result = new JSONObject();
            result.put("type","suc");
            result.put("data",modelItemArray);

            return result;
        }

//        List<ModelRelation> relationDelete = new ArrayList<>();
//        List<ModelRelation> relationAdd = new ArrayList<>();
//
//        for(int i=0;i<modelRelationList1.size();i++){
//            relationDelete.add(modelRelationList1.get(i));
//        }
//        for(int i=0;i<modelRelationList.size();i++){
//            relationAdd.add(modelRelationList.get(i));
//        }
    }

    /**
     * @Description 获取模型浏览和相关计算模型调用次数
     * @param id
     * @Return com.alibaba.fastjson.JSONObject
     * @Author kx
     * @Date 22/2/28
     **/
    public JSONObject getDailyViewAndInvokeCount(String id) {

        ModelItem modelItem = modelItemDao.findFirstById(id);
        List<String> computableModelIds = modelItem.getRelate().getComputableModels();
        List<ComputableModel> computableModelList=new ArrayList<>();
        for(int i=0;i<computableModelIds.size();i++){
            ComputableModel computableModel = computableModelDao.findFirstById(computableModelIds.get(i));
            computableModelList.add(computableModel);
        }

        StatisticsService statisticsService = new StatisticsService();
        JSONObject result = statisticsService.getDailyViewAndInvokeTimes(modelItem,computableModelList,30,null);

        return result;
    }

    //通过DOI从网络上查询文章
    public JSONObject getArticleByDOI(String Doi,String modelOid) throws IOException, DocumentException {
        String[] eles= Doi.split("/");

        String doi = eles[eles.length-2]+"/"+eles[eles.length-1];

        String xml =  searchByElsevierDOI(doi);

        JSONObject result = new JSONObject();

        if(xml == null){
            result.put("find",0);
            return result;
        }
        else  if (xml.equals("Connection timed out: connect") ) {
            result.put("find",-1);
            return result;
        }  else{
            //dom4j解析xml
            org.dom4j.Document doc = null;
            doc = DocumentHelper.parseText(xml);
            Element root = doc.getRootElement();
            System.out.println("根节点：" + root.getName());
            Element coredata = root.element("coredata");
            String title = coredata.elementTextTrim("title");
            String journal = coredata.elementTextTrim("publicationName");

            String pageRange = coredata.elementTextTrim("pageRange");
            String coverDate = coredata.elementTextTrim("coverDate");
            String volume = coredata.elementTextTrim("volume");
            List links = coredata.elements("link");
            String link = ((Element)links.get(1)).attribute("href").getValue();

            Iterator authorIte = coredata.elementIterator("creator");
            List<String> authors = new ArrayList<>();
            while (authorIte.hasNext()) {
                Element record = (Element) authorIte.next();
                String author = record.getText();
                authors.add(author);
            }

            Article article = new Article();
            article.setTitle(title);
            article.setJournal(journal);
            article.setVolume(volume);
            article.setPageRange(pageRange);
            article.setDate(coverDate);
            article.setAuthors(authors);
            article.setLink(link);
            article.setDoi(doi);
            if(findReferExisted(modelOid,doi)) {//同一模型条目下有重复上传
                result.put("find",2);
                result.put("article",article);
            }else{
                result.put("find",1);
                result.put("article",article);
            }
            doc = null;
            System.gc();
//            user中加入这个字段
//            User user = userDao.findFirstByUserName(contributor);
//            List<String>articles = user.getArticles();
//            articles.add(article.getOid());
//            user.setArticles(articles);

//            userDao.save(user);
            return result;
        }
    }

    public boolean findReferExisted(String modelOid,String doi){
        if(!modelOid.equals("")&&modelOid!=null){
            ModelItem modelItem = modelItemDao.findFirstById(modelOid);
            List<String> references = modelItem.getReferences();
            for(String reference:references){
                if(reference.equals(doi))
                    return true;
            }
        }
        return false;
    }

    public String searchByElsevierDOI(String doi) throws IOException {
        String str = "https://api.elsevier.com/content/article/doi/"+doi+"?apiKey=e59f63ca86ba019181c8d3a53f495532";
        URL url = new URL(str);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(9000);
        connection.connect();
        int responseCode = connection.getResponseCode();
        String articleXml = new String();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            int length = connection.getContentLength();
            while ((line = reader.readLine()) != null) {
                if (!line.equals("")) {
                    articleXml+=line+"\n";
                }
            }
            reader.close();
            connection.disconnect();
            return articleXml;
        } else {
            //DOIdata.add(String.valueOf(responseCode));
            return null;
        }
    }

}
