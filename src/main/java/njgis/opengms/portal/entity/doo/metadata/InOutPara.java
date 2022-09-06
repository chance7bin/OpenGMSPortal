package njgis.opengms.portal.entity.doo.metadata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InOutPara {
  String name;
  Integer type;
  String desc;
  String unit;
  String limit;
  String defaultValue;
  String format;
}
