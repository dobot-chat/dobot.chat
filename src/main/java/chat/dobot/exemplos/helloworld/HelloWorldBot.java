package chat.dobot.exemplos.helloworld;

import chat.dobot.bot.DoBotChat;
import chat.dobot.bot.annotations.DoBot;
import chat.dobot.bot.annotations.EstadoChat;
import chat.dobot.bot.Contexto;

@DoBot(id = "helloWorldBot", nome = "Alô Mundo Bot", descricao = "Bot que responde com 'Alô' a qualquer mensagem")
public class HelloWorldBot {

    public static void main(String[] args) {
        DoBotChat meubot = DoBotChat.novoBot();
        meubot.setMensagemInicial("Olá! Eu sou o chatbot Alô Mundo! Escreva qualquer coisa e responderei com `Alô`.");
        meubot.start(8083,8084);
    }

    @EstadoChat(inicial = true)
    public void aloMundo(Contexto chat) {
        String msg = chat.getMensagemUsuario();
        chat.responder("Alô "+msg);
    }
}
