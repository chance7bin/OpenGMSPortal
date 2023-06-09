package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import njgis.opengms.portal.component.annotation.AdminRequired;
import njgis.opengms.portal.dao.ComputableModelDao;
import njgis.opengms.portal.dao.UserDao;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.task.CheckedHistory;
import njgis.opengms.portal.entity.doo.task.CheckedModel;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.entity.dto.SpecificFindDTO;
import njgis.opengms.portal.entity.po.ComputableModel;
import njgis.opengms.portal.entity.po.User;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.enums.ResultEnum;
import njgis.opengms.portal.enums.UserRoleEnum;
import njgis.opengms.portal.service.*;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/11/08
 */
@RestController
@RequestMapping(value = "/managementSystem")
public class ManagementSystemController {

    @Autowired
    ComputableModelService computableModelService;

    @Autowired
    ManagementSystemService managementSystemService;

    @Autowired
    UserService userService;


    @Autowired
    TaskService taskService;

    @Autowired
    ServerService serverService;


    @Autowired
    ComputableModelDao computableModelDao;

    @Autowired
    UserDao userDao;

    @AdminRequired
    @GetMapping("/home")
    public ModelAndView getHomePage(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("managementSystem/manage_home_zh");
        return modelAndView;
    }

    @AdminRequired
    @GetMapping("/home_en")
    public ModelAndView getHomeEnPage(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("managementSystem/manage_home_en");
        return modelAndView;
    }


    @AdminRequired
    @GetMapping("/system")
    public ModelAndView getSystemPage(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("managementSystem/manage_system");
        return modelAndView;
    }

    @GetMapping("/routeTest")
    public ModelAndView getRouteTestPage(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("managementSystem/routeTest/p1");
        return modelAndView;
    }

    @RequestMapping(value = "test", method = RequestMethod.GET)
    public JsonResult test(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name","wyj");
        jsonObject.put("age", "18");
        return ResultUtils.success(jsonObject);
    }


    @ApiOperation(value = "得到已部署的模型 searchText:name")
    @RequestMapping(value="/deployedModel",method= RequestMethod.POST)
    public JsonResult searchDeployedModel(@RequestBody FindDTO findDTO) {
        return managementSystemService.searchDeployedModel(findDTO);
    }

    // @LoginRequired
    @AdminRequired
    @ApiOperation(value = "模型调用")
    @RequestMapping(value="/model/invoke/{modelId}",method= RequestMethod.GET)
    public JsonResult invokeModel(@PathVariable String modelId, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        // String email = "782807969@qq.com";


        JsonResult invokeResult = managementSystemService.invokeModel(modelId, email);


        //单次调用模型也要加到记录中，这之前没考虑到，为了不修改之前的代码就在这里加了
        CheckedHistory checkedHistory = new CheckedHistory();
        List<CheckedHistory> checkedHistoryList = new ArrayList<>();
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
            checkedHistory.setMsrAddress(managementSystemService.getModelContainerByTaskId(taskId));
            checkedHistory.setMsrid(managementSystemService.getMsridByTaskId(taskId));
        } else {
            checkedHistory.setStatus(-1);
            checkedHistory.setMsg(invokeResult.getMsg());
        }

        checkedHistoryList.add(checkedHistory);
        managementSystemService.saveCheckedList(checkedHistoryList, email);


