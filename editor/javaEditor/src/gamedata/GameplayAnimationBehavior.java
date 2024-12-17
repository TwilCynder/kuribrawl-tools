package gamedata;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import gamedata.exceptions.RessourceException;
import gamedata.parsers.AnimationParser;

public class GameplayAnimationBehavior {
    public enum LandingBehaviorType {
        NORMAL, 
        ANIMATION,
        NOTHING;

        

        
    }

    public class LandingBehavior {
        public LandingBehaviorType getType(){
            return LandingBehaviorType.NOTHING;
        }


    }

    public abstract class DurableLandingBehavior extends LandingBehavior {
        private int duration;

        DurableLandingBehavior(int duration){
            this.duration = duration;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int d) {
            this.duration = d;
        }
    }

    public class NormalLandingBehavior extends DurableLandingBehavior {
        NormalLandingBehavior (int duration){
            super (duration);
        }

        @Override
        public LandingBehaviorType getType(){
            return LandingBehaviorType.NORMAL;
        }
    }

    public class AnimationLandingBehavior extends DurableLandingBehavior {
        String anim_name;

        AnimationLandingBehavior (int duration, String name){
            super (duration);
            this.anim_name = name;
        }

        public String getAnimName(){
            return anim_name;
        }

        @Override
        public LandingBehaviorType getType(){
            return LandingBehaviorType.ANIMATION;
        }
    }

    public class LandingBehaviorWindow implements Comparable<LandingBehaviorWindow>{
        private int frame = -1;
        private DurableLandingBehavior behavior;

        public LandingBehaviorWindow(int frame){
            this.frame = frame;
        }

        public int getFrame() {
            return frame;
        }

        public void setBehavior(int duration){
            if (behavior instanceof NormalLandingBehavior){
                behavior.duration = duration;
            } else {
                behavior = new NormalLandingBehavior(duration);
            }
        }

        public void setBehavior(String anim, int duration){
            if (behavior instanceof AnimationLandingBehavior){
                behavior.duration = duration;
                ((AnimationLandingBehavior)behavior).anim_name = anim;
            } else {
                behavior = new AnimationLandingBehavior(duration, anim);
            }
        }

        @Override
        public int compareTo(LandingBehaviorWindow arg0) {
            return frame - arg0.frame;
        }
    }

    private List<LandingBehaviorWindow> landing_behavior_windows;

    public LandingBehaviorWindow addLandingWindow(int frame){
        LandingBehaviorWindow window = new LandingBehaviorWindow(frame);
        landing_behavior_windows.add(window);
        Collections.sort(landing_behavior_windows);
        return window;
    }
}
