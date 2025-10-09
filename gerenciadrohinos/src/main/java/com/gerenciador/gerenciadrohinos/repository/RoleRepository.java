package com.gerenciador.gerenciadrohinos.repository;

import com.gerenciador.gerenciadrohinos.model.Role;
import com.gerenciador.gerenciadrohinos.model.Role.NivelAcesso;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositório para a entidade Role (Nível de Acesso).
 * Permite operações de CRUD e busca por nome do nível.
 */
public interface RoleRepository extends JpaRepository<Role, Long>{
    // Método que o Spring JPA implementa automaticamente: busca uma Role pelo seu nome.
    Optional<Role> findByName(NivelAcesso name);
}
