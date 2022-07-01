package njgis.opengms.portal.entity.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import njgis.opengms.portal.entity.doo.user.UserServerResource;
import njgis.opengms.portal.enums.UserTitleEnum;

import java.util.List;

/**
 * @ClassName userShuttleDTO.java
 * @Author wzh
 * @Version 1.0.0
 * @Description Used to post or get basic info of a user, like a info shuttle bettween portal and userServer.
 * @CreateDate(Y/M/D:H:M) 2021/03/24/ 20:27:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserShuttleDTO {
    private String userId; //uuid 对应id
    private String email;
    /*
    用户可修改信息
     */
    private String password;  //MD5 在userserver后端进行MD5编码
    private String name;

    private String phone;
    private UserTitleEnum title; //enum类型，只需要传入对应的字符串即可
    private String country;
    private String state;
    private String city;
    private String homepage;
    //动态数组，只需要对应String[]即可
    private List<String> organizations;
    //研究领域
    private List<String> domain;
    private String introduction;
    private String avatar;

    /*
    资源对象，直接以对象方式存入mongodb
    其包含的字段：
    UID,Name,Address,Type,Privacy,Thumbnail,EditToolInfo,FileSize
    Parent,MD5,Suffix,Description,Template,UploadTime,Children
     */
    private List<UserServerResource> resource;

    //用户无法自行修改部分
    private String createdTime;
    private List<String> loginIp;

}



