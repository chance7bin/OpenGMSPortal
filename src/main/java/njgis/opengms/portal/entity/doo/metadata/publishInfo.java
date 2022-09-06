package njgis.opengms.portal.entity.doo.metadata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class publishInfo {
  publishMode publishMode;
  publishMethod publishMethod;
  Date publishDate;
  List<contactor> publisher;
}
