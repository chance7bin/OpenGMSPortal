package njgis.opengms.portal.dao;


import njgis.opengms.portal.entity.po.DataItem;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @InterfaceName DataItemDao
 * @Description 数据条目数据访问层
 * @Author sun_liber
 * @Date 2019/2/13
 * @Version 1.0.0
 */
public interface DataItemDao extends MongoRepository<DataItem,String> {

    DataItem findFirstById(String id);

    DataItem findFirstByAuthor(String author);

    DataItem findFirstByName(String name);

}
