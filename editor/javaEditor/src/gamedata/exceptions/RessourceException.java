package gamedata.exceptions;

/**
 * Exception indicating that the content of a ressource path is invalid : missing file, error in a descriptor, etc
 */
public class RessourceException extends Exception{
    public RessourceException(String message){
        super(message);
    }

    public RessourceException(String message, String file, int line){
        super("In file " + file + " at line " + line + " : " + message);
    }

    public RessourceException(String message, Throwable cause){
        super(message, cause);
    }

    public RessourceException(String message, String file, int line, Throwable cause){
        super("In file " + file + " at line " + line + " : " + message, cause);
    }
}
