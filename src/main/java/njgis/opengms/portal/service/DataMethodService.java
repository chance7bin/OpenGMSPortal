package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.dao.*;
import njgis.opengms.portal.entity.doo.AuthorInfo;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.Localization;
import njgis.opengms.portal.entity.doo.data.InvokeService;
import njgis.opengms.portal.entity.doo.model.Resource;
import njgis.opengms.portal.entity.doo.support.MetaData;
import njgis.opengms.portal.entity.doo.support.TaskData;
import njgis.opengms.portal.entity.dto.SpecificFindDTO;
import njgis.opengms.portal.entity.dto.data.dataMethod.DataMethodDTO;
import njgis.opengms.portal.entity.po.DataMethod;
import njgis.opengms.portal.entity.po.DataServerTask;
import njgis.opengms.portal.entity.po.User;
import njgis.opengms.portal.entity.po.Version;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.enums.OperationEnum;
import njgis.opengms.portal.utils.FileUtil;
import njgis.opengms.portal.utils.ResultUtils;
import njgis.opengms.portal.utils.Utils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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

    @Autowired
    ClassificationDao classificationDao;

    @Autowired
    MethodClassificationDao methodClassificationDao;


    @Autowired
    TemplateService templateService;


    @Autowired
    VersionService versionService;

    @Autowired
    NoticeService noticeService;

    @Value("${dataServerManager}")
    private String dataServerManager;

    @Value("${portalDataContainer}")
    private String portalDataContainer;

    @Value("${resourcePath}")
    String resourcePath;

    @Value("${htmlLoadPath}")
    private String htmlLoadPath;



    public JsonResult getMethods(SpecificFindDTO dataMethodsFindDTO){
        return ResultUtils.success(genericService.searchItems(dataMethodsFindDTO, ItemTypeEnum.DataMethod));
    }


    /**
     * @Description 根据id得到页面
     * @Param [id]
     * @return org.springframework.web.servlet.ModelAndView
     **/
    public ModelAndView getPage(String id){
        ModelAndView modelAndView = new ModelAndView();
        try {
            DataMethod dataMethod = (DataMethod)genericService.getById(id,dataMethodDao);

            List<String> classifications = new ArrayList<>();
            for (String classification : dataMethod.getClassifications()) {
                classifications.add(methodClassificationDao.findFirstById(classification).getNameEn());
            }

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
            List<Resource> resources = dataMethod.getResources();

            if (resources != null) {
                for (int i = 0; i < resources.size(); i++) {
                    String path = resources.get(i).getPath();
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




            modelAndView.setViewName("data_application_info");

            // 重新改分类了
            // for (String category: categories){
            //     DataCategorys categorys = dataCategorysDao.findFirstById(category);
            //     String name = categorys.getCategory();
            //     classificationName.add(name);
            // }

            modelAndView.addObject("itemInfo", dataMethod);
            modelAndView.addObject("classifications", classifications);
            modelAndView.addObject("date", dateResult);
            modelAndView.addObject("year", calendar.get(Calendar.YEAR));
            modelAndView.addObject("user", userJson);
            modelAndView.addObject("authorship", authorshipString);
            modelAndView.addObject("resources", resourceArray);
            modelAndView.addObject("lastModifyTime", lastModifyTime);

            modelAndView.addObject("modularType", ItemTypeEnum.DataMethod);
            return modelAndView;



        } catch (Exception e) {
            // System.out.println(e.getMessage());
            // throw new MyException(e.getMessage());
            modelAndView.setViewName("error/404");
            return modelAndView;
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
        if (invokeServices != null){
            for (InvokeService invokeService:invokeServices){
                String token = invokeService.getToken();
                boolean isOnline = isOnline(token);
                if(isOnline){
                    invokeService.setOnlineStatus("online");
                }else {
                    invokeService.setOnlineStatus("offline");
                }
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
        DataMethod dataMethod = dataMethodDao.findFirstById(aid);
        if(dataMethod == null){
            return ResultUtils.error();
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


        return ResultUtils.success(jsonObject);
    }

    /**
     * 根据email查询用户信息
     * @param email
     * @return njgis.opengms.portal.entity.doo.JsonResult
     **/
    public JsonResult getContributorInfo(String email){
        User user = userDao.findFirstByEmail(email);
        JSONObject contributorInfo = new JSONObject();
        if (user == null){
            return ResultUtils.error("user is not exist!");
        }
        contributorInfo.put("name", user.getName());
        contributorInfo.put("userId", user.getEmail());

        return ResultUtils.success(contributorInfo);
    }


    /**
     * 获取xml以及parameter
     * @param aid
     * @param sid
     * @return njgis.opengms.portal.entity.doo.JsonResult
     **/
    public JsonResult getParameter(@PathVariable String aid, @PathVariable String sid) throws IOException, DocumentException {
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
        JSONObject j_result;
        try {
            ResponseEntity<JSONObject> response = restTemplate.exchange(url.trim(), HttpMethod.GET, requestEntity, JSONObject.class);
            j_result = response.getBody();
        }catch (Exception e){
            return ResultUtils.error("request user server err");
        }

        return ResultUtils.success(j_result);
    }


    /**
     * 通过服务id与token获取服务所绑定的数据的信息，包括文件名与url
     * @param sid
     * @param token
     * @return njgis.opengms.portal.entity.doo.JsonResult
     **/
    public JsonResult getRemoteDataInfo(@PathVariable(value = "sid") String sid,@PathVariable(value = "token") String token){
        JsonResult jsonResult;
        try {
//            token = URLEncoder.encode(token, "UTF-8");
            String url = "http://" + dataServerManager + "/files?id=" + sid + "&token=" + token;
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type","application/json");
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<MultiValueMap> requestEntity = new HttpEntity<MultiValueMap>(null, headers);
            ResponseEntity<JSONObject> response = restTemplate.exchange(url.trim(),HttpMethod.GET, requestEntity, JSONObject.class);
            JSONObject j_result = response.getBody();
            if(j_result.getString("code")!=null &&j_result.getString("code").equals("-1")){
                jsonResult = ResultUtils.error("node offline");
            }else{
                jsonResult = ResultUtils.success(j_result);
            }

        }catch (ResourceAccessException e){
            jsonResult = ResultUtils.error("request time out or UnsupportedEncodingException");
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
     * @param email email
     * @param selectData onlineData所选的数据，可选传
     * @param integrate 是否集成的调用，集成的调用则标识"integrate", 可选
     * @return njgis.opengms.portal.entity.doo.JsonResult
     **/
    public JsonResult invokeMethod(String dataMethodId,String serviceId,String serviceName,String[] params,String selectData,
                            String integrate,String email) throws IOException, DocumentException {
        // JsonResult jsonResult = new JsonResult();
        DataServerTask dataServerTask = new DataServerTask();
        Date date = new Date();
        dataServerTask.setRunTime(date);
        // dataServerTask.setOid(UUID.randomUUID().toString());
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
        dataServerTask.setEmail(email);
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
        url="http://" + dataServerManager + "/invokeUrlsDataPcsWithKey";


        String json = postParams.toJSONString();
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        StringEntity postingString = new StringEntity(json,"UTF-8");// json传递
        post.setEntity(postingString);
        post.setHeader("Content-type", "application/json");
        HttpResponse response = null;

        //记录调用次数
        int invokeCount = dataMethod.getInvokeCount();
        invokeCount++;
        dataMethod.setInvokeCount(invokeCount);
        dataMethodDao.save(dataMethod);
        // invokeCount = (invokeCount == null) ? 0 : invokeCount;

        try {
            response = httpClient.execute(post);
        }catch (ResourceAccessException e){
            dataServerTask.setStatus(-1);
            dataServerTaskDao.insert(dataServerTask);
            return ResultUtils.error("request time out!");
        }
        String content = EntityUtils.toString(response.getEntity());
// Log.i("test",content);
//         System.out.println(content);
        JSONObject resp = JSON.parseObject(content);

        log.info(response + "");

        if (resp.getString("code").equals("-1")){
            dataServerTask.setPermission("private");
            dataServerTask.setStatus(-1);//运行失败

            dataServerTaskDao.insert(dataServerTask);
            return ResultUtils.error("code:-1,message:err",resp);
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

        return ResultUtils.success(res);
    }

    /**
     * 新建一个dataMethod条目，并部署部署包
     * @param files 上传的包
     * @param dataMethodDTO
     * @param email
     * @return 插入结果
     */
    public JsonResult insert(List<MultipartFile> files, DataMethodDTO dataMethodDTO, String email){
        JsonResult result;
        DataMethod dataMethod = new DataMethod();
        BeanUtils.copyProperties(dataMethodDTO, dataMethod);

        String path = resourcePath + "/DataApplication/" + dataMethod.getContentType();

        //detail放到localizationList中
        Localization localization = new Localization("en","English",dataMethod.getName(),dataMethodDTO.getDetail());
        List<Localization> local = new ArrayList<>();
        local.add(localization);
        dataMethod.setLocalizationList(local);

        //method放到classifications中
        List<String> cls = new ArrayList<>();
        cls.add(methodClassificationDao.findFirstByNameEn(dataMethod.getMethod()).getId());
        dataMethod.setClassifications(cls);

        List<Resource> resources = new ArrayList<>();
        Utils.saveResource(files, path, email, "", resources);

        if (resources == null){
            result = ResultUtils.error("resources is null");
        }else {
            try {
                dataMethod.setResources(resources);

                // 根据获取的bindTemplateId双向绑定到templated中
                if(dataMethod.getBindDataTemplates() != null){
                    List<String> bindDataTemplates = dataMethod.getBindDataTemplates();
                    List<String> bindDataTemplatesRes = new ArrayList<>();
                    for(String bindDataTemplate:bindDataTemplates){
                        JSONObject jsonObject1 = JSONObject.parseObject(bindDataTemplate);
                        String templateId = jsonObject1.getString("templateId");
                        bindDataTemplatesRes.add(templateId);

                        templateService.addRelatedMethod(templateId,dataMethod.getId());

                    }
                    dataMethod.setBindDataTemplates(bindDataTemplatesRes);

                }
                dataMethod.setAuthor(email);
                dataMethod.setIsAuthor(true);

                Date now = new Date();
                dataMethod.setCreateTime(now);
                dataMethod.setLastModifyTime(now);

                //将服务invokeApplications置入,如果不绑定测试数据，则无需部署，直接创建条目即可
                if(dataMethod.getTestData().size() == 0){
                    dataMethodDao.insert(dataMethod);
                    userService.updateUserResourceCount(email, ItemTypeEnum.DataMethod, "add");
                    result = ResultUtils.success(dataMethod.getId());
                    return result;
                }
                InvokeService invokeService = new InvokeService();
                invokeService.setServiceId(UUID.randomUUID().toString());//
                invokeService.setMethod(dataMethod.getMethod());
                invokeService.setName(dataMethod.getName());

                invokeService.setToken("fdtwTxlnhka8jY66lOT+kKutgZHnvi4NlnDc7QY5jR4=");//75
//                invokeService.setToken("fcky/35Rezr+Kyazr8SRWA==");//33
                invokeService.setContributor(email);
                invokeService.setIsPortal(true);
                List<InvokeService> invokeServices = new ArrayList<>();
                invokeServices.add(invokeService);
                dataMethod.setInvokeServices(invokeServices);

//                dataApplicationDao.insert(dataMethod);

                //部署服务
                result = deployPackage(dataMethod);

                userService.updateUserResourceCount(email, ItemTypeEnum.DataMethod, "add");

//                if (deployRes.getCode() == -1){
//                    result.put("code", -2);
//                }else {
//                    result.put("code", 1);
//                    result.put("id", dataMethod.getId());
//                }
            }catch (Exception e){
                log.info("dataMethod create failed");
                result = ResultUtils.error("dataMethod create failed");
            }
        }



        return result;
    }

    /**
     * 部署文件以及处理服务
     * @param dataMethod 待部署的dataMethod
     * @return 是否成功部署
     */
    public JsonResult deployPackage(DataMethod dataMethod) throws Exception {
        JsonResult res = new JsonResult();
        String dataPath = dataMethod.getTestDataPath();
        //跨域调用容器接口，部署数据
//        String dataUrl="http://172.21.213.111:8899" + "/newFile";
        String dataUrl="http://" + portalDataContainer + "/newFile";

        List<InvokeService> invokeServices = dataMethod.getInvokeServices();
        InvokeService invokeService = invokeServices.get(0);

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> part = new HashMap<>();
        part.put("uid", "0");//存在根目录中
        part.put("instype", "Data");

//        part.put("userToken", "f30f0e82-f6f1-4264-a302-caff7c40ccc9");//33
//        part.put("userToken", "e3cea591-a8a5-4f50-b640-a569eccd94b7");//75
        part.put("userToken", "4cfc7691-c56b-483f-b1c9-bab859be9e00");//75_2
        String newFileId = UUID.randomUUID().toString();
        part.put("id", newFileId);
        part.put("oid", "0");
        part.put("name", dataMethod.getName());
        Date date = new Date();
        part.put("date", date.toString());
        part.put("type", "file");
        part.put("authority", true);

        List<String> dataIds = new ArrayList<>();
        dataIds.add(newFileId);
        invokeService.setDataIds(dataIds);

        MetaData metaData = new MetaData();
        metaData.setDataPath(dataPath);
        metaData.setDataTime("1970/1/1 上午8:00:00");
        metaData.setFileDataType("File");
        metaData.setSecurity("Public");
        metaData.setUid(UUID.randomUUID().toString());

        part.put("meta", metaData);

        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "Bearer "+ StaticParams.access_token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map> httpEntity = new HttpEntity<>(part, headers);
        try {
            ResponseEntity<JSONObject> response = restTemplate.exchange(dataUrl, HttpMethod.PUT, httpEntity, JSONObject.class);
            //解析response
            JSONObject j_result = response.getBody();
            //捕获sdk异常
            if(j_result.getString("code").equals("-1")){
                res = ResultUtils.error(j_result.getString("message"));
                return res;
            }
        }catch (ResourceAccessException e){
            res = ResultUtils.error("deploy file time out");
            return res;
        }


        //部署服务
        String prcUrl="http://" + portalDataContainer + "/newprocess";
        RestTemplate restTemplate2 = new RestTemplate();
        MultiValueMap<String, Object> part2 = new LinkedMultiValueMap<>();
        part2.add("authority", true);
        Date date2 = new Date();
        part2.add("date", date2.toString());

        String serviceId = UUID.randomUUID().toString();
        part2.add("id", serviceId);
        if(dataMethod.getMethod().equals("Processing")||dataMethod.getMethod().equals("Conversion")) {
            part2.add("instype", "Processing");
        }else {
            part2.add("instype", "Visualization");
        }
        part2.add("name", dataMethod.getName());

//        part2.add("id", "I3MXbzRq/NZkbWcKO8tF0w==");//33
        part2.add("id", "5KglgbsDPmrFnA3J9CALzQ==");//75

        //获取xml
        String packageZipPath = resourcePath + "/DataApplication/Package" + dataMethod.getResources().get(0);
        File packageZip = new File(packageZipPath);
        String destDirPath = resourcePath + "/DataApplication/Package/" + UUID.randomUUID().toString();
        //解压zip
        FileUtil.zipUncompress(packageZipPath,destDirPath);
        dataMethod.setPackagePath(destDirPath);

        packageZip = new File(destDirPath);
        File[] files = packageZip.listFiles();
        String fileList = "";
        int paramsCount = 0;
        int i=0;
        for (File file:files){
//            fileList.add(file.getName());
            if (i==0) {
                fileList += file.getName();
            }else {
                fileList += ("," + file.getName());
            }
            i++;
            if (file.getName().substring(file.getName().lastIndexOf(".")).equals(".xml")){
                //解析xml文件
                if (!file.exists()){
                    return null;
                }
                FileInputStream inputStream = new FileInputStream(file);
                int length = inputStream.available();
                byte bytes[] = new byte[length];
                inputStream.read(bytes);
                inputStream.close();
                String xml = new String(bytes, StandardCharsets.UTF_8);

                //解析xml  利用Iterator获取xml的各种子节点
                Document document = DocumentHelper.parseText(xml);
                Element root = document.getRootElement();
                paramsCount =  root.element("Parameter").elements().size();
            }
        }

        part2.add("fileList", fileList);
        part2.add("paramsCount", paramsCount+"");//解析xml
        part2.add("relatedData", newFileId);//dataId
        part2.add("type", "Processing");
        part2.add("uid", "0");

//        part2.add("userToken", "f30f0e82-f6f1-4264-a302-caff7c40ccc9");//33
//        part2.add("userToken", "e3cea591-a8a5-4f50-b640-a569eccd94b7");//75
        part2.add("userToken", "4cfc7691-c56b-483f-b1c9-bab859be9e00");//75_2
        part2.add("processingPath", dataMethod.getPackagePathContainer());

        invokeService.setServiceId(serviceId);
        List<InvokeService> invokeServices1 = new ArrayList<>();
        invokeServices1.add(invokeService);
        dataMethod.setInvokeServices(invokeServices1);

        try {
            JSONObject jsonObject = restTemplate2.postForObject(prcUrl, part2,JSONObject.class);
            //捕获sdk异常
            if(jsonObject.getString("code").equals("-1")){
                res = ResultUtils.error(jsonObject.getString("message"));
                return res;
            }
        }catch (ResourceAccessException e){
            res = ResultUtils.error("deploy server time out");
            return res;
        }
        res = ResultUtils.success(dataMethod.getId());
        dataMethodDao.save(dataMethod);
        return res;
    }


    /**
     * 得到用户上传的dataMethod
     * @param author
     * @param page
     * @param pagesize
     * @param asc
     * @param type 
     * @return org.springframework.data.domain.Page<njgis.opengms.portal.entity.po.DataMethod> 
     * @Author bin
     **/
    public Page<DataMethod> getUsersUploadData(String author, Integer page, Integer pagesize, Integer asc, String type) {
        boolean as = false;
        if (asc == 1) {
            as = true;
        } else {
            as = false;
        }

        Sort sort = Sort.by(as ? Sort.Direction.ASC : Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(page - 1, pagesize, sort);
        return dataMethodDao.findByAuthorAndType(pageable, author, type);

    }


    /**
     * 根据用户email搜索dataMethod
     * @param email
     * @param page
     * @param pageSize
     * @param asc
     * @param searchText
     * @param type
     * @return org.springframework.data.domain.Page<njgis.opengms.portal.entity.po.DataMethod>
     * @Author bin
     **/
    public Page<DataMethod> searchDataByUserId(String email, int page, int pageSize, int asc, String searchText, String type) {
        Sort sort = Sort.by(asc==1 ? Sort.Direction.ASC : Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
        return dataMethodDao.findByAuthorAndNameContainsAndType(pageable, email, searchText, type);
    }



    /**
     * 更新dataMethod
     * @param files
     * @param email 修改者email
     * @param updateDTO
     * @return com.alibaba.fastjson.JSONObject
     * @Author bin
     **/
    public JSONObject update(List<MultipartFile> files, String email, DataMethodDTO updateDTO, String id) {
        JSONObject result = new JSONObject();
        DataMethod dataMethod = dataMethodDao.findFirstById(id);
        String originalItemName = dataMethod.getName();
        List<String> versions = dataMethod.getVersions();
        if (!dataMethod.isLock()) {

            //如果修改者不是作者的话把该条目锁住送去审核
            //提前单独判断的原因是对item统一修改后里面的值已经是新的了，再保存就没效果了
            if (!dataMethod.getAuthor().equals(email)){
                dataMethod.setLock(true);
                dataMethodDao.save(dataMethod);
            } else {
                if (versions == null || versions.size() == 0) {

                    Version version = versionService.addVersion(dataMethod, email, originalItemName);

                    versions = new ArrayList<>();
                    versions.add(version.getId());
                    dataMethod.setVersions(versions);
                }
            }


            // 更新绑定的模板(要在copy属性前更新)
            List<String> newTemplate = new ArrayList<>();
            List<String> oriTemplate = dataMethod.getBindDataTemplates();
            if(updateDTO.getBindDataTemplates() != null){
                List<String> bindDataTemplates = updateDTO.getBindDataTemplates();
                for(String bindDataTemplate:bindDataTemplates){
                    JSONObject jsonObject1 = JSONObject.parseObject(bindDataTemplate);
                    String templateId = jsonObject1.getString("templateId");
                    newTemplate.add(templateId);
                }
            }
            dataMethod.setBindDataTemplates(newTemplate);
            List<String> addTemplateList = Utils.subList(newTemplate,oriTemplate);
            List<String> removeTemplateList = Utils.subList(oriTemplate,newTemplate);
            for (String add : addTemplateList) {
                templateService.addRelatedMethod(add,dataMethod.getId());
            }
            for (String remove : removeTemplateList) {
                templateService.removeRelatedMethod(remove,dataMethod.getId());
            }

            BeanUtils.copyProperties(updateDTO, dataMethod, "bindDataTemplates");

            // 更新localization
            if (dataMethod.getLocalizationList().size() == 0) {
                Localization localization = new Localization("en", "English", dataMethod.getName(), updateDTO.getDetail());
                dataMethod.getLocalizationList().add(localization);
            }
            else {
                dataMethod.getLocalizationList().get(0).setDescription(updateDTO.getDetail());
            }

            //method放到classifications中
            List<String> cls = new ArrayList<>();
            cls.add(methodClassificationDao.findFirstByNameEn(updateDTO.getMethod()).getId());
            dataMethod.setClassifications(cls);



            String path = resourcePath + "/DataApplication/" + updateDTO.getContentType();
            //如果上传新文件
            if (files.size() > 0) {
                List<Resource> resources =new ArrayList<>();
                Utils.saveResource(files, path, email, "",resources);
                if (resources == null) {
                    return null;
                }
                dataMethod.setResources(resources);
            }

            Date now = new Date();

            dataMethod.setLastModifyTime(now);
            dataMethod.setLastModifier(email);

            Version new_version = versionService.addVersion(dataMethod, email,originalItemName);
            if (dataMethod.getAuthor().equals(email)) {
                versions.add(new_version.getId());
                dataMethod.setVersions(versions);
                dataMethodDao.save(dataMethod);
                result.put("method", "update");
                result.put("id", dataMethod.getId());
            } else {

                //发送通知
                noticeService.sendNoticeContainsAllAdmin(email, dataMethod.getAuthor(), dataMethod.getAdmins(), ItemTypeEnum.Version,new_version.getId(), OperationEnum.Edit);
//                List<String> recipientList = Arrays.asList(dataMethod.getAuthor());
//                recipientList = noticeService.addItemAdmins(recipientList,dataMethod.getAdmins());
//                recipientList = noticeService.addPortalAdmins(recipientList);
//                recipientList = noticeService.addPortalRoot(recipientList);
//                noticeService.sendNoticeContains(email, OperationEnum.Edit,version.getId(),recipientList);
                result.put("method", "version");
                result.put("versionId", new_version.getId());
                return result;
            }

            return result;
        } else {
            return null;
        }
    }

    /**
     * 根据id得到dataMethod信息
     * @param id
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult getInfo(String id) {
        DataMethod dataMethod = (DataMethod) genericService.getById(id, dataMethodDao);
//        ComputableModelResultDTO computableModelResultDTO=new ComputableModelResultDTO();
//        ModelItem modelItem=modelItemService.getByOid(computableModel.getRelateModelItem());
//        BeanUtils.copyProperties(computableModel,computableModelResultDTO);
//        computableModelResultDTO.setRelateModelItemName(modelItem.getName());

        JSONArray resourceArray = new JSONArray();
        List<Resource> resources = dataMethod.getResources();

        if (resources != null) {
            for (int i = 0; i < resources.size(); i++) {

                String path = resources.get(i).getPath();

                String[] arr = path.split("\\.");
                String suffix = arr[arr.length - 1];

                arr = path.split("/");
                String name = arr[arr.length - 1].substring(14);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", i);
                jsonObject.put("name", name);
                jsonObject.put("suffix", suffix);
                jsonObject.put("path", htmlLoadPath + resources.get(i));
                resourceArray.add(jsonObject);

            }

        }

        // List<String> cates = new ArrayList<>();          // 现在的 dataMethods 没有分类了
        // cates = dataMethod.getClassifications();
        // List<String> categorys = new ArrayList<>();
        // for(String cate : cates){
        //     DataCategorys dataCategorys = dataItemService.getCategoryById(cate);
        //     categorys.add(dataCategorys.getCategory());
        // }
        //
        // dataMethod.setCategorys(categorys);

        dataMethod.setResourceJson(resourceArray);

        return ResultUtils.success(dataMethod);
    }


    /**
     * 删除dataMethod
     * @param id
     * @return JsonResult
     * @Author bin
     **/
    public JsonResult delete(String id) {
        DataMethod dataMethod = dataMethodDao.findFirstById(id);
        if (dataMethod != null) {
            //删除资源
            String path = resourcePath + "/DataApplication/" + dataMethod.getContentType();
            List<Resource> resources = dataMethod.getResources();
            for (int i = 0; i < resources.size(); i++) {
                Utils.delete(path + resources.get(i));
            }
            //删除template中存的dataMethod
            for (String  template: dataMethod.getBindDataTemplates()) {
                templateService.removeRelatedMethod(template, dataMethod.getId());
            }

            try {
                dataMethodDao.delete(dataMethod);
                userService.updateUserResourceCount(dataMethod.getAuthor(), ItemTypeEnum.DataMethod, "delete");
            }catch (Exception e){
                return ResultUtils.error("delete error");
            }

            return ResultUtils.success();
        } else {
            return ResultUtils.error("未找到该条目");
        }
    }

    /**
     * 根据条目名和当前用户得到数据
     * @param findDTO
     * @param email
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult searchByNameAndAuthor(SpecificFindDTO findDTO,String email){

        return ResultUtils.success(genericService.searchItemsByUser(findDTO, ItemTypeEnum.DataMethod, email));

    }


    public void downloadResource(String id, int index, HttpServletResponse response) {

        DataMethod dataMethod = dataMethodDao.findFirstById(id);

        if (dataMethod == null){
            log.warn("download a resource that does not exist");
            return;
        }


        List<Resource> resources = dataMethod.getResources();

        Resource resource = resources.get(index);

        String path = resourcePath + "/DataApplication/" + dataMethod.getContentType() + resource.getPath();

        File file = new File(path);
        if (!file.exists()){
            log.warn("download a resource that does not exist");
            return;
        }

        int downloadCount = resource.getDownloadCount();
        downloadCount++;
        resource.setDownloadCount(downloadCount);
        dataMethod.setResources(resources);
        dataMethodDao.save(dataMethod);

        FileUtil.downloadFile(path, response);



    }
}
