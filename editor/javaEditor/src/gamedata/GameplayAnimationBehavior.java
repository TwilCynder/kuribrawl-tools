package gamedata;

import java.util.List;

public class GameplayAnimationBehavior {
    public abstract class LandingBehavior {
        private int duration;

        LandingBehavior(int duration){
            this.duration = duration;
        }

        public int getDuration() {
            return duration;
        }
    }

    public class NormalLandingBehavior extends LandingBehavior {
        NormalLandingBehavior (int duration){
            super (duration);
        }
    }

    public class AnimationLandingBehavior extends LandingBehavior {
        String anim_name;

        AnimationLandingBehavior (int duration, String name){
            super (duration);
            this.anim_name = name;
        }

        public String getAnimName(){
            return anim_name;
        }
    }

    public class LandingBehaviorWindow {
        public int frame;
        public LandingBehavior behavior;
    }

    List<LandingBehavior> landing_behavior_windows;
}