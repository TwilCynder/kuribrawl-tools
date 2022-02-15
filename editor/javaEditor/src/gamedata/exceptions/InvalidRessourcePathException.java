package gamedata.exceptions;

/**
 * Exception indicating that a path used as a ressource path is not a valid ressource path (doesn't exist, not a directory, etc)
 */
public class InvalidRessourcePathException extends Exception {
    public InvalidRessourcePathException(String message){
        super(message);
    }

    public InvalidRessourcePathException(String message, Throwable cause){
        super(message, cause);
    }
}
