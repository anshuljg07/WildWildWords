import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Class that carries the client information for the database to access and store
 */
public class DB_ClientInfo {
    public int uniqueID;
    public String playerName;
    public InetAddress IPAddress;
    public int PortNumber;
    public int totalScore;
    public ArrayList<String> previousAnswers; //implement in SQL as a table with rows for each of their previous correct answers
    public boolean ready;
    public int rank;

    /**
     * Constructor that creates a fresh client info if it is a new user
     * @param uniqueID fresh id for new user
     * @param playerName name for a new user
     * @param IPAddress IP address for a new user
     * @param PortNumber Port Number for a new user
     */
    public DB_ClientInfo(int uniqueID, String playerName, InetAddress IPAddress, int PortNumber){
        this.uniqueID = uniqueID;
        this.playerName = playerName;
        this.IPAddress = IPAddress;
        this.PortNumber = PortNumber;
        this.totalScore = 0;
        previousAnswers = new ArrayList<>();
        ready = false;
    }

    /**
     * Constructor that holds client info for a user that is stored in the database
     * @param uniqueID ID for a user in the DB
     * @param playerName Name for a user in the DB
     * @param IPAddress IP Address for a user in the DB
     * @param PortNumber Port Number for a user in the DB
     * @param totalScore total score for a user that is in the DB
     * @param previousAnswers arraylist of answers for a user stored in the DB
     * @param ready ready state for a user who is stored in the DB
     */

    public DB_ClientInfo(int uniqueID, String playerName, InetAddress IPAddress, int PortNumber, int totalScore, ArrayList<String> previousAnswers, boolean ready){
        this.uniqueID = uniqueID;
        this.playerName = playerName;
        this.IPAddress = IPAddress;
        this.PortNumber = PortNumber;
        this.totalScore = totalScore;
        this.previousAnswers = previousAnswers;
        this.ready = ready;
    }
}
