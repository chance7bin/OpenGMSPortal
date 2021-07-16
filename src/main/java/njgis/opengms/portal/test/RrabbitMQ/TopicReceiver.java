package njgis.opengms.portal.test.RrabbitMQ;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @Description description
 * @Author bin
 * @Date 2021/07/12
 */
@Component
public class TopicReceiver {
    @RabbitListener(queues = "xiaomi")
    public void handler1(String message){
        System.out.println("TopicReceiver:xiaomi:" + message);
    }
    @RabbitListener(queues = "huawei")
    public void handler2(String message){
        System.out.println("TopicReceiver:huawei:" + message);
    }
    @RabbitListener(queues = "phone")
    public void handler3(String message){
        System.out.println("TopicReceiver:phone:" + message);
    }
}
