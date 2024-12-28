package application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MapInterface extends Application {

    private Graph graph; // The graph to hold capitals and edges
    private Group group; // Group for holding all graphical elements
    private ImageView imageView;
    private Image mapImage;

    // Latitude and longitude boundaries
    private final double minLongitude = -180;
    private final double maxLongitude = 180;
    private final double minLatitude = -90;
    private final double maxLatitude = 90;

    @Override
    public void start(Stage primaryStage) {
        // Initialize the graph with data from a file
        initializeGraphFromFile("C:\\Users\\Lenovo\\Desktop\\Algorithm\\Capitals.txt");

        // Create the main layout
        BorderPane bPane = new BorderPane();

        // Title
        Label title = new Label("Routes between the world's capitals using Dijkstra's algorithm");
        title.setPadding(new Insets(10));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.DARKBLUE);
        title.setStyle("-fx-background-color: lightblue; -fx-border-color: darkblue; -fx-border-width: 2px;");
        title.setAlignment(Pos.CENTER);
        bPane.setTop(title);
        bPane.setAlignment(title, Pos.CENTER);
        bPane.setPadding(new Insets(10));

        // Map pane with plotted capitals
        group = new Group();
        mapImage = new Image("file:C:/Users/Lenovo/Desktop/Algorithm/blankWorldMap3.jpg");
        imageView = new ImageView(mapImage);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(1000); // Set the desired width
        imageView.setFitHeight(650); // Set the desired height
        group.getChildren().add(imageView);

        plotCapitalsOnMap();

        // Control panel
        bPane.setRight(createControlPanel());
        bPane.setCenter(new StackPane(group));
        bPane.setPadding(new Insets(10));

        // Scene and Stage setup
        Scene scene = new Scene(bPane, 1250, 650);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Capitals Map");
        primaryStage.show();
    }

    private void initializeGraphFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();

            // Read the number of capitals and edges
            String[] parts = line.split(",");
            int numOfCapitals = Integer.parseInt(parts[0].trim());
            int numOfEdges = Integer.parseInt(parts[1].trim());

            // Initialize the graph
            graph = new Graph(numOfCapitals);

            // Read capitals
            for (int i = 0; i < numOfCapitals; i++) {
                line = reader.readLine();
                parts = line.split(",");
                String name = parts[0].trim();
                double latitude = Double.parseDouble(parts[1].trim());
                double longitude = Double.parseDouble(parts[2].trim());
                graph.addCapital(name, latitude, longitude);
            }

            // Read edges
            for (int i = 0; i < numOfEdges; i++) {
                line = reader.readLine();
                parts = line.split(",");
                String from = parts[0].trim();
                String to = parts[1].trim();
                double cost = Double.parseDouble(parts[2].trim());
                int time = Integer.parseInt(parts[3].trim());
                graph.addEdge(from, to, cost, time);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void plotCapitalsOnMap() {
        Capital[] capitals = graph.getVertices();

        // Ensure plotting occurs after the stage is fully loaded
        javafx.application.Platform.runLater(() -> {
            // Get the actual displayed bounds of the ImageView
            double displayedWidth = imageView.getBoundsInParent().getWidth();
            double displayedHeight = imageView.getBoundsInParent().getHeight();
            double imageX = imageView.getBoundsInParent().getMinX();
            double imageY = imageView.getBoundsInParent().getMinY();

            // Debugging bounds
            System.out.println("Image Bounds - X: " + imageX +
                               ", Y: " + imageY +
                               ", Width: " + displayedWidth +
                               ", Height: " + displayedHeight);

            for (Capital capital : capitals) {
                if (capital != null) {
                    // Transform latitude and longitude to screen coordinates
                    double x = imageX + ((capital.getLongitude() - minLongitude) / (maxLongitude - minLongitude)) * displayedWidth;
                    double y = imageY + ((maxLatitude - capital.getLatitude()) / (maxLatitude - minLatitude)) * displayedHeight;

                    // Debugging output for accuracy
                    System.out.println("Capital: " + capital.getCapitalName());
                    System.out.println("Latitude: " + capital.getLatitude() + ", Longitude: " + capital.getLongitude());
                    System.out.println("Mapped X: " + x + ", Mapped Y: " + y);

                    // Create a circle marker centered on (x, y)
                    Circle marker = new Circle(x, y, 5, Color.RED);
                    group.getChildren().add(marker);

                    // Add a label slightly below the marker
                    Text label = new Text(x + 5, y + 5, capital.getCapitalName());
                    label.setFont(Font.font("Arial", FontWeight.BOLD, 10));
                    label.setFill(Color.WHITE);
                    group.getChildren().add(label);
                }
            }
        });
    }


    private VBox createControlPanel() {
        VBox controlPanel = new VBox(5); // Increased spacing for better layout
        controlPanel.setPadding(new Insets(10));
        controlPanel.setStyle("-fx-border-color: #4a90e2; -fx-border-width: 2; " +
                             "-fx-background-color: linear-gradient(to bottom, #e6f7ff, #cce7ff);");

        controlPanel.setMaxWidth(200);
        Label sourceLabel = new Label("Source:");
        sourceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        sourceLabel.setTextFill(Color.DARKBLUE);
        ComboBox<String> sourceComboBox = new ComboBox<>();
        for (int i = 0; i < graph.getNumberOfVertices(); i++) {
            sourceComboBox.getItems().add(graph.getVertex(i).getCapitalName());
        }

        Label targetLabel = new Label("Target:");
        targetLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        targetLabel.setTextFill(Color.DARKBLUE);
        ComboBox<String> targetComboBox = new ComboBox<>();
        for (int i = 0; i < graph.getNumberOfVertices(); i++) {
            targetComboBox.getItems().add(graph.getVertex(i).getCapitalName());
        }

        Label filterLabel = new Label("Filter:");
        filterLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        filterLabel.setTextFill(Color.DARKBLUE);
        ComboBox<String> filterComboBox = new ComboBox<>();
        filterComboBox.getItems().addAll("Distance", "Cost", "Time");

        TextArea pathTextArea = new TextArea();
        pathTextArea.setPromptText("Path will appear here");
        pathTextArea.setEditable(false);
        pathTextArea.setStyle("-fx-font-size: 10; -fx-border-color: #4a90e2;");

        TextField distanceTextField = new TextField();
        distanceTextField.setPromptText("Distance");
        distanceTextField.setEditable(false);
        distanceTextField.setStyle("-fx-font-size: 12; -fx-border-color: #4a90e2;");

        TextField costTextField = new TextField();
        costTextField.setPromptText("Cost");
        costTextField.setEditable(false);
        costTextField.setStyle("-fx-font-size: 12; -fx-border-color: #4a90e2;");

        TextField timeTextField = new TextField();
        timeTextField.setPromptText("Time");
        timeTextField.setEditable(false);
        timeTextField.setStyle("-fx-font-size: 12; -fx-border-color: #4a90e2;");

        Button runButton = new Button("Run");
        runButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white; " +
                            "-fx-font-size: 14; -fx-border-radius: 5; -fx-background-radius: 5;");
        runButton.setOnMouseEntered(e -> runButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white;"));
        runButton.setOnMouseExited(e -> runButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;"));

        Label pathLb = new Label("Path:");
        Label distanceLb = new Label("Distance:");
        Label costLb = new Label("Cost:");
        Label timeLb = new Label("Time:");
        pathLb.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        distanceLb.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        costLb.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        timeLb.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        controlPanel.getChildren().addAll(
            sourceLabel, sourceComboBox,
            targetLabel, targetComboBox,
            filterLabel, filterComboBox,
            runButton,
            pathLb, pathTextArea,
            distanceLb, distanceTextField,
            costLb, costTextField,
            timeLb, timeTextField
            
        );

        return controlPanel;
    }


    public static void main(String[] args) {
        launch();
    }
}
