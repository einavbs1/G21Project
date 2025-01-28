package serverForHandleEntitiesRequests;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;

import common.*;
import queries.queriesForActivityLogs;
import queries.queriesForLibrarian;

/**
 * This class is to handle the client requests about the log Activity table.
 */
public class logActivityServer {

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
	public static Object handleLogActivityRequests(HashMap<String, String> infoFromUser) {

		String menuChoiceString = (infoFromUser.keySet().iterator().next());
		String[] menuSplitString = menuChoiceString.split("\\+");
		ActivityLogMenu x = ActivityLogMenu.getSelectionFromEnumName(menuSplitString[1]);

		switch (x) {

		case AddNewLogActivity:
			String[] activityData = infoFromUser.get(menuChoiceString).split(", ");
			int NewActivityNumber = queriesForActivityLogs.AddNewLogActivity(Integer.parseInt(activityData[0]), // subscriberId
					activityData[1], // activityAction
					activityData[2], // bookBarcode
					activityData[3], // bookTitle
					activityData[4].equals("null") ? null : Integer.parseInt(activityData[4]), // bookcopyCopyNo
					Date.valueOf(activityData[5]));
			if (NewActivityNumber > 0) {
				return (String.valueOf(NewActivityNumber));
			} else {
				return ("Error");
			}

		case LoadActivityById:
			List<String> activities = queriesForActivityLogs
					.LoadLogActivitybyid(Integer.parseInt(infoFromUser.get(menuChoiceString)));
			return (activities);

		case LoadActivityBySerial:
			String activity = queriesForActivityLogs
					.LoadLogActivityBySerialId(Integer.parseInt(infoFromUser.get(menuChoiceString)));
			return (activity);

		default:
			break;
		}

		return "Error";
	}
}
