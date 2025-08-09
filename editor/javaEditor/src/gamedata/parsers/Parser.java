package gamedata.parsers;

import gamedata.exceptions.RessourceException;

public class Parser {
    public static int parseInt(String str, String numberDescription, String filename, int line) throws RessourceException{
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e){
            throw new RessourceException(numberDescription + " is not a valid integer", filename, line, e);
        }
    }

    public static int parseInt(String str, String numberDescription) throws RessourceException{
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e){
            throw new RessourceException(numberDescription + " is not a valid integer", e);
        }
    }

    public static double parseDouble(String str, String numberDescription, String filename, int line) throws RessourceException{
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e){
            throw new RessourceException(numberDescription + " is not a valid number", filename, line, e);
        }
    }

    public static double parseDouble(String str, String numberDescription) throws RessourceException{
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e){
            throw new RessourceException(numberDescription + " is not a valid number", e);
        }
    }

    public static void expectFields(String[] fields, int expected, String message) throws RessourceException{
        if (fields.length < expected) throw new RessourceException(message);
    }
}
