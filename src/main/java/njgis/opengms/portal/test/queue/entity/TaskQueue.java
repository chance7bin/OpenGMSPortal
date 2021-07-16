package njgis.opengms.portal.test.queue.entity;

import lombok.Data;
import njgis.opengms.portal.entity.doo.PortalId;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Description description
 * @Author bin
 * @Date 2021/07/15
 */
@Document
@Data
public class TaskQueue extends PortalId {
    private String taskId;
}
