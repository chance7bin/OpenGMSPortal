package njgis.opengms.portal.entity.dto;

import lombok.Data;
import njgis.opengms.portal.entity.doo.AuthorInfo;
import njgis.opengms.portal.entity.doo.Localization;

import java.io.Serializable;
import java.util.List;

/**
 * @Description 新增条目传输对象
 * @Author bin
 * @Date 2021/08/26
 */
@Data
public class AddDTO implements Serializable {
    String name;
    List<String> alias;
    String status;
    String contentType;
    String overview;  //原先的description
    String detail;  //detail里的html,传过来要转成localizationList
    // String author;
    List<String> keywords;
    List<String> classifications;
    List<AuthorInfo> authorships;
    String uploadImage;
    List<Localization> localizationList;
}
