Macro writeMaxValue(file, type)
    write#type(file, #MAX_VALUE_#type)
EndMacro

Macro valByte(valueS)
    Val(valueS)    
EndMacro

Macro valShort(valueS)
    Val(valueS)    
EndMacro

Macro valUShort(valueS)
    Val(valueS)    
EndMacro

Macro valDouble(valueS)
    ValD(valueS)
EndMacro

Macro writeValue(file, valueS, type)
    If valueS = "x"
        writeMaxValue(file, type)
    Else
        write#type(file, val#type(valueS))
    EndIf   
    
EndMacro

Macro GSAP(obj, struct, field)
    @obj + OffsetOf(struct\field)
EndMacro

; IDE Options = PureBasic 6.00 LTS (Windows - x64)
; Folding = --
; EnableXP