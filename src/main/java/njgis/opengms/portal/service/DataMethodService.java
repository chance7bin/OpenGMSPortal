package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.dao.DataMethodDao;
import njgis.opengms.portal.dao.DataServerTaskDao;
import njgis.opengms.portal.dao.UserDao;
import njgis.opengms.portal.entity.doo.AuthorInfo;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.MyException;
import njgis.opengms.portal.entity.doo.data.InvokeService;
import njgis.opengms.portal.entity.doo.support.TaskData;
import njgis.opengms.portal.entity.po.DataMethod;
import njgis.opengms.portal.entity.po.DataServerTask;
import njgis.opengms.portal.entity.po.User;
import njgis.opengms.portal.utils.ResultUtils;
import njgis.opengms.portal.utils.Utils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/16
 */
@Service
@Slf4j
public class DataMethodService {

    @Autowired
    DataMethodDao dataMethodDao;

    @Autowired
    UserService userService;

    @Autowired
    GenericService genericService;

    @Autowired
    UserDao userDao;

    @Autowired
    DataServerTaskDao dataServerTaskDao;


    @Value("${dataServerManager}")
    private String dataServerManager;

    /**
     * @Description 根据id得到页面
     * @Param [id]
     * @return org.springframework.web.servlet.ModelAndView
     **/
    public ModelAndView getPage(String id){
        try {
            DataMethod dataMethod = dataMethodDao.findFirstById(id);
            List<String> classifications = dataMethod.getClassifications();

            //时间
            Date date = dataMethod.getCreateTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dateResult = simpleDateFormat.format(date);

            //用户信息
            JSONObject userJson = userService.getItemUserInfoByEmail(dataMethod.getAuthor());
            //资源信息
            JSONArray resourceArray = new JSONArray();
            List<String> resources = dataMethod.getResources();

            if (resources != null) {
                for (int i = 0; i < resources.size(); i++) {
                    String path = resources.get(i);
                    String[] arr = path.split("\\.");
                    String suffix = arr[arr.length - 1];
                    arr = path.split("/");
                    String name = null;
                    if (dataMethod.getBatch()!=null&&dataMethod.getBatch() == true){
                        name = arr[arr.length - 1];
                    }else {
                        name = arr[arr.length - 1].substring(14);
                    }
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", name);
                    jsonObject.put("suffix", suffix);
                    jsonObject.put("path", resources.get(i));
                    resourceArray.add(jsonObject);
                }
            }

            String lastModifyTime = simpleDateFormat.format(dataMethod.getLastModifyTime());

            //authorship
            String authorshipString="";
            List<AuthorInfo> authorshipList=dataMethod.getAuthorships();
            if(authorshipList!=null){
                for (AuthorInfo author:authorshipList) {
                    if(authorshipString.equals("")){
                        authorshipString+=author.getName();
                    }
                    else{
                        authorshipString+=", "+author.getName();
                    }

                }
            }


            ModelAndView modelAndView = new ModelAndView();

            modelAndView.setViewName("data_application_info");

            // 重新改分类了
            // for (String category: categories){
            //     DataCategorys categorys = dataCategorysDao.findFirstById(category);
            //     String name = categorys.getCategory();
            //     classificationName.add(name);
            // }

            modelAndView.addObject("dataMethodInfo", dataMethod);
            modelAndView.addObject("classifications", classifications);
            modelAndView.addObject("date", dateResult);
            modelAndView.addObject("year", calendar.get(Calendar.YEAR));
            modelAndView.addObject("user", userJson);
            modelAndView.addObject("authorship", authorshipString);
            modelAndView.addObject("resources", resourceArray);
            modelAndView.addObject("lastModifyTime", lastModifyTime);


            return modelAndView;



        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new MyException(e.getMessage());
        }

    }


    /**
     * @Description 根据id查找与dataMethod关联的invoke service
     * @Author bin
     * @Param [id]
     * @return njgis.opengms.portal.entity.po.DataMethod
     **/
    public DataMethod getApplicationById(String id) throws UnsupportedEncodingException {
        DataMethod dataMethod = dataMethodDao.findFirstById(id);
        dataMethod = (DataMethod) genericService.recordViewCount(dataMethod);
        dataMethodDao.save(dataMethod);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("dataMethod", dataMethod);
        List<InvokeService> invokeServices = dataMethod.getInvokeServices();
        for (InvokeService invokeService:invokeServices){
            String token = invokeService.getToken();
            boolean isOnline = isOnline(token);
            if(isOnline){
                invokeService.setOnlineStatus("online");
            }else {
                invokeService.setOnlineStatus("offline");
            }
        }
        return dataMethod;
    }


