package UI;

import gamedata.CollisionBox;

public interface EntityAnimationEditorWindow extends AnimationEditorWindow {
    public void updateElementControls(CollisionBox cbox, boolean ignoreModifications);
}
