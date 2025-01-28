package queries;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Server.mysqlConnection;

/**
 * This class provides methods to interact with the activity logs table.
 */
public class queriesForActivityLogs {

//////////////////////////////////////////////////////////////////////////////////////////
///////////////////// --- Amir LogActivity Entity
////////////////////////////////////////////////////////////////////////////////////////// section---///////////////////////

	
	/**
     * Retrieves all activity logs for a specific subscriber by their ID.
     *
     * @param idtoload The ID of the subscriber whose activity logs need to be retrieved.
     * @return A list of strings, where each string represents an activity log entry
     *         in the format: "serialid, id, activityaction, bookbarcode, booktitle, copynumber, date".
     */
	public static List<String> LoadLogActivitybyid(int idtoload) {
		String query = "SELECT * FROM activitylog WHERE subscriber_id = ?";

		List<String> subscriberDataList = new ArrayList<>();

		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
			stmt.setInt(1, idtoload);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					int serialid = rs.getInt("activity_serial");
					int id = rs.getInt("subscriber_id");
					String activityaction = rs.getString("activity_action");
					String bookbarcode = rs.getString("book_barcode");
					String booktitle = rs.getString("book_title");
					int copynumber = rs.getInt("bookcopy_copyNo");
					Date date = rs.getDate("activity_date");

					String subscriberData = serialid + ", " + id + ", " + activityaction + ", " + bookbarcode + ", "
							+ booktitle + ", " + copynumber + ", " + date;

					subscriberDataList.add(subscriberData);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return subscriberDataList;
	}

	
	/**
     * Retrieves a single activity log entry by its serial ID.
     *
     * @param serialIdToLoad The serial ID of the activity log to be retrieved.
     * @return A string representing the activity log entry in the format:
     *         "serialid, id, activityaction, bookbarcode, booktitle, copynumber, date".
     *         If no record is found, returns "Record not found".
     */
	public static String LoadLogActivityBySerialId(int serialIdToLoad) {
		String query = "SELECT * FROM activitylog WHERE activity_serial = ?";

		String subscriberData = "Record not found";

		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
			stmt.setInt(1, serialIdToLoad);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int serialid = rs.getInt("activity_serial");
					int id = rs.getInt("subscriber_id");
					String activityaction = rs.getString("activity_action");
					String bookbarcode = rs.getString("book_barcode");
					String booktitle = rs.getString("book_title");
					String copynumber = rs.getString("bookcopy_copyNo");
					Date date = rs.getDate("activity_date");

					subscriberData = serialid + ", " + id + ", " + activityaction + ", " + bookbarcode + ", "
							+ booktitle + ", " + copynumber + ", " + date;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return subscriberData;
	}

	
	/**
     * Adds a new activity log entry to the database.
     *
     * @param subscriberId     The ID of the subscriber associated with the activity log.
     * @param activityAction   The action performed (e.g., "borrowed", "returned").
     * @param bookBarcode      The barcode of the book associated with the activity.
     * @param bookTitle        The title of the book associated with the activity.
     * @param bookcopyCopyNo   The copy number of the book, or null if not applicable.
     * @param activityDate     The date of the activity.
     * @return The generated key (serial ID) of the newly inserted activity log entry,
     *         or -1 if the operation failed.
     */
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
