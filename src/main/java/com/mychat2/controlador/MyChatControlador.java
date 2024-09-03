package com.mychat2.controlador;

import com.mychat2.dominio.Contexto;
import com.mychat2.dominio.MyChat;
import com.mychat2.servico.MyChatServico;
import com.mychat2.utils.AnotacoesUtil;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class MyChatControlador {

    private final MyChat myChat;
    private final Map<String, MyChatServico<Record>> servicos;

    public MyChatControlador(MyChat myChat) {
        this.myChat = myChat;
        this.servicos = inicializarServicos();
    }

    public Map<String, Object> processarGetPaginaChat() {
        Map<String, Object> model = new HashMap<>();
        model.put("mensagens", myChat.getMensagens());

        return model;
    }

    public Map<String, Object> processarPostPaginaChat(Context ctx) {
        String estadoAtual = myChat.getEstadoAtual();

        String msgUsuario = ctx.formParam("msgUsuario");

        myChat.receberMensagem(new Contexto(msgUsuario, estadoAtual, servicos));

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

    private Map<String, MyChatServico<Record>> inicializarServicos() {
        Map<String, MyChatServico<Record>> servicosMap = new HashMap<>();

        AnotacoesUtil.buscarEntidades().forEach(entidade -> {
            MyChatServico<Record> servico = new MyChatServico<>(entidade);
            servicosMap.put(entidade.getSimpleName(), servico);
        });

        return servicosMap;
    }
}
