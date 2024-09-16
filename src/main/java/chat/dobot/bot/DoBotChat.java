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
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.yorm.Yorm;

import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

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
            //Imprime o logo do DoBot
            System.out.println(getdoBotAsciiArt());
            System.out.println("Versão: " + getApplicationVersion());
            YormConfig yormConfig = new YormConfig(portaH2);

            // Inicializa o Javalin
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
                logger.debug("Nenhuma classe anotada com @" + DoBot.class.getSimpleName() + " foi encontrada!");
                System.out.println("ERRO: Não foi possível inicializar o DoBot. \n");
                System.out.println("Não encontrei nenhuma classe anotada com @" + DoBot.class.getSimpleName());
                System.exit(1);
            }
            logger.debug("Instância de {} criada.", chatbotImpl.getClass().getSimpleName());

            DoBot doBot = new DoBot(chatbotImpl, mapearEstados(chatbotImpl), mensagemInicial, tema);

            app.before(ctx -> {
                ctx.res().setCharacterEncoding(StandardCharsets.UTF_8.name());
                ctx.res().setContentType("text/html; charset=UTF-8");
            });

            DoBotController controlador = new DoBotController(doBot);

            app.get("/", ctx -> ctx.render("/home.html", controlador.processarPaginaHome()));
            app.get("/chatbot", ctx -> ctx.render("/chat.html", controlador.processarGetPaginaChat()));
            app.post("/chatbot", ctx -> ctx.render("/chat.html", controlador.processarPostPaginaChat(ctx)));

            logger.debug("Aplicação inicializada com sucesso!");
            System.out.println("Aplicação inicializada com sucesso!\nAcesse http://localhost:" + portaDoBot + " para acessar o chatbot.");
        } catch (Exception e) {
            logger.error("Falha durante a inicialização da aplicação!", e);
            System.exit(1);
        }
    }

    private DoBotTema criarTemaPadrao() {
        DoBotTema tema = new DoBotTema();
        tema.setCorFundoPagina("#ffffff");
        tema.setCorTextoTitulo("#000000");
        tema.setCorFundoChat("#D5D5D5");
        tema.setCorTextoChat("#000000");
        tema.setCorFundoMensagemUsuario("#FEB600");
        tema.setCorFundoMensagemBot("#EE8134");

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

    private String getdoBotAsciiArt() {
        StringBuffer asciiArt = new StringBuffer();

        asciiArt.append("  ____        ____        _         _           _   \n");
        asciiArt.append(" |  _ \\  ___ | __ )  ___ | |_   ___| |__   __ _| |_ \n");
        asciiArt.append(" | | | |/ _ \\|  _ \\ / _ \\| __| / __| '_ \\ / _` | __|\n");
        asciiArt.append(" | |_| | (_) | |_) | (_) | |_ | (__| | | | (_| | |_ \n");
        asciiArt.append(" |____/ \\___/|____/ \\___/ \\__(_)___|_| |_|\\__,_|\\__|\n");

        return asciiArt.toString();
    }

    private String getApplicationVersion() {
        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader("pom.xml"));
            return model.getVersion();
        } catch (Exception e) {
            logger.error("Erro ao ler a versão do pom.xml", e);
            return "desconhecida";
        }
    }

    private Map<String, Consumer<Contexto>> mapearEstados(Object chatbot) {
        logger.debug("Iniciando mapeamento dos estados para {}.", chatbot.getClass().getSimpleName());
        Map<String, Consumer<Contexto>> estados = AnnotationsUtil.mapearEstados(chatbot);

        if (estados.isEmpty()) {
            throw new DoBotException("Nenhum estado mapeado para " + chatbot.getClass().getSimpleName() + "!");
        }

        logger.debug("Mapeamento dos estados concluído com sucesso.");
        logger.debug("Mapeamento de estados encontrados: {}", estados.keySet());
        return estados;
    }
}
