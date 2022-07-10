package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.component.LoginRequired;
import njgis.opengms.portal.dao.ComputableModelDao;
import njgis.opengms.portal.dao.IntegratedTaskDao;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.entity.dto.task.*;
import njgis.opengms.portal.entity.po.ComputableModel;
import njgis.opengms.portal.entity.po.IntegratedTask;
import njgis.opengms.portal.service.GenericService;
import njgis.opengms.portal.service.TaskService;
import njgis.opengms.portal.utils.ResultUtils;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/11/08
 */
@Slf4j
@RestController
@RequestMapping(value = "/task")
public class TaskController {

    @Autowired
    TaskService taskService;

    @Autowired
    ComputableModelDao computableModelDao;

    @Autowired
    GenericService genericService;

    @Autowired
    IntegratedTaskDao integratedTaskDao;

    @Value("${managerServerIpAndPort}")
    private String managerServerIpAndPort;

    @LoginRequired
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    ModelAndView getTask(@PathVariable("id") String id) {

        ModelAndView modelAndView = new ModelAndView();

        ComputableModel computableModel = computableModelDao.findFirstById(id);

        if(computableModel.getPageConfigJson() == null) {

            modelAndView.setViewName("task");

        }else{

            modelAndView.setViewName("task_gbhm");
        }

        return modelAndView;
    }

    @LoginRequired
    @ApiOperation(value = "Task初始化API，获取模型描述信息，State信息，task以及Dx相关信息 [ /TaskInit/{id} ]")
    @RequestMapping(value = "/init/{id}", method = RequestMethod.GET)
    public JsonResult initTask(@PathVariable("id") String id, HttpServletRequest request){

        id = genericService.formatId(id);

        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
//        String email = "opengms@126.com";
        return taskService.initTask(id, email);
    }


    @LoginRequired
    @ApiOperation(value = "加载默认测试数据，返回数据成功上传之后的url")
    @RequestMapping(value = "/loadTestData/{modelId}", method = RequestMethod.POST)
    public JsonResult loadTestData(@PathVariable String modelId, HttpServletRequest request){

        // HttpSession session = request.getSession();
        // String email = session.getAttribute("email").toString();

        String email = "opengms@126.com";
        return taskService.loadTestData(modelId, email);

    }

    @LoginRequired
    @ApiOperation(value = "调用模型 lists中的oid就是computableModel的id")
    @RequestMapping(value = "/invoke", method = RequestMethod.POST)
    public JsonResult invoke(@RequestBody JSONObject lists, HttpServletRequest request) {

        String oid = genericService.formatId(lists.getString("oid"));
        lists.put("oid",oid);

        HttpSession session = request.getSession();
         String email = session.getAttribute("email").toString();
//        String email = "opengms@126.com";
        return taskService.handleInvoke(lists, email);

    }

    @ApiOperation(value = "得到task的结果 [ /getResult ]")
    @RequestMapping(value = "/result", method = RequestMethod.POST)
    JsonResult getResult(@RequestBody TaskCheckDTO data) {

        JSONObject out=taskService.getTaskResult(data);
        return ResultUtils.success(out);

    }


    @LoginRequired
    @ApiOperation(value = "得到任务信息 [ /getTasksByUserIdByStatus ]")
    @RequestMapping(value="/taskInfo",method = RequestMethod.POST )
    public JsonResult getTasksByUserByStatus(@RequestBody TaskFindDTO taskFindDTO, HttpServletRequest request) {

         HttpSession session = request.getSession();
         String email = session.getAttribute("email").toString();
//        String email = "782807969@qq.com";
        return taskService.getTasksByUserByStatus(email,taskFindDTO);

    }

    // @LoginRequired
    // @ApiOperation(value = "更新数据库用")
    // @RequestMapping(value="/update",method = RequestMethod.PUT )
    // public JsonResult updateUserTasks(@RequestBody TaskFindDTO taskFindDTO, HttpServletRequest request) {
    //
    //     // HttpSession session = request.getSession();
    //     // String email = session.getAttribute("email").toString();
    //     String email = "782807969@qq.com";
    //     return taskService.updateUserTasks(email,taskFindDTO);
    //
    // }


    @LoginRequired
    @ApiOperation(value = "增加描述 [ /addDescription ]")
    @RequestMapping(value = "/description",method = RequestMethod.POST)
    public JsonResult addDescription(@RequestBody JSONObject obj)
    {
        String taskId=obj.get("taskId").toString();
        String description=obj.get("description").toString();
        return taskService.addDescription(taskId,description);

    }


    @LoginRequired
    @ApiOperation(value = "删除任务")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    JsonResult delete(@PathVariable String id) {
        return taskService.delete(id);
    }

    @ApiOperation(value = "得到数据处理信息 [ /getDataProcessings ]")
    @RequestMapping(value = "/dataProcessing",method = RequestMethod.GET)
    public JsonResult getDataProcessings() throws IOException, URISyntaxException, DocumentException {
        return ResultUtils.success(taskService.getDataProcessings());

    }


