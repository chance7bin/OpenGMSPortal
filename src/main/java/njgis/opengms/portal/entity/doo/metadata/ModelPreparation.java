package njgis.opengms.portal.entity.doo.metadata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelPreparation implements Serializable {
  modelName modelName;
  modelVersion modelVersion;
  descriptionInfo descriptionInfo;
  List<String> keywords;
  referenceSystem referenceSystem;
  developInfo developInfo;
  publishInfo publishInfo;
  List<relateItem> relateItem;
  List<reference> reference;
  String occupySpace;
  String occupySpaceUnit;
  metaDataVersion metaDataVersion;
}