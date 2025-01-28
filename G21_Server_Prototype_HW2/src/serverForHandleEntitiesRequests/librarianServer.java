package serverForHandleEntitiesRequests;

import java.sql.Date;
import java.util.HashMap;

import common.*;
import queries.queriesForLibrarian;

/**
 * This class is to handle the client requests about the librarian table.
 */
public class librarianServer {

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
	public static Object handleLibrarianRequests(HashMap<String, String> infoFromUser) {

		String menuChoiceString = (infoFromUser.keySet().iterator().next());
		String[] menuSplitString = menuChoiceString.split("\\+");
		LibrarianMenu x = LibrarianMenu.getSelectionFromEnumName(menuSplitString[1]);

		switch (x) {

		// Author: Yuval.
		// This case is getting the information to change from the user and saving in
		// DB.
		case UpdateLibrarian:
			String idNlibinfo[] = infoFromUser.get(menuChoiceString).split(", ");
			boolean succ5 = queriesForLibrarian.updateLibrarianDetails(Integer.parseInt(idNlibinfo[0]), idNlibinfo[1],
					idNlibinfo[2], Date.valueOf(idNlibinfo[3]));
			if (succ5) {
				return ("Updated");
			} else {
				return ("Error");
			}

			// Author: Yuval.
			// This case is getting the information to add new Librarian and saving in DB.
		case AddNewLibrarian:
			String idNinfoNewlib[] = infoFromUser.get(menuChoiceString).split(", ");
			boolean succ6 = queriesForLibrarian.addNewLibrarian(Integer.parseInt(idNinfoNewlib[0]), idNinfoNewlib[1],
					idNinfoNewlib[2]);
			if (succ6) {
				return ("Added");
			} else {
				return ("Error");
			}

			// Author: Yuval.
			// This case is loading the requested Librarian ID from the DB and sending to
			// the client.
		case GetLibrarianDetails:
			String RequestedLibToGet = queriesForLibrarian
					.getLibrarianDetails(Integer.parseInt(infoFromUser.get(menuChoiceString)));
			return (RequestedLibToGet);

		default:
			break;
		}

		return "Error";
	}
}
