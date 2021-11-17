package njgis.opengms.portal.entity.doo.base;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Description 在Id的基础上加入了AccessID
 * @Author kx
 * @Date 2021/7/2
 * @Version 1.0.0
 */
@Document
@Data
public class PortalIdPlus extends PortalId {

    String accessId;//用来访问条目或用户主页的id
}
