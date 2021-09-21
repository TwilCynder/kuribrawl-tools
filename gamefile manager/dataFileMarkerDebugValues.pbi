Structure DebugValues
    fileTypeNames.s[#FILETYPES]
    hurtboxValues.s[5]
    hitboxValues.s[11]
    hurtboxTypes.s[3]
    hitboxTypes.s[3]
    championValues.s[#CHAMPION_VALUES_NB]
    championValueTypes.s[2]
EndStructure

Procedure.i initDebugValues()
    *vals.DebugValues = AllocateMemory(SizeOf(DebugValues))
    
    *vals\fileTypeNames[0] = "Animation"
    *vals\fileTypeNames[1] = "Left Animation"
    *vals\fileTypeNames[2] = "Champion"
    *vals\fileTypeNames[3] = "Simple image"
    *vals\fileTypeNames[4] = "Stage"
    
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
    
    ProcedureReturn *vals
EndProcedure
; IDE Options = PureBasic 5.72 (Windows - x64)
; CursorPosition = 75
; FirstLine = 29
; Folding = -
; EnableXP