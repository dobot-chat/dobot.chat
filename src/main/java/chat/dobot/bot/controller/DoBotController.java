package chat.dobot.bot.controller;

import chat.dobot.bot.DoBotKey;
import chat.dobot.bot.Contexto;
import chat.dobot.bot.domain.DoBot;
import chat.dobot.bot.service.DoBotService;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class DoBotController {

    private final DoBot doBot;
    private static final Logger logger = LoggerFactory.getLogger(DoBotController.class);

    public DoBotController(DoBot doBot) {
        this.doBot = doBot;
    }

    public Map<String, Object> processarPaginaHome() {
        Map<String, Object> model = new HashMap<>();

        model.put("tema", doBot.getDoBotTema());
        model.put("nomeChat", doBot.getNome());

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

        try{
            doBot.receberMensagem(new Contexto(msgUsuario, estadoAtual, servicos));
        } catch (RuntimeException e) {
            logger.error("Erro no processamento da mensagem:",e);
            System.exit(1);
        } catch (Exception e) {
            //TODO : segmentar os erros possíveis e tratar de forma específica
            logger.debug("Erro ao processar mensagem do usuário", e);
            System.out.println("ERRO:\nErro ao processar mensagem do usuário:\n" + e.getMessage());
        }

        Map<String, Object> model = new HashMap<>();
        model.put("mensagens", doBot.getMensagens());
        model.put("tema", doBot.getDoBotTema());

        return model;
    }

}
