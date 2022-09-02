package KBUtil;

/**
 * Represents the result of an operation, with a boolean indicating success or failure, 
 * and aditional data in the form of an object, typically used in cas of success.
 */
public class Result <T>{
    public boolean success;
    public T data;

    public Result(boolean s, T d){
        this.success = s;
        this.data = d;
    }

    public Result(boolean s){
        this.success = s;
        this.data = null;
    }

    @Override
    public String toString() {
        return "Result [" + (success ? "Success" : "Failure") + " ; data=" + data;
    }

}
