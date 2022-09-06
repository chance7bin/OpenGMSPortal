package njgis.opengms.portal.entity.doo.metadata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class modelVersion {
  String seriesName;
  String numbering;
  Integer purpose;
  String changeContent;
  List<contactor> author;
  Date date;
}
