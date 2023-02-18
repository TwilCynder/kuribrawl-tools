package UI.displayers;

import UI.AnimationEditorWindow;

public class AnimationEditorBackend extends AbstractAnimationEditorBackend {
    AnimationEditorWindow editorWindow;

    public AnimationEditorBackend(AnimationEditor editor, AnimationEditorWindow editorWindow) {
        super(editor);
        this.editorWindow = editorWindow;
    }

    @Override
    protected AnimationEditorWindow getEditorWindow() {
        return editorWindow;
    }
}
