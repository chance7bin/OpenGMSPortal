package njgis.opengms.portal.test.WebSocketTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class GreetingController {

    @Autowired
    SimpMessagingTemplate messagingTemplate;

    // @MessageMapping("/hello")用来接收"/app/hello"发送的消息
    // 再将消息转发到@SendTo定义的路径上(前缀为"/topic",消息将会被broker代理，再由broker广播)
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Message greeting(Message message) throws Exception{
        return message;
    }

//    @MessageMapping("/hello")
//    public void greeting(Message message) throws Exception{
//        messagingTemplate.convertAndSend("/topic/greetings",message);
//    }

    @MessageMapping("/chat")
    public void chat(Principal principal, Chat chat){
        String from = principal.getName();
        chat.setFrom(from);
//        System.out.println("------------chat.getTo():" + chat.getTo());
        messagingTemplate.convertAndSendToUser(chat.getTo(),"/queue/chat",chat);
    }

}

