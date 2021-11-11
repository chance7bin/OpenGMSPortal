package njgis.opengms.portal.entity.doo.task;

import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/11/10
 */
@Data
public class InputData {
    String statename;
    String event;
    String url;
    String tag;
    String suffix;
    String templateId;
    List<InputDataChildren> children;
}
