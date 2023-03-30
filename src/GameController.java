import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

/**
 * Class that carries the characteristics to utilize the Game Events in the FX Gui
 */
public class GameController {
    public Label currentRoundNumberLabel;
    public GridPane lettersGrid;
    public AnchorPane anchor;
    private Timeline timeline;
    private static final Integer startTime = 30;
    private Integer timeSeconds = startTime;
    public Label roundScoreLabel;
//    public Label totalScoreLabel;
    public Label timerLabel;
    public TextArea answersDisplayTextArea;
    public TextField answerInputTextField;

    /**
     * Method to Get and Display the 5 letters from server and the current round number, and the client's total score.
     * Method also displays the Timer and its functionality for each round
     */
    public void initialize() {
        anchor.setStyle("-fx-background-color: #ffe6a7");
        String letters = GetClient.getInstance().getClient().getLetters();
        float numLetters = letters.length();
        int columnIndex = 0;
        for (Character letter : letters.toCharArray()) {
            // size the column
            ColumnConstraints columnConstraint = new ColumnConstraints();
            columnConstraint.setPercentWidth(100.0 / numLetters);
            lettersGrid.getColumnConstraints().add(columnConstraint);
            // fill the column
            Label letterLabel = new Label(letter.toString());
            letterLabel.setFont(Font.font("Verdana",FontWeight.BOLD, 18));

            lettersGrid.add(letterLabel, columnIndex, 0);
            columnIndex ++;
        }

        int roundNumber = GetClient.getInstance().getClient().getRoundNumber();
        currentRoundNumberLabel.setText("Round " + roundNumber);

//        int totalScore = GetClient.getInstance().getClient().getTotalScore();
//        totalScoreLabel.setText(String.valueOf(totalScore));


        // timer functionality
        timeSeconds = startTime;
        timerLabel.setText(timeSeconds.toString());
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1),
                        new EventHandler() {
                            // KeyFrame event handler

                            /**
                             * Countdown timer functionality
                             * @param event The event
                             */
                            public void handle(Event event) {
                                timeSeconds--;
                                // update timerLabel
                                timerLabel.setText(
                                        timeSeconds.toString());
                                if (timeSeconds <= 15) {
                                    timerLabel.setTextFill(Color.RED);
                                }
                                if (timeSeconds <= 0) {
                                    timeline.stop();

                                    // send ROUND OVER
                                    Information infoOut = new Information("ROUND OVER");
                                    infoOut.setID(GetClient.getInstance().getClient().getID());
                                    GetClient.getInstance().getClient().sendPacket(infoOut);

                                    // wait for LEADERBOARD then change the scene to leaderboard.fxml
                                    GetClient.getInstance().getClient().receivePacket();
                                    Parent gameFXML = null;
                                    try {
                                        gameFXML = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("leaderboard.fxml")));
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    Stage stage = (Stage) answerInputTextField.getScene().getWindow();
                                    stage.setScene(new Scene(gameFXML));
                                    stage.show();
                                    //load the
                                }
                            }
                        }));
        timeline.playFromStart();
    }

    /**
     * method that contains the logic to shuffle the letters and display the post shuffle
     */
    public void shuffleLetters() {
        lettersGrid.getChildren().clear();
        String shuffledLetters = Game.shuffleLetters(GetClient.getInstance().getClient().getLetters());
        int columnIndex = 0;
        for (Character letter : shuffledLetters.toCharArray()) {
            Label letterLabel = new Label(letter.toString());
            letterLabel.setFont(Font.font("Verdana",FontWeight.BOLD, 18));
            lettersGrid.add(letterLabel, columnIndex, 0);
            columnIndex ++;
        }
    }

    /**
     * method that contains the logic to send a word/answer to the server and parse it to receive the score
     */
    public void addWord() {
        // send word to server
        if (!answerInputTextField.getText().isEmpty() && timeSeconds > 0) {
            Information info = new Information("NEW ANSWER", answerInputTextField.getText().trim().toLowerCase(), GetClient.getInstance().getClient().getID());
            GetClient.getInstance().getClient().sendPacket(info);
            GetClient.getInstance().getClient().receivePacket();

            int previousScore = Integer.parseInt(roundScoreLabel.getText());
            roundScoreLabel.setText(String.valueOf(GetClient.getInstance().getClient().getRoundScore()));
//            totalScoreLabel.setText(String.valueOf(GetClient.getInstance().getClient().getTotalScore()));
            if (previousScore < GetClient.getInstance().getClient().getRoundScore()) {
                answersDisplayTextArea.appendText(answerInputTextField.getText() + "\n");
            }
        }
        answerInputTextField.clear();
    }
}