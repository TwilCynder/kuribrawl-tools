Structure DebugValues
    fileTypeNames.s[#FILETYPES]
    hurtboxValues.s[5]
    hitboxValues.s[11]
    hurtboxTypes.s[3]
    hitboxTypes.s[3]
    championValues.s[#CHAMPION_VALUES_NB]
    championValueTypes.s[#TYPES]
    stagePlatformValues.s[10]
    stageBackgroundValues.s[10]
    stageValues.s[#STAGE_VALUES_NB]
EndStructure

Procedure.i initDebugValues()
    *vals.DebugValues = AllocateMemory(SizeOf(DebugValues))
    
    *vals\fileTypeNames[0] = "Animation"
    *vals\fileTypeNames[1] = "Left Animation"
    *vals\fileTypeNames[2] = "Champion"
    *vals\fileTypeNames[3] = "Stage"
    *vals\fileTypeNames[4] = "Simple image"
    *vals\fileTypeNames[5] = "Simple file"
    *vals\fileTypeNames[6] = "Background animation"
    
    *vals\stagePlatformValues[0] = "x"
    *vals\stagePlatformValues[1] = "y"
    *vals\stagePlatformValues[2] = "w"
    *vals\stagePlatformValues[3] = "animation name"
    
    *vals\stageBackgroundValues[0] = "animation name"
    *vals\stageBackgroundValues[1] = "x"
    *vals\stageBackgroundValues[2] = "y"
    *vals\stageBackgroundValues[3] = "profondeur" 
    
    *vals\stageValues[0] = "width"
    *vals\stageValues[1] = "height"
    *vals\stageValues[2] = "camera bounds offset x"
    *vals\stageValues[3] = "camera bounds offset y"
    *vals\stageValues[4] = "camera bounds width"
    *vals\stageValues[5] = "camera bounds height"
    
    *vals\hitboxValues[0] = "x"
    *vals\hitboxValues[1] = "y"
    *vals\hitboxValues[2] = "w"
    *vals\hitboxValues[3] = "h"
    *vals\hitboxValues[4] = "type"
    *vals\hitboxValues[5] = "damage"
    *vals\hitboxValues[6] = "angle"
    *vals\hitboxValues[7] = "base knockback"
    *vals\hitboxValues[8] = "scaling knockback"
    *vals\hitboxValues[9] = "hitID"
    *vals\hitboxValues[10] = "priority"
    
    *vals\hurtboxValues[0] = "x"
    *vals\hurtboxValues[1] = "y"
    *vals\hurtboxValues[2] = "w"
    *vals\hurtboxValues[3] = "h"
    *vals\hurtboxValues[4] = "type"
    
    *vals\hitboxTypes[0] = "damaging (normal)"
    *vals\hitboxTypes[1] = "grab"
    *vals\hitboxTypes[2] = "special"
    
    *vals\hurtboxTypes[0] = "Normal"
    *vals\hurtboxTypes[1] = "Protected"
    *vals\hurtboxTypes[2] = "Intangible"
    
    *vals\championValues[0] = "Walk speed"
    *vals\championValues[1] = "Dash speed"
    *vals\championValues[2] = "Dash Start speed"
    *vals\championValues[3] = "Dash Turn acceleration"
    *vals\championValues[4] = "Dash Stop deceleration"
    *vals\championValues[5] = "Traction (horizontal deceleration on ground)"
    *vals\championValues[6] = "Max Air H Speed"
    *vals\championValues[7] = "Air H acceleration"
    *vals\championValues[8] = "Air friction (H deceleration)"
    *vals\championValues[9] = "Full Hop V speed"
    *vals\championValues[10] = "Short Hop V speed"
    *vals\championValues[11] = "Air Jump V speed"
    *vals\championValues[12] = "Ground Forward Jump H speed"
    *vals\championValues[13] = "Ground Backward Jump H speed"
    *vals\championValues[14] = "Air Forward Jump H speed"
    *vals\championValues[15] = "Air Backward Jump H speed"
    *vals\championValues[16] = "Gravity"
    *vals\championValues[17] = "Max fall speed"
    *vals\championValues[18] = "Fast fall speed"
    *vals\championValues[19] = "Weight"
    *vals\championValues[20] = "Jumpsquat duration" ;first integer
    *vals\championValues[21] = "Dash Start duration"
    *vals\championValues[22] = "Dash Stop duration"
    *vals\championValues[23] = "Dash Turn duration"
    *vals\championValues[24] = "Landing duration"
    *vals\championValues[25] = "Guard Start duration"
    *vals\championValues[26] = "Guard Stop duration"
    *vals\championValues[27] = "Shield size"
    *vals\championValues[28] = "Shield center X"
    *vals\championValues[29] = "Shield center Y"
    *vals\championValues[30] = "Jumps"
    
    *vals\championValueTypes[0] = "Byte"
    *vals\championValueTypes[1] = "Double"
    *vals\championValueTypes[2] = "Long"
    
    ProcedureReturn *vals
EndProcedure
; IDE Options = PureBasic 6.00 LTS (Windows - x64)
; CursorPosition = 22
; Folding = -
; EnableXP
; CommandLine = ..\res