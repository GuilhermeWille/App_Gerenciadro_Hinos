package com.gerenciador.gerenciadrohinos.model;

/**
 * Representa um registro de hino da planilha Google Sheets.
 * (Record é a forma moderna e concisa do Java para POJOs)
 */
public class Model {
    
    public record Hino(
        //Coluna A
        String titulo,
        //Coluna B
        String artista,
        //Coluna C
        String duracao,
        //Coluna D
        String link,
        //Coluna E
        String adicionadoPor
        ){}
}
