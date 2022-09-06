package njgis.opengms.portal.entity.doo.metadata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class contactor {
  contactorName contactorName;
  contactorAddress contactorAddress;
  List<String> title;
  List<String> affiliation;
  List<String> email;
  List<String> phone;
  List<String> fax;
  List<String> personalPage;
}
