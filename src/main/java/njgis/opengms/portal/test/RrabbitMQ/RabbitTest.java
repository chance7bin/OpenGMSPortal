package njgis.opengms.portal.test.RrabbitMQ;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description description
 * @Author bin
 * @Date 2021/07/12
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitTest {
    @Autowired
    RabbitTemplate rabbitTemplate;

//    exchange策略：direct
    @Test
    public void directTest(){
        rabbitTemplate.convertAndSend("hello-queue","hello direct!");
    }

    @Test
    public void fanoutTest(){
        rabbitTemplate.convertAndSend(FanoutConfig.FANOUTNAME, null, "hello fanout");
    }

    @Test
    public void topicTest(){
        rabbitTemplate.convertAndSend(TopicConfig.TOPICNAME, "xiaomi.news", "小米新闻");
        rabbitTemplate.convertAndSend(TopicConfig.TOPICNAME, "huawei.news", "华为新闻");
        rabbitTemplate.convertAndSend(TopicConfig.TOPICNAME, "xiaomi.phone", "小米手机");
        rabbitTemplate.convertAndSend(TopicConfig.TOPICNAME, "huawei.phone", "华为手机");
        rabbitTemplate.convertAndSend(TopicConfig.TOPICNAME, "phone.news", "手机新闻");
    }
}
