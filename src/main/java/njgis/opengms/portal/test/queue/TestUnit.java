package njgis.opengms.portal.test.queue;

import njgis.opengms.portal.test.queue.dao.QueueDao;
import njgis.opengms.portal.test.queue.dao.ServerDao;
import njgis.opengms.portal.test.queue.dao.SubmitedTaskDao;
import njgis.opengms.portal.test.queue.entity.ServerInfo;
import njgis.opengms.portal.test.queue.entity.SubmitedTask;
import njgis.opengms.portal.test.queue.entity.TaskQueue;
import njgis.opengms.portal.test.queue.service.InvokeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    SubmitedTaskDao submitedTaskDao;

    // @Autowired
    // private InvokeService invokeService;

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

        SubmitedTask taskFinished = (SubmitedTask) submitedTaskDao.findByTaskId("d80e2a53-23fd-4d9a-b857-fa0fabcfe409");
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
        SubmitedTask execTask = submitedTaskDao.findFirstByStatus(0);
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

}
