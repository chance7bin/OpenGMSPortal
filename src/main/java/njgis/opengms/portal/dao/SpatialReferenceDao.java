package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.SpatialReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/09/01
 */
public interface SpatialReferenceDao extends MongoRepository<SpatialReference,String>, GenericItemDao<SpatialReference>{
    Page<SpatialReference> findAllByClassificationsIn(List<String> classifications, Pageable pageable);
    Page<SpatialReference> findAllByNameLikeIgnoreCaseAndClassificationsIn(String Name,List<String> classification,Pageable pageable);
}
