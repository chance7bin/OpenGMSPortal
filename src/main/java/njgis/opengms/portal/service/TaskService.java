package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.component.AbstractTask.AsyncTask;
import njgis.opengms.portal.dao.*;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.MyException;
import njgis.opengms.portal.entity.doo.intergrate.Action;
import njgis.opengms.portal.entity.doo.intergrate.DataProcessing;
import njgis.opengms.portal.entity.doo.intergrate.Model;
import njgis.opengms.portal.entity.doo.intergrate.ModelAction;
import njgis.opengms.portal.entity.doo.support.DailyViewCount;
import njgis.opengms.portal.entity.doo.support.ParamInfo;
import njgis.opengms.portal.entity.doo.support.TaskData;
import njgis.opengms.portal.entity.doo.user.UserTaskInfo;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.entity.dto.task.*;
import njgis.opengms.portal.entity.po.*;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.enums.UserRoleEnum;
import njgis.opengms.portal.utils.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static njgis.opengms.portal.utils.Utils.convertMdl;
import static njgis.opengms.portal.utils.Utils.postJSON;


/**
 * @Description
 * @Author bin
 * @Date 2021/11/08
 */
@Slf4j
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

    @Autowired
    DataServerTaskDao dataServerTaskDao;

    @Autowired
    GenericService genericService;

    @Autowired
    DataItemDao dataItemDao;

    @Autowired
    IntegratedTaskDao integratedTaskDao;

    @Value("${managerServerIpAndPort}")
    private String managerServerIpAndPort;

    @Value ("${dataContainerIpAndPort}")
    String dataContainerIpAndPort;

    //可以可视化的数据模板
    @Value("#{'${visualTemplateIds}'.split(',')}")
    private String[] visualTemplateIds;


    @Value("${resourcePath}")
    private String resourcePath;

    private int modelInvokeInternal = 180;

    @Autowired
    RedisService redisService;

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
        modelInfo.setInvokeCount(modelInfo.getInvokeCount() + 1);
        // computableModelDao.save(modelInfo);
        redisService.saveItem(modelInfo, ItemTypeEnum.ComputableModel);

        //用户信息
        User user = userDao.findFirstByEmail(modelInfo.getAuthor());
        JSONObject userJson = new JSONObject();
        userJson.put("compute_model_user_name", user.getName());
        userJson.put("compute_model_user_oid", user.getId());
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
        if(code==0){
            JSONObject dxServer = data.getJSONObject("dxServer");
            taskInfo.put("ip", data.getString("ip"));
            taskInfo.put("port", data.getString("port"));
            taskInfo.put("pid", data.getString("pid"));
            dxInfo.put("dxIP", dxServer.getString("ip"));
            dxInfo.put("dxPort", dxServer.getString("port"));
            dxInfo.put("dxType", dxServer.getString("type"));
            // dxInfo.put("access_url", dxServer.getString("access_url")); //数据容器部署在内网的话要用这个url访问数据容器
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
            for (int j = 0; j < events.size(); j++) {
                JSONObject event = events.getJSONObject(j);
                if (event.getString("eventType").equals("noresponse")) {
                    //判断是否能够可视化

                    eventsSort.add(event);
                }
            }
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
                    // e.printStackTrace();
                    log.error(e.getMessage());
                } catch (ExecutionException e) {
                    // e.printStackTrace();
                    log.error(e.getMessage());
                }

            });
            computableModel.setTestDataCache(resultDataDTOs);
            // computableModelDao.save(computableModel);
            redisService.saveItem(computableModel, ItemTypeEnum.ComputableModel);
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
            // e.printStackTrace();
            log.error(e.getMessage());
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
        if (lists == null)
            return ResultUtils.error();

        lists.put("username", email);

        // TODO: 2021/11/9 判断用户最后一次调用时间
        // 判断用户最后一次调用时间
        User user = userDao.findFirstByEmail(email);
        UserRoleEnum userRole = user.getUserRole();
        if (userRole == null || !userRole.isAdmin()){
            Date lastInvokeTime = user.getLastInvokeTime();
            Date now = new Date();
            if(lastInvokeTime != null){
                long intervalSec = (now.getTime() - lastInvokeTime.getTime())/1000;
                if(intervalSec<modelInvokeInternal){
                    // return ResultUtils.error(-2,"Each user can only invoke the model once in three minutes, you need to wait "+String.valueOf(modelInvokeInternal-intervalSec)+" seconds before invoking the next model.");
                    return ResultUtils.error(-2,"每位用户在三分钟之内只能调用一次模型，您还需等待 "+String.valueOf(modelInvokeInternal-intervalSec)+" 秒才可以调用下次模型");
                }
            }
            user.setLastInvokeTime(new Date());
            userDao.save(user);
        }

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
            // computableModelDao.save(computableModel);
            redisService.saveItem(computableModel, ItemTypeEnum.ComputableModel);

            //存入用户信息记录
            String msg= userService.addTaskInfo(email,userTaskInfo);
//                result=result.concat("&").concat(msg);

            // 管理系统用于记录检查模型的任务id
            // result.put("taskId", task.getId());

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

        Sort sort = Sort.by(taskFindDTO.getAsc() == 1 ? Sort.Direction.ASC : Sort.Direction.DESC, "runTime");
        Pageable pageable = PageRequest.of(taskFindDTO.getPage()-1, taskFindDTO.getPageSize(), sort);
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

            //把内网ip换成外网可以访问的地址，供外网下载(这段代码迁移到task server了)
            // for (Task t : ts) {
            //     List<TaskData> outputs = t.getOutputs();
            //     for (TaskData output : outputs) {
            //         String url = output.getUrl();
            //         if (url != null && url.contains("172.21.213.111:8082")){
            //             url = url.replaceFirst("172.21.213.111:8082", dataContainerIpAndPort);
            //             // url = url.replaceFirst("221.226.60.2:8082", dataContainerIpAndPort);
            //             output.setUrl(url);
            //         }
            //     }
            // }

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
                                taskDao.save(task);
                                taskList.add(task);
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
            // e.printStackTrace();
            log.error(e.getMessage());
