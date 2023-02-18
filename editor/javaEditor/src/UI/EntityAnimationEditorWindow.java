package UI;

import gamedata.CollisionBox;
import gamedata.EntityAnimation;
import gamedata.EntityFrame;
import gamedata.Frame;

public interface EntityAnimationEditorWindow extends AnimationEditorWindow {
    public void updateAnimControls(EntityAnimation anim, boolean ignoreModifications);
    public void updateFrameControls(Frame frame, EntityFrame entity_frame, boolean ignoreModifications);
    public void updateElementControls(CollisionBox cbox, boolean ignoreModifications);
}
