package org.example.userauthservice_sept2025.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.antlr.v4.runtime.misc.Pair;
import org.example.userauthservice_sept2025.exceptions.PasswordMismatchException;
import org.example.userauthservice_sept2025.exceptions.UserExistException;
import org.example.userauthservice_sept2025.exceptions.UserNotRegisteredException;
import org.example.userauthservice_sept2025.models.Role;
import org.example.userauthservice_sept2025.models.Session;
import org.example.userauthservice_sept2025.models.Status;
import org.example.userauthservice_sept2025.models.User;
import org.example.userauthservice_sept2025.repos.SessionRepo;
import org.example.userauthservice_sept2025.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService implements IAuthService {

    @Autowired
    private UserRepo userRepo;


    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private SessionRepo sessionRepo;

    @Autowired
    private SecretKey secretKey;

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
    public Pair<User,String> login(String email, String password) {
        Optional<User> userOptional = userRepo.findByEmail(email);
        if(userOptional.isEmpty()) {
            throw new UserNotRegisteredException("Please signup first");
        }

        User user = userOptional.get();

        if(!passwordEncoder.matches(password,user.getPassword())) {
            throw new PasswordMismatchException("Incorrect password passed");
        }

        //Generate JWT

//        String message = "{\n" +
//                "   \"email\": \"anurag@gmail.com\",\n" +
//                "   \"roles\": [\n" +
//                "      \"instructor\",\n" +
//                "      \"buddy\"\n" +
//                "   ],\n" +
//                "   \"expirationDate\": \"2ndApril2026\"\n" +
//                "}";
//
//        byte[] content = message.getBytes(StandardCharsets.UTF_8);

        //For 2 session limit
        //We will check for number of sessions for same user
        //and based on that, we can make decision of creating a new token
        //or returning false

        Map<String,Object> claims = new HashMap<>();
        claims.put("user_id",user.getId());
        claims.put("issued_by","scaler");
        Long currentTime = System.currentTimeMillis();
        claims.put("iat",currentTime);  //issued at
        claims.put("exp",currentTime+3600000); //expiry
        claims.put("roles",user.getRoles());


        String token = Jwts.builder().claims(claims).signWith(secretKey).compact();

        Session session = new Session();
        session.setUser(user);
        session.setToken(token);
        session.setStatus(Status.ACTIVE);
        sessionRepo.save(session);

        return new Pair<User,String>(user,token);

    }

    //Validate JWT
       //-> check if token we received in token is present into db or not
       //-> checking expiry of token by doing reverse engineering

    public Boolean validateToken(String token,Long userId) {
        Optional<Session> optionalSession =
                sessionRepo.findByTokenAndUser_Id(token,userId);

        if (optionalSession.isEmpty()) {
            return false;
        }

        JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();
        Claims claims = jwtParser.parseSignedClaims(token).getPayload();

        Long expiry = (Long)claims.get("exp");
        Long currentTime = System.currentTimeMillis();
        //List< Role> roles = claims.get("roles");
        System.out.println("expiry = "+expiry);
        System.out.println("currentTime = "+currentTime);
        if (expiry < currentTime) {
            System.out.println("Token has expired");
            Session session = optionalSession.get();
            session.setStatus(Status.INACTIVE);
            sessionRepo.save(session);
            return false;
        }

        return true;
    }
}
