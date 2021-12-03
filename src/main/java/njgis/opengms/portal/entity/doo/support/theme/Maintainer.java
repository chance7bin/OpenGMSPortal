package njgis.opengms.portal.entity.doo.support.theme;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

/**
 * @Auther mingyuan
 * @Data 2019.12.10 11:37
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Maintainer {
    @Id
    String id;
    String email;
    String name;
    String image;
    String userId;
}
