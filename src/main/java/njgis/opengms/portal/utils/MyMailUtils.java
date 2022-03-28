package njgis.opengms.portal.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;

@Slf4j
public class MyMailUtils {

    @Value("${spring.mail.username}")
    //使用@Value注入application.properties中指定的用户名
    private String from;

    @Autowired
    //用于发送文件
    private JavaMailSender mailSender;

    // /**
    //  * 发送普通文本邮件
    //  * @param from 发件人
    //  * @param to 收件人
    //  * @param subject 主题
    //  * @param content 内容
    //  */
    // public void sendSimpleMail(String from, String to, String subject, String content) {
    //
    //     SimpleMailMessage message = new SimpleMailMessage();
    //     message.setTo(to);//收信人
    //     message.setSubject(subject);//主题
    //     message.setText(content);//内容
    //     message.setFrom(from);//发信人
    //     mailSender.send(message);
    // }
    //
    // public void sendSimpleMail(String to, String subject, String content) {
    //     SimpleMailMessage message = new SimpleMailMessage();
    //     message.setTo(to);//收信人
    //     message.setSubject(subject);//主题
    //     message.setText(content);//内容
    //     message.setFrom(from);//发信人
    //     mailSender.send(message);
    // }
    //
    // public void sendHtmlMail(String to, String subject, String content) {
    //
    //     log.info("发送HTML邮件开始：{},{},{}", to, subject, content);
    //     //使用MimeMessage，MIME协议
    //     MimeMessage message = mailSender.createMimeMessage();
    //
    //     MimeMessageHelper helper;
    //     //MimeMessageHelper帮助我们设置更丰富的内容
    //     try {
    //         helper = new MimeMessageHelper(message, true);
    //         helper.setFrom(from);
    //         helper.setTo(to);
    //         helper.setSubject(subject);
    //         helper.setText(content, true);//true代表支持html
    //         mailSender.send(message);
    //         log.info("发送HTML邮件成功");
    //     } catch (MessagingException e) {
    //         log.error("发送HTML邮件失败：" +  e);
    //     }
    // }


}
