package chat.dobot.bot;

import chat.dobot.bot.persistance.YormConfig;
import chat.dobot.bot.controller.DoBotController;
import chat.dobot.bot.domain.DoBot;
import chat.dobot.bot.domain.DoBotTema;
import chat.dobot.bot.service.DoBotService;
import chat.dobot.bot.utils.AnnotationsUtil;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.yorm.Yorm;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class DoBotChat {

    private final Logger logger = LoggerFactory.getLogger(DoBotChat.class);
    private String mensagemInicial;
    private DoBotTema tema;

    private DoBotChat() {
        this.tema = criarTemaPadrao();
    }

    public static DoBotChat novoBot() {
        return new DoBotChat();
    }

    public void start() {
        start(8080, 8082);
    }

    public void start(int portaDoBot, int portaH2) {
        try {

            YormConfig yormConfig = new YormConfig(portaH2);

            Javalin app = Javalin.create(config -> {
                // Registra os serviços no contexto da aplicação
                config.appData(DoBotKey.SERVICE.key(), inicializarServicos(yormConfig.getYorm()));

                config.staticFiles.add(staticFileConfig -> {
                    staticFileConfig.directory = "/WEB-INF/publico";
                    staticFileConfig.location = Location.CLASSPATH;
                });
                config.fileRenderer(new JavalinThymeleaf(criarThymeleafEngine()));
            }).start(portaDoBot);


            Object chatbotImpl = AnnotationsUtil.buscarClasseChatbot();
            if (chatbotImpl == null) {
                throw new DoBotException("Nenhuma classe anotada com @" + DoBot.class.getSimpleName() + " foi encontrada!");
            }
            logger.info("Instância de {} criada.", chatbotImpl.getClass().getSimpleName());

            DoBot doBot = new DoBot(chatbotImpl, mensagemInicial, tema);

            app.before(ctx -> {
                ctx.res().setCharacterEncoding(StandardCharsets.UTF_8.name());
                ctx.res().setContentType("text/html; charset=UTF-8");
            });

            DoBotController controlador = new DoBotController(doBot);

            app.get("/", ctx -> ctx.render("/home.html", controlador.processarPaginaHome()));
            app.get("/chatbot", ctx -> ctx.render("/chat.html", controlador.processarGetPaginaChat()));
            app.post("/chatbot", ctx -> ctx.render("/chat.html", controlador.processarPostPaginaChat(ctx)));

            logger.info("Aplicação inicializada com sucesso!");
        } catch (Exception e) {
            logger.error("Falha durante a inicialização da aplicação!", e);
            System.exit(1);
        }
    }

    private DoBotTema criarTemaPadrao() {
        DoBotTema tema = new DoBotTema();
        tema.setCorFundoPagina("#0d0d0d");
        tema.setCorTextoTitulo("#1abc9c");
        tema.setCorFundoChat("#222");
        tema.setCorTextoChat("#ffffff");
        tema.setCorFundoMensagemUsuario("#34495e");
        tema.setCorFundoMensagemBot("#1abc9c");

        return tema;
    }

    private TemplateEngine criarThymeleafEngine() {
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        return templateEngine;
    }

    private ITemplateResolver templateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");
        return templateResolver;
    }

    public void setMensagemInicial(String mensagemInicial) {
        this.mensagemInicial = mensagemInicial;
    }

    public String getMensagemInicial() {
        return mensagemInicial;
    }

    /**
     * Retorna o tema do chatbot.
     *
     * @return o tema do chatbot
     */
    public DoBotTema getTema() {
        return tema;
    }

    private Map<String, DoBotService<Record>> inicializarServicos(Yorm yorm) {
        Map<String, DoBotService<Record>> servicosMap = new HashMap<>();

        AnnotationsUtil.buscarEntidades().forEach(entidade -> {
            DoBotService<Record> servico = new DoBotService<>(entidade,yorm);
            servicosMap.put(entidade.getSimpleName(), servico);
        });

        return servicosMap;
    }

}
