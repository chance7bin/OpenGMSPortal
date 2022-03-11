package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.dao.UserDao;
import njgis.opengms.portal.entity.po.User;
import njgis.opengms.portal.utils.XmlTool;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;

/**
 * @Description
 * @Author bin
 * @Date 2022/03/03
 */
@Service
public class DataServerService {

    @Autowired
    UserDao userDao;

    @Value("${dataServerManager}")
    private String dataServerManager;

    public JSONObject getUserNode(String userEmail) throws DocumentException {
        JSONObject userNode = new JSONObject();

        User user = userDao.findFirstByEmail(userEmail);

        String token = user.getDataNodeToken();
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(6000);
        httpRequestFactory.setConnectTimeout(6000);
        httpRequestFactory.setReadTimeout(6000);
        if(token!=null){//如果已经存过token，则直接去缓存里找，测试是否在线即可
            String checkUrl = "http://" + dataServerManager+"state?token="+ URLEncoder.encode(token);
            RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
            JSONObject result = restTemplate.getForObject(checkUrl,JSONObject.class);
            if(result.getString("message").equals("online")){
                userNode.put("token",token);
                return userNode;
            }
        }

        //没有存过token或token不在线，则可能是第一次连或token失效，获取所有id并筛选,更新用户的token
        String url = "http://" + dataServerManager+"/onlineNodes";

        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        String xml = null;
        try{
            xml = restTemplate.getForObject(url,String.class);
        }catch (Exception e){
            JSONObject json = new JSONObject();
            return json;
        }

        if(xml.equals("err")){
            JSONObject json = new JSONObject();
            return json;
        }

        JSONObject jsonObject = XmlTool.xml2Json(xml);
        JSONObject jsonNodeInfo = jsonObject.getJSONObject("serviceNodes");

        try {
            JSONArray nodes = jsonNodeInfo.getJSONArray("onlineServiceNode");

            for (int i = 0; i < nodes.size(); i++) {
                JSONObject node = (JSONObject) nodes.get(i);
                if (node.getString("node").equals(userEmail)) {
                    userNode = node;
                    user.setDataNodeToken(userNode.getString("token"));
                }
            }
        } catch (Exception e) {
            JSONObject node = jsonNodeInfo.getJSONObject("onlineServiceNode");
            if (node.getString("node").equals(userEmail)) {
                userNode = node;
                user.setDataNodeToken(userNode.getString("token"));
            }
        }

        return userNode;
    }

}
