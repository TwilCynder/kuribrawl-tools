package gamedata.parsers;

import gamedata.exceptions.RessourceException;

public class Parser {
    public static int parseInt(String str, String msgIfFail, String filename, int line) throws RessourceException{
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e){
            throw new RessourceException(msgIfFail, filename, line, e);
        }
    }

    public static double parseDouble(String str, String msgIfFail, String filename, int line) throws RessourceException{
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e){
            throw new RessourceException(msgIfFail, filename, line, e);
        }
    }
}
