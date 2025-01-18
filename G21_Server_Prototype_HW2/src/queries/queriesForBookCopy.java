package queries;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Server.mysqlConnection;

public class queriesForBookCopy {

//////////////////////////////////////////////////////////////////////////////////////////
///////////////////// --- Einavs bookCopy Entity
////////////////////////////////////////////////////////////////////////////////////////// section---///////////////////////
//////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Author: Einav This method is getting all information to create new BookCopy
	 * in DB
	 * 
	 * @param barcode
	 * @param copyNo
	 * @param isAvailable
	 * @param isLost
	 * @param returnDate
	 * @param subscriberID
	 * @return
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
	 * Author: Einav This method is returning bookCopy string by his barcode and
	 * copyNo.
	 * 
	 * @param barcode
	 * @param copyNo
	 * @return String of the requested book
	 */
	public static String GetBookCopyFromDB(String barcode, int copyNo) {
		String query = "SELECT * FROM BookCopy WHERE book_barcode = ? AND bookcopy_copyNo = ?";
		String bookData = "Empty";
		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
			stmt.setString(1, barcode);
			stmt.setInt(2, copyNo);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					String book_barcode = rs.getString("book_barcode");
					String book_title = rs.getString("book_title");
					String book_subject = rs.getString("book_subject");
					String book_description = rs.getString("book_description");
					int book_allCopies = rs.getInt("book_allCopies");
					int book_availableCopies = rs.getInt("book_availableCopies");
					int book_ordersNumber = rs.getInt("book_ordersNumber");
					int book_lostNumber = rs.getInt("book_lostNumber");
					String book_location = rs.getString("book_location");
					bookData = book_barcode + ", " + book_title + ", " + book_subject + ", " + book_description + ", "
							+ book_allCopies + ", " + book_availableCopies + ", " + book_ordersNumber + ", "
							+ book_lostNumber + ", " + book_location;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return bookData;
	}

	/**
	 * Author: Einav this method is updating the bookCopy details if we need to
	 * change.
	 * 
	 * @param barcode
	 * @param copyNo
	 * @param isAvailable
	 * @param isLost
	 * @param returnDate
	 * @param subscriberID
	 * @return
	 */
	public static boolean updateBookCopyDetails(String barcode, int copyNo, int isAvailable, int isLost,
			Date returnDate, int subscriberID) {
		String query = "UPDATE bookcopy SET bookCopy_isAvailable = ?, bookCopy_isLost = ?, bookcopy_returnDate = ?, "
				+ "subscriber_id = ? WHERE book_barcode = ? AND bookcopy_copyNo = ?";

		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
			stmt.setInt(1, isAvailable);
			stmt.setInt(2, isLost);
			stmt.setDate(3, returnDate);
			stmt.setInt(4, subscriberID);
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
