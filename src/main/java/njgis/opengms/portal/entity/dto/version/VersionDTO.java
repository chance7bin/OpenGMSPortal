package njgis.opengms.portal.entity.dto.version;

import lombok.Data;
import njgis.opengms.portal.entity.doo.base.PortalItem;
import njgis.opengms.portal.entity.po.Version;

/**
 * @Description 版本对比时用这个类，可以查看到新旧版本的信息，待审核版本的条目信息在父类的content中
 * @Author bin
 * @Date 2021/09/25
 */
@Data
public class VersionDTO extends Version {
    PortalItem original; //原始的条目信息
}
