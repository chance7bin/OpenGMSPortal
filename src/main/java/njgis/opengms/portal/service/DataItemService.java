package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.xml.bind.v2.TODO;
import njgis.opengms.portal.dao.DataItemDao;
import njgis.opengms.portal.dao.ModelItemDao;
import njgis.opengms.portal.dao.UserDao;
import njgis.opengms.portal.entity.doo.AuthorInfo;
import njgis.opengms.portal.entity.doo.DailyViewCount;
import njgis.opengms.portal.entity.doo.Localization;
import njgis.opengms.portal.entity.doo.MyException;
import njgis.opengms.portal.entity.doo.data.InvokeService;
import njgis.opengms.portal.entity.po.DataItem;
import njgis.opengms.portal.entity.po.ModelItem;
import njgis.opengms.portal.entity.po.User;
import njgis.opengms.portal.enums.ResultEnum;
import njgis.opengms.portal.utils.ResultUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/04
 */
@Service
public class DataItemService {

    @Autowired
    DataItemDao dataItemDao;

    @Autowired
    UserService userService;

    @Autowired
    ModelItemDao modelItemDao;

    @Autowired
    UserDao userDao;

    @Value("${htmlLoadPath}")
    private String htmlLoadPath;

    public DataItem getById(String id) {


        return dataItemDao.findById(id).orElseGet(() -> {

            System.out.println("有人乱查数据库！！该ID不存在对象:" + id);

            throw new MyException(ResultEnum.NO_OBJECT);

        });

    }

    public DataItem recordViewCount(DataItem dataItem) {
        Date now = new Date();
        DailyViewCount newViewCount = new DailyViewCount(now, 1);

        List<DailyViewCount> dailyViewCountList = dataItem.getDailyViewCount();
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

        dataItem.setDailyViewCount(dailyViewCountList);
        dataItem.setViewCount(dataItem.getViewCount() + 1);

        return dataItem;
    }


    public ModelAndView getPage(String id){
        ModelAndView view = new ModelAndView();

        DataItem dataItem;
        try {
            dataItem = getById(id);
        }catch (MyException e){
            view.setViewName("error/404");
            return view;
        }


        dataItem = recordViewCount(dataItem);
        dataItemDao.save(dataItem);


        //用户信息

        JSONObject userJson = userService.getItemUserInfoByEmail(dataItem.getAuthor());

        //authorship
        String authorshipString="";
        List<AuthorInfo> authorshipList=dataItem.getAuthorships();
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
        //related models
        JSONArray modelItemArray=new JSONArray();
        List<String> relatedModels=dataItem.getRelatedModels();
        if(relatedModels!=null) {
            for (String mid : relatedModels) {
                try {
                    ModelItem modelItem = modelItemDao.findFirstById(mid);
                    JSONObject modelItemJson = new JSONObject();
                    modelItemJson.put("name", modelItem.getName());
                    modelItemJson.put("id", modelItem.getId());
                    modelItemJson.put("description", modelItem.getOverview());
                    modelItemJson.put("image", modelItem.getImage().equals("") ? null : htmlLoadPath + modelItem.getImage());
                    modelItemArray.add(modelItemJson);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        // List<String> classifications = new ArrayList<>();
        // List<String> categories = dataItem.getClassifications();
        // for (String category : categories) {
        //     DataCategorys dataCategorys = dataCategorysDao.findFirstById(category);
        //     if (dataCategorys == null){
        //         continue;
        //     }
        //     String name = dataCategorys.getCategory();
        //     classifications.add(name);
        // }


        ArrayList<String> fileName = new ArrayList<>();
        if (null!=dataItem.getDataType()&&dataItem.getDataType().equals("DistributedNode")){
            fileName.add(dataItem.getName());
        }
        //设置远程数据内容
        List<InvokeService> invokeServices = dataItem.getInvokeServices();



        //排序
        List<Localization> locals = dataItem.getLocalizationList();
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


        view.setViewName("data_item_info");
        // if (null!=dataItem.getRelatedProcessings()){
        //     view.addObject("relatedProcessing",dataItem.getRelatedProcessings());
        // }
        // if (null!=dataItem.getRelatedVisualizations()){
        //     view.addObject("relatedVisualization",dataItem.getRelatedVisualizations());
        // }
        view.addObject("datainfo", ResultUtils.success(dataItem));
        view.addObject("user",userJson);
        view.addObject("classifications",dataItem.getClassifications());
        view.addObject("relatedModels",modelItemArray);
        view.addObject("authorship",authorshipString);
        view.addObject("fileName",fileName);//后期应该是放该name下的所有数据
        view.addObject("distributeData", invokeServices);//存放远程节点信息，包括

        //多语言description
        view.addObject("detailLanguage",detailLanguage);
        view.addObject("itemType","Data");
        view.addObject("languageList",languageList);
        view.addObject("itemInfo",dataItem);
        view.addObject("detail",detailResult);


        return view;

    }


    public JSONArray getRelation(String id) {

        JSONArray result = new JSONArray();
        DataItem dataItem = dataItemDao.findFirstById(id);
        List<String> relatedModels = dataItem.getRelatedModels();

        for (String modelId : relatedModels) {
            ModelItem modelItem = modelItemDao.findFirstById(modelId);
            JSONObject object = new JSONObject();
            object.put("id", modelItem.getId());
            object.put("name", modelItem.getName());
            User user = userDao.findFirstByEmail(modelItem.getAuthor());
            object.put("author", user.getName());
            object.put("author_email", user.getEmail());
            result.add(object);
        }

        return result;
    }


    // TODO 这个方法逻辑有问题
    public String setRelation(String id, List<String> relations) {

        DataItem dataItem = dataItemDao.findFirstById(id);

        List<String> relationDelete = new ArrayList<>();
        for (int i = 0; i < dataItem.getRelatedModels().size(); i++) {
            relationDelete.add(dataItem.getRelatedModels().get(i));
        }
        List<String> relationAdd = new ArrayList<>();
        for (int i = 0; i < relations.size(); i++) {
            relationAdd.add(relations.get(i));
        }

        for (int i = 0; i < relationDelete.size(); i++) {
            for (int j = 0; j < relationAdd.size(); j++) {
                if (relationDelete.get(i).equals(relationAdd.get(j))) {
                    relationDelete.set(i, "");
                    relationAdd.set(j, "");
                    break;
                }
            }
        }

        for (int i = 0; i < relationDelete.size(); i++) {
            String model_id = relationDelete.get(i);
            if (!model_id.equals("")) {
                ModelItem modelItem = modelItemDao.findFirstById(model_id);
                // TODO 为什么Private就可以加进去了
                if(modelItem.getStatus().equals("Private")){
                    relations.add(modelItem.getId());
                    continue;
                }
                if (modelItem.getRelate().getDataItems() != null) {
                    modelItem.getRelate().getDataItems().remove(id);
                    modelItemDao.save(modelItem);
                }
            }
        }

        for (int i = 0; i < relationAdd.size(); i++) {
            String model_id = relationAdd.get(i);
            if (!model_id.equals("")) {
                ModelItem modelItem = modelItemDao.findFirstById(model_id);
                if (modelItem.getRelate().getDataItems() != null) {
                    modelItem.getRelate().getDataItems().add(id);
                } else {
                    List<String> relatedData = new ArrayList<>();
                    relatedData.add(id);
                    modelItem.getRelate().setDataItems(relatedData);
                }
                modelItemDao.save(modelItem);
            }
        }

        dataItem.setRelatedModels(relations);
        dataItemDao.save(dataItem);

        return "suc";

    }

}
