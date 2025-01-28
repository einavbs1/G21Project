package queries;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import Server.mysqlConnection;

/**
 * This class provides methods to interact with the borrowed records in the database.
 */
public class queriesForBorrows {


	/**
     * Creates a new borrowed record in the database.
     *
     * @param subscriber_id The ID of the subscriber.
     * @param book_barcode  The barcode of the borrowed book.
     * @param book_title    The title of the borrowed book.
     * @param bookcopy_copyNo The copy number of the book.
     * @param borrow_date   The borrow date.
     * @param borrow_expectReturnDate The expected return date.
     * @param reminder_serial The reminder serial number.
     * @return The ID of the created borrowed record or -1 if an error occurs.
     */
	public static int createNewBorrowedRecord(int subscriber_id, String book_barcode, String book_title,
			int bookcopy_copyNo, Date borrow_date, Date borrow_expectReturnDate, int reminder_serial) {
		PreparedStatement stmt;
		try {
			stmt = mysqlConnection.conn.prepareStatement(
					"INSERT INTO borrowedrecords (subscriber_id, book_barcode, book_title, bookcopy_copyNo, borrow_date, borrow_expectReturnDate, reminder_serial) VALUES (?, ?, ?, ?, ?, ?, ?)",
					PreparedStatement.RETURN_GENERATED_KEYS);

			stmt.setInt(1, subscriber_id);

			stmt.setString(2, book_barcode);
			stmt.setString(3, book_title);
			stmt.setInt(4, bookcopy_copyNo);

			stmt.setDate(5, borrow_date);
			stmt.setDate(6, borrow_expectReturnDate);
			
			stmt.setInt(7, reminder_serial);



			int CreatedborrowNumber = stmt.executeUpdate();
			return CreatedborrowNumber;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
     * Retrieves a borrowed record by its borrow number.
     *
     * @param borrowNumber The borrow record number.
     * @return A string representing the borrowed record, or "Empty" if not found.
     */
	public static String getBorrowedRecordFromDB(int borrowNumber) {

		String query = "SELECT * FROM borrowedrecords WHERE borrow_number = ?";
		String borrowedRecords = "Empty";

		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
			stmt.setInt(1, borrowNumber);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int borrow_number = rs.getInt("borrow_number");
					int subscriber_id = rs.getInt("subscriber_id");
					String book_barcode = rs.getString("book_barcode");
					String book_title = rs.getString("book_title");
					int bookcopy_copyNo = rs.getInt("bookcopy_copyNo");
					Date borrow_date = rs.getDate("borrow_date");
					Date borrow_expectReturnDate = rs.getDate("borrow_expectReturnDate");
					Date borrow_actualReturnDate = rs.getDate("borrow_actualReturnDate");
					Integer librarian_id = rs.getInt("borrow_changedBylibrarian_id");
					String librarian_name = rs.getString("borrow_changedBylibrarian_name");
					Date lastChanges = rs.getDate("borrow_lastChange");
					int borrow_lostBook = rs.getInt("borrow_lostBook");
					int borrow_status = rs.getInt("borrow_status");
					int reminder_serial = rs.getInt("reminder_serial");

					// Create a formatted string with the subscriber's information
					borrowedRecords = borrow_number + ", " + subscriber_id + ", " + book_barcode + ", " + book_title
							+ ", " + bookcopy_copyNo + ", " + borrow_date + ", " + borrow_expectReturnDate + ", "
							+ borrow_actualReturnDate + ", " + librarian_id + ", " + librarian_name + ", " + lastChanges + ", "
							+ borrow_lostBook + ", " + borrow_status + ", " + reminder_serial;

				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return borrowedRecords;

	}

	/**
     * Updates the details of a borrowed record.
     *
     * @param borrow_number The borrow record number.
     * @param borrow_expectReturnDate The expected return date.
     * @param borrow_actualReturnDate The actual return date.
     * @param librarianID   The librarian's ID who updated the record.
     * @param librarianName The librarian's name who updated the record.
     * @param lastChange    The date of the last update.
     * @param borrow_lostBook The status of whether the book is lost.
     * @param borrow_status The status of the borrow record.
     * @param reminder_serial The reminder serial number.
     * @return {@code true} if the update was successful, {@code false} otherwise.
     */
	public static boolean UpdateBorrowedRecord(int borrow_number, Date borrow_expectReturnDate, Date borrow_actualReturnDate,
			Integer librarianID, String librarianName, Date lastChange, int borrow_lostBook, int borrow_status, int reminder_serial) {
		String query = "UPDATE borrowedrecords SET borrow_expectReturnDate = ?, borrow_actualReturnDate = ?, borrow_changedBylibrarian_id = ?,"
				+ "borrow_changedBylibrarian_name = ?, borrow_lastChange = ?, borrow_lostBook = ?, borrow_status = ?, reminder_serial = ?"
				+ " WHERE  borrow_number = ?";

		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
			
			stmt.setDate(1, borrow_expectReturnDate);
			
			if(borrow_actualReturnDate == null) {
				stmt.setNull(2, java.sql.Types.DATE);
			}else {
				stmt.setDate(2, borrow_actualReturnDate);
			}
			
			if(librarianID == null) {
				stmt.setNull(3, java.sql.Types.INTEGER);
			}else {
				stmt.setInt(3, librarianID);
			}
			
			if(librarianName == null) {
				stmt.setNull(4, java.sql.Types.VARCHAR);
			}else {
				stmt.setString(4, librarianName);
			}
			
			if(lastChange == null) {
				stmt.setNull(5, java.sql.Types.DATE);
			}else {
				stmt.setDate(5, lastChange);
			}
			
			stmt.setInt(6, borrow_lostBook);
			stmt.setInt(7, borrow_status);
			stmt.setInt(8, reminder_serial);
			
			stmt.setInt(9, borrow_number);

			stmt.executeUpdate();

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
     * Retrieves a list of active borrow records for a specific subscriber.
     *
     * @param subscriber_id The subscriber's ID.
     * @return A list of strings representing active borrow records.
     */
	public static List<String> getAllSubscriberActiveBorrowRecordsFromDB(int subscriber_id) {

		List<String> activeBorrowRecords = new ArrayList<>();
		String query = "SELECT * FROM BorrowedRecords WHERE subscriber_id = ? AND (borrow_status = 1 OR borrow_lostBook = 1)";

		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
			stmt.setInt(1, subscriber_id);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					int borrow_number = rs.getInt("borrow_number");
					String book_barcode = rs.getString("book_barcode");
					String book_title = rs.getString("book_title");
					int bookcopy_copyNo = rs.getInt("bookcopy_copyNo");
					Date borrow_date = rs.getDate("borrow_date");
					Date borrow_expectReturnDate = rs.getDate("borrow_expectReturnDate");
					int borrow_lostBook = rs.getInt("borrow_lostBook");

					String borrowRecord = borrow_number + ", " + book_barcode + ", " + book_title + ", "
							+ bookcopy_copyNo + ", " + borrow_date + ", " + borrow_expectReturnDate + ", " + borrow_lostBook;
					activeBorrowRecords.add(borrowRecord);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return activeBorrowRecords;
	}

	 /**
     * Generates a borrow statistics report for a specific date.
     *
     * @param month The month of the records.
     * @param year  The year of the records.
     * @return A string containing the statistics.
     */
	public static String getBorrowsOfSpecificDate(int month, int year) {

		String borrowsReportData = "Empty";

		String query = "SELECT " + "COUNT(*) AS totalBorrows, "
				+ "SUM(CASE WHEN borrow_actualReturnDate = borrow_expectReturnDate THEN 1 ELSE 0 END) AS returnInTime, "
				+ "SUM(CASE WHEN borrow_actualReturnDate > borrow_expectReturnDate THEN 1 ELSE 0 END) AS lateReturn, "
				+ "SUM(CASE WHEN borrow_actualReturnDate < borrow_expectReturnDate THEN 1 ELSE 0 END) AS returnBeforeTime, "
				+ "SUM(CASE WHEN borrow_actualReturnDate IS NULL THEN 1 ELSE 0 END) AS notReturnedYet, "
				+ "SUM(CASE WHEN borrow_lostBook = 1 THEN 1 ELSE 0 END) AS lostBooks "
				+ "FROM borrowedrecords WHERE MONTH(borrow_date) = ? AND YEAR(borrow_date) = ?";

		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
			stmt.setInt(1, month);
			stmt.setInt(2, year);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int totalBorrows = rs.getInt("totalBorrows");
					int returnInTime = rs.getInt("returnInTime");
					int lateReturn = rs.getInt("lateReturn");
					int returnBeforeTime = rs.getInt("returnBeforeTime");
					int notReturnedYet = rs.getInt("notReturnedYet");
					int lostBooks = rs.getInt("lostBooks");

					borrowsReportData = totalBorrows + ", " + returnInTime + ", " + lateReturn
							+ ", " + returnBeforeTime + ", " + notReturnedYet + ", " + lostBooks;
				} else {
	                // No rows found for the given month and year
	                borrowsReportData = "0, 0, 0, 0, 0, 0";
	            }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return borrowsReportData;
	}
	
	
	/**
	 * Generates a borrow statistics report for a specific book in a given month and year.
	 *
	 * @param barcode The barcode of the book.
	 * @param month   The month of the records (1-12).
	 * @param year    The year of the records.
	 * @return A string containing the statistics in the format:
	 *         "totalBorrows, returnInTime, lateReturn, returnBeforeTime, notReturnedYet, lostBooks".
	 *         Returns "0, 0, 0, 0, 0, 0" if no records are found.
	 */
	public static String getBorrowsOfBookInSpecificDate(String barcode,int month, int year) {

		String borrowsReportData = "Empty";

		String query = "SELECT " + "COUNT(*) AS totalBorrows, "
				+ "SUM(CASE WHEN borrow_actualReturnDate = borrow_expectReturnDate THEN 1 ELSE 0 END) AS returnInTime, "
				+ "SUM(CASE WHEN borrow_actualReturnDate > borrow_expectReturnDate THEN 1 ELSE 0 END) AS lateReturn, "
				+ "SUM(CASE WHEN borrow_actualReturnDate < borrow_expectReturnDate THEN 1 ELSE 0 END) AS returnBeforeTime, "
				+ "SUM(CASE WHEN borrow_actualReturnDate IS NULL THEN 1 ELSE 0 END) AS notReturnedYet, "
				+ "SUM(CASE WHEN borrow_lostBook = 1 THEN 1 ELSE 0 END) AS lostBooks "
				+ "FROM borrowedrecords WHERE MONTH(borrow_date) = ? AND YEAR(borrow_date) = ? AND book_barcode = ?";

		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
			stmt.setInt(1, month);
			stmt.setInt(2, year);
			stmt.setString(3, barcode);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int totalBorrows = rs.getInt("totalBorrows");
					int returnInTime = rs.getInt("returnInTime");
					int lateReturn = rs.getInt("lateReturn");
					int returnBeforeTime = rs.getInt("returnBeforeTime");
					int notReturnedYet = rs.getInt("notReturnedYet");
					int lostBooks = rs.getInt("lostBooks");

					borrowsReportData = totalBorrows + ", " + returnInTime + ", " + lateReturn
							+ ", " + returnBeforeTime + ", " + notReturnedYet + ", " + lostBooks;
				} else {
	                // No rows found for the given month and year
	                borrowsReportData = "0, 0, 0, 0, 0, 0";
	            }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return borrowsReportData;
	}

}
