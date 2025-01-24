package controllers;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import entity.BorrowsReturnReport;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import client.ClientUI;
import common.Month;

public class BorrowedBooksChartResultsController {

    @FXML
    private BarChart<String, Number> BorrowsChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    
    @FXML
    private ComboBox<String> comboBoxMonth;
    @FXML
    private ComboBox<String> comboBoxYear;
    
    @FXML
    private Button btnBack = null;
    @FXML
    private Button btnExit = null;
    @FXML
    private Button btnRefresh = null;
    @FXML
    private Button btnSpecificBookReport = null;
    
    @FXML
    private Label lblErrMsg;
    
    @FXML
    private TableView<String> tableBorrowsReport1; 
    @FXML
    private TableColumn<String, String> columnTotalBorrows;
    @FXML
    private TableColumn<String, String> columnReturnedInTime;
    @FXML
    private TableColumn<String, String> columnLateReturn;
    @FXML
    private TableColumn<String, String> columnReturnedBeforeTime;
    @FXML
    private TableColumn<String, String> columnNotReturnedYet;
    @FXML
    private TableColumn<String, String> columnLostBook;
    
    private Calendar currectCalendar;
    private int month1;
    private int year1;
    private BorrowsReturnReport myReport;
    
    
    
    private void comboBoxMonthYear() {
		Month SelectOptions[] = Month.values();
		for (int i = 0; i < SelectOptions.length; i++) {
			comboBoxMonth.getItems().add(SelectOptions[i].toString());
		}
		
		for (int i = year1+2; i >= (year1-30) ; i--) {
			comboBoxYear.getItems().add(String.valueOf(i));
		}
		
		comboBoxYear.setValue(String.valueOf(year1));
		comboBoxMonth.setValue(Month.getByNumber(month1).toString());
	}
    
    private void initBothTables() {
    	columnTotalBorrows.setCellValueFactory(cellData -> {
        	String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[2]);
        });
    	columnReturnedInTime.setCellValueFactory(cellData -> {
        	String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[3]);
        });
    	columnLateReturn.setCellValueFactory(cellData -> {
        	String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[4]);
        });
    	columnReturnedBeforeTime.setCellValueFactory(cellData -> {
        	String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[5]);
        });
    	columnNotReturnedYet.setCellValueFactory(cellData -> {
        	String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[6]);
        });
    	columnLostBook.setCellValueFactory(cellData -> {
        	String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[7]);
        });

	}
    
    private boolean VerifyInput() {
		if (((Month.getByName(getComboBoxMonth()).getMonthNumber() > month1) && (getComboBoxYear().equals(String.valueOf(year1)))) ||
					Integer.parseInt(getComboBoxYear()) > year1 ){
			lblErrMsg.setText("You can see only reports thats was created last months.");
			return false;
		}
		lblErrMsg.setText("");
		return true;

	}
    
    public void refreshBtn(ActionEvent event) {
    	if(VerifyInput()) {
    		try {
	    		myReport = new BorrowsReturnReport(Month.getByName(getComboBoxMonth()).getMonthNumber(), Integer.parseInt(getComboBoxYear()));
		    	loadReport();
		    	loadBarChartData();
    		}catch (Exception e) {
    			lblErrMsg.setText(e.getMessage());
			}
    	}
    }
    
    private String getComboBoxYear() {
    	return (String) comboBoxYear.getValue();
	}
    
    private String getComboBoxMonth() {
    	return (String) comboBoxMonth.getValue();
	}
    
    public void loadReport() {
    	if(VerifyInput()) {
    		String report = myReport.toString();
    		tableBorrowsReport1.setItems(FXCollections.observableArrayList(report));
    	}
    }
    
    
    @FXML
    private void loadBarChartData() {
        // Clear existing BarChart data
        BorrowsChart.getData().clear();

        if (!tableBorrowsReport1.getItems().isEmpty()) {
            // Create a new data series
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(getComboBoxMonth()+" "+getComboBoxYear());
            
            // Add data to the BarChart
            series.getData().add(new XYChart.Data<>("Total borrows", myReport.getTotalBorrows()));
            series.getData().add(new XYChart.Data<>("Returned in time", myReport.getReturnedInTime()));
            series.getData().add(new XYChart.Data<>("Late return", myReport.getLateReturn()));
            series.getData().add(new XYChart.Data<>("Returned before time", myReport.getReturnedBeforeTime()));
            series.getData().add(new XYChart.Data<>("Not returned yet", myReport.getNotReturnedYet()));
            series.getData().add(new XYChart.Data<>("Lost books", myReport.getLostBooks()));

            // Add the series to the chart
            BorrowsChart.getData().add(series);
        }
    }
    
    
    
    @FXML
    public void initialize() {
    	currectCalendar = Calendar.getInstance();
		month1 = (currectCalendar.get(Calendar.MONTH) + 1);
		year1 = currectCalendar.get(Calendar.YEAR);
		if(month1 == 1) {
			month1 = 12;
			year1--;
		}else {
			month1--;
		}
		try {
			myReport = new BorrowsReturnReport(month1, year1);
	    	comboBoxMonthYear();
	    	initBothTables();
	    	loadReport();
	    	loadBarChartData();
	    }catch (Exception e) {

			lblErrMsg.setText(e.getMessage());
		}
    }
    
    @FXML
    public void goToSpecificBookScreen(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SpecificBookChartResults.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/SpecificBookChartResults.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Specific Book Results");
        stage.show();
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    public void Back(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GenerateReports.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/GenerateReports.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Generate Reports");
        stage.show();
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    public void getExitBtn(ActionEvent event) throws Exception {
        System.out.println("Disconnecting from the Server and ending the program.");
        HashMap<String, String> EndingConnections = new HashMap<>();
        EndingConnections.put("Disconnect", "");
        ClientUI.chat.accept(EndingConnections);
        System.exit(0);
    }
}