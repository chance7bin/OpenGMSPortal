package njgis.opengms.portal.test.WebSocketTest;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 如果消息的前缀是"/topic"，就会将消息转发给消息代理(broker)
        registry.enableSimpleBroker("/topic","/queue");
        // 前缀为"/app"的destination可以通过@MessageMapping注解的方法处理
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 客户端通过这里配置的URL建立WebSocket连接
        registry.addEndpoint("/chat").withSockJS();
    }
}
