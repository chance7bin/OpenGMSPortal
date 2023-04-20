package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.dao.*;
import njgis.opengms.portal.entity.doo.*;
import njgis.opengms.portal.entity.doo.base.PortalItem;
import njgis.opengms.portal.entity.doo.theme.*;
import njgis.opengms.portal.entity.dto.AddDTO;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.entity.dto.community.KnowledgeDTO;
import njgis.opengms.portal.entity.po.*;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.enums.OperationEnum;
import njgis.opengms.portal.utils.ResultUtils;
import njgis.opengms.portal.utils.Utils;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/30
 */
@Slf4j
@Service
public class RepositoryService {

    @Autowired
    TemplateDao templateDao;

    @Autowired
    ConceptDao conceptDao;

    @Autowired
    TemplateClassificationDao templateClassificationDao;

    @Autowired
    ConceptClassificationDao conceptClassificationDao;

    @Autowired
    SpatialReferenceDao spatialReferenceDao;

    @Autowired
    SpatialReferenceClassificationDao spatialReferenceClassificationDao;

    @Autowired
    UnitDao unitDao;

    @Autowired
    UnitClassificationDao unitClassificationDao;

    @Autowired
    GenericService genericService;

    @Autowired
    UserService userService;

    @Autowired
    VersionService versionService;

    @Autowired
    NoticeService noticeService;

    @Autowired
    ThemeDao themeDao;

    @Autowired
    DataItemDao dataItemDao;

    @Autowired
    ModelItemDao modelItemDao;

    @Autowired
    DataMethodDao dataMethodDao;

    @Autowired
    UserDao userDao;

    @Autowired
    UnitConversionDao unitConversionDao;

    @Value("${htmlLoadPath}")
    private String htmlLoadPath;

    @Value("${resourcePath}")
    String resourcePath;

    @Autowired
    RedisService redisService;


    public JSONArray getClassification(List<String> classifications, GenericCategoryDao categoryDao){

        // JSONArray array1 = new JSONArray();
        //
        // for(int i=0;i<classifications.size();i++){
        //
        //     JSONArray array = new JSONArray();
        //     String classId=classifications.get(i);
        //
        //     do {
        //         GenericCategory classification = (GenericCategory) categoryDao.findFirstById(classId);
        //         JSONObject jsonObject = new JSONObject();
        //         jsonObject.put("name",classification.getNameEn());
        //         jsonObject.put("name_zh",classification.getNameCn());
        //         jsonObject.put("id",classification.getId());
        //
        //         array.add(jsonObject);
        //         classId=classification.getParentId();
        //
        //     } while (classId != null && !classId.equals("null"));
        //
        //     // classification= (GenericCategory) categoryDao.findFirstById(classId);
        //     // array.add(classification.getNameEn());
        //     // classId=classification.getParentId();
        //     // classification= (GenericCategory) categoryDao.findFirstById(classId);
        //     // array.add(classification.getNameEn());
        //
        //     for(int j=array.size()-1;j>=0;j--){
        //         array1.add(array.get(j));
        //     }
        // }
        // return array1;

        JSONArray classResult=new JSONArray();

        for(int i=0;i<classifications.size();i++){

            JSONArray array=new JSONArray();
            String classId=classifications.get(i);

            do{
                GenericCategory classification = (GenericCategory) categoryDao.findFirstById(classId);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name_zh",classification.getNameCn());
                jsonObject.put("name",classification.getNameEn());
                jsonObject.put("id",classification.getId());

                array.add(jsonObject);
                classId=classification.getParentId();
            }while(classId != null && !classId.equals("null"));

            JSONArray array1=new JSONArray();
            for(int j=array.size()-1;j>=0;j--){
                array1.add(array.get(j));
            }

            classResult.add(array1);

        }
        // System.out.println(classResult);
        return classResult;
    }


