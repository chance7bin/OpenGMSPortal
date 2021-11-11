package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description
 * @Author bin
 * @Date 2021/11/09
 */
public interface TaskDao extends MongoRepository<Task, String> {
    Task findFirstByTaskId(String taskId);

    Page<Task> findAllByEmailAndStatus(String email, int status, Pageable pageable);

    Page<Task> findAllByEmail(String email,Pageable pageable);

    Task findFirstByTaskIdAndId(String taskId, String id);

    // List<Task> findAllByEmailAndRunTimeGreaterThanEqual(String email, Date date);
}
