package com.gerenciador.gerenciadrohinos.repository;

import com.gerenciador.gerenciadrohinos.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{
 
    // Spring Data JPA gera o SQL: SELECT * FROM system_user WHERE username = ?
    Optional<User> findByUsername(String username);
}
