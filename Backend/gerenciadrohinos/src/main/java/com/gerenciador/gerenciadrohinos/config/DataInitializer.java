package com.gerenciador.gerenciadrohinos.config;

import com.gerenciador.gerenciadrohinos.model.Role;
import com.gerenciador.gerenciadrohinos.model.Role.NivelAcesso;
import com.gerenciador.gerenciadrohinos.model.User;
import com.gerenciador.gerenciadrohinos.repository.RoleRepository;
import com.gerenciador.gerenciadrohinos.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

/**
 * Classe responsável por popular o banco de dados H2 com as Roles e o primeiro
 * usuário administrador assim que a aplicação é iniciada.
 */
@Configuration
public class DataInitializer {

    private static final String DEFAULT_PASSWORD = "123456";

    @Bean
    public CommandLineRunner initializeData(
        RoleRepository roleRepository,
        UserRepository userRepository,
        PasswordEncoder passwordEncoder
    ){
        return args -> {
            System.out.println("--- [SEGURANÇA] Inicializando Roles e Usuário Padrão... ---");

            // --- 1. ENCONTRAR/CRIAR O EDITOR ROLE ---
            Role editorRole;
            // Tenta buscar o editor.
            if (roleRepository.findByName(NivelAcesso.ROLE_EDITOR).isEmpty()) {
                // Se não encontrar, cria e salva.
                editorRole = roleRepository.save(new Role(NivelAcesso.ROLE_EDITOR));
            } else {
                // Se encontrar, pega o valor.
                editorRole = roleRepository.findByName(NivelAcesso.ROLE_EDITOR).get();
            }

            // --- 2. ENCONTRAR/CRIAR O VIEWER ROLE ---
            Role viewRole;
            if (roleRepository.findByName(NivelAcesso.ROLE_VIEWER).isEmpty()) {
                viewRole = roleRepository.save(new Role(NivelAcesso.ROLE_VIEWER));
            } else {
                viewRole = roleRepository.findByName(NivelAcesso.ROLE_VIEWER).get();
            }

            // --- 3. CRIAÇÃO DO USUÁRIO ADMIN (Lógica anterior, que está correta) ---
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(editorRole);
            adminRoles.add(viewRole);

            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD)); 
                admin.setRoles(adminRoles);
                userRepository.save(admin);

                System.out.println("-> Usuário 'admin' criado com sucesso!");
                System.out.println("-> Senha: " + DEFAULT_PASSWORD);
            } else {
                System.out.println("-> Usuário 'admin' já existe. Pulando criação.");
            }
            
            System.out.println("--- [SEGURANÇA] Inicialização concluída. ---");
        };
    }

}