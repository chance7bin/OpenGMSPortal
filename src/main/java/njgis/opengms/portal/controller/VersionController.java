package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.component.LoginRequired;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.service.VersionService;
import njgis.opengms.portal.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.spring.web.json.Json;

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


//    @LoginRequired
    @ApiOperation(value = "接受版本")
    @RequestMapping(value = "/accept/{id}", method = RequestMethod.GET)
    public JsonResult accept(@PathVariable String id, HttpServletRequest request) {
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        return versionService.accept(id, email);
    }

//    @LoginRequired
    @ApiOperation(value = "拒绝版本")
    @RequestMapping(value = "/reject/{id}", method = RequestMethod.GET)
    public JsonResult reject(@PathVariable String id, HttpServletRequest request) {
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        return versionService.reject(id, email);
    }

    @ApiOperation(value = "回退版本")
    @RequestMapping(value = "/fallback/{id}", method = RequestMethod.GET)
    public JsonResult fallback(
        @ApiParam(name = "id", value = "版本id") @PathVariable String id, HttpServletRequest request) {
        HttpSession session=request.getSession();
        String email = session.getAttribute("email").toString();
        return versionService.fallback(id, email);
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
    @PostMapping(value = "/versionList/accepted/{type}")
    public JsonResult getAccepted(@RequestBody FindDTO findDTO,
                                  @ApiParam(name = "type", value = "type传入All为不分类")  @PathVariable ItemTypeEnum type) {
        return versionService.getVersionByConcreteStatus(findDTO,1,type);
    }

    // @ApiOperation(value = "分页得到接收的审核信息")
    // @PostMapping(value = "/versionListByPage/accepted")
    // public JsonResult getAcceptedByPage(@RequestBody FindDTO findDTO) {
    //     return versionService.getConcreteStatus(findDTO,1);
    // }

    @ApiOperation(value = "得到未审核的审核信息")
    @PostMapping(value = "/versionList/uncheck/{type}")
    public JsonResult getUncheck(@RequestBody FindDTO findDTO,
                                 @ApiParam(name = "type", value = "type传入All为不分类")  @PathVariable ItemTypeEnum type) {
        return versionService.getVersionByConcreteStatus(findDTO,0,type);
    }

    // @ApiOperation(value = "分页得到未审核的审核信息")
    // @PostMapping(value = "/versionListByPage/unchecked")
    // public JsonResult getUncheckedByPage(@RequestBody FindDTO findDTO) {
    //     return versionService.getConcreteStatus(findDTO,0);
    // }

    @ApiOperation(value = "得到拒绝的审核信息")
    @PostMapping(value = "/versionList/rejected/{type}")
    public JsonResult getRejected(@RequestBody FindDTO findDTO,
                                  @ApiParam(name = "type", value = "type传入All为不分类")  @PathVariable ItemTypeEnum type) {
        return versionService.getVersionByConcreteStatus(findDTO,-1,type);
    }

    // @ApiOperation(value = "分页得到拒绝的审核信息")
    // @PostMapping(value = "/versionListByPage/reject")
    // public JsonResult getRejectByPage(@RequestBody FindDTO findDTO) {
    //     return versionService.getConcreteStatus(findDTO,-1);
    // }


//    @LoginRequired
    @ApiOperation(value = "得到审核版本的详细信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name="id",value="版本id",required=true)
    })
    @GetMapping (value = "/detail/{id}")
    public JsonResult getVersionDetail(@PathVariable String id) {
        return versionService.getVersionDetail(id);
    }

    // TODO compare页面需要返回什么数据


