package com.gerenciador.gerenciadrohinos.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; 
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gerenciador.gerenciadrohinos.service.Service;
import com.gerenciador.gerenciadrohinos.model.Model.Hino;

import java.io.IOException;
import java.util.List;

/**
 * Controller principal que expõe os endpoints da API para o gerenciamento de hinos.
 * Todos os endpoints iniciam com a rota base "/musicas".
 * * @author GW
 * @version 1.0.0
 * @since 2025-10-08
 */
@RestController
@RequestMapping("/musicas")
public class Controller {
    
    private final Service service;
    
    // Injeção de dependência via construtor
    public Controller(Service service){
        this.service = service;
        }
    
    /**
     * Endpoint GET que retorna a lista completa de todos os hinos cadastrados
     * na planilha, incluindo todas as colunas (Título, Artista, Duração, Link, Integrantes).
     * * Rota: /musicas/todos
     * @return Lista de objetos Hino (o formato JSON final da API, tipado e legível).
     * @throws IOException Se a comunicação com a Google Sheets API falhar.
     */
    @GetMapping("/todos")
    public List<Hino> listarTodasAsMusicas() throws IOException{
        return service.getAllHinos();
    }

     /**
     * Endpoint GET para buscar e filtrar hinos de um artista específico.
     * A filtragem é feita em memória pelo Service, após a leitura da planilha.
     * * Rota: /musicas/artista/{nomeArtista}
     * @param nomeArtista O nome do artista/grupo (extraído da URL, ex: "MORADA").
     * @return Lista de hinos que correspondem ao artista fornecido.
     * @throws IOException Se a comunicação com a API falhar.
     */
    @GetMapping("/artista/{nomeArtista}")
    public List<Hino> buscarPorArtista(@PathVariable("nomeArtista") String nomeArtista) throws IOException{ 
        return service.getMusicasPorArtista(nomeArtista);
    }
}
