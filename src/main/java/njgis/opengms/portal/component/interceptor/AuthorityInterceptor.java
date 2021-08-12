package njgis.opengms.portal.component.interceptor;

import njgis.opengms.portal.component.LoginRequired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;

/**
 * @Description 用户登录拦截器，拦截未登录用户请求：记录请求地址后，跳转至登录页面
 * @Author kx
 * @Date 2021/7/7
 * @Version 1.0.0
 */
@Component
public class AuthorityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // System.out.println("preHandle AuthorityInterceptor:" + request.getRequestURI());

        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HttpSession session = request.getSession();
        Object userOid_obj = session.getAttribute("email");//userserver接入后统一使用email
        // ①:START 方法注解级拦截器
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        // 判断接口是否需要登录
        LoginRequired methodAnnotation = method.getAnnotation(LoginRequired.class);
        // 有 @LoginRequired 注解，需要认证
        if (methodAnnotation != null) {
            // 这写你拦截需要干的事儿，比如取缓存，SESSION，权限判断等
            if (userOid_obj==null){
                //若未登录，则记录用户试图访问的接口，在登录后自动请求
                session.setAttribute("preUrl",request.getRequestURI());
                response.sendRedirect("/user/login");
                return false;
            }

            return true;
        }

        return true;
    }

}