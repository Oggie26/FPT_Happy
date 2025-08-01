package com.example.authservice.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "auths")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Auth extends  AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(unique = true)
    String username;

    @Column
    String password;

}
