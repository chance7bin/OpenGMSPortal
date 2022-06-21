package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.entity.doo.support.ZipStreamEntity;
import njgis.opengms.portal.entity.dto.task.ResultDataDTO;
import njgis.opengms.portal.entity.dto.task.UploadDataDTO;
import njgis.opengms.portal.utils.MyHttpUtils;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
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
@Slf4j
public class AsyncService {

    @Value("${dataContainerIpAndPort}")
    String dataContainerIpAndPort;


    @Value("${resourcePath}")
    private String resourcePath;

    @Autowired
    //用于发送文件
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    //使用@Value注入application.properties中指定的用户名
    private String from;

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
        String url = "https://" + dataContainerIpAndPort + "/data";
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
                String data_url = "https://"+dataContainerIpAndPort+"/data/"+data.getString("source_store_id");
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

    @Async
    public void sendHtmlMail(String to, String subject, String content) {

        // log.info("发送HTML邮件：{},{},{}", to, subject, content);
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
            // mailSender.send(message);
            log.info("向 [{}] 发送邮件成功", to);
        } catch (MessagingException e) {
            log.error("向 [{}] 发送邮件失败", to);
        }
    }

}
