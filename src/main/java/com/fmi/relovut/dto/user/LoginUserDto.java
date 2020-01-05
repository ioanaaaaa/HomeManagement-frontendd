package com.fmi.relovut.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sun.istack.NotNull;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginUserDto {
    @NotNull
    public String email;

    @NotNull
    public String password;
}
