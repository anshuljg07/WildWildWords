import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Class that handles interactions with the client
 * Stores players information
 */
public class Server {
//    private final ArrayList<DB_ClientInfo> ClientList;
    private DatagramSocket socket;
    private int IDGenerator;
    private boolean allClientsReady;

    private Game game;

    private final int numRounds;
    private boolean gameStarted;

    private DatabaseInteractor db;

    public Server(int numRounds){
        this.numRounds = numRounds;
        //static method to initialize all static vars in game class
        Game.readInWords();
        Game.fillLetterScores();

        //First thing: check DB to see if game has already started

        //if started: connect to DB, and then get the Game object and store it as a member variable

        //otherwise: create a game object and store it in the DB, and then set game started in DB to true once all players have done the first ready up

        db = new DatabaseInteractor();
        game = db.getGameData();
        if(game == null){
            game = new Game(numRounds);
            db.createGame(game);
        }

//        gameStarted = false; // replace with db code
//        ClientList = new ArrayList<>(); //replace with db code

        try{
            socket = new DatagramSocket(23692); // TODO: check and make sure that this is the correct port, could be 23690 - 23694 too
        }
        catch(SocketException socketException) {
            socketException.printStackTrace();
            System.exit(1);
        }
    }

    /**
     *
     * @param packet
     */
    public void readPacket(DatagramPacket packet){
        Information info = (Information) Information.byteToObj(packet.getData());
        String APImessage = info.getAPImessage().toLowerCase();

        if(APImessage.equals("hello")){
            acknowledgeClient(packet);

        }
        else if(APImessage.equals("ready state changed")){
            acknowledgeReadyState(packet);

//            if(checkIfAllClientsAreReady() && !gameStarted){
            if(db.allPlayersReady() && !game.getGameStarted()){
                // (DB) modify gameStarted of DB stored game object
                System.out.println("All players are ready");
//                game = new Game(3); //TODO: number of rounds is hard coded but this should be replaced with user input
                game.setGameStarted(true);
                db.changeGameStart();
                playRound(); // send first round
            }
//            else if(checkIfAllClientsAreReady() && gameStarted && !game.getGameDone()){
            else if(db.allPlayersReady() && game.getGameStarted() && !game.getGameDone()){
                game.nextRound();
                playRound();
//                this means that the game has already started, so we are in an intermediate waiting room.
                // instead of instantiating a game just call playRound(), the round will be changed when we send the leaderboard
            }
        }
        else if (APImessage.equals("new answer")){
            checkClientAnswer(packet);
        }

        else if(APImessage.equals("round over")){
            endClientRound(packet);
            if(db.allPlayersNotReady() && game.getGameStarted() && !game.getGameDone()){
//                sendRoundLeaderBoardToClients();
                sendLeaderBoard("ROUND");
            }
            else if(db.allPlayersNotReady() && game.getGameStarted() && game.getGameDone()){
//                sendFinalLeaderBoardToClients()
                sendLeaderBoard("FINAL");

            }

            //set the client's ready status to false


        }
        else if(APImessage.equals("delete player")){

        }
        else if(APImessage.equals("chat message")){

        }
        else if (APImessage.equals("leave game")) {

        }
        else if (APImessage.equals("start game")) {

        }
        else if (APImessage.equals("restart game")){

        }
        else{
            //invalid API CALL, send error packet back to client
        }


    }

//    private void startGame() {
//        game = new Game();
//        while(game.getRoundNumber() <= numRounds){
//
//        }
//
//        for(int i = 0; i < numRounds; i++){
//            playRound();
//        }
//
//        //send the initial round information
//
//        //then wait for users to send me the words they generated
//            //score the words then send them back boolean correct true/false, points awarded, round point total, global point total, previous word history
//
//    }

    //Called once when the game starts, and then called again when all users send round over package.

    /**
     *
     */
    private void playRound() {
        //send round information to clients: 5 letters, round number, client's total score.
        Object[] roundInfo = new Object[3];
        roundInfo[0] = game.getCurrentLetters();
        roundInfo[1] = game.getRoundNumber();

        for(DB_ClientInfo client: db.getClientsForServer()){
            roundInfo[2] = db.getScoreByID(client.uniqueID);

            ArrayList<DB_ClientInfo> toSend = new ArrayList<>();
            toSend.add(client);

            Information info = new Information("ROUND INFO", roundInfo);
            pushToClients(toSend, info);
            System.out.println(Arrays.toString(roundInfo));

            toSend.clear();
        }
        game.nextRound();
    }

    /**
     *
     * @param sourcePacket
     */

