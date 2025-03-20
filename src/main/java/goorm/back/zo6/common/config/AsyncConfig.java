package goorm.back.zo6.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "customTaskExecutor")
    public ThreadPoolTaskExecutor customTaskExecutor() {
        int processors = Runtime.getRuntime().availableProcessors();
        int corePoolSize = Math.max(2, processors);
        int maxPoolSize = Math.max(4, processors * 3); // Ec2 프리티어 코어와 논리 코어는 둘다 1코어, 최소한의 성능 유지
        int queueCapacity = 50;
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);  // 기본 쓰레드 수
        executor.setMaxPoolSize(maxPoolSize);   // 최대 쓰레드 수
        executor.setQueueCapacity(queueCapacity); // 요청 대기 수
        executor.setThreadNamePrefix("event-Async-");
        executor.initialize();
        return executor;
    }
}
