package goorm.back.zo6.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.http11.Http11InputBuffer;
import org.apache.tomcat.util.http.parser.HttpParser;
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

    // S3 Error
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패하였습니다."),
    FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패하였습니다."),
    UNSUPPORTED_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "허용되지 않는 파일 확장자 입니다."),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "파일 이름이 없거나 확장자가 없습니다."),
    PRESIGNED_URL_GENERATION_FAILED(HttpStatus.BAD_REQUEST, "이미지 조회 PresignedUrl 을 생성할 수 없습니다."),

    // Rekognition Error
    REKOGNITION_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Rekognition Collection 에서 이미지 업로드를 실패하였습니다."),
    REKOGNITION_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Rekognition Collection 에서 이미지 삭제를 실패하였습니다."),
    REKOGNITION_NO_MATCH_FOUND(HttpStatus.BAD_REQUEST, "Rekognition 얼굴 매칭에 실패하였습니다."),
    REKOGNITION_API_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR,"Rekognition Api 호출 시 문제가 발생하였습니다."),
    REKOGNITION_CREATE_COLLECTION_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "Rekognition Collection 생성 실패"),
    // Face Error
    FACE_UPLOAD_FAIL(HttpStatus.BAD_REQUEST,"유저 얼굴 이미지 저장 실패하였습니다."),
    FACE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저의 얼굴 이미지 저장 정보를 조회할 수 없습니다."),

    // Conference Error

    CONFERENCE_NOT_FOUNT(HttpStatus.NOT_FOUND, "존재하지 않는 컨퍼런스입니다."),

    // Session Error
    SESSION_NOT_FOUNT(HttpStatus.NOT_FOUND, "존재하지 않는 세션입니다."),

    // File Exception
    FILE_CONVERSION_EXCEPTION(HttpStatus.BAD_REQUEST, "파일 변환 중에 에러가 발생했습니다."),

    CONFERENCE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 컨퍼런스입니다."),

    // Session Error
    SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 세션입니다."),

    // Reservation Error
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 예약입니다."),
  
    // Event Error
    PAYLOAD_CONVERT_ERROR(HttpStatus.BAD_REQUEST, "이벤트 내용 변환 중 에러 발생"),

    // Attendance Error
    MISSING_REQUIRED_PARAMETER(HttpStatus.BAD_REQUEST, "conferenceId 또는 sessionId 가 필요합니다."),

    // Encryption Error
    ENCRYPT_CIPHER_EXCEPTION(HttpStatus.BAD_REQUEST, "암호화 과정에서 에러가 발생했습니다."),
    DECRYPT_CIPHER_EXCEPTION(HttpStatus.BAD_REQUEST,"복호화 과정에서 에러가 발생했습니다."),
    ;

    private final HttpStatus status;
    private final String message;

}
