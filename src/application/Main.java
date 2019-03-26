package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class Main extends Application {
  public static final String DB_URL = "jdbc:postgresql://comp421.cs.mcgill.ca/cs421";
  public static final String DB_USER = "cs421g40";
  public static final String DB_PASSWORD = "Team40Db";
  private Connection conn;


  @Override
  public void start(Stage stage) throws SQLException {
    Properties props = new Properties();
    props.setProperty("user", DB_USER);
    props.setProperty("password", DB_PASSWORD);

    this.conn = DriverManager.getConnection(DB_URL, props);

    PreparedStatement stmtPreparedStatement = conn.prepareStatement("SELECT\r\n"
        + "    COALESCE(year, EXTRACT(YEAR from now())-1), months.month,\r\n"
        + "    COALESCE(numOrders, 0) as \"Number of Orders per month\"\r\n" + "FROM \r\n"
        + "  (SELECT EXTRACT(YEAR from creationdatetime) as year, EXTRACT(MONTH from creationdatetime) AS month , COUNT(*) AS numOrders\r\n"
        + "    FROM Orders\r\n"
        + "    Where EXTRACT(YEAR from creationdatetime) = EXTRACT(YEAR from now())-1\r\n"
        + "    GROUP BY 1,2) as items_count RIGHT JOIN (SELECT 1 AS MONTH\r\n" + "    UNION\r\n"
        + "        SELECT 2 AS MONTH\r\n" + "    UNION\r\n" + "        SELECT 3 AS MONTH\r\n"
        + "    UNION\r\n" + "        SELECT 4 AS MONTH\r\n" + "    UNION\r\n"
        + "        SELECT 5 AS MONTH\r\n" + "    UNION\r\n" + "        SELECT 6 AS MONTH\r\n"
        + "    UNION\r\n" + "        SELECT 7 AS MONTH\r\n" + "    UNION\r\n"
        + "        SELECT 8 AS MONTH\r\n" + "    UNION\r\n" + "        SELECT 9 AS MONTH\r\n"
        + "    UNION\r\n" + "        SELECT 10 AS MONTH\r\n" + "    UNION\r\n"
        + "        SELECT 11 AS MONTH\r\n" + "    UNION\r\n"
        + "        SELECT 12 AS MONTH) as months on items_count.Month = months.month order by 2");

    ResultSet rSet = stmtPreparedStatement.executeQuery();
    XYChart.Series<String, Number> series = new XYChart.Series();
    series.setName("2018");
    
    while(rSet.next()) {
      series.getData().add(new XYChart.Data<String, Number>(rSet.getString("month"), (Number) rSet.getInt(3)));
    }
    final CategoryAxis xAxis = new CategoryAxis();
    final Axis<Number> yAxis = new NumberAxis();
    final BarChart<String, Number> barChart = new BarChart<String, Number>(xAxis, yAxis);
    barChart.setTitle("Orders per month");
    xAxis.setLabel("Month");
    yAxis.setLabel("Number of Orders");
    barChart.getData().add(series);
    
    stage.setTitle("Database data visualization");
    
    Scene scene = new Scene(barChart, 800, 600);
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
