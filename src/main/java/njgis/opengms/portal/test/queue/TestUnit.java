package njgis.opengms.portal.test.queue;

import com.mongodb.client.result.UpdateResult;
import njgis.opengms.portal.test.queue.dao.QueueDao;
import njgis.opengms.portal.test.queue.dao.ServerDao;
import njgis.opengms.portal.test.queue.dao.SubmittedTaskDao;
import njgis.opengms.portal.test.queue.entity.ServerInfo;
import njgis.opengms.portal.test.queue.entity.SubmittedTask;
import njgis.opengms.portal.test.queue.entity.TaskQueue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

/**
 * @Description description
 * @Author bin
 * @Date 2021/07/15
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestUnit {

    @Autowired
    ServerDao serverDao;

    @Autowired
    SubmittedTaskDao submittedTaskDao;

    // @Autowired
    // private InvokeService invokeService;

    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    public void queryTest(){
        System.out.println(serverDao.findAll());
    }

    @Test
    public void uuid(){
        System.out.println(UUID.randomUUID().toString());
    }


    @Test
    public void taskTest(){

        SubmittedTask taskFinished = (SubmittedTask) submittedTaskDao.findByTaskId("d80e2a53-23fd-4d9a-b857-fa0fabcfe409");
        String taskId = taskFinished.getTaskId();
        System.out.println(taskFinished);
        System.out.println(taskId);
    }

    @Test
    public void updateTest(){

        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setIp("127.0.0.1");
        serverInfo.setPort("8060");
        serverInfo.setCurrentRun(0);
        serverInfo.setMaxRun(5);
        serverDao.save(serverInfo);
        // List<ServerTable> all = serverDao.findAll();
        // all.get(0).setPort("8060");
        // serverDao.save(all.get(0));
        // List<Integer> arrays = new ArrayList<>();
        // arrays.add(1);
        // arrays.add(2);
        // System.out.println(arrays.remove(0));

        // TaskTable taskTable = taskDao.findFirstByStatus(1);
        // System.out.println(taskTable);

    }


    @Test
    public void queryTask(){
        SubmittedTask execTask = submittedTaskDao.findFirstByStatus(0);
        System.out.println(execTask);
    }

    @Autowired
    QueueDao queueDao;

    @Test
    public void idTest(){
        // TaskQueue taskQueue = new TaskQueue();
        List<TaskQueue> all = queueDao.findAll();
        all.get(0).setName("bin");
        // taskQueue.setTaskId("12312312");
        queueDao.save( all.get(0));
    }

    @Test
    public void mongoTemplateTest(){
        Query query = Query.query(Criteria.where("taskId").is("12312312"));
        Update update = new Update();
        update.set("taskId","33331212");
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, TaskQueue.class);
        System.out.println(updateResult);
    }

}
