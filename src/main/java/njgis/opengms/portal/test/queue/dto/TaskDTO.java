package njgis.opengms.portal.test.queue.dto;

import lombok.Data;
import njgis.opengms.portal.test.queue.entity.DataItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName taskDTO.java
 * @Author wzh
 * @Version 1.0.0
 * @Description
 * @CreateDate(Y/M/D-H:M:S) 2021/07/16/ 21:05:00
 */
@Data
public class TaskDTO {
    private String taskId;
    private String md5;
    private String model;//存储模型的名称

    private int status;
    private int queueNum;

    private List<DataItem> inputData;
    private List<DataItem> outputData;
}