    /**
     * 往ModelAndView添加公共的字段
     * @param item
     * @param categoryDao
     * @param modelAndView
     * @return org.springframework.web.servlet.ModelAndView
     * @Author bin
     **/
    public ModelAndView getCommonAttribute(PortalItem item, GenericCategoryDao categoryDao, ModelAndView modelAndView){
        //兼容两种格式的数据
        // GenericCategory classification = null;

        JSONArray classResult = new JSONArray();

        List<String> classifications = item.getClassifications();
        /*for(int i=0;i<classifications.size();i++){

            JSONArray array = new JSONArray();
            String classId=classifications.get(i);

            do {
                GenericCategory classification = (GenericCategory) categoryDao.findFirstById(classId);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name",classification.getNameEn());
                jsonObject.put("name_zh",classification.getNameCn());
                jsonObject.put("id",classification.getId());

                array.add(jsonObject);
                classId=classification.getParentId();

            } while (classId != null && !classId.equals("null"));

            // classification= (GenericCategory) categoryDao.findFirstById(classId);
            // array.add(classification.getNameEn());
            // classId=classification.getParentId();
            // classification= (GenericCategory) categoryDao.findFirstById(classId);
            // array.add(classification.getNameEn());

            JSONArray array1 = new JSONArray();
            for(int j=array.size()-1;j>=0;j--){
                array1.add(array.get(j));
            }
            classResult.add(array1);
        }*/

//        classResult.add(getClassification(classifications, categoryDao));
        classResult = getClassification(classifications, categoryDao);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String lastModifyTime=sdf.format(item.getLastModifyTime());

        //用户信息
        JSONObject userJson = userService.getItemUserInfoByEmail(item.getAuthor());

        //修改者信息
        String lastModifier=item.getLastModifier();
        JSONObject modifierJson=null;
        if(lastModifier!=null){
            modifierJson = userService.getItemUserInfoByEmail(lastModifier);
        }

        modelAndView.addObject("itemInfo", item);
        modelAndView.addObject("classifications", classResult);
        modelAndView.addObject("image", htmlLoadPath+item.getImage());
        modelAndView.addObject("year", item.getCreateTime().getYear()+1900);
        modelAndView.addObject("date", sdf.format(item.getCreateTime()));
        modelAndView.addObject("user", userJson);
        modelAndView.addObject("lastModifier", modifierJson);
        modelAndView.addObject("lastModifyTime", lastModifyTime);

        return modelAndView;
    }


    /**
     * 公用插入部分
     * @param item 新增条目实例
     * @param addDTO DTO
     * @param email 贡献者email
     * @param itemType 条目类型
     * @return njgis.opengms.portal.entity.doo.base.PortalItem
     * @Author bin
     **/
    public PortalItem commonInsertPart(PortalItem item, AddDTO addDTO, String email, ItemTypeEnum itemType){

        BeanUtils.copyProperties(addDTO, item);

        Date now = new Date();
        item.setCreateTime(now);
        item.setLastModifyTime(now);
        item.setAuthor(email);

        //设置图片
        String path = "/repository/"+ itemType.getText() + "/" + UUID.randomUUID().toString() + ".jpg";
        if(addDTO.getUploadImage()!=null){
            String[] strs=addDTO.getUploadImage().split(",");
            if(strs.length>1) {
                String imgStr = addDTO.getUploadImage().split(",")[1];
                Utils.base64StrToImage(imgStr, resourcePath + path);
                item.setImage(path);
            }
            else {
                item.setImage("");
            }
        }else{
            item.setImage(null);
        }

        return item;

    }

