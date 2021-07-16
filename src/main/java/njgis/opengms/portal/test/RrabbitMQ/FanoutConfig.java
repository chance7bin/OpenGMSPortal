package njgis.opengms.portal.test.RrabbitMQ;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description description
 * @Author bin
 * @Date 2021/07/12
 */
@Configuration
public class FanoutConfig {
    public final static String FANOUTNAME = "guest-fanout";

    @Bean
    FanoutExchange fanoutExchange(){
        return new FanoutExchange(FANOUTNAME,true,false);
    }

    @Bean
    Queue queueOne(){
        return new Queue("queue-one");
    }

    @Bean
    Queue queueTwo(){
        return new Queue("queue-two");
    }

    @Bean
    Binding bindingOne(){
        return BindingBuilder.bind(queueOne()).to(fanoutExchange());
    }

    @Bean
    Binding bindingTwo(){
        return BindingBuilder.bind(queueTwo()).to(fanoutExchange());
    }
}
