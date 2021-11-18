package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.ModelContainer;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description
 * @Author bin
 * @Date 2021/11/18
 */
public interface ModelContainerDao extends MongoRepository<ModelContainer,String> {
}
