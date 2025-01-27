package queries;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import Server.mysqlConnection;

public class queriesForBorrows {

	//////////////////////////////////////////////////////////////////////////////////////////
	///////////////////// --- Matan Borrow Entity section---///////////////////////

	/**
	 * Author: Matan send request to DB to create new borrowed record
	 * 
	 * @param borrow_number
	 * @param subscriber_id
	 * @param book_barcode
	 * @param book_title
	 * @param bookcopy_copyNo
	 * @param borrow_date
	 * @param borrow_expectReturnDate
	 * @param borrow_actualReturnDate
	 * @param librarian_id
	 * @param librarian_name
	 * @param borrow_lostBook
	 * @return
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
	 * Author: Matan return borrowedRecord string by borrowNumber from DB
	 * 
	 * @param borrowNumber
	 * @return
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
	 * Author: Matan update borrow return date and if the book lost
	 * 
	 * @param borrow_number
	 * @param borrow_expectReturnDate
	 * @param borrow_actualReturnDate
	 * @param borrow_lostBook
	 * @return
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

	/////////////////////// END //////////////////////////////////
	///////////////////// --- Matan Borrow Entity section ---///////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Author: Yuval. This method is returning the list of active borrow records for
	 * a specific subscriber from the DB.
	 * 
	 * @param subscriber_id
	 * @return List of active borrow records
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

	// added by amir 18.1

	/**
	 * Gets borrowed books statistics for the current month
	 * 
	 * @return List of borrowed books with loan duration details for the current
	 *         month
	 */
	public static List<String> getMonthlyBorrowedBooksStats() {
		ArrayList<String> monthlyBorrows = new ArrayList<>();

		String query = "SELECT br.*, "
				+ "DATEDIFF(COALESCE(br.borrow_actualReturnDate, CURRENT_DATE), br.borrow_date) as loan_duration, "
				+ "CASE WHEN br.borrow_actualReturnDate IS NULL THEN "
				+ "    DATEDIFF(CURRENT_DATE, br.borrow_expectReturnDate) " + "ELSE "
				+ "    DATEDIFF(br.borrow_actualReturnDate, br.borrow_expectReturnDate) " + "END as delay_days "
				+ "FROM borrowedrecords br " + "WHERE (YEAR(br.borrow_date) = YEAR(CURRENT_DATE) AND "
				+ "       MONTH(br.borrow_date) = MONTH(CURRENT_DATE)) OR "
				+ "      (br.borrow_actualReturnDate IS NULL) " + "ORDER BY loan_duration DESC";

		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				int borrow_number = rs.getInt("borrow_number");
				int subscriber_id = rs.getInt("subscriber_id");
				String book_barcode = rs.getString("book_barcode");
				String book_title = rs.getString("book_title");
				int bookcopy_copyNo = rs.getInt("bookcopy_copyNo");
				String borrow_date = rs.getString("borrow_date");
				String borrow_expectReturnDate = rs.getString("borrow_expectReturnDate");
				String borrow_actualReturnDate = rs.getString("borrow_actualReturnDate");
				int librarian_id = rs.getInt("librarian_id");
				int loan_duration = rs.getInt("loan_duration");
				int delay_days = rs.getInt("delay_days");

				String borrowRecord = borrow_number + ", " + subscriber_id + ", " + book_barcode + ", " + book_title
						+ ", " + bookcopy_copyNo + ", " + borrow_date + ", " + borrow_expectReturnDate + ", "
						+ borrow_actualReturnDate + ", " + librarian_id + ", " + loan_duration + ", "
						+ (delay_days > 0 ? delay_days : 0);

				monthlyBorrows.add(borrowRecord);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return monthlyBorrows;
	}

}
