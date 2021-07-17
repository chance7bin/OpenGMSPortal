package njgis.opengms.portal.test.queue.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import njgis.opengms.portal.entity.doo.PortalId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Description description
 * @Author bin
 * @Date 2021/07/15
 */
@Document
@Data
// @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
public class SubmitedTask extends PortalId {

    private String taskId;
    private String taskName;
    private String userId;

    private String model;
    private String md5;

    private int queueNum;
    // @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date runTime;
    // 模型状态 0:未运行 1:正在运行 2:运行完成 -1:运行失败
    private int status;
    private List<DataItem> inputData;
    private List<DataItem> outputData;
}
