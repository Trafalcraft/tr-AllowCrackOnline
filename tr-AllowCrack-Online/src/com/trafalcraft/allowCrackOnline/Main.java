package com.trafalcraft.allowCrackOnline;

import com.trafalcraft.allowCrackOnline.auth.ChangeMdp;
import com.trafalcraft.allowCrackOnline.auth.Login;
import com.trafalcraft.allowCrackOnline.auth.Register;
import com.trafalcraft.allowCrackOnline.cache.ManageCache;
import com.trafalcraft.allowCrackOnline.util.DatabaseManager;
import com.trafalcraft.allowCrackOnline.util.Msg;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main extends Plugin {

        private static Main instance;
        private static Configuration config;
        private static Plugin plugin;
        private static DatabaseManager manager;
        private static ManageCache mc;
        private boolean disabled = false;

        public void onEnable() {
                instance = this;
                plugin = this;
                mc = new ManageCache();
                //initialise les Listener+command
                getProxy().getPluginManager().registerCommand(this, new ACCommand(this));
                getProxy().getPluginManager().registerCommand(this, new Register(this));
                getProxy().getPluginManager().registerCommand(this, new Login(this));
                getProxy().getPluginManager().registerCommand(this, new ChangeMdp(this));
                getProxy().getPluginManager().registerListener(this, new ACListener(this
                        , Msg.NOT_ALLOWED_CRACKED_USER.toString()));

                // loadConfig config
                if (!getDataFolder().exists())
                        getDataFolder().mkdir();

                File file = new File(getDataFolder(), "config.yml");

                if (!file.exists()) {
                        try (InputStream in = getResourceAsStream("config.yml")) {
                                Files.copy(in, file.toPath());
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                }
                loadConfig();
                Msg.load();
                getLogger().info("Config initialized");

                // Connect to the database
                manager = new DatabaseManager(this,
                        "jdbc:mysql://" + getConfig().get("database.host") + ":" + getConfig().getInt("database.port")
                                + "/" + getConfig().get("database.db") + "?useUnicode=true&characterEncoding=utf8",
                        getConfig().get("database.user").toString(), getConfig().get("database.pass").toString());
                Connection db = manager.getConnection();
                if (db == null) {
                        getLogger()
                                .severe("AllowCrackOnline is disabling. Please check your database settings in your config.yml");
                        this.disabled = true;
                        return;
                }

                // Initial database table setup
                try {
                        db.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS `"
                                + getConfig().getString("database.prefix")
                                + "users` (`name` VARCHAR(10) NOT NULL, `pass` varchar(64), `lastIP` varchar(15), `lastAuth` "
                                + "varchar(16), PRIMARY KEY (`name`), UNIQUE(`name`), INDEX(`name`)) CHARACTER SET utf8");

                        ResultSet rs = db.createStatement().executeQuery(
                                "SELECT COUNT(*) FROM `" + getConfig().getString("database.prefix") + "users`");
                        ResultSet rs2 = db.createStatement()
                                .executeQuery("SELECT * FROM `" + getConfig().getString("database.prefix") + "users`");

                        while (rs.next())
                                if (rs.getInt(1) > 0) {
                                        for (int i = 0; i < rs.getInt(1); i++) {
                                                rs2.next();
                                                mc.addPlayerCache(rs2.getString(1), rs2.getString(2), rs2.getString(3),
                                                        rs2.getString(4));
                                                System.out.println(rs2.getString(2) + rs2.getString(1));
                                        }
                                }
                } catch (SQLException e) {
                        getLogger().severe("Unable to connect to the database. Disabling...");
                        e.printStackTrace();
                        return;
                }
                getLogger().info("loaded");

        }

        public void onDisable() {
                for (ProxiedPlayer p : getProxy().getPlayers()) {
                        p.disconnect(TextComponent.fromLegacyText(Msg.PLUGIN_DISABLE_KICK_MSG.toString()));
                }
        }

        public boolean hisDisable() {
                return instance.disabled;
        }

        public void sendConsoleMsg(String string) {
                getLogger().info(string);

        }

        public void saveConfig() {
                try {
                        ConfigurationProvider.getProvider(YamlConfiguration.class).save(
                                getConfig(), new File(getDataFolder(), "config.yml"));
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }

        public void loadConfig() {
                try {
                        config = ConfigurationProvider
                                .getProvider(YamlConfiguration.class).load(
                                        new File(getDataFolder(), "config.yml"));
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }

        public static void setDisable(boolean disable) {
                instance.disabled = disable;
        }

        public static Configuration getConfig() {
                return config;
        }

        public static Connection getDatabase() {
                return manager.getConnection();
        }

        public static Main getInstance() {
                return instance;
        }

        public static Plugin getPlugin() {
                return plugin;
        }

        public static ManageCache getManageCache() {
                return mc;
        }
}
