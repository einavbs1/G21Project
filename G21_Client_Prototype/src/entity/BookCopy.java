package entity;

import java.sql.Date;
import java.util.HashMap;
import java.util.NoSuchElementException;
import client.ChatClient;
import client.ClientUI;

/**
 * Author: Einav
 */
public class BookCopy extends Book {

	private int copyNo;
	private int isAvailable;
	private int isLost;
	private Date returnDate;
	private int subscriberID;

	/**
	 * Author: Einav Constractor that load bookCopy from DB if exist.
	 * 
	 * @param barcode
	 * @param CopyNo
	 */
	public BookCopy(String barcode, int copyNo) {
		super(barcode);
		String[] str = getBookCopyFromDB(barcode, copyNo);
		loadBookCopy(str);
	}

	/**
	 * Author: Einav Constractor that adding new bookcopy to DB
	 * 
	 * @param barcode
	 */
	public BookCopy(String barcode) {
		super(barcode);
		int newCopyNo = getAllCopies() + 1;
		String newBookCopy = barcode + ", " + newCopyNo + ", 1, 0, null, null";
		/// addBookCopyToDB
		HashMap<String, String> requestHashMap = new HashMap<String, String>();
		requestHashMap.put("CreateBookCopy", newBookCopy);
		ClientUI.chat.accept(requestHashMap);
		// after adding The bookCopy adding +1 to the book counter.
		Book mybook = new Book(barcode);
		mybook.addToAvailableCopies();
		mybook.addToAllCopies();
		mybook.UpdateDetails();
		// loading this copy from db
		String[] str = getBookCopyFromDB(barcode, newCopyNo);
		loadBookCopy(str);
	}

	/**
	 * Author: Einav private method that loading the details in this Book instance.
	 * 
	 * @param str -book's details string array (usually from the DB).
	 */
	private void loadBookCopy(String[] str) {
		barcode = str[0];
		copyNo = Integer.parseInt(str[1]);
		isAvailable = Integer.parseInt(str[2]);
		isLost = Integer.parseInt(str[3]);
		returnDate = Date.valueOf(str[4]);
		subscriberID = Integer.parseInt(str[5]);
	}

	/**
	 * Author: Einav
	 * 
	 * @param barcode
	 * @return String[] - array of BookCopy's string with each field in array's
	 *         positions.
	 * @throws NoSuchElementException
	 */
	private String[] getBookCopyFromDB(String barcode, int copyNo) throws NoSuchElementException {
		String str = new String();
		HashMap<String, String> requestHashMap = new HashMap<String, String>();
		requestHashMap.put("GetBookCopy", barcode + ", " + String.valueOf(copyNo));
		ClientUI.chat.accept(requestHashMap);
		/// send request to DB to get the string.
		str = ChatClient.getStringfromServer();
		if (str.contains(",")) {
			String[] parts = str.split(", ");
			return parts;
		} else {
			throw new NoSuchElementException(
					"This book :" + barcode + " has no copies in our library. We might add it soon.");
		}
	}

	/**
	 * Author: Einav After we set the new information that we want to save we will
	 * send request to DB. see details in the setter section VVV.
	 */
	public void UpdateDetails() {
		String newDetails = toString();
		// send request to DB to save all this data.
		HashMap<String, String> requestHashMap = new HashMap<String, String>();
		requestHashMap.put("UpdateBookCopyDetails", newDetails);
		ClientUI.chat.accept(requestHashMap);
		// loading new information from DB. ------- was before update might delete.
		loadBookCopy(getBookCopyFromDB(barcode, copyNo));

	}

	/**
	 * Author: Einav checking if the same subscriber is the holder of this book at
	 * this moment if he is so update to lost the book.
	 * 
	 * @param subid - subscriber id (holder of the book)
	 * @throws Exception - id that lost the book doesn't hold this copy.
	 */
	public void lostThisCopy(int subid) throws Exception {
		if (subscriberID == subid) {
			returnDate = null;
			Book mybook = new Book(barcode);
			mybook.addToLostNumber();
			mybook.UpdateDetails();
			isLost = 1;
			UpdateDetails();
		} else {
			throw new Exception("The id: " + subid + " isn't the owner of this bookcopy: " + barcode + ", " + copyNo);
		}
	}

	@Override
	public String toString() {
		return barcode + ", " + copyNo + ", " + isAvailable + ", " + isLost + ", " + returnDate + ", " + subscriberID;
	}

	///////////////////////
	/// Getters
	///////////////////////
	public String getBarcode() {
		return barcode;
	}

	public String getTitle() {
		return title;
	}

	public String getSubject() {
		return subject;
	}

	public String getDescription() {
		return description;
	}

	public String getLocation() {
		return location;
	}

	/////////////////////////////////////////////////
	/// Setters
	///
	/// if we want to update the Book details:
	/// 1. we will set the change.
	/// 2. generate toString
	/// 3. send String to save in DB.
	///
	/// ******* can change specific things. *******
	//////////////////////////////////////////////////

	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}

	public void setSubscriberID(int subscriberID) {
		this.subscriberID = subscriberID;
	}

}
