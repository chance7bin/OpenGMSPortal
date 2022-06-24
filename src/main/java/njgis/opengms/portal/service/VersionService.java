package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.dao.*;
import njgis.opengms.portal.entity.doo.AuthorInfo;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.Localization;
import njgis.opengms.portal.entity.doo.base.PortalItem;
import njgis.opengms.portal.entity.doo.model.ModelItemRelate;
import njgis.opengms.portal.entity.doo.model.Resource;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.entity.po.Article;
import njgis.opengms.portal.entity.po.Version;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.enums.OperationEnum;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description
 * @Author bin
 * @Date 2021/09/06
 */
@Slf4j
@Service
public class VersionService {
    @Autowired
    ClassificationDao classificationDao;

    @Autowired
     ModelClassificationService modelClassificationService;
    @Autowired
    ArticleDao articleDao;

    @Autowired
    ModelItemService modelItemService;

    @Autowired
    SpatialReferenceDao spatialReferenceDao;

    @Autowired
    TemplateDao templateDao;

    @Autowired
    UnitDao unitDao;

    @Autowired
    ConceptDao conceptDao;

    @Autowired
    ComputableModelDao computableModelDao;

    @Autowired
    LogicalModelDao logicalModelDao;

    @Autowired
    DataItemDao dataItemDao;

    @Autowired
    ConceptualModelDao conceptualModelDao;

    @Value("${htmlLoadPath}")
    private String htmlLoadPath;

    @Autowired
    ModelItemDao modelItemDao;

    @Autowired
    VersionDao versionDao;

    @Autowired
    GenericService genericService;

    @Autowired
    NoticeService noticeService;

    @Autowired
    UserService userService;

    @Autowired
    DataItemService dataItemService;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    DataMethodService dataMethodService;

