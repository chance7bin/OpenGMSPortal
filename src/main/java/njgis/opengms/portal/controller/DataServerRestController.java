package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.component.annotation.LoginRequired;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.service.DataServerService;
import njgis.opengms.portal.utils.ResultUtils;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @Description
 * @Author bin
 * @Date 2022/03/03
 */
@Slf4j
@RestController
@RequestMapping(value = "/dataServer")
@CrossOrigin
public class DataServerRestController {

    @Value("${dataServerManager}")
    private String dataServerManager;

    @Autowired
    DataServerService dataServerService;


    @LoginRequired
    @ApiOperation(value = "得到用户节点信息 [ /dataServer/getUserNodes ]")
    @RequestMapping(value = "/userNodes", method = RequestMethod.GET)
    public JsonResult getUserNodes(HttpServletRequest request) throws DocumentException {

        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();

        JSONObject jsonObject = dataServerService.getUserNode(email);
        if(jsonObject.isEmpty()){
            return ResultUtils.success("offline");
        }else {
            return ResultUtils.success(jsonObject);
        }

    }

    @LoginRequired
    @RequestMapping(value = "/pageDataItemChecked", method = RequestMethod.GET)
    public JsonResult pageAllDataItemChecked(@RequestParam(value = "page") int page,
                                             @RequestParam(value = "pageSize") int pageSize,
                                             @RequestParam(value="asc") int asc,
                                             @RequestParam(value="sortEle") String sortEle,
                                             @RequestParam(value="searchText") String searchText,
                                             HttpServletRequest request
    ){
        HttpSession session = request.getSession();
        String userId = session.getAttribute("email").toString();
        return ResultUtils.success(dataServerService.pageDataItemChecked(page,pageSize,asc,sortEle,searchText,userId));

    }


    @RequestMapping(value = "/checkNodeContent", method = RequestMethod.GET)
    public JsonResult checkNodeContent(@RequestParam("serverId") String serverId,
                                       @RequestParam("token") String token,
                                       @RequestParam("type") String type
    ){

        return ResultUtils.success(dataServerService.checkNodeContent(serverId,token,type));

    }


}
