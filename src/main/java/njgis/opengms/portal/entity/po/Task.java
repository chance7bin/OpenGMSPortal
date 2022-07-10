package njgis.opengms.portal.entity.po;

import lombok.Data;
import njgis.opengms.portal.entity.doo.base.PortalId;
import njgis.opengms.portal.entity.doo.intergrate.Model;
import njgis.opengms.portal.entity.doo.intergrate.ModelParam;
import njgis.opengms.portal.entity.doo.support.GeoInfoMeta;
import njgis.opengms.portal.entity.doo.support.TaskData;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document
@Data
public class Task extends PortalId {
    String taskId;
    String computableId;
    String computableName;
    String email;  //原来的userId -> 用userId到user表找email
    String ip;
    String description;
    int port;
    int loadTime;

    Date runTime;

    int status;//Started: 1, Finished: 2, Inited: 0, Error: -1

//    List<String> isPublic;//public ;noPublic ;userNames; public和noPublic都放数组头
    String permission;
    List<TaskData> inputs;
    List<TaskData> outputs;

    boolean flag = true;
    GeoInfoMeta geoInfoMeta;//一般不需要填

    //集成模型
    Boolean integrate;
    List<Model> models;
    String graphXml;
    List<ModelParam> modelParams;

}
