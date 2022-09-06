package njgis.opengms.portal.entity.po;

import lombok.Data;
import njgis.opengms.portal.entity.doo.base.PortalId;
import njgis.opengms.portal.entity.doo.metadata.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
public class ModelMetaData extends PortalId {

  PreIntegrationEvaluation pie;
  ModelPreparation mp;
  ModelOrchestration mo;
  DataInteroperability di;
  MetaDataTest mdt;
  String createTime;
  String editTime;
}
