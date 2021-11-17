package njgis.opengms.portal.entity.po;

import lombok.Data;
import njgis.opengms.portal.entity.doo.base.PortalItem;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/09/01
 */
@Document
@Data
public class Concept extends PortalItem {
    List<String> related;
    String xml;
}
