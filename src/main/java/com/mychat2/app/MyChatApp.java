package com.mychat2.app;

import com.mychat2.config.MyChatConfig;
import org.h2.tools.Server;

import java.sql.SQLException;

public class MyChatApp {
    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        MyChatConfig.start();
    }
}
