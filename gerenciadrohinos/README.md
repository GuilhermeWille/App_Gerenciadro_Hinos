🎼 Gerenciador de Hinos - Igreja IEAD

Aplicação Java Backend desenvolvida para gerenciar e consultar a base de dados de hinos e músicas disponíveis para os grupos de louvor, usando a Google Sheets API v4 como fonte de dados principal.

🚀 Visão Geral do Projeto

Este serviço atua como uma API que lê e organiza os dados da planilha mestre de hinos (Tabela 1).

O objetivo é fornecer uma fonte de dados estável para um front-end (a ser definido) ou para outras aplicações consumirem informações como: título, artista, duração e, crucialmente, o link direto do YouTube/Spotify.

⚙️ Pré-requisitos

Para executar e desenvolver esta aplicação, você precisará ter instalado:

    Java JDK 17+ (ou a versão que você estiver usando).

    Gradle (usado para gerenciar dependências).

    Uma conta Google Cloud Console com a Google Sheets API ativada.

🔒 Configuração e Segurança (CHAVE)

O acesso à planilha é feito através de credenciais de serviço. É o passo mais importante para rodar a aplicação.

1. Obtenção das Credenciais

    No Google Cloud Console, crie uma chave de acesso (geralmente do tipo OAuth 2.0 Client ID ou Service Account).

    Baixe o arquivo JSON gerado.

2. Localização do Arquivo

O seu projeto espera que o arquivo de credenciais esteja na pasta de recursos:

    Nome do Arquivo: credentials.json

    Caminho: src/main/resources/credentials.json

3. Segurança (Git Ignore)

⚠️ ATENÇÃO: Para garantir que suas credenciais não sejam publicadas no seu repositório Git, este arquivo DEVE estar listado no seu .gitignore. Confirme que a linha abaixo existe:

# Exclui o arquivo de credenciais de acesso à API
/src/main/resources/credentials.json

📊 Especificações da Planilha

O SheetService.java está configurado para interagir com a seguinte planilha:

    ID da Planilha: [Insira o ID longo da sua planilha aqui]

    Nome da Aba (Sheet Name): Planilha1 (ou o nome exato da sua aba)

Mapeamento de Colunas (Ranges A1)

O serviço usa a notação A1 para acessar os dados. O mapeamento atual das colunas é:
Coluna	Descrição	Range A1
A	Título da Música	Chave de busca principal.
D	Link do YouTube/Spotify	Usado para retorno direto.
A2:E	Range de Leitura Padrão	Leitura de todas as linhas de dados (ignora o cabeçalho).

▶️ Como Executar

Execute o projeto via Gradle:
Bash

./gradlew bootRun

A aplicação será iniciada na porta padrão [Porta do seu Spring Boot, ex: 8080].

📍 Endpoints Principais (Controller.java)

Método	Endpoint	Descrição
GET	/hinos/todos	Retorna a lista completa de todos os hinos cadastrados.
GET	/hinos/{titulo}	Busca um hino pelo título exato.
GET	/hinos/artista/{artista}	Busca todos os hinos de um artista específico.
[Adicione Outros]	[Adicione Endpoint]	[Adicione Descrição]

Este modelo cobre todos os aspectos essenciais. Agora, você pode preenchê-lo com os detalhes exatos (IDs, endpoints, etc.).

Qualquer dúvida sobre a autenticação ou o uso de um desses endpoints, me diga!