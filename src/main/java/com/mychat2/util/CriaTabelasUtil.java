package com.mychat2.util;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CriaTabelasUtil {

    public static void criarTabelas(DataSource dataSource, List<Class<?>> recordsPersistencia) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            for (Class<?> recordClass : recordsPersistencia) {
                String tableName = recordClass.getSimpleName().toLowerCase();
                StringBuilder createTableSQL = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + " (");

                Field[] fields = recordClass.getDeclaredFields();
                for (Field field : fields) {
                    if (field.getName().equalsIgnoreCase("id")) {
                        createTableSQL.append(field.getName()).append(" INT PRIMARY KEY AUTO_INCREMENT, ");
                    } else {
                        createTableSQL.append(field.getName()).append(" ").append(getSQLType(field.getType())).append(", ");
                    }
                }
                createTableSQL.setLength(createTableSQL.length() - 2); // Remove a última vírgula
                createTableSQL.append(");");

                statement.executeUpdate(createTableSQL.toString());
            }
        }
    }

    private static String getSQLType(Class<?> type) {
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
