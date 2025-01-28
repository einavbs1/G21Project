package queries;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Server.mysqlConnection;

public class queriesForSubjects {
	
	
	
	/**
	 * Author: Einav This method is returning lift of all subjects.
	 * 
	 * @return List of all subjects 
	 */
	public static List<String> getAllSubjects() {
		String query = "SELECT * FROM bookssubjects";
		List<String> listOfSubjects = new ArrayList<String>();
		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					String subject_name = rs.getString("subject_name");
					listOfSubjects.add(subject_name);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return listOfSubjects;
	}
	

}
