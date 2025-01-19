package entity;

import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import client.ChatClient;
import client.ClientUI;

public class Subscriber {

	private static int id;
	private String name;
	private int subscripption_details;
	private String phoneNumber;
	private String email;
	private String password;
	private String status;

	
	/** Author: Avishag.
	 * Constructor that load id from DB if exist. 
	 * @param id
	 */
	public Subscriber(int id) {
		String[] str = getSubscriberFromDB(id);
		loadSubscriber(str);
	}
	
	
	/** Author: Avishag.
	 * Constructor that creates a new subscriber and adds it to the DB.
	 * @param id          - Subscriber ID
	 * @param name        - Subscriber name
	 * @param phoneNumber - Subscriber phone number
	 * @param email       - Subscriber email address
	 * @param password    - Subscriber password
	 */
	public Subscriber(int id, String name, String phoneNumber, String email, String password) {
		String newsub = id +", "+name+", 0,"+phoneNumber+", "+email+", "+password+", Active";
		
		///addSubscriberToDB
		HashMap<String, String> addSubscriberMap = new HashMap<>();
	    addSubscriberMap.put("AddNewSubscriber", newsub);
	    ClientUI.chat.accept(addSubscriberMap);

		//now load to this subscriber
		String[] str = getSubscriberFromDB(id);
		loadSubscriber(str);
	}
	
	
	/** Author: Avishag.
	 * private method that loading the details in this id subscriber instance.
	 * @param str - subscriber's details string array (usually from the DB).
	 */
	private void loadSubscriber(String[] str) {
		id = Integer.parseInt(str[0]);
		name = str[1];
		subscripption_details = Integer.parseInt(str[2]);
		phoneNumber = str[3];
		email = str[4];
		password = str[5];
		status = str[6];
	}

	
	/** Author: Avishag.
	 * @param id - subscriber's id to get.
	 */
	private String[] getSubscriberFromDB(int id) throws NoSuchElementException {
		String response = new String();
		
		/// send request to DB to get the string
		
	    HashMap<String, String> request = new HashMap<>();
	    request.put("GetSubscriberDetails", String.valueOf(id));
	    ClientUI.chat.accept(request);
	    response = ChatClient.getStringfromServer();
	    
		if (response.contains(",")) {
			String[] parts = response.split(", ");
			return parts;
		} else {
			throw new NoSuchElementException("The id: "+id+" is not registered to the system.");
		}
	}

	
	/** Author: Avishag.
	 * After we set the new information that we want to save we will send request to DB.
	 * see details in the setter section VVV. 
	 */
	public void UpdateDetails(){
	    String newDetails = toString();	    
	    /// Send the update request to the server

	    HashMap<String, String> updateMap = new HashMap<>();
	    updateMap.put("UpdateSubscriber", newDetails);

	    ClientUI.chat.accept(updateMap);
	    
		//loading new information from DB. ------- was before update might delete.
	    loadSubscriber(getSubscriberFromDB(id));
	}
	
	
	/**
	 * Author: Amir 19.1.2025
	 * Static method to get monthly subscriber statistics
	 * Used for generating monthly subscriber status reports.
	 * Returns current subscriber status including.
	 * @return List of subscribers with their monthly status details
	 */
	public static List<String> getMonthlySubscriberStats() {
	    HashMap<String, String> monthlyStatsMap = new HashMap<>();
	    monthlyStatsMap.put("GetMonthlySubscriberStats", "");
	    ClientUI.chat.accept(monthlyStatsMap);
	    return ChatClient.getListfromServer();
	}
	
	
	/** Author: Avishag.
	 *toString of subscriber
	 */
	public String toString() {
		return id+", "+name+", "+subscripption_details+", "+phoneNumber+", "+email+", "+password+", "+status;
	}
	
	
	/////////////////////////////////
	/// Getters - Author: Avishag.
	/////////////////////////////////
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getSubscripption_details() {
		return subscripption_details;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public String getStatus() {
		return status;
	}
	
	
	/////////////////////////////////////////////////
	///     	Setters - Author: Avishag.
	///
	///  if we want to update the subscriber details:
	/// 	1. we will set the change.
	/// 	2. generate toString
	/// 	3. send String to save in DB.
	///
	///   ******* can't change the ID *******
	//////////////////////////////////////////////////

	public void setName(String name) {
		this.name = name;
	}

	public void setSubscripption_details(int subscripption_details) {
		this.subscripption_details = subscripption_details;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
