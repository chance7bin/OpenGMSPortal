package njgis.opengms.portal.entity.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import njgis.opengms.portal.entity.doo.base.PortalId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Conference extends PortalId {

    // @Id
    // String id;
    // String oid;

    String title;
    String theme;
    String conferenceRole;
    String location;
    String contributor;
    String startTime;
    String endTime;
    Date createDate;
    int viewCount;
}
