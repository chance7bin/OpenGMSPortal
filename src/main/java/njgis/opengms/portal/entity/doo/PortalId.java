package njgis.opengms.portal.entity.doo;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

/**
 * @Description 门户统一Id生成类，所有持久层对象都要继承该类
 * @Author kx
 * @Date 2021/7/2
 * @Version 1.0.0
 */
@Document
@Data
public class PortalId {

    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "custom-id")
    @GenericGenerator(name = "custom-id", strategy = "njgis.opengms.portal.component.CustomIDGenerator")
    String id;

}
