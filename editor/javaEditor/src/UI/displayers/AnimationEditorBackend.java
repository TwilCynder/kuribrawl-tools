package UI.displayers;

import UI.AnimationEditorWindow;

public class AnimationEditorBackend extends AbstractAnimationEditorBackend {
    AnimationEditorWindow editorWindow;
    AnimationDisplayer editor;

    public AnimationEditorBackend(AnimationEditor editor_, AnimationEditorWindow editorWindow) {
        super();
        this.editorWindow = editorWindow;
        this.editor = editor_;
        onCreated();
    }

    @Override
    protected AnimationEditorWindow getEditorWindow() {
        return editorWindow;
    }

    @Override 
    protected AnimationDisplayer getEditor(){
        return editor;
    }
}
