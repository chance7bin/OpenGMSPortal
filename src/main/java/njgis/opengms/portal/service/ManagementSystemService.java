package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.dao.*;
import njgis.opengms.portal.entity.doo.DailyViewCount;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.UserDailyViewCount;
import njgis.opengms.portal.entity.doo.base.PortalItem;
import njgis.opengms.portal.entity.doo.support.ParamInfo;
import njgis.opengms.portal.entity.doo.task.InputData;
import njgis.opengms.portal.entity.doo.task.InputDataChildren;
import njgis.opengms.portal.entity.doo.task.ModelListItem;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.entity.dto.SpecificFindDTO;
import njgis.opengms.portal.entity.dto.task.ResultDataDTO;
import njgis.opengms.portal.entity.dto.task.TaskCheckListDTO;
import njgis.opengms.portal.entity.dto.task.TaskInvokeDTO;
import njgis.opengms.portal.entity.po.*;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.enums.OperationEnum;
import njgis.opengms.portal.utils.ResultUtils;
import njgis.opengms.portal.utils.Utils;
import njgis.opengms.portal.utils.XmlTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description
 * @Author bin
 * @Date 2021/11/09
 */
@Slf4j
@Service
public class ManagementSystemService {

    @Autowired
    TaskService taskService;

    @Autowired
    GenericService genericService;

    @Autowired
    ComputableModelDao computableModelDao;

    @Autowired
    CheckModelListDao checkModelListDao;

    @Autowired
    ComputableModelService computableModelService;

    // @Autowired
    // CheckedModelDao checkedModelDao;

    @Autowired
    UserService userService;

    @Autowired
    UserDao userDao;

    @Autowired
    TaskDao taskDao;

    @Autowired
    DashboardDao dashboardDao;

    @Autowired
    NoticeService noticeService;


    @Autowired
    ModelContainerDao modelContainerDao;

    @Value("${dataServerManager}")
    private String dataServerManager;


    public JsonResult searchDeployedModel(FindDTO findDTO) {

        JSONObject jsonObject = computableModelService.searchDeployedModel(findDTO);
        List<ComputableModel> content = (List<ComputableModel>) jsonObject.get("content");

        //得到查到的结果并更新下计算模型的运行状态
        content = updateTaskStatus(content);
        JSONArray jsonArray = new JSONArray();
        for (ComputableModel computableModel : content) {
            JSONObject o = new JSONObject();
            o.put("id",computableModel.getId());
            o.put("accessId",computableModel.getAccessId());
            o.put("name",computableModel.getName());
            o.put("author", userService.getUserName(computableModel.getAuthor()));
            o.put("createTime",computableModel.getCreateTime());
            o.put("lastModifyTime",computableModel.getLastModifyTime());
            o.put("status",computableModel.getStatus());
            o.put("dailyViewCount",computableModel.getDailyViewCount());
            o.put("md5",computableModel.getMd5());
            o.put("checkedModel",computableModel.getCheckedModel());
            jsonArray.add(o);
        }


        JSONObject result = new JSONObject();
        result.put("total",jsonObject.get("total"));
        result.put("content",jsonArray);

        return ResultUtils.success(result);
    }