    /**
     * 公用更新部分
     * @param id 更新条目id
     * @param email 修改者email
     * @param updateDTO DTO
     * @param itemType 修改条目类型
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult commonUpdatePart(String id, String email, AddDTO updateDTO, ItemTypeEnum itemType){
        JSONObject result = new JSONObject();
        JSONObject factory = genericService.daoFactory(itemType);
        GenericItemDao itemDao = (GenericItemDao)factory.get("itemDao");
        PortalItem item = (PortalItem) itemDao.findFirstById(id);
        List<String> versions = item.getVersions();
        String author = item.getAuthor();
        String originalItemName = item.getName();
        if (!item.isLock()) {

            //如果修改者不是作者的话把该条目锁住送去审核
            //提前单独判断的原因是对item统一修改后里面的值已经是新的了，再保存就没效果了
            if (!author.equals(email)){
                item.setLock(true);
                // itemDao.save(item);
                redisService.saveItem(item,itemType);
            } else {
                if (versions == null || versions.size() == 0) {

                    Version version = versionService.addVersion(item, email, originalItemName);

                    versions = new ArrayList<>();
                    versions.add(version.getId());
                    item.setVersions(versions);
                }
            }

            item = updatePart(item,updateDTO,itemType,email);

            Version new_version = versionService.addVersion(item, email,originalItemName,false);
            if (author.equals(email)) {
                versions.add(new_version.getId());
                item.setVersions(versions);
                // itemDao.save(item);
                redisService.saveItem(item,itemType);
                result.put("method", "update");
                result.put("id", item.getId());
            } else {

                //发送通知
                List<String> recipientList = new ArrayList<>();
                recipientList.add(author);
                recipientList = noticeService.addItemAdmins(recipientList,item.getAdmins());
                recipientList = noticeService.addPortalAdmins(recipientList);
                recipientList = noticeService.addPortalRoot(recipientList);
                noticeService.sendNoticeContains(email, OperationEnum.Edit,ItemTypeEnum.Version,new_version,recipientList);

                result.put("method", "version");
                result.put("versionId", new_version.getId());

            }
            // return result;
        } else {
            result = null;
        }

        if(result==null){
            return ResultUtils.error(-1,"There is another version have not been checked, please contact opengms@njnu.edu.cn if you want to modify this item.");
        }
        else {
            return ResultUtils.success(result);
        }

    }


    public PortalItem updatePart(PortalItem item, AddDTO updateDTO, ItemTypeEnum itemType, String email){
        BeanUtils.copyProperties(updateDTO, item, Utils.getNullPropertyNames(updateDTO));
        //判断是否为新图片
        String uploadImage = updateDTO.getUploadImage();
        if (uploadImage!=null&&!uploadImage.contains("/" + itemType.getText() + "/") && !uploadImage.equals("")) {
            //删除旧图片
            File file = new File(resourcePath + item.getImage());
            if (file.exists() && file.isFile())
                file.delete();
            //添加新图片
            String path = "/repository/"+ itemType.getText() + "/" + UUID.randomUUID().toString() + ".jpg";
            String imgStr = uploadImage.split(",")[1];
            Utils.base64StrToImage(imgStr, resourcePath + path);
            item.setImage(path);
        }
        Date now = new Date();
        item.setLastModifyTime(now);
        item.setLastModifier(email);
        return item;
    }

    /**
     * 公用删除部分
     * @param id 删除条目id
     * @param email 修改者email
     * @param itemType 删除类型
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult commonDeletePart(String id, String email, ItemTypeEnum itemType){

        JSONObject factory = genericService.daoFactory(itemType);
        GenericItemDao itemDao = (GenericItemDao) factory.get("itemDao");

        PortalItem item = (PortalItem) itemDao.findFirstById(id);
        if (item != null) {
            String image = item.getImage();
            if (image!=null&&image.contains("/" + itemType.getText() + "/")) {
                File file = new File(resourcePath + item.getImage());
                if (file.exists() && file.isFile())
                    file.delete();
            }
            // itemDao.delete(item);
            redisService.deleteItem(item, itemType);
            try {
                userService.updateUserResourceCount(email,itemType,"delete");
            }catch (Exception e){
                return ResultUtils.error("update user resource fail");
            }
            return ResultUtils.success();
        } else {
            return ResultUtils.error("can not find" + itemType.getText());
        }
    }

    /**
     * 根据id得到item信息
     * @param id
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult getRepositoryById(String id, GenericItemDao itemDao) {
        try {
            PortalItem item = genericService.getById(id,itemDao);
            item.setImage(item.getImage()== null || item.getImage().equals("")?"":htmlLoadPath+item.getImage());
            return ResultUtils.success(item);
        }
        catch (MyException e){
            return ResultUtils.error(e.getMessage());
        }

    }

    public ModelAndView getTemplatePage(Template template) {
        return getTemplatePage(template, false);
    }

    public ModelAndView getTemplatePage(Template template, boolean history) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("templateInfo");

        if(template==null){
            modelAndView.setViewName("error/404");
            return modelAndView;
        }

        template=(Template)genericService.recordViewCount(template);
        if (!history){
            templateDao.save(template);
        }

        modelAndView = getCommonAttribute(template, templateClassificationDao, modelAndView);
        modelAndView.addObject("relateModelAndData", getRelateModelAndDataInfo(template.getRelateModelAndData()));
        modelAndView.addObject("modularType", ItemTypeEnum.Template);
        return modelAndView;
    }

    /**
     * 得到template详情页面
     * @param id 
     * @return org.springframework.web.servlet.ModelAndView 
     * @Author bin
     **/
    public ModelAndView getTemplatePage(String id) {
        Template template = templateDao.findFirstById(id);

        return getTemplatePage(template);
    }

    public ModelAndView getConceptPage(Concept concept) {
        return getConceptPage(concept, false);
    }

    public ModelAndView getConceptPage(Concept concept, boolean history) {
            ModelAndView modelAndView = new ModelAndView();
            if(concept==null){
                modelAndView.setViewName("error/404");
                return modelAndView;
            }

            modelAndView.setViewName("conceptInfo");

            concept = (Concept)genericService.recordViewCount(concept);
            if (!history){
                conceptDao.save(concept);
            }

            modelAndView = getCommonAttribute(concept, conceptClassificationDao, modelAndView);

            List<String> related = concept.getRelated();
        JSONArray relateArray = new JSONArray();
        if (related != null) {
            for (String relatedId : related) {
                Concept relatedConcept = conceptDao.findFirstById(relatedId);
                String name = relatedConcept.getName();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", relatedId);
                jsonObject.put("name", name);
                relateArray.add(jsonObject);
            }
        }

        modelAndView.addObject("relateModelAndData", getRelateModelAndDataInfo(concept.getRelateModelAndData()));
        // modelAndView.addObject("relateModelAndData", concept.getRelateModelAndData());
        modelAndView.addObject("related", relateArray);
        modelAndView.addObject("modularType", ItemTypeEnum.Concept);
        return modelAndView;
    }

