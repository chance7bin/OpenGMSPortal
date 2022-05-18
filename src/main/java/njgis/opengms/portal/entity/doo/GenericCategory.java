package njgis.opengms.portal.entity.doo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import njgis.opengms.portal.entity.doo.base.PortalId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
public class GenericCategory extends PortalId {

    List<String> childrenId;
    String nameCn;
    String nameEn;
    String parentId;

}
