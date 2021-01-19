package com.fmi.relovut.controllers;

import com.fmi.relovut.dto.user.RegisterUserDto;
import com.fmi.relovut.models.User;
import com.fmi.relovut.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

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

}
