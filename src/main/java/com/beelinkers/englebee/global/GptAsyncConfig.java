package com.beelinkers.englebee.global;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class GptAsyncConfig {

  @Bean(name = "gptThreadPoolTask")
  public Executor gptThreadPoolTask() {
    ThreadPoolTaskExecutor poolExecutor = new ThreadPoolTaskExecutor();
    poolExecutor.setCorePoolSize(10);
    poolExecutor.setMaxPoolSize(100);
    poolExecutor.setQueueCapacity(100);
    poolExecutor.setThreadNamePrefix("Gpt-");
    return poolExecutor;
  }
}