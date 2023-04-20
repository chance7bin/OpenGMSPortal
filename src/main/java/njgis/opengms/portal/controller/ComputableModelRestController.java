package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import njgis.opengms.portal.component.annotation.LoginRequired;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.base.PortalItem;
import njgis.opengms.portal.entity.doo.intergrate.Model;
import njgis.opengms.portal.entity.doo.intergrate.ModelParam;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.entity.dto.SpecificFindDTO;
import njgis.opengms.portal.entity.dto.UserFindDTO;
import njgis.opengms.portal.entity.dto.community.KnowledgeDTO;
import njgis.opengms.portal.entity.dto.task.TaskFindDTO;
import njgis.opengms.portal.entity.po.ComputableModel;
import njgis.opengms.portal.entity.po.Task;
import njgis.opengms.portal.entity.po.User;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.service.*;
import njgis.opengms.portal.utils.ResultUtils;
import njgis.opengms.portal.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description Computable Model Controller
 * @Author kx
 * @Date 21/10/12
 * @Version 1.0.0
 */
@RestController
@RequestMapping(value = "/computableModel")
public class ComputableModelRestController {

    @Autowired
    GenericService genericService;

    @Autowired
    ComputableModelService computableModelService;

    @Autowired
    UserService userService;

    @Autowired
    TaskService taskService;

    @Autowired
    RepositoryService repositoryService;


    /**
     * @Description 根据id获取计算模型详情页面
     * @param id
     * @Return org.springframework.web.servlet.ModelAndView
     * @Author kx
     * @Date 21/10/12
     **/
    @ApiOperation(value = "根据id获取计算模型详情页面")
    @RequestMapping(value="/{id}",method = RequestMethod.GET)
    ModelAndView get(@PathVariable("id") String id, HttpServletRequest request){
        PortalItem portalItem = genericService.getPortalItem(id, ItemTypeEnum.ComputableModel);
        ModelAndView modelAndView = genericService.checkPrivatePageAccessPermission(portalItem, Utils.checkLoginStatus(request));
        if(modelAndView != null){
            return modelAndView;
        }else {
            return computableModelService.getPage(id);
        }
    }

    /**
     * @Description 获取计算模型信息
     * @param id
     * @Return njgis.opengms.portal.entity.doo.JsonResult
     * @Author kx
     * @Date 21/10/14
     **/
    @ApiOperation(value = "根据id获取计算模型信息(修改item时调用的接口) [ /getInfo/{id} ]")
    @RequestMapping (value="/itemInfo/{id}",method = RequestMethod.GET)
    JsonResult getInfo(@PathVariable ("id") String id){
        return ResultUtils.success(computableModelService.getInfo(id));
    }

    /**
     * @Description 获取计算模型信息
     * @param
     * @Return njgis.opengms.portal.entity.doo.JsonResult
     * @Author kx
     * @Date 21/10/14
     **/
    @LoginRequired
    @ApiOperation(value = "创建computableModel [ /add ]")
    @ApiImplicitParams({
            @ApiImplicitParam(name="info",value="创建item的json文件",required=true,paramType="form",dataType="__file")
    })
    @PostMapping (value="",headers="content-type=multipart/form-data", consumes = "multipart/form-data")
    JsonResult add(HttpServletRequest request) throws IOException {

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        List<MultipartFile> files=multipartRequest.getFiles("resources");
        MultipartFile file=multipartRequest.getFile("computableModel");
        String model=IOUtils.toString(file.getInputStream(),"utf-8");
        JSONObject jsonObject=JSONObject.parseObject(model);

        HttpSession session=request.getSession();
        String email=session.getAttribute("email").toString();

        JSONObject result=computableModelService.insert(files,jsonObject,email);
        if(result.getInteger("code")==1){
            userService.ItemCountPlusOne(email, ItemTypeEnum.ComputableModel);
        }

        return ResultUtils.success(result);
    }

