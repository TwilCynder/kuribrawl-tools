DeclareModule GMB_AngleViewer
  Declare AngleViewerGadget(id.q, x.l, y.l, size.l, angle.d, *callback = 0)
  Declare SetCallback(id.q, *callback)
  Declare SetAngleP(*angleViewer, angle.d)
  Declare SetAngle(id.q, angle.d)
EndDeclareModule

Module GMB_AngleViewer
  Prototype AngleViewerCallback(angle.d)
  
  Structure AngleViewer
    isGMB.b
    callback.AngleViewerCallback
    angle.d
    id.q
    size.l
  EndStructure  
  
  NewList AngleViewers.AngleViewer()
      
  Procedure Render(*angleViewer.AngleViewer)
    halfSize.l =  *angleViewer\size / 2
    circleRadius = *angleViewer\size * 0.45
    StartDrawing(CanvasOutput(*angleViewer\id))
    
    Box(0, 0, *angleViewer\size, *angleViewer\size, $777777)
    DrawingMode(#PB_2DDrawing_Outlined)
    Line(0, halfSize, *angleViewer\size, 1, #Blue)
    Line(halfSize, 0, 1, *angleViewer\size, #Blue)
    Circle(halfSize, halfSize, circleRadius, #Blue)
    LineXY(halfSize, halfSize, halfSize + Cos(*angleViewer\angle) * circleRadius, halfSize - Sin(*angleViewer\angle) * circleRadius, #Red)
    StopDrawing()
    
  EndProcedure
  
  Procedure ClickCallback()
    id.q = EventGadget()
    *angleViewer.AngleViewer = GetGadgetData(id)
    
    x.l = GetGadgetAttribute(id, #PB_Canvas_MouseX) - (*angleViewer\size / 2)
    y.l = GetGadgetAttribute(id, #PB_Canvas_MouseY) - (*angleViewer\size / 2)
    
    *angleViewer\angle = -ATan2(x, y)
    If *angleViewer\angle < 0
      *angleViewer\angle = 2 * #PI + *angleViewer\angle
    EndIf 
    Render(*angleViewer)
    
    If *angleViewer\callback
      *angleViewer\callback(*angleViewer\angle)
    EndIf
    
  EndProcedure
  
  Procedure AngleViewerGadget(id.q, x.l, y.l, size.l, angle.d, *callback = 0)
    Shared AngleViewers()
    Define canvasID.q
    AddElement(AngleViewers())
    
    canvasID = CanvasGadget(id, x, y, size, size)
    If id = #PB_Any
      id = canvasID
    EndIf     
    
    AngleViewers()\id = id
    AngleViewers()\size = size
    AngleViewers()\isGMB = 54
    AngleViewers()\angle = angle
    angleViewers()\callback = *callback
    
    SetGadgetData(id, @AngleViewers())
    BindGadgetEvent(id, @ClickCallback(), #PB_EventType_LeftClick)
    
    Render(@AngleViewers())
    
    ProcedureReturn @AngleViewers()
  EndProcedure
  
  Procedure SetCallback(id.q, *callback)
    *angleViewer.AngleViewer = GetGadgetData(id)
    *angleViewer\callback = *callback
  EndProcedure
  
  Procedure SetAngleP(*angleViewer.AngleViewer, angle.d)
    *angleViewer\angle = angle
    Debug *angleViewer\angle
    Render(*angleViewer)
  EndProcedure
  
  Procedure SetAngle(id.q, angle.d)
    SetAngleP(GetGadgetData(id), angle)
  EndProcedure
  
EndModule

; IDE Options = PureBasic 5.72 (Windows - x64)
; CursorPosition = 85
; FirstLine = 42
; Folding = --
; EnableXP