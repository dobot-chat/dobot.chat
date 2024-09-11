# MyChat V2 - Ferramenta para Desenvolvimento de Chatbots
O MyChat V2 � um ferramenta que facilita a cria��o de chatbots customizados para diferentes finalidades. Ele oferece um sistema de estados para gerenciar o fluxo de conversas e suporte para personaliza��o visual e persist�ncia de dados, quando necess�rio.
## Requisitos
- Java 17 ou superior

## Depend�ncias
O MyChat2 depende de poucas bibliotecas, focando na simplicidade e funcionalidade. Abaixo est�o as principais depend�ncias utilizadas no projeto:

- Yorm: Biblioteca ORM para persist�ncia de dados.
- Javalin: Framework leve para constru��o de aplica��es web RESTful e integra��o do frontend do chatbot.
- Thymeleaf: Motor de templates utilizado para renderizar as p�ginas HTML do chatbot.
- H2 Database: Banco de dados em mem�ria utilizado para persist�ncia.
- Slf4j2: Para o registro de logs no sistema.

## In�cio R�pido
Para utilizar o MyChat2 em seu projeto, siga as instru��es abaixo:

### Depend�ncia Maven
Adicione a seguinte depend�ncia no seu arquivo pom.xml:
```xml
<dependency>
    <groupId>com.mychat2</groupId>
    <artifactId>MyChat2</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
## Estrutura do Chatbot
1. Anota��o @MyChat

    Sua classe de chatbot deve ser anotada com @MyChat para indicar que ela ser� utilizada como um chatbot.
    ```java
    @MyChat
    public class MeuChatbot {
    // Implementa��o do chatbot
    }
    ```
2. Anota��o @EstadoChat

    Os m�todos que representam os estados do chatbot devem ser anotados com @EstadoChat e conter um �nico par�metro do tipo Contexto. Um desses m�todos deve ter a anota��o @EstadoChat com o par�metro inicial = true, o que indica o estado inicial do chatbot.
    ```java
    @EstadoChat(inicial = true)
    public void estadoInicial(Contexto contexto) {
    // Implementa��o do estado inicial
    }
    ```
    ### O que s�o Estados e como eles funcionam
    Os **estados** no chatbot servem para representar diferentes etapas ou fluxos de intera��o. Cada m�todo anotado com @EstadoChat define uma resposta ou comportamento espec�fico do chatbot em determinado momento da intera��o. Atrav�s dos estados, o chatbot pode gerenciar diferentes fases de uma conversa, como por exemplo:

   - Estado Inicial: Ponto de partida da conversa. Esse � o primeiro estado chamado quando o chatbot � iniciado.

   - Estado de A��o: Estados intermedi�rios que lidam com intera��es espec�ficas.

   - Estado Final: Opcionalmente, voc� pode ter estados que finalizam ou encerram o fluxo de conversa, dependendo da l�gica da aplica��o.

    Quando o usu�rio interage com o chatbot, o estado atual � usado para determinar qual m�todo deve ser chamado para responder � mensagem. O fluxo do chatbot � guiado pela troca de estados, permitindo que voc� controle cada passo da intera��o de maneira precisa e modular.

    Por exemplo, se o chatbot est� em um estado de "aguardando nome", ele aguarda a resposta do usu�rio com o nome antes de avan�ar para o pr�ximo estado.


3. Anota��es @Entidade e @Id (opcionais)

   Voc� pode definir **records** persistentes com anota��es @Entidade e @Id para mapear objetos Java para tabelas de banco de dados. Voc� pode utilizar uma inst�ncia de MyChatServico para realizar opera��es CRUD com suas entidades.
    ```java
    @Entidade
    public record Tarefa(@Id int id, String descricao) {}
    ```
## Configurando o Tema do Chatbot
Voc� pode personalizar o tema do chatbot usando a classe MyChatTema. � poss�vel configurar diversas cores para o fundo da p�gina, chat, mensagens, e texto.

Exemplo de configura��o do tema:
```java
MyChatApp.getMyChatTema().setCorFundoPagina("#FFFFFF");
MyChatApp.getMyChatTema().setCorFundoMensagemUsuario("#ADD8E6");
MyChatApp.getMyChatTema().setCorFundoMensagemBot("#FFD700");
MyChatApp.getMyChatTema().setCorTextoChat("#000000");
```
## Configurando a Mensagem Inicial e as Portas
Voc� pode configurar uma mensagem inicial para ser exibida quando o chatbot for acessado pela primeira vez. Defina essa mensagem antes de iniciar o chatbot:
```java
MyChatApp.setMensagemInicial("Bem-vindo ao chatbot!");
```
Al�m disso, voc� pode alterar as portas nas quais a aplica��o ser� executada. Por padr�o, a aplica��o roda na porta 8080 para o chatbot e na porta 8082 para o servidor H2. Se desejar, voc� pode customizar essas portas.
```java
MyChatApp.start(9090, 9092); // Porta 9090 para o chatbot e 9092 para o H2
```
## Exemplo de Main
Aqui est� um exemplo b�sico de como inicializar o chatbot:
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