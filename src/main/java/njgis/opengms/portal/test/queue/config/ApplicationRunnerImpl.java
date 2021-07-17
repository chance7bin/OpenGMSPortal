package njgis.opengms.portal.test.queue.config;

import njgis.opengms.portal.test.queue.service.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @Description 项目启动后执行监听任务队列方法
 * @Author bin
 * @Date 2021/07/16
 */
@Component
public class ApplicationRunnerImpl implements ApplicationRunner {

    @Autowired
    TaskListener taskListener;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        taskListener.taskListener();
    }
}