    public void acknowledgeClient(DatagramPacket sourcePacket) {
        //create a new DB_ClientInfo object
        Information info = (Information) Information.byteToObj(sourcePacket.getData());
        Object data = info.getData();

        boolean sendError = false;
        if(!(data instanceof String)) { // the data sent from the client should be only their name
            //send error packet to Client
            sendErrorPacket(sourcePacket);
            return;
        }

        // If the data is valid, create a new Client using the source packet information and information in the packet
        String name = (String) data;
        DB_ClientInfo newClient = new DB_ClientInfo(db.getIdGenerator(), name, sourcePacket.getAddress(), sourcePacket.getPort());
        db.addPlayer(newClient);

        db.updateIdGenerator();// increment the id generator for the next client

        //send to new Client
        ArrayList<DB_ClientInfo> toSend = new ArrayList<>();
        toSend.add(newClient);
        Information ackInfo = new Information("CLIENT ACK");
        ackInfo.setID(newClient.uniqueID);
        pushToClients(toSend, ackInfo);
    }

    /**
     *
     * @param sourcePacket
     */

    public void acknowledgeReadyState(DatagramPacket sourcePacket){
        //save the ready state into the "DB"
        //then send the ready state list of all clients to the other clients

        Information infoIn = (Information)  Information.byteToObj(sourcePacket.getData());
        Object dataIn = infoIn.getData();

        if(!(dataIn instanceof Boolean)){
            sendErrorPacket(sourcePacket);
            return;
        }

//        boolean newReadyState = (Boolean) dataIn;
        db.changeReadyStatusByID(infoIn.getID());
        System.out.println(db.getClientInfoByIDforClient(infoIn.getID())[1] + " has readied up");

//        for(DB_ClientInfo client: ClientList){
//            if(client.uniqueID == infoIn.getID()){
//                System.out.println(client.playerName + " has readied up");
//                client.ready = newReadyState;
//            }
//        }


//        Information infoIn = (Information) Information.byteToObj(sourcePacket.getData());
//        Object dataIn = infoIn.getData();
//
//        //assuming that the data of the Information object is a Boolean with the new ready state
//        if(!(dataIn instanceof Boolean)){
//            sendErrorPacket(sourcePacket);
//            return;
//        }
//        Object[] readyStateChanged = new Object[3];
//        ArrayList<DB_ClientInfo> toSend = new ArrayList<>();
//        //save new ready state for the changed client into the "db"
//        Boolean newReadyState = (Boolean) dataIn;
//        for (DB_ClientInfo client : ClientList) {
//            if (client.uniqueID == infoIn.getID()) {
//                client.ready = newReadyState;
//                readyStateChanged[0] = client.uniqueID;
//                readyStateChanged[1] = client.playerName;
//                readyStateChanged[2] = client.ready;
//            }
//            else{
//                toSend.add(client);
//            }
//        }

        //TODO: (THREADING) stopping sending it to other clients, just record it in the server
        //send the new client's id, name, and ready state to the other clients other than the source of the ready state change
//        Information infoOut = new Information("READY STATE CHANGED", readyStateChanged, infoIn.getID());
//        pushToClients(toSend, infoOut);
    }

    /**
     *
     * @param packet
     */
    private void checkClientAnswer(DatagramPacket packet) {
        Information info = (Information) Information.byteToObj(packet.getData());
        Object dataIn = info.getData();

        if(!(dataIn instanceof String)){
            sendErrorPacket(packet);
            return;
        }
        String answer = (String) dataIn;
        int clientID = info.getID();

        DB_ClientInfo sourceClient = null;
        ArrayList<DB_ClientInfo> toSend = new ArrayList<>();
        boolean wordExists = false;


        //get the client from the db
        //add client to toSend
        //check if the word exists
        sourceClient = db.getClientInfoByIDforServer(clientID);
        toSend.add(sourceClient);
        wordExists = sourceClient.previousAnswers.contains(answer.trim().toLowerCase());

//        for(DB_ClientInfo client: ClientList){
//            if(client.uniqueID == clientID){
//                sourceClient = client;
//                toSend.add(sourceClient);
//                wordExists = client.previousAnswers.contains(answer.trim().toLowerCase());
//            }
//        }

        int pointsAwarded = 0;
        if(!wordExists && sourceClient != null){
            pointsAwarded = game.scoreWord(answer);
            db.updateScore(sourceClient.uniqueID, pointsAwarded);
            db.addAnswer(sourceClient.uniqueID, answer);
//            sourceClient.totalScore += pointsAwarded;
//            sourceClient.previousAnswers.add(answer);
        }

        Information infoOut = new Information("POINTS AWARDED", pointsAwarded);
        pushToClients(toSend, infoOut);
    }

    /**
     *
     * @param packet
     */
    private void endClientRound(DatagramPacket packet) {
        Information infoIn = (Information) Information.byteToObj(packet.getData());
        int sourceId = infoIn.getID();

        //find the source Client and set its ready to false, so when it enters the waiting room it needs to ready up again
        db.changeReadyStatusByID(sourceId);

//        for(DB_ClientInfo client: ClientList){
//            if(client.uniqueID == sourceId){
//                client.ready = false;
//            }
//        }
    }

