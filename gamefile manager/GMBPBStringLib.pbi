Import "GMBCStringLib.lib"
    GMB_StringFieldUnicode.l(*string, field.l, separator.u, *startPos, *l)
    GMB_CountFieldsUnicode(*string, separator.u)
    GMB_SepRightUnicode(*string, separator.u, n.i)
EndImport

Procedure.s GMB_StringField(line.s, field.l, separator.s)
    startPos.l
    length.l
    GMB_StringFieldUnicode(@line, field, Asc(separator), @startPos, @length)
    ProcedureReturn Mid(line, startPos, length)
EndProcedure

Macro GMB_StringField_(line, field, separator, output)
    GMB_StringFieldUnicode(@line, field, Asc(separator), @startPos, @length)
    output = Mid(line, startPos, length)
EndMacro

Procedure GMB_CountFields(line.s, separator.s)
    
    ProcedureReturn GMB_CountFieldsUnicode(@line, Asc(separator))
EndProcedure

Macro GMB_CountFields_(line, separator)
    GMB_CountFieldsUnicode(@line, Asc(separator))
EndMacro

Procedure.s GMB_SepRight(line.s, separator.s, n.i)
    ProcedureReturn Mid(line, GMB_SepRightUnicode(@line, Asc(separator), n))
EndProcedure

Macro GMB_SepRight_(line, separator, n)
    GMB_SepRightUnicode(@line, Asc(separator), n)
EndMacro


; IDE Options = PureBasic 5.72 (Windows - x64)
; CursorPosition = 27
; Folding = --
; EnableXP