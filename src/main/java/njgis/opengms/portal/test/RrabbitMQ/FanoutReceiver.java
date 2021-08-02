package njgis.opengms.portal.test.RrabbitMQ;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @Description description
 * @Author bin
 * @Date 2021/07/12
 */
@Component
public class FanoutReceiver {
    @RabbitListener(queues = "queue-one")
    public void handler1(String message){
        System.out.println("FanoutReceiver:handler1:" + message);
    }
    @RabbitListener(queues = "queue-two")
    public void handler2(String message){
        System.out.println("FanoutReceiver:handler2:" + message);
    }
}
