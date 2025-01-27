package serverForHandleEntitiesRequests;

import java.util.HashMap;
import java.util.List;

import common.*;
import queries.queriesForBookCopy;
import queries.queriesForBooks;

public class bookCopyServer {

	public static Object handleBookCopyRequests(HashMap<String, String> infoFromUser) {

		String menuChoiceString = (infoFromUser.keySet().iterator().next());
		String[] menuSplitString = menuChoiceString.split("\\+");
		BookCopyMenu x = BookCopyMenu.getSelectionFromEnumName(menuSplitString[1]);

		switch (x) {

		// This case is adding the bookCopy to the DB.
				case CreateBookCopy:
					String NewBookCopyDetails[] = infoFromUser.get(menuChoiceString).split(", ");
					boolean BookCopyCreatesucc = queriesForBookCopy.addNewBookCopyToDB(NewBookCopyDetails[0],
							Integer.parseInt(NewBookCopyDetails[1]), Integer.parseInt(NewBookCopyDetails[2]),
							Integer.parseInt(NewBookCopyDetails[3]), java.sql.Date.valueOf(NewBookCopyDetails[4]),
							Integer.parseInt(NewBookCopyDetails[5]));
					if (BookCopyCreatesucc) {
						return ("book has been added");
					} else {
						return("Error");
					}

				// This case is getting book from DB with barcode and copyNo and returning to
				// client.
				case GetBookCopy:
					String GetBookCopyDetails[] = infoFromUser.get(menuChoiceString).split(", ");
					String RequestedBookCopyFromDB = queriesForBookCopy.GetBookCopyFromDB(GetBookCopyDetails[0],
							Integer.parseInt(GetBookCopyDetails[1]));
					return(RequestedBookCopyFromDB);

				// This case is updating the bookCopy details in the DB.
				case UpdateBookCopyDetails:
					String UpdateBookCopyDetailes[] = infoFromUser.get(menuChoiceString).split(", ");
					boolean UpdateBookCopySucc = queriesForBookCopy.updateBookCopyDetails(UpdateBookCopyDetailes[0],
							Integer.parseInt(UpdateBookCopyDetailes[1]), Integer.parseInt(UpdateBookCopyDetailes[2]),
							Integer.parseInt(UpdateBookCopyDetailes[3]),
							UpdateBookCopyDetailes[4].equals("null") ? null : java.sql.Date.valueOf(UpdateBookCopyDetailes[4]),
							UpdateBookCopyDetailes[5].equals("null") ? null : Integer.parseInt(UpdateBookCopyDetailes[5]));
					if (UpdateBookCopySucc) {
						return("bookCopy has been updated");
					} else {
						return("Error");
					}
					
					default:
						break;
		}
		
		return "Error";
	}
}
