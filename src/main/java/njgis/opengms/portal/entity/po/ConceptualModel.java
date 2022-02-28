package njgis.opengms.portal.entity.po;

import lombok.Data;
import njgis.opengms.portal.entity.doo.base.PortalItem;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
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
    //前端需要调整的地方：relatedModelItems 数组；单语言描述改为多语言，需要向后台传输List<Localization> localizationList字段

//    String relateModelItem;
    List<String> relatedModelItems = new ArrayList<>(); //新增new

    String cXml;
    String svg;

    String contentType;

//    List<String> classifications;
//    List<String> modelItems;
    List<String> imageList = new ArrayList<>(); //原为image

}
