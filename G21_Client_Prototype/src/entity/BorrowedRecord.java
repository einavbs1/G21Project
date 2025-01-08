package entity;

import java.sql.Date;
import java.util.ArrayList;
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
	private int librarianId;
	private String librarianName;
	private int borrowLostBook;
	
	//private static BorrowedRecords[] allBorrowedRecords;
	
	/**Author: Matan.
	 * Constractor that add new borrow record to the DB.
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
	public BorrowedRecord(int borrowNumber, int subscriberId, String bookBarcode, String bookTitle, int bookcopyNo, Date borrowDate,
			Date borrowExpectReturnDate, Date borrowActualReturnDate, int librarianId, String librarianName,int borrowLostBook)
	{
		this.borrowNumber = borrowNumber;
		this.subscriberId = subscriberId;
		this.bookBarcode = bookBarcode;
		this.bookTitle = bookTitle;
		this.bookcopyNo = bookcopyNo;
		this.borrowDate = borrowDate;
		this.borrowExpectReturnDate = borrowExpectReturnDate;
		this.borrowActualReturnDate = borrowActualReturnDate;
		this.librarianId = librarianId;
		this.librarianName = librarianName;
		this.borrowLostBook = borrowLostBook;
		
		String borrowRecord = toString();
		
		//change to DB format
		HashMap<String, String> addBorrowRecordMap = new HashMap<>();
		addBorrowRecordMap.put("AddBorrowRedocrd", borrowRecord);
		ClientUI.chat.accept(addBorrowRecordMap);
		
	}
	
	/** Author: Matan.
	 * Constructor that load borrow records from DB if exist. 
	 * @param id
	 */
	public BorrowedRecord(int borrowNumber) {
		loadBorrowRecord(getBorrowRecordFromDB(borrowNumber));
	}
	/*
	/**Author: Matan.
	 * loading all Borrowed Records of subscriber.
	 * @param str - BorrowRecord as string array (usually from the DB).
	 *//*
	private  static void loadBorrowRecords(List<String> listofstring) {
		int i = 0;
		for (String borrowedRecordString : listofstring) {
			String[] borrowedRecordParts = borrowedRecordString.split(",");
			
			allBorrowedRecords[i].bookBarcode = borrowedRecordParts[0];
			allBorrowedRecords[i].bookTitle = borrowedRecordParts[1];
			allBorrowedRecords[i].bookcopyNo = Integer.parseInt(borrowedRecordParts[2]);
			allBorrowedRecords[i].borrowActualReturnDate = Date.valueOf(borrowedRecordParts[3]);
			allBorrowedRecords[i].borrowDate = Date.valueOf(borrowedRecordParts[4]);
			allBorrowedRecords[i].borrowExpectReturnDate = Date.valueOf(borrowedRecordParts[5]);
			allBorrowedRecords[i].borrowLostBook = Integer.parseInt(borrowedRecordParts[6]);
			allBorrowedRecords[i].borrowNumber = Integer.parseInt(borrowedRecordParts[7]);
			allBorrowedRecords[i].librarianId = Integer.parseInt(borrowedRecordParts[8]);
			allBorrowedRecords[i].librarianName = borrowedRecordParts[9];
			allBorrowedRecords[i].subscriberId = Integer.parseInt(borrowedRecordParts[10]);
			
			i++;
		}
	} */
	
	/**Author: Matan.
	 * loading the details in this BorrowRecord object.
	 * @param str - BorrowRecord as string array (usually from the DB).
	 */
	private void loadBorrowRecord(String[]  str) {
		borrowNumber = Integer.parseInt(str[0]);
		subscriberId = Integer.parseInt(str[1]);
		bookBarcode = str[2];
		bookTitle = str[3];
		bookcopyNo = Integer.parseInt(str[4]);
		borrowDate = Date.valueOf(str[5]);
		borrowExpectReturnDate = Date.valueOf(str[6]);
		borrowActualReturnDate = Date.valueOf(str[7]);
		librarianId = Integer.parseInt(str[8]);
		librarianName = str[9];
		borrowLostBook = Integer.parseInt(str[10]);
	}
	
	/**Author: Matan.
	 * @param subscriberId
	 * @return - all borrow records of specific subcriber
	 * @throws NoSuchElementException
	 */
	private String[] getBorrowRecordFromDB(int borrowNumber) throws NoSuchElementException {
		String borrowedRecord = new String();	
		HashMap<String, String> BorrowRecordHashMap = new HashMap<>();
		BorrowRecordHashMap.put("getBorrowRecord", String.valueOf(subscriberId));
		
		ClientUI.chat.accept(BorrowRecordHashMap); // send request to DB to get record
		
		borrowedRecord = ChatClient.fromserverString;
		ChatClient.ResetServerString();
		
		if (borrowedRecord.contains(",")) {
			String[] recordParts = borrowedRecord.split(", ");
			return recordParts;
		} else {
			throw new NoSuchElementException("The subscriberId: "+subscriberId+" is not registered to the system.");
		}
	}
	
	/** Author: Matan.
	 * Update new return time and send update request to DB.
	 */
	public void UpdateBorrowRecordReturnTime() {
		String updatedBorrowRecord = toString();
		
		// Send the update record to the server
		HashMap<String, String> updatedBorrowRecordMap = new HashMap<>();
		updatedBorrowRecordMap.put("UpdateBorrowRecordReturnTime", updatedBorrowRecord);
		
		ClientUI.chat.accept(updatedBorrowRecordMap);
		
	}
	
	@Override
	public String toString() {
		return borrowNumber+", "+subscriberId+", "+bookBarcode+", "+bookTitle+", "+bookcopyNo+", "+borrowDate+", "+borrowExpectReturnDate+", "
				+borrowActualReturnDate+", "+librarianId+", "+librarianName+", "+borrowLostBook;
	}
	
	///////////////////////
	///     Getters
	///////////////////////

	public String getBookBarcode() {
		return bookBarcode;
	}


	public String getBookTitle() {
		return bookTitle;
	}


	public int getBookcopyNo() {
		return bookcopyNo;
	}


	public Date getBorrowActualReturnDate() {
		return borrowActualReturnDate;
	}


	public Date getBorrowDate() {
		return borrowDate;
	}


	public Date getBorrowExpectReturnDate() {
		return borrowExpectReturnDate;
	}


	public int getBorrowLostBook() {
		return borrowLostBook;
	}


	public int getBorrowNumber() {
		return borrowNumber;
	}


	public int getLibrarianId() {
		return librarianId;
	}


	public String getLibrarianName() {
		return librarianName;
	}


	public int getSubscriberId() {
		return subscriberId;
	}
	
	/////////////////////////////////////////////////
	///     			Setters
	///
	///  if we want to update the subscriber details:
	/// 	1. we will set the change.
	/// 	2. generate toString
	/// 	3. send String to save in DB.
	///
	///   ******* change only Dates(borrow extension) *******
	//////////////////////////////////////////////////
	
	public void setBorrowActualReturnDate(Date borrowActualReturnDate) {
		this.borrowActualReturnDate = borrowActualReturnDate;
	}


	public void setBorrowExpectReturnDate(Date borrowExpectReturnDate) {
		this.borrowExpectReturnDate = borrowExpectReturnDate;
	}


	public void setBorrowLostBook(int borrowLostBook) {
		this.borrowLostBook = borrowLostBook;
	}


	
	
	
	
}