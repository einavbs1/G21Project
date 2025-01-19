package controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import entity.BorrowedRecord;
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
import javafx.stage.Stage;
import client.ClientUI;

public class BorrowedBooksChartController {

    @FXML
    private BarChart<String, Number> borrowedBooksChart;
    
    @FXML
    private CategoryAxis xAxis;
    
    @FXML
    private NumberAxis yAxis;
    
    @FXML
    private ComboBox<String> chartTypeCombo;
    
    @FXML
    private Button btnBack;
    
    @FXML
    private Button btnExit;

    private List<String> currentBorrowedBooks;

    @FXML
    public void initialize() {
        setupChartOptions();
        loadBorrowedBooksData();
        updateChart();
    }

    private void setupChartOptions() {
        chartTypeCombo.getItems().addAll(
            "Books Per Day",
            "Expected Returns",
            "Books Per Subscriber",
            "Delayed Returns"
        );
        chartTypeCombo.setValue("Books Per Day");
        chartTypeCombo.setOnAction(event -> updateChart());
    }

    private void loadBorrowedBooksData() {
        try {
            currentBorrowedBooks = BorrowedRecord.getMonthlyBorrowedBooksStats();
            if (currentBorrowedBooks == null || currentBorrowedBooks.isEmpty()) {
                System.out.println("No borrowed books data available");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading borrowed books data: " + e.getMessage());
        }
    }

    private void updateChart() {
        borrowedBooksChart.getData().clear();
        
        if (currentBorrowedBooks == null || currentBorrowedBooks.isEmpty()) {
            return;
        }
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        
        switch(chartTypeCombo.getValue()) {
            case "Books Per Day":
                series.setName("Books Borrowed Per Day");
                setupBooksPerDayChart(series);
                break;
                
            case "Expected Returns":
                series.setName("Expected Returns Per Day");
                setupExpectedReturnsChart(series);
                break;
                
            case "Books Per Subscriber":
                series.setName("Books Per Subscriber");
                setupBooksPerSubscriberChart(series);
                break;
                
            case "Delayed Returns":
                series.setName("Delayed Returns");
                setupDelayedReturnsChart(series);
                break;
        }
        
        borrowedBooksChart.getData().add(series);
    }

    private void setupBooksPerDayChart(XYChart.Series<String, Number> series) {
        Map<String, Integer> booksPerDay = new HashMap<>();
        
        for (String record : currentBorrowedBooks) {
            String[] parts = record.split(", ");
            if (parts.length >= 6) {
                String borrowDate = parts[5].trim();  // borrow_date
                booksPerDay.merge(borrowDate, 1, Integer::sum);
            }
        }
        
        // Add data to series sorted by date
        booksPerDay.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> series.getData().add(
                new XYChart.Data<>(entry.getKey(), entry.getValue())
            ));
            
        xAxis.setLabel("Borrow Date");
        yAxis.setLabel("Number of Books");
    }

    private void setupExpectedReturnsChart(XYChart.Series<String, Number> series) {
        Map<String, Integer> returnsPerDay = new HashMap<>();
        
        for (String record : currentBorrowedBooks) {
            String[] parts = record.split(", ");
            if (parts.length >= 7) {
                String expectedReturnDate = parts[6].trim();  // borrow_expectReturnDate
                if (expectedReturnDate != null && !expectedReturnDate.equals("null")) {
                    returnsPerDay.merge(expectedReturnDate, 1, Integer::sum);
                }
            }
        }
        
        // Add data to series sorted by date
        returnsPerDay.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> series.getData().add(
                new XYChart.Data<>(entry.getKey(), entry.getValue())
            ));
            
        xAxis.setLabel("Expected Return Date");
        yAxis.setLabel("Number of Books");
    }

    private void setupBooksPerSubscriberChart(XYChart.Series<String, Number> series) {
        Map<String, Integer> booksPerSubscriber = new HashMap<>();
        
        for (String record : currentBorrowedBooks) {
            String[] parts = record.split(", ");
            if (parts.length >= 2) {
                String subscriberId = parts[1].trim();  // subscriber_id
                booksPerSubscriber.merge(subscriberId, 1, Integer::sum);
            }
        }
        
        // Add top 10 subscribers with most books
        booksPerSubscriber.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(10)
            .forEach(entry -> series.getData().add(
                new XYChart.Data<>("Subscriber " + entry.getKey(), entry.getValue())
            ));
            
        xAxis.setLabel("Subscriber ID");
        yAxis.setLabel("Number of Books");
    }
    
    private void setupDelayedReturnsChart(XYChart.Series<String, Number> series) {
        Map<String, Integer> delayedReturns = new HashMap<>();
        
        for (String record : currentBorrowedBooks) {
            String[] parts = record.split(", ");
            if (parts.length >= 11) {  // Make sure we have delay_days data
                String subscriberId = parts[1].trim();
                int delayDays = Integer.parseInt(parts[10].trim());
                if (delayDays > 0) {
                    delayedReturns.merge(subscriberId, delayDays, Integer::sum);
                }
            }
        }
        
        // Show subscribers with delayed returns
        delayedReturns.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(10)
            .forEach(entry -> series.getData().add(
                new XYChart.Data<>("Subscriber " + entry.getKey(), entry.getValue())
            ));
            
        xAxis.setLabel("Subscriber ID");
        yAxis.setLabel("Days Delayed");
    }

    @FXML
    public void Back(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/BorrowedBooksReport.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/BorrowedBooksReport.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Borrowed Books Report");
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