    @LoginRequired
    @ApiOperation(value = "得到dataMethod任务的输出结果")
    @RequestMapping(value = "/dataTaskOutput/{id}", method = RequestMethod.GET)
    public ModelAndView getDataTaskOutput(@PathVariable("id") String id, HttpServletRequest request) {
        HttpSession session = request.getSession();
        ModelAndView modelAndView = new ModelAndView();
        String email = request.getSession().getAttribute("email").toString();
        JSONObject info=taskService.initDataTaskOutput(id, email);

        modelAndView.setViewName("dataTaskOutput");

        modelAndView.addObject("info", info);
        return modelAndView;
    }

    @LoginRequired
    @ApiOperation(value = "得到dataMethod任务 [ /getDataTasks ]")
    @RequestMapping(value = "/dataTasks", method = RequestMethod.POST)
    JsonResult getDataTasks(@RequestBody DataMethodTaskFindDTO dataTasksFindDTO, HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return ResultUtils.success(taskService.getDataTasks(email, dataTasksFindDTO));

    }

    @ApiOperation(value = "根据taskId查任务 [ /getTaskByTaskId ]")
    @RequestMapping(value="/taskByTaskId",method = RequestMethod.GET)
    public JsonResult getTaskByTaskId(@RequestParam(value="id") String taskId){
        return ResultUtils.success(taskService.findByTaskId(taskId));

    }

    @LoginRequired
    @ApiOperation(value = "得到计算任务的输出结果")
    @RequestMapping(value = "/output/{id}", method = RequestMethod.GET)
    ModelAndView getTaskOutput(@PathVariable("id") String ids, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        String email = request.getSession().getAttribute("email").toString();
        JSONObject info=taskService.initTaskOutput(ids, email);

        modelAndView.setViewName("taskOutput");

        modelAndView.addObject("info", info);
        return modelAndView;
    }


    @LoginRequired
    @ApiOperation(value = "获取其他用户发布的运行成功记录 [ /getPublishedTasksByModel ]")
    @RequestMapping(value="/publishedTasksByModel",method = RequestMethod.GET)
    public JsonResult getTasksByModel(@RequestParam(value = "modelId") String modelId, @RequestParam(value = "page")int page, HttpServletRequest request){
        FindDTO findDTO = new FindDTO();
        findDTO.setSortField("runTime");
        findDTO.setAsc(false);
        findDTO.setPage(page + 1);
        findDTO.setPageSize(4);
        return ResultUtils.success(taskService.getPublishedTasksByModelId(modelId,findDTO));
    }


    @LoginRequired
    @ApiOperation(value = "获取用户自己运行成功的记录 [ /getTasksByModelByUser ]")
    @RequestMapping(value="/tasksByModelByUser",method = RequestMethod.GET)
    public JsonResult getTasksByModelByUser(@RequestParam(value = "modelId") String modelId, @RequestParam(value = "page")int page, HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = request.getSession().getAttribute("email").toString();
        return ResultUtils.success(taskService.getTasksByModelByUser(modelId,page,email));
    }

    @LoginRequired
    @ApiOperation(value = "根据调用者email获取task(那个没有分页的用这个替代) [ /getTasksByUserId ]")
    @RequestMapping(value = "/tasksByEmail", method = RequestMethod.GET)
    public JsonResult getTasksByUserId(HttpServletRequest request,@RequestParam(value="page") int page,
                                @RequestParam(value="sortType") String sortType,
                                @RequestParam(value="asc") int sortAsc){

        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();

        return ResultUtils.success(taskService.getTasksByUserId(email,page,sortType,sortAsc));
    }

    @ApiOperation(value = "得到可视化模板的id")
    @RequestMapping(value="/visualTemplateIds", method = RequestMethod.GET)
    public JsonResult getVisualTemplateIds(){
        return ResultUtils.success(taskService.getVisualTemplateIds());
    }

    @LoginRequired
    @ApiOperation(value = "加载data item的数据")
    @RequestMapping(value = "/loadDataItemData", method = RequestMethod.POST)
    public JsonResult loadDataItemData(@RequestBody TestDataUploadDTO testDataUploadDTO,HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return taskService.loadDataItemData(testDataUploadDTO,email);
    }

    @LoginRequired
    @ApiOperation(value = "加载其他用户发布数据，返回数据成功上传之后的url")
    @RequestMapping(value ="/loadPublishedData", method = RequestMethod.POST)
    public JsonResult loadPublishedData(@RequestBody String taskId){
        List<ResultDataDTO> resultDataDTOS = taskService.getPublishedData(taskId);
        if(resultDataDTOS == null||resultDataDTOS.size()==0){
            return ResultUtils.error(-1,"No Test Data");
        }

        return ResultUtils.success(resultDataDTOS);

    }


