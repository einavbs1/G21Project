package queries;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Server.mysqlConnection;

public class queriesForNotifications {

	
//////////////////////////////////////////////////////////////////////////////////////////
///////////////////// --- Einav Notifications Entity  section---///////////////////////
//////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////


public static int addNewNotificationToDB(String message, int subID, Date date, int borrowNum) {
	PreparedStatement stmt;
	try {
		stmt = mysqlConnection.conn.prepareStatement("INSERT INTO notifications VALUES (?, ?, ?, ?)",
				PreparedStatement.RETURN_GENERATED_KEYS);

		stmt.setString(1, message);
		stmt.setInt(2, subID);
		stmt.setDate(3, date);
		stmt.setInt(4, borrowNum);

		int CreatedNotificationNum = stmt.executeUpdate();
		return CreatedNotificationNum;
	} catch (SQLException e) {
		e.printStackTrace();
		return -1;
	}
}

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
				
				NotificationData = notification_serial + ", " + notification_message + ", " + subscriber_id + ", " + notification_date + ", "
						+ borrow_number;
			}
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}

	return NotificationData;
}


/////////////////////// END //////////////////////////////////
///////////////////// --- Einav Notifications Entity section
/////////////////////// ---///////////////////////
//////////////////////////////////////////////////////////////////////////////////////////

	
}
