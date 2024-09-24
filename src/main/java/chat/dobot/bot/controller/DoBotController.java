package chat.dobot.bot.controller;

import chat.dobot.bot.Autor;
import chat.dobot.bot.DoBotException;
import chat.dobot.bot.DoBotKey;
import chat.dobot.bot.Contexto;
import chat.dobot.bot.domain.DoBot;
import chat.dobot.bot.domain.DoBotTema;
import chat.dobot.bot.domain.EstadoInvalidoException;
import chat.dobot.bot.service.DoBotService;
import chat.dobot.bot.utils.ConsoleUtil;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class DoBotController {

    private static final Logger logger = LoggerFactory.getLogger(DoBotController.class);

    public DoBotController() {
    }

    public void processarPaginaHome(Context ctx) {
        Map<String, DoBot> bots = ctx.appData(DoBotKey.BOTS.key());
        Map<String, String> botNames = new HashMap<>();
        for (Map.Entry<String, DoBot> entry : bots.entrySet()) {
            botNames.put(entry.getKey(), entry.getValue().getNome());
        }

        Map<String, Object> model = new HashMap<>();
        model.put("botNames", botNames);
        model.put("tema", new DoBotTema());
        model.put("nomeChat", "DoBot.chat");

        ctx.render("home.html", model);
    }

    private DoBot getBotFrom(Context ctx){
        String nomeBot = ctx.pathParam("botID");
        Map<String, DoBot> bots = ctx.appData(DoBotKey.BOTS.key());
        if(!bots.containsKey(nomeBot)){
            ConsoleUtil.printErro("BUG?! Bot não encontrado:"+nomeBot);
            throw new DoBotException("Bot não encontrado:"+nomeBot);
        }
        ctx.attribute("botID", nomeBot);
        return bots.get(nomeBot);
    }

    /**
     * Exibe a página do chat
     * @param ctx contexto do JavaLin
     */
    public void processarGetPaginaChat(Context ctx) {
        DoBot doBot = getBotFrom(ctx);
        Map<String, Object> model = new HashMap<>();
        model.put("mensagens", doBot.getMensagens());
        model.put("tema", doBot.getDoBotTema());
        ctx.render("chat.html", model);
    }

    /**
     * Processa mensagem enviada pelo usuário
     * @param ctx contexto do JavaLin
     */
    public void processarPostPaginaChat(Context ctx) {
        DoBot doBot = getBotFrom(ctx);
        Map<String, DoBotService<Record>> servicos = ctx.appData(DoBotKey.SERVICE.key());
        String estadoAtual = doBot.getEstadoAtual();

        String msgUsuario = ctx.formParam("msgUsuario");
        if (msgUsuario == null)
            throw new RuntimeException("Bug?! msg do usuário não deveria ser null!");

        try {
            doBot.receberMensagem(new Contexto(msgUsuario, estadoAtual, servicos));
        } catch (EstadoInvalidoException e){
            logger.error("Erro no processamento da mensagem:",e);
            ConsoleUtil.printErro("Erro no processamento da mensagem!",e);
            doBot.addMensagem(Autor.BOT, "🌿👀🌿\n Oops! Ocorreu um erro no processamento da mensagem. <br/> Estou retornando para o estado inicial. 😬");
            doBot.addMensagem(Autor.BOT, doBot.getConfig().getMensagemInicial());
            doBot.setEstadoAtual(DoBot.ESTADO_INICIAL);

        } catch (DoBotException e) {
            logger.error("Erro no processamento da mensagem:",e);
            ConsoleUtil.printErro("Erro no processamento da mensagem!",e);
            doBot.addMensagem(Autor.BOT, "🚨🚨 🌿👀🌿 <br/> Oops! Ocorreu um erro no processamento da mensagem.");
        } catch (Exception e) {
            logger.error("Erro inesperado no processamento da mensagem!",e);
            ConsoleUtil.printErro("Erro Inesperado no processamento da mensagem: ",e);
            doBot.addMensagem(Autor.BOT, "🚨🚨🚨<br/> Oops! Ocorreu um erro inesperado no processamento da mensagem.");
        }

        Map<String, Object> model = new HashMap<>();
        model.put("mensagens", doBot.getMensagens());
        model.put("tema", doBot.getDoBotTema());

        ctx.render("chat.html", model);
    }

}
