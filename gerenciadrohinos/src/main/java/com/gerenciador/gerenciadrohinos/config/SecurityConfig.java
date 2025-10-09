package com.gerenciador.gerenciadrohinos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; 
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain; 

import static com.gerenciador.gerenciadrohinos.model.Role.NivelAcesso.ROLE_EDITOR;
import static com.gerenciador.gerenciadrohinos.model.Role.NivelAcesso.ROLE_VIEWER;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    /**
     * Define o BCryptPasswordEncoder como o algoritmo padrão para criptografar
     * e verificar senhas no sistema.
     * @return Uma instância de PasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    
    /**
    * Define as regras de autorização e o filtro de segurança para todas as requisições HTTP.
    * @param http Objeto para configurar a segurança HTTP.
    * @return O SecurityFilterChain configurado.
    */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/h2-console/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/musicas/**").hasAnyAuthority(ROLE_VIEWER.name(), ROLE_EDITOR.name())
            .requestMatchers(HttpMethod.POST, "/musicas/**").hasRole(ROLE_EDITOR.name())
            .anyRequest().authenticated()
        ).httpBasic(httpBasic -> {})
        .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }
}
