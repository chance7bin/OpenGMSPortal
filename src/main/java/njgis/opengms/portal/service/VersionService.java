package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.component.ServiceException;
import njgis.opengms.portal.constant.HttpStatus;
import njgis.opengms.portal.dao.*;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.Localization;
import njgis.opengms.portal.entity.doo.base.PortalItem;
import njgis.opengms.portal.entity.doo.model.ModelItemRelate;
import njgis.opengms.portal.entity.doo.model.ModelRelation;
import njgis.opengms.portal.entity.doo.model.Resource;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.entity.po.*;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.enums.OperationEnum;
import njgis.opengms.portal.utils.ResultUtils;
import njgis.opengms.portal.utils.Utils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

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
    RepositoryService repositoryService;

    @Autowired
    DataItemService dataItemService;

    @Autowired
    UserDao userDao;

    @Autowired
    LogicalModelService logicalModelService;

    @Autowired
    ConceptualModelService conceptualModelService;

    @Autowired
    ComputableModelService computableModelService;

    @Autowired
    DataHubService dataHubService;

    @Autowired
    DataMethodService dataMethodService;


    @Autowired
    DataHubDao dataHubDao;

    @Autowired
    RedisService redisService;

    /**
     * 添加审核版本
     * @param item 修改的条目数据
     * @param editor 修改者
     * @param originalItemName 原始条目的名字，用于生成版本名
     * @return {@link Version}
     * @author 7bin
     **/
    public Version addVersion(PortalItem item, String editor, String originalItemName){
        return addVersion(item, editor, originalItemName, true);
    }

    /**
     * 添加审核版本
     * @param item 修改的条目数据
     * @param editor 修改者
     * @param originalItemName 原始条目的名字，用于生成版本名
     * @param allowNoDiff 是否允许没有修改的条目添加到版本中
     * @return njgis.opengms.portal.entity.po.Version
     * @Author bin
     **/
    public Version addVersion(PortalItem item, String editor, String originalItemName, boolean allowNoDiff){
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

        GenericItemDao itemDao = (GenericItemDao)genericService.daoFactory(itemType).get("itemDao");

        PortalItem original = (PortalItem) itemDao.findFirstById(item.getId());
        version.setOriginal(original);

        try {
            // version.setChangedField(getDifferenceBetweenTwoVersion(version.getContent(),itemType));
            version.setChangedField(getDifferenceBetweenTwoVersion(version.getContent(),version.getOriginal(), allowNoDiff));
        } catch (ServiceException se){
            // 没有字段修改会在 getDifferenceBetweenTwoVersion 抛 ServiceException 异常

            // 解锁
            original.setLock(false);
            genericService.saveItem(original, itemDao);
            throw se;
        } catch (Exception e){
            // e.printStackTrace();
            log.error(e.getMessage());
        }
        // return null;
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

        //模型条目需要更新关联信息
        if (version.getType() == ItemTypeEnum.ModelItem){
            // 因为不知道更改的是哪个条目的关联，所以要先判断下
            updateModelItemRelation(version);
        }

        //数据条目需要更新关联信息
        if (version.getType() == ItemTypeEnum.DataItem || version.getType() == ItemTypeEnum.DataHub){

            List<String> newRelations = ((DataItem)version.getContent()).getRelatedModels();
            List<String> oriRelations = ((DataItem)version.getOriginal()).getRelatedModels();

            dataItemService.updateModelRelate(newRelations, oriRelations,version.getType(),version.getItemId());
        }


        JSONObject factory = genericService.daoFactory(version.getType());
        GenericItemDao itemDao = (GenericItemDao) factory.get("itemDao");
        try {
            versionDao.save(version);
            // itemDao.save(content);
            redisService.saveItem(content,version.getType());

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
            noticeService.sendNoticeContains(reviewer, OperationEnum.Accept,ItemTypeEnum.Version,version,recipientList);
        }catch (Exception e){
            return ResultUtils.error(e.getMessage());
        }
        return ResultUtils.success();
    }

    private void updateModelItemRelation(Version version){
        ModelItem oriVersion = (ModelItem)version.getOriginal();
        ModelItem newVersion = (ModelItem)version.getContent();
        ModelItemRelate oriRelate = oriVersion.getRelate();
        ModelItemRelate newRelate = newVersion.getRelate();
        String updateRelateItemType = null;
        List<String> updateRelations = null;

        if (!Utils.equalLists(oriRelate.getDataItems(), newRelate.getDataItems())){
            updateRelateItemType = ItemTypeEnum.DataItem.getText();
            updateRelations = newRelate.getDataItems();
        }
        if (!Utils.equalLists(oriRelate.getComputableModels(), newRelate.getComputableModels())){
            updateRelateItemType = ItemTypeEnum.ComputableModel.getText();
            updateRelations = newRelate.getComputableModels();
        }
        if (!Utils.equalLists(oriRelate.getConceptualModels(), newRelate.getConceptualModels())){
            updateRelateItemType = ItemTypeEnum.ConceptualModel.getText();
            updateRelations = newRelate.getConceptualModels();
        }
        if (!Utils.equalLists(oriRelate.getLogicalModels(), newRelate.getLogicalModels())){
            updateRelateItemType = ItemTypeEnum.LogicalModel.getText();
            updateRelations = newRelate.getLogicalModels();
        }

        if (updateRelations != null && updateRelateItemType != null){
            modelItemService.updateModelItemRelation(updateRelations, updateRelateItemType, oriVersion);
        }


        //modelItem条目的关联单独处理
        if (!Utils.equalLists(oriRelate.getModelRelationList(), newRelate.getModelRelationList())){
            modelItemService.updateModelItem2ModelItem(version.getItemId(),
                oriRelate.getModelRelationList(), newRelate.getModelRelationList());
        }



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
            // itemDao.save(content);
            redisService.saveItem(content,version.getType());

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
            noticeService.sendNoticeContains(reviewer, OperationEnum.Reject,ItemTypeEnum.Version,version,recipientList);
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
                Page<Version> allByStatus = versionDao.findAllByItemNameContainsIgnoreCaseAndStatus(findDTO.getSearchText(),status, pageable);
                versionList = allByStatus.getContent();
                count = (int) allByStatus.getTotalElements();
            }
            else {
                Pageable pageable = genericService.getPageable(findDTO);
                // Page<Version> allByStatus = versionDao.findAllByStatusAndType(status, type, pageable);
                Page<Version> allByStatus = versionDao.findAllByItemNameContainsIgnoreCaseAndStatusAndType(findDTO.getSearchText(), status, type, pageable);
                versionList = allByStatus.getContent();
                count = (int) allByStatus.getTotalElements();
                // return ResultUtils.success(versionDao.findAllByStatus(status,pageable));
            }

            List<JSONObject> versions = new ArrayList<>();
            for (Version version : versionList) {
                Version newV = new Version();
                BeanUtils.copyProperties(version, newV, "content","original","changedField");
                JSONObject o = (JSONObject)JSONObject.toJSON(newV);
                o.put("editor",userService.getUserName(newV.getEditor()));
                o.put("editorEmail",newV.getEditor());
                versions.add(o);
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
                Page<Version> versionPage = versionDao.findAllByStatusAndEditorAndTypeIn(status, email, findType, pageable);
                return ResultUtils.success(versionInfoPageable2JSONObject(versionPage));
            }
            else if (op.equals("review")) {
                // 如果登录用户是门户的话那可以得到所有对应状态的版本
                // if (email.equals("opengms@njnu.edu.cn")){
                //     return ResultUtils.success(versionDao.findAllByStatusAndTypeIn(status,findType,pageable));
                // }
                // return ResultUtils.success(versionDao.findAllByStatusAndItemCreatorAndTypeIn(status,email,findType,pageable));
                Page<Version> versionPage = versionDao.findAllByStatusAndAuthReviewersInAndTypeIn(status, email, findType, pageable);
                return ResultUtils.success(versionInfoPageable2JSONObject(versionPage));
            }


        }catch (Exception e){
            return ResultUtils.error(e.getMessage());
        }

        return ResultUtils.error("invalid operation");

    }

    //版本返回的信息从分页类型转换成json对象，并增加前端需要的属性
    private JSONObject versionInfoPageable2JSONObject(Page<Version> versions){

        JSONObject result = new JSONObject();
        List<JSONObject> content = new ArrayList<>();

        for (Version version : versions) {
            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(version));
            jsonObject.put("relateItemId",version.getItemId());
            jsonObject.put("relateItemName",version.getItemName());
            jsonObject.put("relateItemType",version.getType().getText());

            //email -> name
            String editor = version.getEditor();
            User user = userDao.findFirstByEmail(editor);
            if (user != null){
                jsonObject.put("editor",user.getName());
                jsonObject.put("editorAccessId",user.getAccessId());
            }

            content.add(jsonObject);
        }

        result.put("content",content);
        //分页总数
        result.put("totalElements",versions.getTotalElements());

        return result;
    }


    /**
     * 比较两个版本的不同
     * @param editItem 编辑后的item数据
     * @param originalItem 原始条目数据
     * @param allowNoDiff 是否允许没有修改的条目添加到版本中
     * @return void
     * @Author bin
     **/
    public Map<String, Object> getDifferenceBetweenTwoVersion(PortalItem editItem, PortalItem originalItem, boolean allowNoDiff) throws IllegalAccessException {
        // JSONObject factory = genericService.daoFactory(itemType);
        // GenericItemDao itemDao = (GenericItemDao) factory.get("itemDao");
        // PortalItem originalItem = (PortalItem) itemDao.findFirstById(editItem.getId());
        Map<String, Object> differences = genericService.getDifferenceBetweenTwoObject(originalItem, editItem,
            "lastModifyTime", "lastModifier", "versions");
        if(!allowNoDiff && differences.size() == 0){
            // 错误状态码 415 表示不支持的数据输入 : 未有字段修改
            throw new ServiceException("无字段修改，不可提交审核", HttpStatus.UNSUPPORTED_TYPE);
        }
        return differences;
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

    public JSONObject modelGetRelate(ModelItemRelate modelItemRelate){
        List<ModelRelation> modelItems = modelItemRelate.getModelRelationList();
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
        List<String> dataItems=modelItemRelate.getDataItems();
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
        return relationJson;

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
        Map<String, Object> changedField = version.getChangedField();
        for (Map.Entry<String, Object> entry : changedField.entrySet()) {
            String mapKey = entry.getKey();
            Object mapValue = entry.getValue();
            JSONObject props = JSONObject.parseObject(JSONObject.toJSONString(mapValue));
            originalField.put(mapKey, props.get("original"));
            newField.put(mapKey, props.get("new"));
        }


        //********************** 通用属性统一 **************************
        if(originalField.get("name")==null)
            originalField.put("name", version.getContent().getName());
        if(newField.get("name")==null)
            newField.put("name", version.getContent().getName());

        if(originalField.get("image")==null)
            originalField.put("image", null);
        if(newField.get("image")==null)
            newField.put("image", null);

        if(originalField.get("status")==null)
            originalField.put("status", null);
        if(newField.get("status")==null)
            newField.put("status", null);

        if(originalField.get("overview")==null)
            originalField.put("overview", null);
        if(newField.get("overview")==null)
            newField.put("overview", null);

        if(originalField.get("keywords")==null)
            originalField.put("keywords", null);
        if(newField.get("keywords")==null)
            newField.put("keywords", null);


        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");

        if(originalField.get("lastModifier")!=null){
            originalField.put("lastModifier", getLatModifier(originalField.get("lastModifier").toString()));
        }
        else{
            originalField.put("lastModifier",null);
        }

        if(originalField.get("lastModifyTime")!=null){
            originalField.put("lastModifyTime", simpleDateFormat.format(originalField.get("lastModifyTime")));
        }
        else{
            originalField.put("lastModifyTime",null);
        }



        if(newField.get("lastModifier")!=null ){
            newField.put("lastModifier", getLatModifier(newField.get("lastModifier").toString()));
        }
        else{
            newField.put("lastModifier",null);
        }

        if(newField.get("lastModifyTime")!=null){
            newField.put("lastModifyTime", simpleDateFormat.format(newField.get("lastModifyTime")));
        }
        else{
            newField.put("lastModifyTime",null);
        }


        if(originalField.get("references")!=null){
            JSONArray  ref_original = getReferences(JSONArray.parseArray(originalField.get("references").toString()));
            originalField.remove("references");
            originalField.put("references", ref_original);

            JSONArray  ref_new = getReferences(JSONArray.parseArray(newField.get("references").toString()));
            newField.remove("references");
            newField.put("references", ref_new);
        }

        //资源信息
        if(originalField.get("resources")!=null){

            JSONArray resourceArray = new JSONArray();
            List<Resource> resources = ((JSONArray)originalField.get("resources")).toJavaList(Resource.class);

            if (resources != null) {
                for (int i = 0; i < resources.size(); i++) {

                    String path = resources.get(i).getPath();

                    String[] arr = path.split("\\.");
                    String suffix = arr[arr.length - 1];

                    arr = path.split("/");
                    String name = arr[arr.length - 1].substring(14);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", name);
                    jsonObject.put("suffix", suffix);
                    jsonObject.put("path", resources.get(i));
                    resourceArray.add(jsonObject);

                }

            }
            originalField.remove("resources");
            originalField.put("resources", resourceArray);
        }
        if(newField.get("resources")!=null){
            JSONArray resourceArray = new JSONArray();
            List<Resource> resources = ((JSONArray)newField.get("resources")).toJavaList(Resource.class);

            if (resources != null) {
                for (int i = 0; i < resources.size(); i++) {

                    String path = resources.get(i).getPath();

                    String[] arr = path.split("\\.");
                    String suffix = arr[arr.length - 1];

                    arr = path.split("/");
                    String name = arr[arr.length - 1].substring(14);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", name);
                    jsonObject.put("suffix", suffix);
                    jsonObject.put("path", resources.get(i));
                    resourceArray.add(jsonObject);

                }

            }
            newField.remove("resources");
            newField.put("resources", resourceArray);
        }


        //********************** 非通用属性单独处理 **************************
        ItemTypeEnum type = version.getType();
        switch (type){
            case ModelItem:{
                getModelItemChanged(originalField, newField);
                break;
            }
            case LogicalModel:
            case ConceptualModel:
            {
                getLogicalModelItemChanged(originalField, newField);
                break;
            }
            case DataItem:{
                getDataItemChanged(originalField, newField, ItemTypeEnum.DataItem);
                break;
            }
            case DataHub:{
                getDataItemChanged(originalField, newField, ItemTypeEnum.DataHub);
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
        if(originalField.get("classifications") != null&&newField.get("classifications")!=null){

            //得到每个分类对应的分类dao
            JSONObject daoFactory = genericService.daoFactory(type);
            GenericCategoryDao classificationDao = (GenericCategoryDao)daoFactory.get("classificationDao");

            List<String> originalCls = (List<String>)originalField.get("classifications");
            List<String> newCls = (List<String>)newField.get("classifications");

            // List<JSONObject> oriName = new ArrayList<>();
            // List<JSONObject> newName = new ArrayList<>();
            //
            // for (String o : originalCls) {
            //     GenericCategory cls = (GenericCategory)classificationDao.findFirstById(o);
            //     JSONObject jsonObject = new JSONObject();
            //     if (cls == null){
            //         jsonObject.put("id","unknown");
            //         jsonObject.put("name","unknown");
            //         oriName.add(jsonObject);
            //         continue;
            //     }
            //     jsonObject.put("id",cls.getId());
            //     jsonObject.put("name",cls.getNameEn());
            //     oriName.add(jsonObject);
            // }
            // for (String n : newCls) {
            //     GenericCategory cls = (GenericCategory)classificationDao.findFirstById(n);
            //     JSONObject jsonObject = new JSONObject();
            //     if (cls == null){
            //         jsonObject.put("id","unknown");
            //         jsonObject.put("name","unknown");
            //         oriName.add(jsonObject);
            //         continue;
            //     }
            //     jsonObject.put("id",cls.getId());
            //     jsonObject.put("name",cls.getNameEn());
            //     oriName.add(jsonObject);
            // }

            JSONArray oriName = repositoryService.getClassification(originalCls, classificationDao);
            JSONArray newName = repositoryService.getClassification(newCls, classificationDao);

            originalField.remove("classifications");
            originalField.put("classifications", oriName);
            newField.remove("classifications");
            newField.put("classifications", newName);

        }

        if (type == ItemTypeEnum.Concept){
            if(originalField.get("related") != null&&newField.get("related")!=null){
                // List<String> related = concept.getRelated();
                List<String> originalRelated = (List<String>)originalField.get("related");
                List<String> newClsRelated = (List<String>)newField.get("related");

                JSONArray originalRelateArray = new JSONArray();
                JSONArray newRelateArray = new JSONArray();
                getConceptRelated(originalRelated, originalRelateArray, conceptDao);
                getConceptRelated(newClsRelated, newRelateArray, conceptDao);

                originalField.remove("related");
                originalField.put("related", originalRelateArray);
                newField.remove("related");
                newField.put("related", newRelateArray);
            }

        }

    }

    void getConceptRelated(List<String> newClsRelated, JSONArray newRelateArray, ConceptDao conceptDao) {
        if (newClsRelated != null) {
            for (String relatedId : newClsRelated) {
                Concept relatedConcept = conceptDao.findFirstById(relatedId);
                String name = relatedConcept.getName();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", relatedId);
                jsonObject.put("name", name);
                newRelateArray.add(jsonObject);
            }
        }
    }

    private void getDataMethodChanged(JSONObject originalField, JSONObject newField) {

    }

    private void getDataItemChanged(JSONObject originalField, JSONObject newField, ItemTypeEnum type) {


        //classifications
        if(originalField.get("classifications") != null&&newField.get("classifications")!=null){

            //得到每个分类对应的分类dao
            // JSONObject daoFactory = genericService.daoFactory(type);
            // GenericCategoryDao classificationDao = (GenericCategoryDao)daoFactory.get("classificationDao");

            List<String> originalCls = (List<String>)originalField.get("classifications");
            List<String> newCls = (List<String>)newField.get("classifications");

            List<Classification> oriName = dataItemService.getClassifications(originalCls);
            List<Classification> newName = dataItemService.getClassifications(newCls);

            // for (String o : originalCls) {
            //     GenericCategory cls = (GenericCategory)classificationDao.findFirstById(o);
            //     if (cls == null){
            //         oriName.add("unknown");
            //         continue;
            //     }
            //     oriName.add(cls.getNameEn());
            // }
            // for (String n : newCls) {
            //     GenericCategory cls = (GenericCategory)classificationDao.findFirstById(n);
            //     if (cls == null){
            //         oriName.add("unknown");
            //         continue;
            //     }
            //     newName.add(cls.getNameEn());
            // }

            originalField.remove("classifications");
            originalField.put("classifications", oriName);
            newField.remove("classifications");
            newField.put("classifications", newName);

        }

        //dataItem的relatedModels
        if(originalField.get("relatedModels") != null && newField.get("relatedModels") != null){

            List<String> originalRelatedModels = (List<String>)originalField.get("relatedModels");
            List<String> newRelatedModels = (List<String>)newField.get("relatedModels");

            List<String> oriName = new ArrayList<>();
            List<String> newName = new ArrayList<>();

            for (String o : originalRelatedModels) {
                ModelItem item = modelItemDao.findFirstById(o);
                if (item == null){
                    oriName.add("unknown");
                    continue;
                }
                oriName.add(item.getName());
            }
            for (String n : newRelatedModels) {
                ModelItem item = modelItemDao.findFirstById(n);
                if (item == null){
                    newName.add("unknown");
                    continue;
                }
                newName.add(item.getName());
            }


            originalField.remove("relatedModels");
            originalField.put("relatedModels", oriName);
            newField.remove("relatedModels");
            newField.put("relatedModels", newName);

        }
    }
    private void getLogicalModelItemChanged(JSONObject originalField, JSONObject newField) {
        if(originalField.get("imageList") == null){
            originalField.put("imageList", null);
        }
        if(newField.get("imageList") == null){
            newField.put("imageList", null);
        }

    }
    private void getModelItemChanged(JSONObject originalField, JSONObject newField) {

        if(originalField.get("classifications") != null&&newField.get("classifications")!=null){
            JSONArray classResult_old = modelClassificationService.getClassifications((List<String>) originalField.get("classifications"));
            originalField.remove("classifications");
            originalField.put("classifications", classResult_old);

            JSONArray classResult_new = modelClassificationService.getClassifications((List<String>) newField.get("classifications"));
            newField.remove("classifications");
            newField.put("classifications", classResult_new);
        }



        if(originalField.get("relate")!=null){
            //relate
            ModelItemRelate modelItemRelate_original= JSON.parseObject(JSON.toJSONString(originalField.get("relate")), ModelItemRelate.class);
            JSONObject relate_original = modelGetRelate(modelItemRelate_original);

            ModelItemRelate modelItemRelate_new= JSON.parseObject(JSON.toJSONString(newField.get("relate")), ModelItemRelate.class);
            JSONObject relate_new = modelGetRelate(modelItemRelate_new);

            originalField.remove("relate");
            originalField.put("relate", relate_original);

            newField.remove("relate");
            newField.put("relate", relate_new);
        }

    }


    public ModelAndView getHistoryList(String type, String id) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("version/historyList");

        ItemTypeEnum itemType = ItemTypeEnum.getItemTypeByName(type);
        if (itemType == null){
            modelAndView.setViewName("error");
            return modelAndView;
        }

        JSONObject daoFactory = genericService.daoFactory(itemType);
        GenericItemDao itemDao = (GenericItemDao) daoFactory.get("itemDao");

        PortalItem item = (PortalItem)itemDao.findFirstById(id);
        JSONArray resultList = new JSONArray();

        if(item == null){
            modelAndView.addObject("list", resultList);
            return modelAndView;
        }

        List<String> versions = item.getVersions();
        Collections.reverse(versions);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 新增代码， 如果找不到版本信息，先记录下来 然后删除
        List<String> need2delete = new ArrayList<>();

        for (String versionId : versions) {
            Version version = versionDao.findFirstById(versionId);
            if (version == null) {
                need2delete.add(versionId);
                continue;
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("date", sdf.format(version.getSubmitTime()));
            jsonObject.put("id", version.getId());
            User user = userService.getByEmail(version.getEditor());
            jsonObject.put("userName", user.getName());
            jsonObject.put("accessId", user.getAccessId());
            resultList.add(jsonObject);
        }

        // 删除未找到的version
        versions.removeAll(need2delete);
        itemDao.save(item);

        modelAndView.addObject("id", item.getId());
        modelAndView.addObject("name", item.getName());
        modelAndView.addObject("type", type);
        modelAndView.addObject("list", resultList);


        return modelAndView;

    }


    // TODO: 2022/7/7 只能通过id拿到最新的，这种方式拿不到指定版本的 (先copy一个最新的，数据渲染完成之后再更新回去)
    public ModelAndView getItemHistory(String type, String id) {
        ModelAndView modelAndView=new ModelAndView();
        Version version = versionDao.findFirstById(id);
        if (version == null){
            modelAndView.setViewName("error");
            return modelAndView;
        }

        ItemTypeEnum itemType = ItemTypeEnum.getItemTypeByName(type);
        if (itemType == null){
            modelAndView.setViewName("error");
            return modelAndView;
        }

        String itemId = version.getItemId();

        //copy最新版，数据渲染完成之后再更新回去
        GenericItemDao itemDao = (GenericItemDao) genericService.daoFactory(itemType).get("itemDao");
        PortalItem latestVersion = (PortalItem) itemDao.findFirstById(itemId);

        PortalItem content = version.getContent();

        try {
            switch (itemType) {
                case ModelItem:
                    modelAndView= modelItemService.getPage((ModelItem) content, true);
                    break;
                case ConceptualModel:
                    modelAndView= conceptualModelService.getPage((ConceptualModel) content, true);
                    break;
                case LogicalModel:
                    modelAndView= logicalModelService.getPage((LogicalModel) content, true);
                    break;
                case ComputableModel:
                    modelAndView= computableModelService.getPage((ComputableModel) content, true);
                    break;
                case Concept:
                    modelAndView= repositoryService.getConceptPage((Concept) content, true);
                    break;
                case SpatialReference:
                    modelAndView= repositoryService.getSpatialReferencePage((SpatialReference) content, true);
                    break;
                case Template:
                    modelAndView= repositoryService.getTemplatePage((Template) content, true);
                    break;
                case Unit:
                    modelAndView= repositoryService.getUnitPage((Unit) content, true);
                    break;
                case DataItem:
                    modelAndView = dataItemService.getPage((DataItem) content, dataItemDao, true);
                    break;
                case DataHub:
                    modelAndView = dataItemService.getPage((DataItem) content, dataHubDao, true);
                    break;
                case DataMethod:
                    modelAndView = dataMethodService.getPage((DataMethod) content, true);
                    break;

            }

            //更新回去
            // itemDao.save(latestVersion);
            redisService.saveItem(latestVersion, itemType);

            modelAndView.addObject("history",true);
        } catch (Exception e){
            // 获取页面出错的话要把条目更新回去
            redisService.saveItem(latestVersion, itemType);

            modelAndView.setViewName("/error/500");
        }

        return modelAndView;

    }
}