    /**
     * 添加审核版本
     * @param item 修改的条目数据
     * @param editor 修改者
     * @param originalItemName 原始条目的名字，用于生成版本名
     * @return njgis.opengms.portal.entity.po.Version
     * @Author bin
     **/
    public Version addVersion(PortalItem item, String editor, String originalItemName){
        Version version = new Version();

        // 如果editor和itemCreator相同的话直接审核通过，status设置为1
        if (editor.equals(item.getAuthor()))
            version.setStatus(1);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        version.setItemId(item.getId());
        version.setItemName(originalItemName);
        version.setVersionName(sdf.format(date) + "@" + originalItemName);
        version.setContent(item);
        version.setEditor(editor);
        version.setItemCreator(item.getAuthor());
        //有权审核版本的用户
        List<String> authReviewers = new ArrayList<>();
        authReviewers.add(item.getAuthor());
        authReviewers = noticeService.addItemAdmins(authReviewers,item.getAdmins());
        authReviewers = noticeService.addPortalAdmins(authReviewers);
        authReviewers = noticeService.addPortalRoot(authReviewers);
        version.setAuthReviewers(authReviewers);

        version.setSubmitTime(date);
        Class<? extends PortalItem> aClass = item.getClass();
        String name = aClass.getName();
        String[] nameArr = name.split("\\.");
        String type = nameArr[nameArr.length - 1];
        ItemTypeEnum itemType = ItemTypeEnum.getItemTypeByName(type);
        version.setType(itemType);

        PortalItem original = (PortalItem) ((GenericItemDao)genericService.daoFactory(itemType).get("itemDao")).findFirstById(item.getId());
        version.setOriginal(original);

        try {
            // version.setChangedField(getDifferenceBetweenTwoVersion(version.getContent(),itemType));
            version.setChangedField(getDifferenceBetweenTwoVersion(version.getContent(),version.getOriginal()));
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return versionDao.insert(version);

    }

    /**
     * 审核通过
     * @param versionId 审核版本id
     * @param reviewer 审核者
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult accept(String versionId, String reviewer){
        Version version = versionDao.findFirstById(versionId);
        Date date = new Date();
        version.setReviewer(reviewer);
        version.setReviewTime(date);
        version.setStatus(1);
        PortalItem content = version.getContent();
        content.setLock(false);
        //版本更新
        List<String> versions = content.getVersions();
        versions = versions == null ? new ArrayList<>() : versions;
        versions.add(version.getId());
        content.setVersions(versions);

        //贡献者更新
        List<String> contributors = content.getContributors();
        contributors = contributors == null ? new ArrayList<>() : contributors;
        if(!contributors.contains(version.getEditor())){
            contributors.add(version.getEditor());
        }
        content.setContributors(contributors);

        JSONObject factory = genericService.daoFactory(version.getType());
        GenericItemDao itemDao = (GenericItemDao) factory.get("itemDao");
        try {
            versionDao.save(version);
            itemDao.save(content);

            //给编辑者发邮件
            userService.sendAcceptMail(version.getEditor(),content);

            List<String> recipientList = new ArrayList<>(Arrays.asList(version.getItemCreator(),version.getEditor(),version.getReviewer()));
            // if (version.getEditor().equals(version.getReviewer())){
            //     recipientList = new ArrayList<>(Arrays.asList(version.getItemCreator(),version.getEditor()));
            // }
            //
            // else{
            //     recipientList = new ArrayList<>(Arrays.asList(version.getItemCreator(),version.getEditor(),version.getReviewer()));
            //     // recipientList = Arrays.asList(version.getItemCreator(),version.getEditor(),version.getReviewer());
            // }
            recipientList = noticeService.addItemAdmins(recipientList,content.getAdmins());
            recipientList = noticeService.addPortalAdmins(recipientList);
            recipientList = noticeService.addPortalRoot(recipientList);
            noticeService.sendNoticeContains(reviewer, OperationEnum.Accept,ItemTypeEnum.Version,version.getId(),recipientList);
        }catch (Exception e){
            return ResultUtils.error(e.getMessage());
        }
        return ResultUtils.success();
    }


    /**
     * 审核未通过
     * @param versionId 审核版本id
     * @param reviewer 审核者
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult reject(String versionId, String reviewer){
        Version version = versionDao.findFirstById(versionId);
        Date date = new Date();
        version.setReviewer(reviewer);
        version.setReviewTime(date);
        version.setStatus(-1);

        JSONObject factory = genericService.daoFactory(version.getType());
        GenericItemDao itemDao = (GenericItemDao) factory.get("itemDao");
        PortalItem content = (PortalItem) itemDao.findFirstById(version.getItemId());
        content.setLock(false);
        try {
            versionDao.save(version);
            itemDao.save(content);

            //给编辑者发邮件
            userService.sendRejectMail(version.getEditor(),content);

            List<String> recipientList = new ArrayList<>(Arrays.asList(version.getItemCreator(),version.getEditor(),version.getReviewer()));
            // if (version.getEditor().equals(version.getReviewer()))
            //     recipientList = new ArrayList<>(Arrays.asList(version.getItemCreator(),version.getEditor()));
            // else
            //     recipientList = new ArrayList<>(Arrays.asList(version.getItemCreator(),version.getEditor(),version.getReviewer()));
            recipientList = noticeService.addItemAdmins(recipientList,content.getAdmins());
            recipientList = noticeService.addPortalAdmins(recipientList);
            recipientList = noticeService.addPortalRoot(recipientList);
            noticeService.sendNoticeContains(reviewer, OperationEnum.Reject,ItemTypeEnum.Version,version.getId(),recipientList);
        }catch (Exception e){
            return ResultUtils.error(e.getMessage());
        }
        return ResultUtils.success();
    }

    public JsonResult fallback(String id, String email) {

        Version version = versionDao.findFirstById(id);
        if (version == null)
            return ResultUtils.error();

        PortalItem content = version.getContent();
        String originalName = content.getName();
        ItemTypeEnum type = version.getType();
        JSONObject factory = genericService.daoFactory(type);
        GenericItemDao itemDao =  (GenericItemDao) factory.get("itemDao");
        PortalItem item = (PortalItem) itemDao.findFirstById(version.getItemId());
        content.setVersions(item.getVersions());
        content.setLastModifyTime(new Date());

        // 根据content以及changedField得到original
        // // PortalItem 转 map
        // Map<String, Object> contentMap = BeanMapTool.beanToMap(content);
        //
        // ItemTypeEnum type = version.getType();
        // JSONObject factory = genericService.daoFactory(type);
        // Class<? extends PortalItem> clazz = (Class) factory.get("clazz");
        //
        // Map<String, Object> changedField = version.getChangedField();
        //
        // //遍历map
        // for (Map.Entry<String, Object> entry : changedField.entrySet()) {
        //     String mapKey = entry.getKey();
        //     HashMap mapValue = (HashMap)entry.getValue();
        //     System.out.println(mapKey + "：" + mapValue);
        //
        //     //不改变的字段
        //     if (mapKey.equals("versions"))
        //         continue;
        //
        //     if (mapKey.equals("lastModifyTime")){
        //         contentMap.put(mapKey, new Date());
        //     }
        //
        //
        //     contentMap.put(mapKey, mapValue.get("original"));
        //
        // }
        //
        // //map to bean
        // PortalItem newItem = null;
        // try {
        //     newItem = BeanMapTool.mapToBean(contentMap, clazz);
        // } catch (IllegalAccessException | InstantiationException e) {
        //     e.printStackTrace();
        //     ResultUtils.error();
        // }



        return ResultUtils.success(addVersion(content, email,originalName));

    }


    /**
     * 得到审核版本方法的公共代码块
     * @param all 版本列表
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult getVersionsCommonPart(List<Version> all){
        JSONObject result = new JSONObject();
        List<Version> uncheck = new ArrayList<>();
        List<Version> accept = new ArrayList<>();
        List<Version> reject = new ArrayList<>();
        // List<Version> edit = new ArrayList<>();
        for (Version version : all) {
            switch (version.getStatus()){
                case 0:{
                    uncheck.add(version);
                    break;
                }
                case 1:{
                    accept.add(version);
                }
                case -1:{
                    reject.add(version);
                }
            }
        }
        result.put("all",all);
        result.put("uncheck",uncheck);
        result.put("accept",accept);
        result.put("reject",reject);

        return ResultUtils.success(result);
    }


    /**
     * 得到审核版本
     * @param findDTO
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult getVersions(FindDTO findDTO){
        try {
            List<Version> all;
            if (findDTO == null){
                all = versionDao.findAll();
            }
            else {
                Pageable pageable = genericService.getPageable(findDTO);
                all = versionDao.findAll(pageable).getContent();
            }
            return getVersionsCommonPart(all);
        }catch (Exception e){
            return ResultUtils.error(e.getMessage());
        }
    }

    /**
     * 根据状态得到审核版本
     * @param findDTO
     * @param status
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult getVersionByConcreteStatus(FindDTO findDTO, int status) {
        try {
            int count;
            List<Version> versionList;
            if (findDTO == null){
                versionList = versionDao.findAllByStatus(status);
                count = versionList.size();

                // return ResultUtils.success(versionDao.findAllByStatus(status));
            }
            else {
                Pageable pageable = genericService.getPageable(findDTO);
                Page<Version> allByStatus = versionDao.findAllByStatus(status, pageable);
                versionList = allByStatus.getContent();
                count = (int) allByStatus.getTotalElements();
                // return ResultUtils.success(versionDao.findAllByStatus(status,pageable));
            }

            List<Version> versions = new ArrayList<>();
            for (Version version : versionList) {
                Version newV = new Version();
                BeanUtils.copyProperties(version, newV, "content","changedField");
                versions.add(newV);
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content",versions);
            jsonObject.put("count",count);
            return ResultUtils.success(jsonObject);

        }catch (Exception e){
            return ResultUtils.error(e.getMessage());
        }
    }

    public JsonResult getVersionByConcreteStatus(FindDTO findDTO, int status,ItemTypeEnum type) {
        try {
            int count;
            List<Version> versionList;
            if (findDTO == null){
                versionList = versionDao.findAllByStatusAndType(status,type);
                count = versionList.size();

                // return ResultUtils.success(versionDao.findAllByStatus(status));
            }
            else if (type == ItemTypeEnum.All){
                Pageable pageable = genericService.getPageable(findDTO);
                Page<Version> allByStatus = versionDao.findAllByStatus(status, pageable);
                versionList = allByStatus.getContent();
                count = (int) allByStatus.getTotalElements();
            }
            else {
                Pageable pageable = genericService.getPageable(findDTO);
                Page<Version> allByStatus = versionDao.findAllByStatusAndType(status, type, pageable);
                versionList = allByStatus.getContent();
                count = (int) allByStatus.getTotalElements();
                // return ResultUtils.success(versionDao.findAllByStatus(status,pageable));
            }

            List<Version> versions = new ArrayList<>();
            for (Version version : versionList) {
                Version newV = new Version();
                BeanUtils.copyProperties(version, newV, "content","changedField");
                versions.add(newV);
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content",versions);
            jsonObject.put("count",count);
            return ResultUtils.success(jsonObject);

        }catch (Exception e){
            return ResultUtils.error(e.getMessage());
        }
    }


    /**
     * 得到用户已提交审核版本的数据
     * @param email
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult getUserEditVersion(String email) {

        try {
            return ResultUtils.success(versionDao.findAllByEditor(email));
        }catch (Exception e){
            return ResultUtils.error(e.getMessage());
        }

    }

    /**
     * 得到用户待审核版本的数据
     * @param email
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult getUserReviewVersion(String email) {
        try {
            if (email.equals("opengms@njnu.edu.cn")){
                return ResultUtils.success(versionDao.findAll());
            }
            return ResultUtils.success(versionDao.findAllByItemCreator(email));
        }catch (Exception e){
            return ResultUtils.error(e.getMessage());
        }

    }

    /**
     * 根据版本状态、条目类型和操作类型得到版本
     * @param status 版本状态: 0:待审核 -1:被拒绝 1:通过 2:原始版本（或忽略）
     * @param type  条目大类: Model/Data/Community/Theme
     * @param findDTO
     * @param email
     * @param op 操作类型: edit(提交的版本,你是条目的修改者) / review(待审核的版本,你是条目的创建者)
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult getUserVersionByStatusAndByTypeAndByOperation(int status, String type, FindDTO findDTO, String email, String op) {
        Pageable pageable = genericService.getPageable(findDTO);
        List<ItemTypeEnum> findType = new ArrayList<>();
        switch (type){
            case "Model":{
                findType.add(ItemTypeEnum.ModelItem);
                findType.add(ItemTypeEnum.ConceptualModel);
                findType.add(ItemTypeEnum.LogicalModel);
                findType.add(ItemTypeEnum.ComputableModel);
                break;
            }
            case "Data":{
                findType.add(ItemTypeEnum.DataItem);
                findType.add(ItemTypeEnum.DataHub);
                findType.add(ItemTypeEnum.DataMethod);
                break;
            }
            case "Community":{
                findType.add(ItemTypeEnum.Concept);
                findType.add(ItemTypeEnum.SpatialReference);
                findType.add(ItemTypeEnum.Template);
                findType.add(ItemTypeEnum.Unit);
                break;
            }
            case "Theme":{
                findType.add(ItemTypeEnum.Theme);
                break;
            }
            case "All":
            default:{
                findType.add(ItemTypeEnum.ModelItem);
                findType.add(ItemTypeEnum.ConceptualModel);
                findType.add(ItemTypeEnum.LogicalModel);
                findType.add(ItemTypeEnum.ComputableModel);

                findType.add(ItemTypeEnum.DataItem);
                findType.add(ItemTypeEnum.DataHub);
                findType.add(ItemTypeEnum.DataMethod);

                findType.add(ItemTypeEnum.Concept);
                findType.add(ItemTypeEnum.SpatialReference);
                findType.add(ItemTypeEnum.Template);
                findType.add(ItemTypeEnum.Unit);

                findType.add(ItemTypeEnum.Theme);
                // return ResultUtils.error("invalid item type");
            }
        }

        try {
            if (op.equals("edit")){
                return ResultUtils.success(versionDao.findAllByStatusAndEditorAndTypeIn(status,email,findType,pageable));
            }
            else if (op.equals("review")) {
                // 如果登录用户是门户的话那可以得到所有对应状态的版本
                // if (email.equals("opengms@njnu.edu.cn")){
                //     return ResultUtils.success(versionDao.findAllByStatusAndTypeIn(status,findType,pageable));
                // }
                // return ResultUtils.success(versionDao.findAllByStatusAndItemCreatorAndTypeIn(status,email,findType,pageable));
                return ResultUtils.success(versionDao.findAllByStatusAndAuthReviewersInAndTypeIn(status,email,findType,pageable));
            }
        }catch (Exception e){
            return ResultUtils.error(e.getMessage());
        }

        return ResultUtils.error("invalid operation");

    }


    /**
     * 比较两个版本的不同
     * @param editItem 编辑后的item数据
     * @param originalItem 原始条目数据
     * @return void
     * @Author bin
     **/
    public Map<String, Object> getDifferenceBetweenTwoVersion(PortalItem editItem, PortalItem originalItem) throws IllegalAccessException {
        // JSONObject factory = genericService.daoFactory(itemType);
        // GenericItemDao itemDao = (GenericItemDao) factory.get("itemDao");
        // PortalItem originalItem = (PortalItem) itemDao.findFirstById(editItem.getId());
        return genericService.getDifferenceBetweenTwoObject(originalItem,editItem);
    }


    /**
     * 得到审核版本的详细信息
     * @param id 版本id
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult getVersionDetail(String id) {

        try {
            Version version = versionDao.findFirstById(id);
            // JSONObject factory = genericService.daoFactory(version.getType());
            // GenericItemDao itemDao = (GenericItemDao)factory.get("itemDao");
            // PortalItem original = (PortalItem)itemDao.findFirstById(version.getItemId());
            // VersionDTO versionDTO = new VersionDTO();
            // BeanUtils.copyProperties(version,versionDTO);
            // versionDTO.setOriginal(original);

            JSONObject result = new JSONObject();
            result = (JSONObject) JSONObject.toJSON(version);
            JSONObject originalField = new JSONObject();
            JSONObject newField = new JSONObject();
            Map<String, Object> changedField = version.getChangedField();
            for (Map.Entry<String, Object> entry : changedField.entrySet()) {
                String mapKey = entry.getKey();
                Object mapValue = entry.getValue();
                JSONObject props = JSONObject.parseObject(JSONObject.toJSONString(mapValue));
                originalField.put(mapKey, props.get("original"));
                newField.put(mapKey, props.get("new"));
            }
            result.put("originalField", originalField);
            result.put("newField", newField);

            return ResultUtils.success(result);
        } catch (Exception e){
            return ResultUtils.error();
        }
    }


    public PortalItem getOriginalItemInfo(String id){

        Version version = versionDao.findFirstById(id);
        String itemId = version.getItemId();
        ItemTypeEnum type = version.getType();

        JSONObject jsonObject = genericService.daoFactory(type);
        GenericItemDao dao = (GenericItemDao) jsonObject.get("itemDao");
        PortalItem item = (PortalItem) dao.findFirstById(itemId);
        return item;
    }

    public JSONArray getReferences(JSONArray reference_ids){
        JSONArray references = new JSONArray();
        for(int i=0;i<reference_ids.size();i++) {
            Article article = articleDao.findFirstById((String) reference_ids.get(i));
            references.add(JSONArray.toJSONString(article));
//            System.out.println(references.get(i));
        }
        return references;
    }

    public JSONObject getLatModifier(String email){
        JSONObject modifierJson = userService.getItemUserInfoByEmail(email);
        return modifierJson;
    }

    public ModelAndView getPage(String version_id){


        Version version = versionDao.findFirstById(version_id);

        JSONObject originalField = new JSONObject();
        JSONObject newField = new JSONObject();

        //把item所有属性都先赋值成null，不然模板引擎渲染不出来
        PortalItem content = version.getContent();
        //通过getDeclaredFields()方法获取对象类中的所有属性（含私有）
        //getDeclaredFields()方法用来获取类中所有声明的字段，包括public、private和proteced
        //但是不包括父类的申明字段，所以用getDeclaredFields获取不到当前对象的属性
        //如果想要获取从父类继承的字段，可使用getFields()方法，但是此方法仅能获取公共（public）的字段
        List<Field> fieldsList = new ArrayList<>();  // 保存属性对象数组到列表
        Class clazz = content.getClass();
        while (clazz != null) { // 遍历所有父类字节码对象
            Field[] declaredFields = clazz.getDeclaredFields();  // 获取字节码对象的属性对象数组
            fieldsList.addAll(new ArrayList<>(Arrays.asList(declaredFields)));  //将`Filed[]`数组转换为`List<>`然后再将其拼接至`ArrayList`上
            clazz = clazz.getSuperclass();  // 获得父类的字节码对象
        }
        try {
            for (Field field : fieldsList) {
                //设置允许通过反射访问私有变量
                field.setAccessible(true);
                //获取字段的值
                // String value = field.get(content).toString();
                //获取字段属性名称
                // String name = field.getName();
                //其他自定义操作
                originalField.put(field.getName(), null);
                newField.put(field.getName(), null);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }


        Map<String, Object> changedField = version.getChangedField();
        for (Map.Entry<String, Object> entry : changedField.entrySet()) {
            String mapKey = entry.getKey();
            Object mapValue = entry.getValue();
            JSONObject props = JSONObject.parseObject(JSONObject.toJSONString(mapValue));
            originalField.put(mapKey, props.get("original"));
            originalField.put("name", version.getContent().getName());
            originalField.put("status", null);
            newField.put(mapKey, props.get("new"));
            newField.put("name", version.getContent().getName());
            newField.put("status", null);
        }


        //********************** 通用属性统一 **************************

        if(originalField.get("image")==null)
            originalField.put("image", null);
        else
            originalField.put("image", htmlLoadPath + originalField.get("image"));
        if(newField.get("image")==null)
            newField.put("image", null);
        else
            newField.put("image", htmlLoadPath + originalField.get("image"));


        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");

        if(originalField.get("lastModifier")!=null){
            originalField.put("lastModifier", getLatModifier(originalField.get("lastModifier").toString()));
            originalField.put("lastModifyTime", simpleDateFormat.format(originalField.get("lastModifyTime")));
        } else {
            originalField.put("lastModifier", null);
            originalField.put("lastModifyTime", null);
        }

        if(newField.get("lastModifier")!=null ){
            newField.put("lastModifier", getLatModifier(newField.get("lastModifier").toString()));
            newField.put("lastModifyTime", simpleDateFormat.format(newField.get("lastModifyTime")));
        } else {
            newField.put("lastModifier", null);
            newField.put("lastModifyTime", null);
        }

        if(originalField.get("references")!=null){
            JSONArray  ref_original = getReferences(JSONArray.parseArray(originalField.get("references").toString()));
            // originalField.remove("references");
            originalField.put("references", ref_original);

            JSONArray  ref_new = getReferences(JSONArray.parseArray(newField.get("references").toString()));
            // newField.remove("references");
            newField.put("references", ref_new);
        }


        //********************** 非通用属性单独处理 **************************
        ItemTypeEnum type = version.getType();
        switch (type){
            case ModelItem:{
                getModelItemChanged(originalField, newField);
                break;
            }
            case DataItem:
            case DataHub:{
                getDataItemChanged(originalField, newField);
                break;
            }
            case DataMethod:{
                getDataMethodChanged(originalField, newField);
                break;
            }
            case Concept:{
                getRepositoryChanged(originalField, newField, ItemTypeEnum.Concept);
                break;
            }
            case SpatialReference:{
                getRepositoryChanged(originalField, newField, ItemTypeEnum.SpatialReference);
                break;
            }
            case Template:{
                getRepositoryChanged(originalField, newField, ItemTypeEnum.Template);
                break;
            }
            case Unit:{
                getRepositoryChanged(originalField, newField, ItemTypeEnum.Unit);
                break;
            }
            case Theme:{
                getThemeChanged(originalField, newField);
                break;
            }
            default:{
                break;
            }

        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("version/version_compare");
        modelAndView.addObject("itemInfo", JSONObject.toJSON(version.getContent()));
        modelAndView.addObject("originalField", JSONObject.toJSON(originalField));
        modelAndView.addObject("newField", JSONObject.toJSON(newField));
        modelAndView.addObject("modularType", version.getType());


        return modelAndView;

    }

    private void getThemeChanged(JSONObject originalField, JSONObject newField) {

    }

    private void getRepositoryChanged(JSONObject originalField, JSONObject newField, ItemTypeEnum type) {
        //classifications
        //得到每个分类对应的分类dao
        JSONObject daoFactory = genericService.daoFactory(type);
        GenericCategoryDao classificationDao = (GenericCategoryDao)daoFactory.get("classificationDao");
        if(originalField.get("classifications") != null){
            List<String> originalCls = (List<String>)originalField.get("classifications");
            JSONArray oriName = repositoryService.getClassification(originalCls, classificationDao);
            // originalField.remove("classifications");
            originalField.put("classifications", oriName);
        }
        if (newField.get("classifications")!=null){
            List<String> newCls = (List<String>)newField.get("classifications");
            JSONArray newName = repositoryService.getClassification(newCls, classificationDao);
            // newField.remove("classifications");
            newField.put("classifications", newName);
        }


        if (type == ItemTypeEnum.Concept){
            if(originalField.get("related") != null){
                // List<String> related = concept.getRelated();
                List<String> originalRelated = (List<String>)originalField.get("related");
                JSONArray originalRelateArray = repositoryService.getRelateArray(originalRelated);
                // originalField.remove("related");
                originalField.put("related", originalRelateArray);
            }
            if (newField.get("related")!=null){
                List<String> newClsRelated = (List<String>)newField.get("related");
                JSONArray newRelateArray = repositoryService.getRelateArray(newClsRelated);
                // newField.remove("related");
                newField.put("related", newRelateArray);
            }
        }

        if (type == ItemTypeEnum.SpatialReference){
            if(originalField.get("xml") != null){
                originalField.put("localizations",
                    repositoryService
                        .getLocalizationArray((String) originalField.get("xml")));
            }
            if (newField.get("xml") != null){
                newField.put("localizations",
                    repositoryService
                        .getLocalizationArray((String) newField.get("xml")));
            }

            if(originalField.get("classifications") != null){
                originalField.put("isTemporal",
                    repositoryService
                        .getFlag((List<String>) originalField.get("classifications")));
            }
            if (newField.get("classifications") != null){
                newField.put("isTemporal",
                    repositoryService
                        .getFlag((List<String>) newField.get("classifications")));
            }

        }

        if (type == ItemTypeEnum.Unit){
            if(originalField.get("xml") != null){
                originalField.put("localizations",
                    repositoryService
                        .getLocalizationArray((String) originalField.get("xml")));
            }
            if (newField.get("xml") != null){
                newField.put("localizations",
                    repositoryService
                        .getLocalizationArray((String) newField.get("xml")));
            }

            if(originalField.get("xml") != null){
                originalField.put("oid_cvt", (String) originalField.get("conversionId"));
            }
            if (newField.get("xml") != null){
                newField.put("oid_cvt", (String) newField.get("conversionId"));
            }

        }

    }

    private void getDataMethodChanged(JSONObject originalField, JSONObject newField) {

        if(originalField.get("classifications") != null){
            // originalField.remove("classifications");
            originalField.put("classifications",
                dataMethodService
                    .getClassifications((List<String>) originalField.get("classifications")));
        }
        if (newField.get("classifications") != null){
            // newField.remove("classifications");
            newField.put("classifications",
                dataMethodService
                    .getClassifications((List<String>) newField.get("classifications")));
        }

        if(originalField.get("resources") != null){
            // originalField.remove("resources");
            originalField.put("resources",
                dataMethodService
                    .getResourceArray((List<Resource>) originalField.get("resources"), (Boolean) originalField.get("batch")));
        }
        if (newField.get("resources") != null){
            // newField.remove("resources");
            newField.put("resources",
                dataMethodService
                    .getResourceArray((List<Resource>) newField.get("resources"), (Boolean) originalField.get("batch")));
        }

    }

    private void getDataItemChanged(JSONObject originalField, JSONObject newField) {

        if(originalField.get("classifications") != null){
            // originalField.remove("classifications");
            originalField.put("classifications",
                dataItemService
                    .getClassifications((List<String>) originalField.get("classifications")));
        }
        if (newField.get("classifications") != null){
            // newField.remove("classifications");
            newField.put("classifications",
                dataItemService
                    .getClassifications((List<String>) newField.get("classifications")));
        }

        if(originalField.get("relatedModels") != null){
            // originalField.remove("relatedModels");
            originalField.put("relatedModels",
                dataItemService
                    .getModelItemArray((List<String>) originalField.get("relatedModels")));
        }
        if (newField.get("relatedModels") != null){
            // newField.remove("relatedModels");
            newField.put("relatedModels",
                dataItemService
                    .getModelItemArray((List<String>) newField.get("relatedModels")));
        }

        if(originalField.get("authorships") != null){
            // originalField.remove("authorships");
            originalField.put("authorships",
                dataItemService
                    .getAuthorshipString((List<AuthorInfo>) originalField.get("authorships")));
        }
        if (newField.get("authorships") != null){
            // newField.remove("authorships");
            newField.put("authorships",
                dataItemService
                    .getAuthorshipString((List<AuthorInfo>) newField.get("authorships")));
        }

        if(originalField.get("localizationList") != null){
            // originalField.remove("detailLanguage");
            originalField.put("detailLanguage",
                dataItemService
                    .getLocalizationList((List<Localization>) originalField.get("localizationList"))
                    .getString("detailLanguage"));
        }
        if (newField.get("localizationList") != null){
            // newField.remove("detailLanguage");
            newField.put("detailLanguage",
                dataItemService
                    .getLocalizationList((List<Localization>) newField.get("localizationList"))
                    .getString("detailLanguage"));
        }

        if(originalField.get("localizationList") != null){
            // originalField.remove("detail");
            originalField.put("detail",
                dataItemService
                    .getLocalizationList((List<Localization>) originalField.get("localizationList"))
                    .getString("detailResult"));
        }
        if (newField.get("localizationList") != null){
            // newField.remove("detail");
            newField.put("detail",
                dataItemService
                    .getLocalizationList((List<Localization>) newField.get("localizationList"))
                    .getString("detailResult"));
        }

        if(originalField.get("localizationList") != null){
            // originalField.remove("languageList");
            originalField.put("languageList",
                dataItemService
                    .getLanguageList((List<Localization>) originalField.get("localizationList")));
        }
        if (newField.get("localizationList") != null){
            // newField.remove("languageList");
            newField.put("languageList",
                dataItemService
                    .getLanguageList((List<Localization>) newField.get("localizationList")));
        }


    }

    private void getModelItemChanged(JSONObject originalField, JSONObject newField) {

        if(originalField.get("classifications") != null){
            JSONArray classResult_old = modelClassificationService.getClassifications((List<String>) originalField.get("classifications"));
            // originalField.remove("classifications");
            originalField.put("classifications", classResult_old);
        }
        if(newField.get("classifications") != null){
            JSONArray classResult_new = modelClassificationService.getClassifications((List<String>) newField.get("classifications"));
            // newField.remove("classifications");
            newField.put("classifications", classResult_new);
        }



        if(originalField.get("relate") != null && newField.get("relate") != null){
            //relate
            ModelItemRelate modelItemRelate_original= JSON.parseObject(JSON.toJSONString(originalField.get("relate")), ModelItemRelate.class);
            JSONObject relate_original = modelItemService.getRelationJson(modelItemRelate_original);
            // originalField.remove("relate");
            originalField.put("relate", relate_original);
        }
        if (newField.get("relate") != null){
            ModelItemRelate modelItemRelate_new= JSON.parseObject(JSON.toJSONString(newField.get("relate")), ModelItemRelate.class);
            JSONObject relate_new = modelItemService.getRelationJson(modelItemRelate_new);
            // newField.remove("relate");
            newField.put("relate", relate_new);
        }


        if(originalField.get("keywords") != null){
            List<String> originalKeywords = (List<String>)originalField.get("keywords");
            String o = modelItemService.getKeywords(originalKeywords);
            // originalField.remove("metaKeywords");
            originalField.put("metaKeywords", o);
        }
        if (newField.get("keywords") != null){
            List<String> newKeywords = (List<String>)newField.get("keywords");
            String n = modelItemService.getKeywords(newKeywords);
            // newField.remove("metaKeywords");
            newField.put("metaKeywords", n);
        }


        if(originalField.get("localizationList") != null){
            // originalField.remove("detailLanguage");
            originalField.put("detailLanguage",
                modelItemService
                    .getLocalizationList((List<Localization>) originalField.get("localizationList"))
                    .getString("detailLanguage"));
        }
        if (newField.get("localizationList") != null){
            // newField.remove("detailLanguage");
            newField.put("detailLanguage",
                modelItemService
                    .getLocalizationList((List<Localization>) newField.get("localizationList"))
                    .getString("detailLanguage"));
        }

        if(originalField.get("localizationList") != null){
            // originalField.remove("detail");
            originalField.put("detail",
                modelItemService
                    .getLocalizationList((List<Localization>) originalField.get("localizationList"))
                    .getString("detailResult"));
        }
        if (newField.get("localizationList") != null){
            // newField.remove("detail");
            newField.put("detail",
                modelItemService
                    .getLocalizationList((List<Localization>) newField.get("localizationList"))
                    .getString("detailResult"));
        }

        if(originalField.get("localizationList") != null){
            // originalField.remove("languageList");
            originalField.put("languageList",
                modelItemService
                    .getLanguageList((List<Localization>) originalField.get("localizationList")));
        }
        if (newField.get("localizationList") != null){
            // newField.remove("languageList");
            newField.put("languageList",
                modelItemService
                    .getLanguageList((List<Localization>) newField.get("localizationList")));
        }


        if(originalField.get("authorships") != null){
            // originalField.remove("authorship");
            originalField.put("authorship",
                modelItemService
                    .getAuthorshipString((List<AuthorInfo>) originalField.get("authorships")));
        }
        if (newField.get("authorships") != null){
            // newField.remove("authorship");
            newField.put("authorship",
                modelItemService
                    .getAuthorshipString((List<AuthorInfo>) newField.get("authorships")));
        }


    }


}
