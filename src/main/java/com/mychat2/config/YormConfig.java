package com.mychat2.config;

import com.mychat2.utils.AnotacoesUtil;
import com.mychat2.utils.CriaTabelasUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yorm.Yorm;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class YormConfig {

    private static final Logger logger = LoggerFactory.getLogger(YormConfig.class);
    private static final Yorm yorm;

    static {
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
        List<Class<?>> entidades = AnotacoesUtil.buscarEntidades();

        try {
            if (!entidades.isEmpty()) {
                CriaTabelasUtil.criarTabelas(dataSource, entidades);
                logger.info("Tabelas criadas com sucesso! Classes mapeadas: {}", Arrays.toString(entidades.toArray()));
            }
        } catch (SQLException e) {
            logger.error("Falha na criação de tabelas no banco!", e);
        }
    }

    public static Yorm getYorm() {
        return yorm;
    }
}
