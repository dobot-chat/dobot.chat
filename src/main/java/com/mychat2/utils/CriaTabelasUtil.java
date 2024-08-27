package com.mychat2.utils;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CriaTabelasUtil {

    public static void criarTabelas(DataSource dataSource, List<Class<?>> entidades) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            for (Class<?> entidade : entidades) {
                String nomeTabela = entidade.getSimpleName().toLowerCase();
                StringBuilder criaTableSQL = new StringBuilder("CREATE TABLE IF NOT EXISTS " + nomeTabela + " (");

                Field[] fields = entidade.getDeclaredFields();
                for (Field field : fields) {
                    if (field.getName().equalsIgnoreCase("id")) {
                        criaTableSQL.append(field.getName()).append(" INT PRIMARY KEY AUTO_INCREMENT, ");
                    } else {
                        criaTableSQL.append(field.getName()).append(" ").append(getSQLTipo(field.getType())).append(", ");
                    }
                }
                criaTableSQL.setLength(criaTableSQL.length() - 2);
                criaTableSQL.append(");");

                statement.executeUpdate(criaTableSQL.toString());
            }
        }
    }

    private static String getSQLTipo(Class<?> type) {
        if (type == String.class) {
            return "VARCHAR(255)";
        } else if (type == int.class || type == Integer.class) {
            return "INT";
        } else if (type == long.class || type == Long.class) {
            return "BIGINT";
        } else if (type == boolean.class || type == Boolean.class) {
            return "BOOLEAN";
        } else if (type == double.class || type == Double.class) {
            return "DOUBLE";
        } else if (type == LocalDateTime.class) {
            return "TIMESTAMP";
        } else if (type == LocalDate.class) {
            return "DATE";
        } else {
            throw new IllegalArgumentException("Tipo de dado não suportado: " + type.getName());
        }
    }
}
