package njgis.opengms.portal.test.queue.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.test.queue.entity.IOData;
import njgis.opengms.portal.test.queue.entity.DataItem;
import njgis.opengms.portal.utils.ResultUtils;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

/**
 * @Description description
 * @Author bin
 * @Date 2021/07/14
 */
//@RestController
public class InvokeModelControllerBak {
    @GetMapping("/invoking")
    public JsonResult invoking(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("a",1);
        jsonObject.put("b",2);
        return ResultUtils.success(jsonObject);
    }

    @PostMapping("/testInvoking")
    public String testInvoking(@RequestBody IOData inputData){
        return inputData.toString();
    }

    @GetMapping("/testGet")
    public void testGet(){
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // 创建Get请求
        HttpGet httpGet = new HttpGet("http://localhost:7777/invoking");

        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Get请求
            response = httpClient.execute(httpGet);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @PostMapping("/postTest")
    public void postTest(){
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        // 创建Post请求
//        HttpPost httpPost = new HttpPost("http://localhost:7777/testInvoking");
//        HttpPost httpPost = new HttpPost("http://172.21.212.103:8060/modelser/5dc2eac82f5bcf0b94735b32?ac=run");
        HttpPost httpPost = new HttpPost("http://localhost:8060/modelser/60ed970b88baba1c6caee5e9?ac=run");
        DataItem[] inputDataItem = new DataItem[2];
        inputDataItem[0] = new DataItem();
        inputDataItem[0].setStateId("349a82c8-7c63-443e-992f-eeff6defa9c2");
        inputDataItem[0].setStateName("run");
        inputDataItem[0].setEvent("inputTextFile");
        inputDataItem[0].setDataId("gd_6992c5c0-e3e3-11eb-91dd-db660c0494d8");
        inputDataItem[1] = new DataItem();
        inputDataItem[1].setStateId("349a82c8-7c63-443e-992f-eeff6defa9c2");
        inputDataItem[1].setStateName("run");
        inputDataItem[1].setEvent("inputLanguageFile");
        inputDataItem[1].setDataId("gd_6992ecd0-e3e3-11eb-91dd-db660c0494d8");
        IOData inputData = new IOData();
        // inputData.setInputdata(inputDataItem);

        DataItem inputdata = new DataItem();
        inputdata.setStateId("349a82c8-7c63-443e-992f-eeff6defa9c2");
        inputdata.setStateName("run");
        inputdata.setEvent("inputTextFile");
        inputdata.setDataId("gd_6992c5c0-e3e3-11eb-91dd-db660c0494d8");

        String jsonString = JSON.toJSONString(inputdata);


        StringEntity entity = new StringEntity(jsonString, "UTF-8");

        // post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
        httpPost.setEntity(entity);

        // 创建Post请求
        // 参数
        /*URI uri = null;
        try {
            // 将参数放入键值对类NameValuePair中,再放入集合中
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("name", "这是什么鬼"));
            params.add(new BasicNameValuePair("age", "18"));
            // 设置uri信息,并将参数集合放入uri;
            // 注:这里也支持一个键值对一个键值对地往里面放setParameter(String key, String value)
            uri = new URIBuilder().setScheme("http").setHost("localhost").setPort(7777)
                .setPath("/testInvoking").setParameters(params).build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpPost httpPost = new HttpPost(uri);*/

//        httpPost.addHeader("encoding", "UTF-8");
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");

        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Post请求
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();

            System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
