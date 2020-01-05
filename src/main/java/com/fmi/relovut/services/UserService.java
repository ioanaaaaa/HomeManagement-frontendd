package com.fmi.relovut.services;

import com.fmi.relovut.config.security.ApiJWTAuthenticationFilter;
import com.fmi.relovut.dto.account.AccountDetailsDto;
import com.fmi.relovut.dto.account.AccountDto;
import com.fmi.relovut.dto.user.FriendDto;
import com.fmi.relovut.dto.user.RegisterUserDto;
import com.fmi.relovut.dto.user.UserDto;
import com.fmi.relovut.models.Account;
import com.fmi.relovut.models.Currency;
import com.fmi.relovut.models.User;
import com.fmi.relovut.repositories.AccountRepository;
import com.fmi.relovut.repositories.CurrencyRepository;
import com.fmi.relovut.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
public class UserService {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private EmailService emailService;

    public User getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<FriendDto> getFriends(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        return user.getFriends().stream().map(FriendDto::new).collect(Collectors.toList());
    }

    public List<FriendDto> searchUsers(String userEmail, String email, String fullname) {
        User user = userRepository.findByEmail(userEmail);
        List<FriendDto> result = new ArrayList<>();

        if (fullname != null && fullname.length() > 0)
            userRepository.findTop100ByFullnameContaining(fullname).stream().map(FriendDto::new).forEach(result::add);

        if (email != null && email.length() > 0 && result.stream().map(FriendDto::getEmail).noneMatch(e -> e.equalsIgnoreCase(email))) {
            User searchUser = userRepository.findByEmail(email);
            if (searchUser != null)
                result.add(new FriendDto(searchUser));
        }

        List<String> existingFriendEmails = user.getFriends().stream().map(User::getEmail).collect(toList());
        result.removeIf(f -> existingFriendEmails.contains(f.getEmail()) || f.getEmail().equalsIgnoreCase(userEmail));
        return result;
    }

    public void addFriend(String userEmail, String friendEmail) {
        if (userEmail.equalsIgnoreCase(friendEmail))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot add yourself as a friend!");

        User user = userRepository.findByEmail(userEmail);
        User friend = userRepository.findByEmail(friendEmail);
        if (friend == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email address");

        List<String> existingFriendEmails = user.getFriends().stream().map(User::getEmail).collect(toList());
        if (existingFriendEmails.contains(friendEmail))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You already have added that user as a friend!");

        user.getFriends().add(friend);
        userRepository.save(user);
    }

    public void removeFriend(String userEmail, String friendEmail) {
        User user = userRepository.findByEmail(userEmail);
        boolean removedAny = user.getFriends().removeIf(f -> f.getEmail().equalsIgnoreCase(friendEmail));
        if (removedAny)
            userRepository.save(user);
    }

    public String registerUser(RegisterUserDto registerUserDto) {
        User existingUser = userRepository.findByEmail(registerUserDto.getEmail());
        if (existingUser != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A user with that email already exists!");

        if (registerUserDto.getPassword().length() < 8)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password must be at least 8 characters long!");

        Currency currency = currencyRepository.findByIsoName(registerUserDto.getCurrencyIsoName());
        if (currency == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid currency!");


        User newUser = new User()
                .setEmail(registerUserDto.getEmail())
                .setPassword(bCryptPasswordEncoder.encode(registerUserDto.getPassword()))
                .setFullname(registerUserDto.getFullname());
        Account newUserAccount = new Account()
                .setUser(newUser)
                .setAmount(0.0)
                .setCurrency(currency);

        accountRepository.save(newUserAccount);
        emailService.sendRegisterEmail(newUser.getEmail());

        return ApiJWTAuthenticationFilter.generateJwtToken(newUser.getEmail());
    }

    public AccountDetailsDto getAccountDetails(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        return new AccountDetailsDto(user.getAccount());
    }
}
