package mainControllers;

import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import entity.*;
import client.ChatClient;
import client.ClientUI;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/**
 * This class is the controller of the search books results
 */
public class SearchBookResultsController {

    @FXML
    private Button btnExit = null;
    @FXML
    private Button btnBack = null;
    @FXML
    private Button btnOrderBook = null;

    //first table of available books
    @FXML
    private TableView<String> tableViewAvailableBooks; 
    @FXML
    private TableColumn<String, String> columnBookBarcode1;
    @FXML
    private TableColumn<String, String> columnBookTitle1;
    @FXML
    private TableColumn<String, String> columnBookSubject1;
    @FXML
    private TableColumn<String, String> columnBookDescription1;
    @FXML
    private TableColumn<String, String> columnBookAvilableCopies1;
    @FXML
    private TableColumn<String, String> columnBookLocation1;
    
    
    //second table for unavailable books to show they first return date and if can make an order for them.
    @FXML
    private TableView<String> tableViewUnavailableBooks; 
    @FXML
    private TableColumn<String, String> columnBookBarcode2;
    @FXML
    private TableColumn<String, String> columnBookTitle2;
    @FXML
    private TableColumn<String, String> columnBookSubject2;
    @FXML
    private TableColumn<String, String> columnBookDescription2;
    @FXML
    private TableColumn<String, String> columnBookCanOrder2;
    @FXML
    private TableColumn<String, String> columnBookClosestReturnDate2;

    /*
	 * initialize the GUI with the data to the tables views
	 * setting button to go directly to order book screen if the subscriber is logged to the system 
	 */
    public void initialize() {
    	loadBooks();
    	initAvailableBooks();
    	initUnavailableBooks();
    	if(ChatClient.getCurrectSubscriber() == null) {
    		btnOrderBook.setVisible(false);
    	}
    }

    
    /**
     * this private method is setting the available book in the first table view.
     * shows the books and location in the library.
     */
    private void initAvailableBooks() {
    	columnBookBarcode1.setCellValueFactory(cellData -> {
        	String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[0]);
        });
    	columnBookTitle1.setCellValueFactory(cellData -> {
        	String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[1]);
        });
    	columnBookSubject1.setCellValueFactory(cellData -> {
        	String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[2]);
        });
    	columnBookDescription1.setCellValueFactory(cellData -> {
        	String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[3]);
        });
    	columnBookAvilableCopies1.setCellValueFactory(cellData -> {
        	String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[parts.length-4]);
        });
    	columnBookLocation1.setCellValueFactory(cellData -> {
        	String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[parts.length-1]);
        });
    }
    
    /**
     * this private method is setting the unavailable book in the second table view.
     * shows the books if can order them and what is the closest return date.
     */
    private void initUnavailableBooks() {
    	columnBookBarcode2.setCellValueFactory(cellData -> {
        	String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[0]);
        });
    	columnBookTitle2.setCellValueFactory(cellData -> {
        	String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[1]);
        });
    	columnBookSubject2.setCellValueFactory(cellData -> {
        	String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[2]);
        });
    	columnBookDescription2.setCellValueFactory(cellData -> {
        	String[] parts = cellData.getValue().split(", ");
        	String descriptionString= new String();
        	for(int i=3; i<parts.length-5; i++) {
        		if(i>3) {
        			descriptionString = descriptionString +", ";
        		}
        		descriptionString = descriptionString + parts[i];
        	}
            return new javafx.beans.property.SimpleStringProperty(descriptionString);
        });
    	columnBookCanOrder2.setCellValueFactory(cellData -> {
        	String[] parts = cellData.getValue().split(", ");
        	int canOrder = Integer.parseInt(parts[parts.length-5])-Integer.parseInt(parts[parts.length-3]);
        	String resOrder = canOrder > 0 ? "Can Order" : "Can't Order";
        	resOrder = resOrder +" (The are currect "+parts[parts.length-3]+" orders / "+parts[parts.length-5]+" Overall)";
            return new javafx.beans.property.SimpleStringProperty(resOrder);
        });
    	columnBookClosestReturnDate2.setCellValueFactory(cellData -> {
        	String[] parts = cellData.getValue().split(", ");
        	Date closest = Book.getClosestReturnDate(parts[0]);
        	String dateString = "null";
        	if(closest != null) {
        		dateString = closest.toString();
        	}
            return new javafx.beans.property.SimpleStringProperty(dateString);
        });
    	
    }
    
    
    /** This method is for the back button closing the current GUI and uploading the Search books GUI.
     * @param event - click on the back button.
     * @throws IOException
     */
    public void Back(ActionEvent event) throws IOException {
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
    
    
    /** This method is for the order book button closing the current GUI and uploading the order book GUI.
     * only visable to subscriber when login to the system.
     * @param event - click on the back button.
     * @throws IOException
     */
    public void OrderBook(ActionEvent event) throws IOException {
    	FXMLLoader Loader = new FXMLLoader(getClass().getResource("/subscriberGui/OrderBook.fxml"));
        Parent Root = Loader.load();
        Stage Stage = new Stage();
        Scene Scene = new Scene(Root);
        Scene.getStylesheets().add(getClass().getResource("/subscriberGui/OrderBook.css").toExternalForm());
        Stage.setScene(Scene);
        Stage.setTitle("Order Book");
        Stage.show();
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    
    
    /**This method is setting the books in the tables.
     */
    public void loadBooks() {
    	tableViewAvailableBooks.setItems(FXCollections.observableArrayList(SearchBooksController.getAvailableBooks()));
    	tableViewUnavailableBooks.setItems(FXCollections.observableArrayList(SearchBooksController.getUnavailableBooks()));
    }
    
	/**
	 * Constructor of this class, starting the GUI
	 * 
	 * @param primaryStage -Stage of this GUI to upload all the data.
	 */
	public void start(Stage primaryStage) {
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource("/mainGui/SearchBookResults.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/mainGui/SearchBookResults.css").toExternalForm());
			primaryStage.setTitle("Search Book Results Screen");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
    
 
	/*
	 * This method is for the exit button sending a message to the server that now we are disconnecting,
	 * closing the GUI and the connection for the server.
	 */
	public void getExitBtn(ActionEvent event) throws Exception {
		System.out.println("Disconnecting from the Server and ending the program.");
		HashMap<String, String> EndingConnections = new HashMap<String, String>();
		EndingConnections.put("Disconnect", "");
		ClientUI.chat.accept(EndingConnections);
		System.exit(0);
	}
}
