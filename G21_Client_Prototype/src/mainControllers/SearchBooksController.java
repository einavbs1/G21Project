package mainControllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import client.ChatClient;
import client.ClientUI;
import entity.*;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;


/*
 * This class is the controller of search books screen.
 * dividing to 3 search options:
 * 	1. search by name.
 *	2. search by subject of the book
 *	3. search by description of the book, in this case can enter keywords (or sentences) to search and not only one sentence.
 */
public class SearchBooksController {

	private static List<String> AvailableBooks;
	private static List<String> UnavailableBooks;

	@FXML
	private Button btnBack = null;
	@FXML
	private Button btnExit = null;
	@FXML
	private Button btnSearchName = null;
	@FXML
	private Button btnSearchSubject = null;
	@FXML
	private Button btnAddKeyWord = null;
	@FXML
	private Button btnSearchDescription = null;

	@FXML
	private ComboBox<String> comboBoxSubjectSelect;

	@FXML
	private Label lblBookName;
	@FXML
	private Label lblBookSubject;
	@FXML
	private Label lblBookDescription;

	@FXML
	private TextField txtBookName;
	@FXML
	private TextField txtKeyWord;

	@FXML
	private Label lblinstruction;
	@FXML
	private Label lblerrmsg;

	@FXML
	private FlowPane flowPaneDescriptionTags;
	
	///////////////////////////////////////////////////////////////////
	//getters for the search results for the  searchBookResults GUI 
	public static List<String> getAvailableBooks() {
		return AvailableBooks;
	}
	public static List<String> getUnavailableBooks() {
		return UnavailableBooks;
	}
	///////////////////////////////////////////////////////////////////
	
	/*
	 * This method is setting the ComboBox with all the subjects
	 */
	private void setSubjectsComboBox() {
		List<String> allSubjects = new ArrayList<String>();
		allSubjects.add(" ");
		allSubjects.addAll(Subjects.getAllSubjects());
		comboBoxSubjectSelect.getItems().addAll(allSubjects);
		comboBoxSubjectSelect.setValue(" "); // Default selected value
	}

	/*
	 * initialize the gui with the combo box options
	 */
	public void initialize() {
		setSubjectsComboBox();
	}

	/**
	 * This method is returning the selected subject as a string.
	 */
	private String getSubjectSelection() {
		return (String) comboBoxSubjectSelect.getValue();
	}

	/**
	 * This method is return the previous screen:
	 * 	1. if librarian logged in, go back to her menu.
	 * 	2. if subscriber logged in, go back to his menu.
	 * 	3. else - backing to the main menu because it was a guest login.
	 * @param event - the click on the back button action
	 */
	public void BackToMainScreen(ActionEvent event) {
		if (ChatClient.getCurrectLibrarian() != null) {

			try {
				FXMLLoader Loader = new FXMLLoader(getClass().getResource("/librarianGui/LibrarianMenu.fxml"));
				Parent Root = Loader.load();
				Stage Stage = new Stage();
				Scene Scene = new Scene(Root);
				Scene.getStylesheets().add(getClass().getResource("/librarianGui/LibrarianMenu.css").toExternalForm());
				Stage.setScene(Scene);
				Stage.setTitle("Librarian Menu");
				Stage.show();
				((Node) event.getSource()).getScene().getWindow().hide();
			} catch (Exception e) {
			}

		} else if (ChatClient.getCurrectSubscriber() != null) {

			try {
				FXMLLoader Loader = new FXMLLoader(getClass().getResource("/subscriberGui/SubscriberMenu.fxml"));
				Parent Root = Loader.load();
				Stage Stage = new Stage();
				Scene Scene = new Scene(Root);
				Scene.getStylesheets().add(getClass().getResource("/subscriberGui/SubscriberMenu.css").toExternalForm());
				Stage.setScene(Scene);
				Stage.setTitle("Subscriber Menu");
				Stage.show();
				((Node) event.getSource()).getScene().getWindow().hide();
			} catch (Exception e) {
			}

		} else {
			try {
				FXMLLoader Loader = new FXMLLoader(getClass().getResource("/mainGui/MainLoginScreen.fxml"));
				Parent Root = Loader.load();
				Stage Stage = new Stage();
				Scene Scene = new Scene(Root);
				Scene.getStylesheets().add(getClass().getResource("/mainGui/MainLoginScreen.css").toExternalForm());
				Stage.setScene(Scene);
				Stage.setTitle("Main Login Screen");
				Stage.show();
				((Node) event.getSource()).getScene().getWindow().hide();
			} catch (Exception e) {
			}
		}
	}