    @LoginRequired
    @ApiOperation(value = "更新computableModel [ /update ]")
    @ApiImplicitParams({
            @ApiImplicitParam(name="info",value="更新item的json文件",required=true,paramType="form",dataType="__file")
    })
    @PutMapping(value = "/",headers="content-type=multipart/form-data", consumes = "multipart/form-data")
    public JsonResult update(MultipartFile info, HttpServletRequest request) throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        List<MultipartFile> files=multipartRequest.getFiles("resources");
        MultipartFile file=multipartRequest.getFile("computableModel");
        String model=IOUtils.toString(file.getInputStream(),"utf-8");
        JSONObject jsonObject=JSONObject.parseObject(model);

        HttpSession session=request.getSession();
        String email=session.getAttribute("email").toString();

        JSONObject result=computableModelService.update(files,jsonObject,email);

        if(result==null){
            return ResultUtils.error(-1,"There is another version have not been checked, please contact opengms@njnu.edu.cn if you want to modify this item.");
        }
        else {
            return ResultUtils.success(result);
        }
    }

    @LoginRequired
    @ApiOperation(value = "更新knowledge")
    @PostMapping(value = "/knowledge")
    public JsonResult updateKnowledge(@RequestBody KnowledgeDTO knowledgeDTO, HttpServletRequest request) throws IOException {

        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();

        JSONObject result = repositoryService.updateKnowledge(ItemTypeEnum.ComputableModel ,knowledgeDTO,email);

        if(result==null){
            return ResultUtils.error(-1,"There is another version have not been checked, please contact opengms@njnu.edu.cn if you want to modify this item.");
        }
        else {
            return ResultUtils.success(result);
        }
    }

    @LoginRequired
    @ApiOperation(value = "删除computableModel [ /delete ]")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public JsonResult delete(@PathVariable(value="id") String id,
                             HttpServletRequest request){
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        return computableModelService.delete(id,email);
    }

    @ApiOperation(value = "条目查询 ")
    @RequestMapping (value="/list",method = RequestMethod.POST)
    public JsonResult queryList(@RequestBody SpecificFindDTO queryDTO) {
        return ResultUtils.success(genericService.searchItems(queryDTO, ItemTypeEnum.ComputableModel));
    }

    // @LoginRequired
    // @ApiOperation(value = "查询由登录用户创建的所有条目")
    // @RequestMapping (value="/listByAuthor",method = RequestMethod.POST)
    // public JsonResult queryListByAuthor(@RequestBody FindDTO queryDTO, HttpServletRequest request) {
    //     String email = Utils.checkLoginStatus(request);
    //     SpecificFindDTO specificFindDTO = new SpecificFindDTO();
    //     specificFindDTO.setPage(queryDTO.getPage());
    //     specificFindDTO.setPageSize(queryDTO.getPageSize());
    //     specificFindDTO.setAsc(queryDTO.getAsc());
    //     specificFindDTO.setSearchText(queryDTO.getSearchText());
    //     specificFindDTO.setSortField(queryDTO.getSortField());
    //     specificFindDTO.setCurQueryField("author");
    //     return ResultUtils.success(genericService.searchItems(specificFindDTO, ItemTypeEnum.ComputableModel));
    // }

    @ApiOperation(value = "查找部署的模型 [ /searchDeployedModel ]")
    @RequestMapping(value="/deployedModel",method= RequestMethod.POST)
    public JsonResult searchDeployedModel(@RequestBody FindDTO findDTO) {


        JSONObject jsonObject = computableModelService.searchDeployedModel(findDTO);
        List<ComputableModel> ComputableModelList = (List<ComputableModel>) jsonObject.get("content");

        //加上作者的authorId(accessId)
        List<JSONObject> computableModelListJson = new ArrayList<>();
        for (ComputableModel model : ComputableModelList) {
            JSONObject o = (JSONObject)JSONObject.toJSON(model);
            User user = userService.getByEmail(model.getAuthor());
            if (user != null){
                o.put("authorId", user.getAccessId());
                o.put("author", user.getName());
            }
            computableModelListJson.add(o);
        }

        jsonObject.put("content", computableModelListJson);

        return ResultUtils.success(jsonObject);
    }


    @RequestMapping(value="/selecttask",method = RequestMethod.GET)
    public ModelAndView initTaskPage(HttpServletRequest req) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("task");

        return modelAndView;

    }

    /**
     * @Description 某用户查询他人的模型条目
     * @param findDTO
     * @Return njgis.opengms.portal.entity.doo.JsonResult
     **/
    @ApiOperation(value = "某用户查询他人的模型条目", notes = "主要用于个人主页")
    @RequestMapping(value = "/queryListOfAuthor", method = RequestMethod.POST)
    public JsonResult queryListOfAuthor(@RequestBody UserFindDTO findDTO) {

        return ResultUtils.success(genericService.queryByUser(ItemTypeEnum.ComputableModel,findDTO, false));

    }

    /**
     * @Description 某用户查询自己的条目
     * @param findDTO
     * @Return njgis.opengms.portal.entity.doo.JsonResult
     **/
    @LoginRequired
    @ApiOperation(value = "某用户查询自己的条目", notes = "@LoginRequired\n主要用于个人空间")
    @RequestMapping(value = {"/queryListOfAuthorSelf","/listByAuthor"}, method = RequestMethod.POST)
    public JsonResult queryListOfAuthorSelf(@RequestBody UserFindDTO findDTO, HttpServletRequest request) {

        if (findDTO.getAuthorEmail() == null || "".equals(findDTO.getAuthorEmail())){
            HttpSession session=request.getSession();
            String email=session.getAttribute("email").toString();
            findDTO.setAuthorEmail(email);
        }

        return ResultUtils.success(genericService.queryByUser(ItemTypeEnum.ComputableModel,findDTO, true));

    }

    @RequestMapping(value = "/relatedDataByPage",method = RequestMethod.GET)
    public JsonResult getRelatedDataByPage(FindDTO computableModelFindDTO, @RequestParam(value = "id") String id){
        return ResultUtils.success(computableModelService.getRelatedDataByPage(computableModelFindDTO,id));
    }


    @ApiOperation(value = "下载资源")
    @RequestMapping(value = "/resource/download/{id}/{index}", method = RequestMethod.GET)
    public void downloadResource(
        @ApiParam(name = "id", value = "条目id") @PathVariable String id,
        @ApiParam(name = "index", value = "resources数组index")  @PathVariable int index,
        HttpServletResponse response) {

        computableModelService.downloadResource(id,index,response);

    }

    @LoginRequired
    @RequestMapping(value = "/deployToGivenServer",method = RequestMethod.POST)
    JsonResult deploy(@RequestParam("id")String id,@RequestParam("ip")String ip,@RequestParam("port")String port,HttpServletRequest request) throws IOException {

        if (id.contains("?"))
            id = (id.split("\\?"))[0];

        String result=computableModelService.deployToGivenServer(id,ip,port);
        if(result!=null){
            return ResultUtils.success(result);
        }
        else {
            return ResultUtils.success("failed");
        }
    }


    //todo
    //模型集成部分
    @RequestMapping (value="/integratingList",method = RequestMethod.GET)
    Page<ComputableModel> integratingList(int page, String sortType, int sortAsc){
        return computableModelService.integratingList(page,sortType,sortAsc);
    }

    @RequestMapping(value = "/integrating",method = RequestMethod.GET)
    ModelAndView integrating(HttpServletRequest request){
        Page<ComputableModel> computableModelList = computableModelService.integratingList(0,"default",1);
        ModelAndView modelAndView = computableModelService.integrate(computableModelList);

        return modelAndView;
    }

    @RequestMapping(value = "/integratedModel",method = RequestMethod.GET)
    ModelAndView integratedModel(HttpServletRequest request){
        Page<ComputableModel> computableModelList = computableModelService.integratingList(0,"default",1);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("integratedModeling_new");
        modelAndView.addObject("computableModelList", computableModelList);

        return modelAndView;
    }

    @RequestMapping(value = "/integratedTest",method = RequestMethod.GET)
    ModelAndView integratedTest(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("integratedTest");

        return modelAndView;
    }

    @RequestMapping(value = "/getIntegratedTask/{taskId}",method = RequestMethod.GET)
    ModelAndView getIntegratedTask(@PathVariable("taskId") String taskId){

        Task task = taskService.findByTaskId(taskId);
        String xml = task.getGraphXml();
        List<ModelParam> modelParams = task.getModelParams();
        List<Model> models = task.getModels();

        Page<ComputableModel> computableModelList = computableModelService.integratingList(0,"default",1);
        return computableModelService.getIntegratedTask(computableModelList,xml,modelParams,models);

    }

