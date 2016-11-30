package assignment7;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ClientProtocol {
	
	private String message;
	private String name;
	
	public ClientProtocol(String message, String name){
		this.message = message;
		this.name = name;
	}
	
	/**
	 * formatIncoming() formats the incoming message from the server
	 * @return String that denotes the formatted string from the sever
	 */
	public String formatIncoming(){
		
		if (message.length() == 0){
			return null;
		}
		if (message.charAt(0) == '*'){ //someone is leaving chat
			return normalProcedure(message.substring(1));
		}
		else if (message.charAt(0) != '?'){ //someone is not joining chat
			return normalProcedure(message);
		}
		return null;
	}
	
	/**
	 * normalProcedure() performs the normal procedure of converting incoming message to formatted message
	 * @param message
	 * @return String that denotes the formatted message
	 */
	private String normalProcedure(String message){
		String filtered, sender, chatString;
		filtered = String.join("", Arrays.copyOfRange(message.split("-"), 0, 1));
		ArrayList<String> names = new ArrayList<String>(Arrays.asList(filtered.split(",")));
		for (int i = 0; i < names.size(); i++){
			names.set(i, names.get(i).trim());
		}
		
		sender = names.get(0);
		Collections.sort(names);
		
		if (names.size() <= 2){ //avoids outOfBoundsExceptions by having <= 2
			chatString = String.join(" and ", names);
		}
		else{
			chatString = String.join(", ", names.subList(0, names.size() - 1));
			chatString = chatString + ", and " + names.get(names.size() - 1);
		}
		
		if ((names.size() == 1) && (names.get(0).equals("#" + name))){
			return ("You have entered an incorrect user. Try again.\n");
		}
		else if (names.size() == 1 && (names.get(0).charAt(0) != '#')){
			return (sender + " to chat between everyone: " + message.split("-")[1] + "\n");
		}
		else if (names.contains(name)){
			return (sender + " to chat between " + chatString + ": " + message.split("-")[1] + "\n");
		}
		return null;
	}	
}