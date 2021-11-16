package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.ComputableModel;
import njgis.opengms.portal.entity.po.ConceptualModel;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description 计算模型数据访问
 * @Author kx
 * @Date 21/10/12
 * @Version 1.0.0
 */
public interface ComputableModelDao extends MongoRepository<ComputableModel,String>, GenericItemDao<ComputableModel> {
}
