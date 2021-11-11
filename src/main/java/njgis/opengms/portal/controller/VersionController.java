package njgis.opengms.portal.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.component.LoginRequired;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.service.VersionService;
import njgis.opengms.portal.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @Description
 * @Author bin
 * @Date 2021/09/06
 */
@RestController
@RequestMapping(value = "/version")
@Slf4j
public class VersionController {

    @Autowired
    VersionService versionService;


    @LoginRequired
    @ApiOperation(value = "接受版本")
    @RequestMapping(value = "/accept/{id}", method = RequestMethod.POST)
    public JsonResult accept(@PathVariable String id, HttpServletRequest request) {
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        return versionService.accept(id, email);
    }

    @LoginRequired
    @ApiOperation(value = "拒绝版本")
    @RequestMapping(value = "/reject/{id}", method = RequestMethod.POST)
    public JsonResult reject(@PathVariable String id, HttpServletRequest request) {
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        return versionService.reject(id, email);
    }


    @ApiOperation(value = "得到所有审核信息 [ /version/getVersions ]")
    @GetMapping(value = "/versionList")
    public JsonResult getVersions() {
        return versionService.getVersions(null);
    }

    // @ApiOperation(value = "分页得到所有审核信息")
    // @PostMapping(value = "/versionListByPage")
    // public JsonResult getVersionByPage(@RequestBody FindDTO findDTO) {
    //     return versionService.getVersions(findDTO);
    // }

    @ApiOperation(value = "得到接收的审核信息 [ /version/getAccepted ]")
    @PostMapping(value = "/versionList/accepted")
    public JsonResult getAccepted(@RequestBody FindDTO findDTO) {
        return versionService.getVersionByConcreteStatus(findDTO,1);
    }

    // @ApiOperation(value = "分页得到接收的审核信息")
    // @PostMapping(value = "/versionListByPage/accepted")
    // public JsonResult getAcceptedByPage(@RequestBody FindDTO findDTO) {
    //     return versionService.getConcreteStatus(findDTO,1);
    // }

    @ApiOperation(value = "得到未审核的审核信息")
    @PostMapping(value = "/versionList/uncheck")
    public JsonResult getUncheck(@RequestBody FindDTO findDTO) {
        return versionService.getVersionByConcreteStatus(findDTO,0);
    }

    // @ApiOperation(value = "分页得到未审核的审核信息")
    // @PostMapping(value = "/versionListByPage/unchecked")
    // public JsonResult getUncheckedByPage(@RequestBody FindDTO findDTO) {
    //     return versionService.getConcreteStatus(findDTO,0);
    // }

    @ApiOperation(value = "得到拒绝的审核信息")
    @PostMapping(value = "/versionList/rejected")
    public JsonResult getRejected(@RequestBody FindDTO findDTO) {
        return versionService.getVersionByConcreteStatus(findDTO,-1);
    }

    // @ApiOperation(value = "分页得到拒绝的审核信息")
    // @PostMapping(value = "/versionListByPage/reject")
    // public JsonResult getRejectByPage(@RequestBody FindDTO findDTO) {
    //     return versionService.getConcreteStatus(findDTO,-1);
    // }


    @LoginRequired
    @ApiOperation(value = "得到审核版本的详细信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name="id",value="版本id",required=true)
    })
    @GetMapping (value = "/detail/{id}")
    public JsonResult getVersionDetail(@PathVariable String id) {
        return versionService.getVersionDetail(id);
    }

    // TODO compare页面需要返回什么数据


    @LoginRequired
    @ApiOperation(value = "超级用户审核界面")
    @RequestMapping(value = "/review", method = RequestMethod.GET)
    public ModelAndView getRegister(HttpServletRequest request) {
        String email = Utils.checkLoginStatus(request);

        ModelAndView modelAndView = new ModelAndView();
        if(email==null){
            modelAndView.setViewName("error/404");
        }
        else if(email.equals("yss123yss@126.com")||email.equals("opengms@njnu.edu.cn")||email.equals("921485453@qq.com")){
            modelAndView.setViewName("version/versionCheck");
        }
        else {
            modelAndView.setViewName("error/404");
        }

        return modelAndView;
    }


}