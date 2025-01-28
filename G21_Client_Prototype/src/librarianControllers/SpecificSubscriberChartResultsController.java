package librarianControllers;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import entity.Book;
import entity.BorrowedRecord;
import entity.BorrowsReturnReport;
import entity.Subscriber;
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
import mainControllers.ConnectionSetupController;
import client.ClientUI;
import common.Month;

public class SpecificSubscriberChartResultsController {

	@FXML
	private BarChart<String, Number> specificSubscriberChart;
	@FXML
	private CategoryAxis xAxis;
	@FXML
	private NumberAxis yAxis;

	@FXML
	private ComboBox<String> comboBoxMonth;
	@FXML
	private ComboBox<String> comboBoxYear;
	@FXML
	private ComboBox<String> comboBoxSubscriber;

	@FXML
	private Button btnBack = null;
	@FXML
	private Button btnExit = null;
	@FXML
	private Button btnRefresh = null;

	@FXML
	private Label lblErrMsg;

	@FXML
	private TableView<String> tablespecificSubscriberReport;
	@FXML
	private TableColumn<String, String> columnTotalFrozenDays;
	@FXML
	private TableColumn<String, String> columnTotalActiveDays;
	@FXML
	private TableColumn<String, String> columnTotalNotRegisteredDays;
	@FXML
	private TableColumn<String, String> columnTotalDaysInSelectedMonth;

	
	private Calendar currectCalendar;
	private int month1;
	private int year1;
	private Subscriber currSubscriber;
	List<String> subscriberFrozenListDetails;
	String frozenResult;
	
	/**
	 * initialize the values comboBox of the chart for the GUI
	 * for picking dates and books titles
	 */
	private void comboBoxMonthYearSubscribers() {
		List<String> subsInformations = Subscriber.getIDsAndNames();
		for (String subscriberString : subsInformations) {
			comboBoxSubscriber.getItems().add(subscriberString);
		}

		Month SelectOptions[] = Month.values();
		for (int i = 0; i < SelectOptions.length; i++) {
			comboBoxMonth.getItems().add(SelectOptions[i].toString());
		}

		for (int i = year1 + 2; i >= (year1 - 30); i--) {
			comboBoxYear.getItems().add(String.valueOf(i));
		}
		comboBoxSubscriber.setValue(subsInformations.get(0));
		comboBoxYear.setValue(String.valueOf(year1));
		comboBoxMonth.setValue(Month.getByNumber(month1).toString());
	}
	
	/**
	 * initialize the tables for the GUI
	 */
	private void initBothTables() {
		columnTotalFrozenDays.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[0]);
		});
		columnTotalActiveDays.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[1]);
		});
		columnTotalNotRegisteredDays.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[2]);
		});
		columnTotalDaysInSelectedMonth.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[3]);
		});

	}
	
	/**
	 * check if the values are correct
	 * @return true for verified input or false
	 */
	private boolean VerifyInput() {
		if (((Month.getByName(getComboBoxMonth()).getMonthNumber() > month1)
				&& (getComboBoxYear().equals(String.valueOf(year1)))) || Integer.parseInt(getComboBoxYear()) > year1) {
			lblErrMsg.setText("You can see only reports thats was created last months.");
			return false;
		}
		if (getComboBoxSubscriber().isEmpty()) {
			lblErrMsg.setText("You need to choose subscriber.");
			return false;
		}
		lblErrMsg.setText("");
		return true;

	}
	
	/**refresh the chart
     * @param event
     */
	public void refreshBtn(ActionEvent event) {
		if (VerifyInput()) {
			try {
				currSubscriber = new Subscriber(Integer.parseInt(getSubIDFromComboBox()));
				subscriberFrozenListDetails = Subscriber.GetFrozenReportForSubscriber(currSubscriber.getId(),
						Month.getByName(getComboBoxMonth()).getMonthNumber(), Integer.parseInt(getComboBoxYear()));
				frozenResult = calculateFrozenTimeOfSubscriber();
				loadReport();
				loadBarChartData();
			} catch (Exception e) {
				lblErrMsg.setText(e.getMessage());
			}
		}
	}

	/**
	 * calculate the frozen time of subscriber until this curent day for the chart
	 * @return
	 */
	private String calculateFrozenTimeOfSubscriber() {
		
	    YearMonth yearMonthSelected = YearMonth.of(Integer.parseInt(getComboBoxYear()),
	            Month.getByName(getComboBoxMonth()).getMonthNumber());
	    
	    int daysInSelectedMonth = yearMonthSelected.lengthOfMonth();
	    Date registeredDate = currSubscriber.getRegisteredDate();
	    LocalDate registeredLocalDate = registeredDate.toLocalDate();
	    YearMonth registeredYearMonth = YearMonth.from(registeredLocalDate);
	    LocalDate startOfSelectedMonth = yearMonthSelected.atDay(1);
	    LocalDate endOfSelectedMonth = yearMonthSelected.atDay(daysInSelectedMonth);
	    
	    if (yearMonthSelected.isBefore(registeredYearMonth)) {
	        return "0, 0, " + daysInSelectedMonth + ", " + daysInSelectedMonth;
	    }
	    
	    long notRegister = 0;
	    int frozenDays = 0;
	    
	    if (yearMonthSelected.equals(registeredYearMonth)) {
	        notRegister = ChronoUnit.DAYS.between(startOfSelectedMonth, registeredLocalDate);
	    }
	    
	    for (String frozenDetail : subscriberFrozenListDetails) {
            String[] parts = frozenDetail.split(", ");
            LocalDate beginLocalDate = Date.valueOf(parts[2]).toLocalDate();
            LocalDate endLocalDate = Date.valueOf(parts[3]).toLocalDate();
            
            YearMonth beginDate = YearMonth.from(beginLocalDate);
            YearMonth endDate = YearMonth.from(endLocalDate);
            
            if (yearMonthSelected.equals(beginDate)) {
                if (yearMonthSelected.equals(endDate)) {
                    frozenDays += ChronoUnit.DAYS.between(beginLocalDate, endLocalDate);
                } else {
                    frozenDays += ChronoUnit.DAYS.between(beginLocalDate, endOfSelectedMonth);
                }
            } else if (yearMonthSelected.equals(endDate)) {
                frozenDays += ChronoUnit.DAYS.between(startOfSelectedMonth, endLocalDate);
            }
        }
        int activedays = daysInSelectedMonth - (int)notRegister - frozenDays;
        return frozenDays + ", " + activedays + ", " + notRegister + ", " + daysInSelectedMonth;
	}
