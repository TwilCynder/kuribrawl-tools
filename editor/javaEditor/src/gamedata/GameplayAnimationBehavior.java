package gamedata;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import gamedata.exceptions.RessourceException;

public class GameplayAnimationBehavior {
    public static enum LandingBehaviorType {
        NORMAL(NormalLandingBehavior.class, "l"), 
        ANIMATION(AnimationLandingBehavior.class, "a"),
        NOTHING(LandingBehavior.class, "n");
        
        private String code;
		private Class<? extends LandingBehavior> behaviorClass; 

        LandingBehaviorType(Class<? extends LandingBehavior> behaviorClass, String code){
            this.behaviorClass = behaviorClass;
            this.code = code;
        }

        public static Map<String, LandingBehaviorType> codes;
        public static Map<Class<? extends LandingBehavior>, LandingBehaviorType> classes;

        static {
            codes = new TreeMap<>(){{
                for (LandingBehaviorType t : LandingBehaviorType.values()){
                    put(t.code, t);
                }
            }};
            classes = new TreeMap<>(new Comparator<Class<? extends LandingBehavior>>() {
                @Override
                public int compare(Class<? extends LandingBehavior> left, Class<? extends LandingBehavior> right){
                    return left.getName().compareTo(right.getName());
                }
            }){{
                for (LandingBehaviorType t : LandingBehaviorType.values()){
                    put(t.behaviorClass, t);
                }
            }};
        }
    }

    public static class LandingBehavior {
        public final LandingBehaviorType getType(){
            return KBUtil.EnumUtil.valueOf(LandingBehaviorType.classes, this.getClass());
        }

        public LandingBehavior(){}

        public void finalize(Champion champion){}
    }

    public static abstract class DurableLandingBehavior extends LandingBehavior {
        protected int duration;

        DurableLandingBehavior(int duration){
            this.duration = duration;
        }

        protected DurableLandingBehavior(){}

        public int getDuration() {
            return duration;
        }

        public void setDuration(int d) {
            this.duration = d;
        }
    }

    public static class NormalLandingBehavior extends DurableLandingBehavior {
        NormalLandingBehavior (int duration){
            super (duration);
        }

        protected NormalLandingBehavior(){}
    }

    public static class AnimationLandingBehavior extends DurableLandingBehavior {
        protected EntityAnimationReference anim;

        AnimationLandingBehavior (int duration, String name){
            super (duration);
            this.anim = new EntityAnimationReference(name);
        }

        AnimationLandingBehavior (int duration, EntityAnimation anim){
            super(duration);
            this.anim = new EntityAnimationReference(anim);
        }

        @Override
        public void finalize(Champion champion){
            anim.resolve(champion);
            //TODO : what happens if the name didn't point to an animation ?
        }

        public EntityAnimation getEntityAnimation(){
            if (!anim.isResolved()){
                throw new IllegalStateException("Attemps to access outer animation reference before it has been resolved");
            }
            return anim.get();
        }

    }

    public static class LandingBehaviorWindow implements Comparable<LandingBehaviorWindow>{
        private int frame = -1;
        private LandingBehavior behavior;

        public LandingBehaviorWindow(int frame){
            this.frame = frame;
        }

        public LandingBehaviorWindow(int frame, LandingBehavior b){
            this(frame);
            this.behavior = b;
        }

        public int getFrame() {
            return frame;
        }

        public LandingBehavior getLandingBehavior(){
            return behavior;
        }

        public void finalize(Champion champion){
            behavior.finalize(champion);
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
