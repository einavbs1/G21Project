package controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import entity.Subscriber;
import entity.SubscribersStatusReport;
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
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import client.ClientUI;
import common.LibrarianOptions;
import common.Month;

public class SubscriberStatusChartResultsController {

    @FXML
    private BarChart<String, Number> subscriberChart;
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
    private Label lblErrMsg;
    
    @FXML
    private TableView<String> tableStatusReport1; 
    @FXML
    private TableColumn<String, String> columnTotalActive;
    @FXML
    private TableColumn<String, String> columnTotalFrozen;
    @FXML
    private TableColumn<String, String> columnFrozeNow;
    
    @FXML
    private TableView<String> tableStatusReport2;
    @FXML
    private TableColumn<String, String> columnUnfrozedNow;
    @FXML
    private TableColumn<String, String> columnNewRegisters;
    @FXML
    private TableColumn<String, String> columnTotalSubscribers;
    
    private Calendar currectCalendar;
    private int month1;
    private int year1;
    private SubscribersStatusReport myReport;
    

    
    
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
    	columnTotalActive.setCellValueFactory(cellData -> {
        	String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[2]);
        });
    	columnTotalFrozen.setCellValueFactory(cellData -> {
        	String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[3]);
        });
    	columnFrozeNow.setCellValueFactory(cellData -> {
        	String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[4]);
        });
    	columnUnfrozedNow.setCellValueFactory(cellData -> {
        	String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[5]);
        });
    	columnNewRegisters.setCellValueFactory(cellData -> {
        	String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[6]);
        });
    	columnTotalSubscribers.setCellValueFactory(cellData -> {
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
	    		myReport = new SubscribersStatusReport(Month.getByName(getComboBoxMonth()).getMonthNumber(), Integer.parseInt(getComboBoxYear()));
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
    		tableStatusReport1.setItems(FXCollections.observableArrayList(report));
    		tableStatusReport2.setItems(FXCollections.observableArrayList(report));
    	}
    }
    
    
    @FXML
    private void loadBarChartData() {
        // Clear existing BarChart data
        subscriberChart.getData().clear();

        if (!tableStatusReport1.getItems().isEmpty()) {
            // Create a new data series
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(getComboBoxMonth()+" "+getComboBoxYear());
            // Add data to the BarChart
            series.getData().add(new XYChart.Data<>("Active", myReport.getTotalActiveSubscribers()));
            series.getData().add(new XYChart.Data<>("Frozen", myReport.getTotalFrozenSubscribers()));
            series.getData().add(new XYChart.Data<>("Froze Now", myReport.getGotFrozeSubscribers()));
            series.getData().add(new XYChart.Data<>("Unfroze", myReport.getUnfrozedSubscribers()));
            series.getData().add(new XYChart.Data<>("New", myReport.getNewSubscribers()));
            series.getData().add(new XYChart.Data<>("Total", myReport.getTotalSubscribers()));

            // Add the series to the chart
            subscriberChart.getData().add(series);
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
			myReport = new SubscribersStatusReport(month1, year1);
	    	comboBoxMonthYear();
	    	initBothTables();
	    	loadReport();
	    	loadBarChartData();
	    }catch (Exception e) {
			lblErrMsg.setText(e.getMessage());
		}
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