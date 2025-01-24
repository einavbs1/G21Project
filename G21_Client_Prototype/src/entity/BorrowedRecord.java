package entity;

import java.sql.Date;
import java.time.chrono.ThaiBuddhistChronology;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import client.ChatClient;
import client.ClientUI;

/**
 * Author: Matan
 */
public class BorrowedRecord {

	private int borrowNumber;
	private int subscriberId;
	private String bookBarcode;
	private String bookTitle;
	private int bookcopyNo;
	private Date borrowDate;
	private Date borrowExpectReturnDate;
	private Date borrowActualReturnDate;
	private Integer librarianId;
	private String librarianName;
	private int borrowLostBook;
	private int borrowStatus;


	/**
	 * Author: Matan. Constructor that load borrow records from DB if exist.
	 * 
	 * @param id
	 */
	public BorrowedRecord(int borrowNumber) {
		loadBorrowRecord(getBorrowRecordFromDB(borrowNumber));
	}

	/**
	 * Author: Matan.
	 * 	
	 * Constractor that add new borrow record to the DB.
	 * 
	 * @param borrowNumber
	 * @param subscriberId
	 * @param bookBarcode
	 * @param bookTitle
	 * @param bookcopyNo
	 * @param borrowDate
	 * @param borrowExpectReturnDate
	 * @param borrowActualReturnDate
	 * @param librarianId
	 * @param librarianName
	 * @param borrowLostBook
	 */
	public BorrowedRecord(int subscriberId, String bookBarcode, String bookTitle, int bookcopyNo) {
		Date currentDate = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, 14);
        Date newReturnDate = new Date(calendar.getTimeInMillis());
        
		String newBorrow = subscriberId + ", " + bookBarcode + ", " + bookTitle + ", " + bookcopyNo + ", "
				+ currentDate + ", " + newReturnDate + ", null, null, null, 0, 1";

		// change to DB format
		HashMap<String, String> AddNewBorrowMap = new HashMap<>();
		AddNewBorrowMap.put("AddNewBorrow", newBorrow);
		ClientUI.chat.accept(AddNewBorrowMap);
		
		String ordernumString = ChatClient.getStringfromServer();
		
		int newOrderNum =Integer.parseInt(ordernumString);
		
