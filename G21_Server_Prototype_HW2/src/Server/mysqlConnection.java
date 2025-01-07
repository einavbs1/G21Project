package Server;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/*
 * This class is connect to mySQL DB for G21-prototype server. 
 */
public class mysqlConnection {

	public static Connection conn;

	/*
	 * This method is connecting to out G21-prototype server
	 */
	public static String connectToDB() {
		String ret ="";
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			ret = ret + ("Driver definition succeed");

		} catch (Exception ex) {
			/* handle the error */
			ret = ret + ("Driver definition failed");
		}

		try {
			conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/g21_project_schema?serverTimezone=IST", "root",
					"Aa123456");
			ret = ret + ("SQL connection succeed");
		} catch (SQLException ex) {/* handle any errors */
			
			ret = ret + ("SQLException: " + ex.getMessage());
			ret = ret + ("\nSQLState: " + ex.getSQLState());
			ret = ret + ("\nVendorError: " + ex.getErrorCode());
		}
		return ret;
		
	}

	/**
	 * This method is getting information to change in the DB 
	 * @param id		- subscriber ID (PK)
	 * @param phonenum	- Phone number to change to the subscriber
	 * @return True if success to save the changes
	 */
	public static boolean updatephone(int id, String phonenum) {
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("UPDATE subscriber SET subscriber_phone_number = \"" + phonenum
					+ "\" WHERE  subscriber_id = \"" + id + "\"");
			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * This method is getting information to change in the DB 
	 * @param id		- subscriber ID (PK)
	 * @param email		- Email address to change to the subscriber
	 * @return True if success to save the changes
	 */
	public static boolean updatemail(int id, String email) {
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(
					"UPDATE subscriber SET subscriber_email = \"" + email + "\" WHERE  subscriber_id = \"" + id + "\"");
			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * This method is getting ID and returning String of his data
	 * @param idtoload	- ID that client ask his details
	 * @return String of this subscriber
	 */
	public static String Load(int idtoload) {
		String query = "SELECT * FROM subscriber WHERE  subscriber_id = \"" + idtoload + "\"";
		String subscriberData = new String("Empty");
		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
			while (rs.next()) {
				int id = rs.getInt("subscriber_id");
				String name = rs.getString("subscriber_name");
				int history = rs.getInt("detailed_subscription_history");
				String phoneNumber = rs.getString("subscriber_phone_number");
				String email = rs.getString("subscriber_email");

				// Create a formatted string with the subscriber's information
				subscriberData = id + ", " + name + ", " + history + ", " + phoneNumber + ", " + email;
				return subscriberData;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return subscriberData;

	}

	
	/**
	 * This method is returning the list of subscribers from the DB to the client
	 * @return List of the subscribers
	 */
	public static List<String> GetSubscriberTable() {
		List<String> subscribers = new ArrayList<>();
		String query = "SELECT * FROM subscriber";

		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
			while (rs.next()) {
				int id = rs.getInt("subscriber_id");
				String name = rs.getString("subscriber_name");
				int history = rs.getInt("detailed_subscription_history");
				String phoneNumber = rs.getString("subscriber_phone_number");
				String email = rs.getString("subscriber_email");

				// Create a formatted string with the subscriber's information
				String subscriberData = id + ", " + name + ", " + history + ", " + phoneNumber + ", " + email;

				// Add the formatted string to the list
				subscribers.add(subscriberData);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return subscribers;
	}
	
	
	
	
	
	
   
	
	/**
	 * * /****Author: chen tsafir
	 * Creates a new order in the database.
	 *
	 * @param subscriberId  The ID of the subscriber making the order
	 * @param bookBarcode  The barcode of the book being ordered
	 * @return int         The new order number if successful, -1 if failed
	 * 
	 * Note: This method automatically:
	 * - Generates the next available order number
	 * - Sets the request date to current date
	 * - Sets the initial status to 0 (pending)
	 */
    public static int createOrder(int subscriberId, String bookBarcode) {
        try {
            // Get the next order number by add 1 to the last max order (if there is no order in DB - the next order will be 1)
        	Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(order_number) FROM Orders");
            int nextOrderNum = 1;
            if (rs.next()) {
                int maxOrderNum = rs.getInt(1);
                if (!rs.wasNull()) {
                    nextOrderNum = maxOrderNum + 1;
                }
            }

            // Create the new order
            String query = "INSERT INTO Orders (order_number, subscriber_id, book_barcode, order_requestedDate, order_status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, nextOrderNum);
            pstmt.setInt(2, subscriberId);
            pstmt.setString(3, bookBarcode);
            pstmt.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            pstmt.setInt(5, 0); // Initial status - pending (waiting)

            pstmt.executeUpdate();
            return nextOrderNum;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    

    /**
     * Retrieves all orders from the database. (chen tsafir)
     * 
     * @return List<String> A list of strings, each representing an order with its details
     *                     in the format: "orderNumber, subscriberId, bookBarcode, 
     *                     requestDate, status, arrivedDate"
     */
    public static List<String> GetOrdersTable() {
        List<String> orders = new ArrayList<>();
        String query = "SELECT * FROM Orders";

        try (Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String orderData = rs.getInt("order_number") + ", " +
                                 rs.getInt("subscriber_id") + ", " +
                                 rs.getString("book_barcode") + ", " +
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
     * Loads a specific order from the database. (chen tsafir)
     * 
     * @param orderNumber The unique identifier of the order to load
     * @return String     The order details as a comma-separated string, or "Empty" if not found
     */

    public static String LoadOrder(int orderNumber) {
        String query = "SELECT * FROM Orders WHERE order_number = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, orderNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("order_number") + ", " +
                       rs.getInt("subscriber_id") + ", " +
                       rs.getString("book_barcode") + ", " +
                       rs.getInt("bookcopy_copyNo") + ", " +
                       rs.getDate("order_requestedDate") + ", " +
                       rs.getInt("order_status") + ", " +
                       (rs.getDate("order_bookArrivedDate") == null ? "null" : rs.getDate("order_bookArrivedDate"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Empty";
    }
    
    
    

    /**
     * Updates the status of an existing order in the database. (chen tsafir)
     * 
     * @param orderNumber  The unique identifier of the order
     * @param newStatus   The new status to set (-1: cancelled, 0: pending, 1: fulfilled)
     * @return boolean    True if status was updated successfully, false otherwise
     */
    public static boolean updateOrderStatus(int orderNumber, int newStatus) {
        try {
            String query = "UPDATE Orders SET order_status = ? WHERE order_number = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            
            pstmt.setInt(1, newStatus);
            pstmt.setInt(2, orderNumber);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;  // return true if the status update secsessfully
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    
    /**
     * Updates an existing order's details in the database. (chen tsafir)
     * 
     * @param orderNumber    The unique identifier of the order to update
     * @param subscriberId   The ID of the subscriber associated with the order
     * @param bookBarcode    The barcode of the book being ordered
     * @param copyNo        The copy number of the book
     * @param requestDate   The date when the order was requested
     * @param status       The new status of the order (-1: cancelled, 0: pending, 1: fulfilled)
     * @param arrivedDate  The date when the book arrived (can be null)
     * @return boolean     True if update was successful, false otherwise
     */
	public static boolean updateOrder(int orderNumber, int subscriberId, String bookBarcode, int copyNo,
			Date requestDate, int status, Date arrivedDate) {
		try {
			String query = "UPDATE Orders SET subscriber_id = ?, book_barcode = ?, "
					+ "bookcopy_copyNo = ?, order_requestedDate = ?, " + "order_status = ?, order_bookArrivedDate = ? "
					+ "WHERE order_number = ?";

			PreparedStatement pstmt = conn.prepareStatement(query);
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

}
