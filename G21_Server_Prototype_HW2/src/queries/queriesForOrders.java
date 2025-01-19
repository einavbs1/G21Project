package queries;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
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
	public static int createOrder(int subscriberId, String bookBarcode) {

		PreparedStatement stmt;
		try {
			stmt = mysqlConnection.conn.prepareStatement("INSERT INTO orders VALUES (?, ?, ?, ?)",
					PreparedStatement.RETURN_GENERATED_KEYS);

			stmt.setInt(1, subscriberId);
			stmt.setString(2, bookBarcode);
			stmt.setDate(3, new java.sql.Date(System.currentTimeMillis()));
			stmt.setInt(4, 0); // Initial status - pending (waiting)

			int CreatedOrderNum = stmt.executeUpdate();
			return CreatedOrderNum;
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
						+ rs.getString("book_barcode") + ", " + rs.getDate("order_requestedDate") + ", "
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
				return rs.getInt("order_number") + ", " + rs.getInt("subscriber_id") + ", "
						+ rs.getString("book_barcode") + ", " + rs.getInt("bookcopy_copyNo") + ", "
						+ rs.getDate("order_requestedDate") + ", " + rs.getInt("order_status") + ", "
						+ (rs.getDate("order_bookArrivedDate") == null ? "null" : rs.getDate("order_bookArrivedDate"));
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
	public static boolean updateOrderDetails(int orderNumber, int subscriberId, String bookBarcode, int copyNo,
			Date requestDate, int status, Date arrivedDate) {
		try {
			String query = "UPDATE Orders SET subscriber_id = ?, book_barcode = ?, "
					+ "bookcopy_copyNo = ?, order_requestedDate = ?, " + "order_status = ?, order_bookArrivedDate = ? "
					+ "WHERE order_number = ?";

			PreparedStatement pstmt = mysqlConnection.conn.prepareStatement(query);
			pstmt.setInt(1, subscriberId);
			pstmt.setString(2, bookBarcode);
			pstmt.setInt(3, copyNo);
			pstmt.setDate(4, requestDate);
			pstmt.setInt(5, status);
			if (arrivedDate != null) {
				pstmt.setDate(6, arrivedDate);
			} else {
				pstmt.setNull(6, Types.DATE);
			}
			pstmt.setInt(7, orderNumber);

			int rowsAffected = pstmt.executeUpdate();
			return rowsAffected > 0;
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
	public static List<String> GetAllMyOrders(String barcode) {
		String query = "SELECT * FROM orders WHERE book_barcode = ?";
		String bookcopyData = new String();
		List<String> foundOrders = new ArrayList<String>();
		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {

			stmt.setString(1, barcode);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int order_number = rs.getInt("order_number");
					int subscriber_id = rs.getInt("subscriber_id");
					String book_barcode = rs.getString("book_barcode");
					Date order_requestedDate = rs.getDate("order_requestedDate");
					int order_status = rs.getInt("order_status");
					Date order_bookArrivedDate = rs.getDate("order_bookArrivedDate");
					bookcopyData = order_number + ", " + subscriber_id + ", " + book_barcode + ", " + order_requestedDate + ", "
							+ order_status + ", " + order_bookArrivedDate;

					foundOrders.add(bookcopyData);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return foundOrders;
	}
	/////////////////////// END //////////////////////////////////
	///////////////////// --- Chen Orders Entity section ---///////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////


}
