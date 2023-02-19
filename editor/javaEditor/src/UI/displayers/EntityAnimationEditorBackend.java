package UI.displayers;

import UI.EntityAnimationEditorWindow;

public class EntityAnimationEditorBackend extends AbstractEntityAnimationEditorBackend {
    EntityAnimationEditorWindow editorWindow;

    public EntityAnimationEditorBackend(EntityAnimationEditor editor, EntityAnimationEditorWindow editorWindow) {
        super(editor);
        this.editorWindow = editorWindow;
        onCreated(editor);
    }

    @Override
    protected EntityAnimationEditorWindow getEditorWindow() {
        return editorWindow;
    }

}
