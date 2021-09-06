package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.Concept;
import njgis.opengms.portal.entity.po.Template;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description
 * @Author bin
 * @Date 2021/09/01
 */
public interface ConceptDao extends MongoRepository<Concept,String>, GenericItemDao<Concept> {
}
