package njgis.opengms.portal.entity.doo.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Project {
    String projectName;
    String startTime;
    String endTime;
    String contributor;
    String role;
    String fundAgency;
    int amount;
}