        return invokeResult;

    }

    // @LoginRequired
    @AdminRequired
    @ApiOperation(value = "批量模型调用")
    @RequestMapping(value="/model/invoke/batch",method= RequestMethod.POST)
    public JsonResult invokeModelBatch(@RequestBody List<String> modelIdList, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        // String email = "782807969@qq.com";

        return managementSystemService.invokeModelBatch(modelIdList, email);
    }

    // @ApiOperation(value = "模型批量调用")
    // @RequestMapping(value="/model/invoke/batch",method= RequestMethod.POST)
    // public JsonResult batchInvokeModel(@RequestBody FindDTO findDTO) {
    //     return ResultUtils.success(computableModelService.searchDeployedModel(findDTO));
    // }


    // @LoginRequired
    // @ApiOperation(value = "更新任务状态 [不用这个了]")
    // @RequestMapping(value="/update/modelStatus",method= RequestMethod.GET)
    // public JsonResult updateTaskStatus() {
    //     // HttpSession session = request.getSession();
    //     // String email = session.getAttribute("email").toString();
    //     String email = "782807969@qq.com";
    //     return managementSystemService.updateTaskStatus();
    // }


    // @LoginRequired
    // @ApiOperation(value = "保存检查的模型列表")
    // @RequestMapping(value="/checkList/save",method= RequestMethod.POST)
    // public JsonResult saveCheckedList(@RequestBody TaskCheckListDTO taskCheckListDTO){
    //
    //     // HttpSession session = request.getSession();
    //     // String email = session.getAttribute("email").toString();
    //     String email = "782807969@qq.com";
    //     return managementSystemService.saveCheckedList(taskCheckListDTO,email);
    //
    // }

    // @LoginRequired
    @AdminRequired
    @ApiOperation(value = "查找检查的模型列表")
    @RequestMapping(value="/checkList/search",method= RequestMethod.POST)
    public JsonResult searchCheckedList(@RequestBody FindDTO findDTO){

        // HttpSession session = request.getSession();
        // String email = session.getAttribute("email").toString();
        // String email = "782807969@qq.com";
        return managementSystemService.searchCheckedList(findDTO);

    }

    // @LoginRequired
    @AdminRequired
    @ApiOperation(value = "删除检查的模型列表")
    @RequestMapping(value="/checkList/delete/{id}",method= RequestMethod.DELETE)
    public JsonResult deleteCheckedList(@PathVariable String id){

        // HttpSession session = request.getSession();
        // String email = session.getAttribute("email").toString();
        // String email = "782807969@qq.com";
        return managementSystemService.deleteCheckedList(id);

    }


    @ApiOperation(value = "用户列表 searchText:email")
    @RequestMapping(value="/user/info",method= RequestMethod.POST)
    public JsonResult getUserList(@RequestBody FindDTO findDTO){
        return managementSystemService.getUserList(findDTO);
    }



    // @LoginRequired
    @AdminRequired
    @ApiOperation(value = "设置用户权限")
    @RequestMapping(value="/user/role/{id}/{role}",method= RequestMethod.POST)
    public JsonResult setUserRole(@PathVariable String id, @PathVariable UserRoleEnum role){
        return userService.setUserRole(id,role);
    }



    // @ApiOperation(value = "大屏展示需要的数据")
    // @RequestMapping(value="/dashboard/info",method= RequestMethod.GET)
    // public JsonResult getDashboardInfo(){
    //     return managementSystemService.getDashboardInfo();
    // }



    @ApiOperation(value = "条目展示, 根据条目类型(type必填)  查询可以根据curQueryField自己指定查询的属性")
    @RequestMapping(value="/item/info/{itemType}",method= RequestMethod.POST)
    public JsonResult getItemInfo(@PathVariable ItemTypeEnum itemType, @RequestBody SpecificFindDTO specificFindDTO){
        return ResultUtils.success(managementSystemService.getItemInfo(itemType, specificFindDTO));
    }


    // @LoginRequired
    @AdminRequired
    @ApiOperation(value = "给条目添加管理者")
    @RequestMapping(value="/item/admin/{itemType}/{itemId}",method= RequestMethod.POST)
    public JsonResult addAdmin(
        @ApiParam(name = "itemType", value = "条目类型", required = true)
        @PathVariable ItemTypeEnum itemType,
        @ApiParam(name = "itemId", value = "条目id", required = true)
        @PathVariable String itemId,
        @ApiParam(name = "userList", value = "新增的用户email", required = true)
        @RequestBody List<String> userList
    ){
        return managementSystemService.addAdmin(itemType, itemId, userList);
    }

    // @LoginRequired
    @AdminRequired
    @ApiOperation(value = "设置条目的访问状态 status: Public/Discoverable/Private")
    @RequestMapping(value="/item/status/{itemType}/{itemId}/{status}",method= RequestMethod.POST)
    public JsonResult setItemStatus(@PathVariable ItemTypeEnum itemType, @PathVariable String itemId, @PathVariable String status, HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        // String email = "782807969@qq.com";
        return managementSystemService.setItemStatus(itemType, itemId, status,email);

    }

    @ApiOperation(value = "按条目类型查找资源数量")
    @RequestMapping(value="/item/count/{itemType}",method= RequestMethod.GET)
    public JsonResult getItemCount(@PathVariable ItemTypeEnum itemType){
        return ResultUtils.success(managementSystemService.getItemCount(itemType));

    }

    @ApiOperation(value = "查找所有资源的数量")
    @RequestMapping(value="/item/count/all",method= RequestMethod.GET)
    public JsonResult getAllItemCount(){
        return managementSystemService.getAllItemCount();

    }


    @ApiOperation(value = "页面访问量")
    @RequestMapping(value="/view/page/count",method= RequestMethod.GET)
    public JsonResult getPageViewCount(){
        return ResultUtils.success(managementSystemService.getPageViewCount());

    }


    @ApiOperation(value = "用户访问量")
    @RequestMapping(value="/view/user/count",method= RequestMethod.GET)
    public JsonResult getUserViewCount(){
        return ResultUtils.success(managementSystemService.getUserViewCount());

    }


    @ApiOperation(value = "条目访问量 按viewCount排序")
    @RequestMapping(value="/view/item/sortByViewCount/{itemType}",method= RequestMethod.POST)
    public JsonResult getItemViewCount(@PathVariable ItemTypeEnum itemType, @RequestBody SpecificFindDTO specificFindDTO){
        specificFindDTO.setSortField("viewCount");
        return ResultUtils.success(managementSystemService.getItemInfo(itemType, specificFindDTO));
    }


    @ApiOperation(value = "计算模型服务调用的数量 按invokeCount排序")
    @RequestMapping(value="/service/computableModel/invokeCount",method= RequestMethod.POST)
    public JsonResult getModelServiceInvokeCount(@RequestBody SpecificFindDTO specificFindDTO){
        specificFindDTO.setSortField("invokeCount");
        return ResultUtils.success(managementSystemService.getItemInfo(ItemTypeEnum.ComputableModel, specificFindDTO));
    }

    @ApiOperation(value = "数据方法服务调用的数量 按invokeCount排序")
    @RequestMapping(value="/service/dataMethod/invokeCount",method= RequestMethod.POST)
    public JsonResult getDataMethodServiceInvokeCount(@RequestBody SpecificFindDTO specificFindDTO){
        specificFindDTO.setSortField("invokeCount");
        return ResultUtils.success(managementSystemService.getItemInfo(ItemTypeEnum.DataMethod, specificFindDTO));
    }


    // @LoginRequired
    @ApiOperation(value = "获取用户数量")
    @RequestMapping(value="/user/count",method= RequestMethod.GET)
    public JsonResult getUserCount(){
        return ResultUtils.success(managementSystemService.getUserCount());
    }

    @ApiOperation(value = "获取服务节点")
    @RequestMapping(value="/serverNodes",method= RequestMethod.GET)
    public JsonResult getAllServerNodes(){
        return ResultUtils.success(serverService.getAllServerNodes());
    }



    @ApiOperation(value = "得到运行的任务列表")
    @RequestMapping(value="/taskList",method= RequestMethod.POST)
    public JsonResult getTaskList(@RequestBody FindDTO findDTO){
        return ResultUtils.success(managementSystemService.getTaskList(findDTO));
    }

    @ApiOperation(value = "得到模型容器列表")
    @RequestMapping(value="/mscList",method= RequestMethod.GET)
    public JsonResult getMscList(){
        return ResultUtils.success(managementSystemService.getMscList());
    }


    @ApiOperation(value = "得到评论列表")
    @RequestMapping(value="/commentList",method= RequestMethod.POST)
    public JsonResult getCommentList(@RequestBody FindDTO findDTO){
        return ResultUtils.success(managementSystemService.getCommentList(findDTO));
    }


    @ApiOperation(value = "根据id删除评论")
    @RequestMapping(value = "/comment/delete/{id}", method = RequestMethod.GET)
    public JsonResult deleteComment(@PathVariable("id") String id){

        return managementSystemService.deleteComment(id);


    }

}
