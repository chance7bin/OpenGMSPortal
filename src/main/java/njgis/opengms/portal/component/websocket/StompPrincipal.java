package njgis.opengms.portal.component.websocket;

import java.security.Principal;

/**
 * @Description 每个StompHeaderAccessor或WebSocketSession对象都有Principal主体的实例,
 *                可以从中获取用户名，它不是自动生成的，必须由服务器为每个会话手动生成
 * @Author bin
 * @Date 2021/09/09
 */
public class StompPrincipal implements Principal {
    String name;

    StompPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
