package njgis.opengms.portal.entity.dto.user;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2022/03/03
 */
@Data
public class UserInfoUpdateDTO {
    String introduction; //userServer的信息
    List<String> organizations = new ArrayList<>(); //userServer的信息
    // String location;
    List<String> externalLinks = new ArrayList<>();//存放用户的外部网站个人页面
    List<String> researchInterests = new ArrayList<>(); //researchInterests
}
