package com.sep.psp.back.feature_superadmin.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class SuperAdmin {

    @Id
    @Column(nullable = false, updatable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean active;

    public SuperAdmin(
            String username,
            String passwordHash,
            String name
    ) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.name = name;
        this.active = true;
    }
}