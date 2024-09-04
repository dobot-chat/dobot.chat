package com.mychat2.app;

import com.mychat2.anotacoes.Chatbot;
import com.mychat2.config.YormConfig;
import com.mychat2.controlador.MyChatControlador;
import com.mychat2.dominio.MyChat;
import com.mychat2.dominio.MyChatTema;
import com.mychat2.excecao.MyChatExcecao;
import com.mychat2.utils.AnotacoesUtil;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.nio.charset.StandardCharsets;

public class MyChatApp {

    private static final Logger logger = LoggerFactory.getLogger(MyChatApp.class);
    private static String mensagemInicial;
    private static MyChatTema myChatTema = criarTemaPadrao();

    public static void start() {
        start(8080, 8082);
    }

    public static void start(int portaMyChat, int portaH2) {
        try {
            Javalin app = Javalin.create(config -> {
                config.staticFiles.add(staticFileConfig -> {
                    staticFileConfig.directory = "/WEB-INF/publico";
                    staticFileConfig.location = Location.CLASSPATH;
                });
                config.fileRenderer(new JavalinThymeleaf(criarThymeleafEngine()));
            }).start(portaMyChat);

            YormConfig.start(portaH2);

            Object chatbotImpl = AnotacoesUtil.buscarClasseChatbot();
            if (chatbotImpl == null) {
                throw new MyChatExcecao("Nenhuma classe anotada com @" + Chatbot.class.getSimpleName() + " foi encontrada!");
            }
            logger.info("Instância de {} criada.", chatbotImpl.getClass().getSimpleName());

            MyChat myChat = new MyChat(chatbotImpl, mensagemInicial, myChatTema);

            app.before(ctx -> {
                ctx.res().setCharacterEncoding(StandardCharsets.UTF_8.name());
                ctx.res().setContentType("text/html; charset=UTF-8");
            });

            MyChatControlador controlador = new MyChatControlador(myChat);

            app.get("/", ctx -> ctx.render("/home.html", controlador.processarPaginaHome()));
            app.get("/chatbot", ctx -> ctx.render("/chat.html", controlador.processarGetPaginaChat()));
            app.post("/chatbot", ctx -> ctx.render("/chat.html", controlador.processarPostPaginaChat(ctx)));

            logger.info("Aplicação inicializada com sucesso!");
        } catch (Exception e) {
            logger.error("Falha durante a inicialização da aplicação!", e);
            System.exit(1);
        }
    }

    private static MyChatTema criarTemaPadrao() {
        MyChatTema tema = new MyChatTema();
        tema.setCorFundoPagina("#0d0d0d");
        tema.setCorTextoTitulo("#1abc9c");
        tema.setCorFundoChat("#222");
        tema.setCorTextoChat("#ffffff");
        tema.setCorFundoMensagemUsuario("#34495e");
        tema.setCorFundoMensagemBot("#1abc9c");

        return tema;
    }

    private static TemplateEngine criarThymeleafEngine() {
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        return templateEngine;
    }

    private static ITemplateResolver templateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");
        return templateResolver;
    }

    public static void setMensagemInicial(String mensagemInicial) {
        MyChatApp.mensagemInicial = mensagemInicial;
    }

    public static String getMensagemInicial() {
        return mensagemInicial;
    }

    public static MyChatTema getMyChatTema() {
        return myChatTema;
    }

    public static void setMyChatTema(MyChatTema myChatTema) {
        MyChatApp.myChatTema = myChatTema;
    }
}
