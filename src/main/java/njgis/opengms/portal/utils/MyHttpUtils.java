package njgis.opengms.portal.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.entity.doo.MyException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Created by wang ming on 2019/2/18.
 */
@Slf4j
public class MyHttpUtils {

    //设置连接超时时间，单位毫秒
    private static final int CONNECT_TIMEOUT = 75000;

    //设置获取数据的超时时间(即响应时间),单位毫秒

    private static final int SOCKET_TIMEOUT = 10000;

    public static String GET(String urlString, String encode, Map<String, String> headers, String... m)throws IOException, URISyntaxException{
        String body = "";
        //考虑Http身份验证的情况
        CloseableHttpClient client = checkAuth(m);
        if(client == null){
            return "Input Auth parameter error";
        }
        URL url = new URL(urlString);
        URI uri = new URI(url.getProtocol(), url.getHost() + ":" + url.getPort(),url.getPath(),url.getQuery(), null);

        HttpGet httpGet = new HttpGet(uri);

        //设置连接超时时间
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
        httpGet.setConfig(requestConfig);

        //设置header
        if(headers != null && headers.size() > 0){
            for(Map.Entry<String,String> entry: headers.entrySet()){
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }
        }
        CloseableHttpResponse response = client.execute(httpGet);
        HttpEntity entity = response.getEntity();

        if(entity != null){
            body = EntityUtils.toString(entity,encode);
        }
        EntityUtils.consume(entity);

        response.close();
        client.close();
        return body;
    }
    /**
    * @Description:  Http身份验证
    * @Param: [m]
    * @return: org.apache.http.impl.client.CloseableHttpClient
    * @Author: WangMing
    * @Date: 2019/2/18
    */
    public static CloseableHttpClient checkAuth(String... m){
        if(m.length == 2){
            //需要验证
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(m[0],m[1]));
            return HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
        }else if (m.length == 0){
            return HttpClients.createDefault();
        }else{
            return null;
        }
    }

    /**
    * @Description: Post请求，上传数据为json数据
    * @Param: [url, encode, headers, jsonObject]
    * @return: java.lang.String
    * @Author: WangMing
    * @Date: 2019/3/7
    */
    public static String POSTWithJSON(String url, String encode, Map<String, String> headers, JSONObject jsonObject)throws IOException{
        String body = "";
        CloseableHttpClient client = HttpClients.createDefault();
        if(client == null){
            return "Input Auth parameter error";
        }
        HttpPost httpPost = new HttpPost(url);

        //设置连接超时时间
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
        httpPost.setConfig(requestConfig);

        //设置header
        if(headers != null && headers.size() > 0){
            for(Map.Entry<String, String> entry : headers.entrySet()){
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }

        StringEntity stringEntity = new StringEntity(jsonObject.toString(), encode);
        stringEntity.setContentType("application/json");
        httpPost.setEntity(stringEntity);

        CloseableHttpResponse httpResponse = client.execute(httpPost);
        HttpEntity entity = httpResponse.getEntity();

        if(entity != null){
            body = EntityUtils.toString(entity, encode);
        }
        EntityUtils.consume(entity);
        //释放链接
        httpResponse.close();
        client.close();
        return body;
    }

    /**
    * @Description: Post请求，上传数据为字符串文本
    * @Param: [url, encode, headers, stringJson, m]
    * @return: java.lang.String
    * @Author: WangMing
    * @Date: 2019/3/7
    */
    public static String POSTWithString(String url, String encode, Map<String, String> headers, String stringJson, String... m)throws IOException{
        String body = "";
        CloseableHttpClient client = checkAuth(m);;
        if(client == null){
            return "Input Auth parameter error";
        }
        HttpPost httppost = new HttpPost(url);

        //设置连接超时时间
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
        httppost.setConfig(requestConfig);

        if(headers != null && headers.size() > 0){
            for(Map.Entry<String, String> entry : headers.entrySet()){
                httppost.setHeader(entry.getKey(), entry.getValue());
            }
        }

        StringEntity stringEntity = new StringEntity(stringJson, encode);
        httppost.setEntity(stringEntity);

        CloseableHttpResponse httpResponse = client.execute(httppost);
        HttpEntity entity = httpResponse.getEntity();

        if(entity != null){
            body = EntityUtils.toString(entity, encode);
        }
        EntityUtils.consume(entity);
        //释放链接
        httpResponse.close();
        client.close();
        return body;
    }

    /** 
    * @Description: 正常post请求，包含文件上传与传递参数
    * @Param url 链接url
    * @Param encode 编码方式，一般设置为UTF-8
    * @Param header 请求头数据，map格式
    * @Param params body中请求参数，map格式
    * @Param multipartFiles 文件数据，map格式
    * @Param m 验证信息，可有可无
    * @return: java.lang.String 
    * @Author: WangMing 
    * @Date: 2019/3/7 
    */
    public static String POST(String url, String encode, Map<String, String> headers, Map<String, String> params, List<MultipartFile> multipartFiles,
                              String... m)throws IOException{
        String body = "";
        CloseableHttpClient client = checkAuth(m);
        if(client == null){
            return "Input Auth parameter error";
        }

        HttpPost httppost = new HttpPost(url);

        //设置连接超时时间
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
        httppost.setConfig(requestConfig);

        //设置header
        if(headers != null && headers.size() > 0){
            for(Map.Entry<String, String> entry : headers.entrySet()){
                httppost.setHeader(entry.getKey(), entry.getValue());
            }
        }

        //构建body部分
        MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .setCharset(Charset.forName(encode));

        //文件数据
        if (multipartFiles != null && multipartFiles.size() > 0){
            for(MultipartFile multipartFile : multipartFiles){
                builder.addBinaryBody("file", multipartFile.getInputStream(), ContentType.MULTIPART_FORM_DATA, multipartFile.getOriginalFilename());
            }
        }

        //参数数据,解决中文乱码
        ContentType contentType = ContentType.create("text/plain", Charset.forName(encode));
        if(params != null && params.size() > 0){
            for(Map.Entry<String, String> key: params.entrySet()){
                builder.addTextBody(key.getKey(), key.getValue(), contentType);
            }
        }

        HttpEntity entityIn = builder.build();
        //设置参数到请求参数中
        httppost.setEntity(entityIn);

        CloseableHttpResponse response = client.execute(httppost);
        HttpEntity entityOut = response.getEntity();
        if (entityOut != null) {
            //按指定编码转换结果实体为String类型
            body = EntityUtils.toString(entityOut, encode);
        }
        EntityUtils.consume(entityOut);
        //释放链接
        response.close();
        client.close();
        return body;


    }

    /*public static String POSTFile(String url, String encode, Map<String,String>params, Map<String,String> fileMap) throws IOException {
        String body = "";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        //设置header
        httpPost.setHeader("Connection", "Keep-Alive");
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");

        //构建body
        MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .setCharset(Charset.forName(encode));

        if(fileMap != null && fileMap.size() > 0){
            for(Map.Entry<String, String> entry: fileMap.entrySet()){
                File file = new File(entry.getValue());
                builder.addBinaryBody(entry.getKey(), MyFileUtils.getInputStream(file), ContentType.MULTIPART_FORM_DATA, file.getName());
            }
        }

        //构建参数部分，解决中文乱码
        ContentType contentType = ContentType.create("text/plain", Charset.forName(encode));
        if(params != null && params.size() > 0){
            for(Map.Entry<String, String> key: params.entrySet()){
                builder.addTextBody(key.getKey(), key.getValue(), contentType);
            }
        }

        HttpEntity entityIn = builder.build();
        //设置参数到请求参数中
        httpPost.setEntity(entityIn);

        CloseableHttpResponse response = client.execute(httpPost);
        HttpEntity entityOut = response.getEntity();
        if (entityOut != null) {
            //按指定编码转换结果实体为String类型
            body = EntityUtils.toString(entityOut, encode);
        }
        EntityUtils.consume(entityOut);
        //释放链接
        response.close();
        client.close();
        return body;
    }*/

    public static String POSTMultiPartFileToDataServer(String url,String encode, Map<String,String>params,Map<String,MultipartFile> fileMap)throws IOException{
        String body = "";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        //设置header
        httpPost.setHeader("Connection", "Keep-Alive");
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");

        //构建body
        MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .setCharset(Charset.forName(encode));

        if(fileMap != null && fileMap.size() > 0){
            for(Map.Entry<String,MultipartFile> entry: fileMap.entrySet()){
                MultipartFile file = entry.getValue();
                builder.addBinaryBody(entry.getKey(),file.getInputStream(),ContentType.MULTIPART_FORM_DATA, file.getOriginalFilename());
            }
        }


        //构建参数部分，解决中文乱码
        ContentType contentType = ContentType.create("text/plain", Charset.forName(encode));
        if(params != null && params.size() > 0){
            for(Map.Entry<String, String> key: params.entrySet()){
                builder.addTextBody(key.getKey(), key.getValue(), contentType);
            }
        }
        HttpEntity entityIn = builder.build();
        //设置参数到请求参数中
        httpPost.setEntity(entityIn);

        CloseableHttpResponse response = client.execute(httpPost);
        HttpEntity entityOut = response.getEntity();
        if (entityOut != null) {
            //按指定编码转换结果实体为String类型
            body = EntityUtils.toString(entityOut, encode);
        }
        EntityUtils.consume(entityOut);
        //释放链接
        response.close();
        client.close();
        return body;
    }

    public static String DELETE(String url, String encode, Map<String, String> headers, String ...m)throws IOException{
        String body = "";
        CloseableHttpClient client = checkAuth(m);
        if (client == null){
            return "Input Auth parameter error";
        }
        HttpDelete httpDelete = new HttpDelete(url);

        //设置连接超时时间
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
        httpDelete.setConfig(requestConfig);

        //设置header
        if (headers != null && headers.size() > 0){
            for (Map.Entry<String, String> entry : headers.entrySet()){
                httpDelete.setHeader(entry.getKey(), entry.getValue());
            }
        }

        CloseableHttpResponse response = client.execute(httpDelete);
        HttpEntity entity = response.getEntity();

        if(entity != null){
            body = EntityUtils.toString(entity, encode);
        }
        EntityUtils.consume(entity);

        //释放链接
        response.close();
        client.close();
        return body;
    }

    public static String PUTRawString(String url, String encode, Map<String, String> headers, String stringJson, String... m) throws IOException {
        String body = "";
        CloseableHttpClient client = checkAuth(m);
        if (client == null) {
            return "验证输入参数存在问题";
        }
        HttpPut httpput = new HttpPut(url);

        //设置连接超时时间
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
        httpput.setConfig(requestConfig);

        httpput.setHeader("Content-type", "application/json");
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpput.setHeader(entry.getKey(), entry.getValue());
            }
        }
        StringEntity stringEntity;
        if (stringJson == null) {
            ;
        } else {
            stringEntity = new StringEntity(stringJson, encode);
            httpput.setEntity(stringEntity);
        }


        CloseableHttpResponse httpResponse = client.execute(httpput);
        HttpEntity entity = httpResponse.getEntity();

        if (entity != null) {
            //按指定编码转换结果实体为String类型
            body = EntityUtils.toString(entity, encode);
        }
        EntityUtils.consume(entity);
        //释放链接
        httpResponse.close();
        client.close();
        return body;

    }

    public static String PUTRawFile(String url, String encode, Map<String, String> headers, File file, String... m) throws IOException {
        String body = "";
        CloseableHttpClient client = checkAuth(m);
        if (client == null) {
            return "验证输入参数存在问题";
        }
        HttpPut httpput = new HttpPut(url);

        //设置连接超时时间
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
        httpput.setConfig(requestConfig);

        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpput.setHeader(entry.getKey(), entry.getValue());
            }
        }

        //以文件流的方式上传文件，而不是MultiPartFormData方式
        FileEntity fileEntity = new FileEntity(file);
        httpput.setEntity(fileEntity);


        CloseableHttpResponse httpResponse = client.execute(httpput);
        HttpEntity entity = httpResponse.getEntity();

        if (entity != null) {
            //按指定编码转换结果实体为String类型
            body = EntityUtils.toString(entity, encode);
        }
        EntityUtils.consume(entity);
        //释放链接
        httpResponse.close();
        client.close();
        return body;

    }

