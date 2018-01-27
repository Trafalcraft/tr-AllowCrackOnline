package com.trafalcraft.allowCrackOnline.auth;

import com.trafalcraft.allowCrackOnline.Main;
import com.trafalcraft.allowCrackOnline.cache.PlayerCache;
import com.trafalcraft.allowCrackOnline.util.Msg;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Register extends Command {

        public Register(Main main) {
                super("AllowCrack", null, "register");
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
                if (Main.getManageCache().contains(sender.getName())) {
                        PlayerCache playerCache = Main.getManageCache().getPlayerCache(sender.getName());
                        if (playerCache.getPass() != null) {
                                sender.sendMessage(TextComponent.fromLegacyText(Msg.ERROR.toString()
                                        + Msg.PLAYER_ALREADY_REGISTER + "\n" + Msg.LOGIN_HELP));
                        } else if (args.length <= 1) {
                                sender.sendMessage(
                                        TextComponent.fromLegacyText(Msg.ERROR.toString() + Msg.REGISTER_HELP));
                        } else if (!(args[0].equals(args[1]))) {
                                sender.sendMessage(TextComponent.fromLegacyText(Msg.ERROR.toString()
                                        + Msg.REGISTER_PASSWORD_NOT_MATCH + "\n" + Msg.REGISTER_HELP));
                        } else {
                                try {
                                        byte[] passBytes = args[0].getBytes();
                                        MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
                                        algorithm.reset();
                                        algorithm.update(passBytes);
                                        MessageDigest md = MessageDigest.getInstance("SHA-256");
                                        byte[] messageDigest = md.digest(passBytes);
                                        BigInteger number = new BigInteger(1, messageDigest);
                                        String code = number.toString(16);
                                        playerCache.setPass(code);
                                        sender.sendMessage(TextComponent
                                                .fromLegacyText(Msg.PREFIX.toString() + Msg.REGISTER_SUCCESS));
                                        if (!playerCache.isLogged()) {
                                                sender.sendMessage(TextComponent
                                                        .fromLegacyText(Msg.LOGIN_HELP.toString()));
                                        }
                                } catch (NoSuchAlgorithmException e) {
                                        throw new Error("invalid JRE: have not 'MD5' impl.", e);
                                }
                        }
                }
        }
}
