package njgis.opengms.portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Description 线程配置
 * @Author bin
 * @Date 2021/10/11
 */
@Configuration
@EnableScheduling  //同步
@EnableAsync  //异步
public class ScheduleConfig {

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数 核心线程数线程数定义了最小可以同时运行的线程数量
        executor.setCorePoolSize(3);
        // 设置最大线程数 当队列中存放的任务达到队列容量的时候，当前可以同时运行的线程数量变为最大线程数
        executor.setMaxPoolSize(5);
        // 设置队列容量 当新任务来的时候会先判断当前运行的线程数量是否达到核心线程数，如果达到的话，信任就会被存放在队列中
        executor.setQueueCapacity(200);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(60);
        // 设置默认线程名称
        executor.setThreadNamePrefix("AsyncThread-");
        // 设置拒绝策略 当最大池被填满时，此策略为我们提供可伸缩队列
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }

}
