package com.ayoza.feline;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableAsync
@Slf4j
public class SpringAsyncConfig implements AsyncConfigurer {

	@Override
	public Executor getAsyncExecutor() {
		return new ThreadPoolTaskExecutor();
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new CustomAsyncExceptionHandler();
	}
	
	public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
	 
	    @Override
	    public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {
	    	log.error("Method {} with parameters {} threw Exception {}", method.getName(), obj, throwable.getMessage());
	    }
	}
}