//    public static String POSTFileToModelContainer(String url, String encode, String paramTag, File file)throws IOException{
//        String body = "";
//        CloseableHttpClient client = HttpClients.createDefault();
//
//        //创建post方式请求对象
//        HttpPost httpPost = new HttpPost(url);
//
//        //设置连接超时时间
//        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
//        httpPost.setConfig(requestConfig);
//
//        //设置header
//        httpPost.setHeader("Connection", "Keep-Alive");
//        httpPost.setHeader("Account-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
//
//        //构建body
//        MultipartEntityBuilder builder = MultipartEntityBuilder.create()
//                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
//                .setCharset(Charset.forName(encode));;
//
//        builder.addBinaryBody("myfile", MyFileUtils.getInputStream(file), ContentType.MULTIPART_FORM_DATA, paramTag);
//
//        //parameter
//        ContentType contentType = ContentType.create("text/plain", Charset.forName(encode));//解决中文乱码
//        builder.addTextBody("gd_tag", paramTag, contentType);
//
//        HttpEntity entityIn = builder.build();
//        //设置参数到请求对象中
//        httpPost.setEntity(entityIn);
//
//        CloseableHttpResponse response = client.execute(httpPost);
//        HttpEntity entityOut = response.getEntity();
//        if (entityOut != null) {
//            //按指定编码转换结果实体为String类型
//            body = EntityUtils.toString(entityOut, encode);
//        }
//        EntityUtils.consume(entityOut);
//        //释放链接
//        response.close();
//        client.close();
//        return body;
//    }

    public static File downloadFile(String url, String filePath) throws IOException {

        File file = new File(filePath);
        //判断文件是否存在
        if(!file.exists()){
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        }
        CloseableHttpClient client = HttpClients.createDefault();

        //默认不使用代理方式
        RequestConfig config = RequestConfig.custom().build();

        //创建get方式请求对象
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(config);

        try{
            CloseableHttpResponse response = client.execute(httpGet);
            if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
                return null;
            }
            HttpEntity entity = response.getEntity();

            if(entity != null){
                InputStream is = entity.getContent();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int size;
                while((size = is.read(buffer)) != -1){
                    fos.write(buffer, 0, size);
                }

                fos.close();
                is.close();

                return file;
            }
        }catch (Exception e){
            // e.printStackTrace();
            log.error(e.getMessage());
        }
        return null;
    }


    /**
     * 上传数据到数据服务器
     * @param dataContainerIpAndPort
     * @param part
     * @param fileSize 
     * @return com.alibaba.fastjson.JSONObject 
     * @Author bin
     **/
    public static JSONObject uploadDataToDataServer(String dataContainerIpAndPort, MultiValueMap<String, Object> part, int ...fileSize) {

        String url="http://"+ dataContainerIpAndPort +"/configData";

        RestTemplate restTemplate = new RestTemplate();

        JSONObject jsonObject = restTemplate.postForObject(url, part, JSONObject.class);

        if(jsonObject.getIntValue("code")==-1){
            throw new MyException("远程服务出错");
        }

        if(fileSize!=null){
            jsonObject.put("file_size",fileSize);
        }

        return jsonObject;

    }

    public static JSONObject uploadDataToDataServer(String dataContainerIpAndPort, @Nullable org.springframework.http.HttpEntity<?> requestEntity, int ...fileSize) {

        String url="http://"+ dataContainerIpAndPort +"/configData";

        RestTemplate restTemplate = new RestTemplate();

        JSONObject jsonObject = restTemplate.postForObject(url, requestEntity, JSONObject.class);

        if(jsonObject.getIntValue("code")==-1){
            throw new MyException("远程服务出错");
        }

        if(fileSize!=null){
            jsonObject.put("file_size",fileSize);
        }

        return jsonObject;

    }


}
