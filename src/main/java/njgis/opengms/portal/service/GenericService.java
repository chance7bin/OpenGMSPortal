package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSON;
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
import njgis.opengms.portal.entity.po.ComputableModel;
import njgis.opengms.portal.entity.po.ModelItem;
import njgis.opengms.portal.entity.po.User;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.enums.ResultEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description 用于搜索条目
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
    VersionDao versionDao;



    @Value("${htmlLoadPath}")
    private String htmlLoadPath;

    @Value("${resourcePath}")
    private String resourcePath;


    /**
     * @Description 根据传入的查询条件查询数据
     * @Param [findDTO, dataType: 类型(dataHub, dataItem, dataMethod)]
     * @return com.alibaba.fastjson.JSONObject
     **/
    public JSONObject searchItems(SpecificFindDTO findDTO, ItemTypeEnum type){
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


    public JSONObject daoFactory(ItemTypeEnum type){

        JSONObject daoUtils = new JSONObject();
        // 根据查询的条目类型选择相应的DAO
        switch (type){
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

        // 构建classifications集合,如果没有childrenId则表示只查询一个分类条目，有的话表示有多个分类条目
        List<String> classifications = buildClassifications(categoryName, genericCategoryDao);

        Page result;
        try {
            // Object categoryById = genericCategoryDao.findFirstById(categoryName);
            // categoryName = categoryById == null ? "" : ((GenericCatalog)categoryById).getNameEn();
            if(categoryName.equals("")) {          // 不分类的情况
                if(searchText.equals("")){
                    result = genericItemDao.findAll(pageable);
                }
                else if (curQueryField == null || curQueryField.equals("")){
                    // 如果没有查询的类型则默认以名字进行查询
                    result = genericItemDao.findAllByNameContainsIgnoreCase(searchText, pageable);
                }
                else{
                    switch (curQueryField) {
                        case "name":{
                            result = genericItemDao.findAllByNameContainsIgnoreCase(searchText, pageable);
                            break;
                        }
                        case "keyword":{
                            result = genericItemDao.findAllByKeywordsContainsIgnoreCase(searchText, pageable);
                            break;
                        }
                        case "content":{
                            result = genericItemDao.findAllByOverviewContainsIgnoreCase(searchText, pageable);
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
                            result = genericItemDao.findAllByAuthorIn(email, pageable);
                            break;
                        }
                        default:{
                            System.out.println("curQueryField" + curQueryField + " is wrong.");
                            return null;
                        }
                    }
                }
            } else {
                if(searchText.equals("")){
                    result = genericItemDao.findAllByClassificationsIn(classifications, pageable);
                }
                else if (curQueryField == null || curQueryField.equals("")){
                    // 如果没有查询的类型则默认以名字进行查询
                    result = genericItemDao.findAllByNameContainsIgnoreCaseAndClassificationsIn(searchText, classifications, pageable);
                }
                else {
                    switch (curQueryField) {
                        case "name":{
                            result = genericItemDao.findAllByNameContainsIgnoreCaseAndClassificationsIn(searchText, classifications, pageable);
                            break;
                        }
                        case "keyword":{
                            result = genericItemDao.findAllByKeywordsContainsIgnoreCaseAndClassificationsIn(searchText, classifications, pageable);
                            break;
                        }
                        case "content":{
                            result = genericItemDao.findAllByOverviewContainsIgnoreCaseAndClassificationsIn(searchText, classifications, pageable);
                            break;
                        }
                        case "contributor":{
                            List<User> users = userDao.findAllByNameContainsIgnoreCase(searchText);
                            List<String> userEmailList = new ArrayList<>();
                            for (User user : users) {
                                userEmailList.add(userDao.findFirstByName(user.getName()).getEmail());
                            }
                            result = genericItemDao.findAllByAuthorInAndClassificationsIn(userEmailList,classifications, pageable);
                            // if(user != null){
                            //     result = dataItemDao.findAllByAuthorLikeIgnoreCaseAndClassificationsIn(user.getEmail(), categoryName, pageable);
                            // } else {    // 取一个不存在的名字，返回nodata，不能返回null
                            //     result = dataItemDao.findAllByAuthorLikeIgnoreCaseAndClassificationsIn("hhhhhhhhhhhhhhhh", categoryName, pageable);
                            // }
                            break;
                        }
                        default:{
                            System.out.println("curQueryField" + curQueryField + " is wrong.");
                            return null;
                            // result = genericItemDao.findAllByNameContainsIgnoreCaseAndClassificationsIn(searchText, categoryName, pageable);
                            // break;
                        }
                    }
                }

            }
        }catch (Exception e){
            System.out.println("查询数据库时出错");
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

            System.out.println("有人乱查数据库！！该ID不存在对象:" + id);

            throw new MyException(ResultEnum.NO_OBJECT);

        });

    }


    /**
     * @Description 记录访问数量
     * @Author bin
     * @Param [item]
     * @return njgis.opengms.portal.entity.doo.base.PortalItem
     **/
    public PortalItem recordViewCount(PortalItem item) {
        Date now = new Date();
        DailyViewCount newViewCount = new DailyViewCount(now, 1);

        List<DailyViewCount> dailyViewCountList = item.getDailyViewCount();
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

        item.setDailyViewCount(dailyViewCountList);
        item.setViewCount(item.getViewCount() + 1);

        return item;
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
     * 获取分页标准
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
}
