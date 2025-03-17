package goorm.back.zo6.sse.infrastructure;

import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
public class SystemClockHolder implements ClockHolder{
    @Override
    public long mills() {
        return Clock.systemUTC().millis();
    }
}
