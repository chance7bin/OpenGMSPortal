package njgis.opengms.portal.entity.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/12
 */
@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Classification {

    String oid;
    List<String> childrenId;
    String nameCn;
    String nameEn;
    String parentId;

}
