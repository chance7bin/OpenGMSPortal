package njgis.opengms.portal.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description
 * @Author bin
 * @Date 2021/09/01
 */
@Data
public class ResultDTO implements Serializable {

    String id;
    String status;
    String name;
    String image;
    String overview;
    String author;
    Date createTime;

    int viewCount=0;
}
