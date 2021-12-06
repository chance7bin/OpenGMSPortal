package njgis.opengms.portal.component.interceptor;

import njgis.opengms.portal.component.AdminRequired;
import njgis.opengms.portal.enums.UserRoleEnum;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;


/**
 * @Description 管理员权限拦截器，拦截非root和非admin用户
 * @Author kx
 * @Date 2021/7/7
 * @Version 1.0.0
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HttpSession session = request.getSession();
        Object role = session.getAttribute("role");//userserver接入后统一使用email
        // ①:START 方法注解级拦截器
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        // 判断接口是否需要管理员权限
        AdminRequired methodAnnotation = method.getAnnotation(AdminRequired.class);
        // 有 @LoginRequired 注解，需要认证
        if (methodAnnotation != null) {
            // 这写你拦截需要干的事儿，比如取缓存，SESSION，权限判断等
            if (role == null){ //未登录，直接拦截至登录页面
                session.setAttribute("preUrl",request.getRequestURI());
                response.sendRedirect("/user/login");
                return false;
            }else { // 判断用户权限
                String role_str = role.toString();
                UserRoleEnum userRole = UserRoleEnum.getUserRoleByRoleName(role_str);
                if(userRole.isAdmin()){
                    return true;
                }

                //TODO 是否要返回无权限提示?
                return false;
            }

        }

        return true;
    }

}