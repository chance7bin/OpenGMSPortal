package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.dao.*;
import njgis.opengms.portal.entity.doo.AuthorInfo;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.Localization;
import njgis.opengms.portal.entity.doo.base.PortalItem;
import njgis.opengms.portal.entity.doo.model.ModelItemRelate;
import njgis.opengms.portal.entity.doo.model.ModelRelation;
import njgis.opengms.portal.entity.dto.model.modelItem.ModelItemAddDTO;
import njgis.opengms.portal.entity.dto.model.modelItem.ModelItemFindDTO;
import njgis.opengms.portal.entity.dto.model.modelItem.ModelItemResultDTO;
import njgis.opengms.portal.entity.dto.model.modelItem.ModelItemUpdateDTO;
import njgis.opengms.portal.entity.po.*;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.utils.ImageUtils;
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
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
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

    public ModelAndView getPage(PortalItem portalItem) {
        ModelAndView modelAndView=new ModelAndView();

        ModelItem modelInfo = (ModelItem) genericService.recordViewCount(portalItem);

        modelItemDao.save(modelInfo);

        List<String> classifications = new ArrayList<>();
        for (String classification : modelInfo.getClassifications()) {
            classifications.add(classificationDao.findFirstById(classification).getNameEn());
        }

        JSONArray classResult = modelClassificationService.getClassifications(classifications);
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
                String idNew = modelItems.get(i).getId();
                ModelItem modelItemNew = modelItemDao.findFirstById(idNew);
                if (modelItemNew.getStatus().equals("Private")) {
                    continue;
                }
                JSONObject modelItemJson = new JSONObject();
                modelItemJson.put("name", modelItemNew.getName());
                modelItemJson.put("id", modelItemNew.getId());
                modelItemJson.put("relation",modelItems.get(i).getRelation().getText());
                modelItemJson.put("description", modelItemNew.getOverview());
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
            conceptualJson.put("description",conceptualModel.getOverview());
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
            logicalJson.put("description",logicalModel.getOverview());
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
            computableJson.put("description",computableModel.getOverview());
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
                jsonObj.put("description", desc);
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
                jsonObj.put("description", spatialReference.getOverview());
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
                jsonObj.put("description", template.getOverview());
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

                jsonObj.put("description", unit.getOverview());
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
                dataJson.put("description",dataItem.getOverview());
                dataItemArray.add(dataJson);
            }
        }

        //TODO dataHubs dataMethod


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
        modelAndView.addObject("detail",detailResult);
        modelAndView.addObject("date",dateResult);
        modelAndView.addObject("year",calendar.get(Calendar.YEAR));
        modelAndView.addObject("modelItems",modelItemArray);
        modelAndView.addObject("conceptualModels",conceptualArray);
        modelAndView.addObject("logicalModels",logicalArray);
        modelAndView.addObject("computableModels",computableArray);
        modelAndView.addObject("concepts",conceptArray);
        modelAndView.addObject("spatialReferences",spatialReferenceArray);
        modelAndView.addObject("templates",templateArray);
        modelAndView.addObject("units",unitArray);
        modelAndView.addObject("exLinks",exLinks);
        modelAndView.addObject("dataSpaceFiles",dataSpaceFiles);
        modelAndView.addObject("dataItems",dataItemArray);
        modelAndView.addObject("user", userJson);
        modelAndView.addObject("authorship", authorshipString);
        modelAndView.addObject("lastModifier", modifierJson);
        modelAndView.addObject("lastModifyTime", lastModifyTime);
        modelAndView.addObject("references", JSONArray.parseArray(JSON.toJSONString(modelInfo.getReferences())));

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
                ModelItem modelItem1 = modelItemDao.findFirstById(relatedModelList.get(i).getId());
                List<ModelRelation> relatedModelList1 = modelItem1.getRelate().getModelRelationList();
                for(int j = 0;j<relatedModelList1.size();j++){
                    if(relatedModelList1.get(j).getId().equals(modelItem.getId())){
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


    public JSONObject update(ModelItemUpdateDTO modelItemUpdateDTO, String uid){
        ModelItem modelItem=modelItemDao.findFirstById(modelItemUpdateDTO.getOriginId());

        String author=modelItem.getAuthor();
        String authorUserName = author;
        if(!modelItem.isLock()) {
            if (author.equals(uid)) {


//                List<String> versions = modelItem.getVersions();
//                if (versions == null || versions.size() == 0) {
//                    ModelItemVersion modelItemVersionOri = new ModelItemVersion();
//                    BeanUtils.copyProperties(modelItem, modelItemVersionOri, "id");
//                    modelItemVersionOri.setId(UUID.randomUUID().toString());
//                    modelItemVersionOri.setOriginOid(modelItem.getId());
//                    modelItemVersionOri.setVerNumber((long) 0);
//                    modelItemVersionOri.setVerStatus(2);
//                    modelItemVersionOri.setModifier(modelItem.getAuthor());
//                    modelItemVersionOri.setModifyTime(modelItem.getCreateTime());
//                    modelItemVersionDao.insert(modelItemVersionOri);
//
//                    versions = new ArrayList<>();
//                    versions.add(modelItemVersionOri.getId());
//                    modelItem.setVersions(versions);
//                }

                BeanUtils.copyProperties(modelItemUpdateDTO, modelItem);
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

//                ModelItemVersion modelItemVersion = new ModelItemVersion();
//                BeanUtils.copyProperties(modelItem,modelItemVersion,"id");
//                modelItemVersion.setOriginOid(modelItem.getId());
//                modelItemVersion.setId(UUID.randomUUID().toString());
//                modelItemVersion.setModifier(author);
//                modelItemVersion.setModifyTime(curDate);
//                modelItemVersion.setVerNumber(curDate.getTime());
//                modelItemVersion.setVerStatus(2);
//                modelItemVersion.setCreator(author);
//                modelItemVersionDao.insert(modelItemVersion);


//                versions.add(modelItemVersion.getId());
//                modelItem.setVersions(versions);

                modelItemDao.save(modelItem);

                JSONObject result = new JSONObject();
                result.put("method", "update");
                result.put("id", modelItem.getId());

                return result;
            } else {

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
//                modelItemVersion.setOriginid(modelItem.getId());
//                modelItemVersion.setId(UUID.randomUUID().toString());
//                modelItemVersion.setModifier(uid);
//                Date curDate = new Date();
//                modelItemVersion.setModifyTime(curDate);
//                modelItemVersion.setVerNumber(curDate.getTime());
//                modelItemVersion.setVerStatus(0);
//                userService.noticeNumPlusPlus(authorUserName);
//
//                List<Localization> localizationList = modelItemVersion.getLocalizationList();
//                for(int l = 0;l<localizationList.size();l++){
//                    Localization localization = localizationList.get(l);
//                    localization.setDescription(Utils.saveBase64Image(localization.getDescription(),modelItemVersion.getId(),resourcePath,htmlLoadPath));
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
                JSONObject result = new JSONObject();
                result.put("method", "version");
//                result.put("id", modelItemVersion.getId());
//
                return result;
            }
        }
        else{

            return null;
        }
    }



    public JsonResult addRelatedData(String id, List<String> relatedData) {
        ModelItem modelItem = modelItemDao.findFirstById(id);

        modelItem.getRelate().setDataItems(relatedData);

        modelItemDao.save(modelItem);

        return ResultUtils.success();
    }


    public List<Map<String, String>> getRelatedData(String id) {

        ModelItem modelItem = modelItemDao.findFirstById(id);

        List<String> relatedData = modelItem.getRelate().getDataItems();

        if (relatedData == null) {
            List<Map<String, String>> list = new ArrayList<>();
            return list;

        }

        List<Map<String, String>> data = new ArrayList<>();
        Map<String, String> dataInfo;
        DataItem dataItem;


        for (int i = 0; i < relatedData.size(); i++) {
            //只取三个
            if (i == 3) {
                break;
            }

            modelItem = new ModelItem();

            dataItem = dataItemDao.findFirstById(relatedData.get(i));

            dataInfo = new HashMap<>();
            dataInfo.put("name", dataItem.getName());
            dataInfo.put("id", dataItem.getId());
            dataInfo.put("overview", dataItem.getOverview());

            data.add(dataInfo);

        }
        return data;

    }

    //getAllRelatedData
    public List<Map<String, String>> getAllRelatedData(String id, Integer more) {


        ModelItem modelItem = modelItemDao.findFirstById(id);

        List<String> relatedData = modelItem.getRelate().getDataItems();


        List<Map<String, String>> data = new ArrayList<>();

        DataItem dataItem;

        Map<String, String> dataInfo;
        if (relatedData == null) {
            dataInfo = new HashMap<>();
            dataInfo.put("all", "all");
            data.add(dataInfo);
            return data;
        }


        if (more - 5 > relatedData.size() || more - 5 == relatedData.size()) {

            dataInfo = new HashMap<>();
            dataInfo.put("all", "all");
            data.add(dataInfo);

            return data;
        }

        for (int i = more - 5; i < more && i < relatedData.size(); i++) {
            //只取三个

            dataItem = new DataItem();

            dataItem = dataItemDao.findFirstById(relatedData.get(i));

            dataInfo = new HashMap<>();
            dataInfo.put("name", dataItem.getName());
            dataInfo.put("oid", dataItem.getId());
            dataInfo.put("overview", dataItem.getOverview());

            data.add(dataInfo);

        }
        return data;

    }




}
