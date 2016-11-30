package assignment7;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class ServerProtocol {
	
	String message;
	private ConcurrentHashMap<String, Boolean> availability;
	
	public ServerProtocol(String message, ConcurrentHashMap<String, Boolean> availability){
		this.message = message;
		this.availability = availability;
	}
	
	public String getSenderClientName(){
		
		return null;
	}
	
	public String formatOutgoing(){
		
		return null;
	}
	
	private String getNormalSender(){
		
		return null;
	}
}
