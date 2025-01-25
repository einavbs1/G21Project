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

public class queriesForOrders {
	
	
	//////////////////////////////////////////////////////////////////////////////////////////
	///////////////////// --- Chen Orders Entity section---///////////////////////

	/**
	 * Author: Chen Creates a new order in the database.
	 *
	 * @param subscriberId The ID of the subscriber making the order
	 * @param bookBarcode The barcode of the book being ordered
	 * @return int The new order number if successful, -1 if failed
	 * 
	 *         Note: This method automatically: - Generates the next available order
	 *         number - Sets the request date to current date - Sets the initial
	 *         status to 0 (pending)
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
	 * Author: Chen Retrieves all orders from the database.
	 * 
	 * @return List<String> A list of strings, each representing an order with its
	 *         details in the format: "orderNumber, subscriberId, bookBarcode,
	 *         requestDate, status, arrivedDate"
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
	 * Author: Chen Loads a specific order from the database.
	 * 
	 * @param orderNumber The unique identifier of the order to load
	 * @return String The order details as a comma-separated string, or "Empty" if
	 *         not found
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
	 * Author: Chen Updates an existing order's details in the database.
	 * 
	 * @param orderNumber  The unique identifier of the order to update
	 * @param subscriberId The ID of the subscriber associated with the order
	 * @param bookBarcode  The barcode of the book being ordered
	 * @param copyNo       The copy number of the book
	 * @param requestDate  The date when the order was requested
	 * @param status       The new status of the order (-1: cancelled, 0: pending,
	 *                     1: fulfilled)
	 * @param arrivedDate  The date when the book arrived (can be null)
	 * @return boolean True if update was successful, false otherwise
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
	 * Author: Matan This method is returning order list of specific book.
	 * 
	 * @param barcode - the barcode on the requested book
	 * @return List of copybooks on this book barcode
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
	 * Author: Chen
	 * Retrieves all orders for a specific subscriber from the database.
	 * 
	 * @param subscriberId The ID of the subscriber whose orders we want to fetch
	 * @return List<String> A list of strings, each representing an order
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
	 * Author: Chen
	 * Returns the number of active orders for a specific book.
	 * 
	 * @param bookBarcode The barcode of the book to check
	 * @return int Number of active orders for this book
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


			
	/////////////////////// END //////////////////////////////////
	///////////////////// --- Chen Orders Entity section ---///////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////


}
