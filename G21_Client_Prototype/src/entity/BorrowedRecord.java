package entity;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
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
	private int librarianId;
	private String librarianName;
	private int borrowLostBook;


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
	public BorrowedRecord(int subscriberId, String bookBarcode, String bookTitle, int bookcopyNo, int librarianId,
			String librarianName) {
		Date currentDate = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, 14);
        Date newReturnDate = new Date(calendar.getTimeInMillis());
        
		String newBorrow = subscriberId + ", " + bookBarcode + ", " + bookTitle + ", " + bookcopyNo + ", "
				+ currentDate + ", " + newReturnDate + ", null, " + librarianId + ", " + librarianName
				+ ", 0";

		// change to DB format
		HashMap<String, String> AddNewBorrowMap = new HashMap<>();
		AddNewBorrowMap.put("AddNewBorrow", newBorrow);
		ClientUI.chat.accept(AddNewBorrowMap);
		
		String ordernumString = ChatClient.fromserverString;
		ChatClient.ResetServerString();
		
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
		borrowActualReturnDate = (str[7] == null || str[7].equals("")) ? null : Date.valueOf(str[7]);
		librarianId = Integer.parseInt(str[8]);
		librarianName = str[9];
		borrowLostBook = Integer.parseInt(str[10]);
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

		borrowedRecord = ChatClient.fromserverString;
		ChatClient.ResetServerString();

		if (borrowedRecord.contains(",")) {
			String[] recordParts = borrowedRecord.split(", ");
			return recordParts;
		} else {
			throw new NoSuchElementException("The borrowNumber: " + borrowNumber + " is not exists in the system.");
		}
	}

	
	/**
	 * Author: Matan.
	 * Update new return time and send update request to DB.
	 */
	public void UpdateBorrowDetails() {
		String updatedBorrowRecord = toString();

		// Send the update record to the server
		HashMap<String, String> updatedBorrowRecordMap = new HashMap<>();
		updatedBorrowRecordMap.put("UpdateBorrowDetails", updatedBorrowRecord);

		ClientUI.chat.accept(updatedBorrowRecordMap);

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
	

	@Override
	public String toString() {
		return borrowNumber + ", " + subscriberId + ", " + bookBarcode + ", " + bookTitle + ", " + bookcopyNo + ", "
				+ borrowDate + ", " + borrowExpectReturnDate + ", " + borrowActualReturnDate + ", " + librarianId + ", "
				+ librarianName + ", " + borrowLostBook;
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


}