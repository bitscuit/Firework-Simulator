package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

/**
 * A console based testing program for assignment 3.  This program will run the simulation
 * and display the number of particles generated at each time interval as well as a "snapshot"
 * of the particle locations at 2.0 seconds into the simulation.  Due to the the random aspects
 * of the simulation your numbers may not match the ones shown in the sample output.
 * 
 */
public class Main extends Application{
	
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("Layout.fxml"));
			Scene scene = new Scene(root, root.getMaxWidth(), root.getMaxHeight());
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Roman Candle Firework");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Obtains the required parameters, runs the simulation and displays some results.
	 * @param args Not used...
	 */
	public static void main(String[] args) {
		launch(args);
	} // end main

} // end Assn3_Testing
