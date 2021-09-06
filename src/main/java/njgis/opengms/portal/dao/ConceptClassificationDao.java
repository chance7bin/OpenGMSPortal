package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.ConceptClassification;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description
 * @Author bin
 * @Date 2021/09/01
 */
public interface ConceptClassificationDao extends MongoRepository<ConceptClassification,String>,GenericCategoryDao<ConceptClassification>{
}