    /**
     * 判断token是否在线
     * @param token token值
     * @return 是否在线，在线为true，离线为false
     * @throws UnsupportedEncodingException 抛出异常
     */
    public Boolean isOnline(String token) throws UnsupportedEncodingException {
        token = URLEncoder.encode(token, "UTF-8");
        String url = "http://" + dataServerManager + "/state?token=" + token;
        log.info(url);

        //调用url
        RestTemplate restTemplate = new RestTemplate();

        String response = restTemplate.getForObject(url,String.class);
        JSONObject jsonObject = JSON.parseObject(response);

        if(jsonObject.getString("code").equals("0")){
            return true;
        }else {
            return false;
        }
    }


    /**
     * @Description 获取服务模型相关信息
     * @Param [aid, sid]
     * @return njgis.opengms.portal.entity.doo.JsonResult
     **/
    public JsonResult getServiceInfo(@PathVariable String aid, @PathVariable String sid) throws UnsupportedEncodingException {
        JsonResult jsonResult = new JsonResult();
        DataMethod dataMethod = dataMethodDao.findFirstById(aid);
        if(dataMethod == null){
            jsonResult.setMsg("err");
            jsonResult.setCode(-1);
            return jsonResult;
        }
        JSONObject jsonObject = new JSONObject();

        //invokeService
        List<InvokeService> invokeServices = dataMethod.getInvokeServices();
        boolean isPortal = false;
        List<String> dataIds = null;
        for (InvokeService invokeService:invokeServices){
            if(invokeService.getServiceId().equals(sid)){
                jsonObject.put("service", invokeService);
                isPortal = invokeService.getIsPortal();
                dataIds = invokeService.getDataIds();
                String token = invokeService.getToken();
                jsonObject.put("onlineStatus", isOnline(token));
                break;
            }
        }
        //testData
        if (isPortal) {
            jsonObject.put("testData", dataMethod.getTestData());
        }else {
            jsonObject.put("testData", dataIds);//这个需要获取数据信息的接口才能使用
        }
        jsonObject.put("application", dataMethod);


        jsonResult.setData(jsonObject);
        return jsonResult;
    }

    /**
     * 根据email查询用户信息
     * @param email
     * @return njgis.opengms.portal.entity.doo.JsonResult
     **/
    public JsonResult getContributorInfo(String email){
        JsonResult res = new JsonResult();
        User user = userDao.findFirstByEmail(email);
        JSONObject contributorInfo = new JSONObject();
        if (user == null){
            res.setCode(-1);
            res.setMsg("user is not exist!");
            return res;
        }
        contributorInfo.put("name", user.getName());
        contributorInfo.put("userId", user.getEmail());
        res.setData(contributorInfo);

        return res;
    }


    /**
     * 获取xml以及parameter
     * @param aid
     * @param sid
     * @return njgis.opengms.portal.entity.doo.JsonResult
     **/
    public JsonResult getParameter(@PathVariable String aid, @PathVariable String sid) throws IOException, DocumentException {
        JsonResult jsonResult = new JsonResult();
        DataMethod dataMethod = dataMethodDao.findFirstById(aid);
        //先取到service信息
        InvokeService invokeService = new InvokeService();
        List<InvokeService> invokeServices = dataMethod.getInvokeServices();
        for (InvokeService invokeService1 : invokeServices){
            if (invokeService1.getServiceId().equals(sid)){
                invokeService = invokeService1;
                break;
            }
        }
        JSONObject jsonObject = new JSONObject();
        String metaDetail = null;
//        if (!invokeService.getIsPortal()){
        String token = invokeService.getToken();//需要存起来，拿token
        token = URLEncoder.encode(token, "UTF-8");
        String url = "http://" + dataServerManager + "/capability?id=" + invokeService.getServiceId();
        String type = invokeService.getMethod();
        if(type.equals("Conversion")||type.equals("Processing")){
            type = "Processing";
        }
        url += ("&type=" + type);
        url += ("&token=" + token);
        log.info(url);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/json");

        //调用url
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> httpMessageConverters = restTemplate.getMessageConverters();
        httpMessageConverters.stream().forEach(httpMessageConverter -> {
            if(httpMessageConverter instanceof StringHttpMessageConverter){
                StringHttpMessageConverter messageConverter = (StringHttpMessageConverter) httpMessageConverter;
                messageConverter.setDefaultCharset(Charset.forName("UTF-8"));
            }
        });
//            String response = restTemplate.getForObject(url,String.class);
        HttpEntity<MultiValueMap> requestEntity = new HttpEntity<MultiValueMap>(null, headers);
        ResponseEntity<JSONObject> response = restTemplate.exchange(url.trim(), HttpMethod.GET, requestEntity, JSONObject.class);
        JSONObject j_result = response.getBody();
        jsonResult.setData(j_result);
        jsonResult.setCode(0);
        jsonResult.setMsg("suc");

        return jsonResult;
    }


