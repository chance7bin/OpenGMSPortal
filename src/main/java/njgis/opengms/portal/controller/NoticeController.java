package njgis.opengms.portal.controller;

import io.swagger.annotations.ApiOperation;
import njgis.opengms.portal.component.LoginRequired;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

}
