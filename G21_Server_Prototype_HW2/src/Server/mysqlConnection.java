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
