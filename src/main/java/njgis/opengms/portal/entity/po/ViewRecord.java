package njgis.opengms.portal.entity.po;

import lombok.Data;
import njgis.opengms.portal.entity.doo.support.GeoInfoMeta;
import njgis.opengms.portal.enums.ItemTypeEnum;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Data
public class ViewRecord {

    @Id
    String id;
    String ip;
    ItemTypeEnum itemType;
    String itemOid;
    Date date;
    String userOid;
    String url;
    String method;
    String userAgent;

    boolean flag = true;

    GeoInfoMeta geoInfoMeta;


}
