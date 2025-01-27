package serverForHandleEntitiesRequests;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;

import common.*;
import queries.queriesForActivityLogs;
import queries.queriesForLibrarian;

public class logActivityServer {

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
					Date.valueOf(activityData[5]) // activityDate
			);
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
