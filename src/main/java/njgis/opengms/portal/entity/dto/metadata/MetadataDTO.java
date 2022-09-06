package njgis.opengms.portal.entity.dto.metadata;

import lombok.Data;
import njgis.opengms.portal.entity.doo.metadata.*;

import java.io.Serializable;

@Data
public class MetadataDTO implements Serializable {
  PreIntegrationEvaluation pie;
  ModelPreparation mp;
  ModelOrchestration mo;
  DataInteroperability di;
  MetaDataTest mdt;
  String createTime;
  String editTime;
}
