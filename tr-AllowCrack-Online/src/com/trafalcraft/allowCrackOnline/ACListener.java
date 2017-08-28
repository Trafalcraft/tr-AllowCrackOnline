package com.trafalcraft.allowCrackOnline;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Pattern;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.connection.InitialHandler;

public class ACListener  implements Listener{
	  private final Pattern pat = Pattern.compile("^[a-zA-Z0-9_]{2,16}$");
	  private final Main main;
	  private final String kick_invalid_name;
	  
	  //Constructeur pour initialiser le Listener
	  public ACListener(Main main, String invalid)
	  {
		  this.main = main;
		    this.kick_invalid_name = ChatColor.translateAlternateColorCodes('&', invalid);
	  }
	  
	  //Verifie si le joueur respecte bien les règles correspondant au pseudo minecraft
	  @EventHandler(priority=64)
	  public void onPreLogin(PreLoginEvent e){
		    if (e.isCancelled()) {
		        return;
		      }
		  
		      if (e.getConnection().getName().length() > 16)
		      {
		    	  main.getLogger().info(e.getConnection().getName()+" a un pseudo trop long");
		          e.setCancelReason(this.kick_invalid_name);
		          
		          e.setCancelled(true);
		          
		          return;
		      }
		      if (!validate(e.getConnection().getName()))
		      {
		    	  main.getLogger().info(e.getConnection().getName()+" contient un caractère invalide");
		        e.setCancelReason(this.kick_invalid_name);
		        
		        e.setCancelled(true);
		        
		        return;
		      }
		      if(Main.getManageCache().contains(e.getConnection().getName())){
			      InitialHandler handler = (InitialHandler)e.getConnection();
			      
			        this.main.getLogger().info("\u001B[31m"+"Le joueur " + e.getConnection().getName() +" a pu se connecter en version crack!"+ "\u001B[0m");
			        
			        handler.setOnlineMode(false);
		      }

	  }
	  
	  
	  //connecte le joueur au serveur d'autentification si le joueur est autorisé a se connecte en version crack
		@EventHandler(priority = EventPriority.HIGHEST)
		public void onLogin(ServerConnectEvent e){
			ProxiedPlayer player = e.getPlayer();
			
			if(Main.getManageCache().contains(player.getName())){
				if(player.getServer() == null){
					ServerInfo target2 = ProxyServer.getInstance().getServerInfo("log");
					e.setTarget(target2);
				}
			}
			
		}
		
		//sauvegarde les données du joueur dans la base de donnée
		@EventHandler(priority = EventPriority.HIGHEST)
		public void onPlayerLeave(PlayerDisconnectEvent pde){
			final ProxiedPlayer player = pde.getPlayer();
			ServerInfo target = ProxyServer.getInstance().getServerInfo("jeux");
			player.setReconnectServer(target);
			Main.getManageCache().getPlayerCache(player.getName()).setLastIP(player.getAddress().getAddress().toString());
			if(Main.getManageCache().contains(player.getName())){
		        Main.getInstance().getProxy().getScheduler().runAsync(Main.getPlugin(), new Runnable() {
		            @Override
		            public void run() {
		                try {
		                	PreparedStatement st = Main.getDatabase().prepareStatement("");
		                	if(Main.getManageCache().getPlayerCache(player.getName()).getPass() != null){
		                		st = Main.getDatabase().prepareStatement("UPDATE `"+Main.getConfig().get("database.prefix")+"users` SET `pass` = '"+Main.getManageCache().getPlayerCache(player.getName()).getPass()+"' WHERE `name` = 'Amosar';");
		                    	st.executeUpdate();
		                	}
		                    if(Main.getManageCache().getPlayerCache(player.getName()).getLastIP() != null){
		                    	st  = Main.getDatabase().prepareStatement("UPDATE `"+Main.getConfig().get("database.prefix")+"users` SET `lastIP` = '"+player.getAddress().getAddress().getHostName()+"' WHERE `name` = 'Amosar' ;");
		                    	st.executeUpdate();
		                    }
		                    if(Main.getManageCache().getPlayerCache(player.getName()).getLastAuth() != null){
		                    	st = Main.getDatabase().prepareStatement("UPDATE `"+Main.getConfig().get("database.prefix")+"users` SET `lastAUth` = '"+Main.getManageCache().getPlayerCache(player.getName()).getLastAuth()+"' WHERE `name` = 'Amosar' ;");
		                    	st.executeUpdate();
		                    }
		                    st.close();
		                } catch (SQLException e) {
		                    e.printStackTrace();
		                }
		            }
		        });
					target = ProxyServer.getInstance().getServerInfo("log");
					player.setReconnectServer(target);
				}
		}
		
		
	  public boolean validate(String username)
	  {
	    return (username != null) && (this.pat.matcher(username).matches());
	  }
}
