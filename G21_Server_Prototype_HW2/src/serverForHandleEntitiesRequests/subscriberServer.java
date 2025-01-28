package serverForHandleEntitiesRequests;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;

import common.*;
import queries.queriesForFrozenSubscribersRecords;
import queries.queriesForStatusReport;
import queries.queriesForSubscriber;

/**
 * This class is to handle the client requests about the subscriber table.
 */
public class subscriberServer {

	
	/**
	 * This method is getting the request from the server (that got from the client).
	 * and handling the client request to the DB.
	 * 
	 * @param infoFromUser - hashmap that we get from the client.
	 **
	 ** we are getting the request as hashmap from the client.
	 ** Key = EntityServer + request.
	 ** Value = Values that need to query. 
	 *
	 * @return String (OR) List<String> - very query returning different object
	 */
	public static Object handleSubscriberRequests(HashMap<String, String> infoFromUser) {

		String menuChoiceString = (infoFromUser.keySet().iterator().next());
		String[] menuSplitString = menuChoiceString.split("\\+");
		SubscriberMenu x = SubscriberMenu.getSelectionFromEnumName(menuSplitString[1]);

		switch (x) {

		// This case is getting the table from the SQL and sending to the client
		case ShowAllSubscribers:
			List<String> TheTable = queriesForSubscriber.GetSubscriberTable();
			return TheTable;
			
		// This case retrieves a list of subscriber IDs and names
		case GetSubscribersIDsAndNames:
			return (queriesForSubscriber.getSubscribersIDsAndNames());

		// This case adds a new frozen record for a subscriber	
		case AddingNewRecordOfFrozen:
			String[] newFrozenSubDataToAdd = infoFromUser.get(menuChoiceString).split(", ");
			return (queriesForFrozenSubscribersRecords.addNewFrozenRecordToDB(
					Integer.parseInt(newFrozenSubDataToAdd[0]), Date.valueOf(newFrozenSubDataToAdd[1]),
					Date.valueOf(newFrozenSubDataToAdd[2])));
		
		// This case retrieves the frozen report for a specific subscriber.
		case GetFrozenReportForSubscriber:
			String[] frozenSubDataToGet = infoFromUser.get(menuChoiceString).split(", ");
			return (queriesForFrozenSubscribersRecords.GetFrozenReportForSubscriber(
					Integer.parseInt(frozenSubDataToGet[0]), Integer.valueOf(frozenSubDataToGet[1]),
					Integer.valueOf(frozenSubDataToGet[2])));
			
	    // This case retrieves details of a specific frozen record for a subscriber.
		case GetSpecificFrozenRecord:
			String[] frozenRecordToSub = infoFromUser.get(menuChoiceString).split(", ");
			return (queriesForFrozenSubscribersRecords.getRecordOfFrozenDetails(Integer.parseInt(frozenRecordToSub[0]),
					Date.valueOf(frozenRecordToSub[1])));

		// This case updates an existing frozen record.	
		case UpdateRecordOfFrozen:
			String[] frozenRecordToUpdate = infoFromUser.get(menuChoiceString).split(", ");
			Boolean succToUpdateRecordBoolean = queriesForFrozenSubscribersRecords.updateRecordOfFrozen(
					Integer.parseInt(frozenRecordToUpdate[0]), Date.valueOf(frozenRecordToUpdate[1]),
					Date.valueOf(frozenRecordToUpdate[2]));
			if (succToUpdateRecordBoolean) {
				return ("Updated");
			} else {
				return ("Error");
			}
	
		// This case is getting the information to change from the user and saving in DB.
		case UpdateSubscriber:
			String idNinfo[] = infoFromUser.get(menuChoiceString).split(", ");
			boolean succ3 = queriesForSubscriber.updateSubscriverDetails(Integer.parseInt(idNinfo[0]), idNinfo[1],
					idNinfo[2], idNinfo[3], idNinfo[4], idNinfo[5], Date.valueOf(idNinfo[6]),
					idNinfo[7].equals("null") ? null : Date.valueOf(idNinfo[7]), Date.valueOf(idNinfo[8]));
			if (succ3) {
				return "Updated";
			} else {
				return "Error";
			}


		// This case is getting the information to add new user and saving in DB.
		case AddNewSubscriber:
			String idNinfoNew[] = infoFromUser.get(menuChoiceString).split(", ");
			boolean succ4 = queriesForSubscriber.addNewSubscriber(Integer.parseInt(idNinfoNew[0]), idNinfoNew[1],
					idNinfoNew[2], idNinfoNew[3], idNinfoNew[4], idNinfoNew[5]);
			if (succ4) {
				return "Added";
			} else {
				return "Error";
			}


		// This case is loading the requested ID from the DB and sending to the client.
		case GetSubscriberDetails:
			String RequestedIDToGet = queriesForSubscriber
					.getSubscriberDetails(Integer.parseInt(infoFromUser.get(menuChoiceString)));
			return RequestedIDToGet;

		// This case retrieves the Status Report.
		case GetStatusReport:
			String statusSubscriberReport[] = infoFromUser.get(menuChoiceString).split(", ");
			String statusReport = queriesForStatusReport.GetStatusReport(Integer.parseInt(statusSubscriberReport[0]),
					Integer.parseInt(statusSubscriberReport[1]));

			if (statusReport.contains(",")) {
				return (statusReport);
			} else {
				return (queriesForStatusReport.addNewReportToDB(Integer.parseInt(statusSubscriberReport[0]),
						Integer.parseInt(statusSubscriberReport[1])));
			}

	    // This case is for Update Subscriber sStatus Report and saving in DB.	
		case UpdateSubscribersStatusReport:
			String updateDetailesForReport[] = infoFromUser.get(menuChoiceString).split(", ");
			boolean succUpdateReport = queriesForStatusReport.updateSubscribersStatusReport(Integer.parseInt(updateDetailesForReport[0]), Integer.parseInt(updateDetailesForReport[1]),
					Integer.parseInt(updateDetailesForReport[2]),Integer.parseInt(updateDetailesForReport[3]),Integer.parseInt(updateDetailesForReport[4]),
					Integer.parseInt(updateDetailesForReport[5]),Integer.parseInt(updateDetailesForReport[6]),Integer.parseInt(updateDetailesForReport[7]));
			if (succUpdateReport) {
				return "Updated";
			} else {
				return "Error";
			}

		default:
			System.out.println("Error with the choise? = " + menuChoiceString);
			break;

		}

		return null;
	}
}
