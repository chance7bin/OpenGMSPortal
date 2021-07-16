package njgis.opengms.portal.test.RrabbitMQ;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description description
 * @Author bin
 * @Date 2021/07/12
 */
@Configuration
public class TopicConfig {
    public final static String TOPICNAME = "guest-topic";

    @Bean
    TopicExchange topicExchange(){
        return new TopicExchange(TOPICNAME,true,false);
    }

    @Bean
    Queue xiaomi(){
        return new Queue("xiaomi");
    }

    @Bean
    Queue huawei(){
        return new Queue("huawei");
    }

    @Bean
    Queue phone(){
        return new Queue("phone");
    }

    @Bean
    Binding bindingXiaomi(){
        return BindingBuilder.bind(xiaomi()).to(topicExchange())
            .with("xiaomi.#");
    }

    @Bean
    Binding bindingHuawei(){
        return BindingBuilder.bind(huawei()).to(topicExchange()).with("huawei.#");
    }

    @Bean
    Binding bindingPhone(){
        return BindingBuilder.bind(phone()).to(topicExchange()).with("#.phone.#");
    }
}
