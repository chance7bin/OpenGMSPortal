package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.component.AbstractTask.AsyncTask;
import njgis.opengms.portal.dao.ComputableModelDao;
import njgis.opengms.portal.dao.TaskDao;
import njgis.opengms.portal.dao.UserDao;
import njgis.opengms.portal.entity.doo.DailyViewCount;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.support.ParamInfo;
import njgis.opengms.portal.entity.doo.support.TaskData;
import njgis.opengms.portal.entity.doo.user.UserTaskInfo;
import njgis.opengms.portal.entity.dto.task.*;
import njgis.opengms.portal.entity.po.ComputableModel;
import njgis.opengms.portal.entity.po.Task;
import njgis.opengms.portal.entity.po.User;
import njgis.opengms.portal.utils.ResultUtils;
import njgis.opengms.portal.utils.Utils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static njgis.opengms.portal.utils.Utils.convertMdl;
import static njgis.opengms.portal.utils.Utils.postJSON;


/**
 * @Description
 * @Author bin
 * @Date 2021/11/08
 */
@Service
public class TaskService {

    @Autowired
    ComputableModelDao computableModelDao;

    @Autowired
    UserDao userDao;

    @Autowired
    AsyncService asyncService;

    @Autowired
    TaskDao taskDao;

    @Autowired
    UserService userService;

    @Value("${managerServerIpAndPort}")
    private String managerServerIpAndPort;

    @Value ("${dataContainerIpAndPort}")
    String dataContainerIpAndPort;

    //可以可视化的数据模板
    @Value("#{'${visualTemplateIds}'.split(',')}")
    private String[] visualTemplateIds;


    @Value("${resourcePath}")
    private String resourcePath;


