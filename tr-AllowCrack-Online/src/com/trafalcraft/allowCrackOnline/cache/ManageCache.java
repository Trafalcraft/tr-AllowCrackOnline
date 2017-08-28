package com.trafalcraft.allowCrackOnline.cache;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

public class ManageCache {

	private final Map<String, PlayerCache> achievementCache = Maps.newHashMap();
	
	public void addPlayerCache(String name, String pass, String lastIP, String lastAuth){
		if(!this.achievementCache.containsKey(name)){
			PlayerCache pa = new PlayerCache(name, pass, lastIP, lastAuth);
			achievementCache.put(name, pa);
		}
	}
	
	public boolean contains(String p){
		if(this.achievementCache.containsKey(p)){
			return true;
		}
		return false;
	}
	
	public void removePlayerCache(String p){
		if(this.achievementCache.containsKey(p)){
			achievementCache.remove(p);
		}
	}
	
	public Map<String, PlayerCache> playerCacheList(){
		return achievementCache;
	}
	
	public Collection<PlayerCache> playergetAllCacheList(){
		return achievementCache.values();
	}
	
	public PlayerCache getPlayerCache(String p){
			return achievementCache.get(p);
	}
	
}