    /**
     * 通过服务id与token获取服务所绑定的数据的信息，包括文件名与url
     * @param sid
     * @param token
     * @return njgis.opengms.portal.entity.doo.JsonResult
     **/
    public JsonResult getRemoteDataInfo(@PathVariable(value = "sid") String sid,@PathVariable(value = "token") String token){
        JsonResult jsonResult = new JsonResult();
        try {
//            token = URLEncoder.encode(token, "UTF-8");
            String url = "http://111.229.14.128:8898/files?id=" + sid + "&token=" + token;
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type","application/json");
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<MultiValueMap> requestEntity = new HttpEntity<MultiValueMap>(null, headers);
            ResponseEntity<JSONObject> response = restTemplate.exchange(url.trim(),HttpMethod.GET, requestEntity, JSONObject.class);
            JSONObject j_result = response.getBody();
            if(j_result.getString("code")!=null &&j_result.getString("code").equals("-1")){
                jsonResult.setMsg("node offline");
                jsonResult.setCode(-1);
                jsonResult.setData(j_result);
            }else{
                jsonResult.setData(j_result);
                jsonResult.setCode(0);
                jsonResult.setMsg("success");
            }

        }catch (ResourceAccessException e){
            jsonResult.setCode(1);
            jsonResult.setMsg("request time out or UnsupportedEncodingException");
            return jsonResult;
        }
        return jsonResult;
    }

