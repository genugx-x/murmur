package com.genug.murmur.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Login {

    // 이메일 입력 양식
    @NotBlank(message = "이메일을 입력하세요.")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "이메일 형식 올바르지 않습니다.")
    private String email;

    // 영문 대문자, 소문자, 숫자, 특수문자 포함 최소 8
    @NotBlank(message = "비밀번호를 입력하세요.")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+])[A-Za-z\\d!@#$%^&*()_+]{8,}$",
            message = "비밀번호 형식이 올바르지 않습니다.\n" +
                    "(영문 대소문자, 숫자, 특수문자로 이루어진 최소 8글자 이상입니다.)")
    private String password;

    @Builder
    public Login(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
