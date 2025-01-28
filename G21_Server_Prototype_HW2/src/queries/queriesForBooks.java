package queries;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import Server.mysqlConnection;

/**
 * This class provides methods to interact with the books and book copies in the database.
 */
public class queriesForBooks {

//////////////////////////////////////////////////////////////////////////////////////////
///////////////////// --- Einavs books Entity section---///////////////////////
//////////////////////////////////////////////////////////////////////////////////////////

	 /**
     * Adds a new book to the database.
     *
     * @param barcode          The barcode of the book.
     * @param title            The title of the book.
     * @param subject          The subject of the book.
     * @param description      A description of the book.
     * @param allCopies        The total number of copies available for this book.
     * @param availableCopies  The number of currently available copies.
     * @param ordersNumber     The number of orders placed for this book.
     * @param lostNumber       The number of lost copies of this book.
     * @param location         The location of the book in the library.
     * @return {@code true} if the book was successfully added, {@code false} otherwise.
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
     * Retrieves a book from the database based on its barcode.
     *
     * @param barcode The barcode of the book.
     * @return A string containing the book details in the format:
     *         "book_barcode, book_title, book_subject, book_description,
     *         book_allCopies, book_availableCopies, book_ordersNumber, book_lostNumber, book_location".
     *         If no book is found, returns "Empty".
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
     * Updates the details of a book in the database.
     *
     * @param barcode          The barcode of the book.
     * @param title            The updated title of the book.
     * @param subject          The updated subject of the book.
     * @param description      The updated description of the book.
     * @param allCopies        The updated total number of copies.
     * @param availableCopies  The updated number of available copies.
     * @param ordersNumber     The updated number of orders for the book.
     * @param lostNumber       The updated number of lost copies.
     * @param location         The updated location of the book in the library.
     * @return {@code true} if the book details were successfully updated, {@code false} otherwise.
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
     * Searches for books by name, matching any part of the book title.
     *
     * @param bookname The string to search for in book titles.
     * @return A list of strings representing books that match the name.
     */
	public static List<String> SearchBooksByName(String bookname) {
		String query = "SELECT * FROM Books WHERE LOWER(book_title) LIKE ?";
		String bookData = new String();
		List<String> foundBooks = new ArrayList<String>();
		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
			String normalizedInput = "%" + bookname.trim().toLowerCase() + "%";
			stmt.setString(1, normalizedInput);

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
					foundBooks.add(bookData);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return foundBooks;
	}

	/**
     * Searches for books by a specific subject.
     *
     * @param subject The subject of the books to search for.
     * @return A list of strings where each string represents a book with details in the format:
     *         "book_barcode, book_title, book_subject, book_description, book_allCopies,
     *         book_availableCopies, book_ordersNumber, book_lostNumber, book_location".
     */
	public static List<String> SearchBooksBySubject(String subject) {
		String query = "SELECT * FROM Books WHERE book_subject = ?";
		String bookData = new String();
		List<String> foundBooks = new ArrayList<String>();
		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {

			stmt.setString(1, subject);

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

					foundBooks.add(bookData);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return foundBooks;
	}

	/**
     * Searches for books by keywords in the description.
     *
     * @param description A comma-separated string of keywords to search for in book descriptions.
     * @return A list of strings where each string represents a book with details in the format:
     *         "book_barcode, book_title, book_subject, book_description, book_allCopies,
     *         book_availableCopies, book_ordersNumber, book_lostNumber, book_location".
     */
	public static List<String> SearchBooksByDescription(String description) {
		String[] tags = description.split(", ");
	    List<String> foundBooks = new ArrayList<>();

	    StringBuilder queryBuilder = new StringBuilder("SELECT * FROM Books WHERE ");
	    for (int i = 0; i < tags.length; i++) {
	        queryBuilder.append("book_description LIKE ?");
	        if (i < tags.length - 1) {
	            queryBuilder.append(" OR ");
	        }
	    }

	    String query = queryBuilder.toString();

	    try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
	        for (int i = 0; i < tags.length; i++) {
	            stmt.setString(i + 1, "%" + tags[i] + "%");
	        }
	        
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                String book_barcode = rs.getString("book_barcode");
	                String book_title = rs.getString("book_title");
	                String book_subject = rs.getString("book_subject");
	                String book_description = rs.getString("book_description");
	                int book_allCopies = rs.getInt("book_allCopies");
	                int book_availableCopies = rs.getInt("book_availableCopies");
	                int book_ordersNumber = rs.getInt("book_ordersNumber");
	                int book_lostNumber = rs.getInt("book_lostNumber");
	                String book_location = rs.getString("book_location");

	                String bookData = book_barcode + ", " + book_title + ", " + book_subject + ", " +
	                        book_description + ", " + book_allCopies + ", " + book_availableCopies + ", " +
	                        book_ordersNumber + ", " + book_lostNumber + ", " + book_location;

	                if (!foundBooks.contains(bookData)) { 
	                    foundBooks.add(bookData);
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return foundBooks;
	}

	
	
	/**
     * Retrieves all copies of a book by its barcode.
     *
     * @param barcode The barcode of the book.
     * @return A list of all copies of a book
     */
	public static List<String> GetAllMyCopies(String barcode) {
		String query = "SELECT * FROM Bookcopy WHERE book_barcode = ?";
		String bookcopyData = new String();
		List<String> foundBooks = new ArrayList<String>();
		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {

			stmt.setString(1, barcode);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
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
	
	/**
     * Retrieves a list of book barcodes and titles from the database.
     *
     * @return A list of book barcodes and titles
     */
	public static List<String> getBookBarcodesAndTitles() {
        List<String> bookList = new ArrayList<>();
        String query = "SELECT CONCAT(book_barcode, ', ', book_title) AS book_info FROM books";

        try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Retrieve the concatenated book info
                String bookInfo = rs.getString("book_info");
                bookList.add(bookInfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bookList;
    }

/////////////////////// END //////////////////////////////////
///////////////////// --- Einavs books Entity section ---///////////////////////
//////////////////////////////////////////////////////////////////////////////////////////

}
