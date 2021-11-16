package njgis.opengms.portal.entity.dto.model;

import lombok.Data;
import njgis.opengms.portal.entity.doo.base.PortalId;

/**
 * @Description 相关模型简要信息
 * @Author kx
 * @Date 21/10/14
 * @Version 1.0.0
 */
@Data
public class RelatedModelInfoDTO extends PortalId {

    String id;
    String name;
    String description;
    String img;

}
