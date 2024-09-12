# DoBot.chat - Ferramenta para Desenvolvimento de Chatbots
O DoBot.chat é um ferramenta que facilita a criação de chatbots customizados para diferentes finalidades. Ele oferece um sistema de estados para gerenciar o fluxo de conversas e suporte para personalização visual e persistência de dados, quando necessário.

Este é um projeto coordenado pelo prof. Rodrigo Rebouças de Almeida (https://rodrigor.com), do Departamento de Ciências Exatas da Universidade Federal da Paraíba, Campus IV - Rio Tinto - PB, Brasil.

## Equipe

- Rodrigo Rebouças de Almeida (Coordenador do projeto)
- Lucas da Silva Freitas (v2 do DoBot.chat)
- Gabriel Ribeiro Dias (v1 do DoBot.chat)

## Requisitos
- Java 17 ou superior

## Dependências
O DoBot depende de poucas bibliotecas, focando na simplicidade e funcionalidade. Abaixo estão as principais dependências utilizadas no projeto:

- Yorm: Biblioteca ORM para persistência de dados.
- Javalin: Framework leve para construção de aplicações web RESTful e integração do frontend do chatbot.
- Thymeleaf: Motor de templates utilizado para renderizar as páginas HTML do chatbot.
- H2 Database: Banco de dados em memória utilizado para persistência.
- Slf4j2: Para o registro de logs no sistema.

## Início Rápido
Para utilizar o MyChat2 em seu projeto, siga as instruções abaixo:

### Dependência Maven
Adicione a seguinte dependência no seu arquivo pom.xml:
```xml
<dependency>
    <groupId>com.mychat2</groupId>
    <artifactId>MyChat2</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
## Estrutura do Chatbot
1. Anotação @MyChat

    Sua classe de chatbot deve ser anotada com @MyChat para indicar que ela será utilizada como um chatbot.
    ```java
    @MyChat
    public class MeuChatbot {
    // Implementação do chatbot
    }
    ```
2. Anotação @EstadoChat

    Os métodos que representam os estados do chatbot devem ser anotados com @EstadoChat e conter um único parâmetro do tipo Contexto. Um desses métodos deve ter a anotação @EstadoChat com o parâmetro inicial = true, o que indica o estado inicial do chatbot.
    ```java
    @EstadoChat(inicial = true)
    public void estadoInicial(Contexto contexto) {
    // Implementação do estado inicial
    }
    ```
    ### O que são Estados e como eles funcionam
    Os **estados** no chatbot servem para representar diferentes etapas ou fluxos de interação. Cada método anotado com @EstadoChat define uma resposta ou comportamento específico do chatbot em determinado momento da interação. Através dos estados, o chatbot pode gerenciar diferentes fases de uma conversa, como por exemplo:

   - Estado Inicial: Ponto de partida da conversa. Esse é o primeiro estado chamado quando o chatbot é iniciado.

   - Estado de Ação: Estados intermediários que lidam com interações específicas.

   - Estado Final: Opcionalmente, você pode ter estados que finalizam ou encerram o fluxo de conversa, dependendo da lógica da aplicação.

    Quando o usuário interage com o chatbot, o estado atual é usado para determinar qual método deve ser chamado para responder à mensagem. O fluxo do chatbot é guiado pela troca de estados, permitindo que você controle cada passo da interação de maneira precisa e modular.

    Por exemplo, se o chatbot está em um estado de "aguardando nome", ele aguarda a resposta do usuário com o nome antes de avançar para o próximo estado.


3. Anotações @Entidade e @Id (opcionais)

   Você pode definir **records** persistentes com anotações @Entidade e @Id para mapear objetos Java para tabelas de banco de dados. Você pode utilizar uma instância de MyChatServico para realizar operações CRUD com suas entidades.
    ```java
    @Entidade
    public record Tarefa(@Id int id, String descricao) {}
    ```
## Configurando o Tema do Chatbot
Você pode personalizar o tema do chatbot usando a classe MyChatTema. É possível configurar diversas cores para o fundo da página, chat, mensagens, e texto.

Exemplo de configuração do tema:
```java
MyChatApp.getMyChatTema().setCorFundoPagina("#FFFFFF");
MyChatApp.getMyChatTema().setCorFundoMensagemUsuario("#ADD8E6");
MyChatApp.getMyChatTema().setCorFundoMensagemBot("#FFD700");
MyChatApp.getMyChatTema().setCorTextoChat("#000000");
```
## Configurando a Mensagem Inicial e as Portas
Você pode configurar uma mensagem inicial para ser exibida quando o chatbot for acessado pela primeira vez. Defina essa mensagem antes de iniciar o chatbot:
```java
MyChatApp.setMensagemInicial("Bem-vindo ao chatbot!");
```
Além disso, você pode alterar as portas nas quais a aplicação será executada. Por padrão, a aplicação roda na porta 8080 para o chatbot e na porta 8082 para o servidor H2. Se desejar, você pode customizar essas portas.
```java
MyChatApp.start(9090, 9092); // Porta 9090 para o chatbot e 9092 para o H2
```
## Exemplo de Main
Aqui está um exemplo básico de como inicializar o chatbot:
```java
public class Main {
    public static void main(String[] args) {
        MyChatApp.setMensagemInicial("Chatbot inicializado com sucesso!");
        MyChatApp.getMyChatTema().setCorFundoMensagemUsuario("blue");
        MyChatApp.getMyChatTema().setCorFundoMensagemBot("red");
        MyChatApp.start(8081, 8083); // Rodando o chatbot na porta 8081 e o H2 na 8083
    }
}
```
