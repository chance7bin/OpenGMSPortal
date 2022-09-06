package njgis.opengms.portal.entity.doo.metadata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class spatialScope {
  Integer dimension;
  String gridType;
  String resolution;
  Integer scale;
  String scope;
}
