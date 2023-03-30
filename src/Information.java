import java.io.*;

/**
 * Class that contains the characteristics of information objects
 */
public class Information implements Serializable {
    /**
     * ID that is unique to every client
     */
    private int ID;
    /**
     * APImessage to send between client and server
     */
    private String APImessage;
    /**
     * Data object to be sent
     */
    Object data;

    /**
     * Constructor to send API Message when data is null
     * @param APImessage message to be sent between server and client
     */
    public Information(String APImessage){
        this.APImessage = APImessage;
        data = null;
    }

    /**
     * Constructor to send API Message and dataPoint object
     * @param APImessage Message to be sent between server and client
     * @param dataPoint Object that is to be serialized
     */
    public Information(String APImessage, Object dataPoint){
        this.APImessage = APImessage;
        data = dataPoint;
    }

    /**
     * Constructor to send player information
     * @param APImessage Message to be sent between server and client
     * @param dataPoint Object that is to be serialized
     * @param ID UniqueID that is assigned for each client instance
     */
    public Information(String APImessage, Object dataPoint, int ID){
        this.APImessage = APImessage;
        data = dataPoint;
        this.ID = ID;
    }

    /**
     * Getter for the APImessage
     * @return APImessage
     */
    public String getAPImessage() {
        return APImessage;
    }

    /**
     * setter for APImessage
     * @param APImessage APImessage
     */
    public void setAPImessage(String APImessage) {
        this.APImessage = APImessage;
    }

    /**
     * getter for Data object
     * @return data object
     */
    public Object getData() {
        return data;
    }

    /**
     * getter for ID
     * @return unique ID for player/client
     */
    public int getID() {
        return ID;
    }

    /**
     * setter for ID
     * @param ID new unique ID for player/client
     */
    public void setID(int ID) {
        this.ID = ID;
    }

    /**
     * setter for data object
     * @param dataPoint new data
     */
    public void setData(Object dataPoint) {
        data = objToByte(dataPoint);
    }

    /**
     * Tries to serialize object into byte array
     * @param obj object to be serialized
     * @return byte array of serialized object
     */
    public static byte[] objToByte(Object obj){
        try{
            return serialize(obj);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error serializing Object in Information constructor");
            System.exit(1);
        }
        return null;
    }

    /**
     * Serializes object into byte array
     * @param obj object to be serialized
     * @return byte array of serialized object
     * @throws IOException when object cannot be serialized
     */
    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream temp = new ByteArrayOutputStream(6400);
        ObjectOutputStream tempObj = new ObjectOutputStream(temp);
        tempObj.writeObject(obj);
        return temp.toByteArray();
    }

    /**
     * Deserializes byte array into original data object
     * @param bytes serialized byte array
     * @return  data object
     * @throws IOException caught when attempting to deserialize
     * @throws ClassNotFoundException classNotFound
     */
    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream temp = new ByteArrayInputStream(bytes);
        ObjectInputStream tempObj = new ObjectInputStream(temp);
        return tempObj.readObject();
    }

    /**
     * tries to deserialize byte array
     * @param data byte array to be deserialized
     * @return deserialized data object
     */
    public static Object byteToObj(byte[] data){
        Object out = null;
        try{
            out = deserialize(data);
        }
        catch (IOException | ClassNotFoundException ioException){
            ioException.printStackTrace();
            System.out.println("Error serializing Object in Information constructor");
            System.exit(1);
        }

        return out;
    }
}