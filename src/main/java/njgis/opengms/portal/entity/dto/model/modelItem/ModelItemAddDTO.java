package njgis.opengms.portal.entity.dto.model.modelItem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import njgis.opengms.portal.entity.doo.AuthorInfo;
import njgis.opengms.portal.entity.doo.Localization;
import njgis.opengms.portal.entity.doo.model.ModelMetadata;

import java.util.List;

/**
 * @ClassName ModelItemAddDTO
 * @Description 用于模型创建的数据交换对象
 * @Author Kai
 * @Date 2019/2/21
 * @Version 1.0.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelItemAddDTO {

    String name;
    List<String> alias;
    String uploadImage;
    String overview;
    String status;
//    String author;

    List<Localization> localizationList;

    List<AuthorInfo> authorships;
    List<String> classifications;
    List<String> keywords;
    List<String> references;

    ModelMetadata metaData;

}
