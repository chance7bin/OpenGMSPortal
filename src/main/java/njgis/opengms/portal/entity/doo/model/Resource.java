package njgis.opengms.portal.entity.doo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 计算模型资源
 * @Author kx
 * @Date 22/3/1
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Resource {
    String path;
    int downloadCount = 0;
}
