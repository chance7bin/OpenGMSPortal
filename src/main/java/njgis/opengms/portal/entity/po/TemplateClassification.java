package njgis.opengms.portal.entity.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import njgis.opengms.portal.entity.doo.GenericCategory;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/30
 */
@Document
@Data
public class TemplateClassification extends GenericCategory {

}
