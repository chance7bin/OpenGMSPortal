package njgis.opengms.portal.entity.dto.dataItem;

import lombok.Data;
import njgis.opengms.portal.entity.dto.FindDTO;

import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/09/24
 */
@Data
public class DataItemFindDTO extends FindDTO {
    List<String> searchContent;
}
