package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.dao.CheckModelListDao;
import njgis.opengms.portal.dao.ComputableModelDao;
import njgis.opengms.portal.dao.UserDao;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.support.ParamInfo;
import njgis.opengms.portal.entity.doo.task.InputData;
import njgis.opengms.portal.entity.doo.task.InputDataChildren;
import njgis.opengms.portal.entity.doo.task.ModelListItem;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.entity.dto.task.ResultDataDTO;
import njgis.opengms.portal.entity.dto.task.TaskCheckListDTO;
import njgis.opengms.portal.entity.dto.task.TaskInvokeDTO;
import njgis.opengms.portal.entity.po.CheckModelList;
import njgis.opengms.portal.entity.po.ComputableModel;
import njgis.opengms.portal.entity.po.User;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    UserDao userDao;


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
                ResultUtils.error("未找到相应服务");
            } else {
                ResultUtils.error("初始化任务失败");
            }
        }

        //判断是否有测试数据
        JSONObject modelInfo = (JSONObject) initTaskData.get("modelInfo");
        boolean hasTest = (boolean) modelInfo.get("hasTest");
        if (!hasTest)
            ResultUtils.error("未找到测试数据");


        //加载数据
        JsonResult loadTestDataRes = taskService.loadTestData(modelId, email);
        if (loadTestDataRes.getCode() != 1)
            return ResultUtils.error("加载测试数据失败");
        List<ResultDataDTO> loadTestDataData = (List<ResultDataDTO>)loadTestDataRes.getData();


        TaskInvokeDTO taskInvokeDTO = buildInvokeParams(modelId,initTaskData,loadTestDataData);
        JSONObject invokeParams = JSON.parseObject(JSON.toJSONString(taskInvokeDTO));
        return taskService.handleInvoke(invokeParams,email);



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

}
