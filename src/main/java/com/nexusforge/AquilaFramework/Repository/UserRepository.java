package com.nexusforge.AquilaFramework.Repository;

import com.nexusforge.AquilaFramework.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User , Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndPassword(String email , String password);
    List<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username , String email);
}

