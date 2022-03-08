package njgis.opengms.portal.entity.doo.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Conference {

    String title;
    String theme;
    String conferenceRole;
    String location;
    String contributor;
    String startTime;
    String endTime;
}
