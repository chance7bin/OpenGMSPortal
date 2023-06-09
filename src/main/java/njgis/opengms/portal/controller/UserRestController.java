package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.component.annotation.LoginRequired;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.user.UserServerResource;
import njgis.opengms.portal.entity.doo.user.UserSubscribeItem;
import njgis.opengms.portal.entity.dto.user.*;
import njgis.opengms.portal.entity.po.User;
import njgis.opengms.portal.enums.UserRoleEnum;
import njgis.opengms.portal.service.*;
import njgis.opengms.portal.utils.IpUtil;
import njgis.opengms.portal.utils.MyHttpUtils;
import njgis.opengms.portal.utils.ResultUtils;
import njgis.opengms.portal.utils.Utils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

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

    //远程数据容器地址
    @Value("${dataContainerIpAndPort}")
    String dataContainerIpAndPort;

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
//        accessId = accessId.split("\\?")[0];
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

            // 重试验证
            if(result.getInteger("code") != null && result.getInteger("code") == -1){
                return ResultUtils.success(result);
            }

            // 密码验证成功，将用户数据放入到Session中
            String email = result.getString("email");
            String name = result.getString("name");
            UserRoleEnum role = (UserRoleEnum)result.get("role");
            userService.setUserSession(request, email, name, role);

//            WebSocketTest webSocketTest = new WebSocketTest(); //发送websocket信息
//            webSocketTest.sendMessageToAll("user change");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email", email);
            jsonObject.put("name", name);

            // 记录用户访问的数量 -> 迁移到ItemViewRecordInterceptor
            // managementSystemService.recordUserViewCount(ip);

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
            JsonResult result = userService.loadUser(email);
            if(result.getCode()==-3){
                userService.removeUserSession(request);
            }
            return result;
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
    @ApiOperation(value = "从用户服务器获取用户基础信息", notes = "@loginRequired")
    @RequestMapping(value = "/getInfoFromUserServer", method = RequestMethod.GET)
    public JsonResult getInfoFromUserServer(@RequestParam(value = "email") String email, HttpServletRequest request) throws Exception {
//        HttpSession session = request.getSession();

//        String email = session.getAttribute("email").toString();
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
    public JsonResult getFullUserInfo(@RequestParam(value = "email",required = false,defaultValue = "loginUser")String email,HttpServletRequest request) throws Exception {

        if(email.equals("loginUser")){
            HttpSession session = request.getSession();
            email = session.getAttribute("email").toString();
        }
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

        // if(j_result.getString("msg").equals("out")){
        //     ResultUtils.error(-1,"out");
        // }else if (j_result.getString("msg").equals("no user")){
        //     ResultUtils.error(-2,"no user");
        // }
        if(!j_result.getString("msg").equals("suc")){
            ResultUtils.error();
        }

        return ResultUtils.success(j_result);

    }

    @LoginRequired
    @ApiOperation(value = "从用户服务器获取用户数据空间容量", notes = "@loginRequired")
    @RequestMapping(value = "/getCapacity", method = RequestMethod.GET)
    public JsonResult getCapacity(HttpServletRequest request){

        String email = Utils.checkLoginStatus(request);
        if(email!=null){
            return ResultUtils.success(userService.getCapacity(email));
        }else{
            return ResultUtils.error(-1,"no login");
        }

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


    // @LoginRequired
    // @ApiOperation(value = "得到用户提交的version（不建议，用下面的，没有分页很慢） [ /theme/getMessageData ]")
    // @RequestMapping(value = "/versionList/edit",method = RequestMethod.POST)
    // public JsonResult getUserEditVersion(HttpServletRequest request){
    //     HttpSession session = request.getSession();
    //     String email = session.getAttribute("email").toString();
    //     return versionService.getUserEditVersion(email);
    // }
    //
    //
    // @LoginRequired
    // @ApiOperation(value = "得到用户审核的version（不建议，用下面的，没有分页很慢） [ /theme/getMessageData ]")
    // @RequestMapping(value = "/versionList/review",method = RequestMethod.POST)
    // public JsonResult getUserReviewVersion(HttpServletRequest request){
    //     HttpSession session = request.getSession();
    //     String email = session.getAttribute("email").toString();
    //     return versionService.getUserReviewVersion(email);
    // }
    //
    // @LoginRequired
    // @ApiOperation(value = "根据条目类型得到用户提交（你是条目的修改者）的未审核的version [ /theme/getMessageData ]")
    // @ApiImplicitParams({
    //     @ApiImplicitParam(name="type",value="条目大类: Model/Data/Community/Theme",required=true)
    // })
    // @RequestMapping(value = "/versionList/edit/uncheck/{type}",method = RequestMethod.POST)
    // public JsonResult getUserUncheckEditVersionByType(@PathVariable String type, @RequestBody FindDTO findDTO, HttpServletRequest request){
    //     HttpSession session = request.getSession();
    //     String email = session.getAttribute("email").toString();
    //     return versionService.getUserVersionByStatusAndByTypeAndByOperation(0,type,findDTO,email,"edit");
    // }
    //
    // @LoginRequired
    // @ApiOperation(value = "根据条目类型得到用户提交的已通过的version [ /theme/getMessageData ]")
    // @ApiImplicitParams({
    //     @ApiImplicitParam(name="type",value="条目大类: Model/Data/Community/Theme",required=true)
    // })
    // @RequestMapping(value = "/versionList/edit/accepted/{type}",method = RequestMethod.POST)
    // public JsonResult getUserAcceptedEditVersionByType(@PathVariable String type, @RequestBody FindDTO findDTO, HttpServletRequest request){
    //     HttpSession session = request.getSession();
    //     String email = session.getAttribute("email").toString();
    //     return versionService.getUserVersionByStatusAndByTypeAndByOperation(1,type,findDTO,email,"edit");
    // }
    //
    // @LoginRequired
    // @ApiOperation(value = "根据条目类型得到用户提交的已拒绝的version [ /theme/getMessageData ]")
    // @ApiImplicitParams({
    //     @ApiImplicitParam(name="type",value="条目大类: Model/Data/Community/Theme",required=true)
    // })
    // @RequestMapping(value = "/versionList/edit/rejected/{type}",method = RequestMethod.POST)
    // public JsonResult getUserRejectedEditVersionByType(@PathVariable String type, @RequestBody FindDTO findDTO, HttpServletRequest request){
    //     HttpSession session = request.getSession();
    //     String email = session.getAttribute("email").toString();
    //     return versionService.getUserVersionByStatusAndByTypeAndByOperation(-1,type,findDTO,email,"edit");
    // }
    //
    // @LoginRequired
    // @ApiOperation(value = "根据条目类型得到用户审核（你是条目的创建者）的未审核的version [ /theme/getMessageData ]")
    // @ApiImplicitParams({
    //     @ApiImplicitParam(name="type",value="条目大类: Model/Data/Community/Theme",required=true)
    // })
    // @RequestMapping(value = "/versionList/review/uncheck/{type}",method = RequestMethod.POST)
    // public JsonResult getUserUncheckReviewVersionByType(@PathVariable String type, @RequestBody FindDTO findDTO, HttpServletRequest request){
    //     HttpSession session = request.getSession();
    //     String email = session.getAttribute("email").toString();
    //     return versionService.getUserVersionByStatusAndByTypeAndByOperation(0,type,findDTO,email,"review");
    // }
    //
    // @LoginRequired
    // @ApiOperation(value = "根据条目类型得到用户审核的已通过的version [ /theme/getMessageData ]")
    // @ApiImplicitParams({
    //     @ApiImplicitParam(name="type",value="条目大类: Model/Data/Community/Theme",required=true)
    // })
    // @RequestMapping(value = "/versionList/review/accepted/{type}",method = RequestMethod.POST)
    // public JsonResult getUserAcceptedReviewVersionByType(@PathVariable String type, @RequestBody FindDTO findDTO, HttpServletRequest request){
    //     HttpSession session = request.getSession();
    //     String email = session.getAttribute("email").toString();
    //     return versionService.getUserVersionByStatusAndByTypeAndByOperation(1,type,findDTO,email,"review");
    // }
    //
    // @LoginRequired
    // @ApiOperation(value = "根据条目类型得到用户审核的已拒绝的version [ /theme/getMessageData ]")
    // @ApiImplicitParams({
    //     @ApiImplicitParam(name="type",value="条目大类: Model/Data/Community/Theme",required=true)
    // })
    // @RequestMapping(value = "/versionList/review/rejected/{type}",method = RequestMethod.POST)
    // public JsonResult getUserRejectedReviewVersionByType(@PathVariable String type, @RequestBody FindDTO findDTO, HttpServletRequest request){
    //     HttpSession session = request.getSession();
    //     String email = session.getAttribute("email").toString();
    //     return versionService.getUserVersionByStatusAndByTypeAndByOperation(-1,type,findDTO,email,"review");
    // }


    // @LoginRequired
    @ApiOperation(value = "得到用户贡献的资源数量")
    @RequestMapping (value = "/resourceCount", method = RequestMethod.GET)
    public JsonResult getResourceCount(@RequestParam(value = "email", required=false) String requestEmail ,HttpServletRequest request){
        String email;
        if (requestEmail != null && !"".equals(requestEmail)){
            email = requestEmail;
        } else {
            HttpSession session = request.getSession();
            email = session.getAttribute("email").toString();
        }


        return ResultUtils.success(userService.countResource(email));
    }

    @LoginRequired
    @ApiOperation(value = "得到用户贡献的资源数量,同时更新该用户的数据库")
    @RequestMapping (value = "/resourceCountWithUpdate", method = RequestMethod.GET)
    public JsonResult getResourceCountWithUpdate(@RequestParam(value = "email", required=true) String requestEmail ,HttpServletRequest request){
        HttpSession session = request.getSession();
        String loginEmail = session.getAttribute("email").toString();

        return ResultUtils.success(userService.countResourceWithUpdate(requestEmail, loginEmail));
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

    @ApiOperation(value = "根据用户名得到email")
    @RequestMapping (value = "/email", method = RequestMethod.GET)
    public JsonResult getUserEmail(@PathParam("userName") String userName) throws UnsupportedEncodingException {
        userName = userName.split("\\?")[0];
        //处理中文乱码
        String name = URLDecoder.decode(userName, "UTF-8");
        return ResultUtils.success(userService.getUserEmail(name));
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
    @ApiOperation(value = "更新awardsHonors [ /updateAwdHonor ]")
    @RequestMapping(value="/update/awardsHonors",method = RequestMethod.POST)
    public JsonResult updateAwardsHonors(@RequestBody AwardandHonorDTO awardandHonorDTO, HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();

        User user = userService.updateAwardsHonors(awardandHonorDTO,email);
        return ResultUtils.success(user);

    }

    @LoginRequired
    @ApiOperation(value = "更新educationExperiences [ /updateEduExperience ]")
    @RequestMapping(value="/update/educationExperiences",method = RequestMethod.POST)
    public JsonResult updateEducationExperiences(@RequestBody EducationExperienceDTO educationExperienceDTO, HttpServletRequest request){

        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();

        User user = userService.updateEducationExperiences(educationExperienceDTO,email);
        return ResultUtils.success(user);
    }

    @LoginRequired
    @ApiOperation(value = "更新userLab [ /updateLab ]")
    @RequestMapping(value="/update/userLab",method = RequestMethod.POST)
    public JsonResult updateLab(@RequestBody UserLabDTO userLabDTO, HttpServletRequest request){

        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();

        User user = userService.updateLab(userLabDTO,email);
        return ResultUtils.success(user);
    }


    @LoginRequired
    @ApiOperation(value = "保存用户头像")
    @RequestMapping(value = "/update/saveUserIcon", method = RequestMethod.POST)
    public JsonResult saveUserIcon(@RequestParam(value = "img") String img, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return ResultUtils.success(userService.saveUserIcon(img, email));
    }

    @LoginRequired
    @ApiOperation(value = "得到用户的订阅列表 [ /user/getSubscribedList ]")
    @RequestMapping(value = "/subscribedList", method = RequestMethod.GET)
    public JsonResult getSubscribedList(HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return ResultUtils.success(userService.getSubscribedList(email));
    }


    @LoginRequired
    @RequestMapping(value = "/addFile", method = RequestMethod.POST)
    JsonResult addFile(
        @RequestBody UploadUserFileDTO uploadUserFileDTO
        , HttpServletRequest httpServletRequest) throws Exception {

        List<Map> fileArray = uploadUserFileDTO.getFiles();
        List<String> paths = uploadUserFileDTO.getPaths();

        HttpSession httpSession = httpServletRequest.getSession();
        String email = httpSession.getAttribute("email").toString();
        JSONObject result = userService.addFileToUserServer(paths, fileArray, email,"POST");
        if(result == null){
            return ResultUtils.error(-2,"error");
        }
//        System.out.println(result);
        return ResultUtils.success(result);
    }


    @LoginRequired
    @RequestMapping(value = "/addFolder", method = RequestMethod.POST)
    JsonResult addFolder(@RequestParam("paths[]") List<String> paths,
                         @RequestParam("name") String name,
                         HttpServletRequest httpServletRequest) throws Exception {

        HttpSession httpSession = httpServletRequest.getSession();
        String email = httpSession.getAttribute("email").toString();
        String result = userService.addFolderToUserServer(paths, name, email);

        if(result == null){
            return ResultUtils.error(-2,"error");
        }

        return ResultUtils.success(result);
    }


    @ApiOperation(value = "添加账户")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public JsonResult addUser(@RequestBody UserShuttleDTO user) throws Exception {
        return userService.addUserToUserServer(user);
    }


    @LoginRequired
    @ApiOperation(value = "修改密码")
    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public JsonResult changePassword(@RequestParam String oldPass,
                                     @RequestParam String newPass,
                                     HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        String result = userService.changePass(email, oldPass, newPass,session);
        if(result.equals("suc")) {
            userService.removeUserSession(request);
            return ResultUtils.success(result);
        }else if(result.equals("out")) {
            return ResultUtils.error(-1,"out");
        }else{
            return ResultUtils.error(-2,result);
        }

    }

    @LoginRequired
    @RequestMapping(value = "/changeSubscribedModelList", method = RequestMethod.GET)
    public ModelAndView changeSubscribedModelList(@RequestParam("id") String id, HttpServletRequest request){

        User user = userService.getById(id);
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(60*60);//设置session过期时间 为60分钟
        userService.setUserSession(request,user.getEmail(),user.getName());

        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("redirect:/user/userSpace#/account");
        return modelAndView;
    }



    @LoginRequired
    @RequestMapping(value = "/checkDataRestSpace", method = RequestMethod.GET)
    public JsonResult checkDataRestSpace(@RequestParam("fileSize") long fileSize, HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        JSONObject result = userService.checkDataRestSpace(email,fileSize);
        if(result!=null) {
            return ResultUtils.success(result);
        }else{
            return ResultUtils.error(-2,"error");
        }

    }


    @LoginRequired
    @RequestMapping(value = "/deleteFile", method = RequestMethod.POST)
    JsonResult deleteFile(@RequestParam("dataId") String dataId,
                          HttpServletRequest httpServletRequest) throws Exception {

        HttpSession httpSession = httpServletRequest.getSession();
        String email = httpSession.getAttribute("email").toString();
        JSONObject result = userService.deleteFileInUserServer(dataId, email);

        if(result == null){
            return ResultUtils.error(-1,"error");
        }else if(result.getString("data")!=null&&result.getString("data").equals("out")){
            return ResultUtils.error(-1,"no login");
        }

        return ResultUtils.success(result);
    }

    @RequestMapping(value = "/deleteSomeFiles", method = RequestMethod.POST)
    JsonResult deleteFiles(@RequestBody JSONObject deleteFiles,
                           HttpServletRequest httpServletRequest) throws Exception {

        JSONArray jsonArray = deleteFiles.getJSONArray("deleteTarget");
        List<UserServerResource> deleteFileList = jsonArray.toJavaList(UserServerResource.class);
        HttpSession httpSession = httpServletRequest.getSession();
        String email = httpSession.getAttribute("email").toString();
        JSONObject result = userService.deleteFiles(deleteFileList, email);

        if(result == null){
            return ResultUtils.error(-1,"error");
        }else if(result.getString("data")!=null&&result.getString("data").equals("out")){
            return ResultUtils.error(-1,"no login");
        }
        return ResultUtils.success(result.getJSONArray("resource"));
    }

    @LoginRequired
    @ApiOperation(value = "上传失败时，删除预先占用的空间")
    @RequestMapping(value = "/deleteUploadFlag", method = RequestMethod.GET)
    public JsonResult deleteUploadFlag(@RequestParam("uploadFlag") String uploadFlag, HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return ResultUtils.success(userService.deleteUploadFlag(email,uploadFlag));
    }


    @LoginRequired
    @RequestMapping(value = "/forkData", method = RequestMethod.POST)
    JsonResult forkData(@RequestParam("paths[]") List<String> paths,
                        @RequestParam("dataIds[]") List<String> dataIds,
                        @RequestParam("itemId") String dataItemId,
                        HttpServletRequest httpServletRequest) {

        HttpSession httpSession = httpServletRequest.getSession();
        String email = httpSession.getAttribute("email").toString();
        if (paths.get(0).equals("root")) {
            paths.remove(0);
        }
        String result = userService.forkData(paths, dataIds, dataItemId, email);

        return ResultUtils.success();

    }

    @LoginRequired
    @RequestMapping(value = "/getFolder", method = RequestMethod.GET)
    JsonResult getFolder(HttpServletRequest httpServletRequest) throws Exception {

        HttpSession httpSession = httpServletRequest.getSession();
        String email = httpSession.getAttribute("email").toString();
        JSONArray result = userService.getFolderFromUserServer(email);
        return ResultUtils.success(result);
    }

    @RequestMapping(value = "/getFolderAndFile", method = RequestMethod.GET)
    JsonResult getFolder7File(HttpServletRequest httpServletRequest) {

        HttpSession httpSession = httpServletRequest.getSession();
        String email = httpSession.getAttribute("email").toString();
        JSONArray result = userService.getFolder7File(email);

        return ResultUtils.success(result);
    }


    @LoginRequired
    @RequestMapping(value = "/getModelCounts", method = RequestMethod.GET)
    public JsonResult getModelCounts(HttpServletRequest request){
        JSONObject result = new JSONObject();
        HttpSession httpSession = request.getSession();
        String email = httpSession.getAttribute("email").toString();
        User user = userService.getByEmail(email);
        result.put("modelItem",user.getResourceCount().getModelItem());
        result.put("conceptualModel",user.getResourceCount().getConceptualModel());
        result.put("logicalModel",user.getResourceCount().getLogicalModel());
        result.put("computableModel",user.getResourceCount().getComputableModel());
        return ResultUtils.success(result);
    }


    @LoginRequired
    @RequestMapping(value = "/getLoginUser", method = RequestMethod.GET)
    public JsonResult getLoginUser(HttpServletRequest req) {
        HttpSession session = req.getSession();
        String email = session.getAttribute("email").toString();
        return ResultUtils.success(userService.getByEmail(email));
    }

    @RequestMapping(value = "/getUserInfoInUserPage", method = RequestMethod.GET)
    public JsonResult getUserInfoInUserPage(@RequestParam(value = "email") String email) {
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


    @LoginRequired
    @RequestMapping(value = "/getUserSimpleInfo", method = RequestMethod.GET)
    public JsonResult getUserSimpleInfo(HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        return ResultUtils.success(userService.getUserSimpleInfo(email));
    }


    @RequestMapping(value = "/listArticle",method = RequestMethod.GET)
    JsonResult listArticle(@RequestParam(value="page")int page, @RequestParam(value="email") String email){
        return ResultUtils.success(userService.listUserArticle(page,email));
    }


    @LoginRequired
    @RequestMapping(value = "/keywordsSearch", method = RequestMethod.GET)
    JsonResult searchFile(@RequestParam(value = "keyword") String keyword, HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        JSONObject result = userService.searchFileFromUserServer(keyword, email);
        if (result==null){
            return ResultUtils.error(-1, "err");
        }else{
            return ResultUtils.success(result);
        }
    }

    @LoginRequired
    @RequestMapping(value = "/setSubscribe", method = RequestMethod.POST)
    public JsonResult setSubscribe(@RequestParam("subscribe") Boolean subs, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        userService.setSubscribe(email, subs);
        return ResultUtils.success();
    }


    @LoginRequired
    @RequestMapping(value = "/setSubscribedList", method = RequestMethod.POST)
    public JsonResult setSubscribedList(@RequestBody List<UserSubscribeItem> list, HttpServletRequest request){
        HttpSession session = request.getSession();
        String email = session.getAttribute("email").toString();
        userService.setSubscribedList(email,list);
        return ResultUtils.success();
    }

    @LoginRequired
    @RequestMapping(value = "/getUserInfo", method = RequestMethod.GET)
    public JsonResult getUserInfo(HttpServletRequest req) throws Exception {
        HttpSession session = req.getSession();
        String email = session.getAttribute("email").toString();
        JSONObject result = userService.getUserInfo(email);
        return ResultUtils.success(result);
    }


    @RequestMapping(value = "/unsubscribe", method = RequestMethod.GET)
    public ModelAndView unsubscribe(@RequestParam("id") String id) {
        User user = userService.getById(id);
        userService.setSubscribe(user.getEmail(), false);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("notice/unsubscribe");
        return modelAndView;
    }

    @LoginRequired
    @ApiOperation(value = "更新introduction [ /updateIntroduction ]")
    @RequestMapping(value = "/update/introduction", method = RequestMethod.POST)
    JsonResult updateDescription(@RequestParam(value = "introduction")String introduction, HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession();
        String email = httpSession.getAttribute("email").toString();
        String result = userService.updateIntroduction(introduction, email);
        return ResultUtils.success(result);
    }

    @LoginRequired
    @ApiOperation(value = "更新researchInterest [ /updateResearchInterest ]")
    @RequestMapping(value = "/update/researchInterest", method = RequestMethod.POST)
    JsonResult updateResearchInterest(@RequestBody ResearchInterestDTO researchInterestDTO, HttpServletRequest httpServletRequest) {
        List<String> researchInterests = researchInterestDTO.getResearchInterests();
        HttpSession httpSession = httpServletRequest.getSession();
        String email = httpSession.getAttribute("email").toString();
        String result = userService.updateResearchInterest(researchInterests, email);

        return ResultUtils.success(result);
    }

    @LoginRequired
    @ApiOperation(value = "更新exLinks [ /updateExLinks ]")
    @RequestMapping(value = "/update/exLinks", method = RequestMethod.POST)
    JsonResult updateExLinks(@RequestBody List<String> exLinks, HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession();
        String email = httpSession.getAttribute("email").toString();
        String result = userService.updateExternalLinks(exLinks, email);

        return ResultUtils.success(result);
    }

    @LoginRequired
    @RequestMapping(value = "/updateFile", method = RequestMethod.POST)
    JsonResult updateFile(@RequestParam("dataName") String name,
                          @RequestParam("dataId") String dataId,

                          HttpServletRequest httpServletRequest) throws Exception {

        HttpSession httpSession = httpServletRequest.getSession();
        String email = httpSession.getAttribute("email").toString();
        String result = userService.updateFileToUserServer(dataId, name, email);

        if(result==null){
            return ResultUtils.error(-2,"err");
        }else if(result.equals("out")){
            return ResultUtils.error(-1,"no login");
        }

        return ResultUtils.success(result);
    }

    @LoginRequired
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)//判断登录状态上传文件到数据容器
    JSONObject uploadFile(@RequestParam("ogmsdata") MultipartFile[] files,
                          @RequestParam("name")String uploadName,
                          @RequestParam("userId")String userName,
                          @RequestParam("serverNode")String serverNode,
                          @RequestParam("origination")String origination,
                          HttpServletRequest request){

        MultiValueMap<String, Object> part = new LinkedMultiValueMap<>();

        for(int i=0;i<files.length;i++)
            part.add("datafile", files[i].getResource());
        part.add("name", uploadName);
        part.add("userId", userName);
        part.add("serverNode", serverNode);
        part.add("origination", origination);
        return MyHttpUtils.uploadDataToDataServer(dataContainerIpAndPort,part);

    }


    /** 下面的接口是UserServer用到的接口 */
    @RequestMapping(value = "/getOidByEmail", method = RequestMethod.GET)
    public JsonResult getOidByEmail(HttpServletRequest req, @RequestParam String email) {
        User user = userService.getByEmail(email);
        String userId = null;
        if(user != null){
            userId = user.getId();
        }
        return userId == null?ResultUtils.error(-1, "user is not exist."):ResultUtils.success(userId);
    }

}
