package com.mychat2.controllers;

import com.mychat2.domain.Contexto;
import com.mychat2.domain.MeuChat;
import io.javalin.http.Context;
import org.yorm.exception.YormException;

import java.util.HashMap;
import java.util.Map;

public class ChatbotController {

    private final MeuChat meuChat;

    public ChatbotController(MeuChat meuChat) {
        this.meuChat = meuChat;
    }

    public Map<String, Object> processGetChatbotPage() {
        Map<String, Object> model = new HashMap<>();
        model.put("mensagens", meuChat.getMensagens());

        return model;
    }

    public Map<String, Object> processPostChatbotPage(Context ctx) throws YormException {
        String estadoAtual = ctx.sessionAttribute("estadoChatbot");
        if (estadoAtual == null) {
            estadoAtual = "inicial";
        }

        String entrada = ctx.formParam("userInput");

        meuChat.receberMensagem(new Contexto(entrada, estadoAtual));

        ctx.sessionAttribute("estadoChatbot", meuChat.getEstadoAtual());

        Map<String, Object> model = new HashMap<>();
        model.put("mensagens", meuChat.getMensagens());

        return model;
    }

    public Map<String, Object> processHomePage(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        MeuChat meuChat = ctx.attribute("meuChat");

        if (meuChat != null) {
            String nomeChat = meuChat.getClass().getSimpleName();
            model.put("nomeChat", nomeChat);
        }
        return model;
    }
}
