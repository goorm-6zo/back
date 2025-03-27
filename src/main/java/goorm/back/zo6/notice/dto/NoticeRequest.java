package goorm.back.zo6.notice.dto;


import jakarta.validation.constraints.NotBlank;

public record NoticeRequest(
        @NotBlank
        String message,
        @NotBlank
        String noticeTarget
) { }
