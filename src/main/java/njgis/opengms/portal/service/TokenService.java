package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.dao.UserDao;
import njgis.opengms.portal.entity.doo.user.TokenInfo;
import njgis.opengms.portal.entity.po.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName TokenService.java
 * @Author wzh
 * @Version 1.0.0
 * @Description 用户服务器登录token
 * @CreateDate(Y/M/D-H:M:S) 2021/03/25/ 17:11:00
 */
@Slf4j
@Service
public class TokenService {

    @Value("${userServer}")
    String userServer;

    @Value("${userServerCilent}")
    String userServerCilent;

    @Value("${userServerCilentPWD}")
    String userServerCilentPWD;

    @Autowired
    UserDao userDao;

    /**
     * oauth2 密码模式获取token
     * @param email
     * @param pwd
     * @return
     */
    public JSONObject getTokenUsePwd(String email, String pwd){
        LinkedMultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("client_id", userServerCilent);
        paramMap.add("client_secret", userServerCilentPWD);
        paramMap.add("username", email);
        paramMap.add("password", pwd);
        paramMap.add("scope", "all");
        paramMap.add("grant_type", "password");
        String authUri = "http://"+ userServer + "/oauth/token";
        //添加错误处理机制,成功后返回是JSONString,失败的话不是。
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("user-agent","portal_backend");

            String str_paramMap = JSON.toJSONString(paramMap);
            HttpEntity<LinkedMultiValueMap<String, String>> httpEntity = new HttpEntity<>(paramMap, headers);
            ResponseEntity<JSONObject> tokenResult = restTemplate.exchange(authUri,HttpMethod.POST, httpEntity, JSONObject.class);

            JSONObject result = tokenResult.getBody();

            return result;
        }catch (Exception e){
            log.error("Exception: " + e);
            // System.out.println("Exception: " + e.toString());
            return null;
        }
    }

    public JSONObject refreshToken(String refreshToken,String email){
        LinkedMultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("grant_type", "refresh_token");
        paramMap.add("client_id", userServerCilent);
        paramMap.add("client_secret", userServerCilentPWD);
        paramMap.add("refresh_token", refreshToken);
        paramMap.add("scope", "all");
        String authUri = "http://"+ userServer + "/oauth/token";
        //添加错误处理机制,成功后返回是JSONString,失败的话不是。
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            MediaType mediaType = MediaType.parseMediaType("application/x-www-form-urlencoded");
            headers.setContentType(mediaType);
            headers.set("user-agent","portal_backend");
            // restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            // restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            //     public boolean hasError(ClientHttpResponse response) throws IOException {
            //         HttpStatus statusCode = response.getStatusCode();
            //         return statusCode.series() == HttpStatus.Series.SERVER_ERROR;
            //     }
            // });
            HttpEntity<LinkedMultiValueMap<String, String>> httpEntity = new HttpEntity<>(paramMap, headers);
            JSONObject tokenResult = restTemplate.postForObject(authUri, httpEntity, JSONObject.class);
            return tokenResult;
        }catch (Exception e){
            log.error("Exception: " + e);
            // System.out.println("Exception: " + e.toString());
            return null;
        }
    }

    /**
     * 获得用户信息
     * @param access_token
     * @return
     */
    public JSONObject getUserFromResServer(String access_token){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+ access_token);
        //httpEntity = httpHeader + httpBody,当然也可以只有其中一部分
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        String resUserUri = "http://" + userServer + "/userServer/auth/userInfo";
        //Url, RequestType, RequestContent, ResponseDataType

        try{

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<JSONObject> userJson = restTemplate.exchange(resUserUri, HttpMethod.GET, httpEntity, JSONObject.class);
            return userJson.getBody();
        }catch (Exception e){
            log.error("Exception: " + e);
            // System.out.println("Exception: " + e.toString());
            return null;
        }

    }

    /**
     * 获得用户基础信息
     * @param access_token
     * @return
     */
    public JSONObject getBasicInfo(String access_token){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+ access_token);
        headers.set("user-agent","portal_backend");
        //httpEntity = httpHeader + httpBody,当然也可以只有其中一部分
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        String resUserUri = "http://" + userServer + "/auth/userBase";//这里不同
        //Url, RequestType, RequestContent, ResponseDataType

        try{

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<JSONObject> userJson = restTemplate.exchange(resUserUri, HttpMethod.GET, httpEntity, JSONObject.class);
            return userJson.getBody();
        }catch (Exception e){
            log.error("Exception: " + e);
            // System.out.println("Exception: " + e.toString());
            return null;
        }

    }

    /**
     * 用戶登錄
     * @param access_token
     * @return
     */
    public JSONObject logintoUserServer(String access_token,String ip){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+ access_token);
        headers.set("user-agent","portal_backend");
        //httpEntity = httpHeader + httpBody,当然也可以只有其中一部分
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        String resUserUri = "http://" + userServer + "/auth/login/" + ip ;
        //Url, RequestType, RequestContent, ResponseDataType
        try{

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<JSONObject> userJson = restTemplate.exchange(resUserUri, HttpMethod.GET, httpEntity, JSONObject.class);
            return userJson.getBody();
        }catch (Exception e){
            log.error("Exception: " + e);
            // System.out.println("Exception: " + e.toString());
            return null;
        }

    }

    /**
     * @param j_tokenInfo
     * @param user
     * @Return java.lang.String
     * @Author wzh
     * @Date 2021/7/6
     **/
    public String refreshUserTokenInfo(JSONObject j_tokenInfo,User user) throws ParseException {

        TokenInfo tokenInfo = new TokenInfo();

        tokenInfo.setExpire(j_tokenInfo.getString("expires_in"));
        tokenInfo.setToken(j_tokenInfo.getString("access_token"));

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        Date expiryTime = dateFormat.parse(j_tokenInfo.getString("invalidTime"));

        tokenInfo.setExpiryTime(expiryTime);
        tokenInfo.setRefreshToken(j_tokenInfo.getString("refresh_token"));

        user.setTokenInfo(tokenInfo);
        userDao.save(user);

        return tokenInfo.getToken();
    }

    /**
     * @param userEmail
     * @Return java.lang.String
     * @Author wzh
     * @Date 2021/7/6
     **/
    public String checkToken(String userEmail) throws Exception {
        User user = userDao.findFirstByEmail(userEmail);
        TokenInfo tokenInfo = user.getTokenInfo();
        Date expireDate = tokenInfo.getExpiryTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date afterNow = new Date(new Date().getTime() + 100*1000);//加上100s的信息处理时间

        if(expireDate.before(afterNow)){
            String refreshToken = tokenInfo.getRefreshToken();
            JSONObject newTokenInfo = refreshToken(refreshToken,userEmail);
            // TODO newTokenInfo可能为null，要判断一下
            if(newTokenInfo.getString("error")!=null){

                return "out";
            }else{
                return refreshUserTokenInfo(newTokenInfo,user);
            }

        }else{
            return tokenInfo.getToken();
        }
    }




}
