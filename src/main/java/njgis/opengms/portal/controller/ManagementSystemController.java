package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.entity.dto.task.TaskCheckListDTO;
import njgis.opengms.portal.entity.dto.task.TaskFindDTO;
import njgis.opengms.portal.service.ComputableModelService;
import njgis.opengms.portal.service.ManagementSystemService;
import njgis.opengms.portal.service.TaskService;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Description
 * @Author bin
 * @Date 2021/11/08
 */
@RestController
@RequestMapping(value = "/management")
public class ManagementSystemController {

    @Autowired
    ComputableModelService computableModelService;

    @Autowired
    ManagementSystemService managementSystemService;


    @Autowired
    TaskService taskService;

    @GetMapping("/home")
    public ModelAndView getHomePage(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("managementSystem/manage_home");
        return modelAndView;
    }

    @GetMapping("/system")
    public ModelAndView getSystemPage(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("managementSystem/manage_system");
        return modelAndView;
    }

    @RequestMapping(value = "test", method = RequestMethod.GET)
    public JsonResult test(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name","wyj");
        jsonObject.put("age", "18");
        return ResultUtils.success(jsonObject);
    }


    @ApiOperation(value = "得到已部署的模型")
    @RequestMapping(value="/deployedModel",method= RequestMethod.POST)
    public JsonResult searchDeployedModel(@RequestBody FindDTO findDTO) {
        return ResultUtils.success(computableModelService.searchDeployedModel(findDTO));
    }

    // @LoginRequired
    @ApiOperation(value = "模型调用")
    @RequestMapping(value="/model/invoke/{modelId}",method= RequestMethod.GET)
    public JsonResult invokeModel(@PathVariable String modelId) {
        // HttpSession session = request.getSession();
        // String email = session.getAttribute("email").toString();
        String email = "782807969@qq.com";
        return managementSystemService.invokeModel(modelId, email);
    }

    // @ApiOperation(value = "模型批量调用")
    // @RequestMapping(value="/model/invoke/batch",method= RequestMethod.POST)
    // public JsonResult batchInvokeModel(@RequestBody FindDTO findDTO) {
    //     return ResultUtils.success(computableModelService.searchDeployedModel(findDTO));
    // }


    // @LoginRequired
    @ApiOperation(value = "更新任务状态")
    @RequestMapping(value="/update/modelStatus",method= RequestMethod.POST)
    public JsonResult updateTaskStatus(@RequestBody TaskFindDTO taskFindDTO) {
        // HttpSession session = request.getSession();
        // String email = session.getAttribute("email").toString();
        String email = "782807969@qq.com";
        return taskService.getTasksByUserByStatus(email, taskFindDTO);
    }


    // @LoginRequired
    @ApiOperation(value = "保存检查的模型列表")
    @RequestMapping(value="/checkList/save",method= RequestMethod.POST)
    public JsonResult saveCheckedList(@RequestBody TaskCheckListDTO taskCheckListDTO){

        // HttpSession session = request.getSession();
        // String email = session.getAttribute("email").toString();
        String email = "782807969@qq.com";
        return managementSystemService.saveCheckedList(taskCheckListDTO,email);

    }

    // @LoginRequired
    @ApiOperation(value = "查找检查的模型列表")
    @RequestMapping(value="/checkList/search",method= RequestMethod.POST)
    public JsonResult searchCheckedList(@RequestBody FindDTO findDTO){

        // HttpSession session = request.getSession();
        // String email = session.getAttribute("email").toString();
        String email = "782807969@qq.com";
        return managementSystemService.searchCheckedList(findDTO);

    }

    // @LoginRequired
    @ApiOperation(value = "删除检查的模型列表")
    @RequestMapping(value="/checkList/delete/{id}",method= RequestMethod.DELETE)
    public JsonResult deleteCheckedList(@PathVariable String id){

        // HttpSession session = request.getSession();
        // String email = session.getAttribute("email").toString();
        String email = "782807969@qq.com";
        return managementSystemService.deleteCheckedList(id);

    }

}
