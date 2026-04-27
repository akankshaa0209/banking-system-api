package com.akanksha.bank.repository;

import com.akanksha.bank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email); // 🔥 REQUIRED for login
}