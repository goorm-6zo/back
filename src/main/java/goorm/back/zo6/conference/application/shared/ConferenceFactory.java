package goorm.back.zo6.conference.application.shared;

import goorm.back.zo6.conference.application.dto.ConferenceCreateRequest;
import goorm.back.zo6.conference.domain.Conference;
import org.springframework.stereotype.Component;

@Component
public class ConferenceFactory {

    public Conference createConference(ConferenceCreateRequest request) {

        String imageKey = parseS3ImageKeyFromUrl(request.imageUrl());

        return Conference.builder()
                .name(request.name())
                .description(request.description())
                .capacity(request.capacity())
                .location(request.location())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .imageKey(imageKey)
                .isActive(true)
                .hasSessions(request.hasSessions())
                .build();
    }

    private String parseS3ImageKeyFromUrl(String imageUrl) {
        if (imageUrl == null || !imageUrl.contains("/conference/images/")) {
            throw new IllegalArgumentException("Invalid image url");
        }
        return imageUrl.substring(imageUrl.indexOf("conference/images"));
    }
}
