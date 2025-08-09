package gamedata;

/**
 * Refers to an EntityAnimation in the same Champions : during loading refer using it's name, then resolve into the actual object
 */
public class EntityAnimationReference extends GameDataReference<EntityAnimation> {
    private String name;

    public EntityAnimationReference(String name){
        this.name = name;
    }

    public EntityAnimationReference(EntityAnimation anim){
        super(anim);
    }

    public void resolve(Champion champion){
        EntityAnimation anim = champion.getAnimation(name);
        resolve(anim);
    }

    @Override
    public String toString() {
        return isResolved() ? 
            val.toString() : 
            "[UNRESOLVED] Animation name : " + name;
    }
}
