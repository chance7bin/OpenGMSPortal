package njgis.opengms.portal.test.queue.dao;

import njgis.opengms.portal.test.queue.entity.RunTask;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @ClassName RunTaskDao.java
 * @Author wzh
 * @Version 1.0.0
 * @Description
 * @CreateDate(Y/M/D-H:M:S) 2021/07/16/ 22:20:00
 */
public interface RunTaskDao extends MongoRepository<RunTask,String> {
    RunTask findFirstById(String id);

    RunTask findFirstByMsrid(String msrid);

    RunTask findFirstByStatus(int status);

    List<RunTask> findAllByStatus(int status);

    List<RunTask> findAllByMd5(String md5);



}
