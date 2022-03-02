package njgis.opengms.portal.entity.doo.theme;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @Auther mingyuan
 * @Data 2019.12.30 22:26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubClassInfo {
    String uid;
    Date time;
    String status;
    List<ClassInfo> sub_classInfo;
    String formatTime;
}
