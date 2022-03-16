package gamedata.exceptions;

import gamedata.GameData;

public class GameDataException extends Exception{
    Object[] objects;

    public GameDataException(String message, Object... objs){
        super(message);
        objects = objs;
    }

    public GameDataException(Throwable cause, Object... objs){
        super(cause);
        objects = objs;
    } 

    public GameDataException(String message, Throwable cause, Object... objs){
        super(message, cause);
        objects = objs;
    } 

    public Object[] getObjects(){
        return objects;
    }
}