    /**
     * 初始化任务
     * @param id
     * @param email
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult initTask(String id, String email) {
        //条目信息
        ComputableModel modelInfo = computableModelDao.findFirstById(id);
        modelInfo.setViewCount(modelInfo.getViewCount() + 1);
        computableModelDao.save(modelInfo);

        //用户信息
        User user = userDao.findFirstByEmail(modelInfo.getAuthor());
        JSONObject userJson = new JSONObject();
        userJson.put("compute_model_user_name", user.getName());
        userJson.put("compute_model_accessId", user.getAccessId());
        userJson.put("userName", user.getName());
        JSONObject taskInfo = new JSONObject();
        JSONObject dxInfo = new JSONObject();
        JSONObject model_Info = new JSONObject();

        //创建task 获取数据服务器地址
        JsonResult jsonResult = generateTask(id, email);
        int code = jsonResult.getCode();
        JSONObject data = JSONObject.parseObject(JSONObject.toJSONString(jsonResult.getData()));
        // System.out.println("task" + jsonResult);
        String msg = null;
        if(code==1){
            JSONObject dxServer = data.getJSONObject("dxServer");
            taskInfo.put("ip", data.getString("ip"));
            taskInfo.put("port", data.getString("port"));
            taskInfo.put("pid", data.getString("pid"));
            dxInfo.put("dxIP", dxServer.getString("ip"));
            dxInfo.put("dxPort", dxServer.getString("port"));
            dxInfo.put("dxType", dxServer.getString("type"));
            msg = "success";
        }else if(code==-2){
            msg="no service";
        }else if(code==-3){
            msg="create failed";
        }else {
            msg = jsonResult.getMsg();
        }

        model_Info.put("name", modelInfo.getName());
        model_Info.put("des", modelInfo.getOverview());
        model_Info.put("date", modelInfo.getCreateTime());

        //判断是否有测试数据
        boolean hasTest;
        if (modelInfo.getTestDataPath() == null || modelInfo.getTestDataPath().equals("")) {
            hasTest = false;
        } else {
            hasTest = true;
        }
        model_Info.put("hasTest", hasTest);

        JSONObject mdlInfo = Utils.convertMdl(modelInfo.getMdl());
        JSONObject mdlObj = mdlInfo.getJSONObject("mdl");
        JSONArray dataItems = mdlObj.getJSONArray("DataItems");
        JSONArray dataRefs = new JSONArray();
        for (int i = 0; i < dataItems.size(); i++) {
            JSONObject dataRef = dataItems.getJSONArray(i).getJSONObject(0);
            dataRefs.add(dataRef);
        }


        JSONArray states = mdlObj.getJSONArray("states");

        for (int i = 0; i < states.size(); i++) {
            JSONObject state = states.getJSONObject(i);
            JSONArray events = state.getJSONArray("event");
            JSONArray eventsSort = new JSONArray();
            for (int j = 0; j < events.size(); j++) {
                JSONObject event = events.getJSONObject(j);
                if (event.getString("eventType").equals("response")) {
                    //判断是否能够可视化

                    eventsSort.add(event);
                }
            }
            // for (int j = 0; j < events.size(); j++) {
            //     JSONObject event = events.getJSONObject(j);
            //     if (!event.getString("eventType").equals("response")) {
            //         //判断是否能够可视化
            //
            //         eventsSort.add(event);
            //     }
            // }
            state.put("event", eventsSort);
            states.set(i, state);
        }


        model_Info.put("states", states);
        model_Info.put("dataRefs", dataRefs);
        String jsonStr = JSON.toJSONString(modelInfo.getPageConfigJson());
        model_Info.put("pageConfig", JSONObject.parse(jsonStr.replace("\\n","\\\\n")));
        //拼接
        JSONObject result = new JSONObject();
        result.put("userInfo", userJson);
        result.put("modelInfo", model_Info);
        result.put("taskInfo", taskInfo);
        result.put("dxInfo", dxInfo);
        result.put("msg", msg);
        result.put("generateTaskFlag", code);
        result.put("visualIds",visualTemplateIds);

        return ResultUtils.success(result);
    }


    /**
     * 创建task 获取数据服务器地址
     * @param id
     * @param email
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult generateTask(String id, String email) {
        String md5 = getMd5(id);
        JSONObject result = getServiceTask(md5);

        if (result.getInteger("code") == 1) {

            JSONObject data = result.getJSONObject("data");

            if(data==null){
                return ResultUtils.error(-2,"can not get service task!");
            }
            String host = data.getString("host");
            int port = Integer.parseInt(data.getString("port"));

            JSONObject createTaskResult = createTask(md5, host, port, email);
            if (createTaskResult != null && createTaskResult.getInteger("code") == 1) {
                return ResultUtils.success(createTaskResult.getJSONObject("data"));
            } else {
                return ResultUtils.error(-3, "can not create task!");
            }
        } else {
            return ResultUtils.error(-2,"can not get service task!");
        }
    }


    /**
     * 获取模型的md5
     * @param id
     * @return java.lang.String
     * @Author bin
     **/
    public String getMd5(String id) {
        ComputableModel computableModel = computableModelDao.findFirstById(id);
        return computableModel.getMd5();
    }

    /**
     * 根据pid获取可用的任务服务器
     * 根据模型pid，通过管理服务器获取包含有该服务的任务服务器
     * @param md5
     * @return com.alibaba.fastjson.JSONObject
     * @Author bin
     **/
    public JSONObject getServiceTask(String md5) {

        String urlStr = "http://" + managerServerIpAndPort + "/GeoModeling/taskNode/getServiceTask/" + md5;
        JSONObject result = Utils.connentURL("GET", urlStr);

        return result;
    }

