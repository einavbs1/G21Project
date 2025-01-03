package entity;

import java.sql.Date;
import java.util.NoSuchElementException;

public class BookCopy extends Book {

	private int copyNo;
	private int isAvailable;
	private int isLost;
	private Date returnDate;
	private int subscriberID;

	/**Author: Einav
	 * Constractor that load bookCopy from DB if exist.
	 * @param barcode
	 * @param CopyNo
	 */
	public BookCopy(int barcode, int CopyNo) {
		super(barcode);
		String[] str = getBookCopyFromDB(barcode);
		loadBookCopy(str);
	}


	/**Constractor that adding new bookcopy to DB
	 * @param barcode
	 * @param CopyNo
	 * @param isNew - just to distinguish between two constractors
	 */
	public BookCopy(int barcode, int CopyNo, Boolean isNew) {
		super(barcode);
		String newBookCopy = barcode + ", " + CopyNo + ", 1, 0, null, null";
		/// addBookCopyToDB
		// now load to this Book
		loadBookCopy(getBookCopyFromDB(barcode));
		super.addToAvailableCopies();
		super.addToAllCopies();
	}

	/**
	 * private method that loading the details in this Book instance.
	 * 
	 * @param str -book's details string array (usually from the DB).
	 */
	private void loadBookCopy(String[] str) {
		barcode = Integer.parseInt(str[0]);
		title = str[1];
		subject = str[2];
		description = str[3];
		allCopies = Integer.parseInt(str[4]);
		availableCopies = Integer.parseInt(str[5]);
		ordersNumber = Integer.parseInt(str[6]);
		lostNumber = Integer.parseInt(str[7]);
		location = str[8];
	}

	private String[] getBookCopyFromDB(int barcode) throws NoSuchElementException {
		String str = new String();
		/// send request to DB to get the string.
		if (str.contains(",")) {
			String[] parts = str.split(", ");
			return parts;
		} else {
			throw new NoSuchElementException(
					"We are not recognizing this barcode: " + barcode + ". We might add it soon.");
		}
	}

	/**
	 * After we set the new information that we want to save we will send request to
	 * DB. see details in the setter section VVV.
	 */
	public void UpdateDetails() {
		String newDetails = toString();
		// send request to DB to save all this data.

		// loading new information from DB. ------- was before update might delete.
		loadBookCopy(getBookCopyFromDB(barcode));

	}

	
	
	


	@Override
	public String toString() {
		return barcode + ", " + copyNo + ", " + isAvailable + ", " + isLost + ", " + returnDate + ", " + subscriberID;
	}

	///////////////////////
	/// Getters
	///////////////////////
	public int getBarcode() {
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
	
	
	
	public void lostThisCopy(int subid) throws Exception {
		if (subscriberID == subid) {
			returnDate = null;
			isLost = 1;
			UpdateDetails();
		} else {
			throw new Exception("The id: " + subid + " isn't the owner of this bookcopy: " + barcode + ", " + copyNo);
		}
	}
	
	
}
