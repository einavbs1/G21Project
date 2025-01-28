package queries;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Server.mysqlConnection;


/**
 * This class provides methods to interact with the BookCopy table in the database.
 */
public class queriesForBookCopy {

//////////////////////////////////////////////////////////////////////////////////////////
///////////////////// --- Einavs bookCopy Entity
////////////////////////////////////////////////////////////////////////////////////////// section---///////////////////////
//////////////////////////////////////////////////////////////////////////////////////////

	/**
     * Adds a new BookCopy to the database.
     *
     * @param barcode      The barcode of the book.
     * @param copyNo       The copy number of the book.
     * @param isAvailable  The availability status of the book (1 = available, 0 = not available).
     * @param isLost       The lost status of the book (1 = lost, 0 = not lost).
     * @param returnDate   The return date of the book (nullable).
     * @param subscriberID The ID of the subscriber who currently has the book (nullable).
     * @return {@code true} if the book copy was successfully added, {@code false} otherwise.
     */
	public static boolean addNewBookCopyToDB(String barcode, int copyNo, int isAvailable, int isLost, Date returnDate,
			int subscriberID) {
		PreparedStatement stmt;
		try {
			stmt = mysqlConnection.conn.prepareStatement("INSERT INTO bookcopy VALUES (?, ?, ?, ?, ?, ?)");

			stmt.setString(1, barcode);
			stmt.setInt(2, copyNo);
			stmt.setInt(3, isAvailable);
			stmt.setInt(4, isLost);
			stmt.setDate(5, returnDate);
			stmt.setInt(6, subscriberID);

			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	 /**
     * Retrieves a BookCopy from the database based on its barcode and copy number.
     *
     * @param barcode The barcode of the book.
     * @param copyNo  The copy number of the book.
     * @return A string representing the BookCopy details in the format:
     *         "book_barcode, bookcopy_copyNo, bookCopy_isAvailable, bookCopy_isLost,
     *         bookcopy_returnDate, subscriber_id".
     *         If no record is found, returns "Empty".
     */
	public static String GetBookCopyFromDB(String barcode, int copyNo) {
		String query = "SELECT * FROM BookCopy WHERE book_barcode = ? AND bookcopy_copyNo = ?";
		String bookcopyData = "Empty";
		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
			stmt.setString(1, barcode);
			stmt.setInt(2, copyNo);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					String book_barcode = rs.getString("book_barcode");
					String bookcopy_copyNo = rs.getString("bookcopy_copyNo");
					int bookCopy_isAvailable = rs.getInt("bookCopy_isAvailable");
					int bookCopy_isLost = rs.getInt("bookCopy_isLost");
					Date bookcopy_returnDate = rs.getDate("bookcopy_returnDate");
					int subscriber_id = rs.getInt("subscriber_id");
					bookcopyData = book_barcode + ", " + bookcopy_copyNo + ", " + bookCopy_isAvailable + ", " + bookCopy_isLost + ", "
							+ bookcopy_returnDate + ", " + subscriber_id;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return bookcopyData;
	}

	 /**
     * Updates the details of a BookCopy in the database.
     *
     * @param barcode      The barcode of the book.
     * @param copyNo       The copy number of the book.
     * @param isAvailable  The new availability status of the book (1 = available, 0 = not available).
     * @param isLost       The new lost status of the book (1 = lost, 0 = not lost).
     * @param returnDate   The new return date of the book (nullable).
     * @param subscriberID The new subscriber ID associated with the book (nullable).
     * @return {@code true} if the update was successful, {@code false} otherwise.
     */
	public static boolean updateBookCopyDetails(String barcode, int copyNo, int isAvailable, int isLost,
			Date returnDate, Integer subscriberID) {
		String query = "UPDATE bookcopy SET bookCopy_isAvailable = ?, bookCopy_isLost = ?, bookcopy_returnDate = ?, "
				+ "subscriber_id = ? WHERE book_barcode = ? AND bookcopy_copyNo = ?";

		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
			stmt.setInt(1, isAvailable);
			stmt.setInt(2, isLost);
			if(returnDate == null) {
				stmt.setNull(3, java.sql.Types.DATE);
			}else {
				stmt.setDate(3, returnDate);
			}
			if(subscriberID == null) {
				stmt.setNull(4, java.sql.Types.INTEGER);
			}else {
				stmt.setInt(4, subscriberID);
			}
			
			
			stmt.setString(5, barcode);
			stmt.setInt(6, copyNo);

			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

/////////////////////// END //////////////////////////////////
///////////////////// --- Einavs bookCopy Entity section
/////////////////////// ---///////////////////////
//////////////////////////////////////////////////////////////////////////////////////////

}
