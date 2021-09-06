package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.Unit;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description
 * @Author bin
 * @Date 2021/09/01
 */
public interface UnitDao extends MongoRepository<Unit,String>,GenericItemDao<Unit> {
}
