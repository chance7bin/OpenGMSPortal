package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.java.Log;
import njgis.opengms.portal.component.LoginRequired;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.enums.ResultEnum;
import njgis.opengms.portal.service.UserService;
import njgis.opengms.portal.utils.IpUtil;
import njgis.opengms.portal.utils.ResultUtils;
import njgis.opengms.portal.utils.Utils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @Description 用户控制器
 * @Author kx
 * @Date 2021/7/5
 * @Version 1.0.0
 */

@RestController
@RequestMapping({"/user","/profile"})
public class UserRestController {

    @Autowired
    UserService userService;

    /**
     * @Description 用户登录
     * @param account email
     * @param password 使用sha256加密的密码
     * @param request
     * @Return JsonResult
     * @Author kx
     * @Date 2021/7/6
     **/
    @ApiOperation(value = "用户登录")
    @RequestMapping(value = "/in", method = RequestMethod.POST)
    public JsonResult login(@ApiParam(name = "account", value = "email", required = true)
                            @RequestParam(value = "account") String account,
                            @ApiParam(name = "password", value = "经过sha256加密后的密码", required = true)
                            @RequestParam(value = "password") String password,
                            HttpServletRequest request) {

        System.out.println("in");
        String ip = IpUtil.getIpAddr(request);
        System.out.println(ip);

        JSONObject result = userService.loginUserServer(account, DigestUtils.sha256Hex(password), ip);
        if (result != null) {
            // 密码验证成功，将用户数据放入到Session中
            String email = result.getString("email");
            String name = result.getString("name");
            String role = result.getString("role");
            userService.setUserSession(request, email, name, role);

//            WebSocketTest webSocketTest = new WebSocketTest(); //发送websocket信息
//            webSocketTest.sendMessageToAll("user change");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email", email);
            jsonObject.put("name", name);

            return ResultUtils.success(jsonObject);
        }

        return ResultUtils.error(-1, "Error");
    }

    /**
     * @Description 用户登录状态查询
     * @param request
     * @Return com.alibaba.fastjson.JSONObject
     * @Author kx
     * @Date 2021/7/6
     **/
    @ApiOperation(value = "用户登录状态查询", notes = "成功返回用户email，昵称，头像地址")
    @RequestMapping(value = "/load", method = RequestMethod.GET)
    public JsonResult loadUser(HttpServletRequest request) throws Exception {

        String email = Utils.checkLoginStatus(request);

        if (email == null) {
            JSONObject user = new JSONObject();
            return ResultUtils.error(-1, "no login");
        } else {
            return userService.loadUser(email);
        }
    }

    /**
     * @Description 用户登出
     * @param request
     * @Return RedirectView 跳转到首页
     * @Author kx
     * @Date 2021/7/6
     **/
    @ApiOperation(value = "用户登出", notes = "返回到门户主页")
    @RequestMapping(value = "/out", method = RequestMethod.POST)
    public RedirectView logout(HttpServletRequest request) {

        userService.removeUserSession(request);

        RedirectView redirectView = new RedirectView("/home", false /*是否使用相对路径*/, false/* 兼容http1.0*/ , false/* 是否暴露查询参数*/);

//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("redirect:/home");

        return redirectView;
    }

    /**
     * @Description 用户注册页面
     * @param
     * @Return org.springframework.web.servlet.ModelAndView
     * @Author kx
     * @Date 2021/7/6
     **/
    @ApiOperation(value = "返回用户的注册页面")
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView getRegister() {
        System.out.println("register");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        modelAndView.addObject("name", "OpenGMS");

        return modelAndView;
    }

    /**
     * @Description 用户登录页面
     * @param request
     * @Return org.springframework.web.servlet.ModelAndView
     * @Author kx
     * @Date 2021/7/6
     **/
    @ApiOperation(value = "返回用户的登录页面")
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView getLogin(HttpServletRequest request) {
        System.out.println("login");
        HttpSession session = request.getSession();
        ModelAndView modelAndView = new ModelAndView();
        //若用户没有登录，则进入登录页面
        if (session.getAttribute("email") == null) {
            modelAndView.setViewName("login");
            modelAndView.addObject("notice", "After login, more functions will be unlocked.");

            Object preUrl_obj = session.getAttribute("preUrl"); //preUrl是跳转到这个接口之前打开的页面链接
            String preUrl = null;
            if (preUrl_obj != null) {
                preUrl = preUrl_obj.toString();
            }else {
                preUrl = request.getHeader("REFERER");
            }
            modelAndView.addObject("preUrl", preUrl);
            session.removeAttribute("preUrl");
        }
        else { //若用户已经登录，则进入个人空间
            modelAndView.setViewName("redirect:/user/userSpace");

        }

        return modelAndView;
    }

    /**
     * @Description 从用户服务器获取用户基础信息
     * @param request
     * @Return njgis.opengms.portal.entity.doo.JsonResult
     * @Author kx
     * @Date 2021/7/6
     **/
    @LoginRequired
    @ApiOperation(value = "从用户服务器获取用户基础信息", notes = "@loginRequired")
    @RequestMapping(value = "/getInfoFromUserServer", method = RequestMethod.GET)
    public JsonResult getInfoFromUserServer(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();

        String email = session.getAttribute("email").toString();
        JSONObject j_result = userService.getInfoFromUserServer(email);

        if(j_result.getString("msg").equals("out")){
            return ResultUtils.error(-1,"out");
        }else if (j_result.getString("msg").equals("no user")){
            return ResultUtils.error(-2,"no user");
        }else if (j_result.getString("msg").equals("err")){
            return ResultUtils.error(-2,"err");
        }

        return ResultUtils.success(j_result);

    }

    /**
     * @Description 从用户服务器获取用户详细信息
     * @param request
     * @Return njgis.opengms.portal.entity.doo.JsonResult
     * @Author kx
     * @Date 2021/7/6
     **/
    @LoginRequired
    @ApiOperation(value = "从用户服务器获取用户详细信息", notes = "@loginRequired")
    @RequestMapping(value = "/getFullUserInfo", method = RequestMethod.GET)
    public JsonResult getFullUserInfo(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();

        String email = session.getAttribute("email").toString();
        JSONObject j_result = userService.getFullUserInfo(email);

        if(j_result.getString("msg").equals("out")){
            ResultUtils.error(-1,"out");
        }else if (j_result.getString("msg").equals("no user")){
            ResultUtils.error(-2,"no user");
        }

        return ResultUtils.success(j_result);

    }

    /**
     * @Description 从用户服务器获取用户文件资源
     * @param request
     * @Return njgis.opengms.portal.entity.doo.JsonResult
     * @Author kx
     * @Date 2021/7/6
     **/
    @LoginRequired
    @ApiOperation(value = "从用户服务器获取用户文件资源", notes = "@loginRequired")
    @RequestMapping(value = "/getUserResource", method = RequestMethod.GET)
    public JsonResult getUserResource(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();

        String email = session.getAttribute("email").toString();
        JSONObject j_result = userService.getUserResource(email);

        if(j_result.getString("msg").equals("out")){
            ResultUtils.error(-1,"out");
        }else if (j_result.getString("msg").equals("no user")){
            ResultUtils.error(-2,"no user");
        }

        return ResultUtils.success(j_result);

    }
}
