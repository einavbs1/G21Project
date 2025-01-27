package entity;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import client.ChatClient;
import client.ClientUI;
import javafx.collections.FXCollections;

public class Notifications {
	
    private int serial;
    private String message;
    private int subscriberID;
    private Date date;
    private int borrow_number;
	
    /**Author: Einav
	 * Constractor that load notification from DB if exist.
	 * 
	 * @param id
	 */
	public Notifications(int serial) {
		loadNotification(getNotificationFromDB(serial));
	}

	
	/**Author: Einav
	 * Constractor to add new notification to DB without the serial (automaticly added in the DB and returning us).
	 * @param message
	 * @param subscriberID
	 * @param date
	 * @param borrow_number
	 */
	public Notifications(String message, int subscriberID, Date date, int borrow_number) {
		String newNotification = message + ", " + subscriberID + ", " + date + ", " + borrow_number;
		/// addBookToDB
		HashMap<String, String> requestHashMap = new HashMap<String, String>();
		requestHashMap.put("Notifications+CreateNotification", newNotification);
		ClientUI.chat.accept(requestHashMap);		
		// now load to this Book
		String NewNotificationString = ChatClient.getStringfromServer();
		
		int NewNotificationNum =Integer.parseInt(NewNotificationString);
		
		loadNotification(getNotificationFromDB(NewNotificationNum));
	}

	/**Author: Einav
	 * private method that loading the details in this notification instance.
	 * 
	 * @param str -notification's details string array (usually from the DB).
	 */
	private void loadNotification(String[] str) {
		serial = Integer.parseInt(str[0]);
	    message = str[1];
	    subscriberID = Integer.parseInt(str[2]);
	    date = Date.valueOf(str[3]);
	    borrow_number = Integer.parseInt(str[4]);
	}

	/**Author: Einav
	 * 
	 * @param serial
	 * @return String[] - array of notification's string with each field in array's positions.
	 * @throws NoSuchElementException
	 */
	private String[] getNotificationFromDB(int serial) throws NoSuchElementException {
		String str = new String();
		HashMap<String, String> requestHashMap = new HashMap<String, String>();
		requestHashMap.put("Notifications+GetNotification", String.valueOf(serial));
		ClientUI.chat.accept(requestHashMap);
		/// send request to DB to get the string.
		str = ChatClient.getStringfromServer();
		if (str.contains(",")) {
			String[] parts = str.split(", ");
			return parts;
		} else {
			throw new NoSuchElementException(
					"We are not recognizing this notification: " + serial + ".");
		}
	}
	
	
	 /**
	    * Loads all notifications from the server and displays them in the table.
	    * Sends request to server, receives notifications list and updates the table.
	    */
	   public static List<String> getAllNotificationsFromDB() {
	       // Create request HashMap
	       HashMap<String, String> showNotificationsMap = new HashMap<>();
	       showNotificationsMap.put("Notifications+GetAllNotifications", "");
	       
	       // Send request to server
	       ClientUI.chat.accept(showNotificationsMap);
	       
	       // Get response from server
	       List<String> notificationsList = ChatClient.getListfromServer();
	       
	       return notificationsList;
	   }
	   
	   
	   public static List<String> getNewOldNotificationsFromDB(Date fromthisDate) {
	       // Create request HashMap
	       HashMap<String, String> showNotificationsMap = new HashMap<>();
	       showNotificationsMap.put("Notifications+GetNewOldNotifications", String.valueOf(fromthisDate));
	       
	       // Send request to server
	       ClientUI.chat.accept(showNotificationsMap);
	       
	       // Get response from server
	       List<String> notificationsList = ChatClient.getListfromServer();
	       
	       return notificationsList;
	   }
	   
	   
	   
	
	
	@Override
	public String toString() {
		return serial + ", " + message + ", " + subscriberID + ", " + date + ", " + borrow_number;
	}

	///////////////////////
	/// Getters 
	/// No setters because we don't want to change notification.
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


	public Date getDate() {
		return date;
	}


	public int getBorrow_number() {
		return borrow_number;
	}

	
	
	
}
