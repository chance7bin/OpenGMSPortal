package njgis.opengms.portal.entity.po;

import lombok.Data;
import njgis.opengms.portal.entity.doo.intergrate.ControlCondition;
import njgis.opengms.portal.entity.doo.intergrate.DataProcessing;
import njgis.opengms.portal.entity.doo.intergrate.ModelAction;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * wzh
 * 集成模型对象，与普通task不同
 */
@Data
public class IntegratedTask {
    @Id
    String id;

    String oid;
    String taskId;
    String taskName;
    String description;

    List<Map<String,String>> models;
    List<Map<String,String>> processingTools;
    List<ModelAction> modelActions;
    List<DataProcessing> dataProcessings;
    List<Map<String,Object>> dataItems;
    List<Map<String,String>> dataLinks;
    List<ControlCondition> conditions;

    String xml;
    String mxGraph;

    String userId;
    Boolean integrate;

    int status;//Started: 1, Finished: 2, Inited: 0, Error: -1

    Date createTime;
    Date lastModifiedTime;

    Date lastRunTime;
}
