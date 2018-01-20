package com.trafalcraft.allowCrackOnline;

import com.trafalcraft.allowCrackOnline.util.Msg;
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
        private final Pattern pat = Pattern.compile("^[a-zA-Z0-9_]{2,16}$");
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
                        InitialHandler handler = (InitialHandler) e.getConnection();

                        this.main.getLogger().info("\u001B[31m" + Msg.SUCCESSFUL_CONNECTION.toString()
                                .replace("$player", e.getConnection().getName()) + "\u001B[0m");

                        handler.setOnlineMode(false);
                }

        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onLogin(ServerConnectEvent e) {
                ProxiedPlayer player = e.getPlayer();

                if (Main.getManageCache().contains(player.getName())) {
                        //TODO add error if bad authServer
                        if (player.getServer() == null) {
                                ServerInfo target2 = ProxyServer.getInstance()
                                        .getServerInfo(Main.getConfig().getString("Settings.authServer"));
                                Main.getInstance().getLogger().info(Main.getConfig().getString("Settings.authServer"));
                                e.setTarget(target2);
                        }
                }
                if (Main.getManageCache().getPlayerCache(player.getName()).getPass() != null) {
                        player.sendMessage(TextComponent.fromLegacyText(Msg.PREFIX.toString() + Msg.LOGIN_HELP));
                } else {
                        player.sendMessage(TextComponent.fromLegacyText(Msg.PREFIX.toString() + Msg.REGISTER_HELP));
                }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onPlayerLeave(PlayerDisconnectEvent pde) {
                final ProxiedPlayer player = pde.getPlayer();
                ServerInfo target = ProxyServer.getInstance()
                        .getServerInfo(Main.getConfig().getString("Settings.mainServer"));
                player.setReconnectServer(target);
                Main.getManageCache().getPlayerCache(player.getName())
                        .setLastIP(player.getAddress().getAddress().toString());
                if (Main.getManageCache().contains(player.getName())) {
                        target = ProxyServer.getInstance().getServerInfo("Settings.authServer");
                        player.setReconnectServer(target);
                        Main.getInstance().getProxy().getScheduler().runAsync(Main.getPlugin(), () -> {
                                try {
                                        PreparedStatement st = Main.getDatabase().prepareStatement("");
                                        if (Main.getManageCache().getPlayerCache(player.getName()).getPass() != null) {
                                                st = Main.getDatabase().prepareStatement("UPDATE `"
                                                        + Main.getConfig().get("database.prefix")
                                                        + "users` SET `pass` = '"
                                                        + Main.getManageCache().getPlayerCache(player.getName())
                                                        .getPass()
                                                        + "' WHERE `name` = '" + player.getName() + "';");
                                                st.executeUpdate();
                                        }
                                        if (Main.getManageCache().getPlayerCache(player.getName()).getLastIP()
                                                != null) {
                                                st = Main.getDatabase().prepareStatement("UPDATE `"
                                                        + Main.getConfig().get("database.prefix")
                                                        + "users` SET `lastIP` = '"
                                                        + player.getAddress().getAddress().getHostName()
                                                        + "' WHERE `name` = '" + player.getName() + "' ;");
                                                st.executeUpdate();
                                        }
                                        if (Main.getManageCache().getPlayerCache(player.getName()).getLastAuth()
                                                != null) {
                                                st = Main.getDatabase().prepareStatement("UPDATE `"
                                                        + Main.getConfig().get("database.prefix")
                                                        + "users` SET `lastAUth` = '"
                                                        + Main.getManageCache().getPlayerCache(player.getName())
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
                return (username != null) && (this.pat.matcher(username).matches());
        }

        //TODO add check if player is logged
        @EventHandler(priority = EventPriority.HIGHEST)
        public void onChatEvent(ChatEvent e) {
                if (e.getSender() instanceof ProxiedPlayer) {
                        ProxiedPlayer player = (ProxiedPlayer) e.getSender();
                        if (Main.getManageCache().getPlayerCache(player.getName()).getPass() == null) {
                                if (!e.getMessage().startsWith("/register")) {
                                        player.sendMessage(TextComponent
                                                .fromLegacyText(Msg.PREFIX.toString() + Msg.REGISTER_HELP));
                                        e.setCancelled(true);
                                }
                        }
                }
        }
}
