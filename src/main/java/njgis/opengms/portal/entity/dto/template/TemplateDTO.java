package njgis.opengms.portal.entity.dto.template;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import njgis.opengms.portal.entity.dto.AddDTO;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateDTO extends AddDTO {
    String xml;
}
