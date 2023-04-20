package njgis.opengms.portal.entity.po;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    String ip;
    int port;

    String email;  // 发布者 原来的userId -> 用userId到user表找email
    String description; // 描述
    String timeRange; // 时间范围
    String applicationArea; // 应用区域

    int loadTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    Date runTime;

    int status;//Started: 1, Finished: 2, Inited: 0, Error: -1

//    List<String> isPublic;//public ;noPublic ;userNames; public和noPublic都放数组头
    String permission;
    List<TaskData> inputs; // 输入数据
    List<TaskData> outputs; // 输出数据

    boolean flag = true;
    GeoInfoMeta geoInfoMeta;//一般不需要填

    //集成模型
    Boolean integrate;
    List<Model> models;
    String graphXml;
    List<ModelParam> modelParams;

}
