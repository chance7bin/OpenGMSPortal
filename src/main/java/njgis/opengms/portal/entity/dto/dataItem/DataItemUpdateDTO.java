package njgis.opengms.portal.entity.dto.dataItem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataItemUpdateDTO extends DataItemAddDTO{
    String dataItemId;
    List<String> contributors;
}