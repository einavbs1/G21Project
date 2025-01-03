package entity;

import java.util.NoSuchElementException;

public class Subscriber {

	private static int id;
	private String name;
	private int subscripption_details;
	private String phoneNumber;
	private String email;
	private String password;
	private String status;

	
	/**Constractor that load id from DB if exist. 
	 * @param id
	 */
	public Subscriber(int id) {
		String[] str = getStudentFromDB(id);
		loadSubscriber(str);
	}
	
	/**Constractor that creating new subscriber and adding it to DB.
	 * @param id
	 * @param name
	 * @param phoneNumber
	 * @param email
	 * @param password
	 */
	public Subscriber(int id, String name, String phoneNumber, String email, String password) {
		String newsub = id +", "+name+", 0,"+phoneNumber+", "+email+", "+password+", Active";
		///addSוubscriberToDB
		//now load to this subscriber
		String[] str = getStudentFromDB(id);
		loadSubscriber(str);
	}
	
	
	/**private method that loading the details in this id subscriber instance.
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

	private String[] getStudentFromDB(int id) throws NoSuchElementException {
		String str = new String();
		/// send request to DB to get the string.
		if (str.contains(",")) {
			String[] parts = str.split(", ");
			return parts;
		} else {
			throw new NoSuchElementException("The id: "+id+" is not registered to the system.");
		}
	}
	
	

	/**
	 * After we set the new information that we want to save we will send request to DB.
	 * see details in the setter section VVV. 
	 */
	public void UpdateDetails(){
		String newDetails = toString();
		// send request to DB to save all this data.
		
		//loading new information from DB. ------- was before update might delete.
		loadSubscriber(getStudentFromDB(id));
		
	}
	
	
	
	/**toString of subscriber
	 *
	 */
	public String toString() {
		return id+", "+name+", "+subscripption_details+", "+phoneNumber+", "+email+", "+password+", "+status;
	}
	
	///////////////////////
	///     Getters
	///////////////////////
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
	///     			Setters
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
