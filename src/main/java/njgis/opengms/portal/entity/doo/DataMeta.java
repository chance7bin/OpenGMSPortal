package njgis.opengms.portal.entity.doo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * @Description 在数据容器上存储的数据
 * @Author kx
 * @Date 2021/7/7
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataMeta {
    String id;
    String name;
    String suffix;
    String url;
}
