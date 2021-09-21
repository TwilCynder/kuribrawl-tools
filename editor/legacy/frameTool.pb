#DEBUG = 1

;TODO
;play animation

CompilerIf #DEBUG
  #DEFAULT_DATAFILE_PATH = "..\src\res\data.twl"
CompilerElse
  #DEFAULT_DATAFILE_PATH = "..\data.twl"
CompilerEndIf

Enumeration
  #MENUITEM_SAVE
  #MENUITEM_SAVEALL
  #MENUITEM_REBUILD ;save all + rebuild + reload
  #MENUITEM_SETDESCRIPTOR
  #MENUITEM_LOADPROJECTDB
  #MENUITEM_RELOAD
  #MENUITEM_ADDHITBOX
  #MENUITEM_ADDHURTBOX
  #MENUITEM_DELETEBOX
  #MENUITEM_GENERATEHURTBOXES
  #MENUITEM_COPYCBOX
  #MENUITEM_PASTECBOX
  #MENUITEM_CUTCBOX
  #MENUITEM_CBOXINFO
  #MENUITEM_ANIMINFO
  #MENUITEM_FRAMEINFO
  #MENUITEM_RECTSELECT_CREATEHITBOX
  #MENUITEM_RECTSELECT_CREATEHURTBOX
  #MENUITEMS
EndEnumeration

#CANVAS_W = 200
#CANVAS_H = 200

XIncludeFile "../src/utilCore.pb"
XIncludeFile "../src/filelib.pb"
XIncludeFile "../src/gameDatalib.pbi"
XIncludeFile "../src/loadlib.pb"

XIncludeFile "AngleViewerGadget.pbi"

Procedure defaultJumpAnimCallback(*fighter, *data)
EndProcedure

Procedure defaultAttackAnimCallback(*fighter, *data)
EndProcedure

Structure AnimationDescriptorPair
  *animation
  descriptor.s
EndStructure

Structure RectSelection
  coord.RECT
  active.b
EndStructure

Global *selectedAnim.AnimationModel
Define *selectedCollisionBox.CollisionBox, viewPosition.Vector,
       selectedCollisionBoxType.b, firstAnimItem.l, projectDBLoaded.b, originalPath.s, dataFilePath.s,
       popUpMenuPosition.Point, rectSelection.RectSelection
NewMap characters.Champion()

