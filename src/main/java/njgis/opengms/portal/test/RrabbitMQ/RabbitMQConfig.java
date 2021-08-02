package njgis.opengms.portal.test.RrabbitMQ;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @Description description
 * @Author bin
 * @Date 2021/07/12
 */

@Configuration
public class RabbitMQConfig
{
    public final static String DIRECTNAME = "guest-direct";
    @Bean
    Queue queue(){
//        return new Queue("hello-queue");
//        return new Queue("hello-queue");
        return new Queue("hello-queue");
    }
}
