package njgis.opengms.portal.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import njgis.opengms.portal.component.LoginRequired;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.service.NoticeService;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @Description
 * @Author bin
 * @Date 2021/09/08
 */
@RestController
@RequestMapping(value = "/notice")
public class NoticeController {

    @Autowired
    NoticeService noticeService;

    @LoginRequired
    @ApiOperation(value = "将用户未读消息设置为已读")
    @RequestMapping (value = "/notice2read", method = RequestMethod.GET)
    public JsonResult notice2read(HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return noticeService.allNotice2read(email);
    }


    @LoginRequired
    @ApiOperation(value = "将用户单个消息设置为已读,需传入该id")
    @RequestMapping (value = "/notice2read/{noticeId}", method = RequestMethod.GET)
    public JsonResult notice2readById(

        @ApiParam(name = "noticeId", value = "通知id") @PathVariable String noticeId,
        HttpServletRequest request){

        return noticeService.notice2readById(noticeId);
    }

    @LoginRequired
    @ApiOperation(value = "得到用户的通知列表")
    @RequestMapping (value = "/user/noticeList", method = RequestMethod.POST)
    public JsonResult getUserNoticeList(@RequestBody FindDTO findDTO, HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return ResultUtils.success(noticeService.getUserNoticeList(findDTO,email));
    }

    @LoginRequired
    @ApiOperation(value = "得到用户的通知数量")
    @RequestMapping (value = "/user/noticeCount", method = RequestMethod.GET)
    public JsonResult getNoticeCount(HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return ResultUtils.success(noticeService.countUserNoticeNum(email));
    }

    @LoginRequired
    @ApiOperation(value = "得到用户未读的通知数量")
    @RequestMapping (value = "/user/unreadNoticeCount", method = RequestMethod.GET)
    public JsonResult getUnreadNoticeCount(HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return ResultUtils.success(noticeService.countUserUnreadNoticeNum(email));
    }

}
