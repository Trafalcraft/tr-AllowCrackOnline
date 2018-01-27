package com.trafalcraft.allowCrackOnline.util;

import com.trafalcraft.allowCrackOnline.Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
        private List<Connection> connections;
        private Main plugin;
        private String dsn;
        private String user;
        private String pass;

        public DatabaseManager(Main plugin, String dsn, String user, String pass) {
                this.plugin = plugin;
                this.dsn = dsn;
                this.user = user;
                this.pass = pass;
        }

        public synchronized Connection getConnection() {
                connections = new ArrayList<>();
                for (int i = 0; i < connections.size(); i++) {
                        Connection c = connections.get(i);
                        try {
                                if (c.isValid(2) && !c.isClosed()) {
                                        return c;
                                } else {
                                        connections.remove(c);
                                }
                        } catch (SQLException e) {
                                e.printStackTrace();
                                connections.remove(c);
                        }
                }

                Connection c = runConnection();
                if (c != null)
                        connections.add(c);
                return c;
        }

        private Connection runConnection() {
                try {
                        return DriverManager.getConnection(dsn + "&useSSL=false", user, pass);
                } catch (SQLException e) {
                        plugin.getLogger()
                                .severe("Unable to connect to MySQL database.");
                        plugin.getLogger().severe(e.getMessage());
                        return null;
                }
        }

}