////////////////////////////////////////////
	////// getters for the data from combobox//////
	///////////////////////////////////////////////
	
	private String getComboBoxYear() {
		return (String) comboBoxYear.getValue();
	}

	private String getComboBoxMonth() {
		return (String) comboBoxMonth.getValue();
	}

	private String getComboBoxSubscriber() {
		return (String) comboBoxSubscriber.getValue();
	}

	private String getSubIDFromComboBox() {
		String[] partStrings = getComboBoxSubscriber().split(", ");
		return partStrings[0];
	}

	private String getSubNameFromComboBox() {
		String[] partStrings = getComboBoxSubscriber().split(", ");
		return partStrings[1];
	}
/////////////////////////////////////////////////////////////////////
	/**
	 * load the report for the chart
	 */
	public void loadReport() {
		if (VerifyInput()) {
			tablespecificSubscriberReport.setItems(FXCollections.observableArrayList(frozenResult));
		}
	}
	@FXML
	/**
     * load all the data of the chart 
     */
	private void loadBarChartData() {
		// Clear existing BarChart data
		specificSubscriberChart.getData().clear();

		if (frozenResult != null || !tablespecificSubscriberReport.getItems().isEmpty()) {
			// Create a new data series
			XYChart.Series<String, Number> series = new XYChart.Series<>();
			series.setName(getComboBoxSubscriber() + " " + getComboBoxMonth() + " " + getComboBoxYear());
			String[] parts = frozenResult.split(", ");
			// Add data to the BarChart
			series.getData().add(new XYChart.Data<>("Total Frozen days", Integer.parseInt(parts[0])));
			series.getData().add(new XYChart.Data<>("Total Active days", Integer.parseInt(parts[1])));
			series.getData().add(new XYChart.Data<>("Total Not Registered days", Integer.parseInt(parts[2])));
			series.getData().add(new XYChart.Data<>("Total Days in the month", Integer.parseInt(parts[3])));

			// Add the series to the chart
			specificSubscriberChart.getData().add(series);
		}
	}

	/**
     * initialize all the variables for the GUI
     */
	@FXML
	public void initialize() {
		currectCalendar = Calendar.getInstance();
		month1 = (currectCalendar.get(Calendar.MONTH) + 1);
		year1 = currectCalendar.get(Calendar.YEAR);
		if (month1 == 1) {
			month1 = 12;
			year1--;
		} else {
			month1--;
		}
		try {
			currSubscriber = null;
			subscriberFrozenListDetails = new ArrayList<String>();
			frozenResult ="0, 0, 0, 0";
			comboBoxMonthYearSubscribers();
			initBothTables();
			loadReport();
			loadBarChartData();
			//refreshBtn(null);
		} catch (Exception e) {

			lblErrMsg.setText(e.getMessage());
		}
	}
	
	/**
     * change screen to the previous screen
     * @param event
     * @throws IOException
     */
	@FXML
	public void Back(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/librarianGui/SubscriberStatusChartResults.fxml"));
		Parent root = loader.load();
		Stage stage = new Stage();
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/librarianGui/SubscriberStatusChartResults.css").toExternalForm());
		stage.setScene(scene);
		stage.setTitle("Subscriber Status Chart Results Report");
		stage.show();
		((Node) event.getSource()).getScene().getWindow().hide();
	}
	
	/**
     * exit from the librarian
     * @param event
     * @throws Exception
     */
	@FXML
	public void getExitBtn(ActionEvent event) throws Exception {
		ConnectionSetupController.stopConnectionToServer();
		System.exit(0);
	}
}