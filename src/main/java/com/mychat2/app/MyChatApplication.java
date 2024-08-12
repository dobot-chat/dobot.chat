package com.mychat2.app;

import com.mychat2.config.JavalinConfig;
import org.h2.tools.Server;

import java.sql.SQLException;

public class MyChatApplication {
    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        JavalinConfig.start();
    }
}
