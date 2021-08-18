package njgis.opengms.portal.entity.doo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/16
 */
// @Document
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GenericCatalog {

    @Id
    String id;
    String oid;
    List<String> childrenId;
    String nameCn;
    String nameEn;
    String parentId;

}
