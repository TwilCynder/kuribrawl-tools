package gamedata.exceptions;

//we can tell the user what's going on
public class TransparentGameDataException extends Exception {
    public TransparentGameDataException(String message){
        super (message);
    }
}
