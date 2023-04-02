package njgis.opengms.portal.entity.po;

import lombok.Data;
import njgis.opengms.portal.entity.doo.base.PortalItem;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class SpatialReference extends PortalItem {
    String wkname;
    String wkt;
    String type;
    String parentId;
    String xml;

}
