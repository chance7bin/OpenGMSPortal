package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.SpatialReferenceClassification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/09/01
 */
public interface SpatialReferenceClassificationDao extends MongoRepository<SpatialReferenceClassification, String>, GenericCategoryDao<SpatialReferenceClassification>{
    List<SpatialReferenceClassification> findAllByParentId(String parentId);

}
