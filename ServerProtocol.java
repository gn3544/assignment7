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
	
	/**
	 * getSenderClientName() gets name of the client who sent the message
	 * @return String that denotes the name of the client
	 */
	public String getSenderClientName(){
		
		if ((message.charAt(0) == '*') || (message.charAt(0) == '?')){
			return message.substring(1).split("-")[0];
		}
		else{
			return getNormalSender();
		}
	}
	
	/**
	 * formatOutgoing() gets a formats message in case of an error
	 * @return String that denotes the formatted message
	 */
	public String formatOutgoing(){
		
		String filtered;
		
		if ((message.charAt(0) == '*') || (message.charAt(0) == '?')){
			filtered = String.join("", Arrays.copyOfRange(message.substring(1).split("-"), 0, 1));
		}
		else{
			filtered = String.join("", Arrays.copyOfRange(message.split("-"), 0, 1));
		}
		
		ArrayList<String> names = new ArrayList<String>(Arrays.asList(filtered.split(",")));
		for (int i = 0; i < names.size(); i++){
			names.set(i, names.get(i).trim());
		}
		
		for (int i = 0; i < names.size(); i++){
			if (!availability.containsKey(names.get(i))){
				return "#" + getNormalSender() + "-" + "incorrect response.";
			}
		}
		
		return message;
	}
	
	/**
	 * getNormalSender() returns the name of the client during a "normal" use case
	 * @return String that denotes the name of the client
	 */
	private String getNormalSender(){
		
		String filtered, sender;
		filtered = String.join("", Arrays.copyOfRange(message.split("-"), 0, 1));
		ArrayList<String> names = new ArrayList<String>(Arrays.asList(filtered.split(",")));
		for (int i = 0; i < names.size(); i++){
			names.set(i, names.get(i).trim());
		}
		
		sender = names.get(0);
		return sender;
	}
}
