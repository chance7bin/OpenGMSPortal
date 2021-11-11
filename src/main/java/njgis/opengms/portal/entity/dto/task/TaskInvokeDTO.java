package njgis.opengms.portal.entity.dto.task;

import lombok.Data;
import njgis.opengms.portal.entity.doo.task.InputData;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/11/10
 */
@Data
public class TaskInvokeDTO {
    String oid;
    String ip;
    String port;
    String pid;
    List<InputData> inputs = new ArrayList<>();
}
