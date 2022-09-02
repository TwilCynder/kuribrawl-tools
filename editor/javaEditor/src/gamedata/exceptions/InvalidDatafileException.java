package gamedata.exceptions;

/**
 * Used when reading a Datafile directly (currently unused)
 */
public class InvalidDatafileException extends Exception {
    public InvalidDatafileException(String origin, String message){
        super(origin + " : " + message);
    }

    public InvalidDatafileException(String origin, long position, String message){
        this(origin + "(at position " + position + ")", message);
    }
}