	/////////////////////////////////////////////
	/**
	 *Private enum to make a options to search books: 
	 */
	private enum MyBookSearchType {
		SearchByName,
		SearchBySubject,
		AddKeyWord,
		SearchByDescription;
	}
	/////////////////////////////////////////////
	/**
	 * This method is starting to search the book by the name if clicked and user entered word to search.
	 * @param event - the click on search by name button action
	 */
	public void SearchByName(ActionEvent event) {
		boolean hasVal = VerifyInput(MyBookSearchType.SearchByName);
		if (hasVal) {
			List<String> theRes = Book.SearchBookByName(txtBookName.getText());
			if (!theRes.isEmpty()) {
				HandlBooksList(theRes);
				System.out.println(theRes);
				try {
					FXMLLoader Loader = new FXMLLoader(getClass().getResource("/mainGui/SearchBookResults.fxml"));
					Parent Root = Loader.load();
					Stage Stage = new Stage();
					Scene Scene = new Scene(Root);
					Scene.getStylesheets().add(getClass().getResource("/mainGui/SearchBookResults.css").toExternalForm());
					Stage.setScene(Scene);
					Stage.setTitle("Search Book Results");
					Stage.show();
					((Node) event.getSource()).getScene().getWindow().hide();
				} catch (Exception e) {

				}
			} else {
				changeString("Results not found, please try again");
			}
		}
	}

	/**
	 * This method is starting to search the book by the subject if selected from the dropDown choices.
	 * @param event - the click on search by subject button action
	 */
	public void SearchBySubject(ActionEvent event) {
		boolean hasVal = VerifyInput(MyBookSearchType.SearchBySubject);
		if (hasVal) {
			List<String> theRes = Book.SearchBookBySubject(getSubjectSelection());
			if (!theRes.isEmpty()) {
				HandlBooksList(theRes);
				try {
					FXMLLoader Loader = new FXMLLoader(getClass().getResource("/mainGui/SearchBookResults.fxml"));
					Parent Root = Loader.load();
					Stage Stage = new Stage();
					Scene Scene = new Scene(Root);
					Scene.getStylesheets().add(getClass().getResource("/mainGui/SearchBookResults.css").toExternalForm());
					Stage.setScene(Scene);
					Stage.setTitle("Search Book Results");
					Stage.show();
					((Node) event.getSource()).getScene().getWindow().hide();
				} catch (Exception e) {

				}
			} else {
				changeString("Results not found, please try again");
			}
		}
	}

	/**
	 * This method is starting to search the book by the description if clicked and user entered at least one keyword to search.
	 * @param event - the click on search by description button action
	 */
	public void SearchByDescription(ActionEvent event) {
		boolean hasVal = VerifyInput(MyBookSearchType.SearchBySubject);
		if (hasVal) {
			List<String> theRes = Book.SearchBookByDescription(getAllFlowPaneTags());
			if (!theRes.isEmpty()) {
				HandlBooksList(theRes);
				try {
					FXMLLoader Loader = new FXMLLoader(getClass().getResource("/mainGui/SearchBookResults.fxml"));
					Parent Root = Loader.load();
					Stage Stage = new Stage();
					Scene Scene = new Scene(Root);
					Scene.getStylesheets().add(getClass().getResource("/mainGui/SearchBookResults.css").toExternalForm());
					Stage.setScene(Scene);
					Stage.setTitle("Search Book Results");
					Stage.show();
					((Node) event.getSource()).getScene().getWindow().hide();
				} catch (Exception e) {

				}
			} else {
				changeString("Results not found, please try again");
			}
		}
	}

	
	/**
	 * private method that verifing the input before continue to send request to the server to search the books.
	 * 1. if clicked on search by name - verify that there is a name.
	 * 2. if clicked on search by subject - verify that subject has been selected.
	 * 3. if clicked on search by description - verify that there is at least one keyword.
	 * 4. if want to add a keyword to the description search - verify that there is text.
	 * 
	 * @param type - the book search type (from MyBookSearchType Enum).
	 * @return true if there are no issues and false if there are missing fields.
	 */
	private boolean VerifyInput(MyBookSearchType type) {

		switch (type) {

		case SearchByName:
			if (txtBookName.getText().isEmpty()) {
				changeString("You must enter book name to search by name");
			} else {
				return true;
			}
			break;

		case SearchBySubject:
			if (getSubjectSelection().isEmpty()) {
				changeString("You must select subject");
			} else {
				return true;
			}
			break;

		case AddKeyWord:
			if (txtKeyWord.getText().isEmpty()) {
				changeString("You must enter keyword to add");
			} else {
				return true;
			}
			break;
		case SearchByDescription:
			if (getAllFlowPaneTags().isEmpty()) {
				changeString("You must enter at least one keyword to search");
			} else {
				return true;
			}

		default:
			break;

		}
		return false;
	}

	
	
