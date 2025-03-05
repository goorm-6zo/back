package goorm.back.zo6.conference.domain;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@Getter
public class ConferenceId implements Serializable {

    private Long value;

    protected ConferenceId() {}

    public ConferenceId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("value must be greater than zero");
        }
        this.value = value;
    }
}