package njgis.opengms.portal.entity.doo.metadata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreIntegrationEvaluation implements Serializable {
  List<String> modelPurpose;
  List<String> modelAssumption;
  List<String> basicPrinciple;
  Integer modelCategory;
  List<String> classificationInfo;
  applicationScope applicationScope;
  List<String> codeLanguage;
}