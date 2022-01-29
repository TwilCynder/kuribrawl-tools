package gamedata.exceptions;

public class InvalidDatafileException extends Exception {
    public InvalidDatafileException(String origin, String message){
        super(origin + " : " + message);
    }
}
