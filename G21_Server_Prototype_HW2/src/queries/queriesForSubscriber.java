package queries;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

/////////////////////// END //////////////////////////////////
///////////////////// --- Avishag Subscriber Entity section
/////////////////////// ---///////////////////////
//////////////////////////////////////////////////////////////////////////////////////////

}
