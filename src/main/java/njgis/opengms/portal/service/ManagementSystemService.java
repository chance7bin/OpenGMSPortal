package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.dao.*;
import njgis.opengms.portal.entity.doo.DailyViewCount;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.support.ParamInfo;
import njgis.opengms.portal.entity.doo.task.InputData;
import njgis.opengms.portal.entity.doo.task.InputDataChildren;
import njgis.opengms.portal.entity.doo.task.ModelListItem;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.entity.dto.task.ResultDataDTO;
import njgis.opengms.portal.entity.dto.task.TaskCheckListDTO;
import njgis.opengms.portal.entity.dto.task.TaskInvokeDTO;
import njgis.opengms.portal.entity.po.*;
import njgis.opengms.portal.enums.UserRoleEnum;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    @Autowired
    CheckedModelDao checkedModelDao;

    @Autowired
    UserDao userDao;

    @Autowired
    TaskDao taskDao;

    @Autowired
    DashboardDao dashboardDao;


    public JsonResult searchDeployedModel(FindDTO findDTO) {
        return ResultUtils.success(computableModelService.searchDeployedModel(findDTO));
    }

    public JsonResult invokeModel(String modelId, String email) {

        //初始化任务
        JsonResult initTaskRes = taskService.initTask(modelId, email);
        JSONObject initTaskData = (JSONObject)initTaskRes.getData();
        int generateTaskFlag = (int) initTaskData.get("generateTaskFlag");
        if (generateTaskFlag != 1){
            if (generateTaskFlag == -2){
                return ResultUtils.error("未找到相应服务");
            } else {
                return ResultUtils.error("初始化任务失败");
            }
        }

        //判断是否有测试数据
        JSONObject modelInfo = (JSONObject) initTaskData.get("modelInfo");
        boolean hasTest = (boolean) modelInfo.get("hasTest");
        if (!hasTest)
            return ResultUtils.error("未找到测试数据");


        //加载数据
        JsonResult loadTestDataRes = taskService.loadTestData(modelId, email);
        if (loadTestDataRes.getCode() != 1)
            return ResultUtils.error("加载测试数据失败");
        List<ResultDataDTO> loadTestDataData = (List<ResultDataDTO>)loadTestDataRes.getData();

        TaskInvokeDTO taskInvokeDTO;
        try {
            taskInvokeDTO = buildInvokeParams(modelId,initTaskData,loadTestDataData);
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            return ResultUtils.error("构造输入数据出错");
        }

        JSONObject invokeParams = JSON.parseObject(JSON.toJSONString(taskInvokeDTO));
        JsonResult result = taskService.handleInvoke(invokeParams, email);

        if (result.getCode() != 1)
            return ResultUtils.error("调用模型失败");
        JSONObject resultData = (JSONObject) result.getData();

        // 把检查记录存入表中
        CheckedModel model = checkedModelDao.findFirstById(modelId);
        if (model == null){
            CheckedModel checkedModel = new CheckedModel();
            checkedModel.setId(modelId);
            checkedModel.setName(modelInfo.getString("name"));
            checkedModel.setLastCheckTime(new Date());
            checkedModel.setStatus(0);
            checkedModel.getTaskIdList().add(resultData.getString("tid"));
            checkedModelDao.insert(checkedModel);
        } else {
            model.setStatus(0);
            model.setLastCheckTime(new Date());
            model.getTaskIdList().add(resultData.getString("tid"));
            checkedModelDao.save(model);
        }


        return result;



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


    public JsonResult updateTaskStatus() {

        List<Integer> status = Arrays.asList(0, 1);
        List<CheckedModel> modelList = checkedModelDao.findAllByStatusIn(status);
        List<String> ids = new ArrayList<>();
        for (CheckedModel model : modelList) {
            List<String> taskIdList = model.getTaskIdList();
            int size = taskIdList.size();
            ids.add(taskIdList.get(size - 1));
        }

        List<Task> ts = taskDao.findAllByTaskIdIn(ids);
        List<Task> newTasks = taskService.updateUserTasks(ts);

        for(Task newTask : newTasks){
            for(CheckedModel model:modelList){
                List<String> taskIdList = model.getTaskIdList();
                int size = taskIdList.size();
                if(newTask.getTaskId().equals(taskIdList.get(size - 1))){
                    // 更新checkedModel表的状态
                    model.setStatus(newTask.getStatus());
                    checkedModelDao.save(model);
                }
            }

        }


        return ResultUtils.success();

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


    public JsonResult setUserRole(String id, UserRoleEnum role){
        try {
            User user = userDao.findFirstById(id);
            user.setUserRole(role);
            User updatedUser = userDao.save(user);
            return ResultUtils.success(updatedUser);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.error();
        }

    }


    public JsonResult getUserList(FindDTO findDTO){

        Pageable pageable = genericService.getPageable(findDTO);
        Page<User> users = userDao.findAllByNameContainsIgnoreCase(findDTO.getSearchText(), pageable);

        return ResultUtils.success(users);
    }


    public JsonResult getDashboardInfo(){

        JSONObject result = new JSONObject();
        DashBoard dashboard = dashboardDao.findFirstByName("dashboard");


        // 模型数量


        // 访问数量
        List<DailyViewCount> dailyViewCount = dashboard.getDailyViewCount();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        JSONArray dailyViewArr = new JSONArray();
        for (DailyViewCount count : dailyViewCount) {
            JSONObject o = new JSONObject();
            o.put("date",sdf.format(count.getDate()));
            o.put("count",count.getCount());
            dailyViewArr.add(o);
        }
        result.put("DailyViewCount", dailyViewArr);


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


}
