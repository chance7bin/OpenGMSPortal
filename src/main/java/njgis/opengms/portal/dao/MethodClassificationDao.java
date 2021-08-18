package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.Classification;
import njgis.opengms.portal.entity.po.MethodClassification;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/16
 */
public interface MethodClassificationDao extends MongoRepository<MethodClassification,String>,GenericCatalogDao<MethodClassification>{
    MethodClassification findFirstById(String id);
}
