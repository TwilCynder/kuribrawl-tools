package UI;

import gamedata.Animation;
import gamedata.Frame;

public interface AnimationEditorWindow extends EditorWindow {
    public void updateAnimControls(Animation anim, boolean ignoreModifications);
    public void updateFrameControls(Frame frame, boolean ignoreModifications);
}
