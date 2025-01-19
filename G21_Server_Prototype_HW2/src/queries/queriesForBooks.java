package queries;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Server.mysqlConnection;

public class queriesForBooks {

//////////////////////////////////////////////////////////////////////////////////////////
///////////////////// --- Einavs books Entity section---///////////////////////
//////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Author: Einav This method is getting all information to create new Book in DB
	 * 
	 * @param barcode
	 * @param title
	 * @param subject
	 * @param description
	 * @param allCopies
	 * @param availableCopies
	 * @param ordersNumber
	 * @param lostNumber
	 * @param location
	 * @return
	 */
	public static boolean addNewBookToDB(String barcode, String title, String subject, String description,
			int allCopies, int availableCopies, int ordersNumber, int lostNumber, String location) {
		PreparedStatement stmt;
		try {
			stmt = mysqlConnection.conn.prepareStatement("INSERT INTO books VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

			stmt.setString(1, barcode);
			stmt.setString(2, title);
			stmt.setString(3, subject);
			stmt.setString(4, description);
			stmt.setInt(5, allCopies);
			stmt.setInt(6, availableCopies);
			stmt.setInt(7, ordersNumber);
			stmt.setInt(8, lostNumber);
			stmt.setString(9, location);

			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Author: Einav This method is returning book string by his barcode.
	 * 
	 * @param barcode
	 * @return String of the requested book
	 */
	public static String GetBookFromDB(String barcode) {
		String query = "SELECT * FROM books WHERE book_barcode = ?";
		String bookData = "Empty";

		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
			stmt.setString(1, barcode);

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
	 * Author: Einav this method is updating the book details if we need to change.
	 * 
	 * @param barcode
	 * @param title
	 * @param subject
	 * @param description
	 * @param allCopies
	 * @param availableCopies
	 * @param ordersNumber
	 * @param lostNumber
	 * @param location
	 * @return
	 */
	public static boolean updateBookDetails(String barcode, String title, String subject, String description,
			int allCopies, int availableCopies, int ordersNumber, int lostNumber, String location) {

		String query = "UPDATE books SET book_title = ?, book_subject = ?, book_description = ?, "
				+ "book_allCopies = ?, book_availableCopies = ?, book_ordersNumber = ?, "
				+ "book_lostNumber = ?, book_location = ? WHERE book_barcode = ?";

		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
			stmt.setString(1, title);
			stmt.setString(2, subject);
			stmt.setString(3, description);
			stmt.setInt(4, allCopies);
			stmt.setInt(5, availableCopies);
			stmt.setInt(6, ordersNumber);
			stmt.setInt(7, lostNumber);
			stmt.setString(8, location);
			stmt.setString(9, barcode);

			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Author: Einav This method is returning book list of all the copies on this book.
	 * 
	 * @param barcode - the barcode on the requested book
	 * @return List of copybooks on this book barcode
	 */
	public static List<String> GetAllMyCopies(String barcode) {
		String query = "SELECT * FROM Bookcopy WHERE book_barcode = ?";
		String bookcopyData = new String();
		List<String> foundBooks = new ArrayList<String>();
		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {

			stmt.setString(1, barcode);

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

					foundBooks.add(bookcopyData);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return foundBooks;
	}

/////////////////////// END //////////////////////////////////
///////////////////// --- Einavs books Entity section ---///////////////////////
//////////////////////////////////////////////////////////////////////////////////////////

}