//    @RequestMapping(value = "/integratedModel",method = RequestMethod.GET)
//    ModelAndView integratedModel(HttpServletRequest request){
//
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("/error/500");
//
//        return modelAndView;
//    }

    @RequestMapping(value="/pageByClassi",method = RequestMethod.GET)
    public JsonResult pageByClassi(@RequestParam(value="asc") int asc,
                                   @RequestParam(value = "page") int page,
                                   @RequestParam(value = "size") int size,
                                   @RequestParam(value = "sortEle") String sortEle,
                                   @RequestParam(value = "searchText") String searchText,
                                   @RequestParam(value = "classification") String classification
    )
    {
        return ResultUtils.success(computableModelService.pageByClassi(asc,page,size,sortEle,searchText,classification));

    }


    @RequestMapping(value="/getDeployedModelByOid",method = RequestMethod.GET)
    public JsonResult getDeployedModelByOid(@RequestParam(value="oid") String oid    )
    {
        return ResultUtils.success(computableModelService.getDeployedModelById(oid));

    }




    //模型集成结束






    //检查md5是否有对应部署的模型
    @LoginRequired
    @RequestMapping (value = "/checkDeployed", method = RequestMethod.GET)
    public JsonResult checkDeployed(@RequestParam(value="md5") String md5,HttpServletRequest request){
        Boolean deployed = computableModelService.checkDeployed(md5);
        return ResultUtils.success(deployed);
    }


    /** 下面的接口是UserServer用到的接口 */

    @RequestMapping(value="/searchDeployedModelByUser",method=RequestMethod.GET)
    public JsonResult searchDeployedModelByEmail(@RequestParam(value="userName") String userName,@RequestParam(value="asc") int asc,
                                                 @RequestParam(value = "page") int page,
                                                 @RequestParam(value = "size") int size,
                                                 @RequestParam(value = "searchText") String searchText
    ) {
        return ResultUtils.success(computableModelService.searchDeployedModelByUser(userName,asc,page,size,searchText));
    }

    @RequestMapping(value="/searchDeployedModel",method=RequestMethod.GET)
    public JsonResult searchDeployedModel(@RequestParam(value="asc") int asc,
                                          @RequestParam(value = "page") int page,
                                          @RequestParam(value = "size") int size,
                                          @RequestParam(value = "searchText") String searchText
    ) {
        return ResultUtils.success(computableModelService.searchDeployedModel(asc,page,size,searchText));
    }



    @RequestMapping (value="/getInfo/{id}",method = RequestMethod.GET)
    JsonResult getInfoForUserserver(@PathVariable ("id") String id){
        return computableModelService.getInfoForUserserver(id);
    }

    @LoginRequired
    @ApiOperation(value = "得到当前计算模型运行成功的任务信息")
    @RequestMapping(value="/{computableModelId}/taskInfo",method = RequestMethod.POST )
    public JsonResult getTasksByUserByStatus(@PathVariable("computableModelId") String computableModelId ,@RequestBody TaskFindDTO taskFindDTO, HttpServletRequest request) {

        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return taskService.getSuccessfulTasksByUserByComputableModelId(email,computableModelId,taskFindDTO);

    }

}
