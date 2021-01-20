package com.fmi.relovut.services;

import com.fmi.relovut.config.security.ApiJWTAuthenticationFilter;
import com.fmi.relovut.dto.user.RegisterUserDto;
import com.fmi.relovut.models.User;
import com.fmi.relovut.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Service
public class UserService {
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserRepository userRepository;

    @Autowired
    public UserService(BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public String registerUser(RegisterUserDto registerUserDto) {
        User existingUser = userRepository.findByEmail(registerUserDto.getEmail());
        if (existingUser != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A user with that email already exists!");

        if (registerUserDto.getPassword().length() < 8)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password must be at least 8 characters long!");

        User newUser = new User()
                .setEmail(registerUserDto.getEmail())
                .setPassword(bCryptPasswordEncoder.encode(registerUserDto.getPassword()))
                .setFullname(registerUserDto.getFullname());
//
//        emailService.sendRegisterEmail(newUser.getEmail());

        userRepository.save(newUser);
//
        return ApiJWTAuthenticationFilter.generateJwtToken(newUser.getEmail());
    }

    public Set<User> findUsersByIds(Set<Long> userIds){
        return userRepository.findByIds(userIds);
    }

}
