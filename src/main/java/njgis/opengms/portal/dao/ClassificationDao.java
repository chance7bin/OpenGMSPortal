package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.Classification;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/12
 */
public interface ClassificationDao extends MongoRepository<Classification,String> {
    Classification findFirstByOid(String id);
}
