/**
 * A class that holds the Client
 */
public class GetClient {
    /**
     * A self-referencing static instance
     */
    private final static GetClient instance = new GetClient();

    /**
     * Returns the self-referencing instance
     * @return the self-referencing instance
     */
    public static GetClient getInstance() {return instance;}

    /**
     * A client object
     */
    private final Client client = new Client();

    /**
     * Returns the client object
     * @return the client object
     */
    public Client getClient() {return client;}
}

