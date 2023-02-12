package gamedata;

public interface NamedEntity {
    public String getName();
    public default String getDisplayName(){
        return getName(); 
    }

    public String getEntityDesignation();
}
