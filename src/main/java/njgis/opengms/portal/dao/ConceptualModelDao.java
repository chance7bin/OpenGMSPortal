package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.Concept;
import njgis.opengms.portal.entity.po.ConceptualModel;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description 概念模型数据访问
 * @Author kx
 * @Date 21/10/12
 * @Version 1.0.0
 */
public interface ConceptualModelDao extends MongoRepository<ConceptualModel,String>, GenericItemDao<ConceptualModel> {
}
