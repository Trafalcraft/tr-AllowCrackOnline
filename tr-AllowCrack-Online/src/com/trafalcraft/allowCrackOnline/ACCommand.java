package com.trafalcraft.allowCrackOnline;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.trafalcraft.allowCrackOnline.cache.PlayerCache;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;


//Sert récuperer les commandes de base
public class ACCommand extends Command {

	private final Main main;

	//Constructeur du Listener des commandes, recupere les commandes commencant par /ac
	public ACCommand(Main main) {
		super("AllowCrack", "AllowCrack.usage", new String[] { "ac" });

		this.main = main;

	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		//Affiche la page d'aide si il n'y a pas d'argument
		if (args.length <= 0) {
			displayHelp(sender);
		} else {
			switch (args[0].toLowerCase()) {
			//désactive le plugin
			case "disable":
				Main.setDisable(true);
				sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD+ "Le plugin est maintenant innactif!"));
				break;
			//active le plugin
			case "enable":
				Main.setDisable(false);
				sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD+ "Le plugin est de nouveau actif!"));
				break;
			//Ajoute quelqu'un à la whitelist des versions cracks
			case "add":
				final String name = args[1];
				
		        Main.getInstance().getProxy().getScheduler().runAsync(Main.getPlugin(), new Runnable() {
		            public void run() {
		                try {
		                    PreparedStatement st = Main.getDatabase().prepareStatement("INSERT INTO `" + Main.getConfig().getString("database.prefix") + "users` (`name`, `pass`, `lastIP`, `lastAuth`) VALUES(?, ?, ?, ?)");
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
		            }
		        });

				
				sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD+ args[1]+ " est maintenant autorisé a se connecter en version non-officiel"));
				break;
			//Supprime quelqu'un de la whitelist des versions cracks
			case "remove":
				final String name2 = args[1];
				
		        Main.getInstance().getProxy().getScheduler().runAsync(Main.getPlugin(), new Runnable() {
		            @Override
		            public void run() {
		                try {
		                    PreparedStatement st = Main.getDatabase().prepareStatement("DELETE FROM `" + Main.getConfig().getString("database.prefix") + "users` WHERE `name` = \'"+name2+"\'");
		                    Main.getManageCache().removePlayerCache(name2);
		                    st.executeUpdate();
		                    st.close();
		                } catch (SQLException e) {
		                    e.printStackTrace();
		                }
		            }
		        });
		        
				sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD+ args[1]+ " n'est plus autorisé a se connecter en version non-officiel"));
				break;
			//Affiche la whitelist des joueurs accepter en crack
			case "list":
				String msg = "Les joueurs autorisez sont: ";
				for(PlayerCache pc : Main.getManageCache().playergetAllCacheList()){
					msg+=pc.getName()+", ";
				}
				msg = msg.substring(1, msg.length()-2); 
				sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD+ msg));
				break;
			//recharge la config, (update a faire raffraichie la bdd)
			case "reload":
				main.load();
				sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD+ "Config rechargé!"));

				break;
			//Si aucune de ces commandes n'est bonne retourne l'aide
			default:
				displayHelp(sender);
			}

		}
	}

	private void displayHelp(CommandSender sender) {
		sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + ""+ ChatColor.STRIKETHROUGH + "----------" + ChatColor.GOLD + "["+ ChatColor.DARK_GREEN + "AllowCrack " + ChatColor.GRAY+ this.main.getDescription().getVersion() + "" + ChatColor.GOLD+ "]" + ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH+ "----------"));

		sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD+ "/ac toggle - " + ChatColor.DARK_GREEN + "//"));
		sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD+ "/ac add <pseudo> - " + ChatColor.DARK_GREEN + "Ajouter un joueur crack"));
		sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD+ "/ac remove <pseudo> - " + ChatColor.DARK_GREEN+ "Supprimer un joueur crack"));
		sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD+ "/ac list - " + ChatColor.DARK_GREEN+ "Lister les joueurs crack"));
		sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD+ "/ac enable - " + ChatColor.DARK_GREEN + "Activer le plugin"));
		sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD+ "/ac disable - " + ChatColor.DARK_GREEN+ "Desactiver le plugin"));
		sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD+ "/ac reload - " + ChatColor.DARK_GREEN+ "Recharger la configuration"));

		sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + ""+ ChatColor.STRIKETHROUGH + "------------------------------"));
	}

}