    /**
     * 调用服务
     * @param dataMethodId dataMethodId
     * @param serviceId serviceId
     * @param serviceName serviceName
     * @param params 调用所需的参数
     * @param request request
     * @param selectData onlineData所选的数据，可选传
     * @param integrate 是否集成的调用，集成的调用则标识"integrate", 可选
     * @return njgis.opengms.portal.entity.doo.JsonResult
     **/
    public JsonResult invokeMethod(String dataMethodId,String serviceId,String serviceName,String[] params,String selectData,
                            String integrate,HttpServletRequest request) throws IOException, DocumentException {
        JsonResult jsonResult = new JsonResult();
        DataServerTask dataServerTask = new DataServerTask();
        Date date = new Date();
        dataServerTask.setRunTime(date);
        dataServerTask.setOid(UUID.randomUUID().toString());
        dataServerTask.setServiceId(serviceId);
        dataServerTask.setServiceName(serviceName);
//        dataServerTask.setDataType(dataType);
        if (integrate!=null){
            dataServerTask.setIntegrate(true);
        }else {
            dataServerTask.setIntegrate(false);
        }


        DataMethod dataMethod = dataMethodDao.findFirstById(dataMethodId);
        List<InvokeService> invokeServices = dataMethod.getInvokeServices();
        //门户测试解绑
        HttpSession session=request.getSession();
        if(session.getAttribute("email")==null){
            return ResultUtils.error(-1,"no login");
        }
        String reqUsrId = session.getAttribute("email").toString();
        dataServerTask.setUserId(reqUsrId);
        //String reqUsrId = "33";//门户测试时注释掉

        InvokeService invokeService = null;
        for (InvokeService invokeService1 : invokeServices){
            if(invokeService1.getServiceId().equals(serviceId)){
                invokeService = invokeService1;
                break;
            }
        }
        //三种接口都需要的参数
        String token = invokeService.getToken();//需要存起来，拿token
        token = URLEncoder.encode(token, "UTF-8");

        //具体invoke,获取结果数据
        String url = null;
        JSONObject urlRes = null;
        JSONObject input = new JSONObject();

        //将所有参数放到一个JSON中
        List<TaskData> inputs = new ArrayList<>();
        try{
            JSONArray object = (JSONArray) JSONArray.parse(selectData);
            for (int i=0;i<object.size();i++){
                JSONObject jsonObject = object.getJSONObject(i);
                String fileName = jsonObject.getString("name");
                TaskData taskData = new TaskData();
                taskData.setTag(fileName.substring(0, fileName.lastIndexOf(".")));
                taskData.setSuffix(fileName.substring(fileName.lastIndexOf(".")).substring(1));
                taskData.setUrl(jsonObject.getString("url"));
                inputs.add(taskData);
            }

            dataServerTask.setInputs(inputs);
        }catch (Exception e){
            input.put("input", selectData);
            dataServerTask.setInput(input);
        }
        JSONObject postParams = new JSONObject();
        postParams.put("token", token);
        postParams.put("pcsId", serviceId);

        postParams.put("params", params);


        JSONArray postUrls = new JSONArray();
//        MultiValueMap<String, Object> part = new LinkedMultiValueMap<>();

        if(selectData!=null) {
            JSONArray jsonArray = JSONArray.parseArray(selectData);
            JSONObject tmp = new JSONObject();
//                String[] resUrl = new String[jsonArray.size()];
            for (int i=0;i<jsonArray.size();i++){
                tmp.put(jsonArray.getJSONObject(i).getString("name"), jsonArray.getJSONObject(i).getString("url"));
            }
            postParams.put("urls", tmp);
        }
        url="http://111.229.14.128:8898/invokeUrlsDataPcsWithKey";


        String json = postParams.toJSONString();
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        StringEntity postingString = new StringEntity(json,"UTF-8");// json传递
        post.setEntity(postingString);
        post.setHeader("Content-type", "application/json");
        HttpResponse response = null;
        try {
            response = httpClient.execute(post);
        }catch (ResourceAccessException e){
            jsonResult.setCode(1);
            dataServerTask.setStatus(-1);
            dataServerTaskDao.insert(dataServerTask);
            jsonResult.setMsg("request time out!");
            return jsonResult;
        }
        String content = EntityUtils.toString(response.getEntity());
// Log.i("test",content);
        System.out.println(content);
        JSONObject resp = JSON.parseObject(content);

        log.info(response + "");

        if (resp.getString("code").equals("-1")){
            jsonResult.setMsg("code:-1,message:err");
            jsonResult.setCode(-2);
            jsonResult.setData(resp);

            dataServerTask.setPermission("private");
            dataServerTask.setStatus(-1);//运行失败

            dataServerTaskDao.insert(dataServerTask);
            return jsonResult;
        }
//
        urlRes = resp.getJSONObject("urls");
//            JSONObject
        List<TaskData> taskDatas = new ArrayList<>();
        for(String fileName:urlRes.keySet()){
            TaskData taskData = new TaskData();
            taskData.setTag(fileName.substring(0, fileName.lastIndexOf(".")));
            String suffic = fileName.substring(fileName.lastIndexOf(".")).substring(1);
            taskData.setSuffix(suffic);
            taskData.setUrl(urlRes.getString(fileName));
            taskData.setVisualUrl(urlRes.getString(fileName) + "?type=" + suffic);
            taskDatas.add(taskData);
        }

        Date date1 = new Date();
        dataServerTask.setFinishTime(date1);
        dataServerTask.setPermission("private");
//        JSONObject output = new JSONObject();
//        output.put("output", urlRes);
        dataServerTask.setOutputs(taskDatas);
//        dataServerTask.setOutput(urlRes);
        dataServerTask.setStatus(2);//成功运行

        dataServerTaskDao.insert(dataServerTask);
//        invokeService.setCacheUrl(urlRes);
//         dataMethod.setInvokeServices(invokeServices);
//         dataMethodDao.save(dataMethod);
        JSONObject res = new JSONObject();
        res.put("invokeService", invokeService);
        res.put("task", dataServerTask);

        jsonResult.setData(res);


        jsonResult.setCode(0);
        return jsonResult;
    }
}
