package UI;

import gamedata.EntityAnimation;

class ConcreteEntityAnimationMenuItem extends EntityAnimationMenuItem {
    private EntityAnimation anim;

    public ConcreteEntityAnimationMenuItem(EntityAnimation anim_, Window win)
            throws NullPointerException {
        super(anim_, win);
        anim = anim_;
    }


    @Override
    public EntityAnimation getAnimation(){
        return anim;
    }
}
