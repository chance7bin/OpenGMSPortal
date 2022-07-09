package njgis.opengms.portal.entity.po;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;
import njgis.opengms.portal.entity.doo.base.PortalId;
import njgis.opengms.portal.entity.doo.support.GeoInfoMeta;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Data
public class ModelContainer extends PortalId {
    String account;  //用户email 原user表的userName
    String mac;
    String serverName;
    String alias;
    JSONArray serviceList;
//    List<Software> software;
//    Hardware hardware;
    String ip;
    GeoInfoMeta geoInfo;
    Date registerDate;
    Date updateDate;
//    Date updateDate;
//    boolean status;
//    String t_id;
}
