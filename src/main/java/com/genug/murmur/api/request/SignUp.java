package com.genug.murmur.api.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUp {

    private String email;
    private String password;
    private String nickNam;

    @Builder
    public SignUp(String email, String password, String nickNam) {
        this.email = email;
        this.password = password;
        this.nickNam = nickNam;
    }
}
