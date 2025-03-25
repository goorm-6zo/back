package goorm.back.zo6.common.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;

@Configuration
@EnableAsync
public class AsyncConfig {
    private static final TaskDecorator MDC_TASK_DECORATOR = runnable -> {
        Map<String, String> contextMap = MDC.getCopyOfContextMap(); // 현재 쓰레드의 MDC 를 복사.
        return () -> {
            MDC.setContextMap(contextMap); // 새 쓰레드에 복사한 MDC 설정
            runnable.run(); // 로직 실행
        };
    };

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
        executor.setTaskDecorator(MDC_TASK_DECORATOR);
        executor.initialize();
        return executor;
    }
}
