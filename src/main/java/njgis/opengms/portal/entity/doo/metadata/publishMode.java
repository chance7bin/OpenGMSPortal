package njgis.opengms.portal.entity.doo.metadata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class publishMode {
  Boolean openAccess;
  String accessMethod;
  String usageMethod;
  String openSourceProtocol;
}
