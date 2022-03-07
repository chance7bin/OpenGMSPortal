package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.component.LoginRequired;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.entity.dto.user.*;
import njgis.opengms.portal.entity.po.User;
import njgis.opengms.portal.service.*;
import njgis.opengms.portal.utils.IpUtil;
import njgis.opengms.portal.utils.ResultUtils;
import njgis.opengms.portal.utils.Utils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @Description 用户控制器
 * @Author kx
 * @Date 2021/7/5
 * @Version 1.0.0
 */

@Slf4j
@RestController
@RequestMapping({"/user","/profile"})
public class UserRestController {


    @Autowired
    UserService userService;

    @Autowired
    DataItemService dataItemService;

    @Autowired
    DataHubService dataHubService;

    @Autowired
    DataMethodService dataMethodService;
    
    @Autowired
    GenericService genericService;

    @Autowired
    VersionService versionService;

    @Autowired
    NoticeService noticeService;

    @Autowired
    ManagementSystemService managementSystemService;

    @Value("${htmlLoadPath}")
    private String htmlLoadPath;

    /**
     * @Description
     * @param accessId user accessId
     * @param req
     * @Return org.springframework.web.servlet.ModelAndView
     * @Author kx
     * @Date 22/2/23
     **/
    @ApiOperation(value = "访问用户个人主页")
    @RequestMapping(value = "/{accessId}", method = RequestMethod.GET)
    public ModelAndView getUserPage(@PathVariable("accessId") String accessId, HttpServletRequest req) {
//        个人主页使用session的uid也就是username判断
        ModelAndView modelAndView = new ModelAndView();
        HttpSession session = req.getSession();

        if(session.getAttribute("email") == null){
            modelAndView.setViewName("login");
            modelAndView.addObject("notice","After login, more functions will be unlocked.");
            Object preUrl_obj = session.getAttribute("preUrl");
            String preUrl = preUrl_obj==null? req.getHeader("REFERER"):preUrl_obj.toString();
            preUrl = preUrl==null?req.getRequestURL().toString():preUrl;
            modelAndView.addObject("preUrl",preUrl);
            session.removeAttribute("preUrl");
        }
        else {
//            通过userid在门户数据库拿到email
            User userFromDb = userService.getByAccessId(accessId);
            JSONObject user = userService.getInfoFromUserServer(userFromDb.getEmail());
            JSONObject userInfo = (JSONObject) JSONObject.toJSON(user);
            Object oid_obj = session.getAttribute("email");
            if(oid_obj!=null) {
                String loginId = oid_obj.toString();
                userInfo.put("loginId", loginId);
            }else{
                userInfo.put("loginId", null);
            }
            modelAndView.setViewName("user_page_overview");
            modelAndView.addObject("userInfo", userInfo);

            modelAndView.addObject("loadPath", htmlLoadPath);

        }



        return modelAndView;
    }

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

        // System.out.println("in");
        String ip = IpUtil.getIpAddr(request);
        // System.out.println(ip);
        log.info("login ip:{}",ip);

