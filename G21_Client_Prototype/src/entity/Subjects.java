package entity;

import java.util.HashMap;
import java.util.List;

import client.ChatClient;
import client.ClientUI;

public class Subjects {
	
	String subject_name;
	
	
	public Subjects() {
		
	}
	
	
	/**This method is getting all the subjects from the DB.
	 * @return List<String> of the subjects.
	 */
	public static List<String> getAllSubjects(){
		
		HashMap<String, String> requestHashMap = new HashMap<String, String>();
		requestHashMap.put("Book+GetAllSubjects", "");
		ClientUI.chat.accept(requestHashMap);
		List<String> myRes = ChatClient.getListfromServer();
		
		return myRes;
	}
	
	
	

}
