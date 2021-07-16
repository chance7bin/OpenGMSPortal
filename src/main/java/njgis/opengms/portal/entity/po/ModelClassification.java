package njgis.opengms.portal.entity.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import njgis.opengms.portal.entity.doo.base.PortalId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @ClassName ModelClassification
 * @Description 模型的分类
 * @Author Kai
 * @Date 2019/2/21
 * @Version 1.0.0
 */
@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelClassification extends PortalId {

    List<String> childrenId;
    String nameCn;
    String nameEn;
    String parentId;

}