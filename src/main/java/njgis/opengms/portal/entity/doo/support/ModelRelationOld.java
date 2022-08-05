package njgis.opengms.portal.entity.doo.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import njgis.opengms.portal.enums.RelationTypeEnum;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelRelationOld {

    String oid;
    RelationTypeEnum relation;
}