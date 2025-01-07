package Server;

import java.sql.Connection;
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

	
	/** Author: Avishag.
	 * This method adds a new subscriber to the database.
	 * @param id		                - subscriber ID (PK)
	 * @param name		                - name of the subscriber
	 * @param subscriptionDetails		- history of subscriber
	 * @param phoneNumber	        	- phone number of the subscriber 
	 * @param email	                 	- Email address of the subscriber
	 * @param password	            	- password of the subscriber
	 * @param status	             	- status of the subscriber
	 * @return True if success to add the subscriber
	 */
	public static boolean addNewSubscriber(int id, String name, int subscriptionDetails, String phoneNumber, String email, String password, String status) {
	    PreparedStatement stmt;
	    try {
	        stmt = conn.prepareStatement(
	            "INSERT INTO subscriber VALUES (?, ?, ?, ?, ?, ?, ?)");
	        
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
	
	
	/** Author: Yuval.
	 * This method adds a new librarian to the database.
	 * @param librarian_id		    - librarian ID (PK)
	 * @param librarian_name	    - name of the librarian
	 * @param librarian_password	- password of librarian
	 * @return True if success to add the librarian
	 */
	public static boolean addNewLibrarian(int librarian_id, String librarian_name, String librarian_password) {
	    PreparedStatement stmt;
	    try {
	        stmt = conn.prepareStatement(
	            "INSERT INTO librarian VALUES (?, ?, ?)");
	        
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
	
	
	/** Author: Avishag.
	 * This method is getting information to change in the DB 
	 * @param id		                - subscriber ID (PK)
	 * @param name		                - new name to change to the subscriber
	 * @param subscripption_details		- history of subscriber
	 * @param phoneNumber	        	- phoneNumber to change to the subscriber 
	 * @param email	                 	- Email address to change to the subscriber
	 * @param password	            	- password to change to the subscriber
	 * @param status	             	- status to change to the subscriber
	 * @return True if success to save the changes
	 */
	public static boolean updateSubscriverDetails(int id, String name, int subscripption_details, String phoneNumber, String email, String password, String status) {
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(
					"UPDATE subscriber SET subscriber_name = \"" + name + "\", detailed_subscription_history = \"" + subscripption_details + "\", subscriber_phonenumber = \"" + phoneNumber + "\", subscriber_email = \"" + email + "\", subscriber_password = \"" + password + "\", subscriber_status = \"" + status + "\" WHERE subscriber_id = \"" + id + "\"");
			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	/** Author: Avishag.
	 * This method is getting ID and returning String of his data
	 * @param idtoload	- ID that client ask his details
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
	            subscriberData = id + ", " + name + ", " + subscriptionDetails + ", " + phoneNumber + ", " + email + ", " + password + ", " + status;
				return subscriberData;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return subscriberData;

	}
	

	/** Author: Yuval.
	 * This method is getting information to change in the DB 
	 * @param librarian_id		    - librarian ID (PK)
	 * @param librarian_name	    - name of the librarian
	 * @param librarian_password	- password of librarian
	 * @return True if success to save the changes
	 */
	public static boolean updateLibrarianDetails(int librarian_id, String librarian_name, String librarian_password) {
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(
					"UPDATE librarian SET librarian_name = \"" + librarian_name + "\", librarian_password = \"" + librarian_password + "\" WHERE subscriber_id = \"" + librarian_id + "\"");
			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	/** Author: Yuval.
	 * This method is getting Librarian ID and returning String of his data
	 * @param libidtoload	- Librarian ID that client ask his details
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
