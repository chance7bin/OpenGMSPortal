package njgis.opengms.portal.test.queue;

import njgis.opengms.portal.test.queue.dao.ServerDao;
import njgis.opengms.portal.test.queue.dao.TaskDao;
import njgis.opengms.portal.test.queue.entity.ServerTable;
import njgis.opengms.portal.test.queue.entity.TaskTable;
import njgis.opengms.portal.test.queue.service.InvokeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
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
    private ServerDao serverDao;

    @Autowired
    private TaskDao taskDao;

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

        TaskTable taskFinished = (TaskTable) taskDao.findByTaskId("d80e2a53-23fd-4d9a-b857-fa0fabcfe409");
        String taskId = taskFinished.getTaskId();
        System.out.println(taskFinished);
        System.out.println(taskId);
    }

    @Test
    public void updateTest(){

        ServerTable serverTable = new ServerTable();
        serverTable.setIp("127.0.0.1");
        serverTable.setPort("8060");
        serverTable.setCurrentRun(0);
        serverTable.setMaxRun(5);
        serverDao.save(serverTable);
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
        TaskTable execTask = taskDao.findFirstByStatus(0);
        System.out.println(execTask);
    }

    @Test
    public void thread(){
        ExecutorService service = Executors.newFixedThreadPool(10);
        service.execute(new Thread1());
        service.execute(new Thread2());
        service.execute(new Thread3());
        service.execute(new Thread4());
        service.execute(new Thread5());
    }



}

class Thread1 implements Runnable{
    InvokeService is = new InvokeService();
    @Override
    public void run() {
        is.invoking();
    }
}
class Thread2 implements Runnable{
    InvokeService is = new InvokeService();
    @Override
    public void run() {
        is.invoking();
    }
}
class Thread3 implements Runnable{
    InvokeService is = new InvokeService();
    @Override
    public void run() {
        is.invoking();
    }
}
class Thread4 implements Runnable{
    InvokeService is = new InvokeService();
    @Override
    public void run() {
        is.invoking();
    }
}
class Thread5 implements Runnable{
    InvokeService is = new InvokeService();
    @Override
    public void run() {
        is.invoking();
    }
}


