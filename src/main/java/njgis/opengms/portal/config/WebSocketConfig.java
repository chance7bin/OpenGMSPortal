package njgis.opengms.portal.config;

import njgis.opengms.portal.component.websocket.CustomHandshakeHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * @Description
 * @Author bin
 * @Date 2021/09/09
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 如果消息的前缀是"/subscribe"，就会将消息转发给消息代理(broker)
        registry.enableSimpleBroker("/subscribe");
        // 前缀为"/app"的destination可以通过@MessageMapping注解的方法处理
        registry.setApplicationDestinationPrefixes("/notice");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 客户端通过这里配置的URL建立WebSocket连接
        registry.addEndpoint("websocket")
            .setHandshakeHandler(new CustomHandshakeHandler()) //设置自定义握手处理器
            .withSockJS();
    }
}
