package njgis.opengms.portal.test.queue.dao;

import njgis.opengms.portal.test.queue.entity.RunTask;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description description
 * @Author bin
 * @Date 2021/07/16
 */
public interface RunningDao extends MongoRepository<RunTask,String> {

}
