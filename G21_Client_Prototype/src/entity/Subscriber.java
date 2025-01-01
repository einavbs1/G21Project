package entity;

import java.util.NoSuchElementException;

//avishag
//einavvvv - test
//new test?- einav 
//new update -need to check pull request?
//avishag - testtt
public class Subscriber {

	private String id;
	private String name;
	private String subscripption_details;
	private String phoneNumber;
	private String email;
	private String password;
	private String status;

	public Subscriber(String id) {
		String[] str = getStudentFromDB(id);
		loadSubscriber(str);
	}
	
	public Subscriber(String id, String name, String phoneNumber, String email, String password) {
		String newsub = id +", "+name+", 0,"+phoneNumber+", "+email+", "+password+", Active";
		///addToDB
		//now load to this subscriber
		String[] str = getStudentFromDB(id);
		loadSubscriber(str);
	}
	
	private void loadSubscriber(String[] str) {
		id = str[0];
		name = str[1];
		subscripption_details = str[2];
		phoneNumber = str[3];
		email = str[4];
		password = str[5];
		status = str[6];
	}

	private String[] getStudentFromDB(String id) throws NoSuchElementException {
		String str = new String();
		/// send request to DB to get the string.
		if (str.contains(",")) {
			String[] parts = str.split(", ");
			return parts;
		} else {
			throw new NoSuchElementException("This id is not registered to the system.");
		}
	}
	
	public Boolean changePassword(String newPassword) throws Exception {
		if(password.equals(newPassword)) {
			throw new Exception("You must enter a diffrent password than your current.");
		}
		// send request to DB with this.id + newPassword
		
		//loading new password from DB.
		loadSubscriber(getStudentFromDB(id));
		if(password.equals(newPassword)) { //if changed.
			return true;
		}
		else {//else return false;
			return false;
		}
		
	}
	
	
	

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getSubscripption_details() {
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
	
	

}
