package com.trafalcraft.allowCrackOnline;

import com.trafalcraft.allowCrackOnline.cache.PlayerCache;
import com.trafalcraft.allowCrackOnline.util.Msg;
import com.trafalcraft.allowCrackOnline.util.PingServers;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class ACListener implements Listener {
        private final Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]{2,16}$");
        private final Main main;
        private final String kick_invalid_name;

        public ACListener(Main main, String invalid) {
                this.main = main;
                this.kick_invalid_name = ChatColor.translateAlternateColorCodes('&', invalid);
        }

        @EventHandler(priority = 64)
        public void onPreLogin(PreLoginEvent e) {
                if (e.isCancelled()) {
                        return;
                }

                if (e.getConnection().getName().length() > 16) {
                        main.getLogger().info(Msg.TOO_LONG_PASSWORD.toString()
                                .replace("$player", e.getConnection().getName()));
                        e.setCancelReason(TextComponent.fromLegacyText(this.kick_invalid_name));

                        e.setCancelled(true);

                        return;
                }
                if (!validate(e.getConnection().getName())) {
                        main.getLogger().info(Msg.INVALID_CHARACTER.toString()
                                .replace("$player", e.getConnection().getName()));
                        e.setCancelReason(TextComponent.fromLegacyText(this.kick_invalid_name));

                        e.setCancelled(true);

                        return;
                }

                if (Main.getManageCache().contains(e.getConnection().getName())) {
                        ServerInfo target = ProxyServer.getInstance()
                                .getServerInfo(Main.getConfig().getString("Settings.authServer"));

                        InitialHandler handler = (InitialHandler) e.getConnection();

                        if (target == null) {
                                e.setCancelled(true);
                                e.setCancelReason(
                                        TextComponent.fromLegacyText(Msg.AUTH_SERVER_DOWN.toString()));
                                return;

                        }

                        this.main.getLogger().info("\u001B[31m" + Msg.SUCCESSFUL_CONNECTION.toString()
                                .replace("$player", e.getConnection().getName()) + "\u001B[0m");

                        handler.setOnlineMode(false);
                }


        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onLogin(ServerConnectEvent e) {
                if (e.getPlayer() != null) {
                        ProxiedPlayer player = e.getPlayer();

                        if (Main.getManageCache().contains(player.getName())) {
                                if (player.getServer() == null) {
                                        ServerInfo target = ProxyServer.getInstance()
                                                .getServerInfo(Main.getConfig().getString("Settings.authServer"));
                                        e.setTarget(target);
                                        PingServers server_down = new PingServers();
                                        try {
                                                synchronized (server_down) {
                                                        target.ping(server_down);
                                                        server_down.wait();
                                                        boolean serverIsOnline = server_down.serverIsOnline();
                                                        if (!serverIsOnline) {
                                                                player.disconnect(TextComponent
                                                                        .fromLegacyText(
                                                                                Msg.AUTH_SERVER_DOWN.toString()));
                                                        }
                                                }
                                        } catch (InterruptedException e1) {
                                                e1.printStackTrace();
                                        }
                                }
                                if (Main.getManageCache().getPlayerCache(player.getName()).getPass() != null) {
                                        player.sendMessage(
                                                TextComponent.fromLegacyText(Msg.PREFIX.toString() + Msg.LOGIN_HELP));
                                } else {
                                        player.sendMessage(
                                                TextComponent
                                                        .fromLegacyText(Msg.PREFIX.toString() + Msg.REGISTER_HELP));
                                }
                        }
                }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onPlayerLeave(PlayerDisconnectEvent pde) {
                final ProxiedPlayer player = pde.getPlayer();
                if (Main.getManageCache().contains(player.getName())) {
                        PlayerCache playerCache = Main.getManageCache().getPlayerCache(player.getName());
                        playerCache.setLastIP(player.getAddress().getAddress().toString());
                        ServerInfo target = ProxyServer.getInstance().getServerInfo("Settings.authServer");
                        player.setReconnectServer(target);
                        Main.getInstance().getProxy().getScheduler().runAsync(Main.getPlugin(), () -> {
                                try {
                                        PreparedStatement st = Main.getDatabase().prepareStatement("");
                                        if (playerCache.getPass() != null) {
                                                st = Main.getDatabase().prepareStatement("UPDATE `"
                                                        + Main.getConfig().get("database.prefix")
                                                        + "users` SET `pass` = '"
                                                        + playerCache
                                                        .getPass()
                                                        + "' WHERE `name` = '" + player.getName() + "';");
                                                st.executeUpdate();
                                        }
                                        if (playerCache.getLastIP()
                                                != null) {
                                                st = Main.getDatabase().prepareStatement("UPDATE `"
                                                        + Main.getConfig().get("database.prefix")
                                                        + "users` SET `lastIP` = '"
                                                        + player.getAddress().getAddress().getHostName()
                                                        + "' WHERE `name` = '" + player.getName() + "' ;");
                                                st.executeUpdate();
                                        }
                                        if (playerCache.getLastAuth()
                                                != null) {
                                                st = Main.getDatabase().prepareStatement("UPDATE `"
                                                        + Main.getConfig().get("database.prefix")
                                                        + "users` SET `lastAUth` = '"
                                                        + playerCache
                                                        .getLastAuth()
                                                        + "' WHERE `name` = '" + player.getName() + "' ;");
                                                st.executeUpdate();
                                        }
                                        st.close();
                                } catch (SQLException e) {
                                        e.printStackTrace();
                                }
                        });
                }
        }

        public boolean validate(String username) {
                return (username != null) && (this.pattern.matcher(username).matches());
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onChatEvent(ChatEvent e) {
                if (e.getSender() instanceof ProxiedPlayer) {
                        ProxiedPlayer player = (ProxiedPlayer) e.getSender();
                        PlayerCache playerCache = Main.getManageCache().getPlayerCache(player.getName());
                        if (playerCache != null) {
                                if (playerCache.getPass() == null
                                        || !playerCache.isLogged()) {
                                        if ((!e.getMessage().startsWith("/register")) && (!e.getMessage()
                                                .startsWith("/login"))) {
                                                if (playerCache.getPass() == null) {
                                                        player.sendMessage(TextComponent
                                                                .fromLegacyText(
                                                                        Msg.PREFIX.toString() + Msg.REGISTER_HELP));
                                                } else {
                                                        player.sendMessage(TextComponent
                                                                .fromLegacyText(
                                                                        Msg.PREFIX.toString() + Msg.LOGIN_HELP));
                                                }
                                                e.setCancelled(true);
                                        }
                                }
                        }
                }
        }
}
