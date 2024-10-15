# DoBot.chat - Ferramenta para Desenvolvimento de Chatbots

![dobot-horiz-300.png](dobot-horiz-300.png)

O DoBot.chat é uma ferramenta poderosa para a criação de chatbots personalizados, utilizando Java como base. Ele oferece um sistema de controle de estados para gerenciar o fluxo das conversas, com foco em simplicidade e flexibilidade para diversos casos de uso. Além disso, permite a personalização visual e a persistência de dados.

Este projeto é coordenado pelo Prof. Rodrigo Rebouças de Almeida (https://rodrigor.com), da Universidade Federal da Paraíba, Campus IV - Rio Tinto - PB, Brasil.

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
Para utilizar o DoBot em seu projeto, siga as instruções abaixo:

### Dependência Maven
Adicione a seguinte dependência no seu arquivo pom.xml:

```xml
<dependency>
   <groupId>chat.dobot.bot</groupId>
   <artifactId>DoBot.chat</artifactId>
   <version>1.1.1</version>
   <type>pom</type>
</dependency>
```
Acrescente o repositório Maven:
```xml
<repositories>
   <repository>
      <id>projetos</id>
      <url>https://maven.vps.rodrigor.com/repository/projetos/</url>
   </repository>
</repositories>
```

## Estrutura do Chatbot
1. Anotação @DoBotChat

   Sua classe de chatbot deve ser anotada com `@DoBotChat` para indicar que ela será utilizada como um chatbot. A anotação deve conter um ID e um nome. A descrição é opcional.
    ```java
    @DoBotChat(id = "meuChat", nome = "Nome do Bot", descricao = "Descrição do Bot")
    public class MeuChatbot {
    // Implementação do chatbot
    }
    ```

2. Anotação @EstadoChat

   Os métodos que representam os estados do chatbot devem ser anotados com `@EstadoChat` e conter um único parâmetro do tipo `Contexto`. Um desses métodos deve ter a anotação `@EstadoChat(inicial = true)`, que indica o estado inicial do chatbot.
    ```java
    @EstadoChat(inicial = true)
    public void estadoInicial(Contexto contexto) {
    // Implementação do estado inicial
    }
    ```

### O que são Estados e como eles funcionam
Os **estados** no chatbot servem para representar diferentes etapas ou fluxos de interação. Cada método anotado com `@EstadoChat` define uma resposta ou comportamento específico do chatbot em determinado momento da interação.

- **Estado Inicial**: Ponto de partida da conversa. Esse é o primeiro estado chamado quando o chatbot é iniciado.
- **Estado de Ação**: Estados intermediários que lidam com interações específicas.
- Para mudar de estado, você pode chamar o método `proximoEstado` do objeto `Contexto` passando o nome do próximo estado como parâmetro. O nome do estado deve ser o mesmo utilizado na anotação `@EstadoChat` ou o nome do método.

Exemplo de fluxo de estados:

```java
@EstadoChat(inicial = true)
public void estadoInicial(Contexto contexto) {
    String msg = contexto.getMensagemUsuario();
    contexto.responder("Você disse: " + msg);
    contexto.proximoEstado("novoEstado");
}

@EstadoChat
public void novoEstado(Contexto contexto) {
    contexto.responder("Este é o próximo estado.");
}
```
Observe que o estado inicial é definido com a anotação `@EstadoChat(inicial = true)` e o próximo estado é chamado com o método `proximoEstado`, passando o nome do próximo estado como parâmetro. No exemplo, o próximo estado é chamado de `novoEstado`).

Isso significa que, após o usuário enviar uma mensagem no estado inicial, o chatbot responderá com a mensagem "Você disse: [mensagem do usuário]" e mudará para o estado `novoEstado`. Na próxima vez que o usuário enviar uma mensagem, o chatbot responderá com a mensagem "Este é o próximo estado."

Você pode usar estados para tratar opções de menus, por exemplo. Veja os exemplos disponíveis no pacote `chat.dobot.exemplos`.

### Anotações @Entidade e @Id (opcionais)

   Você pode definir **records** persistentes com anotações `@Entidade` e `@Id` para mapear objetos Java para tabelas de banco de dados.
   ```java
   @Entidade
   public record Tarefa(@Id int id, String descricao) {}
   ```

## Configuração do Chatbot
Além dos estados, você pode configurar o chatbot utilizando a anotação `@Config` para definir algumas configurações, como a mensagem inicial exibida ao iniciar o bot.

Exemplo de configuração:
```java
@Config
public void config(DoBotConfig config){
    config.setMensagemInicial("👋 Olá! Eu sou o chatbot Alô Mundo! Escreva qualquer coisa e responderei com 'Alô'.");
}
```

## Configurando o Tema do Chatbot
Você pode personalizar o tema do chatbot usando a classe `DoBotTema`. É possível configurar cores para o fundo da página, mensagens e texto.

Exemplo de configuração do tema:
```java
DoBotChatApp meubot = DoBotChatApp.novoBot();
meubot.tema().setCorFundoPagina("#FFFFFF");
meubot.tema().setCorFundoMensagemUsuario("#ADD8E6");
meubot.tema().setCorFundoMensagemBot("#FFD700");
meubot.tema().setCorTextoChat("#000000");
```

## Configurando a Mensagem Inicial e as Portas
Você pode configurar uma mensagem inicial e as portas onde o chatbot será executado. A primeira porta é usada para o DoBot.chat e a segunda para o servidor H2. Se preferir, também pode iniciar o bot sem parâmetros, utilizando as portas padrão (8080 para o chatbot e 8082 para o H2).

Exemplo:
```java
DoBotChatApp meubot = DoBotChatApp.novoBot();
meubot.setMensagemInicial("Bem-vindo ao chatbot!");
meubot.start(9090, 9092); // Porta 9090 para o chatbot e 9092 para o H2
```

Ou utilizando as portas padrão:
```java
meubot.start();
```

## Exemplo de Main
Aqui está um exemplo básico de como inicializar o chatbot:
```java
import chat.dobot.bot.*;
import chat.dobot.bot.annotations.Config;
import chat.dobot.bot.annotations.DoBotChat;
import chat.dobot.bot.annotations.EstadoChat;

@DoBotChat(id = "hello", nome = "👋 Alô Mundo Bot", descricao = "Bot que responde com 'Alô' a qualquer mensagem")
public class HelloWorldBot {

   public static void main(String[] args) {
      DoBotChatApp meubot = DoBotChatApp.novoBot();
      meubot.ativarExemplos();
      meubot.start(8083,8084);
   }

   @Config
   public void config(DoBotConfig config){
      config.setMensagemInicial("👋 Olá! Eu sou o chatbot Alô Mundo! Escreva qualquer coisa e responderei com `Alô`.");
   }

   @EstadoChat(inicial = true)
   public void aloMundo(Contexto chat) {
      String msg = chat.getMensagemUsuario();
      chat.responder("Alô "+msg);
   }

}

```

## Acessando o Banco de Dados H2
O DoBot.chat utiliza o H2 como banco de dados em memória por padrão. Você pode acessar o console web do H2 para inspecionar as tabelas e dados armazenados.

1. Acessando o Console Web do H2
   Após a aplicação estar rodando, acesse o seguinte endereço:
   ```shell
   http://localhost:8082
   ```

2. Conectando ao H2
   No console web do H2, preencha as seguintes informações para se conectar ao banco de dados:
   - JDBC URL: jdbc:h2:mem:dobotdb
   - User Name: dobot
   - Password: (deixe em branco)

   Após preencher as informações, clique no botão "Connect" para acessar o banco de dados.