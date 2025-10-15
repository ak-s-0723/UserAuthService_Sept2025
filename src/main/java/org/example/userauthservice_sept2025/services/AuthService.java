package org.example.userauthservice_sept2025.services;

import org.example.userauthservice_sept2025.exceptions.PasswordMismatchException;
import org.example.userauthservice_sept2025.exceptions.UserExistException;
import org.example.userauthservice_sept2025.exceptions.UserNotRegisteredException;
import org.example.userauthservice_sept2025.models.User;
import org.example.userauthservice_sept2025.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService implements IAuthService {

    @Autowired
    private UserRepo userRepo;


    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public User signup(String email, String password,
                       String name, String phoneNumber) {
        Optional<User> userOptional = userRepo.findByEmail(email);

        if(userOptional.isPresent()) {
             throw new UserExistException("User with same email already found");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); //this should definitely not be stored as plain text
        user.setName(name);
        user.setPhoneNumber(phoneNumber);
        return userRepo.save(user);
    }

    @Override
    public User login(String email, String password) {
        Optional<User> userOptional = userRepo.findByEmail(email);
        if(userOptional.isEmpty()) {
            throw new UserNotRegisteredException("Please signup first");
        }

        User user = userOptional.get();

        if(!passwordEncoder.matches(password,user.getPassword())) {
            throw new PasswordMismatchException("Incorrect password passed");
        }

        //Generate JWT

        return user;
    }
}
