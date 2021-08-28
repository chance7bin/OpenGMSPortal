package njgis.opengms.portal.entity.dto.dataMethod;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataMethodUpdateDTO extends DataMethodDTO{
    String modifyId;
}
