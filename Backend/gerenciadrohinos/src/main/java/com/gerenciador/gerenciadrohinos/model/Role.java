package com.gerenciador.gerenciadrohinos.model;

import jakarta.persistence.*;


@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private NivelAcesso name;

    public enum NivelAcesso {
        ROLE_VIEWER,
        ROLE_EDITOR
    }

    // --- Construtor Padrão (Necessário para JPA) ---
    public Role() {
    }

    // --- Construtor com Nome ---
    public Role(NivelAcesso name) {
        this.name = name;
    }

    // --- Getters e Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NivelAcesso getName() {
        return name;
    }

    public void setName(NivelAcesso name) {
        this.name = name;
    }
}