    /**
     *
     * @param typeOfLeaderBoard
     */
    private void sendLeaderBoard(String typeOfLeaderBoard){
        Object[] leaderBoardData = new Object[2];
        ArrayList<String> playerNames = new ArrayList<>();
        ArrayList<Integer> totalScores = new ArrayList<>();

        //sort the ArrayList of DB_CustomerInfo objects by overriding the compareTo method
        ArrayList<DB_ClientInfo> clientList = db.getClientsForServer();
        clientList.sort(new Comparator<DB_ClientInfo>() {
            @Override
            public int compare(DB_ClientInfo client1, DB_ClientInfo client2) {
                Integer score1 = client1.totalScore;
                Integer score2 = client2.totalScore;
                return score2.compareTo(score1);
            }
        });

        for(DB_ClientInfo client: clientList){
            playerNames.add(client.playerName);
            totalScores.add(client.totalScore);
        }
        leaderBoardData[0] = playerNames;
        leaderBoardData[1] = totalScores;

        //check if the String typeOfLeaderBoard is correct
        if(!typeOfLeaderBoard.toUpperCase().equals("ROUND") && !typeOfLeaderBoard.toUpperCase().equals("FINAL")){
            System.out.println("The sendLeaderBoard method must be given FINAL or ROUND");
            return;
        }

        Information infoOut = new Information(typeOfLeaderBoard.toUpperCase() + " LEADERBOARD", leaderBoardData);
        pushToClients(db.getClientsForServer(), infoOut);

        if(typeOfLeaderBoard.equalsIgnoreCase("final")){
            //wipe contents of db
            db.clearGameTable();
            db.clearPlayersTable();
            System.exit(0);
        }
    }
//    private void sendRoundLeaderBoardToClients() {
//        Object[] leaderBoardData = new Object[2];
//        ArrayList<String> playerNames = new ArrayList<>();
//        ArrayList<Integer> totalScores = new ArrayList<>();
//
//        //sort the ArrayList of DB_CustomerInfo objects by overriding the compareTo method
//        ClientList.sort(new Comparator<DB_ClientInfo>() {
//            @Override
//            public int compare(DB_ClientInfo client1, DB_ClientInfo client2) {
//                Integer score1 = client1.totalScore;
//                Integer score2 = client2.totalScore;
//                return score2.compareTo(score1);
//            }
//        });
//
//        for(DB_ClientInfo client: ClientList){
//            playerNames.add(client.playerName);
//            totalScores.add(client.totalScore);
//        }
//        leaderBoardData[0] = playerNames;
//        leaderBoardData[1] = totalScores;
//
//        Information infoOut = new Information("ROUND LEADERBOARD", leaderBoardData);
//        pushToClients(ClientList, infoOut);
//    }

    /**
     *
     * @param clients
     * @param info
     */

    public void pushToClients(ArrayList<DB_ClientInfo> clients, Information info){
        for (DB_ClientInfo client : clients) {
            byte[] infoBytes = Information.objToByte(info);
            DatagramPacket packet = new DatagramPacket(infoBytes, infoBytes.length, client.IPAddress, client.PortNumber);

            try {
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error sending packet to " + client.playerName + " IP: " + packet.getAddress() + " Port: " + packet.getPort());
            }
        }
    }

    /**
     *
     * @param sourcePacket
     */

    public void sendErrorPacket(DatagramPacket sourcePacket){
        Information errorInfo = new Information("DATA ERROR");
        byte[] errorInfoByte = Information.objToByte(errorInfo);

        DatagramPacket errorPacket = new DatagramPacket(errorInfoByte, errorInfoByte.length, sourcePacket.getAddress(), sourcePacket.getPort());
        try {
            socket.send(errorPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error sending packet to " + " IP: " + errorPacket.getAddress() + " Port: " + errorPacket.getPort());
        }
    }

//    private boolean checkIfAllClientsAreReady() {
//        for(DB_ClientInfo client: ClientList){
//            if (!client.ready) {
//                return false;
//            }
//        }
//        return true;
//    }

//    private boolean checkIfAllClientsAreNotReady() {
//        for(DB_ClientInfo client: ClientList){
//            if(client.ready){
//                return false;
//            }
//        }
//        return true;
//    }

    /**
     *
     */
    public void waitForPackets(){
        System.out.println("hello");
        while(true){
            try{
                byte[] data = new byte[6400]; //could be larger/smaller if needed
                DatagramPacket toReceivePacket = new DatagramPacket(data, data.length);

                socket.receive(toReceivePacket); //wait to receive packet from client, contains a wait()?
                Information info = (Information) Information.byteToObj(toReceivePacket.getData());
                System.out.println("Packet received. API: " + info.getAPImessage());

                readPacket(toReceivePacket); //read request from CLIENT using LL API, then modify/display new LL, and add to log

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server(5);
        server.waitForPackets();

//        DatabaseInteractor db = new DatabaseInteractor();
//        db.clearPlayersTable();
//        db.clearGameTable();
    }
}
