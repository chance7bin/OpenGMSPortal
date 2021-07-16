package njgis.opengms.portal.entity.dto.modelItem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import njgis.opengms.portal.entity.doo.modelItem.ModelItemRelate;
import njgis.opengms.portal.entity.doo.modelItem.ModelMetadata;


/**
 * @ClassName ModelItemAddDTO
 * @Description
 * @Author Kai
 * @Date 2019/2/21
 * @Version 1.0.0
 *
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelItemUpdateDTO extends ModelItemAddDTO {

    String originId;

    // model 和 data 都放到relate中
    ModelItemRelate relate = new ModelItemRelate();
    ModelMetadata metadata = new ModelMetadata();

}
