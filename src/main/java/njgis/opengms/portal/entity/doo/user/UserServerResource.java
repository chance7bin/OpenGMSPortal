package njgis.opengms.portal.entity.doo.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;

/**
 * @ClassName UserServerResource.java
 * @Author wzh
 * @Version 1.0.0
 * @Description
 * @CreateDate(Y/M/D-H:M:S) 2021/04/13/ 20:47:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserServerResource {

    private String uid;
    private String name;
    //数据在数据容器中的链接
    private String address;
    private Boolean folder;
    private Boolean userUpload;
    private String type;
    private String privacy;
    private String thumbnail;
    private String editToolInfo;
    private long fileSize;
    //存储父资源的 uuid
    private String parent;
    private String md5;
    private String suffix;
    private String description;
    private String template;
    private Date uploadTime;
    private ArrayList<UserServerResource> children;

}