    /**
     * 预处理过程，创建一个Task,获取输入上传的数据服务器地址
     * 向管理服务器发起创建Task的请求，post请求数据为任务服务器地址及IP，模型服务pid,以及用户
     * @param md5
     * @param ip
     * @param port
     * @param email
     * @return com.alibaba.fastjson.JSONObject
     * @Author bin
     **/
    public JSONObject createTask(String md5, String ip, int port, String email) {

        String urlStr = "http://" + managerServerIpAndPort + "/GeoModeling/computableModel/createTask";
//        Map<String, Object> paramMap = new HashMap<String, Object>();
//        paramMap.put("ip", ip);
//        paramMap.put("port", port);
//        paramMap.put("pid", md5);
//        paramMap.put("username", username);
        JSONObject paramMap = new JSONObject();
        paramMap.put("ip", ip);
        paramMap.put("port", port);
        paramMap.put("pid", md5);
        paramMap.put("email", email);

        JSONObject result = Utils.postJSON(urlStr, paramMap);

        return result;
    }


    /**
     * 加载测试数据
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult loadTestData(String modelId,String email){
        // String modelId = testDataUploadDTO.getId();
        ComputableModel computableModel=computableModelDao.findFirstById(modelId);

        // 因为测试数据是存放在门户后台的，只有第一次用到的时候才会上传到数据容器
        if(computableModel.getTestDataCache()!=null){
            return ResultUtils.success(computableModel.getTestDataCache());
        }else {
            JSONObject mdlJSON = Utils.convertMdl(computableModel.getMdl());

            // String[] dataIpAndPort = dataContainerIpAndPort.split(":");
            // testDataUploadDTO.setHost(dataIpAndPort[0]);
            // testDataUploadDTO.setPort(Integer.parseInt(dataIpAndPort[1]));

            //处理得到进行数据上传的List数组
            List<UploadDataDTO> uploadDataDTOs = getTestDataUploadArray(modelId, mdlJSON);
            if (uploadDataDTOs == null) {
                return ResultUtils.error("No Test Data");
            }
            List<Future<ResultDataDTO>> futures = new ArrayList<>();
            //开启异步任务
            uploadDataDTOs.forEach((UploadDataDTO obj) -> {
                Future<ResultDataDTO> future = asyncService.uploadDataToServer(obj, email);
                futures.add(future);
            });
            List<ResultDataDTO> resultDataDTOs = new ArrayList<>();

            futures.forEach((future) -> {
                try {
                    ResultDataDTO resultDatadto = (ResultDataDTO) future.get();
                    resultDataDTOs.add(resultDatadto);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            });
            computableModel.setTestDataCache(resultDataDTOs);
            computableModelDao.save(computableModel);
            return ResultUtils.success(resultDataDTOs);

        }
    }

    //得到数据存储的路径
    public List<UploadDataDTO> getTestDataUploadArray(String modelId, JSONObject mdlJson) {
        JSONArray states = mdlJson.getJSONObject("mdl").getJSONArray("states");


        // String modelId = testDataUploadDTO.getId();
        String parentDirectory = resourcePath + "/computableModel/testify/" + modelId;
        String configPath = parentDirectory + "/" + "config.xml";
        JSONArray configInfoArray = getConfigInfo(configPath, parentDirectory, modelId);
        if (configInfoArray == null) {
            return null;
        }
        //进行遍历
        List<UploadDataDTO> dataUploadList = new ArrayList<>();
        for (int i = 0; i < configInfoArray.size(); i++) {
            JSONObject temp = configInfoArray.getJSONObject(i);
            UploadDataDTO uploadDataDTO = new UploadDataDTO();
            uploadDataDTO.setEvent(temp.getString("event"));
            uploadDataDTO.setState(temp.getString("state"));
            uploadDataDTO.setFilePath(temp.getString("file"));
            uploadDataDTO.setChildren(temp.getJSONArray("children").toJavaList(ParamInfo.class));

            for(int j=0;j<states.size();j++){
                JSONObject state = states.getJSONObject(j);
                if(state.getString("Id").equals(uploadDataDTO.getState())){
                    JSONArray events=state.getJSONArray("event");
                    for(int k=0;k<events.size();k++){
                        JSONObject event=events.getJSONObject(k);
                        if(event.getString("eventName").equals(uploadDataDTO.getEvent())){
                            JSONObject data=event.getJSONArray("data").getJSONObject(0);
                            if(data.getString("dataType").equals("external")){
                                uploadDataDTO.setType("id");
                                uploadDataDTO.setTemplate(data.getString("externalId").toLowerCase());
                                for(String id:visualTemplateIds){
                                    if(uploadDataDTO.getTemplate().equals(id)){
                                        uploadDataDTO.setVisual(true);
                                        break;
                                    }
                                }
                            }else{
                                if(data.getString("schema")!=null) {
                                    uploadDataDTO.setType("schema");
                                    uploadDataDTO.setTemplate(data.getString("schema"));
                                    uploadDataDTO.setVisual(false);
                                }else{
                                    uploadDataDTO.setType("none");
                                    uploadDataDTO.setTemplate("");
                                    uploadDataDTO.setVisual(false);
                                }
                            }

                            break;
                        }
                    }
                    break;
                }
            }
            if(uploadDataDTO.getType()==null){
                uploadDataDTO.setType("none");
                uploadDataDTO.setTemplate("");
                uploadDataDTO.setVisual(false);
            }

            dataUploadList.add(uploadDataDTO);
        }
        return dataUploadList;
    }




    //读取xml信息，返回数据的State,Event和数据路径
    private JSONArray getConfigInfo(String configPath, String parentDirectory, String id) {
        JSONArray resultArray = new JSONArray();
        Document result = null;
        SAXReader reader = new SAXReader();
        try {
            result = reader.read(configPath);
        } catch (DocumentException e) {
            e.printStackTrace();
            return null;
        }
        ComputableModel computableModel = computableModelDao.findFirstById(id);
        JSONObject mdlInfo = convertMdl(computableModel.getMdl());
        JSONObject mdlObj = mdlInfo.getJSONObject("mdl");
        JSONArray dataItems = mdlObj.getJSONArray("DataItems");
        JSONArray dataRefs = new JSONArray();
        for (int i = 0; i < dataItems.size(); i++) {
            JSONObject dataRef = dataItems.getJSONArray(i).getJSONObject(0);
            dataRefs.add(dataRef);
        }
        JSONArray states = mdlObj.getJSONArray("states");

        Element rootElement = result.getRootElement();
        List<Element> items = rootElement.elements();
        for (Element item : items) {
            String fileName = item.attributeValue("File");
            String state = item.attributeValue("State");
            String event = item.attributeValue("Event");
            String filePath = parentDirectory + "/" + fileName;

            JSONArray children = new JSONArray();
            Boolean find = false;
            for (int i = 0; i < states.size(); i++) {
                JSONArray events = states.getJSONObject(i).getJSONArray("event");
                for (int j = 0; j < events.size(); j++) {
                    JSONObject _event = events.getJSONObject(j);
                    Boolean quit = false;
                    if (_event.getString("eventName").equals(event)) {
                        find = true;
                        String type = _event.getString("eventType");
                        if (type.equals("response")) {
                            JSONObject data = _event.getJSONArray("data").getJSONObject(0);
                            if (data.getString("dataType").equals("internal")) {
                                String dataRefText = data.getString("text");
                                if(dataRefText.equals("nc")||dataRefText.equals("zip")){
                                    continue;
                                }
                                for (int k = 0; k < dataRefs.size(); k++) {
                                    JSONObject dataRef = dataRefs.getJSONObject(k);
                                    if (dataRef.getString("text").equals(dataRefText)) {
                                        quit = true;
                                        JSONArray nodes = dataRef.getJSONArray("nodes");
                                        if (nodes != null) {
                                            if (nodes.size() > 0) {
                                                try {

                                                    BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
                                                    String content = "";
                                                    String temp;
                                                    while ((temp = br.readLine()) != null) {
                                                        content += temp;
                                                    }

                                                    Document mdlDoc = DocumentHelper.parseText(content);
                                                    Element dataSetElement = mdlDoc.getRootElement();
                                                    List<Element> XDOs = dataSetElement.elements();

                                                    for (int x = 0; x < XDOs.size(); x++) {
                                                        Element element = XDOs.get(x);
                                                        JSONObject child = new JSONObject();
                                                        child.put("eventName", element.attributeValue("name"));
                                                        child.put("value", element.attributeValue("value"));
                                                        children.add(child);
                                                    }

                                                } catch (Exception e) {
                                                    e.fillInStackTrace();
                                                }
                                            }
                                        }
                                        break;
                                    }

                                }
                            }
                        }

                    }
                    if (quit) {
                        break;
                    }
                }
                if (find) {
                    break;
                }
            }


            JSONObject tempObject = new JSONObject();
            tempObject.put("state", state);
            tempObject.put("event", event);
            tempObject.put("file", filePath);
            tempObject.put("children", children);
            resultArray.add(tempObject);
        }
        return resultArray;
    }


    /**
     * 调用模型的处理工作（前处理、后处理）
     * @param lists
     * @param email
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult handleInvoke(JSONObject lists, String email) {


        lists.put("username", email);

        // TODO: 2021/11/9 判断用户最后一次调用时间
        // 判断用户最后一次调用时间
        // User user = userDao.findFirstByEmail(email);
        // Date lastInvokeTime = user.getLastInvokeTime();
        // Date now = new Date();
        // if(lastInvokeTime != null){
        //     long intervalSec = (now.getTime() - lastInvokeTime.getTime())/1000;
        //     if(intervalSec<modelInvokeInternal){
        //         return ResultUtils.error(-2,"Each user can only invoke the model once in three minutes, you need to wait "+String.valueOf(modelInvokeInternal-intervalSec)+" seconds before invoking the next model.");
        //     }
        // }
        // user.setLastInvokeTime(new Date());
        // userDao.save(user);

        ComputableModel computableModel=computableModelDao.findFirstById(lists.getString("oid"));
        String mdlStr=computableModel.getMdl();
        JSONObject mdlJson= Utils.convertMdl(mdlStr);
        // System.out.println(mdlJson);
        JSONObject mdl=mdlJson.getJSONObject("mdl");
        JSONArray states=mdl.getJSONArray("states");
        //截取RelatedDatasets字符串

        JSONArray outputs=new JSONArray();
        for(int i=0;i<states.size();i++){
            JSONObject state=states.getJSONObject(i);
            JSONArray events=state.getJSONArray("event");
            for(int j=0;j<events.size();j++){
                JSONObject event=events.getJSONObject(j);
                String eventType=event.getString("eventType");
                if(eventType.equals("noresponse")){
                    JSONObject output=new JSONObject();
                    output.put("statename",state.getString("name"));
                    output.put("event",event.getString("eventName"));
                    JSONObject template=new JSONObject();

                    JSONArray dataArr=event.getJSONArray("data");
                    if(dataArr!=null) {
                        JSONObject data = dataArr.getJSONObject(0);
                        String dataType = data.getString("dataType");
                        if (dataType.equals("external")) {
                            String externalId = data.getString("externalId");

                            template.put("type", "id");
                            template.put("value", externalId.toLowerCase());
                            output.put("template", template);

                        } else if (dataType.equals("internal")) {
                            JSONArray nodes = data.getJSONArray("nodes");
                            if (nodes != null) {
                                if(data.getString("schema")!=null) {
                                    template.put("type", "schema");
                                    template.put("value", data.getString("schema"));
                                    output.put("template", template);
                                }else{
                                    template.put("type", "none");
                                    template.put("value", "");
                                    output.put("template", template);
                                }
                            } else {
                                template.put("type", "none");
                                template.put("value", "");
                                output.put("template", template);
                            }
                        } else {
                            template.put("type", "none");
                            template.put("value", "");
                            output.put("template", template);
                        }
                    }else {
                        template.put("type", "none");
                        template.put("value", "");
                        output.put("template", template);
                    }
                    outputs.add(output);
                }
            }
        }
        lists.put("outputs",outputs);


        JSONObject result = invoke(lists);


        if (result == null) {
            return ResultUtils.error("invoke failed!");
        } else {
            Task task = new Task();
            task.setComputableId(lists.getString("oid"));
            task.setComputableName(computableModel.getName());
            task.setTaskId(result.getString("tid"));
            task.setEmail(email);
            task.setIntegrate(false);
            task.setPermission("private");
            // task.setIp(lists.getString("ip"));
            // task.setPort(lists.getInteger("port"));
            task.setIp(result.getString("ip"));
            task.setPort(result.getInteger("port"));
            task.setRunTime(new Date());
            task.setStatus(0);
            JSONArray inputs = lists.getJSONArray("inputs");
            task.setInputs(JSONObject.parseArray(inputs.toJSONString(), TaskData.class));
            task.setOutputs(null);
//                for(int i=0;i<inputs.size();i++)
//                {
//                    JSONObject input=inputs.getJSONObject(i);
//                    BeanUtils.copyProperties(input,);
//                }

            taskDao.save(task);

            UserTaskInfo userTaskInfo=new UserTaskInfo();
            userTaskInfo.setCreateTime(task.getRunTime());
            userTaskInfo.setModelName(task.getComputableName());
            userTaskInfo.setTaskId(task.getTaskId());


            // 记录每日调用的次数
            Date date = new Date();
            DailyViewCount newInvokeCount = new DailyViewCount(date, 1);
            List<DailyViewCount> dailyInvokeCount = computableModel.getDailyInvokeCount();
            // if(computableModel.getDailyInvokeCount()!=null){
            //     dailyInvokeCount = computableModel.getDailyInvokeCount();
            // }
            if(dailyInvokeCount.size()>0) {
                DailyViewCount dailyViewCount = dailyInvokeCount.get(dailyInvokeCount.size() - 1);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                if (sdf.format(dailyViewCount.getDate()).equals(sdf.format(date))) {
                    dailyViewCount.setCount(dailyViewCount.getCount() + 1);
                    dailyInvokeCount.set(dailyInvokeCount.size() - 1, dailyViewCount);
                } else {
                    dailyInvokeCount.add(newInvokeCount);
                }
            }
            else{
                dailyInvokeCount.add(newInvokeCount);
            }

            computableModel.setDailyInvokeCount(dailyInvokeCount);
            computableModel.setInvokeCount(computableModel.getInvokeCount()+1);
            computableModelDao.save(computableModel);

            //存入用户信息记录
            String msg= userService.addTaskInfo(email,userTaskInfo);
//                result=result.concat("&").concat(msg);

            return ResultUtils.success(result);
        }
    }


    // 调用模型
    public JSONObject invoke(JSONObject lists) {

        JSONObject result = postJSON("http://" + managerServerIpAndPort + "/GeoModeling/computableModel/invoke", lists);

        if (result != null) {
            if (result.getInteger("code") == 1) {

                // return result.getJSONObject("data").getString("tid");
                // 由于做任务合并，打开模型信息界面时的服务器可能与调用模型的服务器不一样，
                // 所以请求完这个数据时应该对任务的ip和port进行修改
                // 有用到invoke方法的我都进行了修改，如果之后有错的话再改
                return result.getJSONObject("data");
            }
        }

        return null;
    }

    /**
     * 根据task id去查询模型运行记录
     * @param data
     * @return com.alibaba.fastjson.JSONObject
     * @Author bin
     **/
    public JSONObject getTaskResult(TaskCheckDTO data) {
        JSONObject out = new JSONObject();

        JSONObject result = Utils.postJSON("http://" + managerServerIpAndPort + "/GeoModeling/computableModel/refreshTaskRecord", (JSONObject) JSONObject.toJSON(data));

        //update model status to Started, Started: 1, Finished: 2, Inited: 0, Error: -1
        Task task = taskDao.findFirstByTaskId(data.getTid());
        int state = task.getStatus();
        int remoteState = result.getJSONObject("data").getInteger("status");
        if (remoteState != state) {
            task.setStatus(remoteState);
        }
        if (remoteState == 2) {
            boolean hasValue = false;
            JSONArray outputs = result.getJSONObject("data").getJSONArray("outputs");
            for (int i = 0; i < outputs.size(); i++) {
                if (!outputs.getJSONObject(i).getString("url").equals("")) {
                    hasValue = true;
                    break;
                }
            }
            if (!hasValue) {
                task.setStatus(-1);
            }
            for (int i = 0; i < outputs.size(); i++) {
                if (outputs.getJSONObject(i).getString("url").contains("[")) {//说明是单event多输出的情况
                    outputs.getJSONObject(i).put("multiple",true);
                }
            }

            task.setOutputs(result.getJSONObject("data").getJSONArray("outputs").toJavaList(TaskData.class));

            task = templateMatch(task);
        }
        taskDao.save(task);


        if (task.getStatus() == 0) {
            out.put("status", 0);
        } else if (task.getStatus() == 1) {
            out.put("status", 1);
        } else if (task.getStatus() == 2) {
            out.put("status", 2);
            out.put("outputdata", task.getOutputs());
        } else {
            out.put("status", -1);
        }

        return out;
    }

