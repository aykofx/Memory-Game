import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class MemoryFX extends Application{
	@Override
	public void start(Stage myStage) throws Exception {
        // Create the top-level node
        // Here we use a FlowPane
        // The UI is created using a separate method in the MemoryField class
		FlowPane rootNode = new MemoryField().initGUI(new FlowPane());
        // Create the scene
        // The constructor receives the top-level node and the size
		Scene myScene = new Scene(rootNode, 480, 590);
		
        // Set the title via stage
		myStage.setTitle("Memory Game");
        // Set the scene
		myStage.setScene(myScene);
        // Prevent resizing
		myStage.setResizable(false);
		// Show the window
		myStage.show();
	}
	
	public static void main(String[] args) {
		// Start the application
		launch(args);
	}
}
