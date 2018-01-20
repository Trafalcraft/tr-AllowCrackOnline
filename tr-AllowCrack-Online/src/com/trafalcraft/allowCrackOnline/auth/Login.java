package com.trafalcraft.allowCrackOnline.auth;

import com.trafalcraft.allowCrackOnline.Main;
import com.trafalcraft.allowCrackOnline.util.Msg;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Login extends Command {

        public Login(Main main) {
                super("AllowCrack", "", "login");
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
                if (Main.getManageCache().contains(sender.getName())) {
                        if (Main.getManageCache().getPlayerCache(sender.getName()).getPass() == null) {
                                sender.sendMessage(
                                        TextComponent.fromLegacyText(Msg.ERROR.toString() + Msg.PLAYER_NOT_REGISTER));
                        } else if (args.length <= 0) {
                                sender.sendMessage(TextComponent.fromLegacyText(Msg.ERROR.toString() + Msg.LOGIN_HELP));
                        } else {
                                try {
                                        ProxiedPlayer player = (ProxiedPlayer) sender;
                                        byte[] passBytes = args[0].getBytes();
                                        MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
                                        algorithm.reset();
                                        algorithm.update(passBytes);
                                        MessageDigest md = MessageDigest.getInstance("SHA-256");
                                        byte[] messageDigest = md.digest(passBytes);
                                        BigInteger number = new BigInteger(1, messageDigest);
                                        String code = number.toString(16);
                                        System.out.println(
                                                code + ">" + Main.getManageCache().getPlayerCache(sender.getName())
                                                        .getPass());
                                        if (code.equals(Main.getManageCache().getPlayerCache(sender.getName())
                                                .getPass())) {
                                                sender.sendMessage(TextComponent.fromLegacyText(
                                                        Msg.PREFIX.toString() + Msg.LOGIN_RIGHT_PASSWORD));
                                                ServerInfo target = ProxyServer.getInstance()
                                                        .getServerInfo(
                                                                Main.getConfig().getString("Settings.mainServer"));
                                                player.connect(target);
                                        } else {
                                                sender.sendMessage(TextComponent
                                                        .fromLegacyText(Msg.ERROR.toString() + Msg.WRONG_PASSWORD));
                                                player.disconnect(
                                                        TextComponent.fromLegacyText(Msg.WRONG_PASSWORD.toString()));
                                        }
                                } catch (NoSuchAlgorithmException e) {
                                        throw new Error("invalid JRE: have not 'MD5' impl.", e);
                                }
                        }
                }
        }
}
