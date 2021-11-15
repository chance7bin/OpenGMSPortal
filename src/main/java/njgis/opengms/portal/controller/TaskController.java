package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import njgis.opengms.portal.component.LoginRequired;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.dto.task.TaskCheckDTO;
import njgis.opengms.portal.entity.dto.task.TaskFindDTO;
import njgis.opengms.portal.service.TaskService;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description
 * @Author bin
 * @Date 2021/11/08
 */
@RestController
@RequestMapping(value = "/task")
public class TaskController {

    @Autowired
    TaskService taskService;

    @LoginRequired
    @ApiOperation(value = "Task初始化API，获取模型描述信息，State信息，task以及Dx相关信息 [ /TaskInit/{id} ]")
    @RequestMapping(value = "/init/{id}", method = RequestMethod.GET)
    public JsonResult initTask(@PathVariable("id") String id, HttpServletRequest request){
        // HttpSession session = request.getSession();
        // String email = session.getAttribute("email").toString();
        String email = "782807969@qq.com";
        return taskService.initTask(id, email);
    }


    @LoginRequired
    @ApiOperation(value = "加载默认测试数据，返回数据成功上传之后的url")
    @RequestMapping(value = "/loadTestData/{modelId}", method = RequestMethod.POST)
    public JsonResult loadTestData(@PathVariable String modelId, HttpServletRequest request){

        // HttpSession session = request.getSession();
        // String email = session.getAttribute("email").toString();

        String email = "782807969@qq.com";
        return taskService.loadTestData(modelId, email);

    }

    @LoginRequired
    @ApiOperation(value = "调用模型 lists中的oid就是computableModel的id")
    @RequestMapping(value = "/invoke", method = RequestMethod.POST)
    public JsonResult invoke(@RequestBody JSONObject lists, HttpServletRequest request) {

        // HttpSession session = request.getSession();
        // String email = session.getAttribute("email").toString();
        String email = "782807969@qq.com";
        return taskService.handleInvoke(lists, email);

    }

    @ApiOperation(value = "得到task的结果")
    @RequestMapping(value = "/getResult", method = RequestMethod.POST)
    JsonResult getResult(@RequestBody TaskCheckDTO data) {

        JSONObject out=taskService.getTaskResult(data);
        return ResultUtils.success(out);

    }


    @LoginRequired
    @ApiOperation(value = "得到任务信息 [ /getTasksByUserIdByStatus ]")
    @RequestMapping(value="/taskInfo",method = RequestMethod.POST )
    public JsonResult getTasksByUserByStatus(@RequestBody TaskFindDTO taskFindDTO, HttpServletRequest request) {

        // HttpSession session = request.getSession();
        // String email = session.getAttribute("email").toString();
        String email = "782807969@qq.com";
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
    



}
