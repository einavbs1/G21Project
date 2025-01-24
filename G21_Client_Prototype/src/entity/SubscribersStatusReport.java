package entity;

import java.util.HashMap;
import java.util.NoSuchElementException;

import client.ChatClient;
import client.ClientUI;
import common.Month;

public class SubscribersStatusReport {
	
    private int month;
    private int year;
    private int totalActiveSubscribers;
    private int totalFrozenSubscribers;
    private int gotFrozeSubscribers;
    private int unfrozedSubscribers;
    private int newSubscribers;
    private int totalSubscribers;
    private Month month2;
	
    /**Author: Einav
	 * Constractor that load notification from DB if exist.
	 * 
	 * @param id
	 */
	public SubscribersStatusReport(int month, int year) {
		loadReport(getReportFromDB(month, year));
	}

	

	/**Author: Einav
	 * private method that loading the details in this notification instance.
	 * 
	 * @param str -notification's details string array (usually from the DB).
	 */
	private void loadReport(String[] str) {
		month = Integer.parseInt(str[0]);
	    year = Integer.parseInt(str[1]);
	    totalActiveSubscribers = Integer.parseInt(str[2]);
	    totalFrozenSubscribers = Integer.parseInt(str[3]);
	    gotFrozeSubscribers = Integer.parseInt(str[4]);
	    unfrozedSubscribers = Integer.parseInt(str[5]);
	    newSubscribers = Integer.parseInt(str[6]);
	    totalSubscribers = Integer.parseInt(str[7]);
	    month2 = Month.getByNumber(month);
	}

	/**Author: Einav
	 * 
	 * @param serial
	 * @return String[] - array of notification's string with each field in array's positions.
	 * @throws NoSuchElementException
	 */
	private String[] getReportFromDB(int month, int year) throws NoSuchElementException {
		String str = new String();
		HashMap<String, String> requestHashMap = new HashMap<String, String>();
		requestHashMap.put("GetStatusReport", String.valueOf(month)+", "+String.valueOf(year));
		ClientUI.chat.accept(requestHashMap);
		/// send request to DB to get the string.
		str = ChatClient.getStringfromServer();
		if (str!=null && str.contains(",")) {
			String[] parts = str.split(", ");
			return parts;
		} else {
			throw new NoSuchElementException("We can't give Report for this Date: "+ getMonthString() +"."+year);
		}
	}
	
	
	 /**
	    * Loads all notifications from the server and displays them in the table.
	    * Sends request to server, receives notifications list and updates the table.
	    *
	   public static List<String> getAllNotificationsFromDB() {
	       // Create request HashMap
	       HashMap<String, String> showNotificationsMap = new HashMap<>();
	       showNotificationsMap.put("GetAllNotifications", "");
	       
	       // Send request to server
	       ClientUI.chat.accept(showNotificationsMap);
	       
	       // Get response from server
	       List<String> notificationsList = ChatClient.getListfromServer();
	       
	       return notificationsList;
	   }*/
	
	
	@Override
	public String toString() {
		return month + ", " + year + ", " + totalActiveSubscribers + ", " + totalFrozenSubscribers + ", " + gotFrozeSubscribers + ", " + unfrozedSubscribers + ", " + newSubscribers + ", " + totalSubscribers;
	}
	
	public int getMonth() {
		return month;
	}
	
	public String getMonthString() {
		if (month2 == null) {
	        return "Invalid Month";
	    }
	    return month2.toString();

	}

	public int getYear() {
		return year;
	}



	public int getTotalActiveSubscribers() {
		return totalActiveSubscribers;
	}



	public int getTotalFrozenSubscribers() {
		return totalFrozenSubscribers;
	}



	public int getGotFrozeSubscribers() {
		return gotFrozeSubscribers;
	}



	public int getUnfrozedSubscribers() {
		return unfrozedSubscribers;
	}



	public int getNewSubscribers() {
		return newSubscribers;
	}



	public int getTotalSubscribers() {
		return totalSubscribers;
	}
	
	
	
	///////////////////////
	/// Getters 
	/// No setters because we don't want to change notification.
	///////////////////////

	public void addTotalActive() {
		totalActiveSubscribers++;

	}
	
	public void addTotalFrozen() {
		totalFrozenSubscribers++;

	}
	
	
	public void addGotFroze() {
		gotFrozeSubscribers++;

	}
	
	public void addUnfrozed() {
		unfrozedSubscribers++;

	}
	
	public void addNewSubscriber() {
		newSubscribers++;

	}

	public void addtoTotal() {
		totalSubscribers++;

	}
	
	
}
