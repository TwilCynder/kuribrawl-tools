package gamedata;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import gamedata.exceptions.RessourceException;

public class GameplayAnimationBehavior {
    public enum LandingBehaviorType {
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
        public LandingBehaviorType getType(){
            return KBUtil.EnumUtil.valueOf(LandingBehaviorType.classes, this.getClass());
        }

        /**
         * Creates a LandingBehavior from an array of descriptor fields, which must start at the behavor type (3rd field in the line) 
         * @param fields
         * @return
         */
        public static LandingBehavior parseDescriptorFields(String[] fields) throws RessourceException{
            if (fields.length < 1){
                throw new RessourceException("Landing behavior window line does not contain enough information (must be at least a type code)");
            }

            LandingBehaviorType type;
            try {
                type = KBUtil.EnumUtil.valueOfSafe(LandingBehaviorType.codes, fields[2]);
            } catch (NoSuchElementException err){
                throw new RessourceException("Unknown landing behavior type", err);
            }

            switch (type){
                case NORMAL: {
                    System.out.println("--------------- Normal window");
                }
                break;
                case ANIMATION: {
                    System.out.println("--------------- Animation window window");
                }
                break;
                case NOTHING: {
                    System.out.println("--------------- Normal window");
                }
                break;
            }

            return new LandingBehavior();
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
