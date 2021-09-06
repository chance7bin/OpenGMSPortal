package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.UnitClassification;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description
 * @Author bin
 * @Date 2021/09/01
 */
public interface UnitClassificationDao extends MongoRepository<UnitClassification,String>,GenericCategoryDao<UnitClassification> {
}
