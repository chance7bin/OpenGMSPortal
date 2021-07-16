package njgis.opengms.portal.test.queue.dao;

import njgis.opengms.portal.test.queue.entity.TaskTable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description description
 * @Author bin
 * @Date 2021/07/15
 */
public interface TaskDao extends MongoRepository<TaskTable, String> {
    TaskTable findByTaskId(String taskId);

    TaskTable findFirstByStatus(int status);

    // TaskTable save(TaskTable task);
    //
    // TaskTable save(TaskTable task, String taskId);
}