		loadBorrowRecord(getBorrowRecordFromDB(newOrderNum));

	}


	/**
	 * Author: Matan.
	 * loading the details in this BorrowRecord object.
	 * @param str - BorrowRecord as string array (usually from the DB).
	 */
	private void loadBorrowRecord(String[] str) {
		borrowNumber = Integer.parseInt(str[0]);
		subscriberId = Integer.parseInt(str[1]);
		bookBarcode = str[2];
		bookTitle = str[3];
		bookcopyNo = Integer.parseInt(str[4]);
		borrowDate = Date.valueOf(str[5]);
		borrowExpectReturnDate = Date.valueOf(str[6]);
		borrowActualReturnDate = ((str[7]).equals("null")) ? null : Date.valueOf(str[7]);
		librarianId = Integer.parseInt(str[8]);
		librarianName = str[9];
		borrowLostBook = Integer.parseInt(str[10]);
		borrowStatus = Integer.parseInt(str[11]);
	}

	/**
	 * Author: Matan.
	 * 
	 * @param borrowNumber
	 * @return borrow String[] of specific borrow number.
	 * @throws NoSuchElementException
	 */
	private String[] getBorrowRecordFromDB(int borrowNumber) throws NoSuchElementException {
		String borrowedRecord = new String();
		HashMap<String, String> BorrowRecordHashMap = new HashMap<>();
		BorrowRecordHashMap.put("GetBorrowRecord", String.valueOf(borrowNumber));

		ClientUI.chat.accept(BorrowRecordHashMap); // send request to DB to get record

		borrowedRecord = ChatClient.getStringfromServer();
		if (borrowedRecord.contains(",")) {
			String[] recordParts = borrowedRecord.split(", ");
			return recordParts;
		} else {
			throw new NoSuchElementException("The borrowNumber: " + borrowNumber + " is not exists in the system.");
		}
	}
	
	
	/** Author: Yuval.
	 * @param id - subscriber's id to get his active borrows.
	 */
	public static List<String> getSubscriberActiveBorrowsFromDB(int subscriberId) throws NoSuchElementException {

		
		/// send request to DB to get the string
		
	    HashMap<String, String> request = new HashMap<>();
	    request.put("SubscriberActiveBorrows", String.valueOf(subscriberId));
	    ClientUI.chat.accept(request);
	    List<String> response = ChatClient.getListfromServer();
	    
		if(response.isEmpty()) {
			throw new NoSuchElementException("There is no Active borrows for this id: " + subscriberId);
		}
		
		return response;
	}

	
	/**
	 * Author: Matan.
	 * Update new return time and send update request to DB.
	 */
	public boolean UpdateBorrowDetails() {
		String updatedBorrowRecord = toString();

		// Send the update record to the server
		HashMap<String, String> updatedBorrowRecordMap = new HashMap<>();
		updatedBorrowRecordMap.put("UpdateBorrowDetails", updatedBorrowRecord);

		ClientUI.chat.accept(updatedBorrowRecordMap);
		String str = ChatClient.getStringfromServer();
		if(str.contains("Error")) {
			return false;
		}
		return true;
	}
	
	/** author: Einav
	 * checking if the same subscriber is the owner of this borrow
	 * and updating this borrow to be lost if so.
	 * @param subid 
	 * @throws Exception
	 */
	public void lostThisBook(int subid) throws Exception {
		if (subscriberId == subid) {
			borrowActualReturnDate = null;
			borrowLostBook = 1;
			UpdateBorrowDetails();
		} else {
			throw new Exception("The id: " + subid + " isn't the owner of this borrow: " + borrowNumber);
		}
	}
	
	
	/**Author: Amir 18.1.2025 monthlyStatsMap
	 * Static method to get monthly borrowed books statistics
	 * Used for generating monthly reports
	 * @return List of borrowed books with loan duration details for the current month
	 */	
	public static List<String> getMonthlyBorrowedBooksStats() {
	    HashMap<String, String> monthlyStatsMap = new HashMap<>();
	    monthlyStatsMap.put("GetMonthlyBorrowedStats", "");
	    ClientUI.chat.accept(monthlyStatsMap);
	    return ChatClient.getListfromServer();
	}
	
	public static String getBookBorrowsInSpecificDate(String barcode,int month, int year) {
		HashMap<String, String> requestMap = new HashMap<>();
		requestMap.put("GetBorrowsOfBookInSpecificDate", barcode+", "+month+", "+year);
	    ClientUI.chat.accept(requestMap);
	    return ChatClient.getStringfromServer();
	}
	
	
	@Override
	public String toString() {
		return borrowNumber + ", " + subscriberId + ", " + bookBarcode + ", " + bookTitle + ", " + bookcopyNo + ", "
				+ borrowDate + ", " + borrowExpectReturnDate + ", " + borrowActualReturnDate + ", " + librarianId + ", "
				+ librarianName + ", " + borrowLostBook + ", " + borrowStatus;
	}

	///////////////////////
	/// Getters
	///////////////////////

	public int getBorrowNumber() {
		return borrowNumber;
	}

	public int getSubscriberId() {
		return subscriberId;
	}
	
	public String getBookBarcode() {
		return bookBarcode;
	}

	public String getBookTitle() {
		return bookTitle;
	}

	public int getBookcopyNo() {
		return bookcopyNo;
	}

	public Date getBorrowDate() {
		return borrowDate;
	}

	public Date getBorrowActualReturnDate() {
		return borrowActualReturnDate;
	}

	public Date getBorrowExpectReturnDate() {
		return borrowExpectReturnDate;
	}

	public int getLibrarianId() {
		return librarianId;
	}

	public String getLibrarianName() {
		return librarianName;
	}

	public int getBorrowLostBook() {
		return borrowLostBook;
	}

	public int getBorrowStatus() {
		return borrowStatus;
	}

	/////////////////////////////////////////////////
	/// Setters
	///
	/// if we want to update the subscriber details:
	/// 1. we will set the change.
	/// 2. generate toString
	/// 3. send String to save in DB.
	///
	/// ******* change only Dates(borrow extension) *******
	//////////////////////////////////////////////////

	public void setBorrowActualReturnDate(Date borrowActualReturnDate) {
		this.borrowActualReturnDate = borrowActualReturnDate;
	}

	public void setBorrowExpectReturnDate(Date borrowExpectReturnDate) {
		this.borrowExpectReturnDate = borrowExpectReturnDate;
	}
	
	public void setBorrowStatus(int borrowStatus) {
		this.borrowStatus = borrowStatus;
	}
	
	public void setLibrarianId(Integer librarianId) {
		this.librarianId = librarianId;
	}
	
	public void setLibrarianName(String librarianName) {
		this.librarianName = librarianName;
	}
	
	public void setBorrowLostBook(int borrowLostBook) {
		this.borrowLostBook = borrowLostBook;
	}
	


}