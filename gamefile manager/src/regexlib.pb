;Library by Téo "TwilCynder" Tinarrage
;Updated 11.01.2023


Procedure checkString(regex.l, string.s)
    ExamineRegularExpression(regex, string)
    If NextRegularExpressionMatch(regex)
        size.l = RegularExpressionMatchLength(regex)
        ProcedureReturn Bool(size = Len(string))
    EndIf
    ProcedureReturn #False
EndProcedure

; IDE Options = PureBasic 6.00 LTS (Windows - x64)
; CursorPosition = 10
; Folding = -
; EnableXP
; DPIAware