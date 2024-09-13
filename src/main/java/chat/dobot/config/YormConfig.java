package chat.dobot.config;

import chat.dobot.utils.AnotacoesUtil;
import chat.dobot.utils.CriaTabelasUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yorm.Yorm;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class YormConfig {

    private static final Logger logger = LoggerFactory.getLogger(YormConfig.class);
    private static Yorm yorm;

    public static void start(int portaH2) {
        try {
            Server.createWebServer("-webPort", String.valueOf(portaH2)).start();
            logger.debug("Banco de dados H2 iniciado com sucesso.");
        } catch (SQLException e) {
            logger.error("Falha ao iniciar o banco de dados H2!", e);
            System.exit(1);
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:chatbotdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        config.setUsername("sa");
        config.setPassword("");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        DataSource dataSource = new HikariDataSource(config);

        criarTabelas(dataSource);
        yorm = new Yorm(dataSource);
    }

    private static void criarTabelas(DataSource dataSource) {
        List<Class<Record>> entidades = AnotacoesUtil.buscarEntidades();

        try {
            if (!entidades.isEmpty()) {
                CriaTabelasUtil.criarTabelas(dataSource, entidades);
                logger.info("Tabelas criadas com sucesso! Classes mapeadas: {}", Arrays.toString(entidades.toArray()));
            }
        } catch (Exception e) {
            logger.error("Falha na criação de tabelas no banco!", e);
            System.exit(1);
        }
    }

    public static Yorm getYorm() {
        return yorm;
    }
}
