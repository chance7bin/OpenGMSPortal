package njgis.opengms.portal.entity.doo.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParamInfo {
    String eventName;
    String eventDesc;
    String eventType;
    String value;
}