    public JsonResult invokeModel(String modelId, String email) {

        ComputableModel computableModel = computableModelDao.findFirstById(modelId);
        CheckedModel checkedModel = computableModel.getCheckedModel();
        checkedModel = checkedModel == null ? new CheckedModel() : checkedModel;
        checkedModel.setHasChecked(true);
        checkedModel.setLastCheckTime(new Date());


        //初始化任务
        JsonResult initTaskRes = taskService.initTask(modelId, email);
        JSONObject initTaskData = (JSONObject)initTaskRes.getData();
        int generateTaskFlag = (int) initTaskData.get("generateTaskFlag");
        if (generateTaskFlag != 1){
            if (generateTaskFlag == -2){
                checkedModel.setOnline(false);
                saveComputableModel(computableModel,checkedModel,"未找到相应服务");
                return ResultUtils.error("未找到相应服务");
            } else {
                // checkedModel.setOnline(true);
                saveComputableModel(computableModel,checkedModel,"初始化任务失败");
                return ResultUtils.error("初始化任务失败");
            }
        }

        //判断是否有测试数据
        JSONObject modelInfo = (JSONObject) initTaskData.get("modelInfo");
        boolean hasTest = (boolean) modelInfo.get("hasTest");
        if (!hasTest){
            checkedModel.setHasTest(false);
            saveComputableModel(computableModel,checkedModel,"未找到测试数据");
            return ResultUtils.error("未找到测试数据");
        }



        //加载数据
        JsonResult loadTestDataRes = taskService.loadTestData(modelId, email);
        if (loadTestDataRes.getCode() != 1){
            saveComputableModel(computableModel,checkedModel,"加载测试数据失败");
            return ResultUtils.error("加载测试数据失败");
        }

        List<ResultDataDTO> loadTestDataData = (List<ResultDataDTO>)loadTestDataRes.getData();

        TaskInvokeDTO taskInvokeDTO;
        try {
            taskInvokeDTO = buildInvokeParams(modelId,initTaskData,loadTestDataData);
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            saveComputableModel(computableModel,checkedModel,"构造输入数据出错");
            return ResultUtils.error("构造输入数据出错");
        }

        JSONObject invokeParams = JSON.parseObject(JSON.toJSONString(taskInvokeDTO));
        JsonResult result = taskService.handleInvoke(invokeParams, email);

        if (result.getCode() != 1){
            saveComputableModel(computableModel,checkedModel,"模型调用失败");
            return ResultUtils.error("模型调用失败");
        }

        JSONObject resultData = (JSONObject) result.getData();
        // 把检查记录存入表中
        checkedModel.setStatus(0);
        checkedModel.getTaskIdList().add(resultData.getString("tid"));
        saveComputableModel(computableModel,checkedModel,"模型调用成功");

        return result;



    }

    //更新模型的检查记录
    public void saveComputableModel(ComputableModel computableModel, CheckedModel checkedModel, String msg){
        checkedModel.setMsg(msg);
        computableModel.setCheckedModel(checkedModel);
        computableModelDao.save(computableModel);
    }



    //构造调用invoke接口的参数 先构造测试数据，再根据测试数据匹配模型的输入参数
    public TaskInvokeDTO buildInvokeParams(String modelId, JSONObject initTaskData, List<ResultDataDTO> loadTestDataData){

        JSONObject modelInfo = (JSONObject) initTaskData.get("modelInfo");
        TaskInvokeDTO taskInvokeDTO = new TaskInvokeDTO();
        taskInvokeDTO.setOid(modelId);
        JSONObject taskInfo = (JSONObject) initTaskData.get("taskInfo");
        taskInvokeDTO.setIp(taskInfo.getString("ip"));
        taskInvokeDTO.setPort(taskInfo.getString("port"));
        taskInvokeDTO.setPid(taskInfo.getString("pid"));

        //从initTaskData拿值
        JSONArray states =  modelInfo.getJSONArray("states");
        //从loadTestDataData拿值
        List<InputData> inputs = taskInvokeDTO.getInputs();
        for (Object o : loadTestDataData) {
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(o));
            ResultDataDTO testDataData = jsonObject.toJavaObject(ResultDataDTO.class);
            InputData input = new InputData();
            input.setEvent(testDataData.getEvent());
            input.setTag(testDataData.getTag());
            input.setUrl(testDataData.getUrl());
            input.setSuffix(testDataData.getSuffix());

            List<ParamInfo> children = testDataData.getChildren();
            //匹配initTask和loadTestData的state
            for (int j = 0; j < states.size(); j++) {
                JSONObject state = states.getJSONObject(j);
                String stateId = state.getString("Id");
                if (stateId.equals(testDataData.getStateId())){
                    input.setStatename(state.getString("name"));

                    JSONArray events = state.getJSONArray("event");
                    for (int k = 0; k < events.size(); k++) {
                        JSONObject event = events.getJSONObject(k);
                        String eventName = event.getString("eventName");
                        if (eventName.equals(input.getEvent())){
                            JSONArray data = event.getJSONArray("data");
                            // log.info(String.valueOf(data));
                            JSONObject data0 = data.getJSONObject(0);
                            String externalId = data0.getString("externalId");
                            JSONArray nodes = data0.getJSONArray("nodes");

                            if (externalId != null){
                                input.setTemplateId(externalId.toLowerCase(Locale.ROOT));
                            }

                            if (nodes != null && children != null && children.size() > 0) {
                                List<InputDataChildren> inputChildren = new ArrayList<>();
                                for (int i = 0; i < children.size(); i++) {
                                    InputDataChildren inputDataChildren = new InputDataChildren();

                                    ParamInfo paramInfo = children.get(i);
                                    inputDataChildren.setValue(paramInfo.getValue());

                                    JSONObject node = nodes.getJSONObject(i);
                                    inputDataChildren.setEventId(node.getString("text"));
                                    inputDataChildren.setEventName(node.getString("text"));
                                    inputDataChildren.setEventDesc(node.getString("desc"));
                                    inputDataChildren.setEventType(node.getString("dataType"));
                                    inputDataChildren.setChild(true);

                                    inputChildren.add(inputDataChildren);
                                }
                                input.setChildren(inputChildren);
                            }
                        }


                    }

                }
            }

            inputs.add(input);
        }


