package njgis.opengms.portal.entity.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import njgis.opengms.portal.entity.doo.base.PortalId;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Project extends PortalId {
    // @Id
    // String id;
    // String oid;

    String projectName;
    String startTime;
    String endTime;
    String contributor;
    String role;
    String fundAgency;
    int amount;
    int viewCount;
    Date createDate;
}
