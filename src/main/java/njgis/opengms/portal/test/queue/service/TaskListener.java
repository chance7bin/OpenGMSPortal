package njgis.opengms.portal.test.queue.service;

import njgis.opengms.portal.test.queue.dao.RunTaskDao;
import njgis.opengms.portal.test.queue.dao.ServerDao;
import njgis.opengms.portal.test.queue.dao.SubmitedTaskDao;
import njgis.opengms.portal.test.queue.entity.RunTask;
import njgis.opengms.portal.test.queue.entity.ServerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

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
    private SubmitedTaskDao submitedTaskDao;

    @Autowired
    private ServerDao serverDao;

    @Autowired
    private RunTaskDao runTaskDao;

    @Autowired
    private RunService runService;

    // @PostConstruct
    @Async
    public void taskListener() throws IOException {
        System.out.println("[          Thread] -- TaskListener -- start ");
        // 实时请求，查看待运行任务
        while (true){
            RunTask runTask = runTaskDao.findFirstByStatus(0);
            if (runTask != null){
                // 查看空闲服务器
                String runServerIp = serverListener.getIdleServer();
                if (runServerIp != null){
                    // 执行任务队列里的第一个未执行任务
                    ServerInfo server = serverDao.findByIp(runServerIp);
                    String runServerPort = server.getPort();
                    runTask.setIp(runServerIp);
                    runTask.setPort(runServerPort);

                    runService.run(runTask);
                    runTask.setStatus(1);
                    runTaskDao.save(runTask);
                }
            }
            // 更新正在运行的模型任务
            runService.runningListener();
            try {
                Thread.sleep(1000); //设置暂停的时间 1 秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
