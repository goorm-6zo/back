package goorm.back.zo6.notice.domain;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;

public enum NoticeTarget {
    ALL, ATTENDEE, NON_ATTENDEE;

    public static NoticeTarget from(String target) {
        try {
            return NoticeTarget.valueOf(target.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.TARGET_ERROR);
        }
    }
}
