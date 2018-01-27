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

public class ChangeMdp extends Command {

        public ChangeMdp(Main main) {
                super("AllowCrack", "", "changemdp", "changePassword");
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
                if (Main.getManageCache().contains(sender.getName())) {
                        PlayerCache playerCache = Main.getManageCache().getPlayerCache(sender.getName());
                        if (playerCache.getPass() == null) {
                                sender.sendMessage(
                                        TextComponent.fromLegacyText(Msg.ERROR.toString() + Msg.PLAYER_NOT_REGISTER));
                        } else if (args.length <= 0) {
                                sender.sendMessage(TextComponent.fromLegacyText(Msg.ERROR.toString()
                                        + Msg.CHANGE_PASSWORD_HELP + "\n" + Msg.REGISTER_HELP));
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
                                        if (code.equals(playerCache.getPass())) {
                                                playerCache.setPass(null);
                                                sender.sendMessage(TextComponent.fromLegacyText(Msg.PREFIX.toString()
                                                        + Msg.CHANGE_PASSWORD_RIGHT_PASSWORD + "\n"
                                                        + Msg.REGISTER_HELP));
                                        } else {
                                                sender.sendMessage(TextComponent.fromLegacyText(Msg.ERROR.toString()
                                                        + Msg.WRONG_PASSWORD));
                                        }
                                } catch (NoSuchAlgorithmException e) {
                                        throw new Error("invalid JRE: have not 'SHA-256' impl.", e);
                                }
                        }

                }
                //}
        }
}
