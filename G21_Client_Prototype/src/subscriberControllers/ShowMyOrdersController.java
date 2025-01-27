package subscriberControllers;

import entity.*;
import java.io.IOException;
import java.util.HashMap;

import client.ChatClient;
import client.ClientUI;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;
import mainControllers.ConnectionSetupController;

/**
 * This class handles the GUI for cancelling orders.
 * It displays active orders for a specific subscriber and allows them to cancel orders.
 * 
 * @author chen
 */
public class ShowMyOrdersController {
    @FXML
    private Button btnExit = null;
    @FXML
    private Button btnBack = null;
    @FXML
    private Button btnLoadOrder = null;
    @FXML
    private Button btnCancelOrder = null;
    
    @FXML
    private TextField txtOrderNumber;
    
    @FXML
    private Label lblBookName;
    @FXML
    private Label lblStatus;
    @FXML
    private Label lblError;
    @FXML
    private Label lblOrderNum;

    @FXML
    private TableView<String> ordersTable;
    @FXML
    private TableColumn<String, String> orderNumberColumn;
    @FXML
    private TableColumn<String, String> bookBarcodeColumn;
    @FXML
    private TableColumn<String, String> bookNameColumn;
    @FXML
    private TableColumn<String, String> requestedDateColumn;
    @FXML
    private TableColumn<String, String> statusColumn;
    @FXML
    private TableColumn<String, String> arrivedDateColumn;

    private Subscriber currSubscriber;

    private Book bookToCancel;
    
    private Orders manageOrder;
  

    /**
     * Initializes the controller and sets up the table columns.
     * This method is automatically called after the FXML file has been loaded.
     */
    public void initialize() {
    	currSubscriber = ChatClient.getCurrectSubscriber();
    	getMyOrders();
    	initTheTable();
    	btnCancelOrder.setDisable(true);
    	btnLoadOrder.setDisable(false);
    	txtOrderNumber.setText("");
    	txtOrderNumber.setEditable(true);
    	bookToCancel = null;
    	manageOrder = null;
    	ordersTable.getSortOrder().add(orderNumberColumn);
        orderNumberColumn.setSortType(TableColumn.SortType.ASCENDING);
    }
    
    public void getMyOrders() {
    	ordersTable.setItems(FXCollections.observableArrayList(Orders.getMyActiveOrdersSubscriber(currSubscriber.getId())));
    }
    
    private void initTheTable() {
    	orderNumberColumn.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[0]);
        });
        bookBarcodeColumn.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[2]);
        });
        bookNameColumn.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[3]);
        });
        requestedDateColumn.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[4]);
        });
        statusColumn.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(Orders.getStatusString(Integer.parseInt(parts[5])));
        });
        arrivedDateColumn.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            if(parts[5].equals("null")) {
            	return new javafx.beans.property.SimpleStringProperty("Didn't arrive yet.");
            }
            return new javafx.beans.property.SimpleStringProperty(parts[6]);
        });
	}
    
	private void changeString(String s, String color, Label lbl) {
		Platform.runLater(() -> {
			lbl.setText(s);
			lbl.setTextFill(Paint.valueOf(color));
		});
		PauseTransition pause = new PauseTransition(Duration.seconds(10));
		pause.setOnFinished(event -> {
			if(lbl.getText().equals(s)) {
				lbl.setText(" ");
			}
		});
		pause.play();
	}

	enum myEnum{
		VerifyOrderNum,
		VerifyBook;
	}

    private boolean VerifyInput(myEnum action) {
		if(ordersTable.getItems().size() == 0) {
			changeString("You don't have borrows to extend.","#bf3030",lblError);
			return false;
		}
		switch (action){
		case VerifyOrderNum:
			if (txtOrderNumber.getText().isEmpty()) {
				changeString("You must enter the order number that you want to manage.","#bf3030",lblError);
				return false;
			}
			
			if(!txtOrderNumber.getText().matches("\\d+")) {
				changeString("Order number can contain only digits.","#bf3030",lblError);
				return false;
			}

			for (int i = 0; i < ordersTable.getItems().size(); i++) {
				String borrowNumber = ordersTable.getColumns().get(0).getCellData(i).toString();
				if (borrowNumber.equals(txtOrderNumber.getText()) && (ordersTable.getColumns().get(5).getCellData(i).toString()).contains("progress")) {
					return true;
				}
			}
			changeString("You must enter ONLY active order number from the shown table.","#bf3030",lblError);
			return false;
			
		case VerifyBook:
			
			if(manageOrder==null || bookToCancel==null) {
				changeString("You didn't loaded order to manage yet.","#bf3030",lblError);
				return false;
			}
			return true;
			
			default:
				return false;
		}
		
		

	}
    
    /**
     * Handles the Load Order button click event.
     * Loads and displays details for the specified order number.
     * 
     * @param event The action event
     */
    @FXML
    public void LoadOrderBtn(ActionEvent event) {
    	if(VerifyInput(myEnum.VerifyOrderNum)) {
    		try {
    		manageOrder = new Orders(Integer.parseInt(txtOrderNumber.getText()));
    		bookToCancel = new Book(manageOrder.getBookBarcode());
    		lblBookName.setText("BookName: \t" + (bookToCancel.getTitle()));
    		lblStatus.setText("Status: \t" +Orders.getStatusString(manageOrder.getStatus()));
    		btnCancelOrder.setDisable(false);
    		btnLoadOrder.setDisable(true);
    		txtOrderNumber.setEditable(false);
    		}catch (Exception e) {
				changeString(e.getMessage(),"#bf3030",lblError);
			}
    	}
    }

    /**
     * Handles the Cancel Order button click event.
     * Cancels the specified order if it is in a cancellable state.
     * 
     * @param event The action event
     */
    @FXML
    public void CancelOrderBtn(ActionEvent event) {
    	if(VerifyInput(myEnum.VerifyBook)) {
    		try{
    			manageOrder.setStatus(-1);
	    		manageOrder.UpdateDetails();
	    		bookToCancel.removeFromOrdersNumber();
	    		bookToCancel.UpdateDetails();
	    		lblError.setTextFill(Paint.valueOf("#086f03"));
	    		lblError.setText("Order number: "+ manageOrder.getOrderNumber() +" for the book: "+manageOrder.getBook_title()+"\n has been cancelled.");
	    		initialize();
    		}catch (Exception e) {
				changeString(e.getMessage(),"#bf3030",lblError);
			}
    		
    	}
    }


    /**
     * Handles the back button action.
     * Returns to the menu screen.
     * 
     * @param event The action event
     * @throws IOException If there is an error loading the menu screen
     */
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
     * Handles the exit button action.
     * Disconnects from the server and exits the application.
     * 
     * @param event The action event
     * @throws Exception If there is an error during disconnect
     */
    public void getExitBtn(ActionEvent event) throws Exception {
		ConnectionSetupController.stopConnectionToServer();
		System.exit(0);
	}
}


    
    

