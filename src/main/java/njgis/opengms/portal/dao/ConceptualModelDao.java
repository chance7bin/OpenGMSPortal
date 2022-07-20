package njgis.opengms.portal.dao;

import njgis.opengms.portal.component.AopCacheEnable;
import njgis.opengms.portal.entity.po.ConceptualModel;
import njgis.opengms.portal.enums.ItemTypeEnum;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description 概念模型数据访问
 * @Author kx
 * @Date 21/10/12
 * @Version 1.0.0
 */
public interface ConceptualModelDao extends MongoRepository<ConceptualModel,String>, GenericItemDao<ConceptualModel> {
    @AopCacheEnable(key = "#id", group = ItemTypeEnum.ConceptualModel, expireTime = 300)
    ConceptualModel findFirstById(String id);
}
