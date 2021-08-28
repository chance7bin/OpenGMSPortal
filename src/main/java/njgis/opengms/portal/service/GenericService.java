package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.dao.*;
import njgis.opengms.portal.entity.doo.DailyViewCount;
import njgis.opengms.portal.entity.doo.GenericCatalog;
import njgis.opengms.portal.entity.doo.MyException;
import njgis.opengms.portal.entity.doo.PortalItem;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.entity.po.User;
import njgis.opengms.portal.enums.ResultEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description 用于搜索条目
 * @Author bin
 * @Date 2021/08/16
 */
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

    @Value("${htmlLoadPath}")
    private String htmlLoadPath;

    @Value("${resourcePath}")
    private String resourcePath;


    /**
     * @Description 根据传入的查询条件查询数据
     * @Param [findDTO, dataType: 类型(dataHub, dataItem, dataMethod)]
     * @return com.alibaba.fastjson.JSONObject
     **/
    public JSONObject searchItems(FindDTO findDTO, String type){
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
            System.out.println(err);
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
            // jsonObject.put("oid",portalItem.getOid());
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


    public JSONObject daoFactory(String type){

        JSONObject daoUtils = new JSONObject();
        // 根据查询的条目类型选择相应的DAO
        switch (type){
            case "dataItem":{
                daoUtils.put("itemDao",dataItemDao);
                daoUtils.put("classificationDao",classificationDao);
                break;
            }
            case "dataHub":{
                daoUtils.put("itemDao",dataHubDao);
                daoUtils.put("classificationDao",classificationDao);
                break;
            }
            case "dataMethod":{
                daoUtils.put("itemDao",dataMethodDao);
                daoUtils.put("classificationDao",methodClassificationDao);
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        return daoUtils;

    }


    /**
     * @Description 根据查询字段类型、分类名进行查找
     * @Param [findDTO, pageable, genericItemDao, genericCatalogDao]
     * @return org.springframework.data.domain.Page
     **/
    public Page selectItemsByCurQueryFieldAndCategoryName(FindDTO findDTO, Pageable pageable, GenericItemDao genericItemDao, GenericCategoryDao genericCatalogDao) {

        String searchText = findDTO.getSearchText();
        String curQueryField = findDTO.getCurQueryField();
        String categoryName = findDTO.getCategoryName();


        Page result;
        try {
            // Object categoryById = genericCatalogDao.findFirstById(categoryName);
            // categoryName = categoryById == null ? "" : ((GenericCatalog)categoryById).getNameEn();
            if(categoryName.equals("")) {          // 不分类的情况
                if(searchText.equals("")){
                    result = genericItemDao.findAll(pageable);
                }else{
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
                            User user = userDao.findFirstByName(searchText);
                            if(user != null){
                                result = genericItemDao.findAllByAuthorLikeIgnoreCase(user.getEmail(), pageable);
                            } else {        // 取一个不存在的名字，返回nodata，不能返回null
                                result = genericItemDao.findAllByAuthorLikeIgnoreCase("hhhhhhhhhhhhhhhhhh", pageable);
                            }
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
                    result = genericItemDao.findAllByClassificationsIn(categoryName, pageable);
                } else {
                    switch (curQueryField) {
                        case "name":{
                            result = genericItemDao.findAllByNameContainsIgnoreCaseAndClassificationsIn(searchText, categoryName, pageable);
                            break;
                        }
                        case "keyword":{
                            result = genericItemDao.findAllByKeywordsContainsIgnoreCaseAndClassificationsIn(searchText, categoryName, pageable);
                            break;
                        }
                        case "content":{
                            result = genericItemDao.findAllByOverviewContainsIgnoreCaseAndClassificationsIn(searchText, categoryName, pageable);
                            break;
                        }
                        case "contributor":{
                            List<User> users = userDao.findAllByNameContainsIgnoreCase(searchText);
                            List<String> userEmailList = new ArrayList<>();
                            for (User user : users) {
                                userEmailList.add(userDao.findFirstByName(user.getName()).getEmail());
                            }
                            result = genericItemDao.findByAuthorInAndClassificationsIn(userEmailList,categoryName, pageable);
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
     * @return njgis.opengms.portal.entity.doo.PortalItem
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
     * @return njgis.opengms.portal.entity.doo.PortalItem
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




}