    // 为taskoutput匹配templateId
    public Task templateMatch(Task task){

        String modelId = task.getComputableId();
        ComputableModel modelInfo =  computableModelDao.findFirstById(modelId);
        JSONObject mdlInfo = Utils.convertMdl(modelInfo.getMdl());
        JSONObject mdlObj = mdlInfo.getJSONObject("mdl");
        JSONArray states = mdlObj.getJSONArray("states");

        List<TaskData> outputs = task.getOutputs();

        for (int i=0;i<states.size();i++){
            JSONObject obj = (JSONObject)states.get(i);
            JSONArray event = obj.getJSONArray("event");
            for( int j=0; j<event.size();j++ ){
                JSONObject file = (JSONObject) event.get(j);
                JSONArray dataArray = file.getJSONArray("data");
                JSONObject data = (JSONObject) dataArray.get(0);

                if(file.getString("eventType").equals("noresponse")){
                    for (TaskData output : outputs){
                        if(output.getEvent().equals(file.getString("eventName"))){
                            if(data.getString("dataType").equals("external"))
                                output.setTemplateId(data.getString("externalId"));
                            else
                                output.setTemplateId("schema");
                        }
                    }
                }
            }

        }

        task.setOutputs(outputs);

        return task;

    }


    /**
     * 得到任务信息
     * @param email
     * @param taskFindDTO 
     * @return njgis.opengms.portal.entity.doo.JsonResult 
     * @Author bin
     **/
    public JsonResult getTasksByUserByStatus(String email, TaskFindDTO taskFindDTO) {

        Sort sort = Sort.by(taskFindDTO.getAsc() == 1 ? Sort.Direction.ASC : Sort.Direction.DESC, taskFindDTO.getSortType());
        Pageable pageable = PageRequest.of(taskFindDTO.getPage() - 1, taskFindDTO.getPageSize(), sort);
        Page<Task> tasks = Page.empty();
        String status = taskFindDTO.getStatus();
        try{
            status = status.toLowerCase();
            if (status.equals("calculating")) {
                tasks = taskDao.findAllByEmailAndStatus(email, 1, pageable);
            } else if (status.equals("inited")) {
                tasks = taskDao.findAllByEmailAndStatus(email, 0, pageable);
            } else if (status.equals("successful")) {
                tasks = taskDao.findAllByEmailAndStatus(email, 2, pageable);
            } else if (status.equals("failed"))
                tasks = taskDao.findAllByEmailAndStatus(email, -1, pageable);
            else
                tasks = taskDao.findAllByEmail(email, pageable);
            List<Task> ts = tasks.getContent();



            List<Task> newTasks = updateUserTasks(ts);//先利用这个函数更新一下数据库

            for(Task newTask : newTasks){
                for(Task task:ts){
                    if(newTask.getId().equals(task.getId())){
                        task.setStatus(newTask.getStatus());
                        task.setOutputs(newTask.getOutputs());
                    }
                }

            }

            JSONObject taskObject = new JSONObject();
            taskObject.put("count", tasks.getTotalElements());
            taskObject.put("tasks", ts);
            return ResultUtils.success(taskObject);

        }catch (Exception e){
            return ResultUtils.error("search error");
        }


    }

