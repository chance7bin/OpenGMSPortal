package njgis.opengms.portal.entity.dto.task;

import lombok.Data;

/**
 * @author 7bin
 * @date 2023/03/31
 */
@Data
public class TaskPublishDTO {

    String id; // 任务id
    String computableId; // 计算模型id
    String timeRange; // 时间范围
    String applicationArea; // 应用区域
    String description; // 描述
    String computableName; // 计算模型名称

}
