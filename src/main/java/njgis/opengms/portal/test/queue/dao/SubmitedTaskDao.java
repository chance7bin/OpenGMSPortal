package njgis.opengms.portal.test.queue.dao;

import njgis.opengms.portal.test.queue.entity.SubmitedTask;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description description
 * @Author bin
 * @Date 2021/07/15
 */
public interface SubmitedTaskDao extends MongoRepository<SubmitedTask, String> {
    SubmitedTask findByTaskId(String taskId);

    SubmitedTask findFirstByTaskId(String taskId);

    SubmitedTask findFirstByStatus(int status);

    // TaskTable save(TaskTable task);
    //
    // TaskTable save(TaskTable task, String taskId);
}
