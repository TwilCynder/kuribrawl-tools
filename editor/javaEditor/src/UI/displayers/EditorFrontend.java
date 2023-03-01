package UI.displayers;

import KBUtil.ui.display.InteractableDisplayable;

public interface EditorFrontend extends InteractableDisplayable {
    public AbstractAnimationEditorBackend getBackend();
}