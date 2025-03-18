package goorm.back.zo6.notice.dto;


public record NoticeRequest(
        String message,
        String noticeTarget
) { }
