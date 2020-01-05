package com.fmi.relovut.controllers;

import com.fmi.relovut.dto.account.AccountDetailsDto;
import com.fmi.relovut.dto.user.AddFriendDto;
import com.fmi.relovut.dto.user.FriendDto;
import com.fmi.relovut.dto.user.RegisterUserDto;
import com.fmi.relovut.dto.user.SearchFriendsDto;
import com.fmi.relovut.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String register(@Validated @RequestBody RegisterUserDto registerUserDto) {
        return userService.registerUser(registerUserDto);
    }

    @GetMapping("/current")
    public String getCurrentUser(Principal principal) {
        return principal.getName();
    }

    @GetMapping("/myAccount")
    public AccountDetailsDto getAccountDetails(Principal principal) {
        return userService.getAccountDetails(principal.getName());
    }

    @GetMapping("/myFriends")
    public List<FriendDto> getMyFriends(Principal principal) {
        return userService.getFriends(principal.getName());
    }

    @PostMapping("/searchFriends")
    public List<FriendDto> searchUsersByName(Principal principal, @RequestBody SearchFriendsDto searchFriendsDto) {
        return userService.searchUsers(principal.getName(), searchFriendsDto.email, searchFriendsDto.fullname);
    }

    @PostMapping("/addFriend")
    public void addFriend(Principal principal, @Validated @RequestBody AddFriendDto addFriendDto) {
        userService.addFriend(principal.getName(), addFriendDto.getEmail());
    }

    @PostMapping("/removeFriend")
    public void removeFriend(Principal principal, @Validated @RequestBody AddFriendDto removeFriendDto) {
        userService.removeFriend(principal.getName(), removeFriendDto.getEmail());
    }
}
