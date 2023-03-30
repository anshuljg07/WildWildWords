import java.net.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class carries the characteristics of the interaction between the DB and
 */

public class DatabaseInteractor {
    private static final String DATABASE_URL = "jdbc:postgresql://s-l112.engr.uiowa.edu:5432/swd_db11";

    private Connection connection;
    private ResultSet rs;
    private PreparedStatement ps;

    /**
     * DB Constructor to establish connection to DB
     */
    public DatabaseInteractor() {
        try {
            connection = DriverManager.getConnection(DATABASE_URL,
                    "swd_student11", "engr-2022-11");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * method to add a user to the table
     * @param newPlayer the user and info that is to be added the table
     */

    public void addPlayer(DB_ClientInfo newPlayer) {
        String sql = "INSERT INTO Players (id, IP, Port, Name, Score, AnswerHistory)\n" +
                "VALUES (?,?,?,?,?,?)";
        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, newPlayer.uniqueID);
            ps.setString(2, newPlayer.IPAddress.getCanonicalHostName());
            ps.setString(3, String.valueOf(newPlayer.PortNumber));
            ps.setString(4, newPlayer.playerName);
            ps.setInt(5, newPlayer.totalScore);
            if (newPlayer.previousAnswers == null) {
                ps.setString(6, "");
            }
            ps.setString(6, String.valueOf(newPlayer.previousAnswers));

            ps.executeUpdate();
            System.out.println("added player to table");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * method to remove a user from the table
     * @param removedPlayer user and info that is being removed from the table
     */
    public void removePlayer(DB_ClientInfo removedPlayer) {
        String sql = "DELETE FROM players WHERE id = ?";

        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, removedPlayer.uniqueID);

            ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * method that clears the table with all users and their info
     */
    public void clearPlayersTable() {
        String sql = "DELETE FROM players";

        try {
            ps = connection.prepareStatement(sql);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * method that updates the score for the player stored in the DB
     * @param id unique identifier
     * @param score score that is updated in the DB
     */

    public void updateScore(int id, int score) {
        String get = "SELECT score from Players WHERE id = ?";
        String update = "UPDATE players SET score = ? WHERE id = ?";

        try {
            ps = connection.prepareStatement(get);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            int currScore = 0;
            if (rs.next()) {
                currScore = rs.getInt(1);
            }

            ps = connection.prepareStatement(update);
            ps.setInt(1, score + currScore);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public int getScoreByID(int id) {
        String query = "SELECT score FROM players WHERE id = ?";

        try {
            ps = connection.prepareStatement(query);
            ps.setInt(1,id);
            rs = ps.executeQuery();
            int score = 0;
            if(rs.next()) {
                score = rs.getInt(1);
            }
            return score;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Method to update the ready status for given id
     * @param id unique identifier
     */
    public void changeReadyStatusByID(int id) {
        String get = "SELECT ReadyStatus FROM Players WHERE id = ?";
        String update = "UPDATE Players SET ReadyStatus = ? WHERE id = ?";

        try {
            ps = connection.prepareStatement(get);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            boolean currStatus = false;
            if (rs.next()) {
                currStatus = rs.getBoolean(1);
                currStatus = !currStatus;
            }

            ps = connection.prepareStatement(update);
            ps.setBoolean(1, currStatus);
            ps.setInt(2, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void changeGameStart() {
        String get = "SELECT gamestarted FROM game";
        String update = "UPDATE game SET gamestarted = ?";

        try {
            ps = connection.prepareStatement(get);
            rs = ps.executeQuery();
            boolean currStatus = false;
            if (rs.next()) {
                currStatus = rs.getBoolean(1);
                currStatus = !currStatus;
            }

            ps = connection.prepareStatement(update);
            ps.setBoolean(1, currStatus);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void changegamedone() {
        String get = "SELECT gamedone FROM game";
        String update = "UPDATE game SET gamedone = ?";

        try {
            ps = connection.prepareStatement(get);
            rs = ps.executeQuery();
            boolean currStatus = false;
            if (rs.next()) {
                currStatus = rs.getBoolean(1);
                currStatus = !currStatus;
            }

            ps = connection.prepareStatement(update);
            ps.setBoolean(1, currStatus);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    /**
     * Checks if all players in the database are ready to start
     * @return true of readystatus column is true
     */

    public boolean allPlayersReady() {
        String query = "SELECT ReadyStatus FROM Players";

        try {
            ps = connection.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                if (!(rs.getBoolean(1))) {
                    return false;
                }
            }
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean allPlayersNotReady() {
        String query = "SELECT ReadyStatus FROM Players";

        try {
            ps = connection.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getBoolean(1)) {
                    return false;
                }
            }
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * retrieves the client data for a given unique identifier
     * @param id unique identifier
     * @return the client Info
     */

    //Returns null if id not in table
    public DB_ClientInfo getClientInfoByIDforServer(int id) {
        String query = "SELECT * FROM Players WHERE id = ?";
        try {
            ps = connection.prepareStatement(query);
            ps.setInt(1,id);
            rs = ps.executeQuery();

            DB_ClientInfo clientInfo = null;
            if (rs.next()) {
                ArrayList<String> answerHistory = new ArrayList<>(Arrays.asList(rs.getString("answerhistory").split(" ")));
                clientInfo = new DB_ClientInfo(rs.getInt("id"), rs.getString("name"), InetAddress.getByName(rs.getString("ip")), rs.getInt("port"), rs.getInt("score"), answerHistory, rs.getBoolean("readystatus"));
            }
            return clientInfo;
        } catch (SQLException | UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * retrieves the client info needed for clients
     * @param id unique identifer
     * @return the client info
     */
    public Object[] getClientInfoByIDforClient(int id) {
        String query = "SELECT * FROM Players WHERE id = ?";
        try {
            ps = connection.prepareStatement(query);
            ps.setInt(1,id);
            rs = ps.executeQuery();

            Object[] clientInfo = new Object[3];
            if (rs.next()) {
                clientInfo[0] = rs.getInt("id");
                clientInfo[1] = rs.getString("name");
                clientInfo[2] = rs.getBoolean("readystatus");
            }
            return clientInfo;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Method that retrieves all clients and their information in the database
     * @return list of all clients and their information in the database
     */

    public ArrayList<DB_ClientInfo> getClientsForServer() {
        String query = "SELECT * FROM Players";
        try {
            ps = connection.prepareStatement(query);
            rs = ps.executeQuery();

            ArrayList<DB_ClientInfo> clientsList = new ArrayList<>();
            DB_ClientInfo clientInfo;
            while (rs.next()) {
                ArrayList<String> answerHistory = new ArrayList<>(Arrays.asList(rs.getString("answerhistory").split(" ")));
                clientInfo = new DB_ClientInfo(rs.getInt("id"), rs.getString("name"), InetAddress.getByName(rs.getString("ip")), rs.getInt("port"), rs.getInt("score"), answerHistory, rs.getBoolean("readystatus"));
                clientsList.add(clientInfo);
            }

            return clientsList;
        } catch (SQLException | UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * method that stores the client info into an object array
     * @return object array of client info
     */

    public ArrayList<Object[]> getClientsForClients() {
        String query = "SELECT * FROM Players";
        try {
            ps = connection.prepareStatement(query);
            rs = ps.executeQuery();

            ArrayList<Object[]> clientsList = new ArrayList<>();
            Object[] clientInfo;
            while (rs.next()) {
                clientInfo = new Object[3];
                clientInfo[0] = rs.getInt("id");
                clientInfo[1] = rs.getString("name");
                clientInfo[2] = rs.getBoolean("readystatus");
                clientsList.add(clientInfo);
            }
            return clientsList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param id unique identifier
     * @param newAnswer String of user inputted "answers" that are to be updated into the DB
     */

    public void addAnswer(int id, String newAnswer) {
        String query = "SELECT AnswerHistory FROM Players WHERE id = ?";
        String update = "UPDATE Players SET AnswerHistory = ? WHERE id = ?";
        try {
            ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            String currAnswers = "";
            if (rs.next()) {
                currAnswers = rs.getString(1);
                if (currAnswers.equals("[]")) {
                    currAnswers = "";
                }
            }

            ps = connection.prepareStatement(update);
            ps.setString(1, currAnswers + " " + newAnswer);
            ps.setInt(2, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method that creates the game table
     * @param game game constructor that contains the objects that will be stored in DB
     */
    public void createGame(Game game) {
        String update = "INSERT INTO game (roundnumber, scrambles, numberofrounds, gamestarted, gamedone, scrambleindex)\n" +
                        "VALUES (?,?,?,?,?,?);";
        try {
            ArrayList<String> scrambleList = game.getScrambles();
            StringBuilder sb = new StringBuilder();
            for (int i=0; i<scrambleList.size()-1; i++) {
                sb.append(scrambleList.get(i)+" ");
            }
            sb.append(scrambleList.get(scrambleList.size()-1));

            ps = connection.prepareStatement(update);
            ps.setInt(1, game.getRoundNumber());
            ps.setString(2, sb.toString());
            ps.setInt(3, game.getNumberOfRounds());
            ps.setBoolean(4,game.getGameStarted());
            ps.setBoolean(5, game.getGameDone());
            ps.setInt(6,game.getScramblesIndex());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method that retrieves the objects to be stored in the DB
     * @return an updated row in the Game table or null if no game in DB
     */
    //returns null if no game in database
    public Game getGameData() {
        String query = "SELECT * FROM game";
        Game game = null;
        try {
            ps = connection.prepareStatement(query);
            rs = ps.executeQuery();

            if (rs.next()) {
                ArrayList<String> scrambleList = new ArrayList<>(Arrays.asList(rs.getString("scrambles").split(" ")));
                game = new Game(rs.getInt("roundnumber"), scrambleList, rs.getInt("numberofrounds"), rs.getBoolean("gamestarted"), rs.getBoolean("gamedone"), rs.getInt("scrambleindex"));
            }
            return game;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method that retrieves the unique identifier and stores in the game table
     * @return idGen
     */
    public int getIdGenerator() {
        String query = "SELECT idgenerator FROM game";
        try {
            ps = connection.prepareStatement(query);
            rs = ps.executeQuery();
            int idGen = 1;
            if (rs.next()) {
                idGen = rs.getInt("idgenerator");
            }
            return idGen;

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Method that updates the id generator in the game table
     */
    public void updateIdGenerator() {
        String update = "UPDATE game SET idgenerator = ?";
        try {
            int currIdGen = getIdGenerator();
            ps = connection.prepareStatement(update);
            ps.setInt(1,currIdGen+1);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateRoundNumber() {
        String query = "SELECT roundnumber FROM game";
        String update = "UPDATE game SET roundnumber = ?";
        try {

            ps = connection.prepareStatement(query);
            rs = ps.executeQuery();
            int currRoundNum = 1;
            if(rs.next()) {
                currRoundNum = rs.getInt(1);
            }
            ps = connection.prepareStatement(update);
            ps.setInt(1,currRoundNum+1);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void updateScrambleIndex() {
        String query = "SELECT scrambleindex FROM game";
        String update = "UPDATE game SET scrambleindex = ?";
        try {

            ps = connection.prepareStatement(query);
            rs = ps.executeQuery();
            int currIndex = 0;
            if(rs.next()) {
                currIndex = rs.getInt(1);
            }
            ps = connection.prepareStatement(update);
            ps.setInt(1,currIndex+1);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Method that clears the game table
     */

    public void clearGameTable() {
        String sql = "DELETE FROM game";

        try {
            ps = connection.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter for Connection
     * @return connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Setter for Connection obj
     * @param connection sets the connection obj
     */

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * Getter for result set
     * @return rs
     */
    public ResultSet getRs() {
        return rs;
    }

    /**
     * Setter for the rs Obj
     * @param rs the result set obj
     */

    public void setRs(ResultSet rs) {
        this.rs = rs;
    }

    /**
     * prepared statement getter
     * @return ps
     */

    public PreparedStatement getPs() {
        return ps;
    }

    /**
     * setter for the prepared statement obj
     * @param ps the prepared statement obj
     */
    public void setPs(PreparedStatement ps) {
        this.ps = ps;
    }

    //Main method
    public static void main(String[] args) throws UnknownHostException {
        DatabaseInteractor db = new DatabaseInteractor();
       // db.addPlayer(new DB_ClientInfo(3,"ralaya", InetAddress.getByName("128.255.17.150"), 56723));
        db.clearGameTable();
        db.clearPlayersTable();


    }




}