Procedure drawFrame(*animation.AnimationModel, facing)
  Shared viewPosition, *selectedCollisionBox
  ClearScreen($777777)
  Define spriteSheet.l, x.l, y.l
  If facing = -1 And *animation\spriteSheetL
    spriteSheet = *animation\spriteSheetL
  Else
    spriteSheet = *animation\spriteSheet
  EndIf 
  With *animation\frames()
    ClipSprite(spriteSheet, \display\x, \display\y, \display\w, \display\h)
    x = viewPosition\x
    y = viewPosition\y

    DisplayTransparentSprite(spriteSheet, x, y)
    
    StartDrawing(ScreenOutput())
    Line(viewPosition\x + \origin\x, 0, 1, #CANVAS_H, #Blue)
    Line(0, viewPosition\y + \origin\y, #CANVAS_W, 1, #Blue)
    DrawingMode(#PB_2DDrawing_Outlined)
    ForEach \hitboxes()
      If *selectedCollisionBox = @\hitboxes()
        color.l = $ff00aa
      Else
        color.l = #Red
      EndIf 
      Box(\hitboxes()\x + x + \origin\x, y + \origin\y - \hitboxes()\y, \hitboxes()\x2, \hitboxes()\y2, color)
    Next
    ForEach \hurtboxes()
      If *selectedCollisionBox = @\hurtboxes()
        color.l = $aa00aa
      Else
        color.l = #Green
      EndIf 
      Box(\hurtboxes()\x + x + \origin\x, y + \origin\y - \hurtboxes()\y, \hurtboxes()\x2, \hurtboxes()\y2, color)
    Next 
    StopDrawing()
  EndWith
  FlipBuffers()
EndProcedure

Procedure onFrameChanged(*anim.AnimationModel)
  Shared viewPosition
  viewPosition\x = (#CANVAS_W / 2) - (*anim\frames()\display\w / 2) 
  viewPosition\y = (#CANVAS_H / 2) - (*anim\frames()\display\h / 2) 
  SetGadgetText(12, Str(*anim\frames()\origin\x))
  SetGadgetText(13, Str(*anim\frames()\origin\y))
  drawFrame(*anim, 1)
EndProcedure  

Procedure selectCollisionBox(*b.CollisionBox, type.b)
  Shared *selectedAnim, *selectedCollisionBox, selectedCollisionBoxType
  SetGadgetState(2, *b\x)
  SetGadgetState(4, *b\y)
  SetGadgetState(6, *b\x2)
  SetGadgetState(8, *b\y2)
  selectedCollisionBoxType = type
  *selectedCollisionBox = *b
  drawFrame(*selectedAnim, 1)
EndProcedure

Declare.s findAnimationDescriptor(*animation)
Procedure setAnimation(*animation.AnimationModel)
  Shared *selectedAnim
  *selectedAnim = *animation
  *selectedCollisionBox = 0
  ResetList(*selectedAnim\frames())
  NextElement(*selectedAnim\frames())
  onFrameChanged(*selectedAnim)
  
  SetGadgetText(11, findAnimationDescriptor(*animation))
EndProcedure

Procedure newHitbox(x.l, y.l, w.l, h.l)
  Shared *selectedAnim
  If Not *selectedAnim
    ProcedureReturn 0
  EndIf 
  *r = addHitbox(*selectedAnim\frames(), x, y, w, h)
  drawFrame(*selectedAnim, 1)
  ProcedureReturn *r
EndProcedure

Procedure newHurtbox(x.l, y.l, w.l, h.l)
  Shared *selectedAnim
  If Not *selectedAnim
    ProcedureReturn 0
  EndIf 
  *r = addHurtbox(*selectedAnim\frames(), x, y, w, h)
  drawFrame(*selectedAnim, 1)
  ProcedureReturn *r
EndProcedure

Procedure deleteBox()
  Shared *selectedAnim, *selectedCollisionBox, selectedCollisionBoxType
  If Not *selectedAnim Or Not *selectedCollisionBox
    ProcedureReturn 0
  EndIf 
  If selectedCollisionBoxType = #CBOX_TYPE_HIT
    ChangeCurrentElement(*selectedAnim\frames()\hitboxes(), *selectedCollisionBox)
    DeleteElement(*selectedAnim\frames()\hitboxes())
  Else  
    ChangeCurrentElement(*selectedAnim\frames()\hurtboxes(), *selectedCollisionBox)
    DeleteElement(*selectedAnim\frames()\hurtboxes())
  EndIf 
  *selectedCollisionBox = 0
  drawFrame(*selectedAnim, 1)
EndProcedure

Procedure selectPointedCBox()
  Shared *selectedAnim, viewPosition
 
  Define x.l, y.l, hx.l, hy.l
  x = WindowMouseX(0) - 5
  y = WindowMouseY(0) - 5
  If *selectedAnim
    ForEach *selectedAnim\frames()\hitboxes()
      With *selectedAnim\frames()\hitboxes()
        hx = \x + viewPosition\x + *selectedAnim\frames()\origin\x
        hy = viewPosition\y + *selectedAnim\frames()\origin\y - \y
        If x > hx And x < hx + \x2 And y > hy And y < hy + \y2
          selectCollisionBox(@*selectedAnim\frames()\hitboxes(), #CBOX_TYPE_HIT)
          ProcedureReturn 1
        EndIf 
      EndWith
    Next
    ForEach *selectedAnim\frames()\hurtboxes()
      With *selectedAnim\frames()\hurtboxes()
        hx = \x + viewPosition\x + *selectedAnim\frames()\origin\x
        hy = viewPosition\y + *selectedAnim\frames()\origin\y - \y
        If x > hx And x < hx + \x2 And y > hy And y < hy + \y2
          selectCollisionBox(@*selectedAnim\frames()\hurtboxes(), #CBOX_TYPE_HURT)
          ProcedureReturn 1
        EndIf 
      EndWith
    Next
  EndIf   
  ProcedureReturn 0
EndProcedure

Procedure.s makeHitboxText(*cbox.CollisionBox, type.b)
  Shared *selectedAnim
  Define text.s
  If Not *cbox
    ProcedureReturn ""
  EndIf 
  If type = #CBOX_TYPE_HIT
    text + "h"
  Else
    text + "c"
  EndIf 
  text + Str(ListIndex(*selectedAnim\frames()))
  text + " "
  text + Str(*cbox\x) + " "
  text + Str(*cbox\y) + " "
  text + Str(*cbox\x2) + " "
  text + Str(*cbox\y2) + " "
  If type = #CBOX_TYPE_HIT
    text + Str(getField(*cbox, Hitbox, damage, D)) + " "
  EndIf 
  ProcedureReturn text
EndProcedure

InitSprite()
InitMouse()

OpenWindow(0, 0, 0, 400, 250, "Kuribrawl Frame Tool", #PB_Window_ScreenCentered | #PB_Window_SystemMenu)
OpenWindowedScreen(WindowID(0), 5, 5, #CANVAS_W, #CANVAS_H)

IncludeFile "../src/gameData.pb"
Dim *itemAnims.AnimationModel(0)

NewList animationDescriptorFiles.AnimationDescriptorPair()

Procedure.s findAnimationDescriptor(*animation)
  Shared animationDescriptorFiles()
  ForEach animationDescriptorFiles()
    If animationDescriptorFiles()\animation = *animation
      ProcedureReturn animationDescriptorFiles()\descriptor
    EndIf
  Next
  ProcedureReturn ""
EndProcedure

Procedure saveAnimationDescriptor(*animation.AnimationModel, path.s)
  Define i.b = 0, text.s
  If path = ""
    ProcedureReturn 0
  EndIf 
  CreateFile(0, path)
  WriteStringN(0, Str(ListSize(*animation\frames())))
  If Not *animation\baseSpeed = 1
    If *animation\baseSpeed < 1 And Not *animation\baseSpeed = -1
      WriteStringN(0, "s" + StrF(*animation\baseSpeed, 3))
    Else
      WriteStringN(0, "s" + Str(*animation\baseSpeed))
    EndIf 
  EndIf 
  ForEach *animation\frames()
    text = ""
    If Not (*animation\frames()\origin\x = (*animation\frames()\display\w / 2) And *animation\frames()\origin\y = *animation\frames()\display\h)
      text + "o" + Str(*animation\frames()\origin\x) + " " + Str(*animation\frames()\origin\y) + " "
    EndIf 
    If *animation\frames()\duration
      text + "d" + Str(*animation\frames()\duration) + " "
    EndIf 
    If *animation\frames()\speedmode & 1
      text + "m" + Str(*animation\frames()\speedmode) + " " + Str(*animation\frames()\speed\x) + " " + Str(*animation\frames()\speed\y) + " "
    EndIf
    If Not text = ""
       WriteStringN(0, "f" + Str(i) + " " + text)
    EndIf 
    ForEach *animation\frames()\hitboxes()
      WriteStringN(0, "h" + Str(i) + " " + Str(*animation\frames()\hitboxes()\x) + " " + 
                      Str(*animation\frames()\hitboxes()\y) + " " + Str(*animation\frames()\hitboxes()\x2) + " " +
                      Str(*animation\frames()\hitboxes()\y2) + " " + StrF(*animation\frames()\hitboxes()\damage) + " " +
                      Str(*animation\frames()\hitboxes()\angle) + " " + StrF(*animation\frames()\hitboxes()\bkb, 2) + " " +
                      StrF(*animation\frames()\hitboxes()\skb, 2) + " " + Str(*animation\frames()\hitboxes()\priority) + " " +
                      Str(*animation\frames()\hitboxes()\hit))
    Next 
    ForEach *animation\frames()\hurtboxes()
      WriteStringN(0, "c" + Str(i) + " " + Str(*animation\frames()\hurtboxes()\x) + " " + Str(*animation\frames()\hurtboxes()\y) +
                      " " + Str(*animation\frames()\hurtboxes()\x2) + " " + Str(*animation\frames()\hurtboxes()\y2))
    Next 
    i + 1
  Next 
  CloseFile(0)
EndProcedure

Procedure saveAll()
  Shared animationDescriptorFiles()
  ForEach animationDescriptorFiles()
    saveAnimationDescriptor(animationDescriptorFiles()\animation, animationDescriptorFiles()\descriptor)
  Next
EndProcedure

Procedure setDescriptorFile(*animation)
  Shared animationDescriptorFiles()
  If findAnimationDescriptor(*animation) = ""
    AddElement(animationDescriptorFiles())
    animationDescriptorFiles()\animation = *animation
  EndIf 
  animationDescriptorFiles()\descriptor = InputRequester("Kuribrawl Frame Tool", "Name/Path of the descriptor file for this animation : ", "")
  SetGadgetText(11, animationDescriptorFiles()\descriptor)
EndProcedure

Procedure generateDefaultHurtboxes(*animation.AnimationModel)
  ForEach *animation\frames()
    addHurtbox(*animation\frames(), -*animation\frames()\origin\x, *animation\frames()\origin\y, *animation\frames()\display\w, *animation\frames()\display\h)
  Next
  FirstElement(*animation\frames())
  drawFrame(*animation, 1)
EndProcedure

Procedure setProjectDBLoaded(state.b)
  Shared projectDBLoaded
  projectDBLoaded = state
  DisableMenuItem(0, #MENUITEM_SAVE, 1 - state)
  DisableMenuItem(0, #MENUITEM_SAVEALL, 1 - state)
  DisableMenuItem(0, #MENUITEM_SETDESCRIPTOR, 1 - state)
EndProcedure

Procedure loadProjectDB(path.s)
  Shared animationDescriptorFiles(), characters()
  Define tag.s, infos.s, file.s, *animation
  If Not ReadFile(0, path)
    ProcedureReturn 0
  EndIf 
  ClearList(animationDescriptorFiles())
  While Not Eof(0)
    ReadString(0) ;osef
    tag = ReadString(0)
    infos = StringField(tag, 2, " ")
    tag = StringField(tag, 1, " ")
    If Not StringField(tag, 1, ":") = "A"
      Continue
    EndIf 
    tag = StringField(tag, 2, ":")
    If Left(tag, 1) = "_"
      Continue
    EndIf 
    If Right(infos, 4) = ".dat"
      *animation = characters(StringField(tag, 1, "/"))\animations(StringField(tag, 2, "/"))
      AddElement(animationDescriptorFiles())
      animationDescriptorFiles()\animation = *animation
      animationDescriptorFiles()\descriptor = infos
    EndIf  
  Wend 
  CloseFile(0)
  SetCurrentDirectory(GetPathPart(path))
  ProcedureReturn 1 
EndProcedure

Procedure tryLoadProjectDB(path.s)
  loading:
  If loadProjectDB(path)
    ProcedureReturn 1
  EndIf 
  If MessageRequester("Error", "Can't find project_db.txt in this directory. Do you want to find it yourself ?", #PB_MessageRequester_YesNo) = #PB_MessageRequester_Yes
    path.s = OpenFileRequester("Find this project's project_db.txt", GetCurrentDirectory(), "project_db.txt|*.txt", 0)
    If path = ""
      ProcedureReturn 0
    EndIf 
    Goto loading
  Else
    ProcedureReturn 0
  EndIf   
EndProcedure

Procedure makeMenu()
  Shared *itemAnims(), totalAnims, characters(), firstAnimItem
  Dim *itemAnims.AnimationModel(totalAnims)
  CreateMenu(0, WindowID(0))
  MenuTitle("Animation")
  OpenSubMenu("Open")
  
  firstAnimItem = #MENUITEMS
  Define i.l = 0
  
  ForEach characters()
    OpenSubMenu(characters()\name)
    ForEach characters()\animations()
      MenuItem(i + firstAnimItem, MapKey(characters()\animations()))
      *itemAnims(i) = @characters()\animations()
      i + 1
    Next 
    CloseSubMenu()
  Next   
  CloseSubMenu()
  
  MenuItem(#MENUITEM_SAVE, "Save")
  MenuItem(#MENUITEM_SETDESCRIPTOR, "Set descriptor file")
  MenuItem(#MENUITEM_ANIMINFO, "Edit Anim Info")
  MenuItem(#MENUITEM_FRAMEINFO, "Edit Frame Info")
  
  MenuTitle("Project")
  MenuItem(#MENUITEM_SAVEALL, "Save All")
  MenuItem(#MENUITEM_LOADPROJECTDB, "Load project_db")
  MenuItem(#MENUITEM_REBUILD, "Save and rebuild")
  MenuItem(#MENUITEM_RELOAD, "Reload Data File")
  
  MenuTitle("Cboxes")
  MenuItem(#MENUITEM_ADDHITBOX, "Add hitbox")
  MenuItem(#MENUITEM_ADDHURTBOX, "Add hurtbox")
  MenuItem(#MENUITEM_DELETEBOX, "Delete collision box" + Chr(9) + "Del")
  MenuItem(#MENUITEM_GENERATEHURTBOXES, "Generate default hurtboxes")
  MenuItem(#MENUITEM_COPYCBOX, "Copy collision box" + Chr(9) + "Ctrl+C")
  MenuItem(#MENUITEM_CUTCBOX, "Cut collision box" + Chr(9) + "Ctrl+X")
  MenuItem(#MENUITEM_PASTECBOX, "Paste collision box" + Chr(9) + "Ctrl+V")
EndProcedure  

Procedure resetData()
  Shared characters(), animationDescriptorFiles()
  ForEach kuribrawl\characters()
    ClearMap(kuribrawl\characters()\animations())
  Next
  ClearMap(characters())
  ClearList(animationDescriptorFiles())
  *selectedAnim = 0
EndProcedure

Procedure onLoad()
  Shared characters(), totalAnims
  
  totalAnims = 0
  ForEach kuribrawl\characters()
    
    ForEach kuribrawl\characters()\animations()
      If ListSize(kuribrawl\characters()\animations()\frames()) = 0
        res.s = InputRequester("Kuribrawl Frame Tool", "Animation " + MapKey(kuribrawl\characters()\animations()) + " of character " + MapKey(kuribrawl\characters()) + Chr(10) + " doesn't have any info. Please enter a frame number.", "0")
        nb.b = Val(res)
        If nb < 0 
          nb = 1
        EndIf 
        Dim *frames.FrameModel(0)
        *anim.AnimationModel = kuribrawl\characters()\animations()
        makeFrames(*anim, SpriteWidth(*anim\spritesheet), SpriteHeight(*anim\spritesheet), nb, *frames(), 0)
      EndIf 
      
      CopyList(kuribrawl\characters()\animations()\frames(), characters(MapKey(kuribrawl\characters()))\animations(MapKey(kuribrawl\characters()\animations()))\frames())
      characters()\animations()\spriteSheet = kuribrawl\characters()\animations()\spriteSheet.l
      characters()\animations()\spriteSheetL = kuribrawl\characters()\animations()\spriteSheetL.l
      characters()\animations()\baseSpeed = kuribrawl\characters()\animations()\baseSpeed.d
      totalAnims + 1
    Next 
    characters()\name = kuribrawl\characters()\name
  Next
  makeMenu()
EndProcedure

Procedure load(path.s)
  Shared dataFilePath, originalPath.s
  originalPath = GetCurrentDirectory()
  loadDatafile:
  If Not loadGameData(path)
    If MessageRequester("Kuribrawl Frame Tool", "Can't find data file (data.twl). Do you want to look for it maunally ?", #PB_MessageRequester_YesNo) = #PB_MessageRequester_Yes
      path = OpenFileRequester("Find data.twl", "", "Kuribrawl Data File (data.twl) | data.twl", 0)
      If Not path = ""
        Goto loadDataFile
      EndIf 
    EndIf
    ProcedureReturn 0 
  EndIf
  SetCurrentDirectory(GetPathPart(path))
  dataFilePath = GetCurrentDirectory() + GetFilePart(path)
  SetCurrentDirectory(originalPath)
  onLoad()
  ProcedureReturn 1
EndProcedure

Procedure reload()
  Shared dataFilePath
  resetData()
  loadGameData(dataFilePath)
  onLoad()
  SetGadgetText(11, "")
  tryLoadProjectDB("project_db.txt")
  *selectedAnim = 0
EndProcedure 

Procedure copyCBox(*cbox, type)
  SetClipboardText(makeHitboxText(*cbox, type))
EndProcedure

Procedure cutCBox(*cbox, type)
  copyCBox(*cbox, type)
  deleteBox()
EndProcedure

Procedure pasteCBox()
  Define type.b, text.s, x.l, y.l, w.l, h.l, *cbox
  text = GetClipboardText()
  Select Mid(text, 1, 1)
    Case "h"
      type = #CBOX_TYPE_HIT
    Case "c"
      type = #CBOX_TYPE_HURT
    Default 
      ProcedureReturn 0
  EndSelect
  x = Val(StringField(text, 2, " "))
  y = Val(StringField(text, 3, " "))
  w = Val(StringField(text, 4, " "))
  h = Val(StringField(text, 5, " "))
  Select type
    Case #CBOX_TYPE_HURT
      *cbox = newHurtbox(x, y, w, h)
    Case #CBOX_TYPE_HIT
      *cbox = newHitbox(x, y, w, h)
      setField(*cbox, Hitbox, damage, D, ValD(StringField(text, 6, " ")))
      setField(*cbox, Hitbox, angle, W, Val(StringField(text, 7, " ")))
  EndSelect
EndProcedure

Procedure tryRunDFM(path.s)
  If FileSize("datafilemaker.exe") > 0 
    ProcedureReturn RunProgram("datafilemaker", "-s", path, #PB_Program_Open)
  EndIf 
  ProcedureReturn 0
EndProcedure

Procedure rebuildDataFile()
  Shared dataFilePath, originalPath
  Define currentPath.s, program.l
  currentPath = GetCurrentDirectory()
  saveAll()
  program = tryRunDFM(currentPath)
  If Not program
    SetCurrentDirectory(originalPath)
    program = tryRunDFM(currentPath)
    If Not program
      MessageRequester("Error", "Can't run DFM." + Chr(13) + "The data file was not rebuild")
      ProcedureReturn 0
    EndIf 
  EndIf
  
  OpenWindow(1, 0, 0, 300, 100, "Kuribrawl Frame Tool", #PB_Window_WindowCentered | #PB_Window_SystemMenu, WindowID(0))
  TextGadget(#PB_Any, 50, 5, 200, 60, "Waiting for DFM to finish execution (closing this window will cancel the data file rebuilding)", #PB_Text_Center)
  EnableWindow_(WindowID(0), 0)

  
  Repeat
  Until Not ProgramRunning(program)
  CloseWindow(1)
  EnableWindow_(WindowID(0), 1)
  
  reload()
EndProcedure

Procedure editCBoxInfo()
  Shared *selectedCollisionBox, selectedCollisionBoxType
  
  OpenWindow(1, 0, 0, 150, 200, "Editing collision box info", #PB_Window_WindowCentered | #PB_Window_SystemMenu, WindowID(0))
  
  If selectedCollisionBoxType = #CBOX_TYPE_HIT
    TextGadget(#PB_Any, 5, 5, 140, 20, "Damages")
    SpinGadget(21, 80, 5, 60, 20, 0, 999)
    SetGadgetText(21, StrF(getField(*selectedCollisionBox, Hitbox, damage, D)))
    
    TextGadget(#PB_Any, 5, 30, 140, 20, "Angle")
    ButtonGadget(22, 40, 30, 20, 20, "")
    SpinGadget(20, 80, 30, 60, 20, 0, 360, #PB_Spin_Numeric)
    SetGadgetText(20, StrD(getField(*selectedCollisionBox, Hitbox, angle, W)))
    
    TextGadget(#PB_Any, 5, 55, 140, 20, "Base KB")
    SpinGadget(28, 80, 55, 60, 20, 0, 99, #PB_Spin_Numeric)
    SetGadgetText(28, StrF(getField(*selectedCollisionBox, Hitbox, bkb, D)))
    
    TextGadget(#PB_Any, 5, 80, 140, 20, "Scaling KB")
    SpinGadget(29, 80, 80, 60, 20, 0, 99, #PB_Spin_Numeric)
    SetGadgetText(29, StrF(getField(*selectedCollisionBox, Hitbox, skb, D)))
    
    TextGadget(#PB_Any, 5, 105, 140, 20, "Priority")
    SpinGadget(26, 80, 105, 60, 20, 0, 99, #PB_Spin_Numeric)
    SetGadgetText(26, StrD(getField(*selectedCollisionBox, Hitbox, priority, B)))
    
    TextGadget(#PB_Any, 5, 130, 140, 20, "HitID")
    SpinGadget(27, 80, 130, 60, 20, 0, 99, #PB_Spin_Numeric)
    GadgetToolTip(27, "An hitbox can hit a fighter that has already been struck by another hitbox in the same move If they have different hitIDs. Useful For multihits.")    
    SetGadgetText(27, StrD(getField(*selectedCollisionBox, Hitbox, hit, B)))
  ElseIf selectedCollisionBoxType = #CBOX_TYPE_HURT
  EndIf 
  ButtonGadget(23, 5, 175, 140, 20, "Save")
  
  EnableWindow_(WindowID(0), 0)
EndProcedure

Procedure editAnimInfo()
  Shared *selectedAnim
  
  If Not *selectedAnim
    ProcedureReturn 0
  EndIf
  
  OpenWindow(1, 0, 0, 150, 150, "Editing animation info", #PB_Window_WindowCentered | #PB_Window_SystemMenu, WindowID(0))
  
  TextGadget(#PB_Any, 5, 5, 140, 20, "Speed")
  SpinGadget(21, 80, 5, 60, 20, 0, 999)
  SetGadgetText(21, StrD(*selectedAnim\baseSpeed))
  
  ButtonGadget(24, 5, 125, 140, 20, "Save")
  
  EnableWindow_(WindowID(0), 0)
  
EndProcedure

Procedure editFrameInfo()
  Shared *selectedAnim
  
  If Not *selectedAnim
    ProcedureReturn 0
  EndIf
  
  OpenWindow(1, 0, 0, 170, 180, "Editing frame info", #PB_Window_WindowCentered | #PB_Window_SystemMenu, WindowID(0))
  
  TextGadget(#PB_Any, 5, 5, 140, 20, "Duration")
  SpinGadget(20, 100, 5, 60, 20, 0, 999)
  SetGadgetText(20, StrD(*selectedAnim\frames()\duration))
  CheckBoxGadget(30, 5, 35, 80, 20, "X speed")
  SetGadgetState(30, 1 - (*selectedAnim\frames()\speedMode & 1000) >> 3)
  SpinGadget(21, 100, 35, 60, 20, 0, 999)
  SetGadgetText(21, StrD(*selectedAnim\frames()\speed\x))
  CheckBoxGadget(31, 5, 65, 80, 20, "Y speed")
  SetGadgetState(31, 1 - (*selectedAnim\frames()\speedMode & 10000) >> 4)
  SpinGadget(26, 100, 65, 60, 20, 0, 999)
  SetGadgetText(26, StrD(*selectedAnim\frames()\speed\y))
  CheckBoxGadget(27, 5, 90, 160, 20, "Enable speed changing")
  SetGadgetState(27, *selectedAnim\frames()\speedMode & 1)
  GadgetToolTip(27, "If this is enabled, the fighter's speed will be modified while displaying this frame.")
  CheckBoxGadget(28, 5, 110, 160, 20, "□Add Speed ✓Set speed")
  SetGadgetState(28, (*selectedAnim\frames()\speedMode & %10) >> 1)
  CheckBoxGadget(29, 5, 130, 160, 20, "□Once ✓Whole Frame")
  SetGadgetState(29, (*selectedAnim\frames()\speedMode & %100) >> 2)
  
  ButtonGadget(25, 5, 155, 160, 20, "Save")
  
  EnableWindow_(WindowID(0), 0)
  
EndProcedure

Procedure rectSelectionCreateCBox(type.b)
  Shared rectSelection, viewPosition
  x = rectSelection\coord\left
  y = rectSelection\coord\top
  w = rectSelection\coord\right - rectSelection\coord\left
  h = rectSelection\coord\bottom - rectSelection\coord\top
  
  If w < 0
    x + w
    w = -w
  EndIf
  If h < 0
    y + h
    h = -h
  EndIf 
  
  x = -(*selectedAnim\frames()\origin\x - (x - viewPosition\x))
  y = viewPosition\y + *selectedAnim\frames()\origin\y - y
 
  If type = #CBOX_TYPE_HURT
    newHurtbox(x, y, w, h)
  Else 
    newHitbox(x, y, w, h)
  EndIf 
EndProcedure

Procedure menuCallback()
  Shared *itemAnims(), firstAnimItem, *selectedAnim, *selectedCollisionBox, selectedCollisionBoxType, popUpMenuPosition
  event.l = EventMenu()
  If event < firstAnimItem
    Select event
      Case #MENUITEM_ADDHITBOX
        newHitbox(-20, 60, 40, 40)
      Case #MENUITEM_ADDHURTBOX
        newHurtbox(-20, 60, 40, 40)
      Case #MENUITEM_DELETEBOX
        deleteBox() 
      Case #MENUITEM_SAVE
        If *selectedAnim 
          saveAnimationDescriptor(*selectedAnim, findAnimationDescriptor(*selectedAnim))
        EndIf     
      Case #MENUITEM_SAVEALL
        saveAll()
      Case #MENUITEM_SETDESCRIPTOR
        If *selectedAnim
          setDescriptorFile(*selectedAnim)
        EndIf 
      Case #MENUITEM_GENERATEHURTBOXES
        If *selectedAnim
          generateDefaultHurtboxes(*selectedAnim)
        EndIf 
      Case #MENUITEM_LOADPROJECTDB
        path.s = OpenFileRequester("Find this project's project_db.txt", GetCurrentDirectory(), "Text Files (.txt)|*.txt", 0)
        If Not path = ""
          setProjectDBLoaded(Bool(loadProjectDB(path)))
        EndIf 
      Case #MENUITEM_COPYCBOX
        copyCBox(*selectedCollisionBox, selectedCollisionBoxType)
      Case #MENUITEM_PASTECBOX
        pasteCBox()
      Case #MENUITEM_CUTCBOX
        cutCBox(*selectedCollisionBox, selectedCollisionBoxType)
      Case #MENUITEM_RELOAD
        reload()
      Case #MENUITEM_REBUILD
        rebuildDataFile()
      Case #MENUITEM_CBOXINFO
        editCBoxInfo()
      Case #MENUITEM_ANIMINFO
        editAnimInfo()
      Case #MENUITEM_FRAMEINFO
        editFrameInfo()
      Case #MENUITEM_RECTSELECT_CREATEHITBOX
        rectSelectionCreateCBox(#CBOX_TYPE_HIT)
      Case #MENUITEM_RECTSELECT_CREATEHURTBOX
        rectSelectionCreateCBox(#CBOX_TYPE_HURT)
    EndSelect
  Else
    setAnimation(*itemAnims(event - firstAnimItem))
  EndIf 
EndProcedure   

Procedure angleViewerCallback(angle.d)
  CloseWindow(2)
  SetGadgetText(20, Str(Degree(angle)))
EndProcedure

Procedure promptAngle()
  Shared *selectedCollisionBox
  OpenWindow(2, 0, 0, 300, 300, "Select an angle", #PB_Window_SystemMenu | #PB_Window_WindowCentered, WindowID(1))
  *av = GMB_AngleViewer::AngleViewerGadget(-1, 5, 5, 290, Radian(Val(GetGadgetText(22))), @angleViewerCallback())
  GMB_AngleViewer::SetAngleP(*av, Radian(getField(*selectedCollisionBox, Hitbox, angle, W)))
EndProcedure

Procedure gadgetCallback()
  Shared *selectedAnim, *selectedCollisionBox, selectedCollisionBoxType, diff.l
  gadget = EventGadget()
  event.l = EventType()
  Select gadget
    Case 0
      If Not *selectedAnim
        ProcedureReturn
      EndIf 
      If event = #PB_EventType_Up
        If NextElement(*selectedAnim\frames())
          onFrameChanged(*selectedAnim)
        EndIf 
      Else
        If PreviousElement(*selectedAnim\frames())
          onFrameChanged(*selectedAnim)
        EndIf 
      EndIf 
    Case 2
      If *selectedCollisionBox And (event = #PB_EventType_Change Or event = #PB_EventType_Up Or event = #PB_EventType_Down)
        *selectedCollisionBox\x = Val(GetGadgetText(2))
        drawFrame(*selectedAnim, 1)
      EndIf
    Case 4
      If *selectedCollisionBox And (event = #PB_EventType_Change Or event = #PB_EventType_Up Or event = #PB_EventType_Down)
        *selectedCollisionBox\y = Val(GetGadgetText(4))
        drawFrame(*selectedAnim, 1)
      EndIf
    Case 6
      If *selectedCollisionBox And (event = #PB_EventType_Change Or event = #PB_EventType_Up Or event = #PB_EventType_Down)
        *selectedCollisionBox\x2 = Val(GetGadgetText(6))
        drawFrame(*selectedAnim, 1)
      EndIf
    Case 8
      If *selectedCollisionBox And (event = #PB_EventType_Change Or event = #PB_EventType_Up Or event = #PB_EventType_Down)
        *selectedCollisionBox\y2 = Val(GetGadgetText(8))
        drawFrame(*selectedAnim, 1)
      EndIf
    Case 9
      SetGadgetText(10, makeHitboxText(*selectedCollisionBox, selectedCollisionBoxType))
    Case 12
      If *selectedAnim And (event = #PB_EventType_Change Or event = #PB_EventType_Up Or event = #PB_EventType_Down)
        diff = *selectedAnim\frames()\origin\x
        *selectedAnim\frames()\origin\x = Val(GetGadgetText(12))
        diff = *selectedAnim\frames()\origin\x - diff
        If diff
          ForEach *selectedAnim\frames()\hurtboxes()
            *selectedAnim\frames()\hurtboxes()\x - diff
          Next 
          ForEach *selectedAnim\frames()\hitboxes()
            *selectedAnim\frames()\hitboxes()\x - diff
          Next 
        EndIf 
        drawFrame(*selectedAnim, 1)
      EndIf
    Case 13
      If *selectedAnim And (event = #PB_EventType_Change Or event = #PB_EventType_Up Or event = #PB_EventType_Down)
        diff = *selectedAnim\frames()\origin\y
        *selectedAnim\frames()\origin\y = Val(GetGadgetText(13))
        diff = *selectedAnim\frames()\origin\y - diff
        If diff
          ForEach *selectedAnim\frames()\hurtboxes()
            *selectedAnim\frames()\hurtboxes()\y + diff
          Next 
          ForEach *selectedAnim\frames()\hitboxes()
            *selectedAnim\frames()\hitboxes()\y + diff
          Next 
        EndIf 
        drawFrame(*selectedAnim, 1)
      EndIf
    Case 14
      If *selectedAnim And (event = #PB_EventType_Change Or event = #PB_EventType_Up Or event = #PB_EventType_Down)
        If GetGadgetText(14)
          *selectedAnim\frames()\duration = Val(GetGadgetText(14))
        EndIf 
      EndIf 
    Case 21, 26
      Select EventType()
        Case #PB_EventType_Up
          SetGadgetText(21, StrD(ValD(GetGadgetText(20)) + 0.1))
        Case #PB_EventType_Down
          SetGadgetText(21, StrD(ValD(GetGadgetText(20)) - 0.1))
      EndSelect
    Case 22
      promptAngle()
    Case 23
      Select selectedCollisionBoxType
        Case #CBOX_TYPE_HIT
          setField(*selectedCollisionBox, Hitbox, damage, D, ValD(GetGadgetText(21)))
          setField(*selectedCollisionBox, Hitbox, angle, W, Val(GetGadgetText(20)))
          setField(*selectedCollisionBox, Hitbox, priority, B, Val(GetGadgetText(26)))
          setField(*selectedCollisionBox, Hitbox, hit, B, Val(GetGadgetText(27)))
          setField(*selectedCollisionBox, Hitbox, bkb, D, ValD(GetGadgetText(28)))
          setField(*selectedCollisionBox, Hitbox, skb, D, ValD(GetGadgetText(29)))
      EndSelect
      CloseWindow(1)
      EnableWindow_(WindowID(0), 1)
    Case 24
      *selectedAnim\baseSpeed = ValD(GetGadgetText(21))    
      CloseWindow(1)
      EnableWindow_(WindowID(0), 1)
    Case 25
      *selectedAnim\frames()\duration = Val(GetGadgetText(20))
      
      If GetGadgetState(27)
        *selectedAnim\frames()\speedMode = *selectedAnim\frames()\speedMode | %1
      Else
        *selectedAnim\frames()\speedMode = *selectedAnim\frames()\speedMode & 00
      EndIf 
      If GetGadgetState(28)
        *selectedAnim\frames()\speedMode = *selectedAnim\frames()\speedMode | %10
      Else
        *selectedAnim\frames()\speedMode = *selectedAnim\frames()\speedMode & %01
      EndIf 
      If GetGadgetState(29)
        *selectedAnim\frames()\speedMode = *selectedAnim\frames()\speedMode | %100
      Else
        *selectedAnim\frames()\speedMode = *selectedAnim\frames()\speedMode & %011
      EndIf 
      If Not GetGadgetState(30)
        *selectedAnim\frames()\speedMode = *selectedAnim\frames()\speedMode | %1000
      Else
        *selectedAnim\frames()\speedMode = *selectedAnim\frames()\speedMode & %0111
      EndIf
      If Not GetGadgetState(31)
        *selectedAnim\frames()\speedMode = *selectedAnim\frames()\speedMode | %10000
      Else
        *selectedAnim\frames()\speedMode = *selectedAnim\frames()\speedMode & %01111
      EndIf
      If *selectedAnim\frames()\speedmode & 1
        *selectedAnim\frames()\speed\x = ValD(GetGadgetText(21))
        *selectedAnim\frames()\speed\y = ValD(GetGadgetText(26))
      EndIf 
        
      CloseWindow(1)
      EnableWindow_(WindowID(0), 1)
  EndSelect
EndProcedure

Procedure startRectSelection()
  Shared rectSelection, *selectedAnim
  If Not *selectedAnim
    ProcedureReturn
  EndIf 
  rectSelection\coord\left = WindowMouseX(0) - 5
  rectSelection\coord\top = WindowMouseY(0) - 5
  rectSelection\active = 1
EndProcedure

Procedure updateRectSelection()
  Shared rectSelection
  rectSelection\coord\right = WindowMouseX(0) - 5
  rectSelection\coord\bottom = WindowMouseY(0) - 5
EndProcedure

Procedure displaySelectionRectangle()
  Shared rectSelection, *selectedAnim
  drawFrame(*selectedAnim, 1)
  With rectSelection
    StartDrawing(ScreenOutput())
    DrawingMode(#PB_2DDrawing_Outlined)
    Box(\coord\left, \coord\top, \coord\right - \coord\left, \coord\bottom - \coord\top, #White)
    StopDrawing()
  EndWith
  FlipBuffers()
EndProcedure

Procedure endRectSelection()
  Shared rectSelection
  If Not rectSelection\active
    ProcedureReturn
  EndIf 
  rectSelection\active = 0
  If Abs(rectSelection\coord\left - rectSelection\coord\right) > 10 Or Abs (rectSelection\coord\top - rectSelection\coord\bottom) > 10
    DisplayPopupMenu(3, WindowID(0))
  Else
    drawFrame(*selectedAnim, 1)
  EndIf 
EndProcedure

Procedure clickCallback()
  selectPointedCBox()
EndProcedure

Procedure rightClickCallback()
  Shared popUpMenuPosition
  If selectPointedCBox()
    DisplayPopupMenu(1, WindowID(0))
  ElseIf WindowMouseX(0) > 5 And WindowMouseY(0) > 5 And WindowMouseX(0) < 5 + #CANVAS_W And WindowMouseY(0) < 5 + #CANVAS_H
    DisplayPopupMenu(2, WindowID(0))
  EndIf 
EndProcedure  
  
BindEvent(#PB_Event_Menu, @menuCallback())
BindEvent(#PB_Event_LeftClick, @clickCallback())
BindEvent(#PB_Event_Gadget, @gadgetCallback())
BindEvent(#PB_Event_RightClick, @rightClickCallback())

SpinGadget(0, #CANVAS_W + 10, 5, 20, 50, 0, 1, #PB_Spin_ReadOnly)
FrameGadget(#PB_Any, #CANVAS_W + 35, 5, 160, 50, "Frame Info")
TextGadget(#PB_Any, #CANVAS_W + 40, 25, 20, 20, "ox")
SpinGadget(12, #CANVAS_W + 60, 25, 50, 20, -999, 999, #PB_Spin_Numeric)
TextGadget(#PB_Any, #CANVAS_W + 120, 25, 20, 20, "oy")
SpinGadget(13, #CANVAS_W + 140, 25, 50, 20, -999, 999, #PB_Spin_Numeric)
FrameGadget(#PB_Any, #CANVAS_W + 10, 60, 185, 80, "Collision Box Info")
TextGadget(1, #CANVAS_W + 20, 80, 10, 20, "x")
SpinGadget(2, #CANVAS_W + 35, 80, 50, 20, -999, 999, #PB_Spin_Numeric)
TextGadget(3, #CANVAS_W + 95, 80, 10, 20, "y")
SpinGadget(4, #CANVAS_W + 110, 80, 50, 20, -999, 999, #PB_Spin_Numeric)
TextGadget(5, #CANVAS_W + 20, 110, 10, 20, "w")
SpinGadget(6, #CANVAS_W + 35, 110, 50, 20, -999, 999, #PB_Spin_Numeric)
TextGadget(7, #CANVAS_W + 95, 110, 10, 20, "h")
SpinGadget(8, #CANVAS_W + 110, 110, 50, 20, -999, 999, #PB_Spin_Numeric)
ButtonGadget(9, #CANVAS_W + 10, 145, 20, 20, "OK")
StringGadget(10, #CANVAS_W + 35, 145, 150, 20, "")
TextGadget(11, 5, #CANVAS_H + 10, #CANVAS_W, 20, "")

CreatePopupMenu(1)

;defaultMenuItem(1, #MENUITEM_CTX_SELECT, "Select")
MenuItem(#MENUITEM_COPYCBOX, "Copy")
MenuItem(#MENUITEM_CUTCBOX, "Cut")
MenuItem(#MENUITEM_DELETEBOX, "Delete")
MenuItem(#MENUITEM_CBOXINFO, "Edit CBox info")

CreatePopupMenu(2)
MenuItem(#MENUITEM_FRAMEINFO, "Edit Frame Info")

CreatePopupMenu(3)
MenuItem(#MENUITEM_RECTSELECT_CREATEHITBOX, "Create hitbox")
MenuItem(#MENUITEM_RECTSELECT_CREATEHURTBOX, "Create hurtbox")

AddKeyboardShortcut(0, #PB_Shortcut_Control | #PB_Shortcut_C, #MENUITEM_COPYCBOX)
AddKeyboardShortcut(0, #PB_Shortcut_Control | #PB_Shortcut_X, #MENUITEM_CUTCBOX)
AddKeyboardShortcut(0, #PB_Shortcut_Control | #PB_Shortcut_V, #MENUITEM_PASTECBOX)
AddKeyboardShortcut(0, #PB_Shortcut_Delete, #MENUITEM_DELETEBOX)

If Not load(#DEFAULT_DATAFILE_PATH)
  End
EndIf 

setProjectDBLoaded(tryLoadProjectDB("..\src\res\project_db.txt"))

;-Main Loop
Define event.l
Repeat
  event = WaitWindowEvent()
  Select event
    Case #PB_Event_CloseWindow
      Select EventWindow()
        Case 0
          End
        Case 1
          CloseWindow(1)
          EnableWindow_(WindowID(0), 1)
          If IsWindow(2)
            CloseWindow(2)
          EndIf 
        Case 2
          CloseWindow(2)
      EndSelect
    Case #PB_Event_LeftDoubleClick
      If selectPointedCBox()
        editCBoxInfo()
      EndIf 
    Case #WM_LBUTTONUP
      endRectSelection()
    Case #WM_LBUTTONDOWN
      If IsWindowEnabled_(WindowID(0))
        
        startRectSelection()
      EndIf
      
  EndSelect
  
  If rectSelection\active
    updateRectSelection()
    displaySelectionRectangle()
  EndIf 
  
  Delay(16)
ForEver 
; IDE Options = PureBasic 5.72 (Windows - x64)
; CursorPosition = 833
; FirstLine = 816
; Folding = --------
; EnableXP
; UseIcon = ..\GraphicDesignIsMyPassion\iconFT.ico