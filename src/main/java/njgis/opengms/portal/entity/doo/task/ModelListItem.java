package njgis.opengms.portal.entity.doo.task;

import lombok.Data;

/**
 * @Description
 * @Author bin
 * @Date 2021/11/11
 */
@Data
public class ModelListItem {
    String id;  //模型id
    String name;  //模型名字
    String author;  //模型作者
    String taskId;  //此次调用模型的taskId
}
