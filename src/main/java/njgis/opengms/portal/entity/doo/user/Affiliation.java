package njgis.opengms.portal.entity.doo.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Affiliation {
    String name;
    String department;
    String location;
    String position;




}
