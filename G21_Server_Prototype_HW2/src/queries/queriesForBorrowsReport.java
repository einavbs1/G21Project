package queries;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import Server.mysqlConnection;


/**
 * Handles borrow reports, allowing creation and retrieval of monthly reports.
 */
public class queriesForBorrowsReport {


	/**
     * Adds a new borrow report for the given month and year.
     *
     * @param month1 The month of the report (1-12).
     * @param year1  The year of the report.
     * @return The report data or an error message if a report already exists.
     */
	public static String addNewReportToDB(int month1, int year1) {
		PreparedStatement stmt;
		String statusReportData = "Empty";
		try {
			String dataTosave = queriesForBorrows.getBorrowsOfSpecificDate(month1, year1);
			if (dataTosave.equals("Empty")) {
				return dataTosave;
			}
			String[] dataToReport = dataTosave.split(", ");
			try {
				stmt = mysqlConnection.conn.prepareStatement(
						"INSERT INTO BorrowsReturnReport VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
				String thisMonth = GetStatusReport(month1, year1);
				
				if (!thisMonth.equals("Empty")) {
					return ("There is already exist report for this month and year = " + month1 + "." + year1);
				}
				stmt.setInt(1, month1);
				stmt.setInt(2, year1);
				stmt.setInt(3, Integer.parseInt(dataToReport[0]));
				stmt.setInt(4, Integer.parseInt(dataToReport[1]));
				stmt.setInt(5, Integer.parseInt(dataToReport[2]));
				stmt.setInt(6, Integer.parseInt(dataToReport[3]));
				stmt.setInt(7, Integer.parseInt(dataToReport[4]));
				stmt.setInt(8, Integer.parseInt(dataToReport[5]));
				stmt.executeUpdate();
				statusReportData = month1 + ", " + year1 + ", " + dataToReport[0] + ", " + dataToReport[1]
						 + ", " + dataToReport[2] + ", " + dataToReport[3] + ", " + dataToReport[4] + ", " + dataToReport[5];
				
				return statusReportData;
			} catch (SQLException e) {
				return e.getMessage();
			}
		} catch (Exception e) {
			return e.getMessage();
		}

	}

	/**
     * Retrieves the borrow report for the given month and year.
     *
     * @param month1 The month of the report (1-12).
     * @param year1  The year of the report.
     * @return The report data or "Empty" if no report exists.
     */
	public static String GetStatusReport(int month1, int year1) {
		String query = "SELECT * FROM BorrowsReturnReport WHERE borrowsReport_month = ? and borrowsReport_year = ?";
		String statusReportData = "Empty";
		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
			stmt.setInt(1, month1);
			stmt.setInt(2, year1);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int month = rs.getInt("borrowsReport_month");
					int year = rs.getInt("borrowsReport_year");
					int totalBorrows = rs.getInt("borrowsReport_totalBorrows");
					int returnedInTime = rs.getInt("borrowsReport_returnedInTime");
					int lateReturn = rs.getInt("borrowsReport_lateReturn");
					int returnedBeforeTime = rs.getInt("borrowsReport_returnedBeforeTime");
					int notReturnedYet = rs.getInt("borrowsReport_notReturnedYet");
					int lostBooks = rs.getInt("borrowsReport_lostBooks");

					statusReportData = month + ", " + year + ", " + totalBorrows + ", " + returnedInTime + ", "
							+ lateReturn + ", " + returnedBeforeTime + ", " + notReturnedYet + ", " + lostBooks;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return statusReportData;
	}

}
