package chat.dobot.bot.utils;

import chat.dobot.bot.annotations.Id;
import chat.dobot.bot.domain.DoBot;
import chat.dobot.bot.DoBotException;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CriaTabelasUtil {


    //TODO : criar exceção específica para persistência: "DoBotPersistenciaException"



    public static void criarTabelas(DataSource dataSource, List<Class<Record>> entidades) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            for (Class<Record> entidade : entidades) {
                String nomeTabela = entidade.getSimpleName().toLowerCase();
                StringBuilder criaTableSQL = new StringBuilder("CREATE TABLE IF NOT EXISTS " + nomeTabela + " (");

                Field[] fields = entidade.getDeclaredFields();
                boolean idEncontrado = false;

                for (Field field : fields) {
                    if (field.isAnnotationPresent(Id.class)) {
                        criaTableSQL.append(field.getName()).append(" ").append(getSQLTipo(field.getType())).append(" PRIMARY KEY AUTO_INCREMENT, ");
                        idEncontrado = true;
                    } else {
                        criaTableSQL.append(field.getName()).append(" ").append(getSQLTipo(field.getType())).append(", ");
                    }
                }

                if (!idEncontrado) {
                    throw new DoBotException("A entidade " + entidade.getName() + " deve ter um campo anotado com " + Id.class.getName() + "!");
                }

                criaTableSQL.setLength(criaTableSQL.length() - 2);
                criaTableSQL.append(");");

                statement.executeUpdate(criaTableSQL.toString());
            }
        }
    }

    private static String getSQLTipo(Class<?> tipo) {
        if (tipo == String.class) {
            return "VARCHAR(255)";
        } else if (tipo == int.class || tipo == Integer.class) {
            return "INT";
        } else if (tipo == long.class || tipo == Long.class) {
            return "BIGINT";
        } else if (tipo == boolean.class || tipo == Boolean.class) {
            return "BOOLEAN";
        } else if (tipo == double.class || tipo == Double.class) {
            return "DOUBLE";
        } else if (tipo == LocalDateTime.class) {
            return "TIMESTAMP";
        } else if (tipo == LocalDate.class) {
            return "DATE";
        } else {
            throw new DoBotException("O tipo de dado " + tipo.getName() + " não é suportado para operações de banco no " + DoBot.class.getSimpleName() + "!");
        }
    }
}