//            System.out.println(e.getMessage());
        }
        return taskList;
    }


    public JsonResult addDescription(String taskId, String description) {
        Task task = taskDao.findFirstByTaskId(taskId);
        if (task == null)
            return ResultUtils.error();
        task.setDescription(description);
        taskDao.save(task);
        return ResultUtils.success();

    }

    public JsonResult delete(String id) {
        Task task = taskDao.findFirstById(id);
        if (task != null) {
            taskDao.delete(task);
            return ResultUtils.success();
        } else {
            return ResultUtils.error();
        }
    }


    public JSONArray getDataProcessings() throws DocumentException, IOException, URISyntaxException {
        //因为dataservice不提供直接查询接口，因此只能先找token再遍历
        String baseUrl = "http://111.229.14.128:8898/onlineNodesAllPcs";
        JSONArray j_nodes = new JSONArray();

        try { //dataservice返回的是xml,转换json会遇到一个节点还是多个节点的问题，所以要判断一下转成了JSONObject还是JSONArray
            j_nodes = getDataProcessingNode().getJSONArray("onlineServiceNodes");

        }catch (Exception e){
            j_nodes.add(getDataProcessingNode().getJSONObject("onlineServiceNodes"));
        }

        List<Map<String,String>> nodes = JSONArray.parseObject(j_nodes.toString(),List.class);

        JSONArray result = new JSONArray();
        String url = "";
        for(Map<String,String> node : nodes){
            String token = node.get("token");
            url = baseUrl + "?token=" + URLEncoder.encode(token) + "&type=Processing";
            String xml = MyHttpUtils.GET(url,"utf-8",null);
            JSONObject jsonObject = XmlTool.xml2Json(xml);
            JSONArray j_processings = new JSONArray();
            try{
                j_processings = jsonObject.getJSONArray("AvailablePcs");
                for(int i=0; j_processings!=null&&i<j_processings.size();i++){
                    JSONObject j_process = j_processings.getJSONObject(i);
                    j_process.put("token",token);
                    result.add(j_process);
                }
            }catch (Exception e){
                JSONObject j_processing = jsonObject.getJSONObject("AvailablePcs");
                j_processing.put("token",token);
                result.add(j_processing);
            }


        }

        return result;
    }

    public JSONObject getDataProcessingNode() throws IOException, URISyntaxException, DocumentException {
        String url = "http://111.229.14.128:8898/onlineNodes";

        String xml = MyHttpUtils.GET(url,"utf-8",null);

        JSONObject jsonObject = XmlTool.xml2Json(xml);

        return jsonObject;
    }



    public JSONObject initDataTaskOutput(String id, String userName) {
        DataServerTask dataServerTask = dataServerTaskDao.findFirstById(id);


        User user = userDao.findFirstByEmail(dataServerTask.getEmail());

        JSONObject userJson = new JSONObject();
        userJson.put("compute_model_user_name", user.getName());
        userJson.put("compute_model_user_oid", user.getId());

        user = userDao.findFirstByEmail(userName);

        userJson.put("userName", user.getName());
        userJson.put("userId", user.getId());

        JSONObject result = new JSONObject();
        JsonResult jsonResult = new JsonResult();

        JSONObject model_Info = new JSONObject();
        JSONObject taskInfo = new JSONObject();

        model_Info.put("name", dataServerTask.getServiceName());
        model_Info.put("des", dataServerTask.getDescription());
        model_Info.put("date", dataServerTask.getFinishTime());

        taskInfo.put("creater", user.getName());
        taskInfo.put("description", dataServerTask.getDescription());
        taskInfo.put("permission", dataServerTask.getPermission());
        taskInfo.put("createTime", dataServerTask.getRunTime());
        taskInfo.put("status", dataServerTask.getStatus());
        taskInfo.put("outputs", dataServerTask.getOutputs());

        if (dataServerTask.getPermission().equals("private")&&!dataServerTask.getEmail().equals(userName) ) {
            result.put("permission", "forbid");
//            return result;
        } else {
            result.put("permission", "allow");
        }

        taskInfo.put("inputs", dataServerTask.getInputs());

        result.put("userInfo", userJson);
        result.put("modelInfo", model_Info);
        result.put("taskInfo", taskInfo);
//        result.put("dxInfo", dxInfo);
//         System.out.println(result);

        return result;
    }

    public JSONObject getDataTasks(String email, DataMethodTaskFindDTO dataTasksFindDTO) {
        Pageable pageable = genericService.getPageable(dataTasksFindDTO);
        Page<DataServerTask> dataServerTaskPage;
        if(dataTasksFindDTO.getSearchText().equals("")){
            if(dataTasksFindDTO.getStatus() == 0){
                dataServerTaskPage = dataServerTaskDao.findAllByEmail(email, pageable);
            } else {
                dataServerTaskPage = dataServerTaskDao.findAllByEmailAndStatus(email, dataTasksFindDTO.getStatus(), pageable);
            }
        }else {
            if(dataTasksFindDTO.getStatus() == 0){
                dataServerTaskPage = dataServerTaskDao.findAllByEmailAndServiceNameLike(email, dataTasksFindDTO.getSearchText(), pageable);
            } else {
                dataServerTaskPage = dataServerTaskDao.findAllByEmailAndStatusAndServiceNameLike(email, dataTasksFindDTO.getStatus(),dataTasksFindDTO.getSearchText(),pageable);
            }
        }

        List<DataServerTask> dataServerTasks = dataServerTaskPage.getContent();

        JSONArray jsonArray = new JSONArray();
        for(int i=0;i<dataServerTasks.size();++i){
            jsonArray.add((JSONObject)JSONObject.toJSON(dataServerTasks.get(i)));
        }

        JSONObject res = new JSONObject();
        res.put("list", jsonArray);
        res.put("totalNum", dataServerTaskPage.getTotalElements());
        return res;
    }

    public Task findByTaskId(String taskId) {
        return taskDao.findFirstByTaskId(taskId);
    }

    public JSONObject initTaskOutput(String ids, String userName) {
        String[] twoIds = ids.split("&");

        String modelId = twoIds[0];
        String taskId = twoIds[1];

        ComputableModel modelInfo = computableModelDao.findFirstById(modelId);
        modelInfo.setViewCount(modelInfo.getViewCount() + 1);
        // computableModelDao.save(modelInfo);
        redisService.saveItem(modelInfo, ItemTypeEnum.ComputableModel);

        User user = userDao.findFirstByEmail(modelInfo.getAuthor());
        JSONObject userJSON = userService.getInfoFromUserServer(user.getEmail());

        JSONObject userJson = new JSONObject();
        userJson.put("compute_model_user_name", userJSON.getString("name"));
        userJson.put("compute_model_user_oid", user.getId());

        user = userDao.findFirstByEmail(userName);
        userJSON = userService.getInfoFromUserServer(user.getEmail());

        userJson.put("userName", user.getName());
        userJson.put("userOid", user.getId());
        userJson.put("name",userJSON.getString("name"));

        JSONObject result = new JSONObject();

        //获得task信息
        Task task = findByTaskId(taskId);

        JsonResult jsonResult = generateTask(modelId, userName);
        JSONObject data = JSONObject.parseObject(JSONObject.toJSONString(jsonResult.getData()));

        JSONObject model_Info = new JSONObject();
        JSONObject taskInfo = new JSONObject();
        JSONObject dxInfo = new JSONObject();
        JSONObject dxServer = data.getJSONObject("dxServer");

        model_Info.put("name", modelInfo.getName());
        model_Info.put("des", modelInfo.getOverview());
        model_Info.put("date", modelInfo.getCreateTime());
        dxInfo.put("dxIP", dxServer.getString("ip"));
        dxInfo.put("dxPort", dxServer.getString("port"));
        dxInfo.put("dxType", dxServer.getString("type"));
        taskInfo.put("ip", data.getString("ip"));
        taskInfo.put("port", data.getString("port"));
        taskInfo.put("pid", data.getString("pid"));
        taskInfo.put("creater", task.getEmail());
        taskInfo.put("description", task.getDescription());
        taskInfo.put("permission", task.getPermission());
        taskInfo.put("createTime", task.getRunTime());
        taskInfo.put("status", task.getStatus());
        taskInfo.put("outputs", task.getOutputs());
//
        //判断权限信息
        boolean hasPermission = false;

        if (task.getPermission().equals("private")&&!task.getEmail().equals(userName) ) {
            result.put("permission", "forbid");
//            return result;
        } else {
            result.put("permission", "allow");
        }

        List<TaskData> inputs = task.getInputs();
        for(int i=0;i<inputs.size();i++){
            TaskData input=inputs.get(i);
            for(String id:visualTemplateIds){
                String templateId = input.getTemplateId();
                if(templateId!=null) {
                    if (templateId.toLowerCase().equals(id)) {
                        inputs.get(i).setVisual(true);
                        break;
                    }
                }
            }
        }
        taskInfo.put("inputs", inputs);

        boolean hasTest;
        if (modelInfo.getTestDataPath() == null || modelInfo.getTestDataPath().equals("")) {
            hasTest = false;
        } else {
            hasTest = true;
        }
        model_Info.put("hasTest", hasTest);
        JSONObject mdlInfo = convertMdl(modelInfo.getMdl());
        JSONObject mdlObj = mdlInfo.getJSONObject("mdl");
        JSONArray states = mdlObj.getJSONArray("states");
        model_Info.put("states", states);
        //拼接

        result.put("userInfo", userJson);
        result.put("modelInfo", model_Info);
        result.put("taskInfo", taskInfo);
//        result.put("dxInfo", dxInfo);
//         System.out.println(result);

        return result;
    }


    public JSONObject getPublishedTasksByModelId(String modelId, FindDTO findDTO) {
        Pageable pageable = genericService.getPageable(findDTO);

        //获取published task
        Page<Task> tasks = taskDao.findByComputableIdAndPermissionAndStatus(modelId, "public", 2, pageable);
        List<Task> ts = tasks.getContent();
        long total = tasks.getTotalElements();
        JSONArray taskArray = new JSONArray();
        for (Task task : ts) {

            String caculateUser = task.getEmail();
            User user = userService.getByEmail(caculateUser);
            JSONObject userJSON = userService.getInfoFromUserServer(user.getEmail());
            caculateUser = userJSON.getString("name");
            String taskId = task.getTaskId();
            Date runTime = task.getRunTime();
            String permission = task.getPermission();
            String description = task.getDescription();

            JSONObject obj = new JSONObject();
            obj.put("userName", caculateUser);
            obj.put("userId", user.getEmail());
            obj.put("taskId", taskId);
            obj.put("runTime", runTime);
            obj.put("description", description);
            obj.put("permission", permission);
            taskArray.add(obj);

        }

        JSONObject result = new JSONObject();

        result.put("content", taskArray);
        result.put("total", total);

        return result;
    }


    public JSONObject getTasksByModelByUser(String modelId, int page, String email) {
        Sort sort = Sort.by(Sort.Direction.DESC, "runTime");
        Pageable pageable = PageRequest.of(page, 4, sort);
        //获取该用户所有关于task
        Page<Task> tasksOfUser = taskDao.findByComputableIdAndEmailAndStatus(modelId, email, 2, pageable);
        JSONArray taskArray = new JSONArray();
        List<Task> ts = tasksOfUser.getContent();
        long total = tasksOfUser.getTotalElements();
        for (Task task : ts) {

            String caculateUser = task.getEmail();
            String taskId = task.getTaskId();
            Date runTime = task.getRunTime();
            String permission = task.getPermission();
            String description = task.getDescription();

            JSONObject obj = new JSONObject();
            obj.put("userName", caculateUser);
            obj.put("taskId", taskId);
            obj.put("runTime", runTime);
            obj.put("description", description);
            obj.put("permission", permission);
            taskArray.add(obj);

        }

        JSONObject result = new JSONObject();
        result.put("content", taskArray);
        result.put("total", total);

        return result;
    }

    // TODO: 2021/11/15 这里的逻辑等zh的集成做完之后 看下manage server 再重新写下
    public JSONObject getTasksByUserId(String userName, int page, String sortType, int sortAsc) {
        AsyncTask asyncTask = new AsyncTask();
        List<Future> futures = new ArrayList<>();

        Sort sort = Sort.by(sortAsc == 1 ? Sort.Direction.ASC : Sort.Direction.DESC, "runTime");
        Pageable pageable = PageRequest.of(page, 10, sort);
        Page<Task> tasks = taskDao.findAllByEmail(userName, pageable);
        List<Task> ts = tasks.getContent();
        try {
            for (int i = 0; i < ts.size(); i++) {
                Task task = ts.get(i);
                if (task.getStatus() != 2 && task.getStatus() != -1) {
                    JSONObject param = new JSONObject();
                    param.put("ip", task.getIp());
                    param.put("port", task.getPort());
                    param.put("tid", task.getTaskId());
                    param.put("integrate", task.getIntegrate());

                    futures.add(asyncTask.getRecordCallback(param, managerServerIpAndPort));
                }
            }

            for (Future<?> future : futures) {
                while (true) {//CPU高速轮询：每个future都并发轮循，判断完成状态然后获取结果，这一行，是本实现方案的精髓所在。即有10个future在高速轮询，完成一个future的获取结果，就关闭一个轮询
                    if (future.isDone() && !future.isCancelled()) {//获取future成功完成状态，如果想要限制每个任务的超时时间，取消本行的状态判断+future.get(1000*1, TimeUnit.MILLISECONDS)+catch超时异常使用即可。
                        String result = (String) future.get();//获取结果
                        JSONObject jsonResult = JSON.parseObject(result);
                        String tid = jsonResult.getString("tid");
                        int remoteStatus = jsonResult.getInteger("status");

                        Task task = taskDao.findFirstByTaskId(tid);
                        if (jsonResult.getBoolean("integrate")) {
                            List<Model> models = jsonResult.getJSONArray("models").toJavaList(Model.class);

                            if (task.getStatus() != remoteStatus) {
                                task.setStatus(remoteStatus);
                                task.setModels(models);
                                taskDao.save(task);
                                for (int i = 0; i < ts.size(); i++) {
                                    Task task1 = ts.get(i);
                                    if (task1.getTaskId().equals(tid)) {
                                        task1.setStatus(remoteStatus);
                                        task1.setModels(models);
                                        break;
                                    }
                                }
                            }
                        } else {
                            List<TaskData> outputs = jsonResult.getJSONArray("outputs").toJavaList(TaskData.class);

                            if (task.getStatus() != remoteStatus) {
                                task.setStatus(remoteStatus);
                                task.setOutputs(outputs);
                                taskDao.save(task);
                                for (int i = 0; i < ts.size(); i++) {
                                    Task task1 = ts.get(i);
                                    if (task1.getTaskId().equals(tid)) {
                                        task1.setStatus(remoteStatus);
                                        task1.setOutputs(outputs);
                                        break;
                                    }
                                }
                            }
                        }
                        break;//当前future获取结果完毕，跳出while
                    } else {
                        Thread.sleep(1);//每次轮询休息1毫秒（CPU纳秒级），避免CPU高速轮循耗空CPU---》新手别忘记这个
                    }
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }


        JSONObject taskObject = new JSONObject();
        taskObject.put("count", tasks.getTotalElements());
        taskObject.put("tasks", tasks.getContent());

        return taskObject;

    }


    public String[] getVisualTemplateIds(){
        return visualTemplateIds;
    }


    public JsonResult loadDataItemData(TestDataUploadDTO testDataUploadDTO, String email) throws Exception {
        JsonResult jsonResult = new JsonResult();
        String id = testDataUploadDTO.getId();
        ComputableModel computableModel= computableModelDao.findFirstById(id);
        JSONObject mdlJSON = Utils.convertMdl(computableModel.getMdl());


        String[] dataIpAndPort = dataContainerIpAndPort.split(":");
        testDataUploadDTO.setHost(dataIpAndPort[0]);
        testDataUploadDTO.setPort(Integer.parseInt(dataIpAndPort[1]));

        List<UploadDataDTO> uploadDataDTOs = getTestDataUploadArrayDataItem(testDataUploadDTO, mdlJSON);
        if (uploadDataDTOs == null) {
            return ResultUtils.error(-1, "No Test Data");
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
                // e.printStackTrace();
                log.error(e.getMessage());
            } catch (ExecutionException e) {
                // e.printStackTrace();
                log.error(e.getMessage());
            }

        });
        return ResultUtils.success(resultDataDTOs);
    }

    public List<UploadDataDTO> getTestDataUploadArrayDataItem(TestDataUploadDTO testDataUploadDTO, JSONObject mdlJson) throws Exception {
        JSONArray states = mdlJson.getJSONObject("mdl").getJSONArray("states");
        //根据dataItemId获取数据下载链接,并获取数据流
        DataItem dataItem = dataItemDao.findFirstById(testDataUploadDTO.getDataItemId());
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        if (dataItem.getUrl() != null){
            URL url = new URL(dataItem.getUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(60000);
            inputStream = conn.getInputStream();
        }
        String testPath = resourcePath + "/" + testDataUploadDTO.getId();
        File localFile = new File(testPath);
        if (!localFile.exists()) {
            localFile.mkdirs();
        }
        String path = testPath + "/" + "downLoad.zip";
        localFile = new File(path);
        try {
            //将数据下载至resourcePath下
            if (localFile.exists()) {
                //如果文件存在删除文件
                boolean delete = localFile.delete();
                if (delete == false) {
//                    log.error("Delete exist file \"{}\" failed!!!", path, new Exception("Delete exist file \"" + path + "\" failed!!!"));
                }
            }
            //创建文件
            if (!localFile.exists()) {
                //如果文件不存在，则创建新的文件
                localFile.createNewFile();
//                log.info("Create file successfully,the file is {}", path);
            }

            fileOutputStream = new FileOutputStream(localFile);
            byte[] bytes = new byte[1024];
            int len = -1;
            while ((len = inputStream.read(bytes)) != -1) {
                fileOutputStream.write(bytes, 0, len);
            }
            fileOutputStream.close();
            inputStream.close();

        } catch (FileNotFoundException e){
            // e.printStackTrace();
            log.error(e.getMessage());
        }catch (IOException e) {
            // e.printStackTrace();
            log.error(e.getMessage());
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }catch (IOException e) {
                // e.printStackTrace();
                log.error(e.getMessage());
//                logger.error("InputStream or OutputStream close error : {}", e);
            }
        }

        //将写入的zip文件进行解压
        //需要进行判断
        String destDirPath = resourcePath + "/" + testDataUploadDTO.getId();
        FileUtil.zipUncompress(path,destDirPath);
        //解压后删除zip包，此时测试数据路径就变为testPath
        FileUtil.deleteFile(path);

        //下面为复用getTestDataUploadArray()代码
        String modelId = testDataUploadDTO.getId();
        String parentDirectory = testPath;
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

//    @Async
//    public Future<ResultDataDTO> uploadDataToServer(UploadDataDTO uploadDataDTO, TestDataUploadDTO testDataUploadDTO, String userName) {
//        ResultDataDTO resultDataDTO = new ResultDataDTO();
//        resultDataDTO.setEvent(uploadDataDTO.getEvent());
//        resultDataDTO.setStateId(uploadDataDTO.getState());
//        resultDataDTO.setChildren(uploadDataDTO.getChildren());
//        String testDataPath = uploadDataDTO.getFilePath();
//        String url = "http://" + dataContainerIpAndPort + "/data";
//        //拼凑form表单
//        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
//        params.add("name", uploadDataDTO.getEvent());
//        params.add("userId", userName);
//        params.add("serverNode", "china");
//        params.add("origination", "portal");
//
//        //拼凑file表单
//        List<String> filePaths=new ArrayList<>();
////
//        String configParentPath = resourcePath + "/configFile/" + UUID.randomUUID().toString() + "/";
//        File path = new File(configParentPath);
//        path.mkdirs();
//        String configPath = configParentPath + "config.udxcfg";
//        File configFile = new File(configPath);
//
//        ZipStreamEntity zipStreamEntity = null;
//
//        try {
//
//            configFile.createNewFile();
////            File configFile=File.createTempFile("config",".udxcfg");
//
//            Writer out = new FileWriter(configFile);
//            String content = "<UDXZip>\n";
//            content += "\t<Name>\n";
//            String[] paths = testDataPath.split("/");
//            content += "\t\t<add value=\"" + paths[paths.length - 1] + "\" />\n";
//            content += "\t</Name>\n";
//            content += "\t<DataTemplate type=\"" + uploadDataDTO.getType() + "\">\n";
//            content += "\t\t"+uploadDataDTO.getTemplate()+"\n";
//            content += "\t</DataTemplate>\n";
//            content += "</UDXZip>";
//
//            out.write(content);
//            out.flush();
//            out.close();
//
//
//            filePaths.add(testDataPath);
//            filePaths.add(configPath);
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        JSONObject result;
//
//        try {
//            for(int i=0;i<filePaths.size();i++){
//                File uploadFile = new File(filePaths.get(i));
//                FileInputStream fileInputStream = new FileInputStream(uploadFile);
//                // MockMultipartFile(String name, @Nullable String originalFilename, @Nullable String contentType, InputStream contentStream)
//                // 其中originalFilename,String contentType 旧名字，类型  可为空
//                // ContentType.APPLICATION_OCTET_STREAM.toString() 需要使用HttpClient的包
//                MultipartFile multipartFile = new MockMultipartFile(uploadFile.getName(),uploadFile.getName(), ContentType.APPLICATION_OCTET_STREAM.toString(),fileInputStream);
//
//                params.add("datafile", multipartFile.getResource());
//            }
//            result = MyHttpUtils.uploadDataToDataServer(dataContainerIpAndPort,params);
//        } catch (Exception e) {
//            result = null;
//        }
//        if (result == null) {
//            resultDataDTO.setUrl("");
//            resultDataDTO.setTag("");
//        } else {
//            JSONObject res = result;
//            if (res.getIntValue("code") != 1) {
//                resultDataDTO.setUrl("");
//                resultDataDTO.setTag("");
//                resultDataDTO.setSuffix("");
//            } else {
//                JSONObject data = res.getJSONObject("data");
//                String data_url = "http://"+dataContainerIpAndPort+"/data/"+data.getString("source_store_id");
//                String tag = data.getString("file_name");
//                String[] paths=testDataPath.split("\\.");
//                String suffix = paths[paths.length-1];
//                resultDataDTO.setTag(tag);
//                resultDataDTO.setUrl(data_url);
//                resultDataDTO.setSuffix(suffix);
//                resultDataDTO.setVisual(uploadDataDTO.getVisual());
//            }
//        }
//        return new AsyncResult<>(resultDataDTO);
//
//
//    }


    public List<ResultDataDTO> getPublishedData(String taskId) {
        Task task = taskDao.findFirstByTaskId(taskId);

        List<ResultDataDTO> resultDataDTOList = new ArrayList<>();

//        if(task.getStatus()==1){
        for (int i = 0; i < task.getInputs().size(); i++) {
            ResultDataDTO resultDataDTO = new ResultDataDTO();
            resultDataDTO.setUrl(task.getInputs().get(i).getUrl());
            resultDataDTO.setState(task.getInputs().get(i).getStatename());
            resultDataDTO.setEvent(task.getInputs().get(i).getEvent());
            resultDataDTO.setTag(task.getInputs().get(i).getTag());
            resultDataDTO.setSuffix(task.getInputs().get(i).getSuffix());
            resultDataDTO.setChildren(task.getInputs().get(i).getChildren());
            resultDataDTOList.add(resultDataDTO);
        }
        for (int i = 0; i < task.getOutputs().size(); i++) {
            ResultDataDTO resultDataDTO = new ResultDataDTO();
            resultDataDTO.setUrl(task.getOutputs().get(i).getUrl());
            if(task.getOutputs().get(i).getUrl().contains("["))
                resultDataDTO.setUrls(task.getOutputs().get(i).getUrls());
            resultDataDTO.setState(task.getOutputs().get(i).getStatename());
            resultDataDTO.setEvent(task.getOutputs().get(i).getEvent());
            resultDataDTO.setTag(task.getOutputs().get(i).getTag());
            resultDataDTO.setSuffix(task.getOutputs().get(i).getSuffix());
            resultDataDTO.setMultiple(task.getOutputs().get(i).getMultiple());
            resultDataDTO.setChildren(task.getOutputs().get(i).getChildren());
            resultDataDTOList.add(resultDataDTO);
        }
//        }
        return resultDataDTOList;
    }


    public JSONObject searchTasksByUserId(String email, FindDTO findDTO) {

        findDTO.setSortField("runTime");
        Pageable pageable = genericService.getPageable(findDTO);

        Page<Task> modelItems = taskDao.findByComputableNameContainsIgnoreCaseAndEmail(findDTO.getSearchText().trim(), email, pageable);

        JSONObject modelItemObject = new JSONObject();
        modelItemObject.put("count", modelItems.getTotalElements());
        modelItemObject.put("tasks", modelItems.getContent());

        return modelItemObject;

    }


    public JsonResult setDataTaskPrivate(String id) {
        DataServerTask task = dataServerTaskDao.findFirstById(id);
        if (task == null) {
            return ResultUtils.error();
        }
        task.setPermission("private");
        dataServerTaskDao.save(task);
        return ResultUtils.success(task.getPermission());
    }

    public JsonResult setDataTaskPublic(String id) {
        DataServerTask task = dataServerTaskDao.findFirstById(id);
        if (task == null) {
            return ResultUtils.error();
        }
        task.setPermission("public");
        dataServerTaskDao.save(task);
        return ResultUtils.success(task.getPermission());
    }

    public JsonResult setPrivate(String taskId) {
        Task task = taskDao.findFirstByTaskId(taskId);
        if (task == null) {
            return ResultUtils.error();
        }
        task.setPermission("private");
        taskDao.save(task);
        return ResultUtils.success(task.getPermission());
    }

    public JsonResult setTaskDesc(TaskPublishDTO publishDTO) {

        // 如果id没传过来的话新建一个task记录
        if(publishDTO.getId() == null || "".equals(publishDTO.getId())){
            Task newTask = new Task();
            BeanUtils.copyProperties(publishDTO, newTask);
            return ResultUtils.success();
        }

        Task task = taskDao.findFirstByTaskId(publishDTO.getId());
        if (task == null){
            return ResultUtils.error();
        }
        // 任务公开添加的信息
        BeanUtils.copyProperties(publishDTO, task, "id");
        taskDao.save(task);
        return ResultUtils.success();
    }

    public JsonResult setPublic(String taskId) {
        Task task = taskDao.findFirstByTaskId(taskId);
        if (task == null) {
            return ResultUtils.error();
        }

        task.setPermission("public");
        taskDao.save(task);

        // 关联到计算模型去
        ComputableModel computableModel = computableModelDao.findFirstById(task.getComputableId());
        if (computableModel != null){
            List<String> runRecord = computableModel.getRelateTaskRunRecord();
            if (runRecord == null){
                runRecord = new ArrayList<>();
                // 如果初始值为null的话为该属性分配空间的时候computableModel并不会同步更新，它还是指向null
                computableModel.setRelateTaskRunRecord(runRecord);
            }
            if(!runRecord.contains(taskId)){
                runRecord.add(taskId);
            }
        }
        computableModelDao.save(computableModel);

        return ResultUtils.success(task.getPermission());
    }


    public JSONObject pageIntegrateTaskByUserByStatus(String email, String status, int page, String sortType, int sortAsc,String searchText) {

        User user = userDao.findFirstByEmail(email);
        String userName = user.getAccessId();
        if (userName == null){
            return null;
        }


        Sort sort = Sort.by(sortAsc == 1 ? Sort.Direction.ASC : Sort.Direction.DESC, "lastModifiedTime");
        Pageable pageable = PageRequest.of(page, 10, sort);
        Page<IntegratedTask> integratedTaskPage = Page.empty();

        if (status.equals("calculating")) {
            integratedTaskPage = integratedTaskDao.findByUserIdAndIntegrateAndStatusAndTaskNameContainsIgnoreCase(userName, true, 1,searchText ,pageable);
        } else if (status.equals("successful")) {
            integratedTaskPage = integratedTaskDao.findByUserIdAndIntegrateAndStatusAndTaskNameContainsIgnoreCase(userName, true, 2,searchText , pageable);
        } else if (status.equals("failed"))
            integratedTaskPage = integratedTaskDao.findByUserIdAndIntegrateAndStatusAndTaskNameContainsIgnoreCase(userName, true, -1,searchText , pageable);
        else if (status.equals("builded"))
            integratedTaskPage = integratedTaskDao.findByUserIdAndIntegrateAndStatusAndTaskNameContainsIgnoreCase(userName, true, 0,searchText , pageable);
        else
            integratedTaskPage = integratedTaskDao.findByUserIdAndIntegrateAndTaskNameContainsIgnoreCase(userName, true, searchText,pageable);
        List<IntegratedTask> ts = integratedTaskPage.getContent();

        List<IntegratedTask> newTasks = updateUserItdTasks(userName,ts);//先利用这个函数更新一下数据库

        for(IntegratedTask newTask : newTasks){
            for(IntegratedTask task:ts){
                if(newTask.getOid().equals(task.getOid())){
                    task.setModelActions(newTask.getModelActions());
                    task.setDataProcessings(newTask.getDataProcessings());
                }
            }

        }

        JSONObject taskObject = new JSONObject();
        taskObject.put("count", integratedTaskPage.getTotalElements());
        taskObject.put("tasks", ts);

        return taskObject;
    }

    public List<IntegratedTask> updateUserItdTasks(String userName,List<IntegratedTask> itdTaskList) {//多线程通过managerserver更新数据库
        AsyncTask asyncTask = new AsyncTask();
        List<Future> futures = new ArrayList<>();

//        Sort sort = new Sort(Sort.Direction.DESC, "runTime");
//        List<Task> ts = taskDao.findByUserId(userName);
        List<IntegratedTask> taskList = new ArrayList<>();
        try {
            for (int i = 0; i < itdTaskList.size(); i++) {
                IntegratedTask task = itdTaskList.get(i);
                if (task.getStatus() == 1) {
                    JSONObject param = new JSONObject();
                    param.put("tid", task.getTaskId());
                    param.put("integrate", task.getIntegrate());

                    futures.add(asyncTask.getRecordCallback(param, managerServerIpAndPort));
                }
            }


            for (Future<?> future : futures) {
                while (true) {//CPU高速轮询：每个future都并发轮循，判断完成状态然后获取结果，这一行，是本实现方案的精髓所在。即有10个future在高速轮询，完成一个future的获取结果，就关闭一个轮询
                    if (future.isDone() && !future.isCancelled()) {//获取future成功完成状态，如果想要限制每个任务的超时时间，取消本行的状态判断+future.get(1000*1, TimeUnit.MILLISECONDS)+catch超时异常使用即可。
                        String result = (String) future.get();//获取结果
                        if(!result.equals("{}")){
                            JSONObject jsonResult = JSON.parseObject(result);
                            JSONObject taskInfo = jsonResult.getJSONObject("taskInfo");
                            String taskId = jsonResult.getString("taskId");
                            updateIntegratedTaskInfo(taskId,jsonResult);

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
            // e.printStackTrace();
            log.error(e.getMessage());
//            System.out.println(e.getMessage());
        }
        return taskList;
    }

    public JSONObject updateIntegratedTaskInfo(String taskId,JSONObject data) {
        IntegratedTask task = integratedTaskDao.findByTaskId(taskId);
        int status = data.getInteger("status");
        JSONObject taskInfo = data.getJSONObject("taskInfo");

        //更新output
        JSONObject j_modelActionList = taskInfo.getJSONObject("modelActionList");
        JSONObject j_dataProcessingList = taskInfo.getJSONObject("dataProcessingList");
        List<Action> finishedModelActions = converseOutputModelAction(j_modelActionList.getJSONArray("completed"));
        List<Action> failedModelActions = converseOutputModelAction(j_modelActionList.getJSONArray("failed"));
        List<Action> finishedDataProcessings = converseOutputModelAction(j_dataProcessingList.getJSONArray("completed"));
        List<Action> failedDataProcessings = converseOutputModelAction(j_dataProcessingList.getJSONArray("failed"));

        finishedModelActions.addAll(finishedDataProcessings);
        failedModelActions.addAll(failedDataProcessings);
        updateIntegratedTaskOutput(task, finishedModelActions, failedModelActions);

        //todo common task 与 integrated task的合并
        Task comTask = taskDao.findFirstByTaskId(task.getOid());
        switch (status) {
            case 0:
                task.setStatus(1);
//                comTask = taskDao.findFirstByTaskId(task.getTaskId());
//                comTask.setStatus(-1);
                integratedTaskDao.save(task);
//                taskDao.save(comTask);
                break;
            case -1:
                task.setStatus(-1);
                integratedTaskDao.save(task);
                break;
            case 1:
                task.setStatus(2);
                integratedTaskDao.save(task);
                break;
        }
        return data;

    }

    public List<Action> converseOutputModelAction(JSONArray actionArray) {
        List<Action> actionList = new ArrayList<>();
        for (int i = 0; i < actionArray.size(); i++) {
            JSONObject fromAction = actionArray.getJSONObject(i);
            Action action = new ModelAction();
            action.setId(fromAction.getString("id"));
            JSONArray output = fromAction.getJSONObject("outputData").getJSONArray("outputs");
            List<Map<String,Object>> outputDatas = new ArrayList<>();
            for(int j=0;j<output.size();j++){
                Map<String,Object> outputData = new HashMap<>();
                Map<String,Object> dataContent = new HashMap<>();
                JSONObject j_dataContent = ((JSONObject)output.get(j)).getJSONObject("dataContent");
                outputData.put("value",j_dataContent.getString("value"));
                outputData.put("type",j_dataContent.getString("type"));
                outputData.put("fileName",j_dataContent.getString("fileName"));
                outputData.put("suffix",j_dataContent.getString("suffix"));
                outputDatas.add(outputData);
            }
            action.setOutputData(outputDatas);
            actionList.add(action);
        }

        return actionList;
    }


    public String updateIntegratedTaskOutput(IntegratedTask integratedTask, List<Action> finishedModelActions,List<Action> failedModelActions ){
        List<ModelAction> modelActions = integratedTask.getModelActions();
        List<DataProcessing> dataProcessings = integratedTask.getDataProcessings();

        for(ModelAction modelAction:modelActions){
            for(Action finishedAction:finishedModelActions){
                if(modelAction.getId().equals(finishedAction.getId())){
                    modelAction.setStatus(finishedAction.getStatus());
                    modelAction.setPort(finishedAction.getPort());
                    modelAction.setTaskIp(finishedAction.getTaskIp());
                    for(Map<String,Object> output:modelAction.getOutputData()){
                        for(Map<String,Object> newOutput:finishedAction.getOutputData()){
                            output.put("value",newOutput.get("value"));
                            output.put("fileName",newOutput.get("fileName"));
                            output.put("suffix",newOutput.get("suffix"));
                        }
                    }
                }
            }
            for(Action failedAction:failedModelActions){
                if(modelAction.getId().equals(failedAction.getId())){
                    modelAction.setStatus(-1);
                }
            }
        }

        for(DataProcessing dataProcessing:dataProcessings){
            for(Action finishedAction:finishedModelActions){
                if(dataProcessing.getId().equals(finishedAction.getId())){
                    dataProcessing.setStatus(finishedAction.getStatus());
                    dataProcessing.setPort(finishedAction.getPort());
                    dataProcessing.setTaskIp(finishedAction.getTaskIp());
                    for(Map<String,Object> output:dataProcessing.getOutputData()){
                        for(Map<String,Object> newOutput:finishedAction.getOutputData()){
                            output.put("value",newOutput.get("value"));
//                            output.put("fileName",newOutput.get("fileName"));
                            output.put("fileName","result");
//                            output.put("suffix",newOutput.get("suffix"));
                            output.put("suffix","");
                        }
                    }
                }
            }
            for(Action failedAction:failedModelActions){
                if(dataProcessing.getId().equals(failedAction.getId())){
                    dataProcessing.setStatus(-1);
                }
            }
        }
        integratedTask.setModelActions(modelActions);
        integratedTask.setDataProcessings(dataProcessings);

        Date now = new Date();
        integratedTask.setLastModifiedTime(now);

        return integratedTaskDao.save(integratedTask).getOid();
    }

    public JSONObject PageIntegrateTaskByUser(String email, int pageNum, int pageSize, int asc, String sortElement){
        User user = userDao.findFirstByEmail(email);
        String userName = user.getEmail();
        if (userName == null){
            return null;
        }

        Sort sort = Sort.by(asc==1? Sort.Direction.ASC:Sort.Direction.DESC,sortElement);
        Pageable pageable = PageRequest.of(pageNum,pageSize,sort);
        Page<IntegratedTask> integratedTaskPage = integratedTaskDao.findByUserIdAndIntegrate(userName,true,pageable);

        JSONObject result = new JSONObject();
        result.put("total",integratedTaskPage.getTotalElements());
        result.put("content",integratedTaskPage.getContent());

        return result;
    }

    public String saveIntegratedTask(String xml, String mxgraph, List<Map<String,String>> models, List<Map<String,String>> processingTools,
                                     List<ModelAction> modelActions, List<DataProcessing> dataProcessings,List<Map<String,Object>> dataItems, List<Map<String,String>> dataLinks, String userName, String taskName, String description){
        IntegratedTask integratedTask = new IntegratedTask();

        integratedTask.setOid(UUID.randomUUID().toString());
        integratedTask.setModels(models);
        integratedTask.setProcessingTools(processingTools);
        integratedTask.setModelActions(modelActions);
        integratedTask.setDataProcessings(dataProcessings);
        integratedTask.setDataItems(dataItems);
        integratedTask.setDataLinks(dataLinks);
        integratedTask.setXml(xml);
        integratedTask.setMxGraph(mxgraph);
        integratedTask.setStatus(0);
        integratedTask.setIntegrate(true);
        integratedTask.setUserId(userName);
        integratedTask.setTaskName(taskName);
        integratedTask.setDescription(description);
        Date now = new Date();
        if(integratedTask.getCreateTime()==null){
            integratedTask.setCreateTime(now);
        }
        integratedTask.setLastModifiedTime(now);

        return integratedTaskDao.save(integratedTask).getOid();

    }



    public IntegratedTask getIntegratedTaskByOid(String taskOid){
        IntegratedTask integratedTask = integratedTaskDao.findByOid(taskOid);

        return integratedTask;
    }

    //用户更新集成Task的信息
    public IntegratedTask updateIntegratedTask( String taskOid, String xml, String mxgraph, List<Map<String,String>> models,
                                                List<ModelAction> modelActions,List<DataProcessing> dataProcessings, List<Map<String,Object>> dataItems,List<Map<String,String>> dataLinks,String userName,String taskName,String description){
        IntegratedTask integratedTask = integratedTaskDao.findByOid(taskOid);

        integratedTask.setModels(models);
        integratedTask.setModelActions(modelActions);
        integratedTask.setDataProcessings(dataProcessings);
        integratedTask.setDataLinks(dataLinks);
        integratedTask.setDataItems(dataItems);
        integratedTask.setXml(xml);
        integratedTask.setMxGraph(mxgraph);
        integratedTask.setTaskName(taskName);
        integratedTask.setDescription(description);

        Date now = new Date();
        integratedTask.setLastModifiedTime(now);

        return integratedTaskDao.save(integratedTask);

    }

    //更新集成任务的运行信息
    public JSONObject checkIntegratedTask(String taskId){
        JSONObject data = getIntegratedTask(taskId);
        if(data.isEmpty()){
            return data;
        }else {
            return updateIntegratedTaskInfo(taskId,data);
        }
    }

    //从managerserver获取task的最新状态
    public JSONObject getIntegratedTask(String taskId){
        RestTemplate restTemplate=new RestTemplate();
        String url="http://" + managerServerIpAndPort + "/GeoModeling/task/checkTaskStatus?taskId={taskId}";//远程接口
        Map<String, String> params = new HashMap<>();
        params.put("taskId", taskId);

        JSONObject data = new JSONObject();
        ResponseEntity<JSONObject> responseEntity=restTemplate.getForEntity(url,JSONObject.class,params);
        if (responseEntity.getStatusCode()!= HttpStatus.OK){
            throw new MyException("远程服务出错");
        }
        else {
            data = responseEntity.getBody().getJSONObject("data");
        }

        return data;
    }


    public String updateIntegrateTaskId(String taskOid, String taskId){
        IntegratedTask integratedTask = integratedTaskDao.findByOid(taskOid);

        integratedTask.setTaskId(taskId);
        Date now = new Date();
        integratedTask.setLastModifiedTime(now);

        integratedTaskDao.save(integratedTask);
        return taskId;
    }

    public int deleteIntegratedTask(String oid){
        IntegratedTask integratedTask = integratedTaskDao.findByOid(oid);
        if (integratedTask != null) {
            integratedTaskDao.delete(integratedTask);
            return 1;
        } else {
            return -1;
        }
    }

    public String updateIntegrateTaskName(String taskOid,String taskName) {
        IntegratedTask integratedTask = integratedTaskDao.findByOid(taskOid);

        integratedTask.setTaskName(taskName);
        Date now = new Date();
        integratedTask.setLastModifiedTime(now);

        integratedTaskDao.save(integratedTask);
        return taskName;
    }

    public String updateIntegrateTaskDescription(String taskOid,String taskDescription) {
        IntegratedTask integratedTask = integratedTaskDao.findByOid(taskOid);

        integratedTask.setDescription(taskDescription);
        Date now = new Date();
        integratedTask.setLastModifiedTime(now);

        integratedTaskDao.save(integratedTask);
        return taskDescription;
    }

    /**
     * 获取公开的任务信息
     * @param id 任务id
     * @return 任务是公开的返回任务信息，任务不是公开的返回null
     * @author 7bin
     **/
    public Task getPublicTask(String id) {
        Task task = findByTaskId(id);
        if(!"public".equals(task.getPermission())){
            task = null;
        }
        return task;
    }

}
