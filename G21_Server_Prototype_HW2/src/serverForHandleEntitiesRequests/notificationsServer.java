package serverForHandleEntitiesRequests;

import java.sql.Date;
import java.util.HashMap;

import common.*;
import queries.queriesForLibrarian;
import queries.queriesForNotifications;

/**
 * This class is to handle the client requests about the notifications table.
 */
public class notificationsServer {

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
	public static Object handleNotificationsRequests(HashMap<String, String> infoFromUser) {

		String menuChoiceString = (infoFromUser.keySet().iterator().next());
		String[] menuSplitString = menuChoiceString.split("\\+");
		NotificationMenu x = NotificationMenu.getSelectionFromEnumName(menuSplitString[1]);

		switch (x) {

		case GetNewOldNotifications:
			Date newNotificationsFrom = Date.valueOf(infoFromUser.get(menuChoiceString));
			return (queriesForNotifications.GetNewOldNotifications(newNotificationsFrom));

		case CreateNotification:
			String NewNotificationDetails[] = infoFromUser.get(menuChoiceString).split(", ");
			int NotificationCreateNumber = queriesForNotifications.addNewNotificationToDB(NewNotificationDetails[0],
					Integer.parseInt(NewNotificationDetails[1]), java.sql.Date.valueOf(NewNotificationDetails[2]),
					Integer.parseInt(NewNotificationDetails[3]));
			if (NotificationCreateNumber > 0) {
				return (String.valueOf(NotificationCreateNumber));
			} else {
				return ("Error");
			}

		case GetNotification:
			String GetNotiDetails = infoFromUser.get(menuChoiceString);
			String MyNotification = queriesForNotifications.GetNotification(Integer.valueOf(GetNotiDetails));
			return (MyNotification);

		default:
			break;
		}

		return "Error";
	}
}
