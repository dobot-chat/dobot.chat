package com.mychat2.controlador;

import com.mychat2.dominio.Contexto;
import com.mychat2.dominio.MyChat;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class ChatbotControlador {

    private final MyChat myChat;

    public ChatbotControlador(MyChat myChat) {
        this.myChat = myChat;
    }

    public Map<String, Object> processarGetPaginaChat() {
        Map<String, Object> model = new HashMap<>();
        model.put("mensagens", myChat.getMensagens());

        return model;
    }

    public Map<String, Object> processarPostPaginaChat(Context ctx) {
        String estadoAtual = myChat.getEstadoAtual();

        String msgUsuario = ctx.formParam("msgUsuario");

        myChat.receberMensagem(new Contexto(msgUsuario, estadoAtual));

        Map<String, Object> model = new HashMap<>();
        model.put("mensagens", myChat.getMensagens());

        return model;
    }

    public Map<String, Object> processarPaginaHome() {
        Map<String, Object> model = new HashMap<>();

        if (myChat != null) {
            String nomeChat = myChat.getChatbot().getClass().getSimpleName();
            model.put("nomeChat", nomeChat);
        }

        return model;
    }
}
