package goorm.back.zo6.qr.application;

import org.springframework.stereotype.Component;

@Component
class QRContentBuilder {
    public String build(Long conferenceId, Long sessionId, String url) {
        return url + "?conferenceId=" + conferenceId + "&sessionId=" + sessionId;
    }
}
