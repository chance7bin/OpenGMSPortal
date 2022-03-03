package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiOperation;
import njgis.opengms.portal.component.LoginRequired;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.service.ServerService;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @Description
 * @Author bin
 * @Date 2021/12/27
 */
@RestController
@RequestMapping(value="/server")
public class ServerRestController {

    @Autowired
    ServerService serverService;



    @ApiOperation(value = "获取所有模型/数据容器")
    @RequestMapping(value="/all",method= RequestMethod.GET)
    public JsonResult getAll(){

        JSONArray allServerNodes = serverService.getAllServerNodes();
        if (allServerNodes == null){
            return ResultUtils.error();
        }

        return ResultUtils.success(allServerNodes);

    }



    @LoginRequired
    @ApiOperation(value = "获取用户所有模型容器 [ /server/modelContainer/getModelContainerByUserName ]")
    @RequestMapping(value="/modelContainerByUser",method=RequestMethod.GET)
    JsonResult getModelContainerByUserName(HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return ResultUtils.success(serverService.getModelContainerByUserName(email));
    }

}
