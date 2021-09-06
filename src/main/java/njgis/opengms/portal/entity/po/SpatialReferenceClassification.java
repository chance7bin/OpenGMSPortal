package njgis.opengms.portal.entity.po;

import lombok.Data;
import lombok.NoArgsConstructor;
import njgis.opengms.portal.entity.doo.GenericCategory;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class SpatialReferenceClassification extends GenericCategory {

}
