package queries;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Server.mysqlConnection;

public class queriesForLibrarian {

	//////////////////////////////////////////////////////////////////////////////////////////
	///////////////////// --- Yuval librarian Entity
	////////////////////////////////////////////////////////////////////////////////////////// section---///////////////////////

	/**
	 * Author: Yuval. This method adds a new librarian to the database.
	 * 
	 * @param librarian_id       - librarian ID (PK)
	 * @param librarian_name     - name of the librarian
	 * @param librarian_password - password of librarian
	 * @return True if success to add the librarian
	 */
	public static boolean addNewLibrarian(int librarian_id, String librarian_name, String librarian_password) {
		PreparedStatement stmt;
		try {
			stmt = mysqlConnection.conn.prepareStatement("INSERT INTO librarian VALUES (?, ?, ?)");

			stmt.setInt(1, librarian_id);
			stmt.setString(2, librarian_name);
			stmt.setString(3, librarian_password);

			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Author: Yuval. This method is getting information to change in the DB
	 * 
	 * @param librarian_id       - librarian ID (PK)
	 * @param librarian_name     - name of the librarian
	 * @param librarian_password - password of librarian
	 * @return True if success to save the changes
	 */
	public static boolean updateLibrarianDetails(int librarian_id, String librarian_name, String librarian_password, Date lastCheckedNotifications) {
			
			String query = "UPDATE librarian SET librarian_name = ?, librarian_password = ?, librarian_lastCheckedNotifications = ?"
					+ " WHERE librarian_id = ?";
			
			try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
				stmt.setString(1, librarian_name);
				stmt.setString(2, librarian_password);
				stmt.setDate(3, lastCheckedNotifications);
				
				stmt.setInt(4, librarian_id);

				stmt.executeUpdate();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
	}

	/**
	 * Author: Yuval. This method is getting Librarian ID and returning String of
	 * his data
	 * 
	 * @param libidtoload - Librarian ID that client ask his details
	 * @return String of this Librarian
	 */
	public static String getLibrarianDetails(int libidtoload) {
		String query = "SELECT * FROM librarian WHERE  librarian_id = ?";
		String librarianData = new String("Empty");
		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
			stmt.setInt(1, libidtoload);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int librarian_id = rs.getInt("librarian_id");
					String librarian_name = rs.getString("librarian_name");
					String librarian_password = rs.getString("librarian_password");
					Date librarian_lastCheckedNotifications = rs.getDate("librarian_lastCheckedNotifications");

					// Create a formatted string with the subscriber's information
					librarianData = librarian_id + ", " + librarian_name + ", " + librarian_password + ", " + librarian_lastCheckedNotifications;
					return librarianData;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return librarianData;
	}

	/////////////////////// END //////////////////////////////////
	///////////////////// --- Yuval librarian Entity section
	/////////////////////// ---///////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////

	
}
