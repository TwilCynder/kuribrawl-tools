package gamedata;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import KBUtil.ConstCollection;

public class GameplayAnimationBehavior {
    public static enum LandingBehaviorType {
        NORMAL(NormalLandingBehavior.class), 
        ANIMATION(AnimationLandingBehavior.class),
        NOTHING(LandingBehavior.class);
        
		private Class<? extends LandingBehavior> behaviorClass; 

        LandingBehaviorType(Class<? extends LandingBehavior> behaviorClass){
            this.behaviorClass = behaviorClass;
        }

        public static Map<Class<? extends LandingBehavior>, LandingBehaviorType> classes;

        static {
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

        public String behaviorString(){
            return "No action";
        }

        @Override
        public final String toString() {
            return "Landing Behavior : " + behaviorString();
        }
    }

    protected static abstract class DurableLandingBehavior extends LandingBehavior {
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
        public NormalLandingBehavior (int duration){
            super (duration);
        }

        @Override
        public String behaviorString(){
            return "Normal (" + duration + " frames)";
        }
    }

    public static class AnimationLandingBehavior extends DurableLandingBehavior {
        protected EntityAnimationReference anim;

        public AnimationLandingBehavior (int duration, String name){
            super (duration);
            this.anim = new EntityAnimationReference(name);
        }

        public AnimationLandingBehavior (int duration, EntityAnimation anim){
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

        @Override
        public String behaviorString(){
            return "Animation : " + anim + " (" + duration + " frames)";
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

        public void setFrame(int frame){
            this.frame = frame;
        }

        public void setBehavior(LandingBehavior b){
            this.behavior = b;
        }

        public void finalize(Champion champion){
            behavior.finalize(champion);
        }

        @Override
        public int compareTo(LandingBehaviorWindow arg0) {
            return frame - arg0.frame;
        }

        @Override
        public String toString() {
            return "Landing Behavior Window on frame " + frame + " ; Behavior : " + behavior.behaviorString();
        }
    }

    private List<LandingBehaviorWindow> landing_behavior_windows = new LinkedList<>();

    public LandingBehaviorWindow addLandingWindow(LandingBehaviorWindow window){
        landing_behavior_windows.add(window);
        Collections.sort(landing_behavior_windows);
        return window;
    }

    public ConstCollection<LandingBehaviorWindow> getLandingBehaviorsWindows(){
        return new ConstCollection<>(landing_behavior_windows);
    }
    
}
