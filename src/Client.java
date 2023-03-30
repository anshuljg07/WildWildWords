import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

/**
 * Handles interactions with the server
 */
public class Client {
    private DatagramSocket socket;
    private int ID;
    private String letters;
    private int roundNumber;
    private int roundScore;
    private int totalScore;
    private final ArrayList<Object[]> playersInfo;
    private ArrayList<String> sortedLeaderBoardPlayerList;
    private ArrayList<Integer> sortedLeaderBoardTotalScoreList;
    private Boolean gameOver;

    /**
     * Constructs a new client
     */
    public Client() {
        try {
            socket = new DatagramSocket();
        }
        catch (SocketException socketException){
            socketException.printStackTrace();
            System.exit(1);
        }
        gameOver = false;
        ID = -99;
        roundScore = 0;
        totalScore = 0;
        playersInfo = new ArrayList<>();
        sortedLeaderBoardPlayerList = null;
    }

    /**
     * method that sends an information packet to server
     * @param info information obj
     */
    public void sendPacket(Information info) {
        try {
            byte[] data = Information.objToByte(info);
            DatagramPacket packetToSend = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), 23692);
            socket.send(packetToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that receives information packets from server
     */
    public void receivePacket() {
        try {
            byte[] data = new byte[6400];
            DatagramPacket receivePacket = new DatagramPacket(data, data.length);
            socket.receive(receivePacket);
            Information info = (Information) Information.byteToObj(receivePacket.getData());
            parseInfo(info);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * method that handles client ack from server
     * @param info information obj
     */
    public void handleAcknowledge(Information info) {
        this.ID = info.getID();
    }

    /**
     * method that handles new client from server
     * @param info information obj
     */
    public void handleNewClient(Information info) { //going to be used in the threaded version
        Object data = info.getData();
        if (!(data instanceof Object[])) {
            clientSendErrorPacketToServer();
            return;
        }
        Object[] contents = (Object[]) data;
        Object[] player = new Object[3];
        player[0] = contents[0];
        player[1] = contents[1];
        player[2] = contents[2];
        playersInfo.add(player);
    }

    /**
     * method that handles new round form server
     * @param info information obj
     */
    public void handleNewRound(Information info) {
        roundScore = 0;
        Object data = info.getData();
        if (!(data instanceof Object[])) {
            clientSendErrorPacketToServer();
            return;
        }
        Object[] contents = (Object[]) data;
        this.letters = (String) contents[0];
        this.roundNumber = (Integer) contents[1];
        this.totalScore = (Integer) contents[2];
    }

    /**
     * method that handles points awarded from server
     * @param info information obj
     */
    public void handlePointsAwarded(Information info) {
        Object data = info.getData();
        if (!(data instanceof Integer)) {
            clientSendErrorPacketToServer();
            return;
        }
        int pointsAwarded = (Integer) data;
        this.roundScore += pointsAwarded;
        this.totalScore += pointsAwarded;
    }

    /**
     * method that per round leaderboard request from server
     * @param info information object
     */
    public void handleRoundLeaderboard(Information info) {
        Object data = info.getData();
        if(!(data instanceof Object[])){
            clientSendErrorPacketToServer();
            return;
        }
        Object[] contents = (Object[]) data;
        sortedLeaderBoardPlayerList = (ArrayList<String>) contents[0];
        sortedLeaderBoardTotalScoreList = (ArrayList<Integer>) contents[1];
    }

    /**
     *  method that handles final leaderboard request from server
     * @param info information object
     */
    private void handleFinalLeaderboard(Information info) {
        gameOver = true;
        Object data = info.getData();
        if(!(data instanceof Object[])){
            clientSendErrorPacketToServer();
            return;
        }
        Object[] contents = (Object[]) data;
        sortedLeaderBoardPlayerList = (ArrayList<String>) contents[0];
        sortedLeaderBoardTotalScoreList = (ArrayList<Integer>) contents[1];
    }

    /**
     * Method that sends an error information packet to the server
     */
    public void clientSendErrorPacketToServer(){
        Information clientErrorInfo = new Information("DATA ERROR");
        byte[] data = Information.objToByte(clientErrorInfo);

        DatagramPacket clientErrorPacket = null;
        try {
            clientErrorPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(),23692);
            socket.send(clientErrorPacket);
        } catch (IOException e) {
            e.printStackTrace();
            assert clientErrorPacket != null;
            System.out.println("Error sending packet to " + " IP: " + clientErrorPacket.getAddress() + " Port: " + clientErrorPacket.getPort());
        }
    }

    /**
     * Method that handles the response to server requests
     * @param info information object received from server
     */
    public void parseInfo(Information info) {
        if (info.getAPImessage().equals("CLIENT ACK")) {
            handleAcknowledge(info);
        } else if (info.getAPImessage().equals("NEW CLIENT")) {
            handleNewClient(info);
        } else if (info.getAPImessage().equals("ROUND INFO")) {
            handleNewRound(info);
        } else if (info.getAPImessage().equals("POINTS AWARDED")) {
            handlePointsAwarded(info);
        } else if (info.getAPImessage().equals("ROUND LEADERBOARD")) {
            handleRoundLeaderboard(info);
        } else if (info.getAPImessage().equals("FINAL LEADERBOARD")) {
            handleFinalLeaderboard(info);
        }
    }

    /**
     * retrieves unique identifier for each client
     * @return id
     */
    public int getID() {
        return ID;
    }

    /**
     * getter to retrieve total score of user
     * @return totalScore
     *
     */
    public int getTotalScore() {
        return totalScore;
    }

    /**
     * getter to retrieve the scrambled letters
     * @return letters
     */
    public String getLetters() {
        return letters;
    }

    /**
     * Getter to retrieve round number
     * @return the round number
     */
    public int getRoundNumber() {
        return roundNumber;
    }

    /**
     * setter to retrieve the round number
     * @param roundNumber the round number
     */
    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    /**
     * getter to retrieve user score per round
     * @return roundScore
     */
    public int getRoundScore() {
        return roundScore;
    }


    /**
     * getter for the score of players from highest to lowest after round has been completed
     * @return  sortedLeaderBoardPlayerList
     */
    public ArrayList<String> getSortedLeaderBoardPlayerList() {
        return sortedLeaderBoardPlayerList;
    }

    /**
     * the score of the players from highest to lowest after final round
     * @return sortedLeaderBoardTotalScoreList
     */
    public ArrayList<Integer> getSortedLeaderBoardTotalScoreList() {
        return sortedLeaderBoardTotalScoreList;
    }


    /**
     * Getter for when the game is over
     * @return gameOver
     */
    public Boolean getGameOver() {
        return gameOver;
    }

}