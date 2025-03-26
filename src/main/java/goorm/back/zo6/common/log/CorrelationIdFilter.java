package goorm.back.zo6.common.log;

import jakarta.servlet.*;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // 가장 먼저 실행
public class CorrelationIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String correlationId = UUID.randomUUID().toString(); // 요청마다 고유한  id 생성
        MDC.put("CORRELATION_ID",correlationId);
        chain.doFilter(request, response);
        MDC.clear();
    }
}
