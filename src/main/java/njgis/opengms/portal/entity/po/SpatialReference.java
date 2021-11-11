package njgis.opengms.portal.entity.po;

import lombok.Data;
import njgis.opengms.portal.entity.doo.PortalItem;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Data
public class SpatialReference extends PortalItem {
    String wkname;
    String wkt;
    String type;
    String parentId;
    String xml;
}