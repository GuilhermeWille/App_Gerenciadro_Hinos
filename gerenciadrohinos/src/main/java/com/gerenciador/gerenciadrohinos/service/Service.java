package com.gerenciador.gerenciadrohinos.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.http.HttpCredentialsAdapter;

import org.springframework.core.io.ClassPathResource;

import com.gerenciador.gerenciadrohinos.model.Hino; 

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço principal da aplicação, responsável por toda a comunicação
 * e interação com a Google Sheets API.
 * * <p>Esta classe gerencia a autenticação e fornece métodos para leitura/escrita
 * de dados da planilha de hinos. É um componente Singleton gerenciado pelo Spring.
 * * @author Guilherme Wille
 * @version 1.0.0
 * @since 2025-10-08
 */

@org.springframework.stereotype.Service
public class Service {

    // Injeta o ID da planilha do application.properties
    @Value("${google.sheets.spreadsheet-id}") 
    private String spreadsheetId;

    // Injeta o nome da aba (pode ser usado como valor padrão)
    @Value("${google.sheets.tab-name:Planilha1}") // O valor :Planilha1 é um fallback
    private String defaultSheetName;

    // Constantes e Configurações

    private static final String APPLICATION_NAME = "Gerenciador De Hinos";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    // O caminho para o arquivo de credenciais no diretório 'src/main/resources'
    private static final String CREDENTIAL_PATH = "credentials.json";

    private Sheets sheetsService;
    
    /**
     * Construtor do Service. Responsável por inicializar o objeto 'sheetsService' (API client).
     * * <p>Realiza os seguintes passos críticos:
     * 1. Carrega o arquivo 'credentials.json' do classpath.
     * 2. Cria as credenciais com o escopo 'SPREADSHEETS'.
     * 3. Constrói e inicializa o objeto Sheets com o transporte HTTP confiável.
     * * @throws IOException Se o arquivo de credenciais não for encontrado ou houver erro de leitura.
     * @throws GeneralSecurityException Se houver um erro no estabelecimento de transporte HTTP seguro.
     */

    public Service() throws IOException, GeneralSecurityException {
        ClassPathResource resource = new ClassPathResource(CREDENTIAL_PATH);

        try (InputStream in = resource.getInputStream()){
            GoogleCredentials credentials = GoogleCredentials.fromStream(in)
                    .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

            this.sheetsService = new Sheets.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    new HttpCredentialsAdapter(credentials))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        }
    }

     /**
     * Realiza a leitura dos dados de uma planilha Google Sheets específica.
     * * <p>Usa a notação A1 (e.g., "Planilha1!A2:E") para especificar o conjunto de células a ser lido.
     * @param spreadsheetId O ID da planilha (obtido da URL do Google Sheets).
     * @param range A string de notação A1 que define a área de leitura.
     * @return Uma lista de listas de objetos (cada lista interna é uma linha).
     * @throws IOException Se houver falha na comunicação com a API ou erro de autorização.
     */

    public List<Hino> getAllHinos() throws IOException {
    String range = defaultSheetName + "!A2:E"; // Range A1: Planilha1!A2:E

        // 1. Busca os dados brutos da API
        List<List<Object>> valoresBrutos = sheetsService.spreadsheets().values()
            .get(this.spreadsheetId, range) 
            .execute()
            .getValues();
            
        if (valoresBrutos == null) {
            return List.of(); // Retorna lista vazia se não houver dados
        }

        // 2. Mapeamento: Converte a lista bruta (List<List<Object>>) para List<Hino>
        return valoresBrutos.stream()
            .map(linha -> {
                // Garante que haja dados suficientes na linha para evitar IndexOutOfBounds
                if (linha.size() >= 5) {
                    // Cria um objeto Hino, mapeando cada índice da lista bruta
                    return new Hino(
                        // A ordem é Título(0), Artista(1), Duração(2), Link(3), Integrantes(4)
                        linha.get(0).toString(), // Titulo
                        linha.get(1).toString(), // Artista
                        linha.get(2).toString(), // Duracao
                        linha.get(3).toString(), // Link
                        linha.get(4).toString()  // AdicionadoPor
                    );
                }
            return null; // Retorna nulo para linhas incompletas
        })
            .filter(hino -> hino != null) // Remove quaisquer entradas nulas geradas
            .collect(Collectors.toList());
    }
   
    
   /**
     * Busca hinos por um nome de artista específico, aplicando a lógica de filtro
     * em memória (após a leitura da planilha).
     * * @param nomeArtista O nome exato do artista a ser filtrado.
     * @return Uma lista de objetos Hino onde o artista corresponde ao nome fornecido.
     * @throws IOException Se a comunicação com a API falhar.
     */
    public List<Hino> getMusicasPorArtista(String nomeArtista) throws IOException{
        // 1. Pega todos os dados da planilha. Agora é uma List<Hino>!
        List<Hino> todosOsHinos = getAllHinos();

        if(todosOsHinos == null || nomeArtista == null){
            return List.of();
        }

        // 2. Filtra a lista usando Java Streams
        // Não precisamos mais checar o índice. Usamos o nome do campo!
        List<Hino> hinosFiltrados = todosOsHinos.stream()
            .filter(hino -> {
                // Compara o campo 'artista' do objeto Hino com o nomeArtista (ignorando maiúsculas/minúsculas)
                return hino.artista() != null && 
                    hino.artista().trim().equalsIgnoreCase(nomeArtista.trim());
            })
            .collect(Collectors.toList());

        return hinosFiltrados;
    }
    /**
     * Retorna a instância do cliente da Google Sheets API.
     * * <p>Pode ser usado por outros métodos de serviço para operações mais complexas
     * que não estão diretamente encapsuladas neste serviço (e.g., operações batch).
     * * @return O objeto Sheets inicializado e autenticado.
     */

    public Sheets getSheetsService() {
        return this.sheetsService;
    }
}