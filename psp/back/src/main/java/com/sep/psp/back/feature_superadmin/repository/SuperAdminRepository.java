package com.sep.psp.back.feature_superadmin.repository;

import com.sep.psp.back.feature_superadmin.model.SuperAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SuperAdminRepository extends JpaRepository<SuperAdmin, String> {

    Optional<SuperAdmin> findByUsername(String username);

}