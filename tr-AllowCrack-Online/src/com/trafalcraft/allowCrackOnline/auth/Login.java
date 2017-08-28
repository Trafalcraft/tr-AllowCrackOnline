package com.trafalcraft.allowCrackOnline.auth;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import com.trafalcraft.allowCrackOnline.Main;

public class Login extends Command{
	  @SuppressWarnings("unused")
	private final Main main;
	    private String code;
	
	
	public Login(Main main) {
	    super("AllowCrack", "", new String[] { "login" });
	    
	    this.main = main;
	    
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
	if(Main.getManageCache().contains(sender.getName())){
		if(Main.getManageCache().getPlayerCache(sender.getName()).getPass() == null){
		        	sender.sendMessage(TextComponent.fromLegacyText("§4Erreur §c>> Vous n'êtes pas inscrit \n Faites /register <mot de passe> <mot de passe>"));
				}else if (args.length <= 0)
			    {
		        	sender.sendMessage(TextComponent.fromLegacyText("§4Erreur §c>> Faites /login <mot de passe>"));;
			    }else{
						try{
							ProxiedPlayer player = (ProxiedPlayer) sender;
							byte[] passBytes = args[0].getBytes();
							MessageDigest algorithm = MessageDigest.getInstance("MD5");
							algorithm.reset();
							algorithm.update(passBytes);
							MessageDigest md = MessageDigest.getInstance("MD5");
							byte[] messageDigest = md.digest(passBytes);
							BigInteger number = new BigInteger(1, messageDigest);
							this.code= number.toString(16);
							System.out.println(this.code+">"+Main.getManageCache().getPlayerCache(sender.getName()).getPass());
							if(this.code.equals(Main.getManageCache().getPlayerCache(sender.getName()).getPass())){
					        	sender.sendMessage(TextComponent.fromLegacyText("§aVotre mot de passe est bon \n Bon jeu"));
								ServerInfo target = ProxyServer.getInstance().getServerInfo("jeux");
					        	player.connect(target);
				        	}else{
					        	sender.sendMessage(TextComponent.fromLegacyText("§4Votre mot de passe est faux"));
					        	player.disconnect(TextComponent.fromLegacyText("§4mot de passe est faux"));
				        	}
						}catch (NoSuchAlgorithmException e){
				            throw new Error("invalid JRE: have not 'MD5' impl.", e);
						}
			    }
			}
	}
}
