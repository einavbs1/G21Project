package Server;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/*
 * This class is connect to mySQL DB for G21-prototype server. 
 */
/**
 * 
 */
/**
 * 
 */
public class mysqlConnection {

	public static Connection conn;

	/*
	 * This method is connecting to out G21-prototype server
	 */
	public static String connectToDB() {
		String ret = "";
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			ret = ret + ("Driver definition succeed");

		} catch (Exception ex) {
			/* handle the error */
			ret = ret + ("Driver definition failed");
		}

		try {
			conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/g21_project_schema?serverTimezone=IST", "root",
					"Aa123456");
			ret = ret + ("SQL connection succeed");
		} catch (SQLException ex) {/* handle any errors */

			ret = ret + ("SQLException: " + ex.getMessage());
			ret = ret + ("\nSQLState: " + ex.getSQLState());
			ret = ret + ("\nVendorError: " + ex.getErrorCode());
		}
		return ret;

	}

	/**
	 * This method is getting information to change in the DB
	 * 
	 * @param id       - subscriber ID (PK)
	 * @param phonenum - Phone number to change to the subscriber
	 * @return True if success to save the changes
	 */
	public static boolean updatephone(int id, String phonenum) {
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("UPDATE subscriber SET subscriber_phone_number = \"" + phonenum
					+ "\" WHERE  subscriber_id = \"" + id + "\"");
			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * This method is getting information to change in the DB
	 * 
	 * @param id    - subscriber ID (PK)
	 * @param email - Email address to change to the subscriber
	 * @return True if success to save the changes
	 */
	public static boolean updatemail(int id, String email) {
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(
					"UPDATE subscriber SET subscriber_email = \"" + email + "\" WHERE  subscriber_id = \"" + id + "\"");
			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * This method is getting ID and returning String of his data
	 * 
	 * @param idtoload - ID that client ask his details
	 * @return String of this subscriber
	 */
	public static String Load(int idtoload) {
		String query = "SELECT * FROM subscriber WHERE  subscriber_id = \"" + idtoload + "\"";
		String subscriberData = new String("Empty");
		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
			while (rs.next()) {
				int id = rs.getInt("subscriber_id");
				String name = rs.getString("subscriber_name");
				int history = rs.getInt("detailed_subscription_history");
				String phoneNumber = rs.getString("subscriber_phone_number");
				String email = rs.getString("subscriber_email");

				// Create a formatted string with the subscriber's information
				subscriberData = id + ", " + name + ", " + history + ", " + phoneNumber + ", " + email;
				return subscriberData;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return subscriberData;

	}

	/**
	 * This method is returning the list of subscribers from the DB to the client
	 * 
	 * @return List of the subscribers
	 */
	public static List<String> GetSubscriberTable() {
		List<String> subscribers = new ArrayList<>();
		String query = "SELECT * FROM subscriber";

		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
			while (rs.next()) {
				int id = rs.getInt("subscriber_id");
				String name = rs.getString("subscriber_name");
				int history = rs.getInt("detailed_subscription_history");
				String phoneNumber = rs.getString("subscriber_phone_number");
				String email = rs.getString("subscriber_email");

				// Create a formatted string with the subscriber's information
				String subscriberData = id + ", " + name + ", " + history + ", " + phoneNumber + ", " + email;

				// Add the formatted string to the list
				subscribers.add(subscriberData);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return subscribers;
	}

	//////////////////////////////////////////////////////////////////////////////////////////
	///////////////////// --- Einavs books & bookCopy Entity
	////////////////////////////////////////////////////////////////////////////////////////// section---///////////////////////

	/**
	 * Author: Einav This method is getting all information to create new Book in DB
	 * 
	 * @param barcode
	 * @param title
	 * @param subject
	 * @param description
	 * @param allCopies
	 * @param availableCopies
	 * @param ordersNumber
	 * @param lostNumber
	 * @param location
	 * @return
	 */
	public static boolean addNewBookToDB(String barcode, String title, String subject, String description,
			int allCopies, int availableCopies, int ordersNumber, int lostNumber, String location) {
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("INSERT INTO books VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

			stmt.setString(1, barcode);
			stmt.setString(2, title);
			stmt.setString(3, subject);
			stmt.setString(4, description);
			stmt.setInt(5, allCopies);
			stmt.setInt(6, availableCopies);
			stmt.setInt(7, ordersNumber);
			stmt.setInt(8, lostNumber);
			stmt.setString(9, location);

			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Author: Einav This method is getting all information to create new BookCopy
	 * in DB
	 * 
	 * @param barcode
	 * @param copyNo
	 * @param isAvailable
	 * @param isLost
	 * @param returnDate
	 * @param subscriberID
	 * @return
	 */
	public static boolean addNewBookCopyToDB(String barcode, int copyNo, int isAvailable, int isLost, Date returnDate,
			int subscriberID) {
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("INSERT INTO bookcopy VALUES (?, ?, ?, ?, ?, ?)");

			stmt.setString(1, barcode);
			stmt.setInt(2, copyNo);
			stmt.setInt(3, isAvailable);
			stmt.setInt(4, isLost);
			stmt.setDate(5, returnDate);
			stmt.setInt(6, subscriberID);

			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Author: Einav This method is returning book string by his barcode.
	 * 
	 * @param barcode
	 * @return String of the requested book
	 */
	public static String GetBookFromDB(String barcode) {
		String query = "SELECT * FROM books WHERE book_barcode = ?";
		String bookData = "Empty";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, barcode);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					String book_barcode = rs.getString("book_barcode");
					String book_title = rs.getString("book_title");
					String book_subject = rs.getString("book_subject");
					String book_description = rs.getString("book_description");
					int book_allCopies = rs.getInt("book_allCopies");
					int book_availableCopies = rs.getInt("book_availableCopies");
					int book_ordersNumber = rs.getInt("book_ordersNumber");
					int book_lostNumber = rs.getInt("book_lostNumber");
					String book_location = rs.getString("book_location");

					bookData = book_barcode + ", " + book_title + ", " + book_subject + ", " + book_description + ", "
							+ book_allCopies + ", " + book_availableCopies + ", " + book_ordersNumber + ", "
							+ book_lostNumber + ", " + book_location;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return bookData;
	}

	/**
	 * Author: Einav This method is returning bookCopy string by his barcode and
	 * copyNo.
	 * 
	 * @param barcode
	 * @param copyNo
	 * @return String of the requested book
	 */
	public static String GetBookCopyFromDB(String barcode, int copyNo) {
		String query = "SELECT * FROM BookCopy WHERE book_barcode = ? AND bookcopy_copyNo = ?";
		String bookData = "Empty";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, barcode);
			stmt.setInt(2, copyNo);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					String book_barcode = rs.getString("book_barcode");
					String book_title = rs.getString("book_title");
					String book_subject = rs.getString("book_subject");
					String book_description = rs.getString("book_description");
					int book_allCopies = rs.getInt("book_allCopies");
					int book_availableCopies = rs.getInt("book_availableCopies");
					int book_ordersNumber = rs.getInt("book_ordersNumber");
					int book_lostNumber = rs.getInt("book_lostNumber");
					String book_location = rs.getString("book_location");
					bookData = book_barcode + ", " + book_title + ", " + book_subject + ", " + book_description + ", "
							+ book_allCopies + ", " + book_availableCopies + ", " + book_ordersNumber + ", "
							+ book_lostNumber + ", " + book_location;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return bookData;
	}

	/**
	 * Author: Einav this method is updating the book details if we need to change.
	 * 
	 * @param barcode
	 * @param title
	 * @param subject
	 * @param description
	 * @param allCopies
	 * @param availableCopies
	 * @param ordersNumber
	 * @param lostNumber
	 * @param location
	 * @return
	 */
	public static boolean updateBookDetails(String barcode, String title, String subject, String description,
			int allCopies, int availableCopies, int ordersNumber, int lostNumber, String location) {

		String query = "UPDATE books SET book_title = ?, book_subject = ?, book_description = ?, "
				+ "book_allCopies = ?, book_availableCopies = ?, book_ordersNumber = ?, "
				+ "book_lostNumber = ?, book_location = ? WHERE book_barcode = ?";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, title);
			stmt.setString(2, subject);
			stmt.setString(3, description);
			stmt.setInt(4, allCopies);
			stmt.setInt(5, availableCopies);
			stmt.setInt(6, ordersNumber);
			stmt.setInt(7, lostNumber);
			stmt.setString(8, location);
			stmt.setString(9, barcode);

			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Author: Einav this method is updating the bookCopy details if we need to
	 * change.
	 * 
	 * @param barcode
	 * @param copyNo
	 * @param isAvailable
	 * @param isLost
	 * @param returnDate
	 * @param subscriberID
	 * @return
	 */
	public static boolean updateBookCopyDetails(String barcode, int copyNo, int isAvailable, int isLost,
			Date returnDate, int subscriberID) {
		String query = "UPDATE bookcopy SET bookCopy_isAvailable = ?, bookCopy_isLost = ?, bookcopy_returnDate = ?, "
				+ "subscriber_id = ? WHERE book_barcode = ? AND bookcopy_copyNo = ?";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, isAvailable);
			stmt.setInt(2, isLost);
			stmt.setDate(3, returnDate);
			stmt.setInt(4, subscriberID);
			stmt.setString(5, barcode);
			stmt.setInt(6, copyNo);

			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	/////////////////////// END //////////////////////////////////
	///////////////////// --- Einavs books & bookCopy Entity section
	/////////////////////// ---///////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////

	//////////////////////////////////////////////////////////////////////////////////////////
	///////////////////// --- Avishag Subscriber Entity
	////////////////////////////////////////////////////////////////////////////////////////// section---///////////////////////

	/**
	 * Author: Avishag. This method adds a new subscriber to the database.
	 * 
	 * @param id                  - subscriber ID (PK)
	 * @param name                - name of the subscriber
	 * @param subscriptionDetails - history of subscriber
	 * @param phoneNumber         - phone number of the subscriber
	 * @param email               - Email address of the subscriber
	 * @param password            - password of the subscriber
	 * @param status              - status of the subscriber
	 * @return True if success to add the subscriber
	 */
	public static boolean addNewSubscriber(int id, String name, int subscriptionDetails, String phoneNumber,
			String email, String password, String status) {
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("INSERT INTO subscriber VALUES (?, ?, ?, ?, ?, ?, ?)");

			stmt.setInt(1, id);
			stmt.setString(2, name);
			stmt.setInt(3, subscriptionDetails);
			stmt.setString(4, phoneNumber);
			stmt.setString(5, email);
			stmt.setString(6, password);
			stmt.setString(7, status);

			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Author: Avishag. This method is getting information to change in the DB
	 * 
	 * @param id                    - subscriber ID (PK)
	 * @param name                  - new name to change to the subscriber
	 * @param subscripption_details - history of subscriber
	 * @param phoneNumber           - phoneNumber to change to the subscriber
	 * @param email                 - Email address to change to the subscriber
	 * @param password              - password to change to the subscriber
	 * @param status                - status to change to the subscriber
	 * @return True if success to save the changes
	 */
	public static boolean updateSubscriverDetails(int id, String name, int subscripption_details, String phoneNumber,
			String email, String password, String status) {
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(
					"UPDATE subscriber SET subscriber_name = \"" + name + "\", detailed_subscription_history = \""
							+ subscripption_details + "\", subscriber_phonenumber = \"" + phoneNumber
							+ "\", subscriber_email = \"" + email + "\", subscriber_password = \"" + password
							+ "\", subscriber_status = \"" + status + "\" WHERE subscriber_id = \"" + id + "\"");
			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Author: Avishag. This method is getting ID and returning String of his data
	 * 
	 * @param idtoload - ID that client ask his details
	 * @return String of this subscriber
	 */
	public static String getSubscriberDetails(int idtoload) {
		String query = "SELECT * FROM subscriber WHERE  subscriber_id = \"" + idtoload + "\"";
		String subscriberData = new String("Empty");
		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
			while (rs.next()) {
				int id = rs.getInt("subscriber_id");
				String name = rs.getString("subscriber_name");
				int subscriptionDetails = rs.getInt("subscription_details");
				String phoneNumber = rs.getString("subscriber_phone_number");
				String email = rs.getString("subscriber_email");
				String password = rs.getString("password");
				String status = rs.getString("status");

				// Create a formatted string with the subscriber's information
				subscriberData = id + ", " + name + ", " + subscriptionDetails + ", " + phoneNumber + ", " + email
						+ ", " + password + ", " + status;
				return subscriberData;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return subscriberData;

	}

	/////////////////////// END //////////////////////////////////
	///////////////////// --- Avishag Subscriber Entity section
	/////////////////////// ---///////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////

	//////////////////////////////////////////////////////////////////////////////////////////
	///////////////////// --- Yuval librarian Entity
	////////////////////////////////////////////////////////////////////////////////////////// section---///////////////////////

	/**
	 * Author: Yuval. This method adds a new librarian to the database.
	 * 
	 * @param librarian_id       - librarian ID (PK)
	 * @param librarian_name     - name of the librarian
	 * @param librarian_password - password of librarian
	 * @return True if success to add the librarian
	 */
	public static boolean addNewLibrarian(int librarian_id, String librarian_name, String librarian_password) {
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("INSERT INTO librarian VALUES (?, ?, ?)");

			stmt.setInt(1, librarian_id);
			stmt.setString(2, librarian_name);
			stmt.setString(3, librarian_password);

			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Author: Yuval. This method is getting information to change in the DB
	 * 
	 * @param librarian_id       - librarian ID (PK)
	 * @param librarian_name     - name of the librarian
	 * @param librarian_password - password of librarian
	 * @return True if success to save the changes
	 */
	public static boolean updateLibrarianDetails(int librarian_id, String librarian_name, String librarian_password) {
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(
					"UPDATE librarian SET librarian_name = \"" + librarian_name + "\", librarian_password = \""
							+ librarian_password + "\" WHERE subscriber_id = \"" + librarian_id + "\"");
			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Author: Yuval. This method is getting Librarian ID and returning String of
	 * his data
	 * 
	 * @param libidtoload - Librarian ID that client ask his details
	 * @return String of this Librarian
	 */
	public static String getLibrarianDetails(int libidtoload) {
		String query = "SELECT * FROM librarian WHERE  librarian_id = \"" + libidtoload + "\"";
		String librarianData = new String("Empty");
		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
			while (rs.next()) {
				int librarian_id = rs.getInt("librarian_id");
				String librarian_name = rs.getString("librarian_name");
				String librarian_password = rs.getString("librarian_password");

				// Create a formatted string with the subscriber's information
				librarianData = librarian_id + ", " + librarian_name + ", " + librarian_password;
				return librarianData;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return librarianData;

	}

	/////////////////////// END //////////////////////////////////
	///////////////////// --- Yuval librarian Entity section
	/////////////////////// ---///////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////

	//////////////////////////////////////////////////////////////////////////////////////////
	///////////////////// --- Matan Borrow Entity section---///////////////////////

	/**
	 * Author: Matan send request to DB to create new borrowed record
	 * 
	 * @param borrow_number
	 * @param subscriber_id
	 * @param book_barcode
	 * @param book_title
	 * @param bookcopy_copyNo
	 * @param borrow_date
	 * @param borrow_expectReturnDate
	 * @param borrow_actualReturnDate
	 * @param librarian_id
	 * @param librarian_name
	 * @param borrow_lostBook
	 * @return
	 */
	public static int createNewBorrowedRecord(int subscriber_id, String book_barcode, String book_title,
			int bookcopy_copyNo, Date borrow_date, Date borrow_expectReturnDate, Date borrow_actualReturnDate,
			int librarian_id, String librarian_name, int borrow_lostBook) {
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("INSERT INTO borrowedrecords VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
					PreparedStatement.RETURN_GENERATED_KEYS);

			stmt.setInt(1, subscriber_id);

			stmt.setString(2, book_barcode);
			stmt.setString(3, book_title);
			stmt.setInt(4, bookcopy_copyNo);

			stmt.setDate(5, borrow_date);
			stmt.setDate(6, borrow_actualReturnDate);
			stmt.setDate(7, borrow_expectReturnDate);

			stmt.setInt(8, librarian_id);
			stmt.setString(9, librarian_name);

			stmt.setInt(10, borrow_lostBook);

			int CreatedborrowNumber = stmt.executeUpdate();
			return CreatedborrowNumber;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * Author: Matan return borrowedRecord string by borrowNumber from DB
	 * 
	 * @param borrowNumber
	 * @return
	 */
	public static String getBorrowedRecordFromDB(int borrowNumber) {

		String query = "SELECT * FROM borrowedrecords WHERE borrow_number = ?";
		String borrowedRecords = "Empty";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, borrowNumber);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int borrow_number = rs.getInt("borrow_number");
					int subscriber_id = rs.getInt("subscriber_id");
					String book_barcode = rs.getString("book_barcode");
					String book_title = rs.getString("book_title");
					int bookcopy_copyNo = rs.getInt("bookcopy_copyNo");
					String borrow_date = rs.getString("borrow_date");
					String borrow_expectReturnDate = rs.getString("borrow_expectReturnDate");
					String borrow_actualReturnDate = rs.getString("borrow_actualReturnDate");
					int librarian_id = rs.getInt("librarian_id");
					String librarian_name = rs.getString("librarian_name");
					int borrow_lostBook = rs.getInt("borrow_lostBook");

					// Create a formatted string with the subscriber's information
					borrowedRecords = borrow_number + ", " + subscriber_id + ", " + book_barcode + ", " + book_title
							+ ", " + bookcopy_copyNo + ", " + borrow_date + ", " + borrow_expectReturnDate + ", "
							+ borrow_actualReturnDate + ", " + librarian_id + ", " + librarian_name + ", "
							+ borrow_lostBook;

				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return borrowedRecords;

	}

	/**
	 * Author: Matan update borrow return date and if the book lost
	 * 
	 * @param borrow_number
	 * @param borrow_expectReturnDate
	 * @param borrow_actualReturnDate
	 * @param borrow_lostBook
	 * @return
	 */
	public static boolean UpdateBorrowedRecordReturnTime(int borrow_number, Date borrow_expectReturnDate,
			Date borrow_actualReturnDate, int borrow_lostBook) {
		String query = "UPDATE borrowedrecords SET borrow_expectReturnDate = ?, borrow_actualReturnDate = ?, borrow_lostBook = ?"
				+ "WHERE  borrow_number = ?";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setDate(1, borrow_expectReturnDate);
			stmt.setDate(2, borrow_actualReturnDate);
			stmt.setInt(3, borrow_lostBook);
			stmt.setInt(4, borrow_number);

			stmt.executeUpdate();

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/////////////////////// END //////////////////////////////////
	///////////////////// --- Matan Borrow Entity section ---///////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////

	//////////////////////////////////////////////////////////////////////////////////////////
	///////////////////// --- Amir LogActivity Entity
	////////////////////////////////////////////////////////////////////////////////////////// section---///////////////////////

	/* ADDED BY AMIR */
	public static List<String> LoadLogActivitybyid(int idtoload) {
		// SQL query to fetch all rows from LogActivity table for the given
		// subscriber_id
		String query = "SELECT * FROM activitylog WHERE subscriber_id = ?";

		List<String> subscriberDataList = new ArrayList<>();

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			// Set the value for the placeholder (?) in the query
			stmt.setInt(1, idtoload);

			// Execute the query and get the result set
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					int serialid = rs.getInt("activity_serial");
					int id = rs.getInt("subscriber_id");
					String activityaction = rs.getString("activity_action");
					String bookbarcode = rs.getString("book_barcode");
					String booktitle = rs.getString("book_title");
					int copynumber = rs.getInt("bookcopy_copyNo");
					Date date = rs.getDate("activity_date");

					// Create a formatted string with the retrieved data
					String subscriberData = serialid + ", " + id + ", " + activityaction + ", " + bookbarcode + ", "
							+ booktitle + ", " + copynumber + ", " + date;

					// Add the formatted string to the list
					subscriberDataList.add(subscriberData);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Return the list of formatted strings
		return subscriberDataList;
	}

	/* ADDED BY AMIR */
	public static String LoadLogActivityBySerialId(int serialIdToLoad) {
		// SQL query to fetch the row with the given serial ID
		String query = "SELECT * FROM activitylog WHERE activity_serial = ?";

		// Variable to store the resulting data
		String subscriberData = "Record not found";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			// Set the value for the placeholder (?) in the query
			stmt.setInt(1, serialIdToLoad);

			// Execute the query and get the result set
			try (ResultSet rs = stmt.executeQuery()) {
				// Check if a row exists
				if (rs.next()) {
					// Retrieve column values from the row
					int serialid = rs.getInt("activity_serial");
					int id = rs.getInt("subscriber_id");
					String activityaction = rs.getString("activity_action");
					int bookbarcode = rs.getInt("book_barcode");
					String booktitle = rs.getString("book_title");
					String copynumber = rs.getString("bookcopy_copyNo");
					Date date = rs.getDate("activity_date");

					// Create a formatted string with the retrieved data
					subscriberData = serialid + ", " + id + ", " + activityaction + ", " + bookbarcode + ", "
							+ booktitle + ", " + copynumber + ", " + date;
				}
			}
		} catch (SQLException e) {
			// Print the stack trace if an SQL exception occurs
			e.printStackTrace();
		}

		return subscriberData;
	}

	/* ADDED BY AMIR */
	public static int AddNewLogActivity(int subscriberId, String activityAction, String bookBarcode, String bookTitle,
			Integer bookcopyCopyNo, Date activityDate) {
		String query = "INSERT INTO activitylog VALUES (?, ?, ?, ?, ?, ?)";

			PreparedStatement stmt;
			try {
			stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
			
			stmt.setInt(1, subscriberId);
			stmt.setString(2, activityAction);
			stmt.setString(3, bookBarcode);
			stmt.setString(4, bookTitle);
			stmt.setObject(5, bookcopyCopyNo);
			stmt.setDate(6, activityDate);

			int CreatedActivityLog = stmt.executeUpdate();
			return CreatedActivityLog;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/////////////////////// END //////////////////////////////////
	///////////////////// --- Amir LogActivity Entity section
	/////////////////////// ---///////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////

	//////////////////////////////////////////////////////////////////////////////////////////
	///////////////////// --- Chen Orders Entity section---///////////////////////

	
	
	
	/////////////////////// END //////////////////////////////////
	///////////////////// --- Chen Orders Entity section ---///////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////

}
