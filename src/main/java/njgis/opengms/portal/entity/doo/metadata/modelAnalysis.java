package njgis.opengms.portal.entity.doo.metadata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class modelAnalysis implements Serializable {
  String modelCalibration;
  String modelValidation;
  String effectivenessAnalysis;
  String uncertaintyAnalysis;
  String sensitivityAnalysis;
}