    // 返回相关的模型和数据的基本信息
    public JSONObject getRelateModelAndDataInfo(RelateModelAndData relateModelAndData){
        JSONObject result = new JSONObject();
        relateModelAndData = relateModelAndData == null ? new RelateModelAndData() : relateModelAndData;
        List<String> modelItems = relateModelAndData.getModelItems();
        JSONArray modelArray = new JSONArray();
        for (String id : modelItems) {
            JSONObject item = genericService.getBasicInfo(ItemTypeEnum.ModelItem, id);
            if(item == null){
                continue;
            }
            modelArray.add(item);
        }
        result.put("modelItem", modelArray);

        List<String> computableModels = relateModelAndData.getComputableModels();
        JSONArray computableModelsArray = new JSONArray();
        for (String id : computableModels) {
            JSONObject item = genericService.getBasicInfo(ItemTypeEnum.ComputableModel, id);
            if(item == null){
                continue;
            }
            computableModelsArray.add(item);
        }
        result.put("computableModel", computableModelsArray);

        List<String> conceptualModels = relateModelAndData.getConceptualModels();
        JSONArray conceptualModelsArray = new JSONArray();
        for (String id : conceptualModels) {
            JSONObject item = genericService.getBasicInfo(ItemTypeEnum.ConceptualModel, id);
            if(item == null){
                continue;
            }
            conceptualModelsArray.add(item);
        }
        result.put("conceptualModel", conceptualModelsArray);

        List<String> logicalModels = relateModelAndData.getLogicalModels();
        JSONArray logicalModelsArray = new JSONArray();
        for (String id : logicalModels) {
            JSONObject item = genericService.getBasicInfo(ItemTypeEnum.LogicalModel, id);
            if(item == null){
                continue;
            }
            logicalModelsArray.add(item);
        }
        result.put("logicalModel", logicalModelsArray);


        List<String> dataItems = relateModelAndData.getDataItems();
        JSONArray dataItemsArray = new JSONArray();
        for (String id : dataItems) {
            JSONObject item = genericService.getBasicInfo(ItemTypeEnum.DataItem, id);
            if(item == null){
                continue;
            }
            dataItemsArray.add(item);
        }
        result.put("dataItem", dataItemsArray);

        List<String> dataHubs = relateModelAndData.getDataHubs();
        JSONArray dataHubsArray = new JSONArray();
        for (String id : dataHubs) {
            JSONObject item = genericService.getBasicInfo(ItemTypeEnum.DataHub, id);
            if(item == null){
                continue;
            }
            dataHubsArray.add(item);
        }
        result.put("dataHub", dataHubsArray);

        List<String> dataMethods = relateModelAndData.getDataMethods();
        JSONArray dataMethodsArray = new JSONArray();
        for (String id : dataMethods) {
            JSONObject item = genericService.getBasicInfo(ItemTypeEnum.DataMethod, id);
            if(item == null){
                continue;
            }
            dataMethodsArray.add(item);
        }
        result.put("dataMethod", dataMethodsArray);

        return result;

    }

    /**
     * 得到concept详情页面
     * @param id
     * @return org.springframework.web.servlet.ModelAndView
     * @Author bin
     **/
    public ModelAndView getConceptPage(String id) {

        Concept concept = conceptDao.findFirstById(id);
        return getConceptPage(concept);
    }

    public ModelAndView getSpatialReferencePage(SpatialReference spatialReference) {
        return getSpatialReferencePage(spatialReference, false);
    }

