package com.example.cognito_springsecurity.repository;

import com.example.cognito_springsecurity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByDeletedFlgFalse();
}
