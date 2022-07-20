package njgis.opengms.portal.dao;

import njgis.opengms.portal.component.AopCacheEnable;
import njgis.opengms.portal.entity.po.SpatialReference;
import njgis.opengms.portal.enums.ItemTypeEnum;
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

    @AopCacheEnable(key = "#id", group = ItemTypeEnum.SpatialReference, expireTime = 300)
    SpatialReference findFirstById(String id);

    Page<SpatialReference> findAllByClassificationsIn(List<String> classifications, Pageable pageable);
    Page<SpatialReference> findAllByNameLikeIgnoreCaseAndClassificationsIn(String Name,List<String> classification,Pageable pageable);
}
