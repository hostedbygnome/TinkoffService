package ru.urfu.tinkoffservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import ru.tinkoff.piapi.core.InvestApi;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableConfigurationProperties(ApiConfig.class)
@RequiredArgsConstructor
public class ApplicationConfig implements AsyncConfigurer {
    private final ApiConfig apiConfig;

    @Bean
    public InvestApi api() {
        String ssoToken = System.getenv("ssoToken");
        return InvestApi.create(ssoToken);
    }
    @Bean(name = "threadPoolTaskExecutor")
    public ExecutorService threadPoolTaskExecutor() {
        ExecutorService executor = Executors.newFixedThreadPool(8);
//        executor.
//        executor.setMaxPoolSize(1);
//        executor.setThreadGroupName("Async-");
//        executor.initialize();
        return executor;
    }
}
