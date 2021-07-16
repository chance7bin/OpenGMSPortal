package njgis.opengms.portal.test.RrabbitMQ;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @Description description
 * @Author bin
 * @Date 2021/07/12
 */
@Component
public class DirectReceiver {
    @RabbitListener(queues = "hello-queue")
    public void handler1(String msg){
        System.out.println("DirectReceiver:" + msg);
    }
}
