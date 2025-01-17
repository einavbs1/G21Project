package entity;

import java.util.HashMap;
import java.util.NoSuchElementException;

import client.ChatClient;
import client.ClientUI;
/**
 * Author: Einav
 */
public class Book {

	protected String barcode;
	protected String title;
	protected String subject;
	protected String description;
	protected int allCopies;
	protected int availableCopies;
	protected int ordersNumber;
	protected int lostNumber;
	protected String location;

	/**Author: Einav
	 * Constractor that load book from DB if exist.
	 * 
	 * @param id
	 */
	public Book(String barcode) {
		loadBook(getBookFromDB(barcode));
	}

	
	/**Author: Einav
	 * Constractor to add new book to DB
	 * @param barcode
	 * @param title
	 * @param subject
	 * @param description
	 * @param allCopies
	 * @param availableCopies
	 * @param ordersNumber
	 * @param lostNumber
	 * @param location
	 */
	public Book(String barcode, String title, String subject, String description, String location) {
		String newBook = barcode + ", " + title + ", " + subject + ", " + description + ", 0, 0, 0, 0, " + location;
		/// addBookToDB
		HashMap<String, String> requestHashMap = new HashMap<String, String>();
		requestHashMap.put("CreateBook", newBook);
		ClientUI.chat.accept(requestHashMap);		
		// now load to this Book
		loadBook(getBookFromDB(barcode));
	}

	/**Author: Einav
	 * private method that loading the details in this Book instance.
	 * 
	 * @param str -book's details string array (usually from the DB).
	 */
	private void loadBook(String[] str) {
		barcode = str[0];
		title = str[1];
		subject = str[2];
		description = str[3];
		allCopies = Integer.parseInt(str[4]);
		availableCopies = Integer.parseInt(str[5]);
		ordersNumber = Integer.parseInt(str[6]);
		lostNumber = Integer.parseInt(str[7]);
		location = str[8];
	}

	/**Author: Einav
	 * 
	 * @param barcode
	 * @return String[] - array of Book's string with each field in array's positions.
	 * @throws NoSuchElementException
	 */
	private String[] getBookFromDB(String barcode) throws NoSuchElementException {
		String str = new String();
		HashMap<String, String> requestHashMap = new HashMap<String, String>();
		requestHashMap.put("GetBook", barcode);
		ClientUI.chat.accept(requestHashMap);
		/// send request to DB to get the string.
		str = ChatClient.getStringfromServer();
		if (str.contains(",")) {
			String[] parts = str.split(", ");
			return parts;
		} else {
			throw new NoSuchElementException(
					"We are not recognizing this barcode: " + barcode + ". We might add it soon.");
		}
	}

	/**Author: Einav
	 *  After we set the new information that we want to save we will send request to
	 * DB. see details in the setter section VVV.
	 */
	public void UpdateDetails() {
		String newDetails = toString();
		// send request to DB to save all this data.
		HashMap<String, String> requestHashMap = new HashMap<String, String>();
		requestHashMap.put("UpdateBookDetails", newDetails);
		ClientUI.chat.accept(requestHashMap);
		// loading new information from DB. ------- was before update might delete.
		loadBook(getBookFromDB(barcode));

	}
	
	
	@Override
	public String toString() {
		return barcode + ", " + title + ", " + subject + ", " + description + ", " + allCopies + ", " + availableCopies
				+ ", " + ordersNumber + ", " + lostNumber + ", " + location;
	}
	
	///////////////////////
	///     Getters
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


	public int getAllCopies() {
		return allCopies;
	}


	public int getAvailableCopies() {
		return availableCopies;
	}


	public int getOrdersNumber() {
		return ordersNumber;
	}


	public int getLostNumber() {
		return lostNumber;
	}


	public String getLocation() {
		return location;
	}
	
	/////////////////////////////////////////////////
	///     			Setters
	///
	///  if we want to update the Book details:
	/// 	1. we will set the change.
	/// 	2. generate toString
	/// 	3. send String to save in DB.
	///
	///   ******* can change specific things. *******
	//////////////////////////////////////////////////
	
	
	public void setAllCopies(int allCopies) {
		this.allCopies = allCopies;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	
	
	
	/////////////// ----- adding 1 -----
	public void addToAllCopies() {
		this.availableCopies += 1;
	}
	
	
	public void addToAvailableCopies() {
		this.availableCopies += 1;
	}


	public void addToOrdersNumber() {
		this.ordersNumber += 1;
	}


	public void addToLostNumber() {
		this.lostNumber += 1;
	}
	
	/////////////// ----- Removing 1 -----
	public void removeFromAvailableCopies() {
		this.availableCopies -= 1;
	}


	public void removeFromOrdersNumber() {
		this.ordersNumber -= 1;
	}


	public void removeFromLostNumber() {
		this.lostNumber -= 1;
	}
	
	
}
