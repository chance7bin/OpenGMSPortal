package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.dao.*;
import njgis.opengms.portal.entity.doo.*;
import njgis.opengms.portal.entity.doo.base.PortalItem;
import njgis.opengms.portal.entity.doo.data.SimpleFileInfo;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.entity.dto.SpecificFindDTO;
import njgis.opengms.portal.entity.dto.model.RelatedModelInfoDTO;
import njgis.opengms.portal.entity.po.ModelItem;
import njgis.opengms.portal.entity.po.User;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.enums.ResultEnum;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description 通用Service
 * @Author bin
 * @Date 2021/08/16
 */
@Slf4j
@Service
public class GenericService {

    @Autowired
    UserDao userDao;

    @Autowired
    DataItemDao dataItemDao;

    @Autowired
    ModelItemDao modelItemDao;

    @Autowired
    DataHubDao dataHubDao;

    @Autowired
    DataMethodDao dataMethodDao;

    @Autowired
    ClassificationDao classificationDao;

    @Autowired
    MethodClassificationDao methodClassificationDao;

    @Autowired
    TemplateDao templateDao;

    @Autowired
    TemplateClassificationDao templateClassificationDao;

    @Autowired
    ConceptDao conceptDao;

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
    ThemeDao themeDao;

    @Autowired
    ComputableModelDao computableModelDao;

    @Autowired
    VersionDao versionDao;



    @Value("${htmlLoadPath}")
    private String htmlLoadPath;

    @Value("${resourcePath}")
    private String resourcePath;

    @Value(value = "Public,Discoverable")
    private List<String> itemStatusVisible;


    /**
     * 根据传入的查询条件查询数据(curQueryField未设置的话默认用name进行查询 [e.g. searchByName接口] )
     * @param findDTO 查询条件
     * @param type 条目类型
     * @return com.alibaba.fastjson.JSONObject
     * @Author bin
     **/
    public JSONObject searchItems(SpecificFindDTO findDTO, ItemTypeEnum type){

        JSONObject jsonObject = searchDBItems(findDTO, type);


        return generateSearchResult((List<PortalItem>) jsonObject.get("allPortalItem"), jsonObject.getIntValue("totalElements"));
    }


