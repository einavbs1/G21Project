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

/////////////////////// END //////////////////////////////////
///////////////////// --- Avishag Subscriber Entity section
/////////////////////// ---///////////////////////
//////////////////////////////////////////////////////////////////////////////////////////


	
	
/////////////////////// start amir 19.01.2025 //////////////////////////////////

	
	/**
	 * Gets monthly subscriber status statistics
	 * @return List of subscribers with their current status details
	 */
	public static List<String> getMonthlySubscriberStats() {
	    ArrayList<String> subscriberStats = new ArrayList<>();
	    
	    String query = "SELECT DISTINCT s.subscriber_id, s.subscriber_name, s.status " +
	                  "FROM subscriber s " +
	                  "ORDER BY s.subscriber_id";

	    try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query);
	         ResultSet rs = stmt.executeQuery()) {
	        
	        while (rs.next()) {
	            int subscriber_id = rs.getInt("subscriber_id");
	            String subscriber_name = rs.getString("subscriber_name");
	            String status = rs.getString("status");
	            
	            String subscriberRecord = String.format("%d, %s, %s",
	                subscriber_id,
	                subscriber_name,
	                status
	            );
	            
	            subscriberStats.add(subscriberRecord);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return subscriberStats;
	}
}


/////////////////////// END amir 19.01.2025 //////////////////////////////////

