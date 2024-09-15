package chat.dobot.bot.persistance;

import chat.dobot.bot.utils.AnnotationsUtil;
import chat.dobot.bot.utils.CriaTabelasUtil;
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

/**
 * Classe de configuração do (<a href="https://naynecoder.github.io/yorm/">Yorm</a>)
 * para o banco de dados (<a href="https://www.h2database.com/">H2</a>).
 * O Yorm é um framework de mapeamento objeto-relacional (ORM) que facilita a
 * persistência de objetos Java em bancos de dados relacionais.
 */
public class YormConfig {

    private final Logger logger = LoggerFactory.getLogger(YormConfig.class);
    private Yorm yorm;

    public YormConfig(int portaH2) {
        start(portaH2);
    }

    private void start(int portaH2) {
        try {
            Server.createWebServer("-webPort", String.valueOf(portaH2)).start();
            logger.debug("Banco de dados H2 iniciado com sucesso.");
        } catch (SQLException e) {
            logger.error("Falha ao iniciar o banco de dados H2!", e);
            System.exit(1);
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:dobotdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        config.setUsername("dobot");
        config.setPassword("");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        DataSource dataSource = new HikariDataSource(config);

        criarTabelas(dataSource);
        yorm = new Yorm(dataSource);
    }

    private void criarTabelas(DataSource dataSource) {
        List<Class<Record>> entidades = AnnotationsUtil.buscarEntidades();

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

    public Yorm getYorm() {
        return yorm;
    }
}