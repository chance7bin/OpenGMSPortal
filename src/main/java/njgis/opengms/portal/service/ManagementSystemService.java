package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.dao.*;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.UserDailyViewCount;
import njgis.opengms.portal.entity.doo.base.PortalItem;
import njgis.opengms.portal.entity.doo.support.DailyViewCount;
import njgis.opengms.portal.entity.doo.support.ParamInfo;
import njgis.opengms.portal.entity.doo.support.TaskData;
import njgis.opengms.portal.entity.doo.task.CheckedHistory;
import njgis.opengms.portal.entity.doo.task.CheckedModel;
import njgis.opengms.portal.entity.doo.task.InputData;
import njgis.opengms.portal.entity.doo.task.InputDataChildren;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.entity.dto.SpecificFindDTO;
import njgis.opengms.portal.entity.dto.task.ResultDataDTO;
import njgis.opengms.portal.entity.dto.task.TaskInvokeDTO;
import njgis.opengms.portal.entity.po.*;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.enums.OperationEnum;
import njgis.opengms.portal.enums.ResultEnum;
import njgis.opengms.portal.utils.ResultUtils;
import njgis.opengms.portal.utils.Utils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;
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

    @Resource(name="taskCollection")
    MongoCollection<Document> taskCollection;

    @Resource(name="serverCollection")
    MongoCollection<Document> serverCollection;

    @Autowired
    CommentDao commentDao;

    @Autowired
    RedisService redisService;


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
            o.put("authorEmail", computableModel.getAuthor());
            o.put("createTime",Utils.dateFormat(computableModel.getCreateTime()));
            o.put("viewCount",computableModel.getViewCount());
            o.put("lastModifyTime",computableModel.getLastModifyTime());
            o.put("status",computableModel.getStatus());
            o.put("dailyViewCount",computableModel.getDailyViewCount());
            o.put("md5",computableModel.getMd5());
            o.put("checkedModel",computableModel.getCheckedModel());

            //得到 部署这个模型的模型容器地址
            List<String> container = getModelContainerListByMd5(computableModel.getMd5());
            o.put("deployedMSR",container);

            jsonArray.add(o);
        }


        JSONObject result = new JSONObject();
        result.put("total",jsonObject.get("total"));
        result.put("content",jsonArray);

        return ResultUtils.success(result);
    }


    /**
     * invokeModel方法不管调用成功还是失败，都会把本次调用的情况存在checkedModel
     * 调用成功才会有taskId，调用失败的话错误信息在CheckedModel的msg里
     * @param modelId 调用模型id
     * @param email 调用者email
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult invokeModel(String modelId, String email) {

        ComputableModel computableModel = computableModelDao.findFirstById(modelId);
        if (computableModel == null){
            return ResultUtils.error("未找到相应计算模型");
        }
        CheckedModel checkedModel = computableModel.getCheckedModel();
        checkedModel = checkedModel == null ? new CheckedModel() : checkedModel;
        //重置任务状态
        checkedModel.setHasChecked(true);
        checkedModel.setLastCheckTime(new Date());
        checkedModel.setHasInvokeSuccess(false);


        //初始化任务
        JsonResult initTaskRes = taskService.initTask(modelId, email);
        JSONObject initTaskData = (JSONObject)initTaskRes.getData();
        int generateTaskFlag = (int) initTaskData.get("generateTaskFlag");
        if (generateTaskFlag != ResultEnum.SUCCESS.getCode()){
            if (generateTaskFlag == -2){
                checkedModel.setOnline(false);
                checkedModel.setStatus(-1);
                saveComputableModel(computableModel,checkedModel,"未找到相应服务");
                return ResultUtils.error("未找到相应服务");
            } else {
                // checkedModel.setOnline(true);
                checkedModel.setStatus(-1);
                saveComputableModel(computableModel,checkedModel,"初始化任务失败");
                return ResultUtils.error("初始化任务失败");
            }
        }

        // 调用模型所需的参数
        JSONObject invokeParams = null;

        //判断是否有测试数据
        JSONObject modelInfo = (JSONObject) initTaskData.get("modelInfo");
        boolean hasTest = (boolean) modelInfo.get("hasTest");
        if (hasTest){
            //如果modelInfo里的hasTest为true，就可以直接进行输入数据的构造了
            checkedModel.setHasTest(true);

            //加载数据
            JsonResult loadTestDataRes = taskService.loadTestData(modelId, email);
            if (loadTestDataRes.getCode() != ResultEnum.SUCCESS.getCode()){
                checkedModel.setStatus(-1);
                saveComputableModel(computableModel,checkedModel,"加载测试数据失败");
                return ResultUtils.error("加载测试数据失败");
            }

            List<ResultDataDTO> loadTestDataData = (List<ResultDataDTO>)loadTestDataRes.getData();

            TaskInvokeDTO taskInvokeDTO;
            try {
                taskInvokeDTO = buildInvokeParams(modelId,initTaskData,loadTestDataData);
            }catch (Exception e){
                // log.error(e.getMessage());
                // e.printStackTrace();
                log.error(e.getMessage());
                checkedModel.setStatus(-1);
                saveComputableModel(computableModel,checkedModel,"构造输入数据出错");
                return ResultUtils.error("构造输入数据出错");
            }

            invokeParams = JSON.parseObject(JSON.toJSONString(taskInvokeDTO));

        }
        else {
            //hasTest为false的话，去task表里面找最先执行成功的那个inputs作为输入参数，没有的话才真的没有测试数据
            FindDTO findDTO = new FindDTO();
            findDTO.setSortField("runTime");
            findDTO.setAsc(true);
            findDTO.setPage(1);
            findDTO.setPageSize(4);
            Pageable pageable = genericService.getPageable(findDTO);
            //获取published task
            Page<Task> tasks = taskDao.findByComputableIdAndPermissionAndStatus(modelId, "public", 2, pageable);
            List<Task> ts = tasks.getContent();

            if (ts.size() != 0){

                checkedModel.setHasTest(true);

                // 取索引第一个， 构造inputdata
                List<TaskData> inputs = ts.get(0).getInputs();
                // TaskData -> InputData
                List<InputData> inputDataList = new ArrayList<>();
                for (TaskData input : inputs) {
                    InputData inputData = new InputData();
                    BeanUtils.copyProperties(input,inputData);
                    inputDataList.add(inputData);
                }

                TaskInvokeDTO taskInvoke = new TaskInvokeDTO();
                taskInvoke.setOid(modelId);
                JSONObject taskInfo = (JSONObject) initTaskData.get("taskInfo");
                taskInvoke.setIp(taskInfo.getString("ip"));
                taskInvoke.setPort(taskInfo.getString("port"));
                taskInvoke.setPid(taskInfo.getString("pid"));
                taskInvoke.setInputs(inputDataList);

                invokeParams = JSON.parseObject(JSON.toJSONString(taskInvoke));
            } else {
                checkedModel.setHasTest(false);
                checkedModel.setStatus(-1);
                saveComputableModel(computableModel,checkedModel,"未找到测试数据");
                return ResultUtils.error("未找到测试数据");
            }
        }




        JsonResult result = taskService.handleInvoke(invokeParams, email);

        if (result.getCode() != ResultEnum.SUCCESS.getCode()){
            checkedModel.setStatus(-1);
            saveComputableModel(computableModel,checkedModel,"模型调用失败");
            return ResultUtils.error("模型调用失败");
        }

        JSONObject resultData = (JSONObject) result.getData();
        // 把检查记录存入表中
        checkedModel.setHasInvokeSuccess(true);
        checkedModel.setStatus(0);
        checkedModel.setInvokeEmail(email);
        checkedModel.getTaskIdList().add(resultData.getString("tid"));
        // checkedModel.setMsrAddress(getModelContainerByTaskId(resultData.getString("tid")));
        saveComputableModel(computableModel,checkedModel,"模型调用成功");


        return result;

    }



    // 批量调用模型
    public JsonResult invokeModelBatch(List<String> modelIdList, String email) {

        List<CheckedHistory> checkedHistoryList = new ArrayList<>();

        for (String modelId : modelIdList) {

            // 记录检查的记录
            CheckedHistory checkedHistory = new CheckedHistory();

            //调用模型
            JsonResult invokeResult = invokeModel(modelId, email);

            ComputableModel model = computableModelDao.findFirstById(modelId);
            CheckedModel checkedModel = model.getCheckedModel();
            BeanUtils.copyProperties(checkedModel,checkedHistory,"taskIdList");
            checkedHistory.setModelId(modelId);
            checkedHistory.setModelName(model.getName());
            User user = userDao.findFirstByEmail(model.getAuthor());
            checkedHistory.setAuthor(user.getName());
            if (invokeResult.getCode() == ResultEnum.SUCCESS.getCode()){
                List<String> taskIdList = checkedModel.getTaskIdList();
                int size = taskIdList.size();
                String taskId = taskIdList.get(size - 1);
                checkedHistory.setTaskId(taskId);
                checkedHistory.setMsrAddress(getModelContainerByTaskId(taskId));
                checkedHistory.setMsrid(getMsridByTaskId(taskId));
            } else {
                checkedHistory.setStatus(-1);
                checkedHistory.setMsg(invokeResult.getMsg());
            }

            checkedHistoryList.add(checkedHistory);

        }

        JsonResult save = saveCheckedList(checkedHistoryList, email);
        if (save.getCode() != ResultEnum.SUCCESS.getCode())
            return save;

        return ResultUtils.success(checkedHistoryList);

    }


    //更新模型的检查记录
    public void saveComputableModel(ComputableModel computableModel, CheckedModel checkedModel, String msg){
        checkedModel.setMsg(msg);
        computableModel.setCheckedModel(checkedModel);
        // computableModelDao.save(computableModel);
        redisService.saveItem(computableModel, ItemTypeEnum.ComputableModel);
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



    //更新ComputableModel的运行状态
    public List<ComputableModel> updateTaskStatus(List<ComputableModel> content) {

        List<CheckedModel> modelList = new ArrayList<>();
        for (ComputableModel model : content) {
            CheckedModel cm = model.getCheckedModel();
            if (cm != null){
                if (cm.isHasInvokeSuccess() && (cm.getStatus() == 0 || cm.getStatus() == 1)){
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

        updateCMRunStatusByTask(content, newTasks);

        //这边还存在checkmodel和task两张表不同步的情况，还要再遍历一遍
        if (newTasks.size() == 0){
            updateCMRunStatusByTask(content, ts);
        }




        return content;

    }

    //通过task表的记录更新computable model里的记录
    public void updateCMRunStatusByTask(List<ComputableModel> content, List<Task> ts) {
        for(Task t : ts){
            for(ComputableModel computableModel:content){
                if (t.getComputableId().equals(computableModel.getId())){
                    CheckedModel model = computableModel.getCheckedModel();
                    if (model != null){
                        List<String> taskIdList = model.getTaskIdList();
                        int size = taskIdList.size();
                        if (size == 0){continue;}
                        if(t.getTaskId().equals(taskIdList.get(size - 1))){
                            // 更新model中checkedModel属性的状态，并保存
                            model.setStatus(t.getStatus());
                            model.setMsg(changeTaskMsgByStatus(t.getStatus()));
                            //status = -1 时有两种显示情况，未找到测试数据要单独判断（是否需要）

                            computableModel.setCheckedModel(model);
                            // computableModelDao.save(computableModel);
                            redisService.saveItem(computableModel, ItemTypeEnum.ComputableModel);
                            break;
                        }
                    }
                }

            }
        }
    }

    private String changeTaskMsgByStatus(int status){

        String msg = "";
        switch (status){
            case -1:{
                msg = "模型执行失败";
                break;
            }
            case 0:{
                msg = "正在排队检测";
                break;
            }
            case 1:{
                msg = "正在检测";
                break;
            }
            case 2:{
                msg = "正常";
                break;
            }
            default:{
                msg = "未知状态";
                break;
            }
        }
        return msg;


    }


    public JsonResult saveCheckedList(List<CheckedHistory> checkedHistoryList, String email){

        try {
            CheckModelList checkModelList = new CheckModelList();
            checkModelList.setOperator(email);
            checkModelList.setDraftName(Utils.dateFormat(new Date()));

            checkModelList.setHistoryList(checkedHistoryList);

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
            List<CheckModelList> content = allByDraftName.getContent();
            List<CheckModelList> resultList = new ArrayList<>();

            //废弃 没考虑到task已经在查询computableModel的时候就更新完状态了
            // for (CheckModelList checkModelList : content) {
            //     // 更新历史记录的模型运行状态
            //     CheckModelList cml = updateHistoryStatus(checkModelList);
            //     resultList.add(cml);
            // }

            //根据taskid更新history
            List<String> taskIdList = new ArrayList<>();
            for (CheckModelList checkModelList : content) {
                List<CheckedHistory> historyList = checkModelList.getHistoryList();
                for (CheckedHistory history : historyList) {
                    //状态记录为0以及1才要更新
                    if (history.getStatus() == -1 || history.getStatus() == 2){
                        continue;
                    }
                    taskIdList.add(history.getTaskId());
                }
            }

            List<Task> ts = taskDao.findAllByTaskIdIn(taskIdList);
            List<Task> newTasks = taskService.updateUserTasks(ts);

            updateHistoryByTask(content, newTasks);

            if (newTasks.size() == 0){
                updateHistoryByTask(content, ts);
            }

            JSONObject result = new JSONObject();
            result.put("total",allByDraftName.getTotalElements());
            result.put("content",content);

            return ResultUtils.success(result);
        }catch (Exception e){
            return ResultUtils.error("find error");
        }

    }

    //通过task表的记录更新History里的记录
    public void updateHistoryByTask(List<CheckModelList> checkModelLists, List<Task> ts) {

        for (CheckModelList checkModelList : checkModelLists) {

            List<CheckedHistory> historyList = checkModelList.getHistoryList();

            for (CheckedHistory history : historyList) {

                for (Task t : ts) {

                    if (t.getTaskId().equals(history.getTaskId())){
                        history.setStatus(t.getStatus());
                        history.setMsg(changeTaskMsgByStatus(t.getStatus()));
                        history.setMsrid(getMsridByTaskId(t.getTaskId()));
                        break;
                    }

                }

            }

            // checkModelList.setHistoryList(historyList);
            checkModelListDao.save(checkModelList);

        }
    }


    // 更新模型检查历史记录的状态
    public CheckModelList updateHistoryStatus(CheckModelList checkModelList){

        List<CheckedHistory> historyList = checkModelList.getHistoryList();

        //需要更新的taskId列表
        List<String> ids = new ArrayList<>();
        for (CheckedHistory history : historyList) {
            if (history.isHasInvokeSuccess() && (history.getStatus() == 0 || history.getStatus() == 1)){
                ids.add(history.getTaskId());
            }
        }

        List<Task> ts = taskDao.findAllByTaskIdIn(ids);
        List<Task> newTasks = taskService.updateUserTasks(ts);

        for(Task newTask : newTasks){
            for(CheckedHistory h:historyList){
                if(newTask.getTaskId().equals(h.getTaskId())){
                    // 更新model中checkedModel属性的状态，并保存
                    h.setStatus(newTask.getStatus());
                    h.setMsg(changeTaskMsgByStatus(newTask.getStatus()));
                    break;
                }
            }
        }

        checkModelList.setHistoryList(historyList);
        checkModelListDao.save(checkModelList);


        //


        return checkModelList;

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
        Page<User> users = userDao.findAllByEmailContainsIgnoreCase(findDTO.getSearchText(), pageable);

        return ResultUtils.success(users);
    }

    @Value(value = "Public,Discoverable,Private")
    private List<String> allVisible;

    public JSONObject getItemInfo(ItemTypeEnum itemType, SpecificFindDTO specificFindDTO){
        JSONObject jsonObject = genericService.searchDBItems(specificFindDTO, itemType, allVisible);

        // 如果有字段需要筛选的话再加

        JSONArray itemList = new JSONArray();
        JSONArray allPortalItem = jsonObject.getJSONArray("allPortalItem");
        for (int i = 0; i < allPortalItem.size(); i++) {
            JSONObject object = allPortalItem.getJSONObject(i);
            JSONObject item = new JSONObject();
            item.put("id", object.get("id"));
            item.put("name", object.get("name"));
            item.put("createTime", object.get("createTime"));
            item.put("lastModifyTime", object.get("lastModifyTime"));
            item.put("status", object.get("status"));
            item.put("viewCount", object.get("viewCount"));
            List<JSONObject> adminsList = new ArrayList<>();
            List<String> adminsEmailList = (List<String>) object.get("admins");
            if (adminsEmailList != null){
                for (String email : adminsEmailList) {
                    JSONObject admin = new JSONObject();
                    admin.put("name",userService.getUserName(email));
                    admin.put("email",email);
                    adminsList.add(admin);
                }
            }
            item.put("admins", adminsList);
            item.put("author", userService.getUserName(object.getString("author")));
            item.put("authorEmail", object.getString("author"));
            if (object.get("invokeCount") != null){
                item.put("invokeCount", object.get("invokeCount"));
            }
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
        List<String> recipientList = new ArrayList<>(Arrays.asList(item.getAuthor()));
        recipientList = noticeService.addItemAdmins(recipientList,item.getAdmins());

        // notice的附加信息 通知类型为Information时构造msg时使用
        String remark = "edited " + item.getName() + "'s status to " + item.getStatus();

        noticeService.sendNoticeContains(email, OperationEnum.Inform,ItemTypeEnum.Information,item,recipientList, remark);
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
            ItemTypeEnum.DataMethod,
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
    @Async
    public void recordUserViewCount(String ip){
        // log.info("recordUserViewCount");
        DashBoard dashboard = dashboardDao.findFirstByName("dashboard");
        List<UserDailyViewCount> dailyViewCountList = dashboard.getUserDailyViewCount();

        Date now = new Date();
        UserDailyViewCount newViewCount = new UserDailyViewCount(now, 1, new ArrayList<>(Arrays.asList(ip)));
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




    /**
     * 根据taskId得到运行该任务的模型服务容器地址
     * @param taskId
     * @return java.lang.String 模型服务容器地址 ip:port
     * @Author bin
     **/
    public String getModelContainerByTaskId(String taskId){

        //先根据taskId到task表找，得到模型容器的serverId
        Document filter = new Document();
        filter.append("_id", new ObjectId(taskId));
        FindIterable<Document> taskRes = taskCollection.find(filter);

        String serverId = null;
        for (Document datum : taskRes) {
            //对应TaskServer中server表的id
            serverId = datum.getString("t_server");
        }


        if (serverId == null){
            return "";
        }
        //拿到serverId之后再到server表找MSRAddress
        Document filter2 = new Document();
        filter2.append("_id", new ObjectId(serverId));
        FindIterable<Document> serverRes = serverCollection.find(filter2);

        String MSRAddress = null;
        for (Document server : serverRes) {
            MSRAddress = server.getString("s_ip") + ":" + server.getInteger("s_port");
        }

        return MSRAddress == null ? "" : MSRAddress;

    }

    /**
     * 根据taskId得到运行该任务的模型运行实例id
     * @param taskId
     * @return java.lang.String
     * @Author bin
     **/
    public String getMsridByTaskId(String taskId){
        //先根据taskId到task表找，得到模型容器的serverId
        Document filter = new Document();
        filter.append("_id", new ObjectId(taskId));
        FindIterable<Document> taskRes = taskCollection.find(filter);

        String msrid = null;
        for (Document datum : taskRes) {
            //对应TaskServer中server表的id
            msrid = datum.getString("t_msrid");
        }


        return msrid;
    }


    /**
     * 根据计算模型的md5得到部署该模型的模型服务容器列表
     * @param md5
     * @return java.util.List<java.lang.String>
     * @Author bin
     **/
    public List<String> getModelContainerListByMd5(String md5){

        Document filter = new Document();
        filter.append("s_services", md5);
        FindIterable<Document> serverRes = serverCollection.find(filter);

        List<String> MSRAddress = new ArrayList<>();
        for (Document server : serverRes) {
            MSRAddress.add(server.getString("s_ip") + ":" + server.getInteger("s_port"));
        }

        return MSRAddress;

    }


    /**
     * 根据md5得到计算模型的名字
     * @param md5
     * @return java.lang.String
     * @Author bin
     **/
    public String getComputableModelNameByMd5(String md5){

        ComputableModel model = computableModelDao.findFirstByMd5(md5);
        if (model==null)
            return "";
        return model.getName();
    }


    public JSONObject getTaskList(FindDTO findDTO){

        // 查询的时候从第几个开始查
        int skipIndex = (findDTO.getPage() - 1) * findDTO.getPageSize();

        // 排序顺序
        Bson sort = findDTO.getAsc() ? Sorts.ascending("t_datetime") : Sorts.descending("t_datetime");

        FindIterable<Document> result = taskCollection
            .find()
            .sort(Sorts.orderBy(sort))
            .skip(skipIndex)
            .limit(findDTO.getPageSize());

        long total = taskCollection.countDocuments();
        List<JSONObject> jsonObject = new ArrayList<>();
        for (Document document : result) {
            // JSONObject o = JSONObject.parseObject(JSON.toJSONString(document));
            JSONObject object = new JSONObject();
            object.put("msrid",document.getString("t_msrid"));
            User t_user = userService.getByEmail(document.getString("t_user"));
            if (t_user != null)
                object.put("user",t_user.getName());
            else
                object.put("user",document.getString("t_user"));
            object.put("status",document.getString("t_status"));
            object.put("date",Utils.dateFormat(document.getDate("t_datetime")));
            object.put("server",document.getString("t_server"));
            object.put("md5",document.getString("t_pid"));
            object.put("computableModelName",getComputableModelNameByMd5(document.getString("t_pid")));
            object.put("runServer",getModelContainerByTaskId(String.valueOf(document.get("_id"))));
            object.put("deployedServer",getModelContainerListByMd5(document.getString("t_pid")));

            jsonObject.add(object);
        }

        JSONObject returnRes = new JSONObject();
        returnRes.put("list",jsonObject);
        returnRes.put("total",total);


        return returnRes;
    }


    public List<JSONObject> getMscList(){


        FindIterable<Document> result = serverCollection.find();

        List<JSONObject> list = new ArrayList<>();

        for (Document document : result) {
            JSONObject object = new JSONObject();
            object.put("ip",document.getString("s_ip"));
            object.put("port",document.getInteger("s_port"));
            object.put("type",document.getInteger("s_type"));
            object.put("status",document.getBoolean("s_status"));
            list.add(object);
        }

        return list;
    }

    public JSONObject getCommentList(FindDTO findDTO) {

        Pageable pageable = genericService.getPageable(findDTO);

        Page<Comment> comments = commentDao.findAll(pageable);

        JSONObject result = new JSONObject();

        List<Comment> content = comments.getContent();
        List<JSONObject> contentList = new ArrayList<>();
        for (Comment comment : content) {

            JSONObject o = (JSONObject)JSONObject.toJSON(comment);
            JSONObject daoFactory = genericService.daoFactory(comment.getRelateItemType());
            GenericItemDao itemDao = (GenericItemDao) daoFactory.get("itemDao");
            PortalItem item = (PortalItem)itemDao.findFirstById(comment.getRelateItemId());
            if (item == null){
                o.put("itemName", "条目已删除");
            } else {
                o.put("itemName", item.getName());
            }
            o.put("commentUserName", userService.getUserName(comment.getCommentEmail()));
            contentList.add(o);

        }

        result.put("total",comments.getTotalElements());
        result.put("content",contentList);


        return result;


    }

    public JsonResult deleteComment(@PathVariable("id") String id){

        Comment comment = commentDao.findFirstById(id);

        //删除与父评论关联
        if (comment.getParentId() != null) {
            Comment parentComment = commentDao.findFirstById(comment.getParentId());
            parentComment.getSubComments().remove(id);
            commentDao.save(parentComment);
        }

        //删除子评论
        if (comment.getSubComments().size() != 0) {
            for (String subOid : comment.getSubComments()) {
                Comment subComment = commentDao.findFirstById(subOid);
                commentDao.delete(subComment);
            }
        }

        commentDao.delete(comment);

        return ResultUtils.success();


    }


}
