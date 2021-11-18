package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.service.TaskMonitorService;
import njgis.opengms.portal.utils.ResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @ClassName TaskMonitorController.java
 * @Author wzh
 * @Version 1.0.0
 * @Description
 * @CreateDate(Y/M/D-H:M:S) 2021/04/27/ 16:50:00
 */
@RestController
@RequestMapping(value = "/taskmonitor")
public class TaskMonitorController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    TaskMonitorService taskMonitorService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelAndView getTaskMonitor(HttpServletRequest request){

        HttpSession session = request.getSession();
        ModelAndView modelAndView = new ModelAndView();
//        if(session.getAttribute("uid")==null||!session.getAttribute("name").toString().equals("wzh")){
//            modelAndView.setViewName("login");
//        }
//        else{}

        modelAndView.setViewName("taskMonitor");
        return modelAndView;
    }

    @RequestMapping(value = "/tasks", method = RequestMethod.POST)
    public JsonResult getTasks(@RequestBody Map<String,String> searchInfo, HttpServletRequest request){

        HttpSession session = request.getSession();
//        if(session.getAttribute("uid")==null||!session.getAttribute("name").toString().equals("wzh")){
//            return null;
//        }

        JSONObject result = taskMonitorService.getTasks(searchInfo);
        if (result == null){
            logger.info("---err return---");
            return ResultUtils.error(-1,"err");
        }else{
            logger.info("---suc return---");
            return ResultUtils.success(result);
        }
    }

    @RequestMapping(value = "/containers", method = RequestMethod.POST)
    public JsonResult getContainers(@RequestBody Map<String,String> searchInfo, HttpServletRequest request){

        HttpSession session = request.getSession();
//        if(session.getAttribute("uid")==null||!session.getAttribute("name").toString().equals("wzh")){
//            return null;
//        }

        JSONArray result = taskMonitorService.getContainers(searchInfo);
        if (result == null){
            return ResultUtils.error(-1,"err");
        }else{
            return ResultUtils.success(result);
        }
    }
}
