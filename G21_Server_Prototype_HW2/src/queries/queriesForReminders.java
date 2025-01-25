package queries;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import Server.mysqlConnection;

public class queriesForReminders {


	//////////////////////////////////////////////////////////////////////////////////////////
	///////////////////// --- Einav Reminders Entity  section---///////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////


	public static int addNewReminderToDB(String message, int subID, String subPhone, String subEmail, Date date, int actionNum) {
		PreparedStatement stmt;
		try {
			stmt = mysqlConnection.conn.prepareStatement("INSERT INTO reminders VALUES (?, ?, ?, ?, ?)",
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
					
					ReminderData = reminder_serial + ", " + reminder_message + ", " + subscriber_id + ", " + subscriber_phonenumber + ", "
							+ subscriber_email+ ", " + reminder_date;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ReminderData;
	}
	
	
	public static List<String> GetNewOldReminders(Date fromThisDate, int subID) {
	    List<String> notifications = new ArrayList<>();
	    Date today = Date.valueOf(LocalDate.now());
	    String query = "SELECT *, CASE WHEN reminder_dateWillSend >= ? AND reminder_dateWillSend =< ? THEN 'new' ELSE 'old' END AS reminder_type "
	                 + "FROM reminders WHERE subscriber_id = ? "  // Add the condition for subscriber_id
	                 + "ORDER BY reminder_date DESC";

	    try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
	        stmt.setDate(1, fromThisDate);
	        stmt.setDate(2, today);
	        stmt.setInt(3, subID);
	        
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                String type = rs.getString("reminder_type");
	                int serialNo = rs.getInt("reminder_serial");
	                String message = rs.getString("reminder_message");
	                String phone = rs.getString("subscriber_phonenumber");
	                String email = rs.getString("subscriber_email");
	                Date date = rs.getDate("reminder_dateWillSend");
	                
	                notifications.add(type + ", " + serialNo + ", " + message + ", " + phone + ", " + email + ", " + date);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return notifications;
	}

	/////////////////////// END //////////////////////////////////
	///////////////////// --- Einav Reminders Entity section ---///////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////

	
}