//    @LoginRequired
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


    @ApiOperation(value = "根据版本版本id得到原始的条目信息")
    @RequestMapping(value = "/originalItemInfo/{id}", method = RequestMethod.GET)
    public JsonResult getOriginalItemInfo(@PathVariable String id){
        return versionService.getOriginalItemInfo(id);
    }




    @LoginRequired
    @ApiOperation(value = "得到用户提交的version（不建议，用下面的，没有分页很慢） [ /theme/getMessageData ]")
    @RequestMapping(value = "/user/versionList/edit",method = RequestMethod.GET)
    public JsonResult getUserEditVersion(HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return versionService.getUserEditVersion(email);
    }


    @LoginRequired
    @ApiOperation(value = "得到用户审核的version（不建议，用下面的，没有分页很慢） [ /theme/getMessageData ]")
    @RequestMapping(value = "/user/versionList/review",method = RequestMethod.GET)
    public JsonResult getUserReviewVersion(HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return versionService.getUserReviewVersion(email);
    }

    @LoginRequired
    @ApiOperation(value = "根据条目类型得到用户提交（你是条目的修改者）的未审核的version [ /theme/getMessageData ]")
    @ApiImplicitParams({
        @ApiImplicitParam(name="type",value="条目大类: Model/Data/Community/Theme/All",required=true)
    })
    @RequestMapping(value = "/user/versionList/edit/uncheck/{type}",method = RequestMethod.POST)
    public JsonResult getUserUncheckEditVersionByType(@PathVariable String type, @RequestBody FindDTO findDTO, HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return versionService.getUserVersionByStatusAndByTypeAndByOperation(0,type,findDTO,email,"edit");
    }

    @LoginRequired
    @ApiOperation(value = "根据条目类型得到用户提交的已通过的version [ /theme/getMessageData ]")
    @ApiImplicitParams({
        @ApiImplicitParam(name="type",value="条目大类: Model/Data/Community/Theme/All",required=true)
    })
    @RequestMapping(value = "/user/versionList/edit/accepted/{type}",method = RequestMethod.POST)
    public JsonResult getUserAcceptedEditVersionByType(@PathVariable String type, @RequestBody FindDTO findDTO, HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return versionService.getUserVersionByStatusAndByTypeAndByOperation(1,type,findDTO,email,"edit");
    }

    @LoginRequired
    @ApiOperation(value = "根据条目类型得到用户提交的已拒绝的version [ /theme/getMessageData ]")
    @ApiImplicitParams({
        @ApiImplicitParam(name="type",value="条目大类: Model/Data/Community/Theme/All",required=true)
    })
    @RequestMapping(value = "/user/versionList/edit/rejected/{type}",method = RequestMethod.POST)
    public JsonResult getUserRejectedEditVersionByType(@PathVariable String type, @RequestBody FindDTO findDTO, HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return versionService.getUserVersionByStatusAndByTypeAndByOperation(-1,type,findDTO,email,"edit");
    }

    @LoginRequired
    @ApiOperation(value = "根据条目类型得到用户审核（你是条目的审核者）的未审核的version [ /theme/getMessageData ]")
    @ApiImplicitParams({
        @ApiImplicitParam(name="type",value="条目大类: Model/Data/Community/Theme/All",required=true)
    })
    @RequestMapping(value = "/user/versionList/review/uncheck/{type}",method = RequestMethod.POST)
    public JsonResult getUserUncheckReviewVersionByType(@PathVariable String type, @RequestBody FindDTO findDTO, HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return versionService.getUserVersionByStatusAndByTypeAndByOperation(0,type,findDTO,email,"review");
    }

    @LoginRequired
    @ApiOperation(value = "根据条目类型得到用户审核的已通过的version [ /theme/getMessageData ]")
    @ApiImplicitParams({
        @ApiImplicitParam(name="type",value="条目大类: Model/Data/Community/Theme/All",required=true)
    })
    @RequestMapping(value = "/user/versionList/review/accepted/{type}",method = RequestMethod.POST)
    public JsonResult getUserAcceptedReviewVersionByType(@PathVariable String type, @RequestBody FindDTO findDTO, HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return versionService.getUserVersionByStatusAndByTypeAndByOperation(1,type,findDTO,email,"review");
    }

    @LoginRequired
    @ApiOperation(value = "根据条目类型得到用户审核的已拒绝的version [ /theme/getMessageData ]")
    @ApiImplicitParams({
        @ApiImplicitParam(name="type",value="条目大类: Model/Data/Community/Theme/All",required=true)
    })
    @RequestMapping(value = "/user/versionList/review/rejected/{type}",method = RequestMethod.POST)
    public JsonResult getUserRejectedReviewVersionByType(@PathVariable String type, @RequestBody FindDTO findDTO, HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return versionService.getUserVersionByStatusAndByTypeAndByOperation(-1,type,findDTO,email,"review");
    }


    @ApiOperation(value = "查看版本对比")
    @RequestMapping(value = "/versionCompare/{id}", method = RequestMethod.GET)
    public ModelAndView getComparePage(@PathVariable String id ,HttpServletRequest request) {

        String email = Utils.checkLoginStatus(request);

        ModelAndView modelAndView = new ModelAndView();
        if(email==null){
            modelAndView.setViewName("login");
        }
        else if(email.equals("yss123yss@126.com")||email.equals("opengms@njnu.edu.cn")||email.equals("921485453@qq.com")){
            modelAndView = versionService.getPage(id);
        }
        else {
            modelAndView.setViewName("login");
        }

        return modelAndView;

    }

}
