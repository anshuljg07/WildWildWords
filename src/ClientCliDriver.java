import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class that contains the command line client user interface
 */
public class ClientCliDriver {

    //Main method for the command line client user interface
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Welcome player
        System.out.print("Enter name: ");
        String playerName = scanner.nextLine(); // get players name
        GetClient.getInstance().getClient().sendPacket(new Information("HELLO", playerName)); // send HELLO
        GetClient.getInstance().getClient().receivePacket(); // wait for CLIENT ACK


        // Game
        while (!GetClient.getInstance().getClient().getGameOver()) {
            readyUp(scanner);
            playRound(scanner);
            displayLeaderboard();
        }
        readyUp(scanner);
        playRound(scanner);
        System.out.println("GAME OVER");
        displayLeaderboard();
    }

    /**
     * method that takes in a scanner for the Client to ready up and sends the info to the server
     * @param scanner scanner input by the user to ready up
     */

    private static void readyUp(Scanner scanner) {
        System.out.print("Enter 'READY' to ready up: ");
        String status = scanner.nextLine();
        while (!status.trim().equalsIgnoreCase("READY")) {
            System.out.print("Enter 'READY' to ready up: ");
            status = scanner.nextLine();
        }
        GetClient.getInstance().getClient().sendPacket(new Information("READY STATE CHANGED", true, GetClient.getInstance().getClient().getID())); // send READY STATE CHANGED
        System.out.println("Waiting for everyone to ready up ...");
        if (!GetClient.getInstance().getClient().getGameOver()) {
            GetClient.getInstance().getClient().receivePacket(); // wait for ROUND INFO
            System.out.println("Round is starting ...");
        }
    }

    /**
     * Method that takes in a scanner input of "answers" inside the
     * @param scanner scanner input by the user to input words based of the letter scramble
     */
    private static void playRound(Scanner scanner) {
        String letters = GetClient.getInstance().getClient().getLetters();
        System.out.println("\nRound " + GetClient.getInstance().getClient().getRoundNumber() + '\n');
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 60000) {
            System.out.println("\nLETTERS: " + letters.toUpperCase());
            System.out.println("Round score: " + GetClient.getInstance().getClient().getRoundScore());
            System.out.println("Total score: " + GetClient.getInstance().getClient().getTotalScore());
            System.out.print("Enter word: ");
            String userEntry = scanner.nextLine();
            GetClient.getInstance().getClient().sendPacket(new Information("NEW ANSWER", userEntry, GetClient.getInstance().getClient().getID())); // send NEW ANSWER
            GetClient.getInstance().getClient().receivePacket(); // wait for POINTS AWARDED
        }
    }

    /**
     * Method that displays the leaderboard inside the command line
     */

    private static void displayLeaderboard() {
        Information info = new Information("ROUND OVER"); // send ROUND OVER
        info.setID(GetClient.getInstance().getClient().getID());
        GetClient.getInstance().getClient().sendPacket(info);
        GetClient.getInstance().getClient().receivePacket(); // wait for ROUND LEADERBOARD, or FINAL LEADERBOARD
        GetClient.getInstance().getClient().setRoundNumber(GetClient.getInstance().getClient().getRoundNumber() + 1);
        ArrayList<String> players = GetClient.getInstance().getClient().getSortedLeaderBoardPlayerList();
        ArrayList<Integer> totalScore = GetClient.getInstance().getClient().getSortedLeaderBoardTotalScoreList();
        System.out.println("\nLeaderboard:");
        if (players.size() == totalScore.size()) {
            for (int i = 0; i < players.size(); i++) {
                System.out.println(players.get(i) + ": " + totalScore.get(i) + " points");
            }
            System.out.print('\n');
        }
    }
}
