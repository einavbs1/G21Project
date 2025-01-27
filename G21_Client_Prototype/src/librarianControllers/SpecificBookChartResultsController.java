package librarianControllers;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import entity.Book;
import entity.BorrowedRecord;
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
import mainControllers.ConnectionSetupController;
import client.ClientUI;
import common.Month;

public class SpecificBookChartResultsController {

	@FXML
	private BarChart<String, Number> specificBorrowsChart;
	@FXML
	private CategoryAxis xAxis;
	@FXML
	private NumberAxis yAxis;

	
	@FXML
	private ComboBox<String> comboBoxMonth;
	@FXML
	private ComboBox<String> comboBoxYear;
	@FXML
	private ComboBox<String> comboBoxBooks;

	@FXML
	private Button btnBack = null;
	@FXML
	private Button btnExit = null;
	@FXML
	private Button btnRefresh = null;

	@FXML
	private Label lblErrMsg;

	@FXML
	private TableView<String> tableSpecificBookBorrowsReport1;
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
	private Book currBook;
	String recordBook;

	private void comboBoxMonthYearBooks() {
		List<String> booksInformations = Book.getBookBarcodesAndTitles();
		for (String bookString : booksInformations) {
			comboBoxBooks.getItems().add(bookString);
		}

		Month SelectOptions[] = Month.values();
		for (int i = 0; i < SelectOptions.length; i++) {
			comboBoxMonth.getItems().add(SelectOptions[i].toString());
		}

		for (int i = year1 + 2; i >= (year1 - 30); i--) {
			comboBoxYear.getItems().add(String.valueOf(i));
		}
		comboBoxBooks.setValue(booksInformations.get(0));
		comboBoxYear.setValue(String.valueOf(year1));
		comboBoxMonth.setValue(Month.getByNumber(month1).toString());
	}

	private void initBothTables() {
		columnTotalBorrows.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[0]);
		});
		columnReturnedInTime.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[1]);
		});
		columnLateReturn.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[2]);
		});
		columnReturnedBeforeTime.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[3]);
		});
		columnNotReturnedYet.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[4]);
		});
		columnLostBook.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[5]);
		});

	}

	private boolean VerifyInput() {
		if (((Month.getByName(getComboBoxMonth()).getMonthNumber() > month1)
				&& (getComboBoxYear().equals(String.valueOf(year1)))) || Integer.parseInt(getComboBoxYear()) > year1) {
			lblErrMsg.setText("You can see only reports thats was created last months.");
			return false;
		}
		if (getComboBoxBooks().isEmpty()) {
			lblErrMsg.setText("You need to choose book.");
			return false;
		}
		lblErrMsg.setText("");
		return true;

	}

	public void refreshBtn(ActionEvent event) {
		if (VerifyInput()) {
			try {
				currBook = new Book(getBarcodeFromComboBox());
				recordBook = BorrowedRecord.getBookBorrowsInSpecificDate(currBook.getBarcode(),
						Month.getByName(getComboBoxMonth()).getMonthNumber(), Integer.parseInt(getComboBoxYear()));
				loadReport();
				loadBarChartData();
			} catch (Exception e) {
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

	private String getComboBoxBooks() {
		return (String) comboBoxBooks.getValue();
	}

	private String getBarcodeFromComboBox() {
		String[] partStrings = getComboBoxBooks().split(", ");
		return partStrings[0];
	}

	private String getTitleFromComboBox() {
		String[] partStrings = getComboBoxBooks().split(", ");
		return partStrings[1];
	}

	public void loadReport() {
		if (VerifyInput()) {
			tableSpecificBookBorrowsReport1.setItems(FXCollections.observableArrayList(recordBook));
		}
	}

	@FXML
	private void loadBarChartData() {
		// Clear existing BarChart data
		specificBorrowsChart.getData().clear();

		if (recordBook != null || !tableSpecificBookBorrowsReport1.getItems().isEmpty()) {
			// Create a new data series
			XYChart.Series<String, Number> series = new XYChart.Series<>();
			series.setName(getTitleFromComboBox() + " " + getComboBoxMonth() + " " + getComboBoxYear());
			String[] parts = recordBook.split(", ");
			// Add data to the BarChart
			series.getData().add(new XYChart.Data<>("Total borrows", Integer.parseInt(parts[0])));
			series.getData().add(new XYChart.Data<>("Returned in time", Integer.parseInt(parts[1])));
			series.getData().add(new XYChart.Data<>("Late return", Integer.parseInt(parts[2])));
			series.getData().add(new XYChart.Data<>("Returned before time", Integer.parseInt(parts[3])));
			series.getData().add(new XYChart.Data<>("Not returned yet", Integer.parseInt(parts[4])));
			series.getData().add(new XYChart.Data<>("Lost books", Integer.parseInt(parts[5])));

			// Add the series to the chart
			specificBorrowsChart.getData().add(series);
		}
	}

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
			currBook = null;
			recordBook = "0, 0, 0, 0, 0, 0";
			comboBoxMonthYearBooks();
			initBothTables();
			
			loadReport();
			loadBarChartData();
		} catch (Exception e) {

			lblErrMsg.setText(e.getMessage());
		}
	}

	@FXML
	public void Back(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/librarianGui/BorrowedBooksChartResults.fxml"));
		Parent root = loader.load();
		Stage stage = new Stage();
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/librarianGui/BorrowedBooksChartResults.css").toExternalForm());
		stage.setScene(scene);
		stage.setTitle("Borrowed Books Chart Results Reports");
		stage.show();
		((Node) event.getSource()).getScene().getWindow().hide();
	}

	@FXML
	public void getExitBtn(ActionEvent event) throws Exception {
		ConnectionSetupController.stopConnectionToServer();
		System.exit(0);
	}
}