package com.genug.murmur.api.exception;


import lombok.Getter;

@Getter
public class InvalidRequestException extends ApiException {

    private static final String MESSAGE = "잘못된 요청입니다.";

    private String fieldName;
    private String message;

    public InvalidRequestException() {
        super(MESSAGE);
    }

    public InvalidRequestException(Throwable cause) {
        super(MESSAGE, cause);
    }

    public InvalidRequestException(String message, String fieldName) {
        super(MESSAGE);
        addValidation(message, fieldName);
    }

    @Override
    public int getStatusCode() {
        return 400; // bad request
    }
}
