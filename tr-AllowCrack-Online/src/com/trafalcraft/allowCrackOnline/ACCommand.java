package com.trafalcraft.allowCrackOnline;

import com.trafalcraft.allowCrackOnline.cache.PlayerCache;
import com.trafalcraft.allowCrackOnline.util.Msg;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ACCommand extends Command {

        private final Main main;

        public ACCommand(Main main) {
                super("AllowCrack", "AllowCrack.usage", "ac");

                this.main = main;

        }

        @Override
        public void execute(CommandSender sender, String[] args) {
                if (args.length <= 0) {
                        Msg.sendHelp(sender);
                } else {
                        switch (args[0].toLowerCase()) {
                                case "disable":
                                        Main.setDisable(true);
                                        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD
                                                + Msg.PREFIX.toString() + Msg.PLUGIN_DISABLE));
                                        break;
                                case "enable":
                                        Main.setDisable(false);
                                        sender.sendMessage(
                                                TextComponent.fromLegacyText(ChatColor.GOLD + Msg.PREFIX.toString()
                                                        + Msg.PLUGIN_ENABLE));
                                        break;
                                case "add":
                                        final String name = args[1];

                                        Main.getInstance().getProxy().getScheduler().runAsync(Main.getPlugin(), () -> {
                                                try {
                                                        PreparedStatement st = Main.getDatabase()
                                                                .prepareStatement("INSERT INTO `"
                                                                        + Main.getConfig().getString("database.prefix")
                                                                        + "users` (`name`, `pass`, `lastIP`, `lastAuth`) VALUES(?, ?, ?, ?)");
                                                        st.setString(1, name);
                                                        st.setString(2, null);
                                                        st.setString(3, null);
                                                        st.setString(4, null);
                                                        Main.getManageCache().addPlayerCache(name, null, null, null);
                                                        st.executeUpdate();
                                                        st.close();
                                                } catch (SQLException e) {
                                                        e.printStackTrace();
                                                }
                                        });

                                        sender.sendMessage(
                                                TextComponent.fromLegacyText(ChatColor.GOLD + Msg.PREFIX.toString()
                                                        + Msg.PLAYER_ADDED_TO_ALLOWED_CRACKED_LIST.toString()
                                                        .replace("$player", name)));
                                        break;
                                case "remove":
                                        final String name2 = args[1];

                                        Main.getInstance().getProxy().getScheduler().runAsync(Main.getPlugin(), () -> {
                                                try {
                                                        PreparedStatement st = Main.getDatabase()
                                                                .prepareStatement("DELETE FROM `"
                                                                        + Main.getConfig().getString("database.prefix")
                                                                        + "users` WHERE `name` = \'" + name2 + "\'");
                                                        Main.getManageCache().removePlayerCache(name2);
                                                        st.executeUpdate();
                                                        st.close();
                                                } catch (SQLException e) {
                                                        e.printStackTrace();
                                                }
                                        });

                                        sender.sendMessage(
                                                TextComponent.fromLegacyText(ChatColor.GOLD + Msg.PREFIX.toString()
                                                        + Msg.PLAYER_REMOVED_FROM_ALLOWED_CRACKED_LIST.toString()
                                                        .replace("$player", name2)));
                                        break;
                                case "list":
                                        StringBuilder msg = new StringBuilder(
                                                Msg.PREFIX.toString() + Msg.LIST_ALLOWED_PLAYERS);
                                        for (PlayerCache pc : Main.getManageCache().getAllPlayerCacheList()) {
                                                msg.append(pc.getName()).append(", ");
                                        }
                                        msg = new StringBuilder(msg.substring(1, msg.length() - 2));
                                        sender.sendMessage(
                                                TextComponent.fromLegacyText(ChatColor.GOLD + msg.toString()));
                                        break;
                                case "reload":
                                        main.loadConfig();
                                        sender.sendMessage(
                                                TextComponent.fromLegacyText(ChatColor.GOLD + "Config reloaded!"));

                                        break;
                                default:
                                        Msg.sendHelp(sender);
                        }

                }
        }
}
