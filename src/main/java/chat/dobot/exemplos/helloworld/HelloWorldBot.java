package chat.dobot.exemplos.helloworld;

import chat.dobot.bot.DoBotChatApp;
import chat.dobot.bot.DoBotConfig;
import chat.dobot.bot.annotations.Config;
import chat.dobot.bot.annotations.DoBotChat;
import chat.dobot.bot.annotations.EstadoChat;
import chat.dobot.bot.Contexto;

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
        chat.responder("Vou mostrar o menu na próxima mensagem.");
        chat.mudarEstado("menu");
    }

    @EstadoChat(estado = "menu")
    public void menu(Contexto chat) {
        chat.responder("Menu: \n1 - Alô Mundo\n2 - Sair");
        chat.mudarEstado("opcao");
    }
}
