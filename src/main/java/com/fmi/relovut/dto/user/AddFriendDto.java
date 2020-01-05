package com.fmi.relovut.dto.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class AddFriendDto {
    @Email
    @NotNull
    public String email;
}
