package chat.dobot.bot;


import chat.dobot.bot.annotations.Config;
import chat.dobot.bot.annotations.DoBotChat;
import chat.dobot.bot.annotations.EstadoChat;
import chat.dobot.bot.controller.DoBotController;
import chat.dobot.bot.domain.DoBot;
import chat.dobot.bot.persistance.YormConfig;
import chat.dobot.bot.service.DoBotService;
import chat.dobot.bot.utils.AnnotationsUtil;
import chat.dobot.bot.utils.ConsoleUtil;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.yorm.Yorm;

import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DoBotChatApp {


    private static final String PACOTE_EXEMPLOS = "chat.dobot";
    private final Logger logger = LoggerFactory.getLogger(DoBotChatApp.class);

    private boolean carregarExemplos = false;

    private DoBotChatApp() {
    }

    public static DoBotChatApp novoBot() {
        return new DoBotChatApp();
    }

    public void start() {
        start(8080, 8082);
    }


    public void ativarExemplos() {
        carregarExemplos = true;
    }

    /**
     * Inicializa a aplicação DoBotChat.
     *
     * @param portaDoBot a porta do servidor DoBot
     * @param portaH2    a porta do banco de dados H2
     */
    public void start(int portaDoBot, int portaH2) {
        if (portaDoBot < 0 || portaH2 < 0) {
            ConsoleUtil.printErro("As portas do DoBot e do banco de dados devem ser maiores que 0");
        }
        if (portaDoBot == portaH2) {
            ConsoleUtil.printErro("As portas do DoBot e do banco de dados devem ser diferentes. Exemplo: 8080 e 8082");
        }
        try {
            //Imprime a logo e a versão do DoBotChat
            ConsoleUtil.printYellow(getdoBotAsciiArt());
            ConsoleUtil.printYellow("DoBotChat v" + getApplicationVersion());

            // Configuração do Yorm
            YormConfig yormConfig = new YormConfig(portaH2);


            // Carregar instâncias de chatbots
            Map<String, DoBot> bots = carregarInstanciasChatbot();
            if (bots.isEmpty())
                throw new DoBotException("Nenhuma classe anotada com @DoBotChat foi encontrada");

            logger.debug(bots.size()+" chatBots instanciados: {}.", bots.keySet());

            // Inicializa o Javalin
            Javalin app = Javalin.create(config -> {
                // Registra os serviços no contexto da aplicação
                config.appData(DoBotKey.SERVICE.key(), inicializarPersistencia(yormConfig.getYorm()));

                config.staticFiles.add(staticFileConfig -> {
                    staticFileConfig.directory = "/WEB-INF/publico";
                    staticFileConfig.location = Location.CLASSPATH;
                });
                config.appData(DoBotKey.BOTS.key(), bots);

                TemplateEngine templateEngine = new TemplateEngine();
                templateEngine.setTemplateResolver(templateResolver());
                config.fileRenderer(new JavalinThymeleaf(templateEngine));
            }).start(portaDoBot);

            app.before(ctx -> {
                ctx.res().setCharacterEncoding(StandardCharsets.UTF_8.name());
                ctx.res().setContentType("text/html; charset=UTF-8");
            });

            // TODO : inicializar controlador com a lista de bots
            DoBotController controlador = new DoBotController();

            app.get("/",controlador::processarPaginaHome);
            app.get("/chatbot/{botID}",controlador::processarGetPaginaChat);
            app.post("/chatbot/{botID}",controlador::processarPostPaginaChat);

            ConsoleUtil.printConsole("Aplicação inicializada com sucesso!\nAcesse http://localhost:" + portaDoBot + " para acessar o chatbot.");
        } catch (Exception e) {
            ConsoleUtil.printErro("Falha durante a inicialização da aplicação!", e);
            System.exit(1);
        }

    }

    private ITemplateResolver templateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");
        return templateResolver;
    }


    private Map<String, DoBotService<Record>> inicializarPersistencia(Yorm yorm) {
        Map<String, DoBotService<Record>> servicosMap = new HashMap<>();

        AnnotationsUtil.buscarEntidades().forEach(entidade -> {
            DoBotService<Record> servico = new DoBotService<>(entidade, yorm);
            servicosMap.put(entidade.getSimpleName(), servico);
        });

        return servicosMap;
    }

    /**
     * Busca as classes anotadas com @DoBotChat.
     * @throws DoBotException se ocorrer um erro ao instanciar alguma classe DoBot
     * @return um mapa com os bots carregados
     */
    private Map<String, DoBot> carregarInstanciasChatbot() {
        Map<String, DoBot> bots = new LinkedHashMap<>();
        ConsoleUtil.printConsole("Carregando chatbots...");

        try (ScanResult scanResult = new ClassGraph().enableAnnotationInfo().scan()) {
            for (Class<?> classe : scanResult.getClassesWithAnnotation(DoBotChat.class).loadClasses()) {
                if(classe.getName().startsWith(PACOTE_EXEMPLOS) && !carregarExemplos){
                    continue;
                }else{
                    ConsoleUtil.printConsole("Carregando exemplo: "+classe.getName());
                }
                Object instancia = classe.getDeclaredConstructor().newInstance();
                DoBotChat annotation = classe.getAnnotation(DoBotChat.class);
                if (annotation != null) {
                    String id = annotation.id();
                    String nome = annotation.nome();
                    String descricao = annotation.descricao();
                    DoBot novoBot = new DoBot(id, nome, descricao);
                    ConsoleUtil.printConsole("\n💬⚙️ Carregando chatbot '" + nome + "'("+id+")...");
                    try{
                        varrerMetodos(novoBot, instancia);
                    }catch (DoBotException e){
                        ConsoleUtil.printWarning(e.getMessage());
                        ConsoleUtil.printConsole("💬❌ Chatbot  '" + nome + "'("+id+") não foi inicializado!");
                        continue;
                    }
                    ConsoleUtil.printConsole("💬⚙️ Estados: "+novoBot.getEstados().toString());
                    ConsoleUtil.printConsole("💬✅ Chatbot '" + nome + "'("+id+") carregado com sucesso!");
                    bots.put(novoBot.getId(), novoBot);
                }
            }
        } catch (Exception e) {
            throw new DoBotException("Erro ao instanciar a classe chatbot:\n" + e.getMessage(), e);
        }
        return bots;
    }


    /**
     * Varre os métodos da classe anotada com @DoBotChat, mapeando os estados e configuração do bot.
     * Os estados são mapeados a partir dos métodos anotados com @EstadoChat, na classe anotada com @DoBotChat.
     * O método de configuração é mapeado a partir do método anotado com @Config.
     *
     * @param chatbotImpl objeto do usuario dev que contém a anotação @DoBotChat
     * @return um mapa com os estados do bot
     */
    private void varrerMetodos(DoBot bot, Object chatbotImpl) {
        Map<String, BotStateMethod> estadosMap = new HashMap<>();

        for (Method metodo : chatbotImpl.getClass().getDeclaredMethods()) {

            if(metodo.isAnnotationPresent(Config.class)){
                conferirAssinaturaMetodoConfig(metodo);
                try {
                    metodo.invoke(chatbotImpl, bot.getConfig());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new DoBotException("BUG!? Problema ao executar o método de configuração do bot. Método"+metodo.getName(), e);
                }
                bot.setMensagemInicial(bot.getConfig().getMensagemInicial());
            }

            if (metodo.isAnnotationPresent(EstadoChat.class)) {
                final String nomeEstado = getNomeEstado(metodo);
                if (estadosMap.containsKey(nomeEstado)) {
                    throw new DoBotException("Um mesmo estado não pode ser mapeado para mais de um método! Estado duplicado:" + nomeEstado);
                }

                //verificar se o método possui apenas um parâmetro do tipo Contexto
                conferirAssinaturaMetodoEstado(metodo);

                // Adiciona o método que implementa o estado do bot ao mapa de estados
                estadosMap.put(nomeEstado, contexto -> {
                    try {
                        metodo.invoke(chatbotImpl, contexto);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new DoBotException("BUG!? Problema ao executar o método que implementa estado ao bot. Estado:" + nomeEstado + ", método"+metodo.getName(), e);
                    }
                });
            }
        }
        if (estadosMap.isEmpty()) {
            throw new DoBotException("Nenhum estado mapeado para " + chatbotImpl.getClass().getSimpleName() + "!");
        }
        if (!estadosMap.containsKey("main")) {
            throw new DoBotException("Nenhum estado inicial definido!");
        }

        bot.setEstados(estadosMap);
    }

    private void conferirAssinaturaMetodoConfig(Method metodo) {
        String configName = DoBotConfig.class.getSimpleName();
        if (metodo.getParameterCount() != 1) {
            throw new DoBotException("Método " + metodo.getName() + " deve ter um parâmetro do tipo " + configName);
        }
        if (!metodo.getParameterTypes()[0].getSimpleName().equals(configName)) {
            throw new DoBotException("Método " + metodo.getName() + " deve ter um parâmetro do tipo " + configName + "\nExemplo: public void " + metodo.getName() + "(" + configName + " config)");
        }
    }


    /**
     * Recupera o nome do estado a partir da anotação do método
     *
     * @param metodo que contém anotação @EstadoChat
     * @return o nome do estado
     */
    @NotNull
    private String getNomeEstado(Method metodo) {
        EstadoChat estadoChat = metodo.getAnnotation(EstadoChat.class);
        if(estadoChat == null){
            throw new IllegalArgumentException("BUG! Método " + metodo.getName() + " deveria possuir anotação @EstadoChat!");
        }
        final String estado;
        if (estadoChat.inicial()) {
            estado = DoBot.ESTADO_INICIAL;
        } else {
            estado = estadoChat.estado().isBlank() ? metodo.getName().toLowerCase() : estadoChat.estado().toLowerCase();
        }
        return estado;
    }

    /**
     * Verifica se o método possui a assinatura correta.
     * O método deve ter apenas um parâmetro, do tipo Contexto.
     *
     * @param metodo que implementa um estado do chatbot
     */
    private void conferirAssinaturaMetodoEstado(Method metodo) {
        String contextoName = Contexto.class.getSimpleName();
        if (metodo.getParameterCount() != 1) {
            throw new DoBotException("Método " + metodo.getName() + " deve ter um parâmetro do tipo " + contextoName);
        }
        if (!metodo.getParameterTypes()[0].getSimpleName().equals(contextoName)) {
            throw new DoBotException("Método " + metodo.getName() + " deve ter um parâmetro do tipo " + contextoName + "\nExemplo: public void " + metodo.getName() + "(" + contextoName + " contexto)");
        }
    }

    /**
     * Retorna o logo do DoBotChat em ASCII Art.
     * Este método é utilizado para imprimir o logo do DoBotChat no console, no boot do sistema.
     *
     * @return o logo do DoBotChat em ASCII Art
     */
    private String getdoBotAsciiArt() {
        return """
                  ____        ____        _         _           _   
                 |  _ \\  ___ | __ )  ___ | |_   ___| |__   __ _| |_ 
                 | | | |/ _ \\|  _ \\ / _ \\| __| / __| '_ \\ / _` | __|
                 | |_| | (_) | |_) | (_) | |_ | (__| | | | (_| | |_ 
                 |____/ \\___/|____/ \\___/ \\__(_)___|_| |_|\\__,_|\\__|
                """;
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

}
