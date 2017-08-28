package com.trafalcraft.allowCrackOnline.cache;

public class PlayerCache {
	
	String name;
	String pass;
	String lastIP;
	String lastAuth;
	
	public PlayerCache(String name, String pass, String lastIP, String lastAuth){
		this.name = name;
		this.pass = pass;
		this.lastIP = lastIP;
		this.lastAuth = lastAuth;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getLastIP() {
		return lastIP;
	}

	public void setLastIP(String lastIP) {
		this.lastIP = lastIP;
	}

	public String getLastAuth() {
		return lastAuth;
	}

	public void setLastAuth(String lastAuth) {
		this.lastAuth = lastAuth;
	}
	
	
	
}
