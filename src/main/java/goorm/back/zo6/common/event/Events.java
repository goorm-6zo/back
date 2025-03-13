package goorm.back.zo6.common.event;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;

@Log4j2
public class Events {
    private static ApplicationEventPublisher publisher;

    static void setPublisher(ApplicationEventPublisher publisher){
        Events.publisher = publisher;
    }

    public static void raise(Object event){
        if(publisher != null){
            log.info("얼굴 인증 시작" + event);
            publisher.publishEvent(event);
        }
    }
}
