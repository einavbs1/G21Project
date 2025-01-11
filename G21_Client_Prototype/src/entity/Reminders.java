package entity;

import java.sql.Date;
import java.util.HashMap;
import java.util.NoSuchElementException;

import client.ChatClient;
import client.ClientUI;

public class Reminders {
	
    private int serial;
    private String message;
    private int subscriberID;
    private String subscriberPhone;
    private String subscriberEmail;
    private Date date;
    private int action_number;
	
    /**Author: Einav
	 * Constractor that load reminder from DB if exist.
	 * 
	 * @param id
	 */
	public Reminders(int serial) {
		loadReminder(getReminderFromDB(serial));
	}

	
	/**Author: Einav
	 * Constractor to add new reminder to DB without the serial (automaticly added in the DB and returning us).
	 * @param message
	 * @param subscriberID
	 * @param date
	 * @param borrow_number
	 */
	public Reminders(String message, int subscriberID,String subscriberPhone, String subscriberEmail, Date date, int action_number) {
		String newReminder = message + ", " + subscriberID + ", " + subscriberPhone + ", " + subscriberEmail + ", " + date + ", " + action_number;
		/// addBookToDB
		HashMap<String, String> requestHashMap = new HashMap<String, String>();
		requestHashMap.put("CreateReminder", newReminder);
		ClientUI.chat.accept(requestHashMap);		
		// now load to this Book
		String NewReminderString = ChatClient.getStringfromServer();
		
		int NewReminderNum =Integer.parseInt(NewReminderString);
		
		loadReminder(getReminderFromDB(NewReminderNum));
	}

	/**Author: Einav
	 * private method that loading the details in this reminder instance.
	 * 
	 * @param str -reminder's details string array (usually from the DB).
	 */
	private void loadReminder(String[] str) {
		serial = Integer.parseInt(str[0]);
	    message = str[1];
	    subscriberID = Integer.parseInt(str[2]);
	    subscriberPhone = str[3];
	    subscriberEmail = str[4];
	    date = Date.valueOf(str[5]);
	    action_number = Integer.parseInt(str[6]);
	    
	   
	}

	/**Author: Einav
	 * 
	 * @param serial
	 * @return String[] - array of reminder's string with each field in array's positions.
	 * @throws NoSuchElementException
	 */
	private String[] getReminderFromDB(int serial) throws NoSuchElementException {
		String str = new String();
		HashMap<String, String> requestHashMap = new HashMap<String, String>();
		requestHashMap.put("GetReminder", String.valueOf(serial));
		ClientUI.chat.accept(requestHashMap);
		/// send request to DB to get the string.
		str = ChatClient.getStringfromServer();
		if (str.contains(",")) {
			String[] parts = str.split(", ");
			return parts;
		} else {
			throw new NoSuchElementException(
					"We are not recognizing this reminder: " + serial + ".");
		}
	}
	
	
	@Override
	public String toString() {
		return serial + ", " + message + ", " + subscriberID + ", " + subscriberPhone + ", " + subscriberEmail + ", " + date + ", " + action_number;
	}

	
	///////////////////////
	/// Getters 
	/// No setters because we don't want to change reminders that already sent.
	///////////////////////


	public int getSerial() {
		return serial;
	}


	public String getMessage() {
		return message;
	}


	public int getSubscriberID() {
		return subscriberID;
	}


	public String getSubscriberPhone() {
		return subscriberPhone;
	}


	public String getSubscriberEmail() {
		return subscriberEmail;
	}


	public Date getDate() {
		return date;
	}


	public int getAction_number() {
		return action_number;
	}


	

	
	
	
}
