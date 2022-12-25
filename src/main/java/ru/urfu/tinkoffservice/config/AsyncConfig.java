package ru.urfu.tinkoffservice.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AsyncConfig implements AsyncConfigurer {
//    @Override
//    public Executor getAsyncExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setMaxPoolSize(2);
//        executor.setThreadNamePrefix("Async-");
//        executor.initialize();
//        return executor;
//    }
//    @Override
//    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
//        return AsyncConfigurer.super.getAsyncUncaughtExceptionHandler();
//    }
//    @Bean("asyncExecutor")
//    public Executor getAsyncExecutor() {
//        return Executors.get();
//    }
}
