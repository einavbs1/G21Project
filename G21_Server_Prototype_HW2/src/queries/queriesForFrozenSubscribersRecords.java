package queries;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Server.mysqlConnection;

public class queriesForFrozenSubscribersRecords {

//////////////////////////////////////////////////////////////////////////////////////////
///////////////////// --- Einav Notifications Entity  section---///////////////////////
//////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////

	public static int addNewFrozenRecordToDB(int SubscriberID, Date Begin, Date End) {
		PreparedStatement stmt;
		int generatedKey = -1;
		try {
			stmt = mysqlConnection.conn.prepareStatement("INSERT INTO FrozenSubscribersRecords (subscriber_id, frozen_beginDate, frozen_untilDate) VALUES (?, ?, ?)",
					PreparedStatement.RETURN_GENERATED_KEYS);

			stmt.setInt(1, SubscriberID);
			stmt.setDate(2, Begin);
			stmt.setDate(3, End);

			// Retrieve the generated key
			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					generatedKey = generatedKeys.getInt(1);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return generatedKey;
	}
	
	
	public static boolean updateRecordOfFrozen(int serial, Date Begin, Date End) {

			String query = "UPDATE FrozenSubscribersRecords SET frozen_beginDate = ?, frozen_untilDate = ?"
					+ " WHERE frozen_serial = ?";
			try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
				stmt.setInt(1, serial);
				stmt.setDate(2, Begin);
				stmt.setDate(3, End);

				stmt.executeUpdate();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
	}
	
	

	public static String getRecordOfFrozenDetails(int idtoload, Date End) {

		String query = "SELECT * FROM FrozenSubscribersRecords WHERE subscriber_id = ? AND frozen_untilDate = ?";
		String subscriberData = "Empty";
		
		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
			stmt.setInt(1, idtoload);
			stmt.setDate(2, End);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int serial = rs.getInt("frozen_serial");
					int id = rs.getInt("subscriber_id");
					Date beginDate = rs.getDate("frozen_beginDate");
					Date untilDate = rs.getDate("frozen_untilDate");

					// Create a formatted string with the subscriber's information
					subscriberData = serial + ", " + id + ", " + beginDate + ", " + untilDate;
					return subscriberData;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return subscriberData;

	}
	

	public static List<String> GetFrozenReportForSubscriber(int SubscriberID, int month1, int year1) {
		String query = "SELECT * FROM frozensubscribersrecords"
					+ " WHERE subscriber_id = ? AND "
					+ "((MONTH(frozen_beginDate) = ? AND YEAR(frozen_beginDate) = ?) OR "
					+ "(MONTH(frozen_untilDate) = ? AND YEAR(frozen_untilDate) = ?))";
		List<String> frozenSubscriberList = new ArrayList<>();
		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
			stmt.setInt(1, SubscriberID);
			stmt.setInt(2, month1);
			stmt.setInt(3, year1);
			stmt.setInt(4, month1);
			stmt.setInt(5, year1);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					int serial = rs.getInt("frozen_serial");
					int subID = rs.getInt("subscriber_id");
					Date beginDate = rs.getDate("frozen_beginDate");
					Date endDate = rs.getDate("frozen_untilDate");
					
					String statusReportData = serial + ", " + subID + ", " + beginDate + ", " + endDate;
					frozenSubscriberList.add(statusReportData);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return frozenSubscriberList;
	}
	
	
	

/////////////////////// END //////////////////////////////////
///////////////////// --- Einav Notifications Entity section
/////////////////////// ---///////////////////////
//////////////////////////////////////////////////////////////////////////////////////////

}
