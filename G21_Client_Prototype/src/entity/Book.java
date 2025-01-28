package entity;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
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

	/**
	 * Author: Einav Constractor that load book from DB if exist.
	 * 
	 * @param id
	 */
	public Book(String barcode) {
		loadBook(getBookFromDB(barcode));
	}

	/**
	 * Author: Einav Constractor to add new book to DB
	 * 
	 * @param barcode
	 * @param title
	 * @param subject
	 * @param description
	 * @param location
	 */
	public Book(String barcode, String title, String subject, String description, String location) {
		String newBook = barcode + ", " + title + ", " + subject + ", " + description + ", 0, 0, 0, 0, " + location;
		/// addBookToDB
		HashMap<String, String> requestHashMap = new HashMap<String, String>();
		requestHashMap.put("Book+CreateBook", newBook);
		ClientUI.chat.accept(requestHashMap);
		// now load to this Book
		loadBook(getBookFromDB(barcode));
	}

	/**
	 * Author: Einav private method that loading the details in this Book instance.
	 * 
	 * @param str -book's details string array (usually from the DB).
	 */
	private void loadBook(String[] str) {
		int len = str.length;
		barcode = str[0];
		title = str[1];
		subject = str[2];
		StringBuilder descriptionBuilder = new StringBuilder();
		for (int i = 3; i < len - 5; i++) {
			descriptionBuilder.append(str[i]);
			if (i != len - 6) {
				descriptionBuilder.append(", ");
			}
		}
		description = descriptionBuilder.toString();
		allCopies = Integer.parseInt(str[len - 5]);
		availableCopies = Integer.parseInt(str[len - 4]);
		ordersNumber = Integer.parseInt(str[len - 3]);
		lostNumber = Integer.parseInt(str[len - 2]);
		location = str[len - 1];
	}

	/**
	 * Author: Einav
	 * 
	 * @param barcode
	 * @return String[] - array of Book's string with each field in array's
	 *         positions.
	 * @throws NoSuchElementException
	 */
	private String[] getBookFromDB(String barcode) throws NoSuchElementException {
		String str = new String();
		HashMap<String, String> requestHashMap = new HashMap<String, String>();
		requestHashMap.put("Book+GetBook", barcode);
		ClientUI.chat.accept(requestHashMap);
		/// send request to DB to get the string.
		str = ChatClient.getStringfromServer();
		if (str.contains(",")) {
			String[] parts = str.split(", ");
			return parts;
		} else {
			throw new NoSuchElementException("Unfortunately, this barcode (" + barcode
					+ ") isn't recognized.\n We love adding new books as we evolve, it might join our library soon!");
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
		requestHashMap.put("Book+UpdateBookDetails", newDetails);
		ClientUI.chat.accept(requestHashMap);
		// loading new information from DB. ------- was before update might delete.
		loadBook(getBookFromDB(barcode));

	}

	
	/**
	 * send request of SearchBook to server for query to DB if choosen to search by name
	 * @param bookname 
	 * @return List<String> of books
	 */
	public static List<String> SearchBookByName(String bookname) {

		HashMap<String, String> requestHashMap = new HashMap<String, String>();
		requestHashMap.put("Book+SearchBookByName", bookname);
		ClientUI.chat.accept(requestHashMap);
		List<String> myRes = ChatClient.getListfromServer();

		return myRes;
	}

	/**
	 * send request of SearchBook to server for query to DB if choosen to search by subject
	 * @param subject
	 * @return List<String> of books
	 */
	public static List<String> SearchBookBySubject(String subject) {

		HashMap<String, String> requestHashMap = new HashMap<String, String>();
		requestHashMap.put("Book+SearchBookBySubject", subject);
		ClientUI.chat.accept(requestHashMap);
		List<String> myRes = ChatClient.getListfromServer();

		return myRes;
	}

	/**
	 * send request of SearchBook to server for query to DB if choosen to search by description
	 * @param tags
	 * @return List<String> of books
	 */
	public static List<String> SearchBookByDescription(String tags) {

		HashMap<String, String> requestHashMap = new HashMap<String, String>();
		requestHashMap.put("Book+SearchBookByDescription", tags);
		ClientUI.chat.accept(requestHashMap);
		List<String> myRes = ChatClient.getListfromServer();
		return myRes;
	}

	/**
	 * send requset to server for query to DB to get all the copies of this book
	 * @param barcode
	 * @return
	 */
	public static List<String> getAllmyCopies(String barcode) {

		HashMap<String, String> requestHashMap = new HashMap<String, String>();
		requestHashMap.put("Book+GetAllMyCopies", barcode);
		ClientUI.chat.accept(requestHashMap);
		List<String> myCopies = ChatClient.getListfromServer();

		return myCopies;

	}

	/**
	 * send requset to server for query to DB to get the closest return date of a copy of this book	
	 * @param barcode
	 * @return
	 */
	public static Date getClosestReturnDate(String barcode) {
		Date closestDate = null;
		List<String> myCopies = getAllmyCopies(barcode);
		for (String copyString : myCopies) {
			String[] temp = copyString.split(", ");
			if ((closestDate == null) || (Date.valueOf(temp[4]).before(closestDate))) {
				closestDate = Date.valueOf(temp[4]);
			}
		}
		return closestDate;

	}

	/**return list of book titles and their barcode
	 * @return List<String>
	 */
	public static List<String> getBookBarcodesAndTitles() {

		HashMap<String, String> requestHashMap = new HashMap<String, String>();
		requestHashMap.put("Book+GetBookBarcodesAndTitles", "");
		ClientUI.chat.accept(requestHashMap);
		List<String> myRes = ChatClient.getListfromServer();
		return myRes;

	}

	@Override
	public String toString() {
		return barcode + ", " + title + ", " + subject + ", " + description + ", " + allCopies + ", " + availableCopies
				+ ", " + ordersNumber + ", " + lostNumber + ", " + location;
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
	/// Setters
	///
	/// if we want to update the Book details:
	/// 1. we will set the change.
	/// 2. generate toString
	/// 3. send String to save in DB.
	///
	/// ******* can change specific things. *******
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


	public void addToAvailableCopies() throws Exception {
		if (availableCopies < allCopies - lostNumber) {
			this.availableCopies += 1;
		} else {
			throw new Exception("Cant avaiable another copy. Avialable copies reach to maximum.");
		}
	}

	public void addToOrdersNumber() throws Exception {
		if (ordersNumber < allCopies - lostNumber) {
			this.ordersNumber += 1;
		} else {
			throw new Exception("Cant make order for this book. orders number reach to maximum.");
		}
	}

	public void addToLostNumber() throws Exception {
		if (lostNumber < allCopies) {
			this.lostNumber += 1;
		} else {
			throw new Exception("Cant lost another book. All the copies already lost.");
		}
	}

	/////////////// ----- Removing 1 -----
	public void removeFromAvailableCopies() throws Exception {
		if (availableCopies > 0) {
			this.availableCopies -= 1;
		} else {
			throw new Exception("Available copies can't be negative number.");
		}
	}

	public void removeFromOrdersNumber() throws Exception {
		if (ordersNumber > 0) {
			this.ordersNumber -= 1;
		} else {
			throw new Exception("Order number can't be negative number.");
		}
	}

	public void removeFromLostNumber() throws Exception {
		if (lostNumber > 0) {
			this.lostNumber -= 1;
		} else {
			throw new Exception("Book lost can't be negative number.");
		}
	}

}
