package com.sep.web_shop.back.feature_auth.model;

import com.sep.web_shop.back.feature_auth.enumeration.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "APP_USER")
public class User {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column(unique = true)
    @NotNull
    private String username;

    @Column(unique = true)
    @NotNull
    private String email;

    @Column
    @NotNull
    private String password;

    @Column
    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

}
