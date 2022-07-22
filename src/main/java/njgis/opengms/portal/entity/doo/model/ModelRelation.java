package njgis.opengms.portal.entity.doo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import njgis.opengms.portal.enums.RelationTypeEnum;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelRelation implements Comparable{

    String modelId;
    RelationTypeEnum relation;


    @Override
    public int compareTo(Object o) {
        return this.getModelId().compareTo(((ModelRelation)o).getModelId());
    }

}