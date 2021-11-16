package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.dao.*;
import njgis.opengms.portal.entity.doo.GenericCategory;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.MyException;
import njgis.opengms.portal.entity.doo.base.PortalItem;
import njgis.opengms.portal.entity.dto.AddDTO;
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
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/30
 */
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

    @Value("${htmlLoadPath}")
    private String htmlLoadPath;

    @Value("${resourcePath}")
    String resourcePath;




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
        GenericCategory classification = null;

        JSONArray array = new JSONArray();
        JSONArray classResult = new JSONArray();

        List<String> classifications = item.getClassifications();
        for(int i=0;i<classifications.size();i++){
            array.clear();
            String classId=classifications.get(i);
            classification= (GenericCategory) categoryDao.findFirstById(classId);
            array.add(classification.getNameEn());
            classId=classification.getParentId();
            classification= (GenericCategory) categoryDao.findFirstById(classId);
            array.add(classification.getNameEn());

            JSONArray array1=new JSONArray();
            for(int j=array.size()-1;j>=0;j--){
                array1.add(array.getString(j));
            }
            classResult.add(array1);
        }

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

        modelAndView.addObject("info", item);
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
        String author = item.getAuthor();
        String originalItemName = item.getName();
        if (!item.isLock()) {

            //如果修改者不是作者的话把该条目锁住送去审核
            //提前单独判断的原因是对item统一修改后里面的值已经是新的了，再保存就没效果了
            if (!author.equals(email)){
                item.setLock(true);
                itemDao.save(item);
            }

            item = updatePart(item,updateDTO,itemType,email);

            if (author.equals(email)) {
                itemDao.save(item);
                result.put("method", "update");
                result.put("id", item.getId());
            } else {

                Version version = versionService.addVersion(item, email,originalItemName);
                //发送通知
                List<String> recipientList = Arrays.asList(author);
                noticeService.sendNoticeContainRoot(email, OperationEnum.Edit,version.getId(),recipientList);

                result.put("method", "version");
                result.put("versionId", version.getId());

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
        BeanUtils.copyProperties(updateDTO, item);
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
            itemDao.delete(item);
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


    /**
     * 得到template详情页面
     * @param id 
     * @return org.springframework.web.servlet.ModelAndView 
     * @Author bin
     **/
    public ModelAndView getTemplatePage(String id) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("templateInfo");

        Template template = templateDao.findFirstById(id);
        if(template==null){
            modelAndView.setViewName("error/404");
            return modelAndView;
        }

        template=(Template)genericService.recordViewCount(template);
        templateDao.save(template);

        modelAndView = getCommonAttribute(template, templateClassificationDao, modelAndView);

        return modelAndView;
    }

    /**
     * 得到concept详情页面
     * @param id
     * @return org.springframework.web.servlet.ModelAndView
     * @Author bin
     **/
    public ModelAndView getConceptPage(String id) {

        ModelAndView modelAndView = new ModelAndView();
        Concept concept = conceptDao.findFirstById(id);
        if(concept==null){
            modelAndView.setViewName("error/404");
            return modelAndView;
        }

        modelAndView.setViewName("conceptInfo");

        concept = (Concept)genericService.recordViewCount(concept);
        conceptDao.save(concept);

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

        modelAndView.addObject("related", relateArray);

        return modelAndView;
    }

    /**
     * 得到spatialReference详情页面
     * @param id
     * @return org.springframework.web.servlet.ModelAndView
     * @Author bin
     **/
    public ModelAndView getSpatialReferencePage(String id) {
        String flag="";
        ModelAndView modelAndView = new ModelAndView();

        SpatialReference spatialReference = spatialReferenceDao.findFirstById(id);
        if(spatialReference==null){
            modelAndView.setViewName("error/404");
            return modelAndView;
        }

        modelAndView.setViewName("spatialReferenceInfo");
        spatialReference=(SpatialReference)genericService.recordViewCount(spatialReference);
        spatialReferenceDao.save(spatialReference);

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
                e.printStackTrace();
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

        modelAndView.addObject("isTemporal", flag);

        return modelAndView;
    }

    /**
     * 得到unit详情页面
     * @param id
     * @return org.springframework.web.servlet.ModelAndView
     * @Author bin
     **/
    public ModelAndView getUnitPage(String id) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("unitInfo");

        Unit unit = unitDao.findFirstById(id);
        if(unit==null){
            modelAndView.setViewName("error/404");
            return modelAndView;
        }
        unit=(Unit)genericService.recordViewCount(unit);
        unitDao.save(unit);

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
                e.printStackTrace();
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


        modelAndView.addObject("oid_cvt",unit.getConversionId());

        return modelAndView;
    }




}
