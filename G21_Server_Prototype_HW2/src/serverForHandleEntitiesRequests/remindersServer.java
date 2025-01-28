package serverForHandleEntitiesRequests;

import java.sql.Date;
import java.util.HashMap;

import common.*;
import queries.queriesForLibrarian;
import queries.queriesForReminders;

/**
 * This class is to handle the client requests about the reminders Server table.
 */
public class remindersServer {

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
	public static Object handleRemindersRequests(HashMap<String, String> infoFromUser) {

		String menuChoiceString = (infoFromUser.keySet().iterator().next());
		String[] menuSplitString = menuChoiceString.split("\\+");
		RemindersMenu x = RemindersMenu.getSelectionFromEnumName(menuSplitString[1]);

		switch (x) {

		 // Retrieves reminders based on date and status (new/old).
		case GetNewOldReminders:
			String datatoReminder[] = infoFromUser.get(menuChoiceString).split(", ");
			return (queriesForReminders.GetNewOldReminders(Date.valueOf(datatoReminder[0]),
					Integer.parseInt(datatoReminder[1])));

		// Creates a new reminder entry in the database.
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

		// Retrieves a specific reminder by its ID.
		case GetReminder:
			String GetReminderDetails = infoFromUser.get(menuChoiceString);
			String MyReminder = queriesForReminders.GetReminder(Integer.valueOf(GetReminderDetails));
			return (MyReminder);

		// Updates details of an existing reminder in the database.
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
