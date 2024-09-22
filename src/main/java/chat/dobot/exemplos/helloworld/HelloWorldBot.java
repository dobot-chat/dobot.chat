package chat.dobot.exemplos.helloworld;

import chat.dobot.bot.DoBotChatApp;
import chat.dobot.bot.annotations.DoBotChat;
import chat.dobot.bot.annotations.EstadoChat;
import chat.dobot.bot.Contexto;

@DoBotChat(id = "helloWorldBot", nome = "Alô Mundo Bot", descricao = "Bot que responde com 'Alô' a qualquer mensagem")
public class HelloWorldBot {

    public static void main(String[] args) {
        DoBotChatApp meubot = DoBotChatApp.novoBot();
        meubot.setMensagemInicial("Olá! Eu sou o chatbot Alô Mundo! Escreva qualquer coisa e responderei com `Alô`.");
        meubot.start(8083,8084);
    }

    @EstadoChat(inicial = true)
    public void aloMundo(Contexto chat) {
        String msg = chat.getMensagemUsuario();
        chat.responder("Alô "+msg);
    }
}
