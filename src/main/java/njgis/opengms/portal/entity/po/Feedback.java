package njgis.opengms.portal.entity.po;

import lombok.Data;
import njgis.opengms.portal.entity.doo.base.PortalId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @Description
 * @Author bin
 * @Date 2022/03/03
 */
@Document
@Data
public class Feedback extends PortalId {
    String content;
    String email;
    Date time;
}
