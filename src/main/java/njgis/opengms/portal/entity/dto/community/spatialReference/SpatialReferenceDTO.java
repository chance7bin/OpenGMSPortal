package njgis.opengms.portal.entity.dto.community.spatialReference;

import lombok.Data;
import njgis.opengms.portal.entity.dto.AddDTO;

/**
 * @Description
 * @Author bin
 * @Date 2021/09/03
 */
@Data
public class SpatialReferenceDTO extends AddDTO {
    String wkname;
    String wkt;
}
