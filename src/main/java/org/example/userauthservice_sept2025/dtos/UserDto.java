package org.example.userauthservice_sept2025.dtos;

import lombok.Getter;
import lombok.Setter;
import org.example.userauthservice_sept2025.models.Role;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class UserDto {
    private Long id;
    private String email;
    private String name;
    private List<Role> roles = new ArrayList<>();
}
