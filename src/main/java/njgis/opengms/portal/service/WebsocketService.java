package njgis.opengms.portal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author bin
 * @Date 2021/09/09
 */
@Service
public class WebsocketService {

    @Autowired
    SimpMessagingTemplate messagingTemplate;

    public void sendNotice(){
            messagingTemplate.convertAndSendToUser("782807969@qq.com","/subscribe/receiveNotice","hello man!!");
    }

}