        JSONObject result = userService.loginUserServer(account, DigestUtils.sha256Hex(password), ip);
        if (result != null) {
            // 密码验证成功，将用户数据放入到Session中
            String email = result.getString("email");
            String name = result.getString("name");
            // String role = result.getString("role");
            userService.setUserSession(request, email, name, null);

//            WebSocketTest webSocketTest = new WebSocketTest(); //发送websocket信息
//            webSocketTest.sendMessageToAll("user change");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email", email);
            jsonObject.put("name", name);

            // 记录用户访问的数量
            managementSystemService.recordUserViewCount(ip);

            // return ResultUtils.success(jsonObject);
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
            return ResultUtils.unauthorized();
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
    @RequestMapping(value = "/out", method = RequestMethod.GET)
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
        log.info("register");
        // System.out.println("register");
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
        log.info("login");
        // System.out.println("login");
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

    /**
     * 跳转到个人中心界面
     * @param request
     * @return org.springframework.web.servlet.ModelAndView
     * @Author bin
     **/
    @ApiOperation(value = "跳转到个人中心界面")
    @RequestMapping(value = "/userSpace", method = RequestMethod.GET)
    public ModelAndView getUserSpace(HttpServletRequest request) {
        HttpSession session = request.getSession();
        ModelAndView modelAndView = new ModelAndView();
        if (session.getAttribute("email") == null) {
            modelAndView.setViewName("login");
            modelAndView.addObject("notice", "After login, more functions will be unlocked.");

            Object preUrl_obj = session.getAttribute("preUrl");
            String preUrl = null;
            if (preUrl_obj != null) {
                preUrl = preUrl_obj.toString();
            }else {
                preUrl = request.getHeader("REFERER");
            }
            modelAndView.addObject("preUrl", preUrl);
            session.removeAttribute("preUrl");

        } else {
            // System.out.println("userSpace1");

            modelAndView.setViewName("userSpace1");
        }

        return modelAndView;

    }

    // /**
    //  * 得到用户上传的dataItem
    //  * @param findDTO
    //  * @param request
    //  * @return njgis.opengms.portal.entity.doo.JsonResult
    //  * @Author bin
    //  **/
    // @LoginRequired
    // @ApiOperation(value = "得到用户上传的dataItem [/user(profile)/getDataItems]")
    // @RequestMapping(value = "/dataItemList", method = RequestMethod.POST)
    // JsonResult getUserUploadDataItem(@RequestBody FindDTO findDTO,HttpServletRequest request){
    //     HttpSession session = request.getSession();
    //     String email = session.getAttribute("email").toString();
    //     return ResultUtils.success(genericService.getUserUploadItemList(findDTO, email, ItemTypeEnum.DataItem));
    // }

    // /**
    //  * 得到用户上传的dataHub
    //  * @param findDTO
    //  * @param request
    //  * @return njgis.opengms.portal.entity.doo.JsonResult
    //  * @Author bin
    //  **/
    // @LoginRequired
    // @ApiOperation(value = "得到用户上传的dataHub [/user(profile)/getDataHubs]")
    // @RequestMapping(value = "/dataHubList", method = RequestMethod.POST)
    // JsonResult getUserUploadDataHubs(@RequestBody FindDTO findDTO,HttpServletRequest request) {
    //     HttpSession session = request.getSession();
    //     String email = session.getAttribute("email").toString();
    //     return ResultUtils.success(genericService.getUserUploadItemList(findDTO, email, ItemTypeEnum.DataHub));
    // }

    // /**
    //  * 得到用户上传的dataMethod
    //  * @param email
    //  * @param page
    //  * @param pagesize
    //  * @param asc
    //  * @param type
    //  * @return njgis.opengms.portal.entity.doo.JsonResult
    //  * @Author bin
    //  **/
    // @ApiOperation(value = "得到用户上传的dataMethod [/dataApplication/getApplication]")
    // @RequestMapping(value = "/dataMethodList", method = RequestMethod.GET)      // 这是拿到用户上传的所有条目
    // public JsonResult getUserUploadDataMethod(@RequestParam(value = "email", required = false) String email,
    //                                     @RequestParam(value = "page", required = false) Integer page,
    //                                     @RequestParam(value = "pagesize", required = false) Integer pagesize,
    //                                     @RequestParam(value = "asc", required = false) Integer asc,
    //                                     @ApiParam(name = "type", value = "区分process与visual") @RequestParam(value = "type", required = false) String type
    // ) {
    //     return ResultUtils.success(dataMethodService.getUsersUploadData(email, page, pagesize, asc, type));
    // }


    // /**
    //  * 得到用户上传的concept
    //  * @param findDTO
    //  * @param request
    //  * @return njgis.opengms.portal.entity.doo.JsonResult
    //  * @Author bin
    //  **/
    // @LoginRequired
    // @ApiOperation(value = "得到用户上传的concept [/repository/getConceptsByUserId]")
    // @RequestMapping(value = "/conceptList",method = RequestMethod.POST)
    // public JsonResult getConceptsByUserId(@RequestBody FindDTO findDTO,HttpServletRequest request){
    //     HttpSession session = request.getSession();
    //     String email = session.getAttribute("email").toString();
    //     return ResultUtils.success(genericService.getUserUploadItemList(findDTO, email, ItemTypeEnum.Concept));
    // }

    // /**
    //  * 得到用户上传的spatialReference
    //  * @param findDTO
    //  * @param request
    //  * @return njgis.opengms.portal.entity.doo.JsonResult
    //  * @Author bin
    //  **/
    // @LoginRequired
    // @ApiOperation(value = "得到用户上传的spatialReference [/repository/getSpatialsByUserId]")
    // @RequestMapping(value = "/spatialReferenceList",method = RequestMethod.POST)
    // public JsonResult getSpatialsByUserId(@RequestBody FindDTO findDTO,HttpServletRequest request){
    //     HttpSession session = request.getSession();
    //     String email = session.getAttribute("email").toString();
    //     return ResultUtils.success(genericService.getUserUploadItemList(findDTO, email, ItemTypeEnum.SpatialReference));
    // }


    // /**
    //  * 得到用户template
    //  * @param request
    //  * @return njgis.opengms.portal.entity.doo.JsonResult
    //  * @Author bin
    //  **/
    // @LoginRequired
    // @ApiOperation(value = "得到用户template [ /repository/getTemplatesByUserId ]")
    // @RequestMapping(value = "/templateList",method = RequestMethod.POST)
    // public JsonResult getTemplatesByUserId(@RequestBody FindDTO findDTO,HttpServletRequest request){
    //     HttpSession session = request.getSession();
    //     String email = session.getAttribute("email").toString();
    //     return ResultUtils.success(genericService.getUserUploadItemList(findDTO, email, ItemTypeEnum.Template));
    // }


    // /**
    //  * 得到用户unit
    //  * @param findDTO
    //  * @param request
    //  * @return njgis.opengms.portal.entity.doo.JsonResult
    //  * @Author bin
    //  **/
    // @LoginRequired
    // @ApiOperation(value = "得到用户unit [ /repository/getUnitsByUserId ]")
    // @RequestMapping(value = "/unitList",method = RequestMethod.POST)
    // public JsonResult getUnitsByUserId(@RequestBody FindDTO findDTO, HttpServletRequest request){
    //     HttpSession session = request.getSession();
    //     String email = session.getAttribute("email").toString();
    //     return ResultUtils.success(genericService.getUserUploadItemList(findDTO, email, ItemTypeEnum.Unit));
    // }

    // /**
    //  * 得到用户上传的theme
    //  * @param findDTO
    //  * @param request
    //  * @return njgis.opengms.portal.entity.doo.JsonResult
    //  * @Author bin
    //  **/
    // @LoginRequired
    // @ApiOperation(value = "得到用户theme [ /repository/getThemesByUserId ]")
    // @RequestMapping(value = "/themeList",method = RequestMethod.POST)
    // public JsonResult getThemesByUserId(@RequestBody FindDTO findDTO, HttpServletRequest request){
    //     HttpSession session = request.getSession();
    //     String email = session.getAttribute("email").toString();
    //     JSONObject result = genericService.getUserUploadItemList(findDTO, email, ItemTypeEnum.Theme);
    //     // List<Theme> content = (List<Theme>) result.get("content");
    //     return ResultUtils.success(result);
    // }


    @LoginRequired
    @ApiOperation(value = "得到用户提交的version（不建议，用下面的，没有分页很慢） [ /theme/getMessageData ]")
    @RequestMapping(value = "/versionList/edit",method = RequestMethod.POST)
    public JsonResult getUserEditVersion(HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return versionService.getUserEditVersion(email);
    }


    @LoginRequired
    @ApiOperation(value = "得到用户审核的version（不建议，用下面的，没有分页很慢） [ /theme/getMessageData ]")
    @RequestMapping(value = "/versionList/review",method = RequestMethod.POST)
    public JsonResult getUserReviewVersion(HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return versionService.getUserReviewVersion(email);
    }

    @LoginRequired
    @ApiOperation(value = "根据条目类型得到用户提交（你是条目的修改者）的未审核的version [ /theme/getMessageData ]")
    @ApiImplicitParams({
        @ApiImplicitParam(name="type",value="条目大类: Model/Data/Community/Theme",required=true)
    })
    @RequestMapping(value = "/versionList/edit/uncheck/{type}",method = RequestMethod.POST)
    public JsonResult getUserUncheckEditVersionByType(@PathVariable String type, @RequestBody FindDTO findDTO, HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return versionService.getUserVersionByStatusAndByTypeAndByOperation(0,type,findDTO,email,"edit");
    }

    @LoginRequired
    @ApiOperation(value = "根据条目类型得到用户提交的已通过的version [ /theme/getMessageData ]")
    @ApiImplicitParams({
        @ApiImplicitParam(name="type",value="条目大类: Model/Data/Community/Theme",required=true)
    })
    @RequestMapping(value = "/versionList/edit/accepted/{type}",method = RequestMethod.POST)
    public JsonResult getUserAcceptedEditVersionByType(@PathVariable String type, @RequestBody FindDTO findDTO, HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return versionService.getUserVersionByStatusAndByTypeAndByOperation(1,type,findDTO,email,"edit");
    }

    @LoginRequired
    @ApiOperation(value = "根据条目类型得到用户提交的已拒绝的version [ /theme/getMessageData ]")
    @ApiImplicitParams({
        @ApiImplicitParam(name="type",value="条目大类: Model/Data/Community/Theme",required=true)
    })
    @RequestMapping(value = "/versionList/edit/rejected/{type}",method = RequestMethod.POST)
    public JsonResult getUserRejectedEditVersionByType(@PathVariable String type, @RequestBody FindDTO findDTO, HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return versionService.getUserVersionByStatusAndByTypeAndByOperation(-1,type,findDTO,email,"edit");
    }

    @LoginRequired
    @ApiOperation(value = "根据条目类型得到用户审核（你是条目的创建者）的未审核的version [ /theme/getMessageData ]")
    @ApiImplicitParams({
        @ApiImplicitParam(name="type",value="条目大类: Model/Data/Community/Theme",required=true)
    })
    @RequestMapping(value = "/versionList/review/uncheck/{type}",method = RequestMethod.POST)
    public JsonResult getUserUncheckReviewVersionByType(@PathVariable String type, @RequestBody FindDTO findDTO, HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return versionService.getUserVersionByStatusAndByTypeAndByOperation(0,type,findDTO,email,"review");
    }

    @LoginRequired
    @ApiOperation(value = "根据条目类型得到用户审核的已通过的version [ /theme/getMessageData ]")
    @ApiImplicitParams({
        @ApiImplicitParam(name="type",value="条目大类: Model/Data/Community/Theme",required=true)
    })
    @RequestMapping(value = "/versionList/review/accepted/{type}",method = RequestMethod.POST)
    public JsonResult getUserAcceptedReviewVersionByType(@PathVariable String type, @RequestBody FindDTO findDTO, HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return versionService.getUserVersionByStatusAndByTypeAndByOperation(1,type,findDTO,email,"review");
    }

    @LoginRequired
    @ApiOperation(value = "根据条目类型得到用户审核的已拒绝的version [ /theme/getMessageData ]")
    @ApiImplicitParams({
        @ApiImplicitParam(name="type",value="条目大类: Model/Data/Community/Theme",required=true)
    })
    @RequestMapping(value = "/versionList/review/rejected/{type}",method = RequestMethod.POST)
    public JsonResult getUserRejectedReviewVersionByType(@PathVariable String type, @RequestBody FindDTO findDTO, HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return versionService.getUserVersionByStatusAndByTypeAndByOperation(-1,type,findDTO,email,"review");
    }

    @LoginRequired
    @ApiOperation(value = "得到用户的通知列表")
    @RequestMapping (value = "/noticeList", method = RequestMethod.POST)
    public JsonResult getUserNoticeList(@RequestBody FindDTO findDTO, HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return ResultUtils.success(noticeService.getUserNoticeList(findDTO,email));
    }

    @LoginRequired
    @ApiOperation(value = "得到用户的通知数量")
    @RequestMapping (value = "/noticeCount", method = RequestMethod.GET)
    public JsonResult getNoticeCount(HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return ResultUtils.success(noticeService.countUserNoticeNum(email));
    }

    @LoginRequired
    @ApiOperation(value = "得到用户未读的通知数量")
    @RequestMapping (value = "/unreadNoticeCount", method = RequestMethod.GET)
    public JsonResult getUnreadNoticeCount(HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return ResultUtils.success(noticeService.countUserUnreadNoticeNum(email));
    }

    @LoginRequired
    @ApiOperation(value = "得到用户贡献的资源数量")
    @RequestMapping (value = "/resourceCount", method = RequestMethod.GET)
    public JsonResult getResourceCount(HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return ResultUtils.success(userService.countResource(email));
    }

    // @ApiOperation(value = "得到门户管理员")
    // @RequestMapping (value = "/admin", method = RequestMethod.GET)
    // public List<User> getAdminUser(){
    //     return userService.getAdminUser();
    // }
    //
    // @ApiOperation(value = "得到门户root")
    // @RequestMapping (value = "/root", method = RequestMethod.GET)
    // public List<User> getRootUser(){
    //     return userService.getRootUser();
    // }

    @ApiOperation(value = "根据email得到用户名")
    @RequestMapping (value = "/name", method = RequestMethod.GET)
    public JsonResult getUserName(@PathParam("email") String email){
        return ResultUtils.success(userService.getUserName(email));
    }

    @ApiOperation(value = "通过用户服务器发送验证码")
    @RequestMapping(value = "/sendResetByUserserver", method = RequestMethod.POST)
    public JsonResult sendResetByUserserver(@RequestParam String email) throws IOException, URISyntaxException {

        String result = userService.sendResetByUserserver(email);
        return ResultUtils.success(result);
    }

    @ApiOperation(value = "重置密码")
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public JsonResult resetPass(@RequestParam String email,
                                @RequestParam String code,
                                @RequestParam String newPass) throws IOException, URISyntaxException {

        String result = userService.resetPassword(email,code,newPass);

        if(result.equals("suc")){
            return ResultUtils.success(result);
        }else if(result.equals("no user")){
            return ResultUtils.error(-1,result);
        }else{
            return ResultUtils.error(-2,result);
        }
    }

    @LoginRequired
    @ApiOperation(value = "[ /user/getFileByPath ]")
    @RequestMapping(value = "/fileByPath", method = RequestMethod.GET)
    JsonResult getFileByPath(@RequestParam(value = "paths[]") List<String> paths, HttpServletRequest request) throws Exception {

        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        JSONObject result = userService.getFileByPathFromUserServer(paths, email);

        if(result == null){
            return ResultUtils.error(-2, "err");
        }

        return ResultUtils.success(result);
    }

    @LoginRequired
    @RequestMapping(value="/userSpace/sendFeedback",method = RequestMethod.POST)
    JsonResult sendFeedback(@RequestParam("content") String content,  HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        String result = userService.sendFeedback(content,email);
        return ResultUtils.success(result);
    }


    @LoginRequired
    @ApiOperation(value = "更新userInfo")
    @RequestMapping(value = "/updateUserInfo",method = RequestMethod.POST)
    JsonResult updateUserInfo(@RequestBody UserInfoUpdateDTO userInfoUpdateDTO, HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession();
        String email = session.getAttribute("email").toString();

        // String introduction = userInfoUpdateDTO.getIntroduction();
        // List<String> organizations = userInfoUpdateDTO.getOrganizations();
        // List<String> externalLinks = userInfoUpdateDTO.getExternalLinks();
        // String location = userInfoUpdateDTO.getLocation();
        // List<String> researchInterests = userInfoUpdateDTO.getResearchInterests();
        // String result1 = userService.updateIntroduction(introduction, email);
        // String result2 = userService.updateOrganizations(organizations,email);
        // String result4 = userService.updateExternalLinks(externalLinks,email);
        // String result5 = userService.updateResearchInterest(researchInterests,email);
        // JSONObject result = new JSONObject();
        // result.put("int", result1);
        // result.put("org", result2);
        // result.put("exl", result4);
        // result.put("res", result5);

        User user = userService.updateUserInfo(userInfoUpdateDTO, email);

        if (user == null)
            return ResultUtils.error();
        else
            return ResultUtils.success(user);

    }

    @ApiOperation(value = "更新用户服务器的信息")
    @RequestMapping(value = "/updateUsertoServer", method = RequestMethod.POST)
    public JsonResult updateUserServer(HttpServletRequest req, @RequestBody UserShuttleDTO userShuttleDTO) throws Exception {
        HttpSession session = req.getSession();
        JSONObject jsonObject = new JSONObject();

        if (session.getAttribute("email") == null || !session.getAttribute("email").toString().equals(userShuttleDTO.getEmail())) {
            return ResultUtils.error(-1,"out");

        } else {

            String email = session.getAttribute("email").toString();
            int result = userService.updateUsertoServer(userShuttleDTO);
            if(result == -1){

                return ResultUtils.error(-1,"out");
            }else if(result == -2) {
                return ResultUtils.error(-2,"error");
            }

            req.setAttribute("name", userShuttleDTO.getName());
            return ResultUtils.success("suc");
        }

    }

    @LoginRequired
    @ApiOperation(value = "更新articles")
    @RequestMapping(value="/update/articles",method = RequestMethod.POST)
    public JsonResult updateArticles(@RequestBody ArticlesDTO articlesDTO, HttpServletRequest request){

        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();

        User user = userService.updateArticles(articlesDTO,email);
        return ResultUtils.success(user);

    }

    @LoginRequired
    @ApiOperation(value = "更新projects")
    @RequestMapping(value="/update/projects",method = RequestMethod.POST)
    public JsonResult updateProjects(@RequestBody ProjectDTO projectDTO, HttpServletRequest request){

        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();

        User user = userService.updateProjects(projectDTO,email);
        return ResultUtils.success(user);
    }

    @LoginRequired
    @ApiOperation(value = "更新conferences")
    @RequestMapping(value="/update/conferences",method = RequestMethod.POST)
    public JsonResult updateConferences(@RequestBody ConferenceDTO conferenceDTO, HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();

        User user = userService.updateConferences(conferenceDTO,email);
        return ResultUtils.success(user);

    }

    @LoginRequired
    @ApiOperation(value = "更新academicServices")
    @RequestMapping(value="/update/academicServices",method = RequestMethod.POST)
    public JsonResult updateAcademicServices(@RequestBody AcademicServiceDTO academicServiceDTO, HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();

        User user = userService.updateAcademicServices(academicServiceDTO,email);
        return ResultUtils.success(user);

    }

    @LoginRequired
    @ApiOperation(value = "更新awardsHonors")
    @RequestMapping(value="/update/awardsHonors",method = RequestMethod.POST)
    public JsonResult updateAwardsHonors(@RequestBody AwardandHonorDTO awardandHonorDTO, HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();

        User user = userService.updateAwardsHonors(awardandHonorDTO,email);
        return ResultUtils.success(user);

    }

    @LoginRequired
    @ApiOperation(value = "更新educationExperiences")
    @RequestMapping(value="/update/educationExperiences",method = RequestMethod.POST)
    public JsonResult updateEducationExperiences(@RequestBody EducationExperienceDTO educationExperienceDTO, HttpServletRequest request){

        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();

        User user = userService.updateEducationExperiences(educationExperienceDTO,email);
        return ResultUtils.success(user);
    }

    @LoginRequired
    @ApiOperation(value = "更新userLab")
    @RequestMapping(value="/update/userLab",method = RequestMethod.POST)
    public JsonResult updateLab(@RequestBody UserLabDTO userLabDTO, HttpServletRequest request){

        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();

        User user = userService.updateLab(userLabDTO,email);
        return ResultUtils.success(user);
    }


}
