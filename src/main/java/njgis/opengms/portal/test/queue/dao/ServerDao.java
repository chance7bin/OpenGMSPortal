package njgis.opengms.portal.test.queue.dao;

import njgis.opengms.portal.test.queue.entity.ServerInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description description
 * @Author bin
 * @Date 2021/07/15
 */
public interface ServerDao extends MongoRepository<ServerInfo,String> {

//    List<ServerTable> findAll();
    ServerInfo findByIp(String ip);

}
