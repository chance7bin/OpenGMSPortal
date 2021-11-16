package njgis.opengms.portal.entity.dto.data.dataItem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import njgis.opengms.portal.entity.doo.DataMeta;
import njgis.opengms.portal.entity.doo.support.DataItemMeta;
import njgis.opengms.portal.entity.dto.AddDTO;

import java.util.List;

/**
 * @ClassName DataItemAddDTO
 * @Description todo
 * @Author sun_liber
 * @Date 2019/2/13
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataItemDTO extends AddDTO {
    String url;  //原先的reference 若dataType为Url，则需填写该字段
    List<String> displays;
    String dataType;
    List<DataMeta> dataList; //若dataType为File, 则添加上传至数据容器的文件元数据
    DataItemMeta meta;
}
