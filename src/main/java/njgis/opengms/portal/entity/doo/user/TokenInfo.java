package njgis.opengms.portal.entity.doo.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @ClassName TokenInfo.java
 * @Author wzh
 * @Version 1.0.0
 * @Description
 * @CreateDate(Y/M/D-H:M:S) 2021/03/25/ 19:42:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class TokenInfo {

    String token;   //请求用户资源的令牌
    String expire;   //有效时间:7200(s)
    String refreshToken;  //刷新令牌的令牌
    Date expiryTime;  //失效时间 Date类型

}
