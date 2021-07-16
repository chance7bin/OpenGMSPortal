package njgis.opengms.portal.test.queue.dao;

import njgis.opengms.portal.test.queue.entity.ServerTable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description description
 * @Author bin
 * @Date 2021/07/15
 */
public interface ServerDao extends MongoRepository<ServerTable,String> {

//    List<ServerTable> findAll();
    ServerTable findByIp(String ip);

}
