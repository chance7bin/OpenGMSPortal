package njgis.opengms.portal.test.queue.entity;

import com.sun.javafx.beans.IDProperty;
import lombok.Data;
import njgis.opengms.portal.entity.doo.PortalId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description description
 * @Author bin
 * @Date 2021/07/16
 */
@Document("RunTask")
@Data
public class RunTask extends PortalId {

    private String taskId;

    private String md5;
    private List<String> relatedTasks = new ArrayList<>();//保存对应提交任务的id

    private String ip;
    private String port;
    private String mid;//模型容器中的模型对应id

    private List<DataItem> inputData;
    private List<DataItem> outputData;

    private String msrid;
    // 模型状态 0:未运行 1:正在运行 2:运行完成 -1:运行失败
    private int status;
}
