package goorm.back.zo6.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // User Error
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),
    USER_ALREADY_EXISTS(HttpStatus.NOT_FOUND, "이미 존재하는 유저입니다."),

    // Login Error
    USER_NOT_MATCH_LOGIN_INFO(HttpStatus.BAD_REQUEST, "로그인 정보에 해당하는 유저가 존재하지 않습니다."),

    // Role Error
    ROLE_INVALID(HttpStatus.BAD_REQUEST, "유효하지 않은 Role 입니다."),

    // Invalidation Error
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INVALID_JSON_FORMAT(HttpStatus.BAD_REQUEST, "요청한 JSON 형식이 올바르지 않습니다."),

    // JWT Error
    WRONG_TYPE_TOKEN(HttpStatus.UNAUTHORIZED,"토큰의 서명이 유효하지 않습니다."),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED,"잘못된 형식의 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED,"만료된 토큰입니다."),
    UNKNOWN_TOKEN_ERROR(HttpStatus.BAD_REQUEST,"토큰의 값이 존재하지 않습니다."),
    MISSING_TOKEN(HttpStatus.BAD_REQUEST, "토큰이 존재하지 않습니다."),

    // Conference Error
    CONFERENCE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 컨퍼런스입니다."),

    // Session Error
    SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 세션입니다."),
    ;

    private final HttpStatus status;
    private final String message;

}
