package UI;

import gamedata.Animation;

class ConcreteAnimationMenuItem extends AnimationMenuItem {
    private Animation anim;

    public ConcreteAnimationMenuItem(Animation anim_, Window win) throws NullPointerException {
        super(anim_, win);
        anim = anim_;
    }


    @Override
    public Animation getAnimation(){
        return anim;
    }
}