    /**
     * 不对查询的结果做处理
     * @param findDTO
     * @param type
     * @return com.alibaba.fastjson.JSONObject
     * @Author bin
     **/
    public JSONObject searchDBItems(SpecificFindDTO findDTO, ItemTypeEnum type){
        // setGenericDataItemDao(dataType);

        // 所有条目都继承PortalItem类
        List<PortalItem> allPortalItem;
        int totalElements = 0;
        try {
            Page itemsPage;
            Pageable pageable = PageRequest.of(findDTO.getPage()-1, findDTO.getPageSize(), Sort.by(findDTO.getAsc()? Sort.Direction.ASC: Sort.Direction.DESC,findDTO.getSortField()));

            // 从工厂中拿对应的dao
            JSONObject daoFactory = daoFactory(type);

            itemsPage = selectItemsByCurQueryFieldAndCategoryName(findDTO, pageable, (GenericItemDao) daoFactory.get("itemDao"), (GenericCategoryDao) daoFactory.get("classificationDao"));
            //把查询到的结果放在try中,如果按照之前return null的话前端页面会加载不出来，所以如果查询报错的话那allPortalItem大小就为0
            allPortalItem = itemsPage.getContent();
            totalElements = (int) itemsPage.getTotalElements();
        } catch (MyException err) {
            log.error(String.valueOf(err));
            allPortalItem = new ArrayList<>();
            // TODO 查询数据库出错要做什么处理
            // return null;
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("allPortalItem",allPortalItem);
        jsonObject.put("totalElements",totalElements);




        return jsonObject;
    }


    /**
     * 根据登录用户得到条目信息 (curQueryField未设置的话默认用name进行查询 [e.g. searchByTitleByOid接口] )
     * @param findDTO
     * @param type
     * @param user 登录用户email
     * @return com.alibaba.fastjson.JSONObject
     * @Author bin
     **/
    public JSONObject searchItemsByUser(FindDTO findDTO, ItemTypeEnum type, String user){
        // 所有条目都继承PortalItem类
        List<PortalItem> allPortalItem;
        int totalElements = 0;
        try {
            Page itemsPage;
            Pageable pageable = PageRequest.of(findDTO.getPage()-1, findDTO.getPageSize(), Sort.by(findDTO.getAsc()? Sort.Direction.ASC: Sort.Direction.DESC,findDTO.getSortField()));

            // 从工厂中拿对应的dao
            JSONObject daoFactory = daoFactory(type);
            GenericItemDao itemDao = (GenericItemDao) daoFactory.get("itemDao");
            itemsPage = itemDao.findAllByNameContainsIgnoreCaseAndAuthor(findDTO.getSearchText(), user, pageable);
            //把查询到的结果放在try中,如果按照之前return null的话前端页面会加载不出来，所以如果查询报错的话那allPortalItem大小就为0
            allPortalItem = itemsPage.getContent();
            totalElements = (int) itemsPage.getTotalElements();
        } catch (MyException err) {
            log.error(String.valueOf(err));
            allPortalItem = new ArrayList<>();
            // TODO 查询数据库出错要做什么处理
            // return null;
        }

        return generateSearchResult(allPortalItem, totalElements);
    }

    /**
     * 生成接口返回的数据信息
     * @param allPortalItem
     * @param totalElements
     * @return com.alibaba.fastjson.JSONObject
     * @Author bin
     **/
    public JSONObject generateSearchResult(List<PortalItem> allPortalItem, int totalElements){
        JSONArray lists = new JSONArray();
        JSONArray users = new JSONArray();
        for (int i=0;i<allPortalItem.size();++i) {

            PortalItem portalItem = allPortalItem.get(i);

            String email = portalItem.getAuthor();
            User user = userDao.findFirstByEmail(email);
            JSONObject userObject = new JSONObject();
            userObject.put("id",user.getId());
            userObject.put("image",user.getAvatar().equals("")?"":htmlLoadPath + user.getAvatar());
            userObject.put("name",user.getName());
            userObject.put("userId",user.getAccessId());
            userObject.put("email", user.getEmail());
            users.add(userObject);

            JSONObject jsonObject = new JSONObject();
            // jsonObject.put("author",userObject);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            if (portalItem.getCreateTime() != null) {
                jsonObject.put("createTime", simpleDateFormat.format(portalItem.getCreateTime()));
            }
            jsonObject.put("name",portalItem.getName());
            jsonObject.put("keywords", portalItem.getKeywords());
            jsonObject.put("description",portalItem.getOverview());
            // jsonObject.put("type",portalItem.getType());
            jsonObject.put("status",portalItem.getStatus());
            // jsonObject.put("id",portalItem.getId());
            jsonObject.put("id", portalItem.getId());
            jsonObject.put("viewCount",portalItem.getViewCount());
            jsonObject.put("dailyViewCount",portalItem.getDailyViewCount());
            // jsonObject.put("invokeServices",portalItem.getInvokeServices());
            // jsonObject.put("authorName",user.getName());
            // jsonObject.put("authorId",user.getAccessId());
            lists.add(jsonObject);
        }

        JSONObject res = new JSONObject();
        res.put("list",lists);
        res.put("total",totalElements);
        res.put("users",users);
        return res;
    }


