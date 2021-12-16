package njgis.opengms.portal.entity.doo.task;

import lombok.Data;

/**
 * @Description 用于存储每次调用模型的记录
 * @Author bin
 * @Date 2021/12/15
 */
@Data
public class CheckedHistory extends CheckedModel{
    String modelId; //模型id
    String modelName; //模型名字
    String author; //模型作者
    String taskId; //调用成功的任务id
}
