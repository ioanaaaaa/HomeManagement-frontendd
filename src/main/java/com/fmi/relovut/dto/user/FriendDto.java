package com.fmi.relovut.dto.user;

import com.fmi.relovut.models.User;
import lombok.Data;

@Data
public class FriendDto {
    public FriendDto(User user) {
        this.accountId = user.getAccount().getId().toString();
        this.email = user.getEmail();
        this.fullname = user.getFullname();
    }

    private final String accountId;
    private final String email;
    private final String fullname;
}
