package com.example.authservice.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),

    // 11xx
    INVALID_KEY(1100, "Invalid uncategorized error", HttpStatus.BAD_REQUEST),
    INVALID_JSON(1101, "Json invalid", HttpStatus.BAD_REQUEST),
    OTP_EXPIRED(1102, "Otp expired", HttpStatus.BAD_REQUEST),
    INVALID_OTP(1103, "Invalid Otp", HttpStatus.BAD_REQUEST),
    INVALID_LOGIN(1105, "Invalid Login", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1106, "Invalid Password", HttpStatus.BAD_REQUEST),
    INVALID_STATUS(1118, "Invalid Status", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST(1119, "Invalid Request", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_CHANGE_ROLE(1120, "No right to change ", HttpStatus.UNAUTHORIZED),
    INTERNAL_SERVER_ERROR(1121, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    // 12xx
    UNAUTHENTICATED(1201, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    NOT_FOUND_USER(1202, "User not found", HttpStatus.NOT_FOUND),
    USER_BLOCKED(1203, "User blocked", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1204, "User has existed", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_FOUND(1205, "Email not found", HttpStatus.NOT_FOUND),
    EMAIL_EXISTED(1206, "Email has existed", HttpStatus.BAD_REQUEST),
    USER_DELETED(1207, "User has deleted", HttpStatus.BAD_REQUEST)

    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