	/** This method is adding the keyword to the description field to search by.
	 * @param event - the click on the add keyword button.
	 */
	public void btnAddKeyWord(ActionEvent event) {
		if (VerifyInput(MyBookSearchType.AddKeyWord)) {
			addtoPane(txtKeyWord.getText());
			txtKeyWord.setText("");
		}

	}

	
	/** 
	 * this method is adding the keyword to the flowPane, set of all the keywords to search books by.
	 * @param tag - keyword to add.
	 */
	public void addtoPane(String tag) {
		HBox tagBox = new HBox();
		tagBox.setStyle("-fx-background-color: #e0e0e0; -fx-border-radius: 5; -fx-background-radius: 5;");
		tagBox.setPadding(new Insets(5));
		tagBox.setSpacing(5);

		Label tagLabel = new Label(tag);

		Button removeButton = new Button("x");
		removeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-font-weight: bold;");

		removeButton.setOnAction(event -> {
			flowPaneDescriptionTags.getChildren().remove(tagBox);
		});

		tagBox.getChildren().addAll(tagLabel, removeButton);

		flowPaneDescriptionTags.getChildren().add(tagBox);

	}

	
	/**
	 * This method is returning all the tags to search.
	 * @return String of all the keywords ", " separate
	 */
	private String getAllFlowPaneTags() {
		StringBuilder tags = new StringBuilder();

		for (Node node : flowPaneDescriptionTags.getChildren()) {
			if (node instanceof HBox) {
				HBox tagBox = (HBox) node;
				for (Node child : tagBox.getChildren()) {
					if (child instanceof Label) {
						Label label = (Label) child;
						tags.append(label.getText()).append(", ");
						break;
					}
				}
			}
		}

		if (tags.length() > 0) {
			tags.setLength(tags.length() - 2);
		}

		return tags.toString();
	}

	/**
	 * This method is changing the message on the String to 10 seconds
	 * 
	 * @param s - the message we want to see on the GUI
	 */
	private void changeString(String s) {
		Platform.runLater(() -> {
			lblerrmsg.setText(s);
		});
		PauseTransition pause = new PauseTransition(Duration.seconds(10));
		pause.setOnFinished(event -> {
			lblerrmsg.setText("");
		});
		pause.play();
	}

	
	/**
	 * This method is handling the results from the search.
	 * separating the list of the book to the available to borrow and those that aren't.
	 * @param mybooksRes - the result of the book search.
	 */
	private void HandlBooksList(List<String> mybooksRes) {
		List<String> canBorrowList = new ArrayList<String>();
		List<String> canOrderList = new ArrayList<String>();

		for (String book : mybooksRes) {
			String[] afterSplit = book.split(", ");
			//System.out.println("the book: "+afterSplit[0]+" has: "+afterSplit[afterSplit.length-4]);
			if (Integer.parseInt(afterSplit[afterSplit.length-4]) > 0) {
				canBorrowList.add(book);
			} else {
				canOrderList.add(book);
			}
		}
		AvailableBooks = canBorrowList;
		UnavailableBooks = canOrderList;
	}

	/**
	 * Constructor of this class, starting the GUI
	 * @param primaryStage -Stage of this GUI to upload all the data.
	 */
	public void start(Stage primaryStage) {
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource("/mainGui/SearchBooks.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/mainGui/SearchBooks.css").toExternalForm());
			primaryStage.setTitle("Search Books Screen");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/*
	 * This method is for the exit button sending a message to the server that now
	 * we are disconnecting, closing the GUI and the connection for the server.
	 */
	public void getExitBtn(ActionEvent event) throws Exception {
		System.out.println("Disconnecting from the Server and ending the program.");
		HashMap<String, String> EndingConnections = new HashMap<String, String>();
		EndingConnections.put("Disconnect", "");
		ClientUI.chat.accept(EndingConnections);
		System.exit(0);
	}

}
