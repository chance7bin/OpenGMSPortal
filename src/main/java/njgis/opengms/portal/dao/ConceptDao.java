package njgis.opengms.portal.dao;

import njgis.opengms.portal.component.AopCacheEnable;
import njgis.opengms.portal.entity.po.Concept;
import njgis.opengms.portal.enums.ItemTypeEnum;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description
 * @Author bin
 * @Date 2021/09/01
 */
public interface ConceptDao extends MongoRepository<Concept,String>, GenericItemDao<Concept> {

    @AopCacheEnable(key = "#id", group = ItemTypeEnum.Concept, expireTime = 300)
    Concept findFirstById(String id);
}
