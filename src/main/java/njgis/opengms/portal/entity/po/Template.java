package njgis.opengms.portal.entity.po;

import lombok.Data;
import njgis.opengms.portal.entity.doo.base.PortalItem;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/26
 */
@Document
@Data
public class Template extends PortalItem {

    String xml;
    String type;
    String parentId;

    List<String> relatedMethods;//所链接的方法
}
