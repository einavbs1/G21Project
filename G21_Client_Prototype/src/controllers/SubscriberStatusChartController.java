package controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import entity.Subscriber;
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

public class SubscriberStatusChartController {

    @FXML
    private BarChart<String, Number> subscriberChart;
    
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

    private List<String> currentSubscribers;

    @FXML
    public void initialize() {
        setupChartOptions();
        loadSubscriberData();
        updateChart();
    }

    private void setupChartOptions() {
        chartTypeCombo.getItems().addAll(
            "Subscribers by Status",
            "Status Distribution"
        );
        chartTypeCombo.setValue("Subscribers by Status");
        chartTypeCombo.setOnAction(event -> updateChart());
    }

    private void loadSubscriberData() {
        try {
            currentSubscribers = Subscriber.getMonthlySubscriberStats();
            if (currentSubscribers == null || currentSubscribers.isEmpty()) {
                System.out.println("No subscriber data available");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading subscriber data: " + e.getMessage());
        }
    }

    private void updateChart() {
        subscriberChart.getData().clear();
        
        if (currentSubscribers == null || currentSubscribers.isEmpty()) {
            return;
        }
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        
        switch(chartTypeCombo.getValue()) {
            case "Subscribers by Status":
                series.setName("Number of Subscribers");
                setupSubscribersByStatusChart(series);
                break;
                
            case "Status Distribution":
                series.setName("Status Distribution");
                setupStatusDistributionChart(series);
                break;
        }
        
        subscriberChart.getData().add(series);
    }

    private void setupSubscribersByStatusChart(XYChart.Series<String, Number> series) {
        Map<String, Integer> statusCount = new HashMap<>();
        
        // Count subscribers for each status
        for (String record : currentSubscribers) {
            String[] parts = record.split(", ");
            String status = parts[2].trim();
            statusCount.merge(status, 1, Integer::sum);
        }
        
        // Add data to series
        statusCount.forEach((status, count) -> 
            series.getData().add(new XYChart.Data<>(status, count))
        );
        
        xAxis.setLabel("Subscriber Status");
        yAxis.setLabel("Number of Subscribers");
    }

    private void setupStatusDistributionChart(XYChart.Series<String, Number> series) {
        Map<String, Integer> statusCount = new HashMap<>();
        int totalSubscribers = currentSubscribers.size();
        
        // Count subscribers for each status
        for (String record : currentSubscribers) {
            String[] parts = record.split(", ");
            String status = parts[2].trim();
            statusCount.merge(status, 1, Integer::sum);
        }
        
        // Calculate and add percentages to series
        statusCount.forEach((status, count) -> {
            double percentage = (count * 100.0) / totalSubscribers;
            series.getData().add(new XYChart.Data<>(status, percentage));
        });
        
        xAxis.setLabel("Subscriber Status");
        yAxis.setLabel("Percentage of Subscribers (%)");
    }

    @FXML
    public void Back(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SubscriberStatusReport.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/SubscriberStatusReport.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Subscriber Status Report");
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