package njgis.opengms.portal.entity.doo.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author mingyuan
 * @Date 2020.12.16 17:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestData {
    String oid;
    String path;
    //绑定的服务
    String url;
    String distributeId;
    String token;

    String label;
    String suffix;
}
