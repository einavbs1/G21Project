package subscriberControllers;

import java.io.IOException;
import java.util.HashMap;
import client.ClientUI;
import client.ChatClient;
import entity.Subscriber;
import entity.Book;
import entity.LogActivity;
import entity.Orders;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;
import mainControllers.ConnectionSetupController;

/**
 * This class handles the GUI for ordering books. It allows subscribers to order
 * books by entering their barcode.
 * 
 * @author Chen
 */
public class OrderBookController {

	@FXML
	private TextField txtBookBarcode;

	@FXML
	private Button btnLoadBook = null;
	@FXML
	private Button btnSearchBooks = null;
	@FXML
	private Button btnOrderBook = null;
	@FXML
	private Button btnBack = null;
	@FXML
	private Button btnExit = null;

	@FXML
	private Label lblTitle;
	@FXML
	private Label lblinstructions;
	@FXML
	private Label lblBookBarcode;
	@FXML
	private Label lblTipToFindBook;
	@FXML
	private Label lblmsgMain;
	@FXML
	private Label lblmsgOrder;

	private Book bookToOrder;

	private Subscriber currSubscriber;

	/**
	 * Sets the current subscriber ID for making orders.
	 * 
	 * @param subscriberId The ID of the subscriber making the order
	 */
	public void initialize() {
		currSubscriber = ChatClient.getCurrectSubscriber();
		lblmsgMain.setText("");
		txtBookBarcode.setText("");
		if (!VerifyActions(myEnum.VerifySubscriberStatus)) {
			lblmsgMain.setTextFill(Paint.valueOf("#bf3030"));
			lblmsgMain.setText("You can't make any order.\n Your subscriptions is Frozen.");
			txtBookBarcode.setEditable(false);
			btnLoadBook.setDisable(true);
		}else {
			txtBookBarcode.setEditable(true);
			btnLoadBook.setDisable(false);
		}
		btnOrderBook.setDisable(true);
		bookToOrder = null;
	}

	private void changeString(String s, String color, Label lbl) {
		Platform.runLater(() -> {
			lbl.setText(s);
			lbl.setTextFill(Paint.valueOf(color));
		});
		PauseTransition pause = new PauseTransition(Duration.seconds(10));
		pause.setOnFinished(event -> {
			if (lbl.getText().equals(s)) {
				lbl.setText(" ");
			}
		});
		pause.play();
	}

	enum myEnum {
		VerifySubscriberStatus, VerifyBookBarcode, VerifyCanOrderThisBook;

	}

	private boolean VerifyActions(myEnum action) {

		switch (action) {

		case VerifySubscriberStatus:
			currSubscriber = new Subscriber(currSubscriber.getId());
			ChatClient.setCurrectSubscriber(currSubscriber);
			if (!currSubscriber.getStatus().toLowerCase().equals("active")) {
				return false;
			}
			break;

		case VerifyBookBarcode:
			if (txtBookBarcode.getText().isEmpty()) {
				changeString("You have to enter the book barcode load his data.", "#bf3030", lblmsgMain);
				return false;
			}

			if (!txtBookBarcode.getText().matches("\\d+")) {
				changeString("Book barcode can contain only digits.", "#bf3030", lblmsgMain);
				return false;
			}
			break;

		case VerifyCanOrderThisBook:
			if (bookToOrder == null) {
				changeString("You need to load the book first.", "#bf3030", lblmsgMain);
				return false;
			}
			if(Orders.checkMyActiveOrders(bookToOrder.getBarcode()).size() >= (bookToOrder.getAllCopies() - bookToOrder.getLostNumber())) {
				changeString(
						"You can't order this book.\n You can try again in couple days or after the closest return date.",
						"#bf3030", lblmsgMain);
				return false;
			}
			break;

		default:
			break;

		}

		return true;

	}

