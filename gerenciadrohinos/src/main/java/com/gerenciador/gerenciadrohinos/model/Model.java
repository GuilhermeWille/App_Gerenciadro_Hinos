package src.main.java.com.gerenciador.gerenciadrohinos.model;
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
        String aidionadoPor
        ){}
}
