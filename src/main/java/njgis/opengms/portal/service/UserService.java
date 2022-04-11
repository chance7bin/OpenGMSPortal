package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.dao.*;
import njgis.opengms.portal.entity.doo.DataMeta;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.MyException;
import njgis.opengms.portal.entity.doo.base.PortalIdPlus;
import njgis.opengms.portal.entity.doo.base.PortalItem;
import njgis.opengms.portal.entity.doo.user.Conference;
import njgis.opengms.portal.entity.doo.user.Project;
import njgis.opengms.portal.entity.doo.user.*;
import njgis.opengms.portal.entity.dto.user.*;
import njgis.opengms.portal.entity.po.*;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.enums.ResultEnum;
import njgis.opengms.portal.enums.UserRoleEnum;
import njgis.opengms.portal.utils.MyHttpUtils;
import njgis.opengms.portal.utils.ResultUtils;
import njgis.opengms.portal.utils.Utils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.*;


/**
 * @Description 用户业务层
 * @Author kx
 * @Date 2021/7/5
 * @Version 1.0.0
 */
@Slf4j
@Service
public class UserService {

    @Value("${resourcePath}")
    private String resourcePath;

    @Value("${htmlLoadPath}")
    private String htmlLoadPath;

    @Value("${userServer}")
    private String userServer;

    @Value("${userServerCilent}")
    private String userServerCilent;

    @Value("${userServerCilentPWD}")
    private String userServerCilentPWD;

    @Value("${spring.mail.username}")
    //使用@Value注入application.properties中指定的用户名
    private String from;

    @Autowired
    GenericService genericService;

    @Autowired
    //用于发送文件
    private JavaMailSender mailSender;

    @Autowired
    UserDao userDao;

    @Autowired
    NoticeService noticeService;

    @Autowired
    TokenService tokenService;

    @Autowired
    FeedbackDao feedbackDao;

    @Autowired
    ComputableModelDao computableModelDao;

    @Autowired
    DataItemDao dataItemDao;

    @Autowired
    ArticleService articleService;

    @Autowired
    TaskDao taskDao;

    //可以可视化的数据模板
    @Value("#{'${visualTemplateIds}'.split(',')}")
    private String[] visualTemplateIds;


    /**
     * @Description 用户相关条目计数 加一+++++
     * @param email 用户邮箱
     * @param itemType 条目类型
     * @Return void
     * @Author kx
     * @Date 2021/7/7
     **/
    public void ItemCountPlusOne(String email, ItemTypeEnum itemType){
        User user = userDao.findFirstByEmail(email);
        UserResourceCount resourceCount = user.getResourceCount();
        int number = 1;
        resourceCount = changeItemCount(resourceCount, itemType, number);
        user.setResourceCount(resourceCount);
        userDao.save(user);
    }

    /**
     * @Description 用户相关条目计数 减一-----
     * @param email 用户邮箱
     * @param itemType 条目类型
     * @Return void
     * @Author kx
     * @Date 2021/7/7
     **/
    public void ItemCountMinusOne(String email, ItemTypeEnum itemType){
        User user = userDao.findFirstByEmail(email);
        UserResourceCount resourceCount = user.getResourceCount();
        int number = -1;
        resourceCount = changeItemCount(resourceCount, itemType, number);
        user.setResourceCount(resourceCount);
        userDao.save(user);
    }

    /**
     * @Description 改变对应类型的条目数量
     * @param resourceCount
     * @param itemType
     * @param number
     * @Return njgis.opengms.portal.entity.doo.user.UserResourceCount
     * @Author kx
     * @Date 2021/7/7
     **/
    public UserResourceCount changeItemCount(UserResourceCount resourceCount, ItemTypeEnum itemType, int number){
        switch (itemType){
            case ModelItem:
                resourceCount.setModelItem(resourceCount.getModelItem()+number);
                break;
            case ConceptualModel:
                resourceCount.setConceptualModel(resourceCount.getConceptualModel()+number);
                break;
            case LogicalModel:
                resourceCount.setLogicalModel(resourceCount.getLogicalModel()+number);
                break;
            case ComputableModel:
                resourceCount.setConceptualModel(resourceCount.getComputableModel()+number);
                break;
            case Concept:
                resourceCount.setConcept(resourceCount.getConcept()+number);
                break;
            case SpatialReference:
                resourceCount.setSpatialReference(resourceCount.getSpatialReference()+number);
                break;
            case Template:
                resourceCount.setTemplate(resourceCount.getTemplate()+number);
                break;
            case Unit:
                resourceCount.setUnit(resourceCount.getUnit()+number);
                break;
            case Theme:
                resourceCount.setTheme(resourceCount.getTheme()+number);
                break;
            case DataItem:
                resourceCount.setDataItem(resourceCount.getDataItem()+number);
                break;
            case DataHub:
                resourceCount.setDataHub(resourceCount.getDataHub()+number);
                break;
            case DataMethod:
                resourceCount.setDataMethod(resourceCount.getDataMethod()+number);
                break;
            // case Article:
            //     resourceCount.setArticle(resourceCount.getArticle()+number);
            //     break;
            // case Project:
            //     resourceCount.setProject(resourceCount.getProject()+number);
            //     break;
            // case Conference:
            //     resourceCount.setConference(resourceCount.getConference()+number);
            //     break;
        }
        return resourceCount;
    }

    /**
     * @Description 使用session记录用户登录状态
     * @param request
     * @param email
     * @param name
     * @Return void
     * @Author kx
     * @Date 2021/7/6
     **/
    public void setUserSession(HttpServletRequest request, String email, String name, String role){
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(120*60);//设置session过期时间 为120分钟
//            session.setAttribute("uid", result.getString("userName"));
        session.setAttribute("email", email);
        session.setAttribute("name", name);
        session.setAttribute("role", role);
    }

    public void setUserSession(HttpServletRequest request, String email, String name){
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(120*60);//设置session过期时间 为120分钟
//            session.setAttribute("uid", result.getString("userName"));
        session.setAttribute("email", email);
        session.setAttribute("name", name);
    }

    /**
     * @Description 清除session中记录的登录信息
     * @param request
     * @Return void
     * @Author kx
     * @Date 2021/7/6
     **/
    public void removeUserSession(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.removeAttribute("email");
        session.removeAttribute("name");
        session.removeAttribute("role");
    }

