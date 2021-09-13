package njgis.opengms.portal.component.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Map;
import java.util.UUID;

/**
 * @Description 通过覆盖DefaultHandshakeHandler为每个连接生成唯一的StompPrincipal，即连接后生成的user-name
 * @Author bin
 * @Date 2021/09/09
 */
public class CustomHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        // 向下转型 先判断request是否是ServletServerHttpRequest的实例
        // 从运行结果上看进入了方法体内，表示传入的request其实是ServletServerHttpRequest的一个实例
        // 向下转型的目的是为了使用getServletRequest方法获得session来获取连接用户的uid
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpSession httpSession = servletRequest.getServletRequest().getSession();
            String email = (String) httpSession.getAttribute("email");
            return new StompPrincipal(email == null ? UUID.randomUUID().toString() : email);
            //httpSession 为null
        }
        // 使用uuid作为名称生成principal
        return new StompPrincipal(UUID.randomUUID().toString());
    }
}
