package com.trafalcraft.allowCrackOnline.util;

import com.trafalcraft.allowCrackOnline.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public enum Msg {

        PREFIX("§9[§3tr-AllowCrackOnline§9]> §2"),
        ERROR("§9[§4tr-AllowCrackOnline§9]> §c"),
        NO_PERMISSIONS("§4ERROR §9§l> §r§bYou don't have permission to do that!"),
        COMMAND_USE("§9[§4tr-AllowCrackOnline§9]> §r§cCommand use: §6$command"),

        NOT_ALLOWED_CRACKED_USER("Invalid username. Hacking?"),
        PLUGIN_DISABLE_KICK_MSG("You have been disconnected \n Proxy restarting"),

        WRONG_PASSWORD("Wrong password"),
        PLAYER_NOT_REGISTER("You are not register"),
        //TODO add entry in config file
        PLAYER_ALREADY_REGISTER("You are already register"),
        REGISTER_HELP("Do /register <password> <password>"),
        REGISTER_PASSWORD_NOT_MATCH("The password not match"),
        REGISTER_SUCCESS("§aYou have been register"),
        CHANGE_PASSWORD_HELP("Do /changemdp <old password>"),
        CHANGE_PASSWORD_RIGHT_PASSWORD("Your password is good"),
        LOGIN_HELP("Do /login <password>"),
        LOGIN_RIGHT_PASSWORD("§aThis is the good password \n Good game"),

        PLUGIN_DISABLE("The plugin is now disable !"),
        PLUGIN_ENABLE("The plugin is now enable !"),
        PLAYER_ADDED_TO_ALLOWED_CRACKED_LIST("$player is now allowed to used a no-official version"),
        PLAYER_REMOVED_FROM_ALLOWED_CRACKED_LIST("$player is no longer allowed to used a no-official version"),
        LIST_ALLOWED_PLAYERS("The allowed player are : "),

        TOO_LONG_PASSWORD("$player have a too long name"),
        INVALID_CHARACTER("$player contain an invalid character"),
        SUCCESSFUL_CONNECTION("The player $player is connected in cracked version");

        private String value;

        Msg(String value) {
                this.value = value;
        }

        public static void sendHelp(CommandSender sender) {
                sender.sendMessage(TextComponent.fromLegacyText(""));
                sender.sendMessage(TextComponent.fromLegacyText(
                        ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "----------" + ChatColor.GOLD + "["
                                + ChatColor.DARK_GREEN + "AllowCrack " + ChatColor.GRAY
                                + Main.getInstance().getDescription().getVersion() + "" + ChatColor.GOLD + "]"
                                + ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "----------"));
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD
                        + "/ac enable - " + ChatColor.DARK_GREEN + "enable the plugin"));
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD
                        + "/ac disable - " + ChatColor.DARK_GREEN + "disable the plugin"));
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD
                        + "/ac add <pseudo> - " + ChatColor.DARK_GREEN + "Add a cracked player to the allowed list"));
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD
                        + "/ac remove <pseudo> - " + ChatColor.DARK_GREEN
                        + "Remove a cracked player from the allowed list"));
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD
                        + "/ac list - " + ChatColor.DARK_GREEN + "List the cracked players"));
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD
                        + "/ac reload - " + ChatColor.DARK_GREEN + "reload the configuration file"));

                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + ""
                        + ChatColor.STRIKETHROUGH + "------------------------------"));
        }

        public static void load() {
                Msg.PREFIX.replaceBy("msg.base.prefix");
                Msg.ERROR.replaceBy("msg.base.error");
                Msg.NO_PERMISSIONS.replaceBy("msg.base.no_permissions");
                Msg.COMMAND_USE.replaceBy("msg.base.command_use");

                Msg.NOT_ALLOWED_CRACKED_USER.replaceBy("msg.info.notAllowedCrackedUser");
                Msg.PLUGIN_DISABLE_KICK_MSG.replaceBy("msg.info.pluginDisableKickMsg");

                Msg.WRONG_PASSWORD.replaceBy("msg.userCommands.wrongPassword");
                Msg.PLAYER_NOT_REGISTER.replaceBy("msg.userCommands.playerNotRegister");
                Msg.REGISTER_HELP.replaceBy("msg.userCommands.registerHelp");
                Msg.REGISTER_PASSWORD_NOT_MATCH.replaceBy("msg.userCommands.registerPasswordNotMatch");
                Msg.REGISTER_SUCCESS.replaceBy("msg.userCommands.registerSuccess");
                Msg.CHANGE_PASSWORD_HELP.replaceBy("msg.userCommands.changePasswordHelp");
                Msg.CHANGE_PASSWORD_RIGHT_PASSWORD.replaceBy("msg.userCommands.changePasswordRightPassword");
                Msg.LOGIN_HELP.replaceBy("msg.userCommands.loginHelp");
                Msg.LOGIN_RIGHT_PASSWORD.replaceBy("msg.userCommands.loginRightPassword");

                Msg.PLUGIN_DISABLE.replaceBy("msg.adminCommands.pluginDisable");
                Msg.PLUGIN_ENABLE.replaceBy("msg.adminCommands.pluginEnable");
                Msg.PLAYER_ADDED_TO_ALLOWED_CRACKED_LIST.replaceBy("msg.adminCommands.playerAddedToAllowedCrackedList");
                Msg.PLAYER_REMOVED_FROM_ALLOWED_CRACKED_LIST
                        .replaceBy("msg.adminCommands.playerRemovedFromAllowedCrackedList");
                Msg.LIST_ALLOWED_PLAYERS.replaceBy("msg.adminCommands.listAllowedPlayers");

                Msg.TOO_LONG_PASSWORD.replaceBy("msg.invalidPlayer.tooLongPassword");
                Msg.INVALID_CHARACTER.replaceBy("msg.invalidPlayer.invalidCharacter");
                Msg.SUCCESSFUL_CONNECTION.replaceBy("msg.invalidPlayer.successfulConnection");
        }

        public String toString() {
                return value;
        }

        private void replaceBy(String value) {
                this.value = Main.getConfig().getString(value);
        }

}
