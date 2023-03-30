import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class WaitingRoomController {
    /**
     * This is the ready up button, which tells the server that the client is ready to play the game
     */
    public Button readyUpButton;
//    public AnchorPane anchor;
//
//    public void initialize() {
//        anchor.setStyle("-fx-background-color: #ffe6a7");
//    }

    /**
     * This method is called when the user clicks 'Ready Up'. This sends READY STATE CHANGED to the server and waits for ROUND INFO.
     * When the round information is received, the scene is switched to the game.
     * @throws IOException when game.fxml cannot be loaded
     */
    public void readyUp() throws IOException {
        Information info = new Information("READY STATE CHANGED", true, GetClient.getInstance().getClient().getID());
        GetClient.getInstance().getClient().sendPacket(info);
        // wait for ROUND INFO packet from server, this tells us all players are ready to play
        GetClient.getInstance().getClient().receivePacket();
        // go to game page
        Parent gameFXML = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("game.fxml")));
        Stage stage = (Stage) readyUpButton.getScene().getWindow();
        stage.setScene(new Scene(gameFXML));
        stage.show();
    }
}