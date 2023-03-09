package UI.displayers;

import UI.EntityAnimationEditorWindow;

public class EntityAnimationEditorBackend extends AbstractEntityAnimationEditorBackend {
    EntityAnimationEditorWindow editorWindow;
    EntityAnimationDisplayer editor;

    public EntityAnimationEditorBackend(EntityAnimationEditor editor_, EntityAnimationEditorWindow editorWindow) {
        super();
        this.editorWindow = editorWindow;
        this.editor = editor_;
        onCreated();
    }

    @Override
    protected EntityAnimationEditorWindow getEditorWindow() {
        return editorWindow;
    }

    @Override 
    protected EntityAnimationDisplayer getEditor(){
        return editor;
    }

}
