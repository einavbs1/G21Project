package entity;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import client.ChatClient;
import client.ClientUI;


//CLASS ADDED BY AMIR
public class LogActivity {
	
    private int activitySerial;
    private int subscriberId;
    private String activityAction;
    private String bookBarcode;
    private String bookTitle;
    private Integer bookcopyCopyNo;
    private Date activityDate;

    
    
    /**Author: Amir
     * Constructor for loading from DB using serial number
     * @param activitySerial The serial number of the activity to load
     */
    public LogActivity(int activitySerial) {
        String[] str = getActivityBySerial(activitySerial);
        if (str != null) {
            loadActivity(str);
        }
    }
    
    
    /**Author: Amir
     * Constructor for creating a new activity log entry
     * @param subscriberId ID of the subscriber who performed the action
     * @param activityAction Description of the action performed
     * @param bookBarcode Barcode of the book involved (if applicable)
     * @param bookTitle Title of the book involved (if applicable)
     * @param bookcopyCopyNo Copy number of the book (if applicable)
     */
    public LogActivity(int subscriberId, String activityAction, 
    		String bookBarcode, String bookTitle, int bookcopyCopyNo) {
    	Date currentDate = Date.valueOf(LocalDate.now());
    
    	String newActivityLog = subscriberId + ", " + activityAction + ", " + bookBarcode + ", " + bookTitle + ", "
				+ bookcopyCopyNo + ", " + currentDate;
    	HashMap<String, String> addNewActivityLogMap = new HashMap<>();
		addNewActivityLogMap.put("LogActivity+AddNewLogActivity", newActivityLog);
		ClientUI.chat.accept(addNewActivityLogMap);
    	
		String NewActivityLogString = ChatClient.getStringfromServer();
		
		int NewActivityLogNum =Integer.parseInt(NewActivityLogString);
		
		loadActivity(getActivityBySerial(NewActivityLogNum));
		
	//this.subscriberId = subscriberId;
	//this.activityAction = activityAction;
	//this.bookBarcode = bookBarcode;
	//this.bookTitle = bookTitle;
	//this.bookcopyCopyNo = bookcopyCopyNo;
	//this.activityDate = new Date(System.currentTimeMillis());
	}
    

    /**Author: Amir
     * 
     * Load activity details from string array (from DB)
     * @param str Array containing activity details
     */
    private void loadActivity(String[] str) {
        this.activitySerial = Integer.parseInt(str[0]);
        this.subscriberId = Integer.parseInt(str[1]);
        this.activityAction = str[2];
        this.bookBarcode = str[3];
        this.bookTitle = str[4];
        this.bookcopyCopyNo = (str[5] != null && !str[5].equals("null")) ? Integer.parseInt(str[5]) : null;
        this.activityDate = Date.valueOf(str[6]);
    }

    /**Author: Amir
     * Load a single activity by its serial number from the server
     * @param serialNumber The serial number of the activity to load
     * @return String array containing activity details or null if not found
     */
    private static String[] getActivityBySerial(int serialNumber) {
    	
        HashMap<String, String> loadMap = new HashMap<>();
        loadMap.put("LogActivity+LoadActivityBySerial", String.valueOf(serialNumber));
        ClientUI.chat.accept(loadMap);
        
        String resultfromDB = ChatClient.getStringfromServer();
        
        if (!resultfromDB.equals("Record not found")) {
            String[] result = resultfromDB.split(", ");
            return result;
        }
        return null;
    }

    /**Author: Amir
     * Load all activities for a specific subscriber from the server
     * @param subscriberId The subscriber ID to get activities for
     * @return List of activities as strings
     */
    public static List<String> loadActivitiesBySubscriberId(int subscriberId) {
        HashMap<String, String> loadMap = new HashMap<>();
        loadMap.put("LogActivity+LoadActivityById", String.valueOf(subscriberId));
        
        ClientUI.chat.accept(loadMap);
        
        List<String> result = ChatClient.getListfromServer();
        return result;
    }

    /**Author: Amir
     * Convert activity to string format for DB storage
     */
    @Override
    public String toString() {
        return activitySerial + ", " + subscriberId + ", " + activityAction + ", " + 
               (bookBarcode != null ? bookBarcode : "null") + ", " + 
               (bookTitle != null ? bookTitle : "null") + ", " + 
               (bookcopyCopyNo != null ? bookcopyCopyNo.toString() : "null") + ", " + 
               activityDate.toString();
    }

    // Getters
    public int getActivitySerial() {
        return activitySerial;
    }

    public int getSubscriberId() {
        return subscriberId;
    }

    public String getActivityAction() {
        return activityAction;
    }

    public String getBookBarcode() {
        return bookBarcode;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public Integer getBookcopyCopyNo() {
        return bookcopyCopyNo;
    }

    public Date getActivityDate() {
        return activityDate;
    }
}