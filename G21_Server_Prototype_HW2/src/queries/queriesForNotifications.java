package queries;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Server.mysqlConnection;



/**
 * Handles notification management, including adding, retrieving, and classifying notifications.
 */
public class queriesForNotifications {


	/**
     * Adds a new notification to the database.
     *
     * @param message   The notification message.
     * @param subID     The subscriber ID.
     * @param date      The notification date.
     * @param borrowNum The related borrow number.
     * @return The generated notification serial or -1 on failure.
     */
	public static int addNewNotificationToDB(String message, int subID, Date date, int borrowNum) {
		PreparedStatement stmt;
		try {
			stmt = mysqlConnection.conn.prepareStatement("INSERT INTO notifications (notification_message, subscriber_id, notification_date, borrow_number) VALUES (?, ?, ?, ?)",
					PreparedStatement.RETURN_GENERATED_KEYS);

			stmt.setString(1, message);
			stmt.setInt(2, subID);
			stmt.setDate(3, date);
			stmt.setInt(4, borrowNum);

			stmt.executeUpdate();

			int generatedKey = -1;
			// Retrieve the generated key
			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					generatedKey = generatedKeys.getInt(1);
				}
			}
			return generatedKey;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	
	 /**
     * Retrieves a specific notification by serial.
     *
     * @param serial The notification serial.
     * @return The notification details as a string or "Empty" if not found.
     */
	public static String GetNotification(int serial) {
		String query = "SELECT * FROM notifications WHERE notification_serial = ? ";
		String NotificationData = "Empty";
		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
			stmt.setInt(1, serial);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int notification_serial = rs.getInt("notification_serial");
					String notification_message = rs.getString("notification_message");
					int subscriber_id = rs.getInt("subscriber_id");
					Date notification_date = rs.getDate("notification_date");
					int borrow_number = rs.getInt("borrow_number");

					NotificationData = notification_serial + ", " + notification_message + ", " + subscriber_id + ", "
							+ notification_date + ", " + borrow_number;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return NotificationData;
	}

	/**
     * Retrieves all notifications from the database.
     *
     * @return A list of all notifications as strings.
     */
	public static List<String> GetAllNotifications() {
		List<String> notifications = new ArrayList<>();
		String query = "SELECT * FROM notifications";

		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				String notificationData = rs.getInt("notification_serial") + ", " + rs.getString("notification_message")
						+ ", " + rs.getInt("subscriber_id") + ", " + rs.getDate("notification_date") + ", "
						+ rs.getInt("borrow_number");

				notifications.add(notificationData);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return notifications;
	}
	
	
	/**
     * Retrieves notifications and classifies them as "new" or "old" based on the given date.
     *
     * @param fromThisDate The date to classify notifications as new or old.
     * @return A list of classified notifications.
     */
	public static List<String> GetNewOldNotifications(Date fromThisDate) {
	    List<String> notifications = new ArrayList<>();
	    String query = "SELECT *, CASE WHEN notification_date >= ? THEN 'new' ELSE 'old' END AS notification_type "
	                 + "FROM notifications "
	                 + "ORDER BY notification_date DESC";

	    try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
	        stmt.setDate(1, fromThisDate);
	        
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                String type = rs.getString("notification_type");
	                int serialNo = rs.getInt("notification_serial");
	                String message = rs.getString("notification_message");
	                int subscriberId = rs.getInt("subscriber_id");
	                Date date = rs.getDate("notification_date");
	                int borrowNum = rs.getInt("borrow_number");
	                
	                notifications.add(type + ", " + serialNo + ", " + message + ", " + subscriberId + ", " + date + ", " + borrowNum);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return notifications;
	}


}
