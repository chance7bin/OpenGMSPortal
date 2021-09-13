package njgis.opengms.portal.controller;

import io.swagger.annotations.ApiOperation;
import njgis.opengms.portal.dao.NoticeDao;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.service.NoticeService;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

/**
 * @Description
 * @Author bin
 * @Date 2021/09/09
 */
@RestController
public class WebsocketController {

    @Autowired
    SimpMessagingTemplate messagingTemplate;

    @Autowired
    NoticeService noticeService;

    @Autowired
    NoticeDao noticeDao;

    @MessageMapping("/sendNotice")
    public void sendNotice(Principal principal){
        // Principal用来获取当前登录用户的信息()
        if (principal != null){
            String from = principal.getName();
            // chat.setFrom(from);
            // messagingTemplate.convertAndSendToUser(chat.getTo(),"/subscribe/receiveNotice",chat);
            messagingTemplate.convertAndSendToUser("782807969@qq.com","/subscribe/receiveNotice","hello man!!");
        }
    }

    @ApiOperation(value = "websocket测试页面（登录后才可以收到消息）websocket前端的使用示例在WebsocketExample.html中")
    @GetMapping(value = "/wsExample")
    public ModelAndView toExample(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("WebsocketExample");
        return modelAndView;
    }


    @ApiOperation(value = "发送消息的测试")
    @GetMapping(value = "/sendToClient")
    public JsonResult sendToClient(){
        noticeService.sendToClient(noticeDao.findFirstByRecipient("782807969@qq.com"));
        return ResultUtils.success();
    }

}
