package com.gerenciador.gerenciadrohinos.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange; // Import para escrita
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.oauth2.GoogleCredentials;

import jakarta.annotation.PostConstruct;

import com.google.auth.http.HttpCredentialsAdapter;

import org.springframework.core.io.ClassPathResource;
import org.springframework.beans.factory.annotation.Value;

import com.gerenciador.gerenciadrohinos.model.Model.Hino; 
import com.google.api.services.sheets.v4.model.AppendValuesResponse; // Import para resposta da escrita

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Serviço principal da aplicação, responsável por toda a comunicação
 * e interação com a Google Sheets API.
 * <p>Esta classe gerencia a autenticação e fornece métodos para leitura/escrita
 * de dados da planilha de hinos. É um componente Singleton gerenciado pelo Spring.
 *
 * @author Guilherme Wille
 * @version 1.0.0
 * @since 2025-10-08
 */
@org.springframework.stereotype.Service
public class Service {

    // Injeta o ID da planilha do application.properties
    @Value("${google.sheets.spreadsheet-id}") 
    private String spreadsheetId;

    // Injeta o nome da aba (pode ser usado como valor padrão)
    @Value("${google.sheets.tab-name:Planilha1}") 
    private String defaultSheetName;

    // Constantes e Configurações

    private static final String APPLICATION_NAME = "Gerenciador De Hinos";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    // O caminho para o arquivo de credenciais no diretório 'src/main/resources'
    private static final String CREDENTIAL_PATH = "credentials.json";

    private Sheets sheetsService;
    
    // Construtor vazio. A inicialização real ocorre no @PostConstruct.
    public Service() {}

    /**
     * Método de inicialização da API, executado após a injeção de todos os beans (@Value).
     */
    @PostConstruct
    public void init() throws IOException, GeneralSecurityException {
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
            System.out.println("-> Google Sheets API client inicializado com sucesso.");
        }
    }

    // --- MÉTODOS DE LEITURA (GET) ---

    /**
     * Realiza a leitura dos dados da planilha e mapeia para List<Hino>.
     */
    public List<Hino> getAllHinos() throws IOException {
        String range = defaultSheetName + "!A2:E";
        // ... (lógica de leitura e mapeamento, que estava correta) ...
        
        List<List<Object>> valoresBrutos = sheetsService.spreadsheets().values()
            .get(this.spreadsheetId, range) 
            .execute()
            .getValues();
            
        if (valoresBrutos == null) {
            return List.of();
        }

        // 2. Mapeamento: Converte a lista bruta (List<List<Object>>) para List<Hino>
        return valoresBrutos.stream()
            .map(linha -> {
                if (linha.size() >= 5) {
                    return new Hino(
                        linha.get(0).toString(), 
                        linha.get(1).toString(), 
                        linha.get(2).toString(), 
                        linha.get(3).toString(), 
                        linha.get(4).toString()
                    );
                }
                return null;
            })
            .filter(hino -> hino != null)
            .collect(Collectors.toList());
    }
    
    /**
     * Busca hinos por um nome de artista específico, aplicando a lógica de filtro
     * em memória (após a leitura da planilha).
     */
    public List<Hino> getMusicasPorArtista(String nomeArtista) throws IOException{
        List<Hino> todosOsHinos = getAllHinos();

        if(todosOsHinos == null || nomeArtista == null){
            return List.of();
        }

        List<Hino> hinosFiltrados = todosOsHinos.stream()
            .filter(hino -> {
                return hino.artista() != null && 
                    hino.artista().trim().equalsIgnoreCase(nomeArtista.trim());
            })
            .collect(Collectors.toList());

        return hinosFiltrados;
    }

    // --- MÉTODO DE ESCRITA (POST) ---

    /**
     * Adiciona um novo hino à planilha mestre.
     * <p>Usa a operação 'append' para inserir uma nova linha no final da planilha.
     * @param novoHino O objeto Hino a ser persistido.
     * @return O objeto Hino persistido (para confirmação).
     * @throws IOException Se a escrita na planilha falhar.
     */
    public Hino appendHino(Hino novoHino) throws IOException {
        String range = defaultSheetName + "!A:E"; // Range completo para escrita (adiciona no final)

        // 1. Converte o objeto Hino em uma lista de objetos (formato Sheets API)
        List<Object> rowData = Arrays.asList(
            novoHino.titulo(),
            novoHino.artista(),
            novoHino.duracao(),
            novoHino.link(),
            novoHino.adicionadoPor()
        );
        List<List<Object>> values = Arrays.asList(rowData);

        // 2. Define o corpo da requisição
        ValueRange body = new ValueRange().setValues(values);

        // 3. Executa a requisição de APPEND (adiciona nova linha)
        AppendValuesResponse result = this.sheetsService.spreadsheets().values().append(
            spreadsheetId, 
            range, 
            body
        )
        .setValueInputOption("USER_ENTERED") // Trata valores como se fossem digitados manualmente
        .execute();

        // Em uma aplicação real, você faria uma nova leitura, mas aqui retornamos o objeto original
        return novoHino;
    }

    /**
     * Retorna a instância do cliente da Google Sheets API.
     */
    public Sheets getSheetsService() {
        return this.sheetsService;
    }
}
