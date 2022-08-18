package njgis.opengms.portal.entity.dto.task;

import lombok.Data;
import njgis.opengms.portal.entity.doo.intergrate.DataProcessing;
import njgis.opengms.portal.entity.doo.intergrate.ModelAction;

import java.util.List;
import java.util.Map;

@Data
public class IntegratedTaskAddDto {

    String taskOid;
    String taskId;
    String taskName;
    String description;

    List<Map<String,String>> models;

    List<Map<String,String>> processingTools;

    List<ModelAction> modelActions;

    List<DataProcessing> dataProcessings;

    List<Map<String,Object>> dataItems;

    List<Map<String,String>> dataLinks;

    String xml;
    String mxgraph;

}

