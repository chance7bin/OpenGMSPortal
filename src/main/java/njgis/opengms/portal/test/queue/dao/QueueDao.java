package njgis.opengms.portal.test.queue.dao;

import njgis.opengms.portal.test.queue.entity.TaskQueue;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description description
 * @Author bin
 * @Date 2021/07/15
 */
public interface QueueDao extends MongoRepository<TaskQueue,String> {

    // TaskQueue findFirstBy

}
