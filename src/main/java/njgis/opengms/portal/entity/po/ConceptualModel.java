package njgis.opengms.portal.entity.po;

import lombok.Data;
import njgis.opengms.portal.entity.doo.PortalItem;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @ClassName ConceptualModel
 * @Description 概念模型类
 * @Author Kai
 * @Date 2019/3/1
 * @Version 1.0.0
 */

@Document
@Data
public class ConceptualModel extends PortalItem {

//    String relateModelItem;
    List<String> relatedModelItems; //新增new

    String cXml;
    String svg;

    String contentType;

//    List<String> classifications;
//    List<String> modelItems;
    List<String> imageList; //原为image

}
