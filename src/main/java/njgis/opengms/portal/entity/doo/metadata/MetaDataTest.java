package njgis.opengms.portal.entity.doo.metadata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetaDataTest implements Serializable {
  String runTime;
  List<runTemplate> runTemplates;
  modelAnalysis modelAnalysis;
  String instruction;
}
