package chat.dobot.bot.controller;

import chat.dobot.bot.DoBotKey;
import chat.dobot.bot.domain.Contexto;
import chat.dobot.bot.domain.DoBot;
import chat.dobot.bot.service.DoBotService;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class DoBotController {

    private final DoBot doBot;

    public DoBotController(DoBot doBot) {
        this.doBot = doBot;
    }

    public Map<String, Object> processarPaginaHome() {
        Map<String, Object> model = new HashMap<>();

        model.put("tema", doBot.getDoBotTema());
        model.put("nomeChat", doBot.getChatbot().getClass().getSimpleName());

        return model;
    }

    public Map<String, Object> processarGetPaginaChat() {
        Map<String, Object> model = new HashMap<>();
        model.put("mensagens", doBot.getMensagens());
        model.put("tema", doBot.getDoBotTema());

        return model;
    }

    public Map<String, Object> processarPostPaginaChat(Context ctx) {
        Map<String, DoBotService<Record>> servicos = ctx.appData(DoBotKey.SERVICE.key());
        String estadoAtual = doBot.getEstadoAtual();

        String msgUsuario = ctx.formParam("msgUsuario");

        doBot.receberMensagem(new Contexto(msgUsuario, estadoAtual, servicos));

        Map<String, Object> model = new HashMap<>();
        model.put("mensagens", doBot.getMensagens());
        model.put("tema", doBot.getDoBotTema());

        return model;
    }

}
