package njgis.opengms.portal.test.queue.dao;

import njgis.opengms.portal.test.queue.entity.SubmittedTask;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description description
 * @Author bin
 * @Date 2021/07/15
 */
public interface SubmittedTaskDao extends MongoRepository<SubmittedTask, String> {
    SubmittedTask findByTaskId(String taskId);

    SubmittedTask findFirstByTaskId(String taskId);

    SubmittedTask findFirstByStatus(int status);

    // TaskTable save(TaskTable task);
    //
    // TaskTable save(TaskTable task, String taskId);
}
