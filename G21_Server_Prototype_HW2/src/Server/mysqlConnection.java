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
		String ret ="";
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
	 * @param id		- subscriber ID (PK)
	 * @param phonenum	- Phone number to change to the subscriber
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
	 * @param id		- subscriber ID (PK)
	 * @param email		- Email address to change to the subscriber
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
	
	
	
	//			Borrowed 		Records		section           //
	
	
	
	
	/**Author: Matan
	 * send request to DB to create new borrowed record
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
	public static boolean createNewBorrowedRecord (int borrow_number, int subscriber_id, String book_barcode, String book_title, int bookcopy_copyNo, Date borrow_date, 
			Date borrow_expectReturnDate, Date borrow_actualReturnDate, int librarian_id, String librarian_name, int borrow_lostBook)
	{
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("INSERT INTO borrowedrecords VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			
			stmt.setString(1, book_barcode);
			stmt.setString(2, book_title);
			stmt.setInt(3, bookcopy_copyNo);
			stmt.setDate(4, borrow_actualReturnDate);
			stmt.setDate(5, borrow_date);
			stmt.setDate(6, borrow_expectReturnDate);
			stmt.setInt(7, borrow_lostBook);
			stmt.setInt(8, borrow_number);
			stmt.setInt(9, librarian_id);
			stmt.setString(10, librarian_name);
			stmt.setInt(11, subscriber_id);
			
			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**Author: Matan
	 * return all borrowedRecords string by borrowNumber from DB 
	 * @param subscriberId
	 * @return
	 */
	public static String getBorrowedRecordFromDB(int borrowNumber) {
		String borrowedRecords = new String();	
		String query = "SELECT * FROM borrowedrecords WHERE borrow_number = \"" + borrowNumber + "\"";
		
		try (PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
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
				String borrowedRecord = borrow_number+", "+subscriber_id+", "+book_barcode+", "+book_title+", "+bookcopy_copyNo+", "
				+borrow_date+", "+borrow_expectReturnDate+", "+borrow_actualReturnDate+", "+librarian_id+", "+librarian_name+", "+borrow_lostBook; 
						
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return borrowedRecords;
	}
	
	
	/**Author: Matan
	 * update borrow return date and if the book lost
	 * @param borrow_number
	 * @param borrow_expectReturnDate
	 * @param borrow_actualReturnDate
	 * @param borrow_lostBook
	 * @return
	 */
	public static boolean UpdateBorrowedRecordReturnTime(int borrow_number, Date borrow_expectReturnDate,Date borrow_actualReturnDate,
			int borrow_lostBook) {
		String query = "UPDATE borrowedrecords SET borrow_expectReturnDate = ?, borrow_actualReturnDate = ?, borrow_lostBook = ?"
				+ "WHERE  borrow_number = ?";
		
		try (PreparedStatement stmt = conn.prepareStatement(query)){
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
	/**
	 * This method is getting ID and returning String of his data
	 * @param idtoload	- ID that client ask his details
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

}
