import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class WelcomePageController {
    /**
     * This is an input box that gets the player's name that is about to join the waiting room
     */
    public TextField playerNameTextField;
    public AnchorPane anchor;

    /**
     * This function is called with the 'play game' button is pressed. The function sends a HELLO to server and waits for CLIENT ACK,
     * when that is received, then the scene is switched to the waiting room
     * @throws IOException when waiting-room.fxml cannot be loaded
     */

    public void initialize() {
        anchor.setStyle("-fx-background-color: #ffe6a7");
    }

    public void enterWaitingRoom() throws IOException {
        if (!playerNameTextField.getText().isEmpty()) {
            // send DP containing playNameTextField contents to Server
            GetClient.getInstance().getClient().sendPacket(new Information("HELLO", playerNameTextField.getText()));
            // wait for confirmation from Server
            GetClient.getInstance().getClient().receivePacket();
            // go to the waiting room
            Parent waitingRoomFXML = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("waiting-room(1).fxml")));
            Stage stage = (Stage) playerNameTextField.getScene().getWindow();
            stage.setScene(new Scene(waitingRoomFXML));
            stage.show();
        }
    }
}
