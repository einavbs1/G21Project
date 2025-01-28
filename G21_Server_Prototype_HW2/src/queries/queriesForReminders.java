package queries;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import Server.mysqlConnection;


/**
 * Handles operations related to reminders in the database, including creation, retrieval, and updates.
 */
public class queriesForReminders {


	 /**
     * Adds a new reminder to the database.
     *
     * @param message The reminder message.
     * @param subID   The subscriber ID.
     * @param subPhone The subscriber phone number.
     * @param subEmail The subscriber email.
     * @param date    The date the reminder will be sent.
     * @return The generated reminder serial or -1 if failed.
     */
	public static int addNewReminderToDB(String message, int subID, String subPhone, String subEmail, Date date) {
		PreparedStatement stmt;
		try {
			stmt = mysqlConnection.conn.prepareStatement(
					"INSERT INTO reminders (reminder_message, subscriber_id, subscriber_phonenumber, subscriber_email, reminder_dateWillSend) VALUES (?, ?, ?, ?, ?)",
					PreparedStatement.RETURN_GENERATED_KEYS);

			stmt.setString(1, message);
			stmt.setInt(2, subID);
			stmt.setString(3, subPhone);
			stmt.setString(4, subEmail);
			stmt.setDate(5, date);

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
     * Retrieves a reminder by its serial number.
     *
     * @param serial The serial number of the reminder.
     * @return The reminder details as a string or "Empty" if not found.
     */
	public static String GetReminder(int serial) {
		String query = "SELECT * FROM reminders WHERE reminder_serial = ? ";
		String ReminderData = "Empty";
		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
			stmt.setInt(1, serial);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int reminder_serial = rs.getInt("reminder_serial");
					String reminder_message = rs.getString("reminder_message");
					int subscriber_id = rs.getInt("subscriber_id");
					String subscriber_phonenumber = rs.getString("subscriber_phonenumber");
					String subscriber_email = rs.getString("subscriber_email");
					Date reminder_date = rs.getDate("reminder_dateWillSend");

					ReminderData = reminder_serial + ", " + reminder_message + ", " + subscriber_id + ", "
							+ subscriber_phonenumber + ", " + subscriber_email + ", " + reminder_date;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ReminderData;
	}

	/**
     * Updates the details of an existing reminder in the database.
     *
     * @param serial   The serial number of the reminder.
     * @param message  The reminder message.
     * @param subID    The subscriber ID.
     * @param phone    The subscriber phone number.
     * @param email    The subscriber email.
     * @param datetosend The date the reminder will be sent.
     * @return True if the update was successful, false otherwise.
     */
	public static boolean updateReminderDetails(int serial, String message, int subID, String phone, String email,
			Date datetosend) {

		String query = "UPDATE reminders SET reminder_message = ?, subscriber_id = ?"
				+ ", subscriber_phonenumber = ?, subscriber_email = ?, reminder_dateWillSend = ?"
				+ " WHERE reminder_serial = ?";
		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
			stmt.setString(1, message);
			stmt.setInt(2, subID);
			stmt.setString(3, phone);
			stmt.setString(4, email);
			stmt.setDate(5, datetosend);

			stmt.setInt(6, serial);

			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	
	/**
     * Retrieves reminders for a specific subscriber, classified as new, old, or not sent yet.
     *
     * @param fromThisDate The date to classify reminders.
     * @param subID        The subscriber ID.
     * @return A list of reminders classified as "new", "old", or "not_sent_yet".
     */
	public static List<String> GetNewOldReminders(Date fromThisDate, int subID) {
		List<String> notifications = new ArrayList<>();
		Date today = Date.valueOf(LocalDate.now());
		String query = "SELECT *, CASE WHEN reminder_dateWillSend >= ? AND reminder_dateWillSend <= ? THEN 'new' "
				+ "WHEN reminder_dateWillSend > ? THEN 'not_sent_yet' "
				+ "ELSE 'old' END AS reminder_type "
				+ "FROM reminders WHERE subscriber_id = ? " // Add the condition for subscriber_id
				+ "ORDER BY reminder_dateWillSend DESC";

		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
			stmt.setDate(1, fromThisDate);
			stmt.setDate(2, today);
			stmt.setDate(3, today);
			stmt.setInt(4, subID);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					String type = rs.getString("reminder_type");
					int serialNo = rs.getInt("reminder_serial");
					String message = rs.getString("reminder_message");
					String phone = rs.getString("subscriber_phonenumber");
					String email = rs.getString("subscriber_email");
					Date date = rs.getDate("reminder_dateWillSend");

					notifications
							.add(type + ", " + serialNo + ", " + message + ", " + phone + ", " + email + ", " + date);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return notifications;
	}


}
