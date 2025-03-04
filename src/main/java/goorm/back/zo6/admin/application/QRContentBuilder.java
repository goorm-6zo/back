package goorm.back.zo6.admin.application;

import org.springframework.stereotype.Component;

@Component
public class QRContentBuilder {
    public String build(Long conferenceId, Long sectionId, String url) {
        return "conferenceId=" + conferenceId + "&sectionId=" + sectionId + "&url=" + url;
    }
}
