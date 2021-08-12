package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.doo.data.DataCategorys;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/05
 */
public interface DataCategorysDao extends MongoRepository<DataCategorys,String> {
    DataCategorys findFirstById(String id);
}