	public void loadBook(ActionEvent event) {
		lblmsgOrder.setText("");
		if (VerifyActions(myEnum.VerifyBookBarcode)) {
			try {
				bookToOrder = new Book(txtBookBarcode.getText());
				String msg = "The book: " + bookToOrder.getTitle() + " (barcode: " + bookToOrder.getBarcode();
				if (VerifyActions(myEnum.VerifyCanOrderThisBook)) {
					//load again the book after changes in order list.
					bookToOrder = new Book(txtBookBarcode.getText());
					
					lblmsgMain.setTextFill(Paint.valueOf("#086f03"));
					msg = msg + ").\n Can be order.";
					txtBookBarcode.setEditable(false);
					btnLoadBook.setDisable(true);
					btnOrderBook.setDisable(false);
				} else {
					lblmsgMain.setTextFill(Paint.valueOf("#bf3030"));
					msg = msg + ").\n Can't be order.";
					txtBookBarcode.setEditable(true);
					btnLoadBook.setDisable(false);
					btnOrderBook.setDisable(true);
				}
				lblmsgMain.setText(msg);

			} catch (Exception e) {
				changeString(e.getMessage(), "#bf3030", lblmsgMain);
			}
		}
	}
	
	public void openSearchBookScreen(ActionEvent event) throws IOException {
		FXMLLoader Loader = new FXMLLoader(getClass().getResource("/mainGui/SearchBooks.fxml"));
        Parent Root = Loader.load();
        Stage Stage = new Stage();
        Scene Scene = new Scene(Root);
        Scene.getStylesheets().add(getClass().getResource("/mainGui/SearchBooks.css").toExternalForm());
        Stage.setScene(Scene);
        Stage.setTitle("Search Books");
        Stage.show();
        ((Node) event.getSource()).getScene().getWindow().hide();
	}
	

	/**
	 * Handles the order button click event. Validates subscriber status and book
	 * availability before making order.
	 * 
	 * @param event The action event
	 */
	@FXML
	public void orderBook(ActionEvent event) {
		if (VerifyActions(myEnum.VerifyCanOrderThisBook)) {
			try {
				new Orders(currSubscriber.getId(), bookToOrder.getBarcode(), bookToOrder.getTitle() );
				bookToOrder.addToOrdersNumber();
				bookToOrder.UpdateDetails();
				
				String activityMsg = "You ordered the book: " + bookToOrder.getTitle() + " (barcode: "
						+ bookToOrder.getBarcode() + ").";
				
				new LogActivity(currSubscriber.getId(), activityMsg, bookToOrder.getBarcode(), bookToOrder.getTitle(), 0);
				
				lblmsgOrder.setTextFill(Paint.valueOf("#086f03"));
				lblmsgOrder.setText("The order for the book: " + bookToOrder.getTitle() + " (barcode: "
						+ bookToOrder.getBarcode() + ")\n has been created successfully");
				
			} catch (Exception e) {
				lblmsgOrder.setTextFill(Paint.valueOf("#bf3030"));
				lblmsgOrder.setText(e.getMessage());
			}
			initialize();
		}
	}

	/**
	 * Handles the back button action. Returns to the menu screen.
	 * 
	 * @param event The action event
	 * @throws IOException If there is an error loading the menu screen
	 */
	@FXML
	public void Back(ActionEvent event) throws IOException {
		FXMLLoader Loader = new FXMLLoader(getClass().getResource("/subscriberGui/SubscriberMenu.fxml"));
		Parent Root = Loader.load();
		Stage Stage = new Stage();
		Scene Scene = new Scene(Root);
		Scene.getStylesheets().add(getClass().getResource("/subscriberGui/SubscriberMenu.css").toExternalForm());
		Stage.setScene(Scene);
		Stage.setTitle("Subscriber Menu");
		Stage.show();
		((Node) event.getSource()).getScene().getWindow().hide();
	}

	/**
	 * Handles the exit button action. Disconnects from the server and exits the
	 * application.
	 * 
	 * @param event The action event
	 * @throws Exception If there is an error during disconnect
	 */
	@FXML
	public void getExitBtn(ActionEvent event) throws Exception {
		ConnectionSetupController.stopConnectionToServer();
		System.exit(0);
	}
}