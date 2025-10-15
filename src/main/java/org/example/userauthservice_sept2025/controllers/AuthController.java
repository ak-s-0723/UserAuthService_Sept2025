package org.example.userauthservice_sept2025.controllers;

import org.example.userauthservice_sept2025.dtos.LoginRequestDto;
import org.example.userauthservice_sept2025.dtos.SignupRequestDto;
import org.example.userauthservice_sept2025.dtos.UserDto;
import org.example.userauthservice_sept2025.exceptions.PasswordMismatchException;
import org.example.userauthservice_sept2025.exceptions.UserExistException;
import org.example.userauthservice_sept2025.exceptions.UserNotRegisteredException;
import org.example.userauthservice_sept2025.models.User;
import org.example.userauthservice_sept2025.services.AuthService;
import org.example.userauthservice_sept2025.services.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignupRequestDto signupRequestDto) {
        try {
            User user = authService.signup(signupRequestDto.getEmail(), signupRequestDto.getPassword(), signupRequestDto.getName(), signupRequestDto.getPhoneNumber());
            return new ResponseEntity<>(from(user),HttpStatus.CREATED);
        }catch (UserExistException exception) {
             return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto loginRequestDto) {
       try {
           User user = authService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
           return new ResponseEntity<>(from(user),HttpStatus.OK);
       }catch (PasswordMismatchException exception) {
           return new ResponseEntity<>(null,HttpStatus.UNAUTHORIZED);
       }catch (UserNotRegisteredException exception1) {
           return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
       }
    }

    private UserDto from(User user) {
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        return userDto;
    }
}
