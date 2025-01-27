package serverForHandleEntitiesRequests;

import java.sql.Date;
import java.util.HashMap;

import common.*;
import queries.queriesForLibrarian;
import queries.queriesForReminders;

public class remindersServer {

	public static Object handleRemindersRequests(HashMap<String, String> infoFromUser) {

		String menuChoiceString = (infoFromUser.keySet().iterator().next());
		String[] menuSplitString = menuChoiceString.split("\\+");
		RemindersMenu x = RemindersMenu.getSelectionFromEnumName(menuSplitString[1]);

		switch (x) {

		case GetNewOldReminders:
			String datatoReminder[] = infoFromUser.get(menuChoiceString).split(", ");
			return (queriesForReminders.GetNewOldReminders(Date.valueOf(datatoReminder[0]),
					Integer.parseInt(datatoReminder[1])));

		case CreateReminder:
			String NewReminderDetails[] = infoFromUser.get(menuChoiceString).split(", ");
			int ReminderCreateNumber = queriesForReminders.addNewReminderToDB(NewReminderDetails[0],
					Integer.parseInt(NewReminderDetails[1]), NewReminderDetails[2], NewReminderDetails[3],
					java.sql.Date.valueOf(NewReminderDetails[4]));
			if (ReminderCreateNumber > 0) {
				return (String.valueOf(ReminderCreateNumber));
			} else {
				return ("Error");
			}

		case GetReminder:
			String GetReminderDetails = infoFromUser.get(menuChoiceString);
			String MyReminder = queriesForReminders.GetReminder(Integer.valueOf(GetReminderDetails));
			return (MyReminder);

		case UpdateReminderDetails:
			String newDetailsToReminder[] = infoFromUser.get(menuChoiceString).split(", ");
			boolean reminderUpdated = queriesForReminders.updateReminderDetails(
					Integer.parseInt(newDetailsToReminder[0]), newDetailsToReminder[1],
					Integer.parseInt(newDetailsToReminder[2]), newDetailsToReminder[3], newDetailsToReminder[4],
					Date.valueOf(newDetailsToReminder[5]));
			if (reminderUpdated) {
				return ("Updated");
			} else {
				return ("Error");
			}

		default:
			break;
		}

		return "Error";
	}
}