    @LoginRequired
    @ApiOperation(value = "查找用户的任务 [ /searchTasksByUserId ]")
    @RequestMapping (value="/taskByUser",method = RequestMethod.POST)
    public JsonResult searchTasksByUserId(HttpServletRequest request,
                                          @RequestBody FindDTO findDTO){

        HttpSession session=request.getSession();
        String email=session.getAttribute("email").toString();
        JSONObject result=taskService.searchTasksByUserId(email,findDTO);
        return ResultUtils.success(result);
    }

    @LoginRequired
    @ApiOperation(value = "把数据方法的任务私有化 [ /setDataTaskPrivate ]")
    @RequestMapping(value = "/dataMethod/privatization/{id}",method = RequestMethod.POST)
    public JsonResult setDataTaskPrivate(@PathVariable String id)
    {
        return taskService.setDataTaskPrivate(id);
    }


    @LoginRequired
    @ApiOperation(value = "把数据方法的任务公有化 [ /setDataTaskPublic ]")
    @RequestMapping(value = "/dataMethod/publicization/{id}",method = RequestMethod.POST)
    public JsonResult setDataTaskPublic(@PathVariable String id)
    {
        return taskService.setDataTaskPublic(id);
    }

    @LoginRequired
    @ApiOperation(value = "把任务私有化 [ /setPrivate ]")
    @RequestMapping(value = "/privatization/{taskId}",method = RequestMethod.POST)
    public JsonResult setPrivate(@PathVariable String taskId)
    {
        return taskService.setPrivate(taskId);

    }

    @LoginRequired
    @ApiOperation(value = "把任务公有化 [ /setPublic ]")
    @RequestMapping(value = "/publicization/{taskId}",method = RequestMethod.POST)
    public JsonResult setPublic(@PathVariable String taskId)
    {
        return taskService.setPublic(taskId);
    }


    @RequestMapping(value="/runIntegratedTask",method = RequestMethod.POST)
    JsonResult runIntegratedModel(@RequestParam("file") MultipartFile file,
                                  @RequestParam("name") String name,
                                  @RequestParam("taskOid") String taskOid,
                                  HttpServletRequest request) throws IOException {
        HttpSession session = request.getSession();
        if(session.getAttribute("uid")==null) {
            return ResultUtils.error(-1, "no login");
        }
        else {
            try{
                String username = session.getAttribute("uid").toString();
                RestTemplate restTemplate=new RestTemplate();
                String url="http://" + managerServerIpAndPort + "/GeoModeling/task/runTask";//远程接口
//                logger.info(url);
                String suffix="."+ FilenameUtils.getExtension(file.getOriginalFilename());
                File temp=File.createTempFile("temp",suffix);
                file.transferTo(temp);
                FileSystemResource resource = new FileSystemResource(temp);
                MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
                param.add("file", resource);
                param.add("userName",username);
//                logger.info("param");
                HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(param);
                ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, JSONObject.class);
                if (responseEntity.getStatusCode()!= HttpStatus.OK){
                    throw new Exception("远程服务出错");
                }
                else{
                    JSONObject body=responseEntity.getBody();
                    if(body.getInteger("code")==-1){
                        return ResultUtils.error(-2,body.getString("msg"));
                    }
                    else {
                        String taskId = responseEntity.getBody().getString("data");

                        IntegratedTask task = integratedTaskDao.findByOid(taskOid);
                        task.setTaskId(taskId);
                        task.setStatus(1);
                        integratedTaskDao.save(task);
                        return ResultUtils.success(taskId);
                    }
                }
            }catch (Exception e){
                // e.printStackTrace();
                log.error(e.getMessage());
                return ResultUtils.error(-1,"err");
            }


        }
    }

    @LoginRequired
    @RequestMapping(value="/pageIntegrateTaskByUserByStatus",method = RequestMethod.GET )
    JsonResult pageIntegrateTaskByUserByStatus(HttpServletRequest request,
                                               @RequestParam(value="status") String status,
                                               @RequestParam(value="page") int page,
                                               @RequestParam(value="sortType") String sortType,
                                               @RequestParam(value="asc") int sortAsc,
                                               @RequestParam(value="searchText") String searchText
    ) {
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();

        JSONObject result = taskService.pageIntegrateTaskByUserByStatus(email,status,page,sortType,sortAsc,searchText);

        if(result == null){
            return ResultUtils.error(-1, "error");
        }else{
            return ResultUtils.success(result);
        }

    }

    @LoginRequired
    @RequestMapping(value = "/pageIntegrateTaskByUser",method = RequestMethod.GET)
    JsonResult pageIntegrateTaskByUser(@RequestParam(value = "pageNum") int pageNum,
                                       @RequestParam(value = "pageSize") int pageSize,
                                       @RequestParam(value = "asc") int asc,
                                       @RequestParam(value = "sortElement") String sortElement,
                                       HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return ResultUtils.success(taskService.PageIntegrateTaskByUser(email,pageNum,pageSize,asc,sortElement));

    }

}
