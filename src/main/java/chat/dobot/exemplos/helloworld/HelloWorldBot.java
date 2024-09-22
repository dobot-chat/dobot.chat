package chat.dobot.exemplos.helloworld;

import chat.dobot.bot.DoBotChat;
import chat.dobot.bot.annotations.DoBot;
import chat.dobot.bot.annotations.EstadoChat;
import chat.dobot.bot.Contexto;

//@DoBot
public class HelloWorldBot {

    public static void main(String[] args) {
        DoBotChat meubot = DoBotChat.novoBot();
        meubot.setMensagemInicial("Olá! Eu sou o chatbot Alô Mundo! Escreva qualquer coisa e responderei com `Alô`.");
        meubot.start(8083,8084);
    }

    //@EstadoChat(inicial = true)
    public void aloMundo(Contexto chat) {
        String msg = chat.getMensagemUsuario();
        chat.responder("Alô "+msg);
        chat.responder("Vou mostrar o menu na próxima mensagem.");
        chat.mudarEstado("menu");
    }

    //@EstadoChat(estado = "menu")
    public void menu(Contexto chat) {
        chat.responder("Menu: \n1 - Alô Mundo\n2 - Sair");
        chat.mudarEstado("opcao");
    }
}