    /**
     * @Description 通过用户服务器进行登录
     * @param account email
     * @param password sha256加密后的密码
     * @param ip 用户登录ip
     * @Return com.alibaba.fastjson.JSONObject
     * @Author wzh
     * @Date 2021/7/6
     **/
    public JSONObject loginUserServer(String account, String password, String ip){
        JSONObject j_tokenInfo = tokenService.getTokenUsePwd(account, password);

        UserShuttleDTO userShuttleDTO = new UserShuttleDTO();
        if (j_tokenInfo == null){
            return null;
        }
        String access_token = (String)j_tokenInfo.get("access_token");

        //获取到用户服务器对象
        try{
            JSONObject userBaseJson = tokenService.logintoUserServer(access_token,ip).getJSONObject("data");
            userShuttleDTO = JSONObject.toJavaObject(userBaseJson, UserShuttleDTO.class);

            //更新该user的token
            User user = userDao.findFirstByEmail(account);
            if(user==null){
                User newUser = new User();
                String name = userShuttleDTO.getName();
                newUser.setId(userShuttleDTO.getUserId());
                newUser.setEmail(userShuttleDTO.getEmail());
                newUser.setAccessId(Utils.generateAccessId(name, userDao.findAllByAccessIdContains(name), true));
                userDao.insert(newUser);
            }
            user = userDao.findFirstByEmail(account);

            user.setName(userShuttleDTO.getName());
            user.setAvatar(userShuttleDTO.getAvatar());


            //TODO wzh 没看懂
            tokenService.refreshUserTokenInfo(j_tokenInfo,user);

            JSONObject ipUpdate = new JSONObject();
            ipUpdate.put("loginIp",ip);

            updatePartInfotoUserServer(userShuttleDTO.getEmail(),ipUpdate);

            JSONObject j_userShuttleDTO = (JSONObject) JSONObject.toJSON(userShuttleDTO);

            //我将返回值改成了email,name,role 2021.7.7
            JSONObject result = new JSONObject();
            result.put("email",j_userShuttleDTO.getString("email"));
            result.put("name",j_userShuttleDTO.getString("name"));
            // role暂时没有，先注释调
            // result.put("role",user.getUserRole().getRole());

            //更新该用户的资源数量
            updateAllResourceCount(j_userShuttleDTO.getString("email"));

            return j_userShuttleDTO;
        }catch (Exception e){
            log.error(e.getMessage());
            // System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * @Description 根据门户用户信息更新用户服务器用户
     * @param email
     * @param updateInfo
     * @Return int
     * @Author wzh
     * @Date 2021/7/6
     **/
    public int updatePartInfotoUserServer(String email,JSONObject updateInfo) throws ParseException, IOException, URISyntaxException, IllegalAccessException {

        try {
            String token = tokenService.checkToken(email);
            if(token.equals("out")){
                return -1;
            }

            RestTemplate restTemplate = new RestTemplate();
//            LinkedMultiValueMap<String, Object> valueMap = new LinkedMultiValueMap<>();
//            for (Map.Entry entry : jsonObject.entrySet()){
//                String filedName =  (String)entry.getKey();
//                valueMap.add(filedName, entry.getValue());
//            }
            String url = "http://" + userServer + "/AuthServer/user/add";
            HttpHeaders httpHeaders = new HttpHeaders();
            MediaType mediaType = MediaType.parseMediaType("application/json;charset=UTF-8");
            httpHeaders.setContentType(mediaType);
            httpHeaders.add("Authorization","Bearer " + token);
            httpHeaders.set("user-agent","portal_backend");

            HttpEntity<Object> httpEntity = new HttpEntity<>(updateInfo.toString(), httpHeaders);
            ResponseEntity<JSONObject> registerResult = restTemplate.exchange(url, HttpMethod.POST, httpEntity, JSONObject.class);

            User user = userDao.findFirstByEmail(email);
            updatePortaluser(registerResult.getBody().getJSONObject("data"),user);

            int resCode = (int) registerResult.getBody().get("code");

            return resCode;
            // Confirm
//            User user = JSONObject.toJavaObject(jsonObject, User.class);
//            Query query = new Query(Criteria.where("userId").is(user.getUserId()));
//            User user1 = mongoTemplate.findOne(query, User.class);
//            if(user1 != null) return ResultUtils.error(-3, "Fail: user already exists in the database.");

//            return ResultUtils.success(mongoTemplate.save(user));

        } catch (Exception ex) {
            return 0;
        }
    }

    /**
     * @Description 根据用户服务器信息更新门户用户
     * @param updateUserInfo
     * @param portalUser
     * @Return void
     * @Author wzh
     * @Date 2021/7/6
     **/
    public void updatePortaluser(JSONObject updateUserInfo,User portalUser) throws NoSuchFieldException, IllegalAccessException {

        try {
            UserShuttleDTO userShuttleDTO = updateUserInfo.toJavaObject(UserShuttleDTO.class);

            if(portalUser==null){
                portalUser = new User();
            }
            for (Field shuttleField : userShuttleDTO.getClass().getDeclaredFields()) {
                String keyName = shuttleField.getName();
                Field field = null;
                try{
                    field = portalUser.getClass().getDeclaredField(keyName);
                    shuttleField = userShuttleDTO.getClass().getDeclaredField(keyName);
                }catch (Exception e){

                }
                if(field != null){
                    field.setAccessible(true);
                    shuttleField.setAccessible(true);
                    if (field.get(portalUser)!=null&&!field.get(portalUser).equals(shuttleField.get(userShuttleDTO))) {
                        if(shuttleField.getName().equals("avatar")){//用户头像单独处理
                            String avatar = (String) shuttleField.get(userShuttleDTO);
                            avatar = "http://" + userServer + "/userServer" + avatar;
                            field.set(portalUser, avatar);
                        }else{
                            field.set(portalUser, shuttleField.get(userShuttleDTO));
                        }
                    }
                }
            }
            userDao.save(portalUser);
        }catch (Exception e){

        }
    }


    /**
     * @Description 加载用户基础信息（邮箱，昵称，头像）
     * @param email
     * @Return com.alibaba.fastjson.JSONObject
     * @Author kx
     * @Date 2021/7/6
     **/
    public JsonResult loadUser(String email) throws Exception {

        JSONObject userInfo = new JSONObject();

        if (email == null) {

            return ResultUtils.error("email is null");
        }

        User user = userDao.findFirstByEmail(email);

        //读取用户服务器上存储的用户基本信息
        JSONObject j_userBaseInfo = getUserBasicInfo(user.getEmail());

        if(j_userBaseInfo == null){
            return ResultUtils.error(-3, "can't get user info from user server");
        }else{
            userInfo.put("email", email);
            userInfo.put("accessId", j_userBaseInfo.getString("userId"));
            userInfo.put("name", j_userBaseInfo.getString("name"));
            userInfo.put("avatar", j_userBaseInfo.getString("avatar"));
        }

        return ResultUtils.success(userInfo);
    }

    /**
     * @Description 根据email从用户服务器获取用户信息
     * @param email
     * @Return com.alibaba.fastjson.JSONObject
     * @Author kx
     * @Date 2021/7/6
     **/
    public JSONObject getUserBasicInfo(String email) throws Exception {
        User user = userDao.findFirstByEmail(email);
        JSONObject jsonObject = new JSONObject();
        if(user!=null){
            String token = tokenService.checkToken(email);
            if(token.equals("out")){
                return null;
            }
            jsonObject = tokenService.getBasicInfo(token);
            JSONObject j_userInfo = new JSONObject();
            if(jsonObject!=null){

                j_userInfo = jsonObject.getJSONObject("data");
                String avatar = j_userInfo.getString("avatar");
                if(avatar!=null){
                    avatar = "http://" + userServer + avatar;
                }
                j_userInfo.put("avatar",avatar);
            }else{
                return null;
            }
            return j_userInfo;

        }else{
            return null;
        }

    }

    /**
     * @Description 从用户服务器获取用户基础信息 TODO wzh 基础信息包括哪些？
     * @param email
     * @Return com.alibaba.fastjson.JSONObject
     * @Author kx
     * @Date 2021/7/6
     **/
    public JSONObject getInfoFromUserServer(String email){
        JSONObject jsonObject = new JSONObject();

        try {
            RestTemplate restTemplate = new RestTemplate();
            String userInfoUrl = "http://" + userServer + "/user/" + email + "/" + userServerCilent + "/" + userServerCilentPWD;
            HttpHeaders headers = new HttpHeaders();
            MediaType mediaType = MediaType.parseMediaType("application/json;charset=UTF-8");
            headers.setContentType(mediaType);
            headers.set("user-agent", "portal_backend");
            HttpEntity httpEntity = new HttpEntity(headers);
            ResponseEntity<JSONObject> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, httpEntity, JSONObject.class);
            JSONObject userInfo = response.getBody().getJSONObject("data");

            String avatar = userInfo.getString("avatar");
            if(avatar!=null){
                avatar = "/userServer" + avatar;
            }
            userInfo.put("avatar",avatar);
            userInfo.put("msg","suc");
            return userInfo;
        }catch(Exception e){
            log.error(e.getMessage());
            // System.out.println(e.fillInStackTrace());
            jsonObject.put("msg","no user");
        }
        return jsonObject;
    }

    /**
     * @Description 从用户服务器和门户获取用户全部信息 TODO 是否要去掉密码？
     * @param email
     * @Return com.alibaba.fastjson.JSONObject
     * @Author kx
     * @Date 2021/7/6
     **/
    public JSONObject getFullUserInfo(String email) throws Exception {
        JSONObject commonInfo = getInfoFromUserServer(email);
        JSONObject j_result = new JSONObject();
        if (commonInfo.getString("msg").equals("suc")){
            j_result.putAll(commonInfo);

            User user = userDao.findFirstByEmail(email);

            List<String> exLinks = new ArrayList<>();
            exLinks.add(user.getHomepage());
            j_result.put("id",user.getId());
            j_result.put("userId", user.getAccessId());
            j_result.put("email", user.getEmail());
            j_result.put("phone", user.getPhone());
            j_result.put("lab", user.getLab());
            j_result.put("homepage", user.getHomepage());
            j_result.put("externalLinks", exLinks);
            j_result.put("eduExperiences", user.getEducationExperiences());
            j_result.put("awdHonors", user.getAwardsHonors());
            j_result.put("runTask", user.getRunTask());
            j_result.put("image", user.getAvatar().equals("") ? "" : htmlLoadPath + user.getAvatar());
            j_result.put("subscribe", user.getSubscribe());

        }else {
            j_result = commonInfo;
        }

        return j_result;
    }

    /**
     * @Description 从用户服务器获取用户文件资源信息
     * @param email
     * @Return com.alibaba.fastjson.JSONObject
     * @Author kx
     * @Date 2021/7/6
     **/
    public JSONObject getUserResource(String email) throws Exception {
        // JSONObject j_userInfo = getInfoFromUserServer(email);
        JSONObject j_userInfo = getOwnInfoFromUserServer(email);

        JSONObject j_result = new JSONObject();
        if (j_userInfo.getString("msg").equals("suc")){
            JSONArray resource = j_userInfo.getJSONArray("resource");
            JSONObject obj = new JSONObject();
            obj.put("uid", "0");
            obj.put("name", "All Folder");
            obj.put("children", resource);

            j_result.put("resource",obj);
            j_result.put("msg","suc");

        } else {
            j_result = j_userInfo;
        }

        return j_result;
    }

    public JSONObject getOwnInfoFromUserServer(String email){
        User user = userDao.findFirstByEmail(email);
        JSONObject jsonObject = new JSONObject();
        if(user!=null){
            String token = tokenService.checkToken(email);
            if(token.equals("out")){
                jsonObject.put("msg","out");
                return jsonObject;
            }
            jsonObject = tokenService.getUserFromResServer(token);
            JSONObject j_userInfo = new JSONObject();
            if(jsonObject!=null){

                j_userInfo = jsonObject.getJSONObject("data");
                String avatar = j_userInfo.getString("avatar");
                if(avatar!=null){
                    avatar = "/userServer" + avatar;
                }
                j_userInfo.put("avatar",avatar);
                jsonObject = null;
                j_userInfo.put("msg","suc");
            }else{
                j_userInfo.put("msg","err");
            }


            return j_userInfo;

        }else{
            jsonObject.put("msg","no user");
        }

        return jsonObject;
    }


    public JSONObject getItemUserInfoByEmail(String email) {
        User user = userDao.findFirstByEmail(email);
        JSONObject userInfo = getInfoFromUserServer(user.getEmail());
        JSONObject userJson = new JSONObject();
        userJson.put("name", userInfo.getString("name"));
        // userJson.put("id", user.getId());
        userJson.put("email", user.getEmail());
        userJson.put("accessId", user.getAccessId());
        // userJson.put("image", user.getAvatar().equals("") ? "" : htmlLoadPath + user.getAvatar());
        userJson.put("image", userInfo.getString("avatar"));
        return userJson;
    }

    public User getByEmail(String email) {
        try {
            return userDao.findFirstByEmail(email);
        } catch (Exception e) {
            log.error("有人乱查数据库！！该UID不存在User对象");
            // System.out.println("有人乱查数据库！！该UID不存在User对象");
            throw new MyException(ResultEnum.NO_OBJECT);
        }
    }

    public User getByAccessId(String accessId) {
        try {
            return userDao.findFirstByAccessId(accessId);
        } catch (Exception e) {
            log.error("有人乱查数据库！！该UID不存在User对象");
            // System.out.println("有人乱查数据库！！该UID不存在User对象");
            throw new MyException(ResultEnum.NO_OBJECT);
        }
    }

    public String getAvatarFromUserServer(String email){
        HttpHeaders headers = new HttpHeaders();
        headers.set("user-agent","portal_backend");
        //httpEntity = httpHeader + httpBody,当然也可以只有其中一部分
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        String resUserUri = "http://" + userServer + "/user/getAvatar/" + email;
        //Url, RequestType, RequestContent, ResponseDataType

        try{

            RestTemplate restTemplate = new RestTemplate();


            ResponseEntity<JSONObject> userJson = restTemplate.exchange(resUserUri, HttpMethod.GET, httpEntity, JSONObject.class);
            JSONObject result = userJson.getBody();
            int code = result.getInteger("code");
            if(code == -1){
                return "/static/img/icon/default.png";
            }else {
                // return  "https://" + userServer + result.getString("msg");
                return  "/userServer" + result.getString("msg");
            }

        }catch (Exception e){
            log.error("Exception: " + e);
            // System.out.println("Exception: " + e.toString());
            return "/static/img/icon/default.png";
        }
    }

    /**
     * 更新用户数据条目的数量，在插入成功条目之后调用
     * @param email
     * @param itemType UserResourceCount对应的属性
     * @param op 增加还是减少 add delete 
     * @return void 
     * @Author bin
     **/
    public void updateUserResourceCount(String email, ItemTypeEnum itemType, String op){
        User user = userDao.findFirstByEmail(email);
        UserResourceCount userResourceCount = user.getResourceCount();
        if (userResourceCount == null){
            userResourceCount = new UserResourceCount();
        }
        try {
            Class<? extends UserResourceCount> aClass = userResourceCount.getClass();
            Field field = aClass.getDeclaredField(itemType.getText());
            field.setAccessible(true);
            // 直接加减偶尔会出现问题，直接查数据库的表
            // int count = (int)field.get(userResourceCount);
            // if (op.equals("add")) {
            //     ++count;
            // } else {
            //     --count;
            // }

            JSONObject daoFactory = genericService.daoFactory(itemType);
            GenericItemDao itemDao = (GenericItemDao)daoFactory.get("itemDao");
            int count = itemDao.countByAuthor(email);

            field.set(userResourceCount,count);
            user.setResourceCount(userResourceCount);
            userDao.save(user);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public void updateUserResourceCount(String email, ItemTypeEnum itemType){

        updateUserResourceCount(email,itemType,null);

    }

    public void updateUserNoticeNum(String email){
        User user = userDao.findFirstByEmail(email);
        user.setNoticeNum(noticeService.countUserNoticeNum(email));
        userDao.save(user);
    }

    public void updateAllResourceCount(String email){
        User user = userDao.findFirstByEmail(email);
        UserResourceCount resourceCount = user.getResourceCount();
        Class cls = resourceCount.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            // System.out.println(field.getName() + ":" + field.get(resourceCount) );
            ItemTypeEnum itemType = ItemTypeEnum.getItemTypeByName(field.getName());
            updateUserResourceCount(email,itemType);
        }
    }


    /**
     * 得到contributor的信息
     * @param email
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult getContributorInfo(@PathVariable(value = "email") String email) {
        User user = userDao.findFirstByEmail(email);
        JSONObject contributorInfo = new JSONObject();
        if (user == null) {
            return ResultUtils.error("user is not exist!");
        }
        contributorInfo.put("name", user.getName());
        contributorInfo.put("accessId", user.getAccessId());

        return ResultUtils.success(contributorInfo);
    }

    /**
     * 更新任务信息
     * @param email
     * @param userTaskInfo
     * @return java.lang.String
     * @Author bin
     **/
    public String addTaskInfo(String email, UserTaskInfo userTaskInfo) {
        try {
            User user = userDao.findFirstByEmail(email);
            if (user != null) {
                List<UserTaskInfo> runTask = user.getRunTask();
                runTask.add(userTaskInfo);
                Date now = new Date();

                user.setRunTask(runTask);
                user.setUpdateTime(now);
                userDao.save(user);
                // System.out.println(userDao.save(user));
                return "add taskInfo suc";
            } else
                return "no user";

        } catch (Exception e) {
            return "fail";
        }


    }

    /**
     * 得到用户贡献资源数量
     * @param email
     * @return UserResourceCount
     * @Author kai
     **/
    public UserResourceCount countResource(String email){
        //先更新，再查
//        updateAllResourceCount(email);
        User user = userDao.findFirstByEmail(email);
        return user.getResourceCount();
    }


    /**
     * 设置用户权限
     * @param id
     * @param role
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult setUserRole(String id, UserRoleEnum role){
        try {
            User user = userDao.findFirstById(id);
            user.setUserRole(role);
            User updatedUser = userDao.save(user);
            return ResultUtils.success(updatedUser);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtils.error();
        }

    }

    /**
     * 得到门户管理员
     * @param
     * @return java.util.List<njgis.opengms.portal.entity.po.User>
     * @Author bin
     **/
    public List<User> getAdminUser(){
        return userDao.findAllByUserRole(UserRoleEnum.ROLE_ADMIN);
    }

    /**
     * 得到门户root用户
     * @param
     * @return java.util.List<njgis.opengms.portal.entity.po.User>
     * @Author bin
     **/
    public List<User> getRootUser(){
        return userDao.findAllByUserRole(UserRoleEnum.ROLE_ROOT);
    }


    //根据传入的字符串返回用户名
    public String getUserName(String author){
        if (author.contains("@")) {
            User user = userDao.findFirstByEmail(author);
            if (user != null)
                author = user.getName();
        }
        return author;
    }

    //根据传入的字符串返回用户email
    public String getUserEmail(String userName){
        if(userName!=null && userName.length()!=0){
            User user = userDao.findFirstByAccessId(userName);
            if(user != null){
                return user.getEmail();
            }
        }
        return null;
    }



    /**
     * @Description 通过用户服务器发送验证码
     * @param email
     * @Return java.lang.String
     * @Author kx
     * @Date 21/12/7
     **/
    public String sendResetByUserserver(String email) throws IOException, URISyntaxException {
        try{
            String url = "http://" + userServer + "/user/resetPwd/" + email;
            Map<String,String> headers = new HashMap<>();
            headers.put("user-agent","portal_backend");
            String result = MyHttpUtils.GET(url,"UTF-8",headers);

            JSONObject j_result = JSONObject.parseObject(result);
            int code = j_result.getInteger("code");

            if(code==0){
                return "suc";
            }else if(code==-1){
                return "no user";
            }else {
                return "err";
            }
        }catch (Exception e){
            return "err";
        }

    }

    /**
     * @Description 重置密码
     * @param email
     * @param resetCode 验证码
     * @param newPass
     * @Return java.lang.String
     * @Author kx
     * @Date 21/12/7
     **/
    public String resetPassword(String email, String resetCode, String newPass) throws IOException, URISyntaxException {

        try{
            String url = "http://" + userServer + "/user/resetPwd/" + email + "/" + resetCode + "/" + DigestUtils.sha256Hex(newPass) ;
            Map<String,String> headers = new HashMap<>();
            headers.put("user-agent","portal_backend");

            String result = MyHttpUtils.GET(url,"UTF-8",headers);

            JSONObject j_result = JSONObject.parseObject(result);
            int code = j_result.getInteger("code");

            if(code==0){
                return "suc";
            }else if(code==-1){
                return "no user";
            }else {
                return "err";
            }
        }catch (Exception e){
            return "err";
        }


    }

    public JSONObject getFileByPathFromUserServer(List<String> paths, String email) throws Exception {
        String token = tokenService.checkToken(email);
        if(token.equals("out")){
            return null;
        }else{
            JSONObject result = new JSONObject();

            if(paths.get(0).equals("0")){
                result = getUserResource(email);

            }

            try {
                String pathStr = StringUtils.join(paths.toArray(),",");

                String url = "http://" + userServer + "/auth/res/" + pathStr;

                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization","Bearer " + token);
                headers.set("user-agent","portal_backend");
                HttpEntity<JSONObject> httpEntity = new HttpEntity<>(headers);
                ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url,HttpMethod.GET, httpEntity, JSONObject.class);
                result = responseEntity.getBody();

                if(result.getInteger("code")!=0){
                    return null;
                }
                JSONObject obj = new JSONObject();
                obj.put("data", result.getJSONArray("data"));
                return obj;
            }catch (Exception e){
                return null;
            }

        }


    }

    public String sendFeedback(String content, String email) {
        Feedback feedback = new Feedback();
        feedback.setContent(content);
        feedback.setEmail(email);
        Date now = new Date();
        feedback.setTime(now);

        feedbackDao.save(feedback);

        return "success";
    }


    public String updateIntroduction(String introduction, String email) {
        try {
            User user = userDao.findFirstByEmail(email);
            if (user != null) {
                user.setIntroduction(introduction);
                userDao.save(user);
                return "success";
            } else
                return "no user";

        } catch (Exception e) {
            return "fail";
        }

    }

    public String updateOrganizations(List<String> organizations, String email) {
        try {
            User user = userDao.findFirstByEmail(email);
            if (user != null) {
                user.setOrganizations(organizations);
                userDao.save(user);
                return "success";
            } else
                return "no user";

        } catch (Exception e) {
            return "fail";
        }

    }

    public String updateExternalLinks(List<String> externalLinks, String email) {
        try {
            User user = userDao.findFirstByEmail(email);
            if (user != null) {
                user.setHomepage(externalLinks.get(0));
                userDao.save(user);
                return "success";
            } else
                return "no user";

        } catch (Exception e) {
            return "fail";
        }

    }

    public String updateResearchInterest(List<String> researchInterests, String email) {
        try {
            User user = userDao.findFirstByEmail(email);
            if (user != null) {
                user.setDomain(researchInterests);
//                System.out.println(user.getResearchInterests());
                userDao.save(user);
                return "success";
            } else
                return "no user";

        } catch (Exception e) {
            return "fail";
        }
    }

    public int updateUsertoServer(UserShuttleDTO userShuttleDTO) throws Exception {
        String token = tokenService.checkToken(userShuttleDTO.getEmail());
        if(token.equals("out")){
            return -1;
        }

        try {
            String url = "http://" + userServer + "/auth/update";
            HttpHeaders httpHeaders = new HttpHeaders();
            MediaType mediaType = MediaType.parseMediaType("application/json;charset=UTF-8");
            httpHeaders.setContentType(mediaType);
            httpHeaders.set("user-agent","portal_backend");
            httpHeaders.add("Authorization","Bearer " + token);
            RestTemplate restTemplate = new RestTemplate();

            HashMap<String,Object> params = new HashMap<>();
            putValueToMap(params,userShuttleDTO);

            HttpEntity<Object> httpEntity = new HttpEntity<>(params, httpHeaders);
            ResponseEntity<JSONObject> result = restTemplate.exchange(url, HttpMethod.POST, httpEntity, JSONObject.class);

            if(result.getBody().getInteger("code")==-2){
                return -2;
            }

            User user = userDao.findFirstByEmail(userShuttleDTO.getEmail());
            updatePortalUser(result.getBody().getJSONObject("data"),user);

            return 1;
        }catch (Exception e){
            return -2;
        }

    }

    public void putValueToMap(Map<String,Object> map,Object obj) throws IllegalAccessException {
        Field[] fields = obj.getClass().getDeclaredFields();
        if(map instanceof LinkedMultiValueMap){
            map = (LinkedMultiValueMap)map;
        }
        for(int i=0;i<fields.length;i++){
            String name = fields[i].getName();
            //设置对象的访问权限，保证对private的属性的访问
            fields[i].setAccessible(true);
            if(fields[i].get(obj)!=null){
                Object ele = fields[i].get(obj);
                map.put(name,ele);
            }

        }
    }

    public void updatePortalUser(JSONObject updateUserInfo,User portalUser) throws NoSuchFieldException, IllegalAccessException {

        try {
            UserShuttleDTO userShuttleDTO = updateUserInfo.toJavaObject(UserShuttleDTO.class);

            if(portalUser==null){
                portalUser = new User();
            }
            for (Field shuttleField : userShuttleDTO.getClass().getDeclaredFields()) {
                String keyName = shuttleField.getName();
                if(keyName.equals("userId")){
                    continue;
                }
                Field field = null;
                try{
                    field = portalUser.getClass().getDeclaredField(keyName);
                    shuttleField = userShuttleDTO.getClass().getDeclaredField(keyName);
                }catch (Exception e){

                }
                if(field != null){
                    field.setAccessible(true);
                    shuttleField.setAccessible(true);
                    if (field.get(portalUser)!=null&&!field.get(portalUser).equals(shuttleField.get(userShuttleDTO))) {
                        if(shuttleField.getName().equals("avatar")){//用户头像单独处理
                            String avatar = (String) shuttleField.get(userShuttleDTO);
                            avatar = "/userServer" + avatar;
                            field.set(portalUser, avatar);
                        }else{
                            field.set(portalUser, shuttleField.get(userShuttleDTO));
                        }

                    }


                }


            }
            userDao.save(portalUser);
        }catch (Exception e){

        }

    }

    public User updateArticles(ArticlesDTO articlesDTO, String email){

        User user = userDao.findFirstByEmail(email);
        user.setArticles(articlesDTO.getIdList());
        return userDao.save(user);
    }


    public User updateProjects(ProjectDTO projectDTO, String email) {
        User user = userDao.findFirstByEmail(email);
        List<Project> projects = projectDTO.getProjects();
        List<njgis.opengms.portal.entity.po.Project> projects1 = new ArrayList<>();
        for (Project project : projects) {
            njgis.opengms.portal.entity.po.Project project1 = new njgis.opengms.portal.entity.po.Project();
            BeanUtils.copyProperties(project,project1);
            projects1.add(project1);
        }
        user.setProjects(projects1);
        return userDao.save(user);
    }

    public User updateConferences(ConferenceDTO conferenceDTO, String email) {
        User user = userDao.findFirstByEmail(email);
        List<Conference> conferences = conferenceDTO.getConferences();
        List<njgis.opengms.portal.entity.po.Conference> conferences1 = new ArrayList<>();
        for (Conference conference : conferences) {
            njgis.opengms.portal.entity.po.Conference conference1 = new njgis.opengms.portal.entity.po.Conference();
            BeanUtils.copyProperties(conference,conference1);
            conferences1.add(conference1);
        }
        user.setConferences(conferences1);
        return userDao.save(user);

    }

    public User updateAcademicServices(AcademicServiceDTO academicServiceDTO, String email) {
        User user = userDao.findFirstByEmail(email);
        user.setAcademicServices(academicServiceDTO.getAcademicServices());
        return userDao.save(user);
    }

    public User updateAwardsHonors(AwardandHonorDTO awardandHonorDTO, String email) {

        User user = userDao.findFirstByEmail(email);
        user.setAwardsHonors(awardandHonorDTO.getAwardandHonors());
        return userDao.save(user);
    }

    public User updateEducationExperiences(EducationExperienceDTO educationExperienceDTO, String email) {
        User user = userDao.findFirstByEmail(email);
        user.setEducationExperiences(educationExperienceDTO.getEducationExperiences());
        return userDao.save(user);
    }

    public User updateLab(UserLabDTO userLabDTO, String email) {
        User user = userDao.findFirstByEmail(email);
        user.setLab(userLabDTO.getUserLabs());
        return userDao.save(user);
    }

    public User updateUserInfo(UserInfoUpdateDTO userInfoUpdateDTO, String email) {

        //获取用户服务器的信息
        JSONObject commonInfo = getInfoFromUserServer(email);
        UserShuttleDTO userShuttleDTO = JSONObject.toJavaObject(commonInfo, UserShuttleDTO.class);
        User user = userDao.findFirstByEmail(email);

        //更新用户服务器的信息
        userShuttleDTO.setIntroduction(userInfoUpdateDTO.getIntroduction());
        userShuttleDTO.setOrganizations((ArrayList<String>) userInfoUpdateDTO.getOrganizations());
        try {
            updateUsertoServer(userShuttleDTO);
        }catch (Exception e){
           return null;
        }

        user.setHomepage(userInfoUpdateDTO.getExternalLinks().get(0));
        user.setDomain(userInfoUpdateDTO.getResearchInterests());
        user.setIntroduction(userInfoUpdateDTO.getIntroduction());
        user.setOrganizations(userInfoUpdateDTO.getOrganizations());
        userDao.save(user);

        return user;



    }


    public String saveUserIcon(String img, String email) {
        try {
            User user = userDao.findFirstByEmail(email);
            if (user != null) {
                String uploadImage = img;
                String path = "/user/";
                if (!uploadImage.contains("/user/")) {
                    //删除旧图片
                    File file = new File(resourcePath + user.getAvatar());
                    if (file.exists() && file.isFile())
                        file.delete();
                    //添加新图片
                    path = "/user/" + UUID.randomUUID().toString() + ".jpg";
                    String imgStr = uploadImage.split(",")[1];
                    Utils.base64StrToImage(imgStr, resourcePath + path);
                    user.setAvatar(path);

                }
                userDao.save(user);
                return path;

            } else
                return "no user";

        } catch (Exception e) {
            return "fail";
        }
    }


    public JSONArray getSubscribedList(String email) {
        User user = userDao.findFirstByEmail(email);
        List<UserSubscribeItem> subscribeItemList = user.getSubscribeItemList();
        JSONArray array = new JSONArray();
        for (int i = 0; i < subscribeItemList.size(); i++) {
            ComputableModel computableModel = computableModelDao.findFirstById(subscribeItemList.get(i).getOid());

            JSONObject subscribe = new JSONObject();
            subscribe.put("name", computableModel.getName());
            subscribe.put("type", computableModel.getContentType());
            subscribe.put("oid", computableModel.getId());

            array.add(subscribe);
        }

        return array;

    }


    // public void commentNumMinus(String username, int comment_num) {
    //     User user = userDao.findFirstByEmail(username);
    //     int count = user.getMessageNum();
    //     count = count - comment_num;
    //     user.setMessageNum(count);
    //     userDao.save(user);
    // }


    /**
     * 发送普通文本邮件
     * @param from 发件人
     * @param to 收件人
     * @param subject 主题
     * @param content 内容
     */
    public void sendSimpleMail(String from, String to, String subject, String content) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);//收信人
        message.setSubject(subject);//主题
        message.setText(content);//内容
        message.setFrom(from);//发信人
        mailSender.send(message);
    }

    public void sendSimpleMail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);//收信人
        message.setSubject(subject);//主题
        message.setText(content);//内容
        message.setFrom(from);//发信人
        mailSender.send(message);
    }

    public void sendHtmlMail(String to, String subject, String content) {

        log.info("发送HTML邮件：{},{},{}", to, subject, content);
        //使用MimeMessage，MIME协议
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper;
        //MimeMessageHelper帮助我们设置更丰富的内容
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);//true代表支持html
            mailSender.send(message);
            log.info("发送HTML邮件成功");
        } catch (MessagingException e) {
            log.error("发送HTML邮件失败：" +  e);
        }
    }

    /**
     * 条目被接受发送邮件
     * @param to 发邮件的对象email
     * @param item 修改的条目内容
     * @return void
     * @Author bin
     **/
    public void sendAcceptMail(String to, PortalItem item) {

        String url = "https://geomodeling.njnu.edu.cn/";

        User user = getByEmail(to);

        String content = "" +
            "<html>" +
                "<body>" +
                    "<p>Dear " + user.getName() + ":</p>" +
                    "<p>Congratulations on your modifications \"" + item.getName() + "\" have been accepted.</p>" +
                    "<p><a href=" + url + " target=\"_blank\">Click here</a> to see the latest version of the entry.</p>" +
                    "<p>Regards,</p>" +
                    "<p>OpenGMS</p>" +
                "</body>" +
            "</html>";

        sendHtmlMail(to, "Modifications are accepted", content);

    }


    public void sendRejectMail(String to, PortalItem item) {

        String url = "https://geomodeling.njnu.edu.cn/";

        User user = getByEmail(to);

        String content = "" +
            "<html>" +
                "<body>" +
                    "<p>Dear " + user.getName() + ":</p>" +
                    "<p>Unfortunately, your edits \"" + item.getName() + "\" did not pass the review.</p>" +
                    "<p>You can go to the <a href=" + url + " target=\"_blank\">entry page</a> to make them again.</p>" +
                    "<p>Regards,</p>" +
                    "<p>OpenGMS</p>" +
                "</body>" +
            "</html>";

        sendHtmlMail(to, "Modifications are rejected", content);

    }


    public JSONObject addFileToUserServer(List<String> paths, List<Map> files, String email, String method) throws Exception {
        String token = tokenService.checkToken(email);
        if(token.equals("out")){
            return null;
        }else{
            JSONObject result = new JSONObject();
            JSONArray idList = new JSONArray();
            JSONArray errList = new JSONArray();
            int sucCount = 0;
            int errCount = 0;
            List<String> pathsCopy = new ArrayList<>();
            for (int i = 0; i < files.size(); i++) {

                String fileName = files.get(i).get("file_name").toString();
                String address = "/data/" + files.get(i).get("source_store_id").toString();
                String[] a = fileName.split("\\.");
                String name = files.get(i).get("name").toString();
                String suffix = files.get(i).get("suffix").toString();
                String uid = UUID.randomUUID().toString();
                Boolean folder = false;
                String type = "data";
                String privacy = "private";
                long fileSize = Long.parseLong(String.valueOf(files.get(i).get("file_size"))) ;
                Date uploadTime = new Date();

                JSONObject j_resourceInfo = new JSONObject();
                j_resourceInfo.put("address",address);
                j_resourceInfo.put("name",name);
                j_resourceInfo.put("suffix",suffix);
                j_resourceInfo.put("uid",uid);
                j_resourceInfo.put("folder",folder);
                j_resourceInfo.put("type",type);
                j_resourceInfo.put("privacy",privacy);
                j_resourceInfo.put("fileSize",fileSize);
                j_resourceInfo.put("uploadTime",uploadTime);
                j_resourceInfo.put("description","");
                j_resourceInfo.put("md5","");
                j_resourceInfo.put("userUpload",true);

                String templateId = "";
                if(files.get(i).get("template")!=null){
                    templateId = files.get(i).get("template").toString();
                }

                String pathStr = StringUtils.join(paths.toArray(),",");

                String url = null;
                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization","Bearer " + token);
                headers.set("user-agent","portal_backend");
                HttpEntity<JSONObject> httpEntity = new HttpEntity<>(j_resourceInfo,headers);

                JSONObject response = new JSONObject();
                try {
                    if(method.toLowerCase().equals("post")){
                        url = "http://" + userServer + "/auth/res/" + pathStr;
                        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, JSONObject.class);
                        response = responseEntity.getBody();
                    }else{
                        url = "http://" + userServer + "/auth/res/" ;
                        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url,HttpMethod.PUT, httpEntity, JSONObject.class);
                        response = responseEntity.getBody();
                    }
                    if(response.getInteger("code")!=0){
                        return null;
                    }
                    sucCount++;
                    idList.add(j_resourceInfo.getString("uid"));
                }catch (Exception e){
                    errCount++;
                    errList.add(j_resourceInfo);
                }

            }

            result.put("sucCount",sucCount);
            result.put("sucList",idList);
            result.put("errCount",errCount);
            result.put("errList",errList);

            return result;
        }

    }

    public String addFolderToUserServer(List<String> paths, String name, String email) throws Exception {
        String token = tokenService.checkToken(email);
        if(token.equals("out")){
            return null;
        }else{
            List<String> pathsCopy = new ArrayList<>();

            String uid = UUID.randomUUID().toString();
            Boolean folder = true;
            String type = "data";
            String privacy = "public";

            Date uploadTime = new Date();

            JSONObject j_resourceInfo = new JSONObject();
            j_resourceInfo.put("name", name);
            j_resourceInfo.put("uid", uid);
            j_resourceInfo.put("folder", folder);
            j_resourceInfo.put("type", type);
            j_resourceInfo.put("privacy", privacy);
            j_resourceInfo.put("uploadTime", uploadTime);
            j_resourceInfo.put("description", "");
            j_resourceInfo.put("md5", "");
            j_resourceInfo.put("userUpload", "");

            try{
                String pathStr = StringUtils.join(paths.toArray(), ",");

                String url = "http://" + userServer + "/auth/res/" + pathStr;
                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", "Bearer " + token);
                headers.set("user-agent","portal_backend");
                HttpEntity<JSONObject> httpEntity = new HttpEntity<>(j_resourceInfo, headers);

                JSONObject result = new JSONObject();
                ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, JSONObject.class);
                result = responseEntity.getBody();

                if (result.getInteger("code") != 0) {
                    return null;
                }


                return uid;
            }catch (Exception e){
                return null;
            }

        }

    }

    public JsonResult addUserToUserServer(UserShuttleDTO userShuttleDTO) {
        try {
            String email = userShuttleDTO.getEmail();
            User existedUser = userDao.findFirstByEmail(email);
//            if(existedUser!=null){
//                return ResultUtils.error(-2, "Fail: user already exists in the database.");
//            }

            try{
                RestTemplate restTemplate = new RestTemplate();

                String url = "http://" + userServer + "/user";
                HttpHeaders httpHeaders = new HttpHeaders();
                MediaType mediaType = MediaType.parseMediaType("application/json;charset=UTF-8");
                httpHeaders.setContentType(mediaType);
                httpHeaders.set("user-agent","portal_backend");
                String pwd = userShuttleDTO.getPassword();
                userShuttleDTO.setPassword(DigestUtils.sha256Hex(pwd));

                String j_userShuttleDTO = JSON.toJSONString(userShuttleDTO);

                HttpEntity<Object> httpEntity = new HttpEntity<>(j_userShuttleDTO, httpHeaders);
                ResponseEntity<JSONObject> registerResult = restTemplate.exchange(url, HttpMethod.POST, httpEntity, JSONObject.class);
                int resCode = (int) registerResult.getBody().get("code");
                JSONObject data = registerResult.getBody().getJSONObject("data");
                if (resCode == 0) {
                    User user = new User();
                    user.setId(data.getString("userId"));
                    user.setEmail(userShuttleDTO.getEmail());
                    user.setName(userShuttleDTO.getName());
                    user.setAccessId(generateAccessId(userShuttleDTO.getName().trim()));
                    userDao.insert(user);
                    return ResultUtils.success(user.getId());
                } else if (resCode == -3) {
                    return ResultUtils.error(-2, "Fail: user already exists in the database.");
                } else {
                    return ResultUtils.error(-1, (String) registerResult.getBody().get("msg"));
                }
            }catch (Exception e){
                return ResultUtils.error(-1, "http error");
            }



        } catch (Exception ex) {
            return ResultUtils.error(-1, "Fail: Exception");
        }
    }

    public String generateAccessId(String name){
        name = name.trim().replaceAll(" ","_");
        //查询userid重名的用户
        List<PortalIdPlus> list = userDao.findAllByAccessIdContains(name);
        //若没有重名，则直接使用
        if(list.size()==0) {
            return name;
        }else { //有重名则判断是否是真正重名，并计算排序序号
            List<Integer> orders = new ArrayList<>();
            for (PortalIdPlus u : list) {
                //userid前部是否一致
                if (u.getAccessId().startsWith(name)) {
                    String left = u.getAccessId().replace(name, "");
                    if (left.equals("")) {
                        orders.add(1);
                    }else{
                        String[] leftNameSplit = left.split("_");
                        if(leftNameSplit.length==2 && leftNameSplit[0].equals("")){
                            try {
                                int num = Integer.parseInt(leftNameSplit[1]);
                                orders.add(num);
                            }catch (Exception e){
                                continue;
                            }
                        }
                    }
                }
            }
            if(orders.size()==0) { // 不是真正重名
                return name;
            }else {
                orders.sort(Comparator.comparingInt(Integer::intValue).reversed());
                return name + "_" + (orders.get(0)+1);
            }
        }
    }

    public String changePass(String email, String oldPass, String newPass,HttpSession session) throws Exception {

        oldPass = DigestUtils.sha256Hex(oldPass);
        newPass = DigestUtils.sha256Hex(newPass);

        User user = userDao.findFirstByEmail(email);
        String token = tokenService.checkToken(user.getEmail());

        if(token.equals("out")){
            return "out";
        }

        try{
            String url = "http://" + userServer + "/auth/newPwd/" + oldPass + "/" + newPass;
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization","Bearer " + token);
            headers.set("user-agent","portal_backend");
            HttpEntity<JSONObject> httpEntity = new HttpEntity<>(headers);
            ResponseEntity<JSONObject> result = restTemplate.exchange(url,HttpMethod.GET, httpEntity, JSONObject.class);

            if(result.getBody().getInteger("code")==0){
                return "suc";
            }else{
                String msg = result.getBody().getString("msg");
                if(msg.equals("Old password is not correct")){
                    return "wrong oldpass";
                }

                return "fail";
            }
        }catch (Exception e){
            return null;
        }



    }

    public User getById(String id) {
        try {
            return userDao.findFirstById(id);
        } catch (Exception e) {
            System.out.println("有人乱查数据库！！该OID不存在User对象");
            throw new MyException(ResultEnum.NO_OBJECT);
        }
    }

    //检查用户是否有足够的容量上传数据
    public JSONObject checkDataRestSpace(String email, long fileSize) {

        try{
            String token = tokenService.checkToken(email);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer "+ token);
            headers.set("user-agent","portal_backend");
            //httpEntity = httpHeader + httpBody,当然也可以只有其中一部分
            HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
            String restSpaceUri = "http://" + userServer + "/auth/res/upload/"+fileSize;

            RestTemplate restTemplate = new RestTemplate();
            JSONObject resultJson = restTemplate.exchange(restSpaceUri, HttpMethod.GET, httpEntity, JSONObject.class).getBody();
            int code = resultJson.getInteger("code");
            if(code==0) {
                return resultJson.getJSONObject("data");
            }else if(code==-2){
                JSONObject obj = new JSONObject();
                obj.put("canUpload", false);
                obj.put("restCapacity", resultJson.getString("msg"));
                return obj;
            }else{
                return null;
            }
        }catch (Exception e){
            System.out.println("Exception: " + e.toString());
            return null;
        }
    }

    public JSONObject deleteFileInUserServer(String id, String email) throws Exception {
        String token = tokenService.checkToken(email);
        if(token.equals("out")){
            JSONObject result = new JSONObject();
            result.put("data","out");
            return result;
        }else{
            try {
                String url = "http://" + userServer + "/auth/res/" + id;

                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", "Bearer " + token);
                headers.set("user-agent","portal_backend");
                HttpEntity<JSONObject> httpEntity = new HttpEntity<>(headers);
                ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, JSONObject.class);
                JSONObject result = responseEntity.getBody();

                if (result.getInteger("code") != 0) {
                    return null;
                }
                return result.getJSONObject("data");
            }catch (Exception e){
                return null;
            }
        }




    }

    public JSONObject deleteFiles(List<UserServerResource> deletes, String email) throws Exception {
//        List<String> result = new ArrayList<>();
//        for (UserServerResource file : deletes) {
//            String id = file.getUid();
//            result.add(deleteFile(id, userName));
//        }
//        return result;
        String token = tokenService.checkToken(email);
        if(token.equals("out")){
            JSONObject result = new JSONObject();
            result.put("data","out");
            return result;
        }else{
            try {
                String ids = deletes.get(0).getUid();

                for(int i=1;i<deletes.size();i++){
                    ids += ",";
                    ids += deletes.get(i).getUid();
                }

                String url = "http://" + userServer + "/auth/res/batch/" + ids;

                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", "Bearer " + token);
                headers.set("user-agent","portal_backend");

                HttpEntity<JSONObject> httpEntity = new HttpEntity<>(headers);
                ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, JSONObject.class);
                JSONObject result = responseEntity.getBody();

                if (result.getInteger("code") != 0) {
                    return null;
                }
                return result.getJSONObject("data");
            }catch (Exception e){
                return null;
            }
        }
    }

    //上传失败时，删除预先占用的空间
    public JSONObject deleteUploadFlag(String email, String uploadFlag){

        try{
            String token = tokenService.checkToken(email);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer "+ token);
            headers.set("user-agent","portal_backend");
            //httpEntity = httpHeader + httpBody,当然也可以只有其中一部分
            HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
            String capacityUri = "http://" + userServer + "/auth/res/uploadFlag/"+uploadFlag;

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<JSONObject> userJson = restTemplate.exchange(capacityUri, HttpMethod.GET, httpEntity, JSONObject.class);
            return userJson.getBody().getJSONObject("data");
        }catch (Exception e){
            System.out.println("Exception: " + e.toString());
            return null;
        }
    }

    public String forkData(List<String> paths, List<String> dataIds, String itemId, String email) {

        User user = userDao.findFirstByEmail(email);

        DataItem dataItem = dataItemDao.findFirstById(itemId);

        user.setFileContainer(setData(paths, user.getFileContainer(), dataIds, dataItem, "0"));
        userDao.save(user);

        return "suc";
    }

    private List<FileMeta> setData(List<String> paths, List<FileMeta> fileMetaList, List<String> dataIds, DataItem dataItem, String father) {

        if (paths.size() == 0) {
            List<DataMeta> dataList = dataItem.getDataList();
            for (String dataId : dataIds) {
                for (DataMeta dataMeta : dataList) {
                    String url = dataMeta.getUrl();
                    String param = url.split("[?]")[1];
                    String id = param.split("=")[1];
                    if (dataId.equals(url)) {
                        boolean exist = false;
                        for (FileMeta fm : fileMetaList) {
                            if (fm.getId().equals(id)) {
                                exist = true;
                                break;
                            }
                        }
                        if (!exist) {
                            Date date = new Date();
                            FileMeta fileMeta = new FileMeta(false, false, id, father, dataMeta.getName(), dataMeta.getSuffix(), dataMeta.getUrl(), "", null,date);
                            fileMetaList.add(fileMeta);
                        }
                        break;
                    }

                }
            }
        } else {
            String id = paths.remove(paths.size() - 1);
            for (FileMeta fileMeta : fileMetaList) {
                if (fileMeta.getId().equals(id)) {
                    fileMeta.setContent(setData(paths, fileMeta.getContent(), dataIds, dataItem, id));
                }
            }
        }
        return fileMetaList;
    }


    //从用户服务器获取用户已用数据容量和总容量
    public JSONObject getCapacity(String email) {
        String token = tokenService.checkToken(email);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+ token);
        headers.set("user-agent","portal_backend");
        //httpEntity = httpHeader + httpBody,当然也可以只有其中一部分
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        String capacityUri = "http://" + userServer + "/auth/res/capacity";

        try{
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<JSONObject> userJson = restTemplate.exchange(capacityUri, HttpMethod.GET, httpEntity, JSONObject.class);
            return userJson.getBody().getJSONObject("data");
        }catch (Exception e){
            System.out.println("Exception: " + e.toString());
            return null;
        }
    }

    public JSONArray getFolderFromUserServer(String email) throws Exception {

        String token = tokenService.checkToken(email);
        if(token.equals("out")){
            return null;
        }else {

            try{
                String url = "http://" + userServer + "/auth/res/folder";

                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", "Bearer " + token);
                headers.set("user-agent","portal_backend");
                HttpEntity<JSONObject> httpEntity = new HttpEntity<>(headers);
                ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, JSONObject.class);
                JSONObject result = responseEntity.getBody();

                if(result.getInteger("code")!=0){
                    return null;
                }
                JSONArray content = new JSONArray();
                JSONObject obj = new JSONObject();
                obj.put("uid", "0");
                obj.put("name", "All Folder");
                obj.put("children", result.getJSONArray("data"));

                content.add(obj);

                return content;
            }catch (Exception e){
                return null;
            }

        }
    }

    public JSONArray getFolder7File(String email) {
        User user = userDao.findFirstByEmail(email);
        List<FileMeta> fileMetaList = user.getFileContainer();

        if (fileMetaList == null || fileMetaList.size() == 0) {
            Date date = new Date();
            FileMeta fileMeta = new FileMeta(true, false, UUID.randomUUID().toString(), "0", "My Data", "", "", null, new ArrayList<>(),date);
            fileMetaList = new ArrayList<>();
            fileMetaList.add(fileMeta);
            user.setFileContainer(fileMetaList);
            userDao.save(user);
        }


        JSONArray content = new JSONArray();
        JSONObject obj = new JSONObject();
        obj.put("id", "0");
        obj.put("label", "All Folder");
        obj.put("children", gAllFile(fileMetaList));

        content.add(obj);

        return content;

    }

    private JSONArray gAllFile(List<FileMeta> fileMetaList) {


        JSONArray parent = new JSONArray();

        if (fileMetaList != null && fileMetaList.size() != 0) {
            for (int i = 0; i < fileMetaList.size(); i++) {
                FileMeta fileMeta = fileMetaList.get(i);
                if (fileMeta.getId() != null) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", fileMeta.getId());
                    jsonObject.put("label", fileMeta.getName());
                    jsonObject.put("folder", fileMeta.getIsFolder());
                    jsonObject.put("upload", fileMeta.getIsUserUpload());
                    jsonObject.put("father", fileMeta.getFather());
                    jsonObject.put("suffix", fileMeta.getSuffix());
                    jsonObject.put("url", fileMeta.getUrl());
                    for (String id : visualTemplateIds) {
                        if (fileMeta.getTemplateId()!=null&&id.equals(fileMeta.getTemplateId())) {
                            jsonObject.put("visual", true);
                            break;
                        }
                    }
                    if (!jsonObject.containsKey("visual")) {
                        jsonObject.put("visual", false);
                    }
                    jsonObject.put("children", gAllFile(fileMeta.getContent()));

                    parent.add(jsonObject);
                }
            }
        }
        return parent;
    }


    public JSONObject getUserSimpleInfo(String email) {
        JSONObject result = new JSONObject();
        User user = userDao.findFirstByEmail(email);
        JSONObject jsonObject = getInfoFromUserServer(email);
        result.put("oid", user.getId());
        result.put("name", jsonObject.getString("name"));
        result.put("org", jsonObject.getJSONArray("organizations").get(0));
        result.put("email", user.getEmail());
        result.put("homepage", jsonObject.getString("homepage"));

        return result;

    }


    public JSONObject listUserArticle(int page, String email) {
        JSONObject result = new JSONObject();

        User user = userDao.findFirstByEmail(email);
        if (user == null) {
            result.put("user", "no user");
            return result;
        }

        List<String> articleIds = user.getArticles();
        int total = articleIds.size();
        List<Article> articles = new ArrayList<>();

        int i = total - page * 6 - 1;
        while (i >= 0 && i >= total - (page + 1) * 6) {
            Article article = articleService.listById(articleIds.get(i));
            articles.add(article);
            i--;
        }
        result.put("total", total);
        result.put("list", articles);
        return result;

    }

    public JSONObject searchFileFromUserServer(String keyword, String email) throws Exception {
        String token = tokenService.checkToken(email);
        if(token.equals("out")){
            return null;
        }else{
            JSONObject result = new JSONObject();
            try {


                String url = "http://" + userServer + "/auth/res/search/" + keyword;

                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization","Bearer " + token);
                headers.set("user-agent","portal_backend");
                HttpEntity<JSONObject> httpEntity = new HttpEntity<>(headers);
                ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url,HttpMethod.GET, httpEntity, JSONObject.class);
                result = responseEntity.getBody();

                JSONObject obj = new JSONObject();
                if(result.getInteger("code")!=0){
                    if(result.getInteger("code")==-1){//无此对象
                        obj.put("data", new JSONArray());
                    }else{
                        return null;
                    }
                }else{
                    obj.put("data", result.getJSONArray("data"));

                }

                return obj;
            }catch (Exception e){
                return null;
            }

        }
    }

    public void setSubscribe(String email, Boolean subscribe) {
        User user = userDao.findFirstByEmail(email);
        user.setSubscribe(subscribe);
        userDao.save(user);
    }

    public void setSubscribedList(String email, List<UserSubscribeItem> subscribeItemList) {
        User user = userDao.findFirstByEmail(email);
        user.setSubscribeItemList(subscribeItemList);
        userDao.save(user);
    }

    public JSONObject getUserInfo(String email) throws Exception {
        JSONObject result = new JSONObject();
        int success = taskDao.findAllByEmailAndStatus(email, 2).size();
        int calculating = taskDao.findAllByEmailAndStatus(email, 0).size();
        calculating += taskDao.findAllByEmailAndStatus(email, 1).size();
        int failed = taskDao.findAllByEmailAndStatus(email, -1).size();

        JSONObject taskStatistic = new JSONObject();
        taskStatistic.put("success", success);
        taskStatistic.put("fail", failed);
        taskStatistic.put("calculating", calculating);
        result.put("record", taskStatistic);


        result.put("userInfo", getFullUserInfo(email));
        return result;
    }


    public String updateFileToUserServer(String id, String name, String email) throws Exception {
        String token = tokenService.checkToken(email);
        if(token.equals("out")){
            return "out";
        }else{
            JSONObject result = new JSONObject();
            JSONArray idList = new JSONArray();
            JSONArray errList = new JSONArray();
            int sucCount = 0;
            int errCount = 0;
            List<String> pathsCopy = new ArrayList<>();


            JSONObject j_resourceInfo = new JSONObject();

            j_resourceInfo.put("name", name);
            j_resourceInfo.put("uid", id);

            String url = null;
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + token);
            headers.set("user-agent","portal_backend");
            HttpEntity<JSONObject> httpEntity = new HttpEntity<>(j_resourceInfo, headers);

            JSONObject response = new JSONObject();
            try {
                url = "http://" + userServer + "/auth/res/";
                ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, JSONObject.class);
                response = responseEntity.getBody();
            } catch (Exception e) {
                return null;
            }


            if (response.getInteger("code") != 0) {
                return null;
            }
            return id;

        }
    }


}
