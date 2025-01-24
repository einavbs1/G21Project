package queries;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	 * @param phoneNumber         - phone number of the subscriber
	 * @param email               - Email address of the subscriber
	 * @param password            - password of the subscriber
	 * @param status              - status of the subscriber
	 * @return True if success to add the subscriber
	 */
	public static boolean addNewSubscriber(int id, String name, String phoneNumber,
			String email, String password, String status) {
		PreparedStatement stmt;
		try {
			stmt = mysqlConnection.conn.prepareStatement("INSERT INTO subscriber VALUES (?, ?, ?, ?, ?, ?)");

			stmt.setInt(1, id);
			stmt.setString(2, name);
			stmt.setString(3, phoneNumber);
			stmt.setString(4, email);
			stmt.setString(5, password);
			stmt.setString(6, status);

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
	 * @param phoneNumber           - phoneNumber to change to the subscriber
	 * @param email                 - Email address to change to the subscriber
	 * @param password              - password to change to the subscriber
	 * @param status                - status to change to the subscriber
	 * @return True if success to save the changes
	 */
	public static boolean updateSubscriverDetails(int id, String name, String phoneNumber, String email,
			String password, String status) {

		String query = "UPDATE subscriber SET subscriber_name = ?, subscriber_phonenumber = ?"
				+ ", subscriber_email = ?, subscriber_password = ?" + ", subscriber_status = ? WHERE subscriber_id = ?";
		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
			stmt.setString(1, name);
			stmt.setString(2, phoneNumber);
			stmt.setString(3, email);
			stmt.setString(4, password);
			stmt.setString(5, status);
			stmt.setInt(6, id);

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

		String query = "SELECT * FROM subscriber WHERE subscriber_id = ?";
		String subscriberData = "Empty";
		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
			stmt.setInt(1, idtoload);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int id = rs.getInt("subscriber_id");
					String name = rs.getString("subscriber_name");
					String phoneNumber = rs.getString("subscriber_phonenumber");
					String email = rs.getString("subscriber_email");
					String password = rs.getString("subscriber_password");
					String status = rs.getString("subscriber_status");

					// Create a formatted string with the subscriber's information
					subscriberData = id + ", " + name + ", " + phoneNumber + ", " + email + ", " + password + ", "
							+ status;
					return subscriberData;
				}
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
	            String phoneNumber = rs.getString("subscriber_phonenumber");
	            String email = rs.getString("subscriber_email");
	            String password = rs.getString("subscriber_password");
	            String status = rs.getString("subscriber_status"); // Updated field

	            // Create a formatted string with the subscriber's information
	            String subscriberData = id + ", " + name + ", " + phoneNumber + ", " + email + ", " + password + ", " + status;

	            // Add the formatted string to the list
	            subscribers.add(subscriberData);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return subscribers;
	}
	
	
	public static Map<String, Integer> GetSubscriberStatusCount() {
	    Map<String, Integer> statusCounts = new HashMap<>();
	    statusCounts.put("Active", 0);
	    statusCounts.put("Frozen", 0);

	    String query = "SELECT subscriber_status, COUNT(*) AS count FROM subscriber GROUP BY subscriber_status";

	    try (Statement stmt = mysqlConnection.conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
	        while (rs.next()) {
	            String status = rs.getString("subscriber_status");
	            int count = rs.getInt("count");

	            // Update the count in the map if the status is Active or Frozen
	            if (statusCounts.containsKey(status)) {
	                statusCounts.put(status, count);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return statusCounts;
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

