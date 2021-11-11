package njgis.opengms.portal.entity.doo.task;

import lombok.Data;
import njgis.opengms.portal.entity.doo.support.ParamInfo;

/**
 * @Description
 * @Author bin
 * @Date 2021/11/10
 */
@Data
public class InputDataChildren extends ParamInfo {
    String eventId;
    boolean child;
}
