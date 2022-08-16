package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.dao.DataItemDao;
import njgis.opengms.portal.dao.UserDao;
import njgis.opengms.portal.entity.po.DataItem;
import njgis.opengms.portal.entity.po.User;
import njgis.opengms.portal.utils.XmlTool;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    DataItemDao dataItemDao;

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


    public JSONObject pageDataItemChecked(int page,int pageSize,int asc,String sortEle,String searchText,String userName){
        Sort sort = Sort.by(asc==1 ? Sort.Direction.ASC : Sort.Direction.DESC, sortEle);
        Pageable pageable = PageRequest.of(page, pageSize, sort);

        Page<DataItem> dataItemResultDTOPage = dataItemDao.findAllByNameContainsIgnoreCase(pageable,searchText);

        List<DataItem> dataItemList = dataItemResultDTOPage.getContent();
        JSONArray array = new JSONArray();

        for(int i=0;i<dataItemList.size();i++){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("oid",dataItemList.get(i).getId());
            jsonObject.put("name",dataItemList.get(i).getName());
            jsonObject.put("createDate",dataItemList.get(i).getCreateTime());

            User user = userDao.findFirstByEmail(dataItemList.get(i).getAuthor());

            jsonObject.put("contributor",user.getName());
            jsonObject.put("contributorId",user.getAccessId());

            array.add(jsonObject);
        }

        JSONObject result = new JSONObject();
        result.put("content",array);
        result.put("total",dataItemResultDTOPage.getTotalElements());

        return result;
    }


    public JSONObject checkNodeContent(String serverId, String token, String type){

        String contentType = type.equals("Visualization")?"Visualization":"Processing";

        String url = "http://"+dataServerManager+"/capability?"+"id="+serverId+"&token="+URLEncoder.encode(token)+"&type="+contentType;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/json");
        Map<String,String> mheader = new HashMap<>();
        mheader.put("Content-Type","application/json");

        HttpEntity<MultiValueMap> requestEntity = new HttpEntity<MultiValueMap>(null, headers);

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);// 设置超时
        requestFactory.setReadTimeout(5000);
        RestTemplate restTemplate = new RestTemplate(requestFactory);

        JSONObject result = new JSONObject();

        try{
            ResponseEntity<JSONObject> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, JSONObject.class);
            JSONObject j_result = response.getBody();

            try{
                int code = j_result.getInteger("code");
                if(code==0){
                    JSONObject capability = j_result.getJSONObject("capability");
                    result.put("content",capability.get("data"));
//                    TestService testService = new TestService();
//                    testService.setContent(capability.get("data"));
//                    testServiceDao.insert(testService);

                }else{
                    result.put("content","offline");
                }

            }catch (Exception e){
                result.put("content","offline");
            }
        }catch (Exception e){
            result.put("content","offline");
        }


        return result;

    }

}
