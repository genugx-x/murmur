package com.genug.murmur.api.exception;

public class LoginFailException extends ApiException {

    private static final String MESSAGE = "로그인에 실패하였습니다.\n 아이디 또는 비밀번호 확인 후 다시 시도해주세요.";

    public LoginFailException() {
        super(MESSAGE);
    }

    public LoginFailException(Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public int getStatusCode() {
        return 401;
    }


}
