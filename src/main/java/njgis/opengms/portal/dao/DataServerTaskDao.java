package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.DataServerTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Author mingyuan
 * @Date 2020.01.06 14:18
 */
public interface DataServerTaskDao extends MongoRepository<DataServerTask, String> {
    // DataServerTask findFirstByOid(String oid);

    Page<DataServerTask> findAllByEmail(String email, Pageable pageable);
    Page<DataServerTask> findAllByEmailAndStatus(String email, int status, Pageable pageable);
    Page<DataServerTask> findAllByEmailAndServiceNameLike(String email, String serviceName, Pageable pageable);
    Page<DataServerTask> findAllByEmailAndStatusAndServiceNameLike(String email, int status, String serviceName, Pageable pageable);

    DataServerTask findFirstById(String id);

}
