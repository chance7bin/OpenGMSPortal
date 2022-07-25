package njgis.opengms.portal.dao;

import njgis.opengms.portal.component.annotation.AopCacheEnable;
import njgis.opengms.portal.entity.po.LogicalModel;
import njgis.opengms.portal.enums.ItemTypeEnum;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description 逻辑模型数据访问
 * @Author kx
 * @Date 21/10/12
 * @Version 1.0.0
 */
public interface LogicalModelDao extends MongoRepository<LogicalModel,String>, GenericItemDao<LogicalModel> {

    @AopCacheEnable(key = "#id", group = ItemTypeEnum.LogicalModel, expireTime = 300)
    LogicalModel findFirstById(String id);

}
