package queries;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Server.mysqlConnection;


/**
 * Handles operations related to subscriber status reports in the database.
 */
public class queriesForStatusReport {


	/**
     * Adds a new subscriber status report to the database.
     *
     * @param month1 The month of the report.
     * @param year1  The year of the report.
     * @return The status report details or an error message if it already exists.
     */
	public static String addNewReportToDB(int month1, int year1) {
		PreparedStatement stmt;
		String statusReportData = "Empty";
		try {
			stmt = mysqlConnection.conn.prepareStatement(
					"INSERT INTO SubscribersStatusReport VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
					PreparedStatement.RETURN_GENERATED_KEYS);

			Map<String, Integer> statusCounts = new HashMap<>();
			String thisMonth = GetStatusReport(month1, year1);
			if (!thisMonth.equals("Empty")) {
				return ("There is already exist report for this month and year = " + month1 + "." + year1);
			}
			Calendar currectCalendar = Calendar.getInstance();
			if ((month1 == (currectCalendar.get(Calendar.MONTH) + 1) && year1 == currectCalendar.get(Calendar.YEAR))) {
				statusCounts = queriesForSubscriber.GetSubscriberStatusCount();
			}
			int activeCount = statusCounts.getOrDefault("Active", 0);
			int frozenCount = statusCounts.getOrDefault("Frozen", 0);
			int total = activeCount + frozenCount;

			stmt.setInt(1, month1);
			stmt.setInt(2, year1);
			stmt.setInt(3, activeCount);
			stmt.setInt(4, frozenCount);
			stmt.setInt(5, 0);
			stmt.setInt(6, 0);
			stmt.setInt(7, 0);
			stmt.setInt(8, total);

			stmt.executeUpdate();
			statusReportData = month1 + ", " + year1 + ", " + activeCount + ", " + frozenCount + ", 0, 0, 0, " + total;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return statusReportData;
	}

	
	/**
     * Retrieves a subscriber status report from the database.
     *
     * @param month1 The month of the report.
     * @param year1  The year of the report.
     * @return The status report details or "Empty" if not found.
     */
	public static String GetStatusReport(int month1, int year1) {
		String query = "SELECT * FROM SubscribersStatusReport WHERE statusReport_month = ? and statusReport_year = ?";
		String statusReportData = "Empty";
		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
			stmt.setInt(1, month1);
			stmt.setInt(2, year1);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int month = rs.getInt("statusReport_month");
					int year = rs.getInt("statusReport_year");
					int totalActiveSubscribers = rs.getInt("statusReport_totalActiveSubscribers");
					int totalFrozenSubscribers = rs.getInt("statusReport_totalFrozenSubscribers");
					int gotFrozeSubscribers = rs.getInt("statusReport_gotFrozeSubscribers");
					int unfrozedSubscribers = rs.getInt("statusReport_unfrozedSubscribers");
					int newSubscribers = rs.getInt("statusReport_newSubscribers");
					int totalSubscribers = rs.getInt("statusReport_totalSubscribers");

					statusReportData = month + ", " + year + ", " + totalActiveSubscribers + ", "
							+ totalFrozenSubscribers + ", " + gotFrozeSubscribers + ", " + unfrozedSubscribers + ", "
							+ newSubscribers + ", " + totalSubscribers;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return statusReportData;
	}

	
	/**
     * Updates a subscriber status report in the database.
     *
     * @param month1    The month of the report.
     * @param year1     The year of the report.
     * @param active    The number of active subscribers.
     * @param frozen    The number of frozen subscribers.
     * @param gotfroze  The number of subscribers who were frozen.
     * @param unfroze   The number of subscribers who were unfrozen.
     * @param newSubs   The number of new subscribers.
     * @param totalSubs The total number of subscribers.
     * @return True if the update was successful, false otherwise.
     */
	public static boolean updateSubscribersStatusReport(int month1, int year1, int active, int frozen, int gotfroze,
			int unfroze, int newSubs, int totalSubs) {

		String query = "UPDATE SubscribersStatusReport SET statusReport_totalActiveSubscribers = ?, statusReport_totalFrozenSubscribers = ?"
				+ ", statusReport_gotFrozeSubscribers = ?, statusReport_unfrozedSubscribers = ?, statusReport_newSubscribers = ?, statusReport_totalSubscribers = ?"
				+ " WHERE statusReport_month = ? AND statusReport_year = ?";
		try (PreparedStatement stmt = mysqlConnection.conn.prepareStatement(query)) {
			stmt.setInt(1, active);
			stmt.setInt(2, frozen);
			stmt.setInt(3, gotfroze);
			stmt.setInt(4, unfroze);
			stmt.setInt(5, newSubs);
			stmt.setInt(6, totalSubs);

			stmt.setInt(7, month1);
			stmt.setInt(8, year1);

			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}


}