    //多线程通过managerserver更新数据库
    public List<Task> updateUserTasks(List<Task> ts) {

        AsyncTask asyncTask = new AsyncTask();
        List<Future<String>> futures = new ArrayList<>();

//        Sort sort = new Sort(Sort.Direction.DESC, "runTime");
//        List<Task> ts = taskDao.findByUserId(userName);
        List<Task> taskList = new ArrayList<>();
        try {
            for (int i = 0; i < ts.size(); i++) {
                Task task = ts.get(i);
                if (task.getStatus() != 2 && task.getStatus() != -1) {
                    JSONObject param = new JSONObject();
                    param.put("ip", task.getIp());
                    param.put("port", task.getPort());
                    param.put("tid", task.getTaskId());
                    // 多加一个oid用于标识唯一提交任务
                    param.put("id", task.getId());
//                    param.put("integrate", task.getIntegrate());

                    futures.add(asyncTask.getRecordCallback(param, managerServerIpAndPort));
                }
            }

            // TODO: 2021/11/9 是否有必要使用while循环，感觉会浪费资源
            for (Future<?> future : futures) {
                while (true) {//CPU高速轮询：每个future都并发轮循，判断完成状态然后获取结果，这一行，是本实现方案的精髓所在。即有10个future在高速轮询，完成一个future的获取结果，就关闭一个轮询
                    if (future.isDone() && !future.isCancelled()) {//获取future成功完成状态，如果想要限制每个任务的超时时间，取消本行的状态判断+future.get(1000*1, TimeUnit.MILLISECONDS)+catch超时异常使用即可。
                        String result = (String) future.get();//获取结果
                        if(!result.equals("{}")){
                            JSONObject jsonResult = JSON.parseObject(result);
                            String tid = jsonResult.getString("tid");
                            String id = jsonResult.getString("id");
                            int remoteStatus = jsonResult.getInteger("status");
                            List<TaskData> outputs = jsonResult.getJSONArray("outputs").toJavaList(TaskData.class);

                            // Task task = taskDao.findFirstByTaskId(tid);
                            // 多加一个oid进行限制，因为任务合并后输入数据相同的任务tid是一样的
                            Task task = taskDao.findFirstByTaskIdAndId(tid,id);

                            if (task.getStatus() != remoteStatus) {
                                task.setStatus(remoteStatus);
                                task.setOutputs(outputs);
                                taskList.add(task);
                                taskDao.save(task);
                                // for (int i = 0; i < ts.size(); i++) {
                                //     Task task1 = ts.get(i);
                                //     if (task1.getTaskId().equals(tid) && task1.getOid().equals(oid)) {
                                //         task1.setStatus(remoteStatus);
                                //         task1.setOutputs(outputs);
                                //         break;
                                //     }
                                // }
                            }
                            break;//当前future获取结果完毕，跳出while
                        }else{

                        }
                        break;

                    } else {
                        Thread.sleep(1);//每次轮询休息1毫秒（CPU纳秒级），避免CPU高速轮循耗空CPU---》新手别忘记这个
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println(e.getMessage());
        }
        return taskList;
    }


}
