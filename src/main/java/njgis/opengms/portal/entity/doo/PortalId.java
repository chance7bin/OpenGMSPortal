package njgis.opengms.portal.entity.doo;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.util.UUID;

/**
 * @Description 门户统一Id生成类，所有持久层对象都要继承该类
 * @Author kx
 * @Date 2021/7/2
 * @Version 1.0.0
 */

@Data
public class PortalId {

    @org.springframework.data.annotation.Id
    @GeneratedValue(generator = "customId")
    @GenericGenerator(name = "customId", strategy = "njgis.opengms.portal.component.CustomIDGenerator")
    @Field(targetType = FieldType.STRING)
    String id = UUID.randomUUID().toString();

}
