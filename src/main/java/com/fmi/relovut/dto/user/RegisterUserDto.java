package com.fmi.relovut.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sun.istack.internal.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterUserDto {
    @NotNull
    @Email
    private String email;

    @NotNull
    @Length(min = 8, max = 64)
    private String password;

    @NotNull
    private String fullname;

    @NotNull
    private String currencyIsoName;
}
