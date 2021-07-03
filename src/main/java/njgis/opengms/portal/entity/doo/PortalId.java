package njgis.opengms.portal.entity.doo;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

/**
 * @Description todo
 * @Author kx
 * @Date 2021/7/2
 * @Version 1.0.0
 */
@Document
@Data
public class PortalId {

    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "custom-id")     @GenericGenerator(name = "custom-id", strategy = "njgis.opengms.portal.utils.CustomIDGenerator")
    String id;

}
