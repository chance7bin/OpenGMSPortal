package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/11/09
 */
public interface TaskDao extends MongoRepository<Task, String> {
    Task findFirstByTaskId(String taskId);

    Task findFirstById(String id);

    Page<Task> findAllByEmailAndStatus(String email, int status, Pageable pageable);

    Page<Task> findAllByEmailAndStatusAndComputableId(String email, int status, String computableId,Pageable pageable);

    List<Task> findAllByEmailAndStatus(String email, int status);

    Page<Task> findAllByEmail(String email,Pageable pageable);

    Task findFirstByTaskIdAndId(String taskId, String id);

    // List<Task> findAllByEmailAndRunTimeGreaterThanEqual(String email, Date date);

    Page<Task> findByComputableIdAndPermissionAndStatus(String modelId,String permission,int status,Pageable pageable);

    Page<Task> findByComputableIdAndEmailAndStatus(String modelId,String email,int status,Pageable pageable);

    Page<Task> findByComputableNameContainsIgnoreCaseAndEmail(String name,String author,Pageable pageable);

    List<Task> findAllByTaskIdIn(List<String> taskIds);

    List<Task> findAllByComputableId(String computableModelId);

    List<Task> findAllByComputableIdAndPermission(String computableModelId, String permission);

    List<Task> findAllByComputableIdAndRunTimeGreaterThanEqual(String oid, Date date);


}
