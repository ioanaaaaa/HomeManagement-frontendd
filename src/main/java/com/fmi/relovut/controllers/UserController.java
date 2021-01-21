package com.fmi.relovut.controllers;

import com.fmi.relovut.dto.user.RegisterUserDto;
import com.fmi.relovut.dto.user.UserDto;
import com.fmi.relovut.models.User;
import com.fmi.relovut.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Set;

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
    public User getCurrentUser(Principal principal) {
        return userService.getByEmail(principal.getName());
    }

    @PostMapping("")
    public List<UserDto> searchUsers(@RequestParam(value = "searchTerm", required = false) String searchTerm){
        return UserDto.toDtos(userService.searchUsers(searchTerm));
    }

}
