package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.dao.ModelContainerDao;
import njgis.opengms.portal.entity.po.ModelContainer;
import njgis.opengms.portal.utils.Utils;
import njgis.opengms.portal.utils.XmlTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/12/27
 */
@Service
public class ServerService {


    @Autowired
    ModelContainerDao modelContainerDao;


    @Value("${dataServerManager}")
    private String dataServerManager;


    //获取所有模型\数据容器
    public JSONArray getAllServerNodes(){

        JSONArray nodes = new JSONArray();
        //模型容器节点
        List<ModelContainer> modelContainerList = modelContainerDao.findAll();
        for(ModelContainer modelContainer : modelContainerList){
            JSONObject node = new JSONObject();
            node.put("geoJson",modelContainer.getGeoInfo());
            node.put("type","model");
            nodes.add(node);
        }

        //数据容器节点
        try {
            HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
            httpRequestFactory.setConnectionRequestTimeout(6000);
            httpRequestFactory.setConnectTimeout(6000);
            httpRequestFactory.setReadTimeout(6000);

            String url = "http://" + dataServerManager + "/onlineNodes";

            RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
            String xml = null;
            try {
                xml = restTemplate.getForObject(url, String.class);
            } catch (Exception e) {

            }

            if (xml.equals("err")) {
                xml = null;
            }

            if(xml!=null) {

                JSONObject jsonObject = XmlTool.xml2Json(xml);
                JSONObject jsonNodeInfo = jsonObject.getJSONObject("serviceNodes");



                try {
                    JSONArray dataNodes = jsonNodeInfo.getJSONArray("onlineServiceNode");

                    for (int i = 0; i < dataNodes.size(); i++) {
                        JSONObject dataNode = (JSONObject) dataNodes.get(i);
                        JSONObject node = new JSONObject();
                        node.put("geoJson", Utils.getGeoInfoMeta(dataNode.getString("ip")));
                        node.put("type","data");
                        nodes.add(node);
                    }
                } catch (Exception e) {
                    JSONObject dataNode = jsonNodeInfo.getJSONObject("onlineServiceNode");
                    JSONObject node = new JSONObject();
                    node.put("geoJson",Utils.getGeoInfoMeta(dataNode.getString("ip")));
                    node.put("type","data");
                    nodes.add(node);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

        return nodes;
    }

}