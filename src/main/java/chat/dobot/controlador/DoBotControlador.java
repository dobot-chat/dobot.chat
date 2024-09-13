package chat.dobot.controlador;

import chat.dobot.dominio.Contexto;
import chat.dobot.dominio.DoBot;
import chat.dobot.servico.DoBotServico;
import chat.dobot.utils.AnotacoesUtil;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class DoBotControlador {

    private final DoBot doBot;
    private final Map<String, DoBotServico<Record>> servicos;

    public DoBotControlador(DoBot doBot) {
        this.doBot = doBot;
        this.servicos = inicializarServicos();
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
        String estadoAtual = doBot.getEstadoAtual();

        String msgUsuario = ctx.formParam("msgUsuario");

        doBot.receberMensagem(new Contexto(msgUsuario, estadoAtual, servicos));

        Map<String, Object> model = new HashMap<>();
        model.put("mensagens", doBot.getMensagens());
        model.put("tema", doBot.getDoBotTema());

        return model;
    }

    private Map<String, DoBotServico<Record>> inicializarServicos() {
        Map<String, DoBotServico<Record>> servicosMap = new HashMap<>();

        AnotacoesUtil.buscarEntidades().forEach(entidade -> {
            DoBotServico<Record> servico = new DoBotServico<>(entidade);
            servicosMap.put(entidade.getSimpleName(), servico);
        });

        return servicosMap;
    }
}
