package entity;

import java.sql.Date;
import java.util.ArrayList;
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

    /**
     * Constructor for creating a new activity log entry
     * @param subscriberId ID of the subscriber who performed the action
     * @param activityAction Description of the action performed
     * @param bookBarcode Barcode of the book involved (if applicable)
     * @param bookTitle Title of the book involved (if applicable)
     * @param bookcopyCopyNo Copy number of the book (if applicable)
     */
    public LogActivity(int subscriberId, String activityAction, 
    		String bookBarcode, String bookTitle, Integer bookcopyCopyNo) { 
	this.subscriberId = subscriberId;
	this.activityAction = activityAction;
	this.bookBarcode = bookBarcode;
	this.bookTitle = bookTitle;
	this.bookcopyCopyNo = bookcopyCopyNo;
	this.activityDate = new Date(System.currentTimeMillis());
	}
    
    
    
    
    /**
     * Constructor for loading from DB using serial number
     * @param activitySerial The serial number of the activity to load
     */
    public LogActivity(int activitySerial) {
        String[] str = loadActivityBySerial(activitySerial);
        if (str != null) {
            loadActivity(str);
        }
    }
	
	//Saving action to data
	public boolean saveNewActivity() {
		HashMap<String, String> saveMap = new HashMap<>();
		String activityData = subscriberId + ", " + activityAction + ", " + 
		               bookBarcode + ", " + bookTitle + ", " + 
		               bookcopyCopyNo + ", " + activityDate;
		saveMap.put("SaveLogActivity", activityData);
		
		ClientUI.chat.accept(saveMap);
		
		while (ChatClient.fromserverString.equals(new String())) {
		  try {
		      Thread.sleep(100);
		  } catch (InterruptedException e) {
		      e.printStackTrace();
		  }
		}
		
		boolean success = ChatClient.fromserverString.equals("Updated");
		ChatClient.ResetServerString();
		return success;
	}

    /**
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

    /**
     * Load a single activity by its serial number from the server
     * @param serialNumber The serial number of the activity to load
     * @return String array containing activity details or null if not found
     */
    public static String[] loadActivityBySerial(int serialNumber) {
        HashMap<String, String> loadMap = new HashMap<>();
        loadMap.put("LoadActivityBySerial", String.valueOf(serialNumber));
        
        ClientUI.chat.accept(loadMap);
        
        while (ChatClient.fromserverString.equals(new String())) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        if (!ChatClient.fromserverString.equals("Record not found")) {
            String[] result = ChatClient.fromserverString.split(", ");
            ChatClient.ResetServerString();
            return result;
        }
        ChatClient.ResetServerString();
        return null;
    }

    /**
     * Load all activities for a specific subscriber from the server
     * @param subscriberId The subscriber ID to get activities for
     * @return List of activities as strings
     */
    public static List<String> loadActivitiesBySubscriberId(int subscriberId) {
        HashMap<String, String> loadMap = new HashMap<>();
        loadMap.put("LoadActivityById", String.valueOf(subscriberId));
        
        ClientUI.chat.accept(loadMap);
        
        // ממתינים לתשובה מהשרת
        while (ChatClient.awaitResponse) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        List<String> result = new ArrayList<>(ChatClient.subscribersTable);
        ChatClient.subscribersTable.clear();
        return result;
    }

    /**
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