package queries;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Server.mysqlConnection;

public class queriesForSubscriber {

//////////////////////////////////////////////////////////////////////////////////////////
///////////////////// --- Avishag Subscriber Entity
////////////////////////////////////////////////////////////////////////////////////////// section---///////////////////////
//////////////////////////////////////////////////////////////////////////////////////////

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
			stmt = mysqlConnection.conn.prepareStatement("INSERT INTO subscriber VALUES (?, ?, ?, ?, ?, ?, ?)");

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
			stmt = mysqlConnection.conn.prepareStatement(
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
		try (Statement stmt = mysqlConnection.conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
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
	
	
	/**Author: Avishag.
	 * This method is returning the list of subscribers from the DB to the client
	 * 
	 * @return List of the subscribers
	 */
	public static List<String> GetSubscriberTable() {
	    List<String> subscribers = new ArrayList<>();
	    String query = "SELECT * FROM subscriber";

	    try (Statement stmt = mysqlConnection.conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
	        while (rs.next()) {
	            int id = rs.getInt("subscriber_id");
	            String name = rs.getString("subscriber_name");
	            int subscriptionDetails = rs.getInt("subscription_details"); // Updated field
	            String phoneNumber = rs.getString("subscriber_phone_number");
	            String email = rs.getString("subscriber_email");
	            String status = rs.getString("status"); // Updated field

	            // Create a formatted string with the subscriber's information
	            String subscriberData = id + ", " + name + ", " + subscriptionDetails + ", " + phoneNumber + ", " + email + ", " + status;

	            // Add the formatted string to the list
	            subscribers.add(subscriberData);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return subscribers;
	}

/////////////////////// END //////////////////////////////////
///////////////////// --- Avishag Subscriber Entity section
/////////////////////// ---///////////////////////
//////////////////////////////////////////////////////////////////////////////////////////

}
