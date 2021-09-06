package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.Concept;
import njgis.opengms.portal.entity.po.SpatialReference;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description
 * @Author bin
 * @Date 2021/09/01
 */
public interface SpatialReferenceDao extends MongoRepository<SpatialReference,String>, GenericItemDao<SpatialReference>{
}
