package serverForHandleEntitiesRequests;

import java.sql.Date;
import java.util.HashMap;

import common.*;
import queries.queriesForLibrarian;
import queries.queriesForNotifications;

public class notificationsServer {

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
