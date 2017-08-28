package com.trafalcraft.allowCrackOnline.auth;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.trafalcraft.allowCrackOnline.Main;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class Register extends Command {
	@SuppressWarnings("unused")
	private final Main main;
	private String code;

	public Register(Main main) {
		super("AllowCrack", null, new String[] { "register" });

		this.main = main;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
			if(Main.getManageCache().contains(sender.getName())){
				if(Main.getManageCache().getPlayerCache(sender.getName()).getPass() != null){
					sender.sendMessage(TextComponent.fromLegacyText("§4Erreur §c>> Vous êtes déja inscrit, faites /login <mot de passe>"));
				} else if (args.length <= 1) {
					sender.sendMessage(TextComponent.fromLegacyText("§4Erreur §c>> Faites /register <mot de passe> <mot de passe>"));
				} else if (!(args[0].equals(args[1]))) {
					sender.sendMessage(TextComponent.fromLegacyText("§4Erreur §c>> Les mots de passe ne correspondent pas \n Faites /register <mot de passe> <mot de passe>"));
				} else {
					try {
						byte[] passBytes = args[0].getBytes();
						MessageDigest algorithm = MessageDigest.getInstance("MD5");
						algorithm.reset();
						algorithm.update(passBytes);
						MessageDigest md = MessageDigest.getInstance("MD5");
						byte[] messageDigest = md.digest(passBytes);
						BigInteger number = new BigInteger(1, messageDigest);
						this.code = number.toString(16);
						Main.getManageCache().getPlayerCache(sender.getName()).setPass(this.code);
						//main.saveConfig();
						sender.sendMessage(TextComponent.fromLegacyText("§aVotre mot de passe a bien été enregistré \n Faites /login <mot de passe>"));
					} catch (NoSuchAlgorithmException e) {
						throw new Error("invalid JRE: have not 'MD5' impl.", e);
					}
				}
			}
	}
}
