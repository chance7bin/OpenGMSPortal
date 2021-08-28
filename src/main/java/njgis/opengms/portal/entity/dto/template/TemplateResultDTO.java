package njgis.opengms.portal.entity.dto.template;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateResultDTO {

    String id;
    String status;
    String name;
    String image;
    String overview;
    String author;
    Date createTime;

    int viewCount=0;
}
