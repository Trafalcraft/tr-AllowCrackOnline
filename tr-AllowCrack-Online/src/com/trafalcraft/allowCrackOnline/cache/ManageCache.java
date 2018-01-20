package com.trafalcraft.allowCrackOnline.cache;

import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;

public class ManageCache {

        private final Map<String, PlayerCache> crackedPlayerCache = Maps.newHashMap();

        public void addPlayerCache(String name, String pass, String lastIP, String lastAuth) {
                if (!this.crackedPlayerCache.containsKey(name)) {
                        PlayerCache pa = new PlayerCache(name, pass, lastIP, lastAuth);
                        crackedPlayerCache.put(name, pa);
                }
        }

        public boolean contains(String p) {
                return this.crackedPlayerCache.containsKey(p);
        }

        public void removePlayerCache(String p) {
                if (this.crackedPlayerCache.containsKey(p)) {
                        crackedPlayerCache.remove(p);
                }
        }

        public Map<String, PlayerCache> playerCacheList() {
                return crackedPlayerCache;
        }

        public Collection<PlayerCache> getAllPlayerCacheList() {
                return crackedPlayerCache.values();
        }

        public PlayerCache getPlayerCache(String p) {
                return crackedPlayerCache.get(p);
        }

}
