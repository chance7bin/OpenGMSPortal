package njgis.opengms.portal.test.queue.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Description description
 * @Author bin
 * @Date 2021/07/16
 */
@Configuration
@EnableAsync
public class ThreadConfig implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);//核心线程数目
        // executor.setMaxPoolSize(Integer.MAX_VALUE);//最大线程数目
        executor.setMaxPoolSize(100);//最大线程数目
        executor.setQueueCapacity(20);//队列中最大的数目
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
