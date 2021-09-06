package njgis.opengms.portal.entity.dto.concept;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import njgis.opengms.portal.entity.dto.AddDTO;

import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/09/02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConceptDTO extends AddDTO {
    List<String> related;
}
