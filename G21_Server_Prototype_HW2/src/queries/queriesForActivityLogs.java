package queries;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Server.mysqlConnection;

public class queriesForActivityLogs {

//////////////////////////////////////////////////////////////////////////////////////////
///////////////////// --- Amir LogActivity Entity
////////////////////////////////////////////////////////////////////////////////////////// section---///////////////////////

	/* ADDED BY AMIR */
	public static List<String> LoadLogActivitybyid(int idtoload) {
// SQL query to fetch all rows from LogActivity table for the given
// subscriber_id
		String query = "SELECT * FROM activitylog WHERE subscriber_id = ?";

		List<String> subscriberDataList = new ArrayList<>();

		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
// Set the value for the placeholder (?) in the query
			stmt.setInt(1, idtoload);

// Execute the query and get the result set
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					int serialid = rs.getInt("activity_serial");
					int id = rs.getInt("subscriber_id");
					String activityaction = rs.getString("activity_action");
					String bookbarcode = rs.getString("book_barcode");
					String booktitle = rs.getString("book_title");
					int copynumber = rs.getInt("bookcopy_copyNo");
					Date date = rs.getDate("activity_date");

// Create a formatted string with the retrieved data
					String subscriberData = serialid + ", " + id + ", " + activityaction + ", " + bookbarcode + ", "
							+ booktitle + ", " + copynumber + ", " + date;

// Add the formatted string to the list
					subscriberDataList.add(subscriberData);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

// Return the list of formatted strings
		return subscriberDataList;
	}

	/* ADDED BY AMIR */
	public static String LoadLogActivityBySerialId(int serialIdToLoad) {
// SQL query to fetch the row with the given serial ID
		String query = "SELECT * FROM activitylog WHERE activity_serial = ?";

// Variable to store the resulting data
		String subscriberData = "Record not found";

		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
// Set the value for the placeholder (?) in the query
			stmt.setInt(1, serialIdToLoad);

// Execute the query and get the result set
			try (ResultSet rs = stmt.executeQuery()) {
// Check if a row exists
				if (rs.next()) {
// Retrieve column values from the row
					int serialid = rs.getInt("activity_serial");
					int id = rs.getInt("subscriber_id");
					String activityaction = rs.getString("activity_action");
					String bookbarcode = rs.getString("book_barcode");
					String booktitle = rs.getString("book_title");
					String copynumber = rs.getString("bookcopy_copyNo");
					Date date = rs.getDate("activity_date");

// Create a formatted string with the retrieved data
					subscriberData = serialid + ", " + id + ", " + activityaction + ", " + bookbarcode + ", "
							+ booktitle + ", " + copynumber + ", " + date;
				}
			}
		} catch (SQLException e) {
// Print the stack trace if an SQL exception occurs
			e.printStackTrace();
		}

		return subscriberData;
	}

	/* ADDED BY AMIR */
	public static int AddNewLogActivity(int subscriberId, String activityAction, String bookBarcode, String bookTitle,
			Integer bookcopyCopyNo, Date activityDate) {
		PreparedStatement stmt;
		try {
			stmt = mysqlConnection.conn.prepareStatement("INSERT INTO activitylog (subscriber_id, activity_action, book_barcode, book_title, bookcopy_copyNo, activity_date) VALUES (?, ?, ?, ?, ?, ?)",
					PreparedStatement.RETURN_GENERATED_KEYS);

			stmt.setInt(1, subscriberId);
			stmt.setString(2, activityAction);
			stmt.setString(3, bookBarcode);
			stmt.setString(4, bookTitle);
			if (bookcopyCopyNo == null) {
	            stmt.setNull(5, java.sql.Types.INTEGER);
	        } else {
	            stmt.setInt(5, bookcopyCopyNo);
	        }
			stmt.setDate(6, activityDate);

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

/////////////////////// END //////////////////////////////////
///////////////////// --- Amir LogActivity Entity section
/////////////////////// ---///////////////////////
//////////////////////////////////////////////////////////////////////////////////////////

}