    /**
     * dao工厂
     * @param type
     * @return com.alibaba.fastjson.JSONObject
     * @Author bin
     **/
    public JSONObject daoFactory(ItemTypeEnum type){

        JSONObject daoUtils = new JSONObject();
        // 根据查询的条目类型选择相应的DAO
        switch (type){
            case ModelItem:{
                daoUtils.put("itemDao",modelItemDao);
                daoUtils.put("classificationDao",classificationDao);
                break;
            }
            case ComputableModel:{
                daoUtils.put("itemDao",computableModelDao);
                daoUtils.put("classificationDao",classificationDao);
                break;
            }
            case DataItem:{
                daoUtils.put("itemDao",dataItemDao);
                daoUtils.put("classificationDao",classificationDao);
                break;
            }
            case DataHub:{
                daoUtils.put("itemDao",dataHubDao);
                daoUtils.put("classificationDao",classificationDao);
                break;
            }
            case DataMethod:{
                daoUtils.put("itemDao",dataMethodDao);
                daoUtils.put("classificationDao",methodClassificationDao);
                break;
            }
            case Template:{
                daoUtils.put("itemDao",templateDao);
                daoUtils.put("classificationDao",templateClassificationDao);
                break;
            }
            case Concept:{
                daoUtils.put("itemDao",conceptDao);
                daoUtils.put("classificationDao",conceptClassificationDao);
                break;
            }
            case SpatialReference:{
                daoUtils.put("itemDao",spatialReferenceDao);
                daoUtils.put("classificationDao",spatialReferenceClassificationDao);
                break;
            }
            case Unit:{
                daoUtils.put("itemDao",unitDao);
                daoUtils.put("classificationDao",unitClassificationDao);
                break;
            }
            case Theme:{
                daoUtils.put("itemDao",themeDao);
                break;
            }
            case Version:{
                daoUtils.put("itemDao",versionDao);
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        return daoUtils;

    }


    /**
     * 构建classifications集合,如果没有childrenId则表示只查询一个分类条目，有的话表示有多个分类条目
     * @param categoryId
     * @param genericCategoryDao
     * @return java.util.List<java.lang.String>
     * @Author bin
     **/
    public List<String> buildClassifications(String categoryId, GenericCategoryDao genericCategoryDao){
        List<String> classifications = new ArrayList<>();
        GenericCategory category = (GenericCategory)genericCategoryDao.findFirstById(categoryId);
        List<String> childrenId = category.getChildrenId();
        if (childrenId == null || childrenId.size() == 0){
            classifications.add(categoryId);
        }
        else {
            for (String child : childrenId) {
                classifications.addAll(buildClassifications(child,genericCategoryDao));
            }
        }
        return classifications;
    }


    /**
     * @Description 根据查询字段类型、分类名进行查找
     * @Param [findDTO, pageable, genericItemDao, genericCatalogDao]
     * @return org.springframework.data.domain.Page
     **/
    public Page selectItemsByCurQueryFieldAndCategoryName(SpecificFindDTO findDTO, Pageable pageable, GenericItemDao genericItemDao, GenericCategoryDao genericCategoryDao) {

        String searchText = findDTO.getSearchText();
        String curQueryField = findDTO.getCurQueryField();
        String categoryName = findDTO.getCategoryName();
        List<String> classifications = new ArrayList<>();

        // 构建classifications集合,如果没有childrenId则表示只查询一个分类条目，有的话表示有多个分类条目
        if (categoryName != null && !categoryName.equals(""))
            classifications = buildClassifications(categoryName, genericCategoryDao);

        Page result;
        try {
            // Object categoryById = genericCategoryDao.findFirstById(categoryName);
            // categoryName = categoryById == null ? "" : ((GenericCatalog)categoryById).getNameEn();
            if(categoryName == null || categoryName.equals("")) {          // 不分类的情况
                if(searchText.equals("")){
                    result = genericItemDao.findAllByStatusIn(itemStatusVisible,pageable);
                    // result = genericItemDao.findAll(pageable);
                }
                else if (curQueryField == null || curQueryField.equals("")){
                    // 如果没有查询的类型则默认以名字进行查询
                    result = genericItemDao.findAllByNameContainsIgnoreCaseAndStatusIn(searchText, itemStatusVisible, pageable);
                }
                else{
                    switch (curQueryField) {
                        case "name":{
                            result = genericItemDao.findAllByNameContainsIgnoreCaseAndStatusIn(searchText, itemStatusVisible, pageable);
                            break;
                        }
                        case "keyword":{
                            result = genericItemDao.findAllByKeywordsContainsIgnoreCaseAndStatusIn(searchText, itemStatusVisible, pageable);
                            break;
                        }
                        case "content":{
                            result = genericItemDao.findAllByOverviewContainsIgnoreCaseAndStatusIn(searchText, itemStatusVisible, pageable);
                            break;
                        }
                        case "contributor":{
                            List<User> users = userDao.findAllByNameContainsIgnoreCase(searchText);
                            List<String> userEmailList = new ArrayList<>();
                            for (User user : users) {
                                userEmailList.add(userDao.findFirstByName(user.getName()).getEmail());
                            }
                            result = genericItemDao.findAllByAuthorInOrContributorsIn(userEmailList, userEmailList, pageable);
                            break;
                        }
                        //添加查询用户本人创建的条目 kx 21.10.15
                        case "author":{
                            List<String> email =  new ArrayList<>();
                            email.add(searchText);
                            result = genericItemDao.findAllByAuthorInAndStatusIn(email, itemStatusVisible, pageable);
                            break;
                        }
                        default:{
                            log.error("curQueryField" + curQueryField + " is wrong.");
                            return null;
                        }
                    }
                }
            } else {
                if(searchText.equals("")){
                    // result = genericItemDao.findAllByClassificationsIn(classifications, pageable);
                    result = genericItemDao.findAllByClassificationsInAndStatusIn(classifications, itemStatusVisible, pageable);
                }
                else if (curQueryField == null || curQueryField.equals("")){
                    // 如果没有查询的类型则默认以名字进行查询
                    result = genericItemDao.findAllByNameContainsIgnoreCaseAndClassificationsInAndStatusIn(searchText, classifications, itemStatusVisible, pageable);
                }
                else {
                    switch (curQueryField) {
                        case "name":{
                            result = genericItemDao.findAllByNameContainsIgnoreCaseAndClassificationsInAndStatusIn(searchText, classifications, itemStatusVisible, pageable);
                            break;
                        }
                        case "keyword":{
                            result = genericItemDao.findAllByKeywordsContainsIgnoreCaseAndClassificationsInAndStatusIn(searchText, classifications, itemStatusVisible, pageable);
                            break;
                        }
                        case "content":{
                            result = genericItemDao.findAllByOverviewContainsIgnoreCaseAndClassificationsInAndStatusIn(searchText, classifications, itemStatusVisible, pageable);
                            break;
                        }
                        case "contributor":{
                            List<User> users = userDao.findAllByNameContainsIgnoreCase(searchText);
                            List<String> userEmailList = new ArrayList<>();
                            for (User user : users) {
                                userEmailList.add(userDao.findFirstByName(user.getName()).getEmail());
                            }
                            result = genericItemDao.findAllByAuthorInAndClassificationsInAndStatusIn(userEmailList,classifications, itemStatusVisible, pageable);
                            // if(user != null){
                            //     result = dataItemDao.findAllByAuthorLikeIgnoreCaseAndClassificationsIn(user.getEmail(), categoryName, pageable);
                            // } else {    // 取一个不存在的名字，返回nodata，不能返回null
                            //     result = dataItemDao.findAllByAuthorLikeIgnoreCaseAndClassificationsIn("hhhhhhhhhhhhhhhh", categoryName, pageable);
                            // }
                            break;
                        }
                        default:{
                            log.error("curQueryField" + curQueryField + " is wrong.");
                            return null;
                            // result = genericItemDao.findAllByNameContainsIgnoreCaseAndClassificationsIn(searchText, categoryName, pageable);
                            // break;
                        }
                    }
                }

            }
        }catch (Exception e){
            log.error(e.getMessage());
            throw new MyException(ResultEnum.NO_OBJECT);
        }


        return result;
    }


    /**
     * @Description 根据id查dataItem
     * @Param [id, genericItemDao]
     * @return njgis.opengms.portal.entity.doo.base.PortalItem
     **/
    public PortalItem getById(String id, GenericItemDao genericItemDao) {


        return (PortalItem) genericItemDao.findById(id).orElseGet(() -> {

            log.error("有人乱查数据库！！该ID不存在对象:" + id);

            throw new MyException(ResultEnum.NO_OBJECT);

        });

    }
    
    /**
     * 根据id查dataItem
     * @param id 条目id
     * @param type 条目类型
     * @return njgis.opengms.portal.entity.doo.JsonResult 
     * @Author bin
     **/
    public JsonResult getById(String id, ItemTypeEnum type){

        JSONObject factory = daoFactory(type);
        GenericItemDao itemDao = (GenericItemDao)factory.get("itemDao");
        try {
            PortalItem item = getById(id, itemDao);
            return ResultUtils.success(item);
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            return ResultUtils.error();
        }
        

    }
    
    

    /**
     * 保存条目数据
     * @param item
     * @param genericItemDao
     * @return njgis.opengms.portal.entity.doo.base.PortalItem
     * @Author bin
     **/
    public PortalItem saveItem(PortalItem item, GenericItemDao genericItemDao){
        return (PortalItem) genericItemDao.save(item);
    }


    /**
     * @Description 记录访问数量
     * @Author bin
     * @Param [item]
     * @return njgis.opengms.portal.entity.doo.base.PortalItem
     **/
    public PortalItem recordViewCount(PortalItem item) {
        List<DailyViewCount> dailyViewCountList = item.getDailyViewCount();

        item.setDailyViewCount(recordViewCountByField(dailyViewCountList));
        item.setViewCount(item.getViewCount() + 1);

        return item;
    }


    /**
     * @Description 记录访问数量(参数是字段)
     * @Author bin
     * @Param [dailyViewCountList]
     * @return njgis.opengms.portal.entity.doo.PortalItem
     **/
    public List<DailyViewCount> recordViewCountByField(List<DailyViewCount> dailyViewCountList) {
        Date now = new Date();
        DailyViewCount newViewCount = new DailyViewCount(now, 1);
        // List<DailyViewCount> dailyViewCountList = item.getDailyViewCount();
        if (dailyViewCountList == null) {
            List<DailyViewCount> newList = new ArrayList<>();
            newList.add(newViewCount);
            dailyViewCountList = newList;
        } else if (dailyViewCountList.size() > 0) {
            DailyViewCount dailyViewCount = dailyViewCountList.get(dailyViewCountList.size() - 1);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            if (sdf.format(dailyViewCount.getDate()).equals(sdf.format(now))) {
                dailyViewCount.setCount(dailyViewCount.getCount() + 1);
                dailyViewCountList.set(dailyViewCountList.size() - 1, dailyViewCount);
            } else {
                dailyViewCountList.add(newViewCount);
            }
        } else {
            dailyViewCountList.add(newViewCount);
        }

        return dailyViewCountList;
    }


    /**
     * 得到用户上传的条目列表
     * @param findDTO
     * @param email
     * @param typeEnum
     * @return com.alibaba.fastjson.JSONObject
     * @Author bin
     **/
    public JSONObject getUserUploadItemList(FindDTO findDTO, String email, ItemTypeEnum typeEnum){

        JSONObject factory = daoFactory(typeEnum);
        GenericItemDao itemDao = (GenericItemDao) factory.get("itemDao");

        Pageable pageable = PageRequest.of(findDTO.getPage()-1, findDTO.getPageSize(), Sort.by(findDTO.getAsc()? Sort.Direction.ASC: Sort.Direction.DESC,findDTO.getSortField()));

        Page items = itemDao.findByAuthor(pageable, email);

        JSONObject TemplateObject = new JSONObject();
        TemplateObject.put("count", items.getTotalElements());
        TemplateObject.put("content", items.getContent());

        return TemplateObject;
    }


    /**
     * 得到Pageable
     * @param findDTO
     * @return org.springframework.data.domain.Pageable
     * @Author bin
     **/
    public Pageable getPageable(FindDTO findDTO){
        return PageRequest.of(findDTO.getPage()-1, findDTO.getPageSize(), Sort.by(findDTO.getAsc()? Sort.Direction.ASC: Sort.Direction.DESC,findDTO.getSortField()));
    }


    /**
     * @Description 概念、逻辑、计算模型 获取与其相关的模型条目简要信息
     * @param relatedModelItems
     * @Return com.alibaba.fastjson.JSONArray
     * @Author kx
     * @Date 21/11/11
     **/
    public List<RelatedModelInfoDTO> getRelatedModelInfoList(List<String> relatedModelItems){
        List<RelatedModelInfoDTO> relatedModelInfoDTOList = new ArrayList<>();

        for(int i = 0;i<relatedModelItems.size();i++){
            String relatedModelItemId = relatedModelItems.get(i);
            ModelItem modelItem=modelItemDao.findFirstById(relatedModelItemId);
            RelatedModelInfoDTO relatedModelInfoDTO = new RelatedModelInfoDTO();
            relatedModelInfoDTO.setName(modelItem.getName());
            relatedModelInfoDTO.setId(modelItem.getId());
            relatedModelInfoDTOList.add(relatedModelInfoDTO);
        }
        return relatedModelInfoDTOList;
    }


    /**
     * @Description 根据文件路径获取用于前端展示的文件信息
     * @param filePathList
     * @Return java.util.List<njgis.opengms.portal.entity.doo.data.SimpleFileInfo>
     * @Author kx
     * @Date 21/11/11
     **/
    public List<SimpleFileInfo> getSimpleFileInfoList(List<String> filePathList){
        List<SimpleFileInfo> simpleFileInfoList = new ArrayList<>();
        if (filePathList != null) {
            for (int i = 0; i < filePathList.size(); i++) {

                String path = filePathList.get(i);

                String[] arr = path.split("\\.");
                String suffix = arr[arr.length - 1];

                arr = path.split("/");
                String name = arr[arr.length - 1].substring(14);

                SimpleFileInfo simpleFileInfo = new SimpleFileInfo();
                simpleFileInfo.setName(name);
                simpleFileInfo.setSuffix(suffix);
                simpleFileInfo.setPath(path);

                simpleFileInfoList.add(simpleFileInfo);
            }
        }
        return simpleFileInfoList;
    }

    /**
     * @Description 根据id或accessId获取条目
     * @param id
     * @Return njgis.opengms.portal.entity.doo.base.PortalItem
     * @Author kx
     * @Date 21/11/15
     **/
    public PortalItem getPortalItem(String id, ItemTypeEnum itemTypeEnum){
        GenericItemDao genericItemDao = (GenericItemDao) daoFactory(itemTypeEnum).get("itemDao");
        PortalItem item = null;
        if(id.matches("[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}")) {
            //条目信息
            try {
                item = (PortalItem) genericItemDao.findFirstById(id);
            } catch (MyException e) {
                return null;
            }
        }else{
            try {
                item = (PortalItem) genericItemDao.findFirstByAccessId(id);
            } catch (MyException e) {
                return null;
            }
        }
        return item;
    }

    /**
     * @Description 检查私有条目是否可以被用户访问
     * @param item 条目对象
     * @param email 访问者唯一标识
     * @Return org.springframework.web.servlet.ModelAndView
     * @Author kx
     * @Date 21/11/12
     **/
    public ModelAndView checkPrivatePageAccessPermission(PortalItem item, String email){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error/404");

        User user = userDao.findFirstByEmail(email);
        if(user.getUserRole().isAdmin()){
            return null;
        }

        if (item.getStatus().equals("Private")) {
            if (email == null) {
                return modelAndView;
            } else {
                if (!email.equals(item.getAuthor())) {
                    return modelAndView;
                }
            }
        }
        return null;
    }
    /**
     * 获取对象所有属性，包括父类
     * @param object
     * @return java.lang.reflect.Field[]
     * @Author bin
     **/
    public List<Field> getAllFields(Object object) {
        Class clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        return fieldList;
    }

    /**
     * 比较两个对象的不同，返回不相等的属性
     * @param originalObj 原始对象
     * @param newObj 待比较对象
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @Author bin
     **/
    public Map<String,Object> getDifferenceBetweenTwoObject(Object originalObj, Object newObj) throws IllegalAccessException {
        Map<String,Object> differences = new HashMap<>();

        //获得所有属性
        List<Field> allFields = getAllFields(originalObj);
        for (Field field : allFields) {
            field.setAccessible(true);
            Object originalField = field.get(originalObj);
            Object newField = field.get(newObj);
            if (newField == null){
                if (originalField != null){
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("original", originalField);
                    jsonObject.put("new", null);
                    differences.put(field.getName(),jsonObject);
                }

            }
            else if (!newField.equals(originalField)){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("original", originalField);
                jsonObject.put("new", newField);
                differences.put(field.getName(),jsonObject);
            }
        }


        return differences;
    }

    /**
     * detail 转 localizationList
     * @param name
     * @param detail
     * @return java.util.List<njgis.opengms.portal.entity.doo.Localization>
     * @Author bin
     **/
    public List<Localization> detail2localization(String name, String detail){
        List<Localization> list = new ArrayList<>();
        Localization localization = new Localization();
        localization.setLocalCode("en");
        localization.setLocalName("English");
        localization.setName(name);
        localization.setDescription(detail);
        list.add(localization);
        return list;
    }

    /**
     * 格式化日期
     * @param date
     * @return java.lang.String
     * @Author bin
     **/
    public String dateFormat(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

}
