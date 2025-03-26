package goorm.back.zo6.common.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class ExcludeErrorFilter extends Filter<ILoggingEvent> {

    @Override
    public FilterReply decide(ILoggingEvent event) {
        // ERROR 레벨만 필터링
        if (event.getLevel().isGreaterOrEqual(Level.ERROR)) {
            String message = event.getFormattedMessage();
            if (message != null && message.contains("400")) {
                return FilterReply.DENY; // 400 에러는 Discord 전송 안 함
            }
            return FilterReply.ACCEPT;
        }
        return FilterReply.DENY; // ERROR 이외 로그는 무시
    }
}
