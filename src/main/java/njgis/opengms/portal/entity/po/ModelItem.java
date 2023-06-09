package njgis.opengms.portal.entity.po;

import lombok.Data;
import njgis.opengms.portal.entity.doo.base.PortalItem;
import njgis.opengms.portal.entity.doo.model.ModelItemRelate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
@Data
public class ModelItem extends PortalItem {

    String knowledgeGraph;


    List<String> references = new ArrayList<>();// 与Article类相关联
//    List<String> relatedData = new ArrayList<>();

    ModelItemRelate relate = new ModelItemRelate();

//    List<ModelRelation> modelRelationList = new ArrayList<>();

    // ModelMetadata metadata = new ModelMetadata();

//    String modelId; //替换为accessId

    //存放模型元数据信息id
    String metadataId;


}
