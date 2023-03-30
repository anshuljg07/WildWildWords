import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LeaderboardController {
    public AnchorPane anchor;
    /**
     * Lists the names of all players in the game in order from best to worst
     */
    @FXML
    private ListView<String> namesListView;

    /**
     * Lists the scores of all the players in the game from best to worst
     */
    @FXML
    private ListView<Integer> scoreListView;

    /**
     * A label displaying the current round number
     */
    @FXML
    private Label currentRoundLabel;

    /**
     * A button to go to the next round
     */
    @FXML
    private Button nextRound;

    /**
     * A label saying 'Round'
     */
    @FXML
    private Label roundNameLabel;

    /**
     * Fills both namesListView and scoreListView with the proper information from the Client class
     */
    @FXML
    void initialize(){
        anchor.setStyle("-fx-background-color: #ffe6a7");
        Client client = GetClient.getInstance().getClient();

        //populate the nameListView with the sortedLeaderBoardPlayerNames arraylist
        ObservableList<String> playerNameList = FXCollections.observableArrayList();
        namesListView.setItems(playerNameList);
        playerNameList.addAll(client.getSortedLeaderBoardPlayerList());

        //populate the scoreListView with the sortedLeaderBoardTotalScores arrayList
        ObservableList<Integer> totalScoreList = FXCollections.observableArrayList();
        scoreListView.setItems(totalScoreList);
        totalScoreList.addAll(client.getSortedLeaderBoardTotalScoreList());

        if(client.getGameOver()){
            //set the Round Number label to not visible
            currentRoundLabel.setVisible(false);
            roundNameLabel.setVisible(false);
            //set the next button to not visible and disabled
            nextRound.setVisible(false);
            nextRound.setDisable(true);
        }
        else{
            //set the round number
            currentRoundLabel.setText(String.valueOf(client.getRoundNumber()));
        }
    }

    /**
     * Takes the user to another waiting room
     */
    @FXML
    void moveToNextRound() {
        Parent gameFXML;
        try {
            gameFXML = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("waiting-room(1).fxml")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = (Stage) currentRoundLabel.getScene().getWindow();
        stage.setScene(new Scene(gameFXML));
        stage.show();
    }
}
