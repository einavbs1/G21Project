package serverForHandleEntitiesRequests;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;

import common.*;
import queries.queriesForBookCopy;
import queries.queriesForBooks;
import queries.queriesForBorrows;
import queries.queriesForBorrowsReport;

public class borrowedRecordServer {

	public static Object handleBorrowedRecordRequests(HashMap<String, String> infoFromUser) {

		String menuChoiceString = (infoFromUser.keySet().iterator().next());
		String[] menuSplitString = menuChoiceString.split("\\+");
		BorrowedRecordMenu x = BorrowedRecordMenu.getSelectionFromEnumName(menuSplitString[1]);

		switch (x) {

		case GetBorrowRecord:
			String getBorrowedRecord = infoFromUser.get(menuChoiceString);
			String borrowedRecordTableByid = queriesForBorrows
					.getBorrowedRecordFromDB(Integer.parseInt(getBorrowedRecord));
			return (borrowedRecordTableByid);

		// This case is updating the expected borrowing time in the record from the
		// client and sending to the DB.
		case UpdateBorrowDetails:
			String updateBorrowRecordsdetails[] = infoFromUser.get(menuChoiceString).split(", ");

			boolean borrowRecordUpdateSuccess = queriesForBorrows.UpdateBorrowedRecord(
					Integer.parseInt(updateBorrowRecordsdetails[0]), Date.valueOf(updateBorrowRecordsdetails[6]),
					updateBorrowRecordsdetails[7].equals("null") ? null : Date.valueOf(updateBorrowRecordsdetails[7]),
					updateBorrowRecordsdetails[8].equals("null") ? null
							: Integer.valueOf(updateBorrowRecordsdetails[8]),
					updateBorrowRecordsdetails[9].equals("null") ? null : String.valueOf(updateBorrowRecordsdetails[9]),
					updateBorrowRecordsdetails[10].equals("null") ? null : Date.valueOf(updateBorrowRecordsdetails[10]),
					Integer.parseInt(updateBorrowRecordsdetails[11]), Integer.parseInt(updateBorrowRecordsdetails[12]),
					Integer.parseInt(updateBorrowRecordsdetails[13]));

			if (borrowRecordUpdateSuccess) {
				return ("Borrow record has been updated");
			} else {
				return ("Error");
			}

			// This case is getting new borrowing record from the client and saving in DB.
		case AddNewBorrow:
			String newBorrowRecordsdetails[] = infoFromUser.get(menuChoiceString).split(", ");
			int newBorrowNumber = queriesForBorrows.createNewBorrowedRecord(
					Integer.parseInt(newBorrowRecordsdetails[0]), newBorrowRecordsdetails[1],
					newBorrowRecordsdetails[2], Integer.parseInt(newBorrowRecordsdetails[3]),
					java.sql.Date.valueOf(newBorrowRecordsdetails[4]),
					java.sql.Date.valueOf(newBorrowRecordsdetails[5]), Integer.parseInt(newBorrowRecordsdetails[6]));

			if (newBorrowNumber > 0) {
				return (String.valueOf(newBorrowNumber));
			} else {
				return ("Error");
			}

		case SubscriberActiveBorrows:
			String getSubscriberId = infoFromUser.get(menuChoiceString);
			List<String> ActiveBorrowListToTable = queriesForBorrows
					.getAllSubscriberActiveBorrowRecordsFromDB(Integer.parseInt(getSubscriberId));
			return (ActiveBorrowListToTable);

		case GetBorrowsOfBookInSpecificDate:
			String specificBookDateBorrows[] = infoFromUser.get(menuChoiceString).split(", ");
			String specificBookDateBorrowsReport = queriesForBorrows.getBorrowsOfBookInSpecificDate(
					specificBookDateBorrows[0], Integer.parseInt(specificBookDateBorrows[1]),
					Integer.parseInt(specificBookDateBorrows[2]));
			return (specificBookDateBorrowsReport);

		case GetBorrowsReport:
			String borrowsReportdata[] = infoFromUser.get(menuChoiceString).split(", ");
			String borrowReport = queriesForBorrowsReport.GetStatusReport(Integer.parseInt(borrowsReportdata[0]),
					Integer.parseInt(borrowsReportdata[1]));
			if (borrowReport.contains(",")) {
				return (borrowReport);
			} else {
				return (queriesForBorrowsReport.addNewReportToDB(Integer.parseInt(borrowsReportdata[0]),
						Integer.parseInt(borrowsReportdata[1])));
			}

		default:
			break;
		}

		return "Error";
	}
}
