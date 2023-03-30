import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

/**
 * Class that contains the characteristics of setting up the Client GUI
 */
public class ClientGuiDriver extends Application {
    /**
     * Method that loads the Client GUI
     * @param primaryStage Building the primary stage container for the Client GUI
     * @throws IOException Io Exception if interrupted or failed
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent welcomePageFXML = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("welcome-page.fxml")));
        Scene welcomeScene = new Scene(welcomePageFXML);
        primaryStage.setTitle("Wild Wild Words");
        primaryStage.setScene(welcomeScene);
        primaryStage.show();
    }

    //main method to launch
    public static void main(String[] args) {
        launch(args);
    }
}