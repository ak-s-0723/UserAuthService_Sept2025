package org.example.userauthservice_sept2025.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import javax.crypto.Mac;

@Setter
@Getter
@Entity
public class Session extends BaseModel {
    private String token;

    @ManyToOne
    private User user;
}


// 1            M
//User        Session
//1             1
//
//        1 : M
