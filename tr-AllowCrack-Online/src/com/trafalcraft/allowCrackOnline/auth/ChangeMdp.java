package com.trafalcraft.allowCrackOnline.auth;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.trafalcraft.allowCrackOnline.Main;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class ChangeMdp extends Command{
	  @SuppressWarnings("unused")
	private final Main main;
	    private String code;
	  
	public ChangeMdp(Main main) {
	    super("AllowCrack", "", new String[] { "changemdp" });
	    
	    this.main = main;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(Main.getManageCache().contains(sender.getName())){
				if(Main.getManageCache().getPlayerCache(sender.getName()).getPass() == null){
		        	sender.sendMessage(TextComponent.fromLegacyText("§4Erreur §c>> Vous n'êtes pas inscrit \n Faites /register <mot de passe> <mot de passe>"));
				}else if (args.length <= 0)
			    {
		        	sender.sendMessage(TextComponent.fromLegacyText("§4Erreur §c>> Faites /changemdp <ancien mot de passe> \nPuis /register <mot de passe> <mot de passe>"));;
			    }else{
						try{
							byte[] passBytes = args[0].getBytes();
							MessageDigest algorithm = MessageDigest.getInstance("MD5");
							algorithm.reset();
							algorithm.update(passBytes);
							MessageDigest md = MessageDigest.getInstance("MD5");
							byte[] messageDigest = md.digest(passBytes);
							BigInteger number = new BigInteger(1, messageDigest);
							this.code= number.toString(16);
							if(this.code.equals(Main.getManageCache().getPlayerCache(sender.getName()).getPass())){
								Main.getManageCache().getPlayerCache(sender.getName()).setPass(null);
					        	sender.sendMessage(TextComponent.fromLegacyText("§aVotre mot de passe est bon \n Faite /register <mot de passe> <mot de passe>"));
				        	}else{
					        	sender.sendMessage(TextComponent.fromLegacyText("§4Votre mot de passe est faux"));
				        	}
						}catch (NoSuchAlgorithmException e){
				            throw new Error("invalid JRE: have not 'MD5' impl.", e);
						}
			    }
			
				
				
			}
		//}
	}
}
