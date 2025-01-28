package serverForHandleEntitiesRequests;

import java.util.HashMap;
import java.util.List;

import common.*;
import queries.queriesForBooks;
import queries.queriesForSubjects;

/**
 * This class is to handle the client requests about the books table.
 */
public class bookServer {

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
	public static Object handleBookRequests(HashMap<String, String> infoFromUser) {

		String menuChoiceString = (infoFromUser.keySet().iterator().next());
		String[] menuSplitString = menuChoiceString.split("\\+");
		BookMenu x = BookMenu.getSelectionFromEnumName(menuSplitString[1]);

		switch (x) {

		case GetBookBarcodesAndTitles:
			List<String> AllBooksinfoFromDB = queriesForBooks.getBookBarcodesAndTitles();
			return (AllBooksinfoFromDB);

		// This case is adding the book to the DB.
		case CreateBook:
			String NewBookDetails[] = infoFromUser.get(menuChoiceString).split(", ");
			boolean BookCreatesucc = queriesForBooks.addNewBookToDB(NewBookDetails[0], NewBookDetails[1],
					NewBookDetails[2], NewBookDetails[3], Integer.parseInt(NewBookDetails[4]),
					Integer.parseInt(NewBookDetails[5]), Integer.parseInt(NewBookDetails[6]),
					Integer.parseInt(NewBookDetails[7]), NewBookDetails[8]);
			if (BookCreatesucc) {
				return ("book has been added");
			} else {
				return ("Error");
			}

			// This case is getting book from DB with barcode and returning to client.
		case GetBook:
			String RequestedBookFromDB = queriesForBooks.GetBookFromDB(infoFromUser.get(menuChoiceString));
			return (RequestedBookFromDB);

		// This case is updating the book details in the DB.
		case UpdateBookDetails:
			String UpdateBookDetailes[] = infoFromUser.get(menuChoiceString).split(", ");
			int len = UpdateBookDetailes.length;
			StringBuilder descriptionBuilder = new StringBuilder();
			for (int i = 3; i < len - 5; i++) {
				descriptionBuilder.append(UpdateBookDetailes[i]);
				if (i != len - 6) {
					descriptionBuilder.append(", ");
				}
			}

			boolean UpdateBookSucc = queriesForBooks.updateBookDetails(UpdateBookDetailes[0], UpdateBookDetailes[1],
					UpdateBookDetailes[2], descriptionBuilder.toString(), Integer.parseInt(UpdateBookDetailes[len - 5]),
					Integer.parseInt(UpdateBookDetailes[len - 4]), Integer.parseInt(UpdateBookDetailes[len - 3]),
					Integer.parseInt(UpdateBookDetailes[len - 2]), UpdateBookDetailes[len - 1]);
			if (UpdateBookSucc) {
				return ("book has been updated");
			} else {
				return ("Error");
			}

			//
		case GetAllMyCopies:
			String bookBarcodeNeedsCopies = infoFromUser.get(menuChoiceString);
			List<String> allMycopies = queriesForBooks.GetAllMyCopies(bookBarcodeNeedsCopies);
			return (allMycopies);

		case SearchBookByName:
			String bookNameToSearch = infoFromUser.get(menuChoiceString);
			List<String> allBooksByName = queriesForBooks.SearchBooksByName(bookNameToSearch);
			return (allBooksByName);

		case SearchBookBySubject:
			String subjectToSearch = infoFromUser.get(menuChoiceString);
			List<String> allBooksBySubject = queriesForBooks.SearchBooksBySubject(subjectToSearch);
			return (allBooksBySubject);

		case SearchBookByDescription:
			String DescriptionToSearch = infoFromUser.get(menuChoiceString);
			List<String> allBooksByDescription = queriesForBooks.SearchBooksByDescription(DescriptionToSearch);
			return (allBooksByDescription);

		case GetAllSubjects:
			List<String> allSubjectsList = queriesForSubjects.getAllSubjects();
			return (allSubjectsList);

		default:
			System.out.println("Error with the choise? = " + menuChoiceString);
			break;

		}

		return null;
	}
}
