package com.gerenciador.gerenciadrohinos.model;

import jakarta.persistence.*;
import java.util.Set;

/**
 * Representa um usuário do sistema (Gerenciador/Editor/Visualizador).
 * Esta entidade será armazenada no banco de dados H2.
 */
@Entity
@Table(name = "system_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; // Nome de login

    @Column(nullable = false)
    private String password; // Senha (armazenada como hash)

    // Relação com as Roles (Níveis de Acesso)
    @ManyToMany(fetch = FetchType.EAGER) // Carrega as roles junto com o usuário
    @JoinTable(name = "user_roles",
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    public User() {
    }

    // --- Getters e Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}