        return taskInvokeDTO;

    }


    public List<ComputableModel> updateTaskStatus(List<ComputableModel> content) {

        List<CheckedModel> modelList = new ArrayList<>();
        for (ComputableModel model : content) {
            CheckedModel cm = model.getCheckedModel();
            if (cm != null){
                if (cm.isHasChecked() && (cm.getStatus() == 0 || cm.getStatus() == 1)){
                    modelList.add(cm);
                }
            }
        }

        // List<Integer> status = Arrays.asList(0, 1);
        // List<CheckedModel> modelList = checkedModelDao.findAllByStatusIn(status);
        List<String> ids = new ArrayList<>();
        for (CheckedModel model : modelList) {
            List<String> taskIdList = model.getTaskIdList();
            int size = taskIdList.size();
            ids.add(taskIdList.get(size - 1));
        }

        List<Task> ts = taskDao.findAllByTaskIdIn(ids);
        List<Task> newTasks = taskService.updateUserTasks(ts);

        for(Task newTask : newTasks){
            for(ComputableModel computableModel:content){
                CheckedModel model = computableModel.getCheckedModel();
                if (model != null){
                    List<String> taskIdList = model.getTaskIdList();
                    int size = taskIdList.size();
                    if(newTask.getTaskId().equals(taskIdList.get(size - 1))){
                        // 更新model中checkedModel属性的状态，并保存
                        model.setStatus(newTask.getStatus());
                        computableModel.setCheckedModel(model);
                        computableModelDao.save(computableModel);
                        break;
                    }
                }
            }
        }


        return content;

    }


    public JsonResult saveCheckedList(TaskCheckListDTO taskCheckListDTO, String email){

        try {
            CheckModelList checkModelList = new CheckModelList();
            checkModelList.setOperator(email);
            checkModelList.setDraftName(taskCheckListDTO.getDraftName());
            List<ModelListItem> list = checkModelList.getModelList();
            List<String> modelList = taskCheckListDTO.getModelList();
            for (String model : modelList) {
                ModelListItem modelListItem = new ModelListItem();
                ComputableModel cm = computableModelDao.findFirstById(model);
                modelListItem.setId(model);
                modelListItem.setName(cm.getName());
                User user = userDao.findFirstByEmail(cm.getAuthor());
                modelListItem.setAuthor(user.getName());
                list.add(modelListItem);
            }
            checkModelList.setModelList(list);

            checkModelListDao.save(checkModelList);
        }catch (Exception e){
            return ResultUtils.error("save error");
        }
        return ResultUtils.success();


    }


    public JsonResult searchCheckedList(FindDTO findDTO){
        try {
            Pageable pageable = genericService.getPageable(findDTO);
            Page<CheckModelList> allByDraftName = checkModelListDao.findAllByDraftNameContainsIgnoreCase(findDTO.getSearchText(), pageable);
            return ResultUtils.success(allByDraftName);
        }catch (Exception e){
            return ResultUtils.error("find error");
        }

    }

    public JsonResult deleteCheckedList(String id){

        try {
            checkModelListDao.deleteById(id);
            return ResultUtils.success();
        }catch (Exception e){
            return ResultUtils.error("delete error");
        }
    }



    public JsonResult getUserList(FindDTO findDTO){

        Pageable pageable = genericService.getPageable(findDTO);
        Page<User> users = userDao.findAllByNameContainsIgnoreCase(findDTO.getSearchText(), pageable);

        return ResultUtils.success(users);
    }



    public JSONObject getItemInfo(ItemTypeEnum itemType, SpecificFindDTO specificFindDTO){
        JSONObject jsonObject = genericService.searchDBItems(specificFindDTO, itemType);

        // 如果有字段需要筛选的话再加

        JSONArray itemList = new JSONArray();
        JSONArray allPortalItem = jsonObject.getJSONArray("allPortalItem");
        for (int i = 0; i < allPortalItem.size(); i++) {
            JSONObject object = allPortalItem.getJSONObject(i);
            JSONObject item = new JSONObject();
            item.put("id", object.get("id"));
            item.put("name", object.get("name"));
            item.put("createTime", object.get("id"));
            item.put("lastModifyTime", object.get("id"));
            item.put("status", object.get("id"));
            item.put("viewCount", object.get("id"));
            item.put("author", userService.getUserName(object.getString("author")));
            itemList.add(item);
        }
        JSONObject result = new JSONObject();
        result.put("totalElements", jsonObject.getIntValue("totalElements"));
        result.put("itemList",itemList);

        return result;
    }



    public JsonResult addAdmin(ItemTypeEnum itemType, String itemId, List<String> userList){

        JSONObject factory = genericService.daoFactory(itemType);
        GenericItemDao itemDao = (GenericItemDao)factory.get("itemDao");
        PortalItem item = genericService.getById(itemId, itemDao);
        List<String> admins = item.getAdmins();
        if (admins == null)
            admins = new ArrayList<>();
        for (String user : userList) {
            if (!admins.contains(user))
                admins.add(user);
        }
        item.setAdmins(admins);
        item.setLastModifyTime(new Date());

        genericService.saveItem(item,itemDao);

        return ResultUtils.success();
    }


    public JsonResult setItemStatus(ItemTypeEnum itemType, String itemId, String status,String email){
        JSONObject factory = genericService.daoFactory(itemType);
        GenericItemDao itemDao = (GenericItemDao)factory.get("itemDao");
        PortalItem item = genericService.getById(itemId, itemDao);
        item.setStatus(status);
        genericService.saveItem(item, itemDao);
        //发送通知
        List<String> recipientList = Arrays.asList(item.getAuthor());
        recipientList = noticeService.addItemAdmins(recipientList,item.getAdmins());

        // notice的附加信息
        String remark = "edited " + item.getName() + "'s status to" + item.getStatus();

        noticeService.sendNoticeContains(email, OperationEnum.Inform,item.getId(),recipientList, remark);
        return ResultUtils.success();
    }


    public long getItemCount(ItemTypeEnum itemType){
        JSONObject factory = genericService.daoFactory(itemType);
        GenericItemDao itemDao = (GenericItemDao)factory.get("itemDao");
        return itemDao.count();
    }

    public JsonResult getAllItemCount(){

        // 统计的条目类型
        ItemTypeEnum[] itemTypeEnums = {
            ItemTypeEnum.ModelItem,
            ItemTypeEnum.DataItem,
            ItemTypeEnum.DataHub,
            ItemTypeEnum.DataHub,
            ItemTypeEnum.Concept,
            ItemTypeEnum.SpatialReference,
            ItemTypeEnum.Template,
            ItemTypeEnum.Unit
        };

        JSONArray result = new JSONArray();
        for (ItemTypeEnum itemType : itemTypeEnums) {
            JSONObject factory = genericService.daoFactory(itemType);
            GenericItemDao itemDao = (GenericItemDao)factory.get("itemDao");
            long count = itemDao.count();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type",itemType);
            jsonObject.put("count",count);
            result.add(jsonObject);
        }


        return ResultUtils.success(result);
    }


    //得到仪表板的数据
    public JsonResult getDashboardInfo(){

        JSONObject result = new JSONObject();

        // 模型数量

        // 访问数量

        // 用户数量

        return ResultUtils.success(result);
    }




    // 记录访问页面的数量
    public void recordViewCount(){
        DashBoard dashboard = dashboardDao.findFirstByName("dashboard");
        List<DailyViewCount> dailyViewCount = dashboard.getDailyViewCount();
        dashboard.setDailyViewCount(genericService.recordViewCountByField(dailyViewCount));
        dashboardDao.save(dashboard);
    }


    // 页面访问量
    public JSONArray getPageViewCount(){
        DashBoard dashboard = dashboardDao.findFirstByName("dashboard");

        List<DailyViewCount> dailyViewCount = dashboard.getDailyViewCount();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        JSONArray dailyViewArr = new JSONArray();
        for (DailyViewCount count : dailyViewCount) {
            JSONObject o = new JSONObject();
            o.put("date",sdf.format(count.getDate()));
            o.put("count",count.getCount());
            dailyViewArr.add(o);
        }

        return dailyViewArr;

    }


    // 记录用户访问的数量
    public void recordUserViewCount(String ip){
        DashBoard dashboard = dashboardDao.findFirstByName("dashboard");
        List<UserDailyViewCount> dailyViewCountList = dashboard.getUserDailyViewCount();

        Date now = new Date();
        UserDailyViewCount newViewCount = new UserDailyViewCount(now, 1, Arrays.asList(ip));
        if (dailyViewCountList == null) {
            List<UserDailyViewCount> newList = new ArrayList<>();
            newList.add(newViewCount);
            dailyViewCountList = newList;
        } else if (dailyViewCountList.size() > 0) {
            UserDailyViewCount dailyViewCount = dailyViewCountList.get(dailyViewCountList.size() - 1);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if (sdf.format(dailyViewCount.getDate()).equals(sdf.format(now))) {

                List<String> ipList = dailyViewCount.getIpList();
                if (!ipList.contains(ip)){
                    ipList.add(ip);
                }

                dailyViewCount.setCount(ipList.size());
                dailyViewCountList.set(dailyViewCountList.size() - 1, dailyViewCount);
            } else {
                dailyViewCountList.add(newViewCount);
            }
        } else {
            dailyViewCountList.add(newViewCount);
        }

        dashboard.setUserDailyViewCount(dailyViewCountList);
        dashboardDao.save(dashboard);
    }

    //得到用户的访问量
    public JSONArray getUserViewCount(){
        DashBoard dashboard = dashboardDao.findFirstByName("dashboard");
        List<UserDailyViewCount> userDailyViewCount = dashboard.getUserDailyViewCount();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        JSONArray dailyViewArr = new JSONArray();
        for (UserDailyViewCount count : userDailyViewCount) {
            JSONObject o = new JSONObject();
            o.put("date",sdf.format(count.getDate()));
            o.put("count",count.getCount());
            dailyViewArr.add(o);
        }
        return dailyViewArr;
    }

    // 获取用户数量
    public long getUserCount(){
        return userDao.count();
    }


    //获取所有模型\数据容器
    public JsonResult getAllServerNodes(){

        JSONArray nodes = new JSONArray();
        //模型容器节点
        List<ModelContainer> modelContainerList = modelContainerDao.findAll();
        for(ModelContainer modelContainer : modelContainerList){
            JSONObject node = new JSONObject();
            node.put("geoJson",modelContainer.getGeoInfo());
            node.put("type","model");
            nodes.add(node);
        }

        //数据容器节点
        try {
            HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
            httpRequestFactory.setConnectionRequestTimeout(6000);
            httpRequestFactory.setConnectTimeout(6000);
            httpRequestFactory.setReadTimeout(6000);

            String url = "http://" + dataServerManager + "/onlineNodes";

            RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
            String xml = null;
            try {
                xml = restTemplate.getForObject(url, String.class);
            } catch (Exception e) {

            }

            if (xml.equals("err")) {
                xml = null;
            }

            if(xml!=null) {

                JSONObject jsonObject = XmlTool.xml2Json(xml);
                JSONObject jsonNodeInfo = jsonObject.getJSONObject("serviceNodes");



                try {
                    JSONArray dataNodes = jsonNodeInfo.getJSONArray("onlineServiceNode");

                    for (int i = 0; i < dataNodes.size(); i++) {
                        JSONObject dataNode = (JSONObject) dataNodes.get(i);
                        JSONObject node = new JSONObject();
                        node.put("geoJson", Utils.getGeoInfoMeta(dataNode.getString("ip")));
                        node.put("type","data");
                        nodes.add(node);
                    }
                } catch (Exception e) {
                    JSONObject dataNode = jsonNodeInfo.getJSONObject("onlineServiceNode");
                    JSONObject node = new JSONObject();
                    node.put("geoJson",Utils.getGeoInfoMeta(dataNode.getString("ip")));
                    node.put("type","data");
                    nodes.add(node);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return ResultUtils.success(nodes);
    }


}
