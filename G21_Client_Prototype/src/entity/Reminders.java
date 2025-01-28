package entity;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
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

	/**
	 * Author: Einav Constractor that load reminder from DB if exist.
	 * 
	 * @param serial
	 */
	public Reminders(int serial) {
		loadReminder(getReminderFromDB(serial));
	}

	/**
	 * Author: Einav Constractor to add new reminder to DB without the serial
	 * (automaticly added in the DB and returning us).
	 * 
	 * @param message
	 * @param subscriberID
	 * @param subscriberPhone
	 * @param subscriberEmail
	 * @param date
	 */
	public Reminders(String message, int subscriberID, String subscriberPhone, String subscriberEmail, Date date) {
		String newReminder = message + ", " + subscriberID + ", " + subscriberPhone + ", " + subscriberEmail + ", "
				+ date;
		/// addBookToDB
		HashMap<String, String> requestHashMap = new HashMap<String, String>();
		requestHashMap.put("Reminders+CreateReminder", newReminder);
		ClientUI.chat.accept(requestHashMap);
		// now load to this Book
		String NewReminderString = ChatClient.getStringfromServer();

		int NewReminderNum = Integer.parseInt(NewReminderString);

		loadReminder(getReminderFromDB(NewReminderNum));
	}

	/**
	 * Author: Einav private method that loading the details in this reminder
	 * instance.
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

	}

	/**
	 * A After we set the new information that we want to save we will send request
	 * to DB. see details in the setter section VVV.
	 */
	public boolean UpdateDetails() {
		HashMap<String, String> updateMap = new HashMap<>();
		updateMap.put("Reminders+UpdateReminderDetails", toString()); // Using toString to generate a string with all
																		// the details

		ClientUI.chat.accept(updateMap);
		String UpdateString = ChatClient.getStringfromServer();

		if (UpdateString.equals("Updated")) {
			loadReminder(getReminderFromDB(serial));
			return true;
		}
		return false;

	}

	/**
	 * Author: Einav
	 * 
	 * @param serial
	 * @return String[] - array of reminder's string with each field in array's
	 *         positions.
	 * @throws NoSuchElementException
	 */
	private String[] getReminderFromDB(int serial) throws NoSuchElementException {
		String str = new String();
		HashMap<String, String> requestHashMap = new HashMap<String, String>();
		requestHashMap.put("Reminders+GetReminder", String.valueOf(serial));
		ClientUI.chat.accept(requestHashMap);
		/// send request to DB to get the string.
		str = ChatClient.getStringfromServer();
		if (str.contains(",")) {
			String[] parts = str.split(", ");
			return parts;
		} else {
			throw new NoSuchElementException("We are not recognizing this reminder: " + serial + ".");
		}
	}

	/**
	 * Loads certain reminders(divided to new and old by fromthisDate) from the
	 * server and displays them in the table. Sends request to server, receives
	 *  reminders and updates the table.
	 *  @param fromthisDate
	 *  @param id
	 *  return all recordes of NewOldReminders with List<String> format
	 */
	public static List<String> getNewOldRemindersFromDB(Date fromthisDate, int id) {

		HashMap<String, String> showRemindersMap = new HashMap<>();
		showRemindersMap.put("Reminders+GetNewOldReminders", String.valueOf(fromthisDate) + ", " + id);

		ClientUI.chat.accept(showRemindersMap);
		List<String> remindersList = ChatClient.getListfromServer();

		return remindersList;
	}

	@Override
	public String toString() {
		return serial + ", " + message + ", " + subscriberID + ", " + subscriberPhone + ", " + subscriberEmail + ", "
				+ date;
	}

	///////////////////////
	/// Getters
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

	///////////////////////
	/// Setters
	///////////////////////

	public void setMessage(String message) {
		this.message = message;
	}

	public void setSubscriberPhone(String subscriberPhone) {
		this.subscriberPhone = subscriberPhone;
	}

	public void setSubscriberEmail(String subscriberEmail) {
		this.subscriberEmail = subscriberEmail;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
