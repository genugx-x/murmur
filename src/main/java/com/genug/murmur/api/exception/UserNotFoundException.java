package com.genug.murmur.api.exception;

public class UserNotFoundException extends ApiException {

    private static final String MESSAGE = "존재하지 않는 계정입니다.";

    public UserNotFoundException() {
        super(MESSAGE);
    }

    public UserNotFoundException(Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }


}
