package queries;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Server.mysqlConnection;

public class queriesForReminders {


	//////////////////////////////////////////////////////////////////////////////////////////
	///////////////////// --- Einav Reminders Entity  section---///////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////


	public static int addNewReminderToDB(String message, int subID, String subPhone, String subEmail, Date date, int actionNum) {
		PreparedStatement stmt;
		try {
			stmt = mysqlConnection.conn.prepareStatement("INSERT INTO reminders VALUES (?, ?, ?, ?, ?, ?)",
					PreparedStatement.RETURN_GENERATED_KEYS);

			stmt.setString(1, message);
			stmt.setInt(2, subID);
			stmt.setString(3, subPhone);
			stmt.setString(4, subEmail);
			stmt.setDate(5, date);
			stmt.setInt(6, actionNum);

			int CreatedReminderNum = stmt.executeUpdate();
			return CreatedReminderNum;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
		}

	
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
					Date reminder_date = rs.getDate("reminder_date");
					int action_number = rs.getInt("action_number");
					
					ReminderData = reminder_serial + ", " + reminder_message + ", " + subscriber_id + ", " + subscriber_phonenumber + ", "
							+ subscriber_email+ ", " + reminder_date + ", " + action_number;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ReminderData;
	}

	/////////////////////// END //////////////////////////////////
	///////////////////// --- Einav Reminders Entity section ---///////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////

	
}
