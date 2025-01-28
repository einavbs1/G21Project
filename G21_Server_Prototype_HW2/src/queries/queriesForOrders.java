package queries;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import Server.mysqlConnection;


/**
 * Handles operations related to orders in the database, such as creating, retrieving, and updating orders.
 */
public class queriesForOrders {
	
	

	 /**
     * Creates a new order in the database.
     *
     * @param subscriberId The ID of the subscriber making the order.
     * @param bookBarcode  The barcode of the book being ordered.
     * @param bookTitle    The title of the book being ordered.
     * @return The new order number if successful, -1 otherwise.
     */
	public static int createOrder(int subscriberId, String bookBarcode, String bookTitle) {
		PreparedStatement stmt;
		try {
			stmt = mysqlConnection.conn.prepareStatement("INSERT INTO orders (subscriber_id, book_barcode, book_title, order_requestedDate, order_status) VALUES (?, ?, ?, ?, ?)",
					PreparedStatement.RETURN_GENERATED_KEYS);

			stmt.setInt(1, subscriberId);
			stmt.setString(2, bookBarcode);
			stmt.setString(3, bookTitle);
			stmt.setDate(4, Date.valueOf(LocalDate.now()));
			stmt.setInt(5, 1); // Initial status - active
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

	/**
     * Retrieves all orders from the database.
     *
     * @return A list of all orders as strings.
     */
	public static List<String> GetOrdersTable() {
		List<String> orders = new ArrayList<>();
		String query = "SELECT * FROM Orders";

		try (Statement stmt = mysqlConnection.conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
			while (rs.next()) {
				String orderData = rs.getInt("order_number") + ", " + rs.getInt("subscriber_id") + ", "
						+ rs.getString("book_barcode") + ", " + rs.getString("book_title") + ", " + rs.getDate("order_requestedDate") + ", "
						+ rs.getInt("order_status") + ", " + rs.getDate("order_bookArrivedDate");
				orders.add(orderData);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orders;
	}

	/**
     * Loads a specific order by its order number.
     *
     * @param orderNumber The order number.
     * @return The order details as a string or "Empty" if not found.
     */

	public static String LoadOrder(int orderNumber) {
		String query = "SELECT * FROM Orders WHERE order_number = ?";
		try {
			PreparedStatement pstmt = mysqlConnection.conn.prepareStatement(query);
			pstmt.setInt(1, orderNumber);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				String orderData = rs.getInt("order_number") + ", " + rs.getInt("subscriber_id") + ", "
						+ rs.getString("book_barcode") + ", " + rs.getString("book_title") + ", " + rs.getDate("order_requestedDate") + ", "
						+ rs.getInt("order_status") + ", " + rs.getDate("order_bookArrivedDate");
				return orderData;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "Empty";
	}

	/**
     * Updates the details of an existing order.
     *
     * @param orderNumber  The order number.
     * @param subscriberId The subscriber ID.
     * @param bookBarcode  The book barcode.
     * @param bookTitle    The book title.
     * @param requestDate  The request date.
     * @param status       The status of the order.
     * @param arrivedDate  The arrival date (nullable).
     * @return True if the update was successful, false otherwise.
     */
	public static boolean updateOrderDetails(int orderNumber, int subscriberId, String bookBarcode, String bookTitle,
			Date requestDate, int status, Date arrivedDate) {
		try {
			String query = "UPDATE Orders SET subscriber_id = ?, book_barcode = ?, book_title = ?,"
					+ "order_requestedDate = ?, order_status = ?, order_bookArrivedDate = ? "
					+ "WHERE order_number = ?";

			PreparedStatement pstmt = mysqlConnection.conn.prepareStatement(query);
			pstmt.setInt(1, subscriberId);
			pstmt.setString(2, bookBarcode);
			pstmt.setString(3, bookTitle);
			pstmt.setDate(4, requestDate);
			pstmt.setInt(5, status);
			if (arrivedDate != null) {
				pstmt.setDate(6, arrivedDate);
			} else {
				pstmt.setNull(6, Types.DATE);
			}
			pstmt.setInt(7, orderNumber);

			pstmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
     * Retrieves all active orders for a specific book.
     *
     * @param barcode The book barcode.
     * @return A list of active orders for the book.
     */
	public static List<String> GetAllMyActiveOrders(String barcode) {
		String query = "SELECT * FROM orders WHERE book_barcode = ? AND order_status IN (1, 2)";
		String bookcopyData = new String();
		List<String> foundOrders = new ArrayList<String>();
		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
			stmt.setString(1, barcode);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int order_number = rs.getInt("order_number");
					int subscriber_id = rs.getInt("subscriber_id");
					String book_barcode = rs.getString("book_barcode");
					String book_title = rs.getString("book_title");
					Date order_requestedDate = rs.getDate("order_requestedDate");
					int order_status = rs.getInt("order_status");
					Date order_bookArrivedDate = rs.getDate("order_bookArrivedDate");
					bookcopyData = order_number + ", " + subscriber_id + ", " + book_barcode + ", " + book_title + ", " + order_requestedDate + ", "
							+ order_status + ", " + order_bookArrivedDate;

					foundOrders.add(bookcopyData);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return foundOrders;
	}

	
	/**
     * Retrieves all orders for a specific subscriber.
     *
     * @param subscriberId The subscriber ID.
     * @return A list of all orders for the subscriber.
     */
	public static List<String> GetOrdersBySubscriber(int subscriberId) {
	    List<String> orders = new ArrayList<>();
	    String query = "SELECT * FROM Orders WHERE subscriber_id = ?";

	    try (PreparedStatement pstmt = mysqlConnection.conn.prepareStatement(query)) {
	        pstmt.setInt(1, subscriberId);
	        ResultSet rs = pstmt.executeQuery();
	        
	        while (rs.next()) {
	            String orderData = rs.getInt("order_number") + ", " + 
	                             rs.getInt("subscriber_id") + ", " +
	                             rs.getString("book_barcode") + ", " + 
	                             rs.getString("book_title") + ", " + 
	                             rs.getDate("order_requestedDate") + ", " +
	                             rs.getInt("order_status") + ", " + 
	                             rs.getDate("order_bookArrivedDate");
	            orders.add(orderData);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return orders;
	}
	
	/**
     * Gets the count of active orders for a specific book.
     *
     * @param bookBarcode The book barcode.
     * @return The count of active orders.
     */
	public static int getActiveOrdersCountForBook(String bookBarcode) {
	    String query = "SELECT COUNT(*) FROM Orders WHERE book_barcode = ? AND order_status IN (1, 2)";
	    try (PreparedStatement pstmt = mysqlConnection.conn.prepareStatement(query)) {
	        pstmt.setString(1, bookBarcode);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getInt(1);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return 0;
	}


			


}
