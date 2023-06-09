package njgis.opengms.portal.entity.doo.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class AwardandHonor {
    String name;
    Date awardTime;
    String contributor;
    //    String type;
    String awardAgency;
}
