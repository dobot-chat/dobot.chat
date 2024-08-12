package com.mychat2.config;

import com.mychat2.controllers.ChatbotController;
import com.mychat2.domain.MeuChat;
import com.mychat2.util.BuscaAnotacoesUtil;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.nio.charset.StandardCharsets;

public class JavalinConfig {

    private static final Logger logger = LoggerFactory.getLogger(JavalinConfig.class);

    public static void start() {
        try {
            Javalin app = Javalin.create(config -> {
                config.staticFiles.add(staticFileConfig -> {
                    staticFileConfig.directory = "/WEB-INF/public";
                    staticFileConfig.location = Location.CLASSPATH;
                });
                config.fileRenderer(new JavalinThymeleaf(createThymeleafEngine()));
            }).start(8080);

            MeuChat meuChat = BuscaAnotacoesUtil.buscarClasseChatbot();
            if (meuChat == null) {
                logger.error("Nenhuma classe extendendo de MeuChat e anotada com @ChatBot foi encontrada.");
                return;
            }
            logger.info("Instância de {} criada", meuChat.getClass().getSimpleName());

            app.before(ctx -> {
                ctx.attribute("meuChat", meuChat);
                ctx.res().setCharacterEncoding(StandardCharsets.UTF_8.name());
                ctx.res().setContentType("text/html; charset=UTF-8");
            });

            ChatbotController controller = new ChatbotController(meuChat);

            app.get("/", ctx -> ctx.render("/index.html", controller.processHomePage(ctx)));
            app.get("/chatbot", ctx -> ctx.render("/chatbot.html"));
            app.post("/chatbot", ctx -> ctx.render("/chatbot.html", controller.processChatbotPage(ctx)));

            logger.info("Aplicação inicializada com sucesso!");
        } catch (Exception e) {
            logger.error("Erro durante a inicialização da aplicação!", e);
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
