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
