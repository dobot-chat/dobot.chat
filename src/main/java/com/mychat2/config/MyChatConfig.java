package com.mychat2.config;

import com.mychat2.controlador.MyChatControlador;
import com.mychat2.dominio.MyChat;
import com.mychat2.exception.MyChatExcecao;
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

public class MyChatConfig {

    private static final Logger logger = LoggerFactory.getLogger(MyChatConfig.class);

    public static void start() {
        try {
            Javalin app = Javalin.create(config -> {
                config.staticFiles.add(staticFileConfig -> {
                    staticFileConfig.directory = "/WEB-INF/publico";
                    staticFileConfig.location = Location.CLASSPATH;
                });
                config.fileRenderer(new JavalinThymeleaf(createThymeleafEngine()));
            }).start(8080);

            Object chatbotImpl = AnotacoesUtil.buscarClasseChatbot();
            if (chatbotImpl == null) {
                throw new MyChatExcecao("Nenhuma classe anotada com @Chatbot foi encontrada!");
            }
            logger.info("Instância de {} criada.", chatbotImpl.getClass().getSimpleName());

            MyChat myChat = new MyChat(chatbotImpl);

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
        }
    }

    private static TemplateEngine createThymeleafEngine() {
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
}
