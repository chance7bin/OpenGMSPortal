package njgis.opengms.portal.test.queue.service;

import com.sun.xml.bind.v2.TODO;
import njgis.opengms.portal.test.queue.dao.ServerDao;
import njgis.opengms.portal.test.queue.dao.TaskDao;
import njgis.opengms.portal.test.queue.entity.ServerTable;
import njgis.opengms.portal.test.queue.entity.TaskTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @Description description
 * @Author bin
 * @Date 2021/07/16
 */
@Service
public class TaskListener{

    @Autowired
    ServerService serverListener;

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private ServerDao serverDao;

    @Autowired
    private RunService runService;

    // @PostConstruct
    @Async
    public void taskListener(){
        System.out.println("[          Thread] -- TaskListener -- start ");
        // 实时请求，查看待运行任务
        while (true){
            TaskTable execTask = taskDao.findFirstByStatus(0);
            if (execTask != null){
                // 查看空闲服务器
                String runServerIp = serverListener.getIdleServer();
                if (runServerIp != null){
                    // 执行任务队列里的第一个未执行任务
                    ServerTable server = serverDao.findByIp(runServerIp);
                    String runServerPort = server.getPort();
                    System.out.println("[          Task Running] -- taskId : " + execTask.getTaskId());
                    execTask.setIp(runServerIp);
                    execTask.setPort(runServerPort);
                    // TODO 更新状态放到模型容器返回msrid的时候更新
                    execTask.setStatus(1);
                    taskDao.save(execTask);
                    runService.run(execTask);
                }
            }
            runService.runningListener();
            try {
                Thread.sleep(1000); //设置暂停的时间 1 秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
