Enumeration
    #HITBOXTYPE_DAMAGE
    #HITBOXTYPE_GRAB
    #HITBOXTYPE_WIND
    #HITBOXTYPE_SPECIAL
EndEnumeration

Enumeration
    #HURTBOXTYPE_NORMAL
    #HURTBOXTYPE_PROTECTED
    #HURTBOXTYPE_INVINCIBLE
    #HURTBOXTYPE_INTANGIBLE
EndEnumeration    

Structure Vector
    x.l
    y.l
EndStructure

Structure VectorDouble
    x.d
    y.d
EndStructure

Structure Rectangle Extends Vector
    w.l
    h.l
EndStructure

Structure CBox Extends Rectangle
    type.l
EndStructure

Structure Hitbox Extends CBox
    damage.d
    bkb.d
    skb.d
    angle.f
    hit.l
    prio.l
EndStructure

Structure Hurtbox Extends CBox
    ;nada    
EndStructure

Structure Frame
    display.Rectangle
    origin.Vector
    duration.l
    List hurtboxes.Hurtbox()
    List hitboxes .Hitbox ()
    movement.VectorDouble
    movementType.Vector
EndStructure

Structure Animation
    sourceImage.l
    sourceImageSize.Vector
    Array frames.Frame(0)
    speed.d
EndStructure

Structure Champion
    Map animations.Animation()
EndStructure

Procedure createChampion()
    ProcedureReturn AllocateStructure(Champion)
EndProcedure

; IDE Options = PureBasic 5.72 (Windows - x64)
; CursorPosition = 68
; FirstLine = 19
; Folding = -
; EnableXP