package chat.dobot.app;

import chat.dobot.config.YormConfig;
import chat.dobot.controlador.DoBotControlador;
import chat.dobot.dominio.DoBot;
import chat.dobot.dominio.DoBotTema;
import chat.dobot.utils.AnotacoesUtil;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.nio.charset.StandardCharsets;

public class DoBotApp {

    private static final Logger logger = LoggerFactory.getLogger(DoBotApp.class);
    private static String mensagemInicial;
    private static DoBotTema tema = criarTemaPadrao();

    public static void start() {
        start(8080, 8082);
    }

    public static void start(int portaDoBot, int portaH2) {
        try {
            Javalin app = Javalin.create(config -> {
                config.staticFiles.add(staticFileConfig -> {
                    staticFileConfig.directory = "/WEB-INF/publico";
                    staticFileConfig.location = Location.CLASSPATH;
                });
                config.fileRenderer(new JavalinThymeleaf(criarThymeleafEngine()));
            }).start(portaDoBot);

            YormConfig.start(portaH2);

            Object chatbotImpl = AnotacoesUtil.buscarClasseChatbot();
            if (chatbotImpl == null) {
                throw new DoBotException("Nenhuma classe anotada com @" + DoBot.class.getSimpleName() + " foi encontrada!");
            }
            logger.info("Instância de {} criada.", chatbotImpl.getClass().getSimpleName());

            DoBot doBot = new DoBot(chatbotImpl, mensagemInicial, tema);

            app.before(ctx -> {
                ctx.res().setCharacterEncoding(StandardCharsets.UTF_8.name());
                ctx.res().setContentType("text/html; charset=UTF-8");
            });

            DoBotControlador controlador = new DoBotControlador(doBot);

            app.get("/", ctx -> ctx.render("/home.html", controlador.processarPaginaHome()));
            app.get("/chatbot", ctx -> ctx.render("/chat.html", controlador.processarGetPaginaChat()));
            app.post("/chatbot", ctx -> ctx.render("/chat.html", controlador.processarPostPaginaChat(ctx)));

            logger.info("Aplicação inicializada com sucesso!");
        } catch (Exception e) {
            logger.error("Falha durante a inicialização da aplicação!", e);
            System.exit(1);
        }
    }

    private static DoBotTema criarTemaPadrao() {
        DoBotTema tema = new DoBotTema();
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
        DoBotApp.mensagemInicial = mensagemInicial;
    }

    public static String getMensagemInicial() {
        return mensagemInicial;
    }

    /**
     * Retorna o tema do chatbot.
     *
     * @return o tema do chatbot
     */
    public static DoBotTema getTema() {
        return tema;
    }


}
