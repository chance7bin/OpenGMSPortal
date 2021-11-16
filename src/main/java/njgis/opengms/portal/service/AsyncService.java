package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.entity.doo.support.ZipStreamEntity;
import njgis.opengms.portal.entity.dto.task.ResultDataDTO;
import njgis.opengms.portal.entity.dto.task.UploadDataDTO;
import njgis.opengms.portal.utils.MyHttpUtils;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

/**
 * @Description 处理异步任务的类
 * 被调用方法 和 调用处的代码处在同一个类相当于本类调用，并没有使用代理类 @Async不会产生效果
 * @Author bin
 * @Date 2021/11/09
 */
@Service
public class AsyncService {

    @Value("${dataContainerIpAndPort}")
    String dataContainerIpAndPort;


    @Value("${resourcePath}")
    private String resourcePath;


    /**
     * 上传数据到数据容器
     * @param uploadDataDTO
     * @param email
     * @return java.util.concurrent.Future<njgis.opengms.portal.entity.dto.task.ResultDataDTO>
     * @Author bin
     **/
    @Async
    public Future<ResultDataDTO> uploadDataToServer(UploadDataDTO uploadDataDTO, String email) {
        // System.out.println(uploadDataDTO.getFilePath());
        ResultDataDTO resultDataDTO = new ResultDataDTO();
        resultDataDTO.setEvent(uploadDataDTO.getEvent());
        resultDataDTO.setStateId(uploadDataDTO.getState());
        resultDataDTO.setChildren(uploadDataDTO.getChildren());
        String testDataPath = uploadDataDTO.getFilePath();
        String url = "http://" + dataContainerIpAndPort + "/data";
        //拼凑form表单
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("name", uploadDataDTO.getEvent());
        params.add("userId", email);
        params.add("serverNode", "china");
        params.add("origination", "portal");

        //拼凑file表单
        List<String> filePaths=new ArrayList<>();
//
        String configParentPath = resourcePath + "/configFile/" + UUID.randomUUID().toString() + "/";
        File path = new File(configParentPath);
        path.mkdirs();
        String configPath = configParentPath + "config.udxcfg";
        File configFile = new File(configPath);

        ZipStreamEntity zipStreamEntity = null;

        try {

            configFile.createNewFile();
//            File configFile=File.createTempFile("config",".udxcfg");

            Writer out = new FileWriter(configFile);
            String content = "<UDXZip>\n";
            content += "\t<Name>\n";
            String[] paths = testDataPath.split("/");
            content += "\t\t<add value=\"" + paths[paths.length - 1] + "\" />\n";
            content += "\t</Name>\n";
            content += "\t<DataTemplate type=\"" + uploadDataDTO.getType() + "\">\n";
            content += "\t\t"+uploadDataDTO.getTemplate()+"\n";
            content += "\t</DataTemplate>\n";
            content += "</UDXZip>";

            out.write(content);
            out.flush();
            out.close();


            filePaths.add(testDataPath);
            filePaths.add(configPath);


        } catch (Exception e) {
            e.printStackTrace();
        }


        JSONObject result;

        try {
            for(int i=0;i<filePaths.size();i++){
                File uploadFile = new File(filePaths.get(i));
                FileInputStream fileInputStream = new FileInputStream(uploadFile);
                // MockMultipartFile(String name, @Nullable String originalFilename, @Nullable String contentType, InputStream contentStream)
                // 其中originalFilename,String contentType 旧名字，类型  可为空
                // ContentType.APPLICATION_OCTET_STREAM.toString() 需要使用HttpClient的包
                MultipartFile multipartFile = new MockMultipartFile(uploadFile.getName(),uploadFile.getName(), ContentType.APPLICATION_OCTET_STREAM.toString(),fileInputStream);

                params.add("datafile", multipartFile.getResource());
            }
            result = MyHttpUtils.uploadDataToDataServer(dataContainerIpAndPort,params);
        } catch (Exception e) {
            result = null;
        }
        if (result == null) {
            resultDataDTO.setUrl("");
            resultDataDTO.setTag("");
        } else {
            JSONObject res = result;
            if (res.getIntValue("code") != 1) {
                resultDataDTO.setUrl("");
                resultDataDTO.setTag("");
                resultDataDTO.setSuffix("");
            } else {
                JSONObject data = res.getJSONObject("data");
                String data_url = "http://"+dataContainerIpAndPort+"/data/"+data.getString("source_store_id");
                String tag = data.getString("file_name");
                String[] paths=testDataPath.split("\\.");
                String suffix = paths[paths.length-1];
                resultDataDTO.setTag(tag);
                resultDataDTO.setUrl(data_url);
                resultDataDTO.setSuffix(suffix);
                resultDataDTO.setVisual(uploadDataDTO.getVisual());
            }
        }
        return new AsyncResult<>(resultDataDTO);


    }

}
