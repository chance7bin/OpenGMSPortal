package njgis.opengms.portal.entity.po;

import lombok.Data;
import njgis.opengms.portal.entity.doo.base.PortalId;
import njgis.opengms.portal.entity.doo.support.GeoInfoMeta;
import njgis.opengms.portal.enums.ItemTypeEnum;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Data
public class ViewRecord extends PortalId {

    String ip;
    ItemTypeEnum itemType;
    String itemId;  //原itemOid
    Date date;
    String email; //原userOid
    String url;
    String method;
    String userAgent;

    boolean flag = true;

    GeoInfoMeta geoInfoMeta;


}