    public ModelAndView getSpatialReferencePage(SpatialReference spatialReference, boolean history) {
        String flag="";
        ModelAndView modelAndView = new ModelAndView();
        if(spatialReference==null){
            modelAndView.setViewName("error/404");
            return modelAndView;
        }

        modelAndView.setViewName("spatialReferenceInfo");
        spatialReference=(SpatialReference)genericService.recordViewCount(spatialReference);

        if (!history){
            spatialReferenceDao.save(spatialReference);
        }

        modelAndView = getCommonAttribute(spatialReference, spatialReferenceClassificationDao, modelAndView);

        //兼容两种格式的数据
        GenericCategory classification = null;

        List<String> classifications = spatialReference.getClassifications();
        for(int i=0;i<classifications.size();i++){
            String classId=classifications.get(i);
            classification=spatialReferenceClassificationDao.findFirstById(classId);
            flag = classification.getNameEn();
            if(!(flag.equals("Global") || flag.equals("Local")))
                flag = "other";
        }

        if(spatialReference.getXml()!=null)
        {
            org.dom4j.Document d = null;
            JSONArray localizationArray = new JSONArray();
            try {
                d = DocumentHelper.parseText(spatialReference.getXml());
                org.dom4j.Element root = d.getRootElement();
//            org.dom4j.Element Localizations = root.element("Localizations");
                List<org.dom4j.Element> LocalizationList = root.elements("Localization");
                for (org.dom4j.Element Localization : LocalizationList) {
                    String language = Localization.attributeValue("local");
                    String name = Localization.attributeValue("name");
                    String desc = Localization.attributeValue("description");
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("language", language);
                    jsonObject.put("name", name);
                    jsonObject.put("desc", desc);
                    localizationArray.add(jsonObject);
                }
            } catch (DocumentException e) {
                // e.printStackTrace();
                log.error(e.getMessage());
            }

            localizationArray.sort(new Comparator<Object>() {
                @Override
                public int compare(Object o1, Object o2) {
                    JSONObject a = (JSONObject) o1;
                    JSONObject b = (JSONObject) o2;
                    return a.getString("language").compareToIgnoreCase(b.getString("language"));
                }
            });
            modelAndView.addObject("localizations",localizationArray);
        }

        modelAndView.addObject("relateModelAndData", getRelateModelAndDataInfo(spatialReference.getRelateModelAndData()));
        modelAndView.addObject("isTemporal", flag);
        modelAndView.addObject("modularType", ItemTypeEnum.SpatialReference);
        return modelAndView;
    }

    /**
     * 得到spatialReference详情页面
     * @param id
     * @return org.springframework.web.servlet.ModelAndView
     * @Author bin
     **/
    public ModelAndView getSpatialReferencePage(String id) {
        SpatialReference spatialReference = spatialReferenceDao.findFirstById(id);
        return getSpatialReferencePage(spatialReference);

    }

    public ModelAndView getUnitPage(Unit unit) {
        return getUnitPage(unit, false);
    }

    public ModelAndView getUnitPage(Unit unit, boolean history) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("unitInfo");

        if(unit==null){
            modelAndView.setViewName("error/404");
            return modelAndView;
        }
        unit=(Unit)genericService.recordViewCount(unit);
        if (!history){
            unitDao.save(unit);
        }

        modelAndView = getCommonAttribute(unit, unitClassificationDao, modelAndView);

        if(unit.getXml()!=null)
        {
            org.dom4j.Document d = null;
            JSONArray localizationArray = new JSONArray();
            try {
                d = DocumentHelper.parseText(unit.getXml());
                org.dom4j.Element root = d.getRootElement();
                org.dom4j.Element Localizations = root.element("Localizations");
                List<org.dom4j.Element> LocalizationList = Localizations.elements("Localization");
                for (org.dom4j.Element Localization : LocalizationList) {
                    String language = Localization.attributeValue("Local");
                    String name = Localization.attributeValue("Name");
                    String desc = Localization.attributeValue("Description");
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("language", language);
                    jsonObject.put("name", name);
                    jsonObject.put("desc", desc);
                    localizationArray.add(jsonObject);
                }
            } catch (DocumentException e) {
                // e.printStackTrace();
                log.error(e.getMessage());
            }

            localizationArray.sort(new Comparator<Object>() {
                @Override
                public int compare(Object o1, Object o2) {
                    JSONObject a = (JSONObject) o1;
                    JSONObject b = (JSONObject) o2;
                    return a.getString("language").compareToIgnoreCase(b.getString("language"));
                }
            });
            modelAndView.addObject("localizations",localizationArray);
        }


        modelAndView.addObject("relateModelAndData", getRelateModelAndDataInfo(unit.getRelateModelAndData()));
        modelAndView.addObject("oid_cvt",unit.getConversionId());
        modelAndView.addObject("modularType", ItemTypeEnum.Unit);
        return modelAndView;
    }

    /**
     * 得到unit详情页面
     * @param id
     * @return org.springframework.web.servlet.ModelAndView
     * @Author bin
     **/
    public ModelAndView getUnitPage(String id) {
        Unit unit = unitDao.findFirstById(id);
        return getUnitPage(unit);

    }



    /**
     * 根据用户得到repository
     * @param
     * @return com.alibaba.fastjson.JSONObject
     * @Author bin
     **/
    public JSONObject getRepositoryByUser(FindDTO findDTO, String email, ItemTypeEnum itemType){
        Pageable pageable = genericService.getPageable(findDTO);
        JSONObject factory = genericService.daoFactory(itemType);
        GenericItemDao itemDao = (GenericItemDao)factory.get("itemDao");
        Page result = itemDao.findAllByAuthor(email, pageable);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("count", result.getTotalElements());
        jsonObject.put("concepts", result.getContent());
        return jsonObject;
    }


    /**
     * 根据名称和用户得到repository
     * @param
     * @return com.alibaba.fastjson.JSONObject
     * @Author bin
     **/
    public JSONObject getRepositoryByNameAndUser(FindDTO findDTO, String email, ItemTypeEnum itemType){
        Pageable pageable = genericService.getPageable(findDTO);
        JSONObject factory = genericService.daoFactory(itemType);
        GenericItemDao itemDao = (GenericItemDao)factory.get("itemDao");
        Page result = itemDao.findAllByNameContainsIgnoreCaseAndAuthor(findDTO.getSearchText() ,email, pageable);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("count", result.getTotalElements());
        jsonObject.put("concepts", result.getContent());
        return jsonObject;
    }


    /**
     * 得到theme信息
     * @param
     * @return org.springframework.web.servlet.ModelAndView
     * @Author bin
     **/
    public JSONArray getThematic() {
        List<Theme> themes= themeDao.findAll();
        Collections.sort(themes, Collections.reverseOrder());
        JSONArray themeRs = new JSONArray();
        for (int i=0;i<themes.size();i++){
            JSONObject themeR = new JSONObject();
            themeR.put("oid",themes.get(i).getId());
            themeR.put("img",themes.get(i).getImage());
            themeR.put("status",themes.get(i).getStatus());
            themeR.put("creator_name",themes.get(i).getCreator_name());
            themeR.put("creator_eid",themes.get(i).getCreator_eid());
            themeR.put("name",themes.get(i).getThemename());
            themeRs.add(themeR);
        }
        return themeRs;
    }

    public ModelAndView getThemePage(String id, HttpServletRequest req) throws InvocationTargetException {

        ModelAndView modelAndView = new ModelAndView();


        Theme theme = themeDao.findFirstById(id);
        theme=(Theme)genericService.recordViewCount(theme);
        themeDao.save(theme);

//        获取loginId
        HttpSession session = req.getSession();

        Object oid_obj = session.getAttribute("email");
        if(oid_obj!=null)
        {
            modelAndView.addObject("loginId",oid_obj.toString());
        }
        else {
            modelAndView.addObject("loginId",null);
        }


        if(theme==null){
            modelAndView.setViewName("error/404");
            return modelAndView;
        }

        modelAndView.setViewName("theme_info");
        modelAndView.addObject("info", theme);

        //详情页面
        //String detailResult;
        // String theme_detailDesc=theme.getDetail();
        String theme_detailDesc = "";
        if(theme.getLocalizationList().size() != 0){
            theme_detailDesc=theme.getLocalizationList().get(0).getDescription();
        }


        JSONArray array = new JSONArray();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String lastModifyTime = sdf.format(theme.getLastModifyTime());


//        model
        List<ClassInfo> classInfos = theme.getClassinfo();
        JSONArray classInfos_result = new JSONArray();
        for(int i=0;i<classInfos.size();i++){
            if(classInfos.get(i).getOid() == null && classInfos.get(i).getMcname() == null && classInfos.get(i).getChildren().size() == 0 && classInfos.get(i).getModelsoid() == null){
                continue;
            }
            classInfos_result.add(findChildren(classInfos.get(i)));

        }
        // System.out.println(classInfos_result);

//        data
        List<DataClassInfo> dataClassInfos = theme.getDataClassInfo();
        JSONArray dataClassInfos_result = new JSONArray();
        for(int i=0;i<dataClassInfos.size();++i){
            if(dataClassInfos.get(i).getOid() == null && dataClassInfos.get(i).getDcname() == null && dataClassInfos.get(i).getChildren().size() == 0 && dataClassInfos.get(i).getDatasoid() == null){
                continue;
            }
            dataClassInfos_result.add(findDataChildren(dataClassInfos.get(i)));
        }

//        data method
        List<DataMethodClassInfo> dataMethodClassInfos = theme.getDataMethodClassInfo();
        JSONArray dataMethodClassInfos_result = new JSONArray();
        if(dataMethodClassInfos!=null){
            for(int i=0;i<dataMethodClassInfos.size();++i){
                if(dataMethodClassInfos.get(i).getOid() == null && dataMethodClassInfos.get(i).getDmcname() == null && dataMethodClassInfos.get(i).getChildren().size() == 0 && dataMethodClassInfos.get(i).getDataMethodsoid() == null){
                    continue;
                }
                dataMethodClassInfos_result.add(findDataMethodChildren(dataMethodClassInfos.get(i)));
            }
        }
        else{
            dataMethodClassInfos_result = null;
        }

//        application
        List<Application> applications = theme.getApplication();
        JSONArray applications_result = new JSONArray();

        for(int i=0;i<applications.size();i++){
            JSONObject app = new JSONObject();
            Application application = applications.get(i);
            app.put("name",application.getApplicationname());
            app.put("link",application.getApplicationlink());
            app.put("image",application.getApplication_image());
            applications_result.add(app);
        }

        List<Maintainer> maintainers = theme.getMaintainer();
        JSONArray maintainer_result = new JSONArray();
        if (maintainers!=null) {
            for (int i = 0; i < maintainers.size(); i++) {
                JSONObject ma = new JSONObject();
                Maintainer maintainer = maintainers.get(i);
                ma.put("name", maintainer.getName());
                ma.put("id", maintainer.getId());
                User user = userDao.findFirstById(maintainer.getId());
                if(user!=null){
                    ma.put("image",user.getAvatar().equals("")?"":genericService.formatUserAvatar(user.getAvatar()));
                }

                maintainer_result.add(ma);
            }
        }


        modelAndView.addObject("image", htmlLoadPath+theme.getImage());
        modelAndView.addObject("detail",theme_detailDesc);
        modelAndView.addObject("references",JSONArray.parseArray(JSON.toJSONString(theme.getReferences())));
        modelAndView.addObject("modelClassInfos",classInfos_result);
        modelAndView.addObject("dataClassInfos",dataClassInfos_result);
        modelAndView.addObject("dataMethodClassInfos",dataMethodClassInfos_result);
        modelAndView.addObject("applications",applications_result);
        modelAndView.addObject("maintainer",maintainer_result);

        return modelAndView;
    }

    public Object findChildren(ClassInfo classInfo) throws InvocationTargetException {
        JSONObject classObj = new JSONObject();
        classObj.put("label",classInfo.getMcname());

        List<ClassInfo> childrenArr = new ArrayList<>();
        childrenArr = classInfo.getChildren();

        if(childrenArr==null||childrenArr.size()==0){

            //没有孩子
            JSONArray models=new JSONArray();
            List<String> modelOids=classInfo.getModelsoid();
            for(int j=0;j<modelOids.size();j++) {
                JSONObject model=new JSONObject();
                ModelItem modelItem=modelItemDao.findFirstById(modelOids.get(j));
                if(modelItem == null){
                    continue;
                }
                model.put("name",modelItem.getName());
                model.put("image",modelItem.getImage());
                model.put("oid",modelItem.getId());
                models.add(model);
            }
            classObj.put("content",models);
            classObj.put("children",new JSONArray());

        }
        else if(childrenArr.size()>0){

            JSONArray children = new JSONArray();
            for(int j=0;j<childrenArr.size();++j){
                children.add(findChildren(childrenArr.get(j)));
            }
            classObj.put("content",new JSONArray());
            classObj.put("children",children);

        }


        return classObj;


    }

    public Object findDataChildren(DataClassInfo dataClassInfo) throws InvocationTargetException {
        JSONObject classObj = new JSONObject();
        classObj.put("label",dataClassInfo.getDcname());

        List<DataClassInfo> childrenArr = new ArrayList<>();
        childrenArr = dataClassInfo.getChildren();

        if(childrenArr==null||childrenArr.size()==0){

            //没有孩子
            JSONArray models=new JSONArray();
            List<String> dataOids=dataClassInfo.getDatasoid();
            for(int j=0;j<dataOids.size();j++) {
                JSONObject data=new JSONObject();
                // ModelItem modelItem=modelItemDao.findFirstByOid(dataOids.get(j));
                DataItem dataItem = dataItemDao.findFirstById(dataOids.get(j));
                if(dataItem == null){
                    continue;
                }
                data.put("name",dataItem.getName());
                data.put("image",dataItem.getImage());
                data.put("oid",dataItem.getId());
                models.add(data);
            }
            classObj.put("content",models);
            classObj.put("children",new JSONArray());

        }
        else if(childrenArr.size()>0){

            JSONArray children = new JSONArray();
            for(int j=0;j<childrenArr.size();++j){
                children.add(findDataChildren(childrenArr.get(j)));
            }
            classObj.put("content",new JSONArray());
            classObj.put("children",children);

        }

        return classObj;


    }

    public Object findDataMethodChildren(DataMethodClassInfo dataMethodClassInfo) throws InvocationTargetException {
        JSONObject classObj = new JSONObject();
        classObj.put("label",dataMethodClassInfo.getDmcname());

        List<DataMethodClassInfo> childrenArr = new ArrayList<>();
        childrenArr = dataMethodClassInfo.getChildren();

        if(childrenArr==null||childrenArr.size()==0){

            //没有孩子
            JSONArray models=new JSONArray();
            List<String> dataMethodOids=dataMethodClassInfo.getDataMethodsoid();
            for(int j=0;j<dataMethodOids.size();j++) {
                JSONObject dataMethod=new JSONObject();
                DataMethod dataApplication = dataMethodDao.findFirstById(dataMethodOids.get(j));
                if(dataApplication == null){
                    continue;
                }

                dataMethod.put("name",dataApplication.getName());
                dataMethod.put("image",dataApplication.getImage());
                dataMethod.put("oid",dataApplication.getId());
                models.add(dataMethod);
            }
            classObj.put("content",models);
            classObj.put("children",new JSONArray());

        }
        else if(childrenArr.size()>0){

            JSONArray children = new JSONArray();
            for(int j=0;j<childrenArr.size();++j){
                children.add(findDataMethodChildren(childrenArr.get(j)));
            }
            classObj.put("content",new JSONArray());
            classObj.put("children",children);

        }

        return classObj;


    }


    public JSONObject getUnitConversionById(String id) {

        UnitConversion unitConversion= unitConversionDao.findFirstById(id);
        JSONObject result = new JSONObject();
        result.put("Units",unitConversion.getUnits());

        return result;

    }

    /**
     * 通用的更新knowledge方法
     *
     * @param knowledgeDTO
     * @param email
     * @return {@link JSONObject}
     * @author 7bin
     **/
    public JSONObject updateKnowledge(ItemTypeEnum itemType , KnowledgeDTO knowledgeDTO, String email) {

        GenericItemDao itemDao = (GenericItemDao)genericService.daoFactory(itemType).get("itemDao");

        PortalItem item = (PortalItem) itemDao.findFirstById(knowledgeDTO.getId());
        String author = item.getAuthor();
        List<String> versions = item.getVersions();
        if (!item.isLock()) {

            if (author.equals(email)) {
                if (versions == null || versions.size() == 0) {

                    Version version = versionService.addVersion(item, email, item.getName());

                    versions = new ArrayList<>();
                    versions.add(version.getId());
                    item.setVersions(versions);
                }
            } else {
                item.setLock(true);
                redisService.saveItem(item, itemType);
            }


            RelateKnowledge relateKnowledge = item.getRelateKnowledge();
            relateKnowledge = relateKnowledge == null ? new RelateKnowledge() : relateKnowledge;
            BeanUtils.copyProperties(knowledgeDTO, relateKnowledge, "id");
            item.setRelateKnowledge(relateKnowledge);

            item.setLastModifyTime(new Date());
            item.setLastModifier(email);

            JSONObject result = new JSONObject();

            Version version_new = versionService.addVersion(item, email, item.getName(), false);
            if (item.getAuthor().equals(email)) {
                versionService.updateKnowledge(version_new);

                versions.add(version_new.getId());
                item.setVersions(versions);

                redisService.saveItem(item, itemType);
                result.put("method", "update");
                result.put("id", item.getId());
            } else {
                // 发送通知
                noticeService.sendNoticeContainsAllAdmin(email, item.getAuthor(), item.getAdmins() ,ItemTypeEnum.Version,version_new, OperationEnum.Edit);

                result.put("method", "version");
                result.put("versionId", version_new.getId());
                return result;
            }
            return result;

        } else {
            return null;
        }
    }


    // 根据relateKnowledge获取community的相关信息
    public JSONObject getAllKnowledge(RelateKnowledge relateKnowledge){
        relateKnowledge = relateKnowledge == null ? new RelateKnowledge() : relateKnowledge;
        List<String> concepts = relateKnowledge.getConcepts();
        List<String> templates = relateKnowledge.getTemplates();
        List<String> units = relateKnowledge.getUnits();
        List<String> spatialReferences = relateKnowledge.getSpatialReferences();

        JSONArray conceptArray=new JSONArray();
        if(concepts!=null) {
            for (int i = 0; i < concepts.size(); i++) {
                String id = concepts.get(i);
                Concept concept = conceptDao.findFirstById(id);
                if (concept == null){continue;}
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
                if (spatialReference == null){continue;}
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
                if (template == null){continue;}
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
                if (unit == null){continue;}
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

        JSONObject relationJson = new JSONObject();
        relationJson.put("concepts",conceptArray);
        relationJson.put("spatialReferences",spatialReferenceArray);
        relationJson.put("templates",templateArray);
        relationJson.put("units",unitArray);

        return relationJson;

    }


    // 根据id跟type获取相关信息
    public JSONArray getRelatedResources(String id, ItemTypeEnum itemType){

        GenericItemDao itemDao = (GenericItemDao)genericService.daoFactory(itemType).get("itemDao");
        PortalItem item = (PortalItem)itemDao.findFirstById(id);
        RelateKnowledge relateKnowledge = item.getRelateKnowledge();
        return  getRelatedResources(relateKnowledge);

    }

    // 根据relateKnowledge获取community的相关信息2
    public JSONArray getRelatedResources(RelateKnowledge relation){

        relation = relation == null ? new RelateKnowledge() : relation;

        JSONArray result=new JSONArray();

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

        return result;
    }


}
