package njgis.opengms.portal.dao;

import njgis.opengms.portal.component.annotation.AopCacheEnable;
import njgis.opengms.portal.entity.po.Unit;
import njgis.opengms.portal.enums.ItemTypeEnum;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description
 * @Author bin
 * @Date 2021/09/01
 */
public interface UnitDao extends MongoRepository<Unit,String>,GenericItemDao<Unit> {

    @AopCacheEnable(key = "#id", group = ItemTypeEnum.Unit, expireTime = 300)
    Unit findFirstById(String id);

}
