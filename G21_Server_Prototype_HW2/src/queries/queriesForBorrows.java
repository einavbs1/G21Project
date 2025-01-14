package queries;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
			int bookcopy_copyNo, Date borrow_date, Date borrow_expectReturnDate, Date borrow_actualReturnDate,
			int librarian_id, String librarian_name, int borrow_lostBook) {
		PreparedStatement stmt;
		try {
			stmt = mysqlConnection.conn.prepareStatement("INSERT INTO borrowedrecords VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
					PreparedStatement.RETURN_GENERATED_KEYS);

			stmt.setInt(1, subscriber_id);

			stmt.setString(2, book_barcode);
			stmt.setString(3, book_title);
			stmt.setInt(4, bookcopy_copyNo);

			stmt.setDate(5, borrow_date);
			stmt.setDate(6, borrow_actualReturnDate);
			stmt.setDate(7, borrow_expectReturnDate);

			stmt.setInt(8, librarian_id);
			stmt.setString(9, librarian_name);

			stmt.setInt(10, borrow_lostBook);

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
					String borrow_date = rs.getString("borrow_date");
					String borrow_expectReturnDate = rs.getString("borrow_expectReturnDate");
					String borrow_actualReturnDate = rs.getString("borrow_actualReturnDate");
					int librarian_id = rs.getInt("librarian_id");
					String librarian_name = rs.getString("librarian_name");
					int borrow_lostBook = rs.getInt("borrow_lostBook");

					// Create a formatted string with the subscriber's information
					borrowedRecords = borrow_number + ", " + subscriber_id + ", " + book_barcode + ", " + book_title
							+ ", " + bookcopy_copyNo + ", " + borrow_date + ", " + borrow_expectReturnDate + ", "
							+ borrow_actualReturnDate + ", " + librarian_id + ", " + librarian_name + ", "
							+ borrow_lostBook;

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
	public static boolean UpdateBorrowedRecordReturnTime(int borrow_number, Date borrow_expectReturnDate,
			Date borrow_actualReturnDate, int borrow_lostBook) {
		String query = "UPDATE borrowedrecords SET borrow_expectReturnDate = ?, borrow_actualReturnDate = ?, borrow_lostBook = ?"
				+ "WHERE  borrow_number = ?";

		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
			stmt.setDate(1, borrow_expectReturnDate);
			stmt.setDate(2, borrow_actualReturnDate);
			stmt.setInt(3, borrow_lostBook);
			stmt.setInt(4, borrow_number);

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

	
}
