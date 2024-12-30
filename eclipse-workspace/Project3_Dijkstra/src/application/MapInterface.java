package application;

import java.io.BufferedReader;
import java.io.File;
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
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MapInterface extends Application {

	private Graph graph; // The graph to hold capitals and edges
	private Group group; // Group for holding all graphical elements
	private ImageView imageView;
	private Image mapImage;
	private ComboBox<String> sourceComboBox;
	private ComboBox<String> targetComboBox;
	TextField timeTextField;
	ComboBox<String> filterComboBox;
	TextField costTextField;
	TextField distanceTextField;
	TextArea pathTextArea;
	private ComboBox<String> activeComboBox; // Track the currently active combo box
    private Circle sourceCircle, targetCircle; // Track the two blue circles
    private Line[] pathLines = new Line[100]; // Initial size of 100
    private int pathLineCount = 0; // Counter for the current number of lines
	private final double minLongitude = -180;
	private final double maxLongitude = 180;
	private final double minLatitude = -90;
	private final double maxLatitude = 90;
	private Line[] arrowHeads = new Line[200]; // Array to store arrowhead lines
	private int arrowHeadCount = 0; // Counter for arrowhead lines
	Label fileName = new Label();

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
		bPane.setRight(createControlPanel(primaryStage));
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

	        // Check if the file is empty
	        if (line == null) {
	            showAlert(Alert.AlertType.ERROR, "File Error", "The file is empty.");
	            return;
	        }

	        String[] parts = line.split(",");
	        if (parts.length != 2) {
	            showAlert(Alert.AlertType.ERROR, "File Error", "The first line must contain exactly two values: the number of capitals and edges.");
	            return;
	        }

	        int numOfCapitals;
	        int numOfEdges;
	        try {
	            numOfCapitals = Integer.parseInt(parts[0].trim());
	            numOfEdges = Integer.parseInt(parts[1].trim());
	        } catch (NumberFormatException e) {
	            showAlert(Alert.AlertType.ERROR, "File Error", "The first line must contain numeric values for capitals and edges count.");
	            return;
	        }

	        graph = new Graph(numOfCapitals);

	        // Read and validate the capitals
	        int actualCapitalsCount = 0;
	        for (int i = 0; i < numOfCapitals; i++) {
	            line = reader.readLine();
	            if (line == null) {
	                showAlert(Alert.AlertType.ERROR, "File Error", "The file does not contain enough capitals as specified in the first line.");
	                return;
	            }

	            parts = line.split(",");
	            if (parts.length != 3) {
	                showAlert(Alert.AlertType.ERROR, "File Error", "Each capital line must contain three values: name, latitude, and longitude.");
	                return;
	            }

	            String name = parts[0].trim();
	            double latitude;
	            double longitude;
	            try {
	                latitude = Double.parseDouble(parts[1].trim());
	                longitude = Double.parseDouble(parts[2].trim());
	            } catch (NumberFormatException e) {
	                showAlert(Alert.AlertType.ERROR, "File Error", "Latitude and longitude must be numeric values for capital: " + name);
	                return;
	            }

	            graph.addCapital(name, latitude, longitude);
	            actualCapitalsCount++;
	        }

	        if (actualCapitalsCount != numOfCapitals) {
	            showAlert(Alert.AlertType.ERROR, "File Error", "The actual number of capitals does not match the count specified in the first line.");
	            return;
	        }

	        // Read and validate the edges
	        int actualEdgesCount = 0;
	        for (int i = 0; i < numOfEdges; i++) {
	            line = reader.readLine();
	            if (line == null) {
	                showAlert(Alert.AlertType.ERROR, "File Error", "The file does not contain enough edges as specified in the first line.");
	                return;
	            }

	            parts = line.split(",");
	            if (parts.length != 4) {
	                showAlert(Alert.AlertType.ERROR, "File Error", "Each edge line must contain four values: from, to, cost, and time.");
	                return;
	            }

	            String from = parts[0].trim();
	            String to = parts[1].trim();
	            double cost;
	            int time;
	            try {
	                cost = Double.parseDouble(parts[2].trim().replace("$", ""));
	                time = Integer.parseInt(parts[3].trim().replace("min", ""));
	            } catch (NumberFormatException e) {
	                showAlert(Alert.AlertType.ERROR, "File Error", "Cost and time must be numeric values for edge: " + from + " -> " + to);
	                return;
	            }

	            graph.addEdge(from, to, cost, time);
	            actualEdgesCount++;
	        }

	        if (actualEdgesCount != numOfEdges) {
	            showAlert(Alert.AlertType.ERROR, "File Error", "The actual number of edges does not match the count specified in the first line.");
	            return;
	        }

	        System.out.println("Graph successfully initialized with " + numOfCapitals + " capitals and " + numOfEdges + " edges.");

	    } catch (IOException e) {
	        showAlert(Alert.AlertType.ERROR, "File Error", "Failed to read the file. " + e.getMessage());
	    }
	}
	private void showAlert(Alert.AlertType type, String title, String message) {
	    Alert alert = new Alert(type);
	    alert.setTitle(title);
	    alert.setHeaderText(null);
	    alert.setContentText(message);
	    alert.showAndWait();
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
			System.out.println("Image Bounds - X: " + imageX + ", Y: " + imageY + ", Width: " + displayedWidth
					+ ", Height: " + displayedHeight);

			for (Capital capital : capitals) {
				if (capital != null) {
					// Transform latitude and longitude to screen coordinates
					double x = imageX + ((capital.getLongitude() - minLongitude) / (maxLongitude - minLongitude))
							* displayedWidth;
					double y = imageY
							+ ((maxLatitude - capital.getLatitude()) / (maxLatitude - minLatitude)) * displayedHeight;

					// Debugging output for accuracy
					System.out.println("Capital: " + capital.getCapitalName());
					System.out.println("Latitude: " + capital.getLatitude() + ", Longitude: " + capital.getLongitude());
					System.out.println("Mapped X: " + x + ", Mapped Y: " + y);

					// Create a circle marker centered on (x, y)
					Circle marker = new Circle(x, y, 5, Color.RED);
					marker.setOnMouseClicked(event -> handleMarkerClick(marker, capital.getCapitalName()));
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

	private VBox createControlPanel(Stage primaryStage) {
		VBox controlPanel = new VBox(5); 
		controlPanel.setPadding(new Insets(10));
		controlPanel.setStyle("-fx-border-color: #4a90e2; -fx-border-width: 2; "
				+ "-fx-background-color: linear-gradient(to bottom, #e6f7ff, #cce7ff);");

		controlPanel.setMaxWidth(200);
		FileChooser fileChooser = new FileChooser();
        Button chooseFileButton = new Button("Choose File");
        chooseFileButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                initializeGraphFromFile(file.getPath());
                
                fileName.setText(file.getName());

            }
        });
        
		Label sourceLabel = new Label("Source:");
		sourceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
		sourceLabel.setTextFill(Color.DARKBLUE);
		sourceComboBox = new ComboBox<>();
		for (int i = 0; i < graph.getNumberOfVertices(); i++) {
			sourceComboBox.getItems().add(graph.getVertex(i).getCapitalName());
		}
		setupComboBoxFocus(sourceComboBox); // Setup focus handling for the source ComboBox

		Label targetLabel = new Label("Target:");
		targetLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
		targetLabel.setTextFill(Color.DARKBLUE);
		targetComboBox = new ComboBox<>();
		for (int i = 0; i < graph.getNumberOfVertices(); i++) {
			targetComboBox.getItems().add(graph.getVertex(i).getCapitalName());
		}
		setupComboBoxFocus(targetComboBox); // Setup focus handling for the target ComboBox

		Label filterLabel = new Label("Filter:");
		filterLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
		filterLabel.setTextFill(Color.DARKBLUE);
		 filterComboBox = new ComboBox<>();
		filterComboBox.getItems().addAll("Distance", "Cost", "Time");

		pathTextArea = new TextArea();
		pathTextArea.setPromptText("Path will appear here");
		pathTextArea.setEditable(false);
		pathTextArea.setStyle("-fx-font-size: 10; -fx-border-color: #4a90e2;");

		distanceTextField = new TextField();
		distanceTextField.setPromptText("Distance");
		distanceTextField.setEditable(false);
		distanceTextField.setStyle("-fx-font-size: 12; -fx-border-color: #4a90e2;");

		costTextField = new TextField();
		costTextField.setPromptText("Cost");
		costTextField.setEditable(false);
		costTextField.setStyle("-fx-font-size: 12; -fx-border-color: #4a90e2;");
	
		 timeTextField = new TextField();
		timeTextField.setPromptText("Time");
		timeTextField.setEditable(false);
		timeTextField.setStyle("-fx-font-size: 12; -fx-border-color: #4a90e2;");

//		 // Add ToggleButtons for Source and Target
//	    sourceToggle = new ToggleButton("Activate Source");
//	    
//	    targetToggle = new ToggleButton("Activate Target");
//	 // Ensure only one ToggleButton is active at a time
//	    ToggleGroup toggleGroup = new ToggleGroup();
//	    sourceToggle.setToggleGroup(toggleGroup);
//	    targetToggle.setToggleGroup(toggleGroup);

		Button runButton = new Button("Run");
		runButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white; "
				+ "-fx-font-size: 14; -fx-border-radius: 5; -fx-background-radius: 5;");
		runButton.setOnMouseEntered(e -> runButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white;"));
		runButton.setOnMouseExited(e -> runButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;"));
	
	    runButton.setOnAction(e -> calculateShortestPath());

		Button clearBt = new Button("Clear");
		clearBt.setStyle("-fx-background-color: Blue; -fx-text-fill: white; "
				+ "-fx-font-size: 14; -fx-border-radius: 5; -fx-background-radius: 5;");
		clearBt.setOnMouseEntered(e -> clearBt.setStyle("-fx-background-color: LightBlue; -fx-text-fill: white;"));
		clearBt.setOnMouseExited(e -> clearBt.setStyle("-fx-background-color: Blue; -fx-text-fill: white;"));

		clearBt.setOnAction(e -> clearFields());

		Label pathLb = new Label("Path:");
		Label distanceLb = new Label("Distance:");
		Label costLb = new Label("Cost:");
		Label timeLb = new Label("Time:");
		pathLb.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		distanceLb.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		costLb.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		timeLb.setFont(Font.font("Arial", FontWeight.BOLD, 12));

		HBox hbox = new HBox(10);
		hbox.getChildren().addAll(runButton, clearBt);
		hbox.setAlignment(Pos.CENTER);
		controlPanel.getChildren().addAll(chooseFileButton,fileName,sourceLabel, sourceComboBox, targetLabel, targetComboBox, filterLabel,
				filterComboBox, hbox, pathLb, pathTextArea, distanceLb, distanceTextField, costLb, costTextField,
				timeLb, timeTextField

		);

		return controlPanel;
	}

	private void clearFields() {
	    // Clear combo box selections and text fields
	    sourceComboBox.setValue(null);
	    targetComboBox.setValue(null);
	    distanceTextField.clear();
	    pathTextArea.clear();
	    costTextField.clear();
	    filterComboBox.setValue(null);
	    timeTextField.clear();

	    // Reset circle colors
	    if (sourceCircle != null) sourceCircle.setFill(Color.RED);
	    if (targetCircle != null) targetCircle.setFill(Color.RED);

	    sourceCircle = null;
	    targetCircle = null;

	    // Remove all path lines and arrows
	    for (int i = 0; i < pathLineCount; i++) {
	        group.getChildren().remove(pathLines[i]);
	    }
	    pathLineCount = 0;

	    // Remove all arrowheads
	    for (int i = 0; i < arrowHeadCount; i++) {
	        group.getChildren().remove(arrowHeads[i]);
	    }
	    arrowHeadCount = 0;
	}

	 private void calculateShortestPath() {
		 if(fileName.getText().isEmpty()) {
	            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a file!", ButtonType.OK);
	            alert.show();
	            return;
		 }
	        if (sourceComboBox.getValue() == null || targetComboBox.getValue() == null || filterComboBox.getValue() == null) {
	            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select Source, Target, and Filter!", ButtonType.OK);
	            alert.show();
	            return;
	        }

	        String source = sourceComboBox.getValue();
	        String target = targetComboBox.getValue();
	        String filter = filterComboBox.getValue();

	        try {
	            GraphTable[] T = new GraphTable[graph.getNumberOfVertices()];
	            graph.dijkstra(source, T, filter);

	            int targetIndex = graph.getVertexIndex(target);
	            double totalMetric = T[targetIndex].getDistance();

	            StringBuilder pathBuilder = new StringBuilder();
	            graph.buildPath(targetIndex, T, pathBuilder);
	            pathTextArea.setText(pathBuilder.toString());
	            
	            double totalDistance = 0;
	            double totalCost = 0;
	            int totalTime = 0;

	            Capital current = graph.getVertex(targetIndex);

	            while (T[targetIndex].getPath() != null) {
	                Capital previous = T[targetIndex].getPath();
	                int previousIndex = graph.getVertexIndex(previous.getCapitalName());

	                // Find the edge connecting 'previous' to 'current'
	                for (Edge edge : graph.getVertex(previousIndex).getEdges()) {
	                    if (edge.getDestination().equals(current.getCapitalName())) {
	                    	System.out.println(edge.getDestination()+ " "+edge.getDistance());
	                        totalDistance += edge.getDistance();
	                        totalCost += edge.getCost();
	                        totalTime += edge.getTime();
	                        

	                        // Draw the line for the edge
	                        drawPathLine(previous, current);
	                        
	                        break;
	                    }
	                }

	                // Move to the previous vertex
	                targetIndex = previousIndex;
	                current = previous;
	            }

	            distanceTextField.setText(String.format("%.2f km", totalDistance));
	            costTextField.setText(String.format("$%.2f", totalCost));
	            timeTextField.setText(String.format("%d mins", totalTime));

	        } catch (IllegalArgumentException ex) {
	            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
	            alert.show();
	        }
	    }

	 private void drawPathLine(Capital from, Capital to) {
		    double displayedWidth = imageView.getBoundsInParent().getWidth();
		    double displayedHeight = imageView.getBoundsInParent().getHeight();
		    double imageX = imageView.getBoundsInParent().getMinX();
		    double imageY = imageView.getBoundsInParent().getMinY();

		    double startX = imageX + ((from.getLongitude() - minLongitude) / (maxLongitude - minLongitude)) * displayedWidth;
		    double startY = imageY + ((maxLatitude - from.getLatitude()) / (maxLatitude - minLatitude)) * displayedHeight;

		    double endX = imageX + ((to.getLongitude() - minLongitude) / (maxLongitude - minLongitude)) * displayedWidth;
		    double endY = imageY + ((maxLatitude - to.getLatitude()) / (maxLatitude - minLatitude)) * displayedHeight;

		    // Draw the main line
		    Line line = new Line(startX, startY, endX, endY);
		    line.setStroke(Color.BLUE);
		    line.setStrokeWidth(2);
		    group.getChildren().add(line);

		    // Store the line
		    if (pathLineCount == pathLines.length) {
		        Line[] newArray = new Line[pathLines.length * 2];
		        System.arraycopy(pathLines, 0, newArray, 0, pathLines.length);
		        pathLines = newArray;
		    }
		    pathLines[pathLineCount++] = line;

		    // Draw arrowhead
		    double arrowLength = 10; // Length of arrowhead lines
		    double arrowAngle = Math.toRadians(30); // Angle for arrowhead
		    double angle = Math.atan2(endY - startY, endX - startX);

		    double arrowX1 = endX - arrowLength * Math.cos(angle - arrowAngle);
		    double arrowY1 = endY - arrowLength * Math.sin(angle - arrowAngle);
		    Line arrowLine1 = new Line(endX, endY, arrowX1, arrowY1);
		    arrowLine1.setStroke(Color.BLUE);
		    arrowLine1.setStrokeWidth(2);
		    group.getChildren().add(arrowLine1);

		    double arrowX2 = endX - arrowLength * Math.cos(angle + arrowAngle);
		    double arrowY2 = endY - arrowLength * Math.sin(angle + arrowAngle);
		    Line arrowLine2 = new Line(endX, endY, arrowX2, arrowY2);
		    arrowLine2.setStroke(Color.BLUE);
		    arrowLine2.setStrokeWidth(2);
		    group.getChildren().add(arrowLine2);

		    // Store the arrowhead lines
		    if (arrowHeadCount + 2 >= arrowHeads.length) {
		        Line[] newArray = new Line[arrowHeads.length * 2];
		        System.arraycopy(arrowHeads, 0, newArray, 0, arrowHeads.length);
		        arrowHeads = newArray;
		    }
		    arrowHeads[arrowHeadCount++] = arrowLine1;
		    arrowHeads[arrowHeadCount++] = arrowLine2;
		}


	private void handleMarkerClick(Circle marker, String capitalName) {
	    if (activeComboBox == null) {
	        Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a ComboBox first!", ButtonType.OK);
	        alert.show();
	        return;
	    }

	    if (activeComboBox == sourceComboBox) {
	        if (sourceCircle != null) sourceCircle.setFill(Color.RED); // Reset previous source circle
	        sourceComboBox.setValue(capitalName);
	        marker.setFill(Color.BLUE);
	        sourceCircle = marker;
	    } else if (activeComboBox == targetComboBox) {
	        if (targetCircle != null) targetCircle.setFill(Color.RED); // Reset previous target circle
	        targetComboBox.setValue(capitalName);
	        marker.setFill(Color.BLUE);
	        targetCircle = marker;
	    }

	    // Ensure only two circles are blue
	    if (sourceCircle != null && targetCircle != null) {
	        if (sourceCircle.equals(targetCircle)) {
	            resetSelection();
	        }
	    }
	}
	private void resetSelection() {
	    if (sourceCircle != null) sourceCircle.setFill(Color.RED);
	    if (targetCircle != null) targetCircle.setFill(Color.RED);

	    sourceComboBox.setValue(null);
	    targetComboBox.setValue(null);

	    sourceCircle = null;
	    targetCircle = null;
	    activeComboBox = null;
	}
	private void setupComboBoxFocus(ComboBox<String> comboBox) {
	    comboBox.focusedProperty().addListener((observable, oldValue, newValue) -> {
	        if (newValue) { // If the ComboBox is focused
	            activeComboBox = comboBox; // Set it as the active ComboBox
	        }
	    });
	}

 
	public static void main(String[] args) {
		launch();
	}
}
