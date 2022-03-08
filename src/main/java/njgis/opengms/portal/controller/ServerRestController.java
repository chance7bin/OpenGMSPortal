package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import njgis.opengms.portal.component.LoginRequired;
import njgis.opengms.portal.dao.ModelContainerDao;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.po.ModelContainer;
import njgis.opengms.portal.service.ServerService;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

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

    @Autowired
    ModelContainerDao modelContainerDao;

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


    //删除
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/modelContainer/remove", method = RequestMethod.POST)
    JsonResult remove(@RequestParam("account") String userName,
                      @RequestParam("mac") String mac) {

        ModelContainer modelContainer = modelContainerDao.findFirstByAccountAndMac(userName, mac);
        if (modelContainer == null) {
            return ResultUtils.error(-1, "No model container matches this userName and mac!");
        } else {
            modelContainerDao.delete(modelContainer);
            return ResultUtils.success("Delete suc!");
        }

    }

    //获取服务列表,分页
    @ApiOperation(value = "获取服务列表,分页")
    @RequestMapping(value = "/modelContainer/getServiceListByPage", method = RequestMethod.POST)
    JsonResult getServiceListByPage(@RequestParam("mac") String mac,
                                    @RequestParam("page") int page,
                                    @RequestParam("pageSize") int pageSize
    ) {

        ModelContainer modelContainer = modelContainerDao.findByMac(mac);
        JSONArray serviceList = modelContainer.getServiceList();
        int start = (page-1)*pageSize;
        int end = (start+pageSize-1)<serviceList.size()?(start+pageSize-1):serviceList.size()-1;
        JSONObject result = new JSONObject();
        List<Object> resultList = serviceList.subList(start,end+1);
        result.put("list",resultList);
        result.put("total",serviceList.size());

        return ResultUtils.success(result);

    }

    //设置别名
    @ApiOperation(value = "设置别名")
    @RequestMapping(value = "/modelContainer/setAlias", method = RequestMethod.POST)
    JsonResult setAlias(@RequestParam("alias") String alias,
                        @RequestParam("mac") String mac) {

        ModelContainer modelContainer = modelContainerDao.findByMac(mac);
        modelContainer.setAlias(alias);
        modelContainerDao.save(modelContainer);
        return ResultUtils.success(alias);

    }

}
