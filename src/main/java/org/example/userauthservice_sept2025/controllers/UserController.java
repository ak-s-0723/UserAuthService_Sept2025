package org.example.userauthservice_sept2025.controllers;

import org.example.userauthservice_sept2025.dtos.UserDto;
import org.example.userauthservice_sept2025.models.User;
import org.example.userauthservice_sept2025.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/users")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public UserDto getUserDetails(@PathVariable Long id) {
        User user = userService.getUserDetails(id);
        if(user==null) return null;
        System.out.println(user.getEmail());
        return from(user);
    }


    private UserDto from(User user) {
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        return userDto;
    }
}
