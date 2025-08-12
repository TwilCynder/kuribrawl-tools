;TODO Modifs in the file format
;  Change the $5x filemarkers to a more meaningful value (DONE)
;  Animation speed : float >double (DONE)

IncludeFile "regexlib.pb"

#DFV_MAJ = 0
#DFV_MIN = 3
#DFV_REV = 3

#RFV_MAJ = 0
#RFV_MIN = 3
#RFV_REV = 5

Structure File
    path.s
    content.s
EndStructure

Structure LoadedFile
    size.l
    *buffer
EndStructure

Enumeration
    #FILETYPE_ANIMATION
    #FILETYPE_LEFTANIM
    #FILETYPE_CHAMPION
    #FILETYPE_STAGE
    #FILETYPE_IMAGE
    #FILETYPE_FILE
    #FILETYPE_BANIMATION
    #FILETYPES
EndEnumeration

Enumeration AnimationTagPrefix $0B
    #ANIMATION_POOL_CHAMPION
    #ANIMATION_POOL_STAGE
EndEnumeration

Enumeration
    #HURTBOXTYPE_NORMAL
    #HURTBOXTYPE_PROTECTED
    #HURTBOXTYPE_INVINCIBLE
    #HURTBOXTYPE_INTANGIBLE
EndEnumeration

Enumeration
    #HITBOXTYPE_DAMAGE
    #HITBOXTYPE_GRAB
    #HITBOXTYPE_WIND
    #HITBOXTYPE_SPECIAL
EndEnumeration

Enumeration
    #ANIM_END_NORMAL
    #ANIM_END_HELPLESS
    #ANIM_END_CUSTOM
EndEnumeration

#MAX_VALUE_BYTE = 255
#MAX_VALUE_UCHAR = 127
#MAX_VALUE_USHORT = 65535
#MAX_VALUE_SHORT = 32767
#MAX_VALUE_LONG = 2147483647

;General
#FILEMARKER_404 = $F0
#FILEMARKER_INTERFILE = $FF
#FILEMARKER_DESCRIPTORSTART = $FE
#FILEMARKER_GENERICSEPARATOR = $FD

;Animations
#FILEMARKER_ANIMSPEED = 1
#FILEMARKER_FRAMEINFO = 2
#FILEMARKER_FRAMEDURATION = $20
#FILEMARKER_FRAMEORIGIN = $21
#FILEMARKER_FRAMEMOVEMENT = $22
#FILEMARKER_HURTBOXINFO = 3
#FILEMARKER_HURTBOXFRAMEINDEX = $31
#FILEMARKER_HITBOXINFO = 4
#FILEMARKER_HITBOXFRAMEINDEX = $41
#FILEMARKER_LANDINGINFO = 5
#FILEMARKER_LANDING_NORMAL = $50
#FILEMARKER_LANDING_ANIMATION = $51
#FILEMARKER_LANDING_NOTHING = $52
#FILEMARKER_ANIM_END_INFO = 6



;Champions
#FILEMARKER_MOVEINFO = 2
#FILEMARKER_MULTIMOVE = 3
#FILEMARKER_MULTIMOVEEND = $30
;Stage
#FILEMARKER_PLATFORMINFO = 1
#FILEMARKER_PLATFORMANIMATION = $10
#FILEMARKER_BACKGROUNDELEMENT = 2

Enumeration
    #TYPE_BYTE
    #TYPE_DOUBLE
    #TYPE_SHORT
    #TYPES
EndEnumeration

XIncludeFile "dataFileMakerMacros.pbi"

#CHAMPION_VALUES_NB = 31
Dim championValues.b(#CHAMPION_VALUES_NB)
#STAGE_VALUES_NB = 6
Dim stageValues.b(#STAGE_VALUES_NB)

XIncludeFile "dataFileMarkerData.pbi"

Define identifierRegex.i = CreateRegularExpression(#PB_Any, "[a-zA-Z_0-9]")
Define basicTagRegex.i = CreateRegularExpression(#PB_Any, "[a-zA-Z_0-9/]+")

NewList files.File()

Global enableMessageBox.b = #True, logging.b = 0, verbose = 0
;EnableMessageBox : windows popup on error
;Logging : console output will be used (opening one if not started from a terminal), at least for warnings
;Verbose : all kind of stuff will be displayed (implies logging)

XIncludeFile "GMBPBStringLib.pbi"

XIncludeFile "dataFileMarkerDebugValues.pbi"
Global *debugValues.DebugValues = #Null

Macro printLogForce(text)
    If logging
        PrintN(text)
    EndIf
EndMacro

Macro printLog(text)
    If verbose
        PrintN(text)
    EndIf
EndMacro

Macro warning(text)
    If logging
        PrintN("WARNING : " + text)
    EndIf
EndMacro

Procedure error(text.s)
    Shared enableMessageBox, logging
    printLog("ERROR : " + text)
    If enableMessageBox
        MessageBox_(0, "Kuribrawl Data File Maker error : " + Chr(13) + text, "Error", #MB_OK | #MB_ICONERROR)
    EndIf
    If logging
        Input()
    EndIf
    End
EndProcedure

Procedure writeShort(file, value)
    WriteWord(file, value)
EndProcedure

Procedure writeUShort(file, value)
    WriteUnicodeCharacter(file, value)
EndProcedure

Procedure ValDefault(text$, def)
    If text$ = "x"
        ProcedureReturn def
    EndIf
    ProcedureReturn Val(text$)
EndProcedure

Procedure ValDefaultShort(text$)
    ProcedureReturn ValDefault(text$, #MAX_VALUE_SHORT)
EndProcedure

Procedure WriteShortT(file, val$)
    WriteWord(file, ValDefaultShort(text$))
EndProcedure

Procedure writeSignature(datafile.i)
    WriteLong(datafile, $54545454)
EndProcedure

Procedure writeVersion(datafile.i, maj.a, min.a, rev.a)
    WriteAsciiCharacter(datafile, maj)
    WriteAsciiCharacter(datafile, min)
    WriteAsciiCharacter(datafile, rev)
EndProcedure

Procedure.s getDescriptorLine(file.i, *lineN.Long)
    Define line.s
    Repeat
        If Eof(file)
            ProcedureReturn ""
        EndIf
        line = StringField(ReadString(file), 1, "#")
        *lineN\l + 1
    Until Not line = ""
    ProcedureReturn line
EndProcedure

Procedure readFileList()
    Shared files()

    file.i = ReadFile(#PB_Any, "project_db.txt")

    If Not file
        error("Could Not find project DB.")
        End
    EndIf

    lineN.l = 0

    While Not Eof(file)
        AddElement(files())
        files()\path = getDescriptorLine(file, @lineN)
        files()\content = getDescriptorLine(file, @lineN)
    Wend
    CloseFile(file)
EndProcedure

loadedfile.LoadedFile
loadedFile\buffer = #Null
loadedFile\size = 0
Procedure readFileToMemory(path.s)
    Shared loadedFile
    file.i = ReadFile(#PB_Any, path)
    If Not file
        ProcedureReturn 0
    EndIf

    loadedFile\size = Lof(file)
    loadedFile\buffer = ReAllocateMemory(loadedFile\buffer, loadedFile\size)

    ReadData(file, loadedFile\buffer, loadedFile\size)
    CloseFile(file)
    ProcedureReturn loadedFile\size
EndProcedure

Procedure writeMemoryToFile(datafile.i)
    Shared loadedFile
    WriteData(datafile, loadedFile\buffer, loadedFile\size)
EndProcedure

Procedure writeFileType(datafile.i, type.b)
    WriteAsciiCharacter(datafile, type)
EndProcedure

Procedure writeAsciiString(datafile.i, tag.s)
    WriteString(datafile, tag, #PB_Ascii)
    WriteAsciiCharacter(datafile, $A)
EndProcedure

Procedure startsWithNumber(text.s)
    charCode.b = Asc(Left(text, 1))
    If Left(text, 1) = "-"
        charCode = Asc(Mid(text, 2, 1))
    Else
        charCode.b = Asc(Left(text, 1))
    EndIf
    If charCode < 48 Or charCode > 57
        ProcedureReturn 0
    Else
        ProcedureReturn 1
    EndIf
EndProcedure

Procedure startsWithNumberOrX(text.s)
    ProcedureReturn Bool(text = "x" Or startsWithNumber(text))
EndProcedure

Procedure isValidIdentifier(id.s)
    Shared identifierRegex
    ProcedureReturn MatchRegularExpression(identifierRegex, id)
EndProcedure

Macro errorLocationInfo(text)
    info + " (line " + lineN + ") : " + text
EndMacro

Macro hexLoc
    Hex(Loc(datafile))
EndMacro

Procedure checkIsEntity(isEntity.b, info.s, lineN.l, elementType.s)
    If (Not IsEntity)
        error(errorLocationInfo("Illegal element in background animation : " + elementType + " (should only be present in entity animations)"))
    EndIf

EndProcedure

Macro checkIsEntityM(elementType)
    checkIsEntity(isEntity, info, lineN, elementType)
EndMacro


Procedure writeAnimationDescriptor(datafile.i, info.s, isEntity.b)
    Define value.l, line.s, value$, valueD.d, frameNumber.l, lastModifiedFrame.b = -1, i.b

    lineN.l = 1

    printLog("---")
    printLog("Writing Animation descriptor at offset " + Hex(Loc(datafile)) + " (" + Loc(datafile) + ")")
    WriteAsciiCharacter(datafile, #FILEMARKER_DESCRIPTORSTART)

    If Right(info, 4) = ".dat"
        ;- The info string is supposedly a file name (opening it) ---------------------------------
        printLog("  Uses a descriptor file : " + info)
        descriptorfile.i = ReadFile(#PB_Any, info)
        If Not descriptorFile
            error("Could not open descriptor file " + info)
        EndIf

        ;- Reading frame number -------------------------------------------------------------------
        line = ReadString(descriptorFile)
        value = Val(line)
        If value < 1
            If line = ""
                error("Missing frame number in descriptor file")
            Else
                If Not startsWithNumber(line)
                    error("The descriptor file does not start with a frame number")
                Else
                    error("Null or nagative frame number")
                EndIf
            EndIf
        EndIf
        If value > 65535
            error("Frame number must be between 0 and 65535")
        EndIf
        printLog("  Frame number : " + value)
        writeUShort(datafile, value)
        frameNumber = value

        ;- Reading other lines --------------------------------------------------------------------

        While Not Eof(descriptorFile)
            line = getDescriptorLine(descriptorFile, @lineN)

            Select Left(line, 1)
                Case "s"
                    ;- - Animation speed line -----------------------------------------------------
                    value$ = Mid(line, 2)
                    valueD = ValD(value$)
                    If valueD <= 0
                        warning(errorLocationInfo(" : Null or negative speed, using 1 instead"))
                    Else
                        WriteAsciiCharacter(datafile, #FILEMARKER_ANIMSPEED)
                        WriteDouble(datafile, valueD)
                        Debug "Loc : " + Loc(datafile)
                        printLog("  Speed : " + StrD(valueD))
                    EndIf
                Case "f"
                    ;- - Frame info line ----------------------------------------------------------
                    value$ = Mid(line, 2)
                    If value$ = ""
                        warning(errorLocationInfo(" : No frame index specified after 'f' indicator : skipping line"))
                    EndIf
                    value = Val(value$)
                    If value < 0 Or value > frameNumber
                        error(errorLocationInfo(" : frame index must be between 0 and the frame number (" + frameNumber + ")"))
                    EndIf

                    WriteAsciiCharacter(datafile, #FILEMARKER_FRAMEINFO)
                    writeUShort(datafile, value)
                    printLog("  Info on frame " + value)
                    lastModifiedFrame = value
                    ;- - - Reading all values
                    i = 2
                    value$ = GMB_StringField(line, i, " ")
                    While Not value$ = ""
                        Select Left(value$, 1)
                            Case "d"
                                ;- - - Frame duration ---------------------------------------------
                                value = Val(Mid(value$, 2))
                                If value < 1
                                    error(errorLocationInfo("frame duration must be positive"))
                                EndIf

                                WriteAsciiCharacter(datafile, #FILEMARKER_FRAMEDURATION)
                                writeUShort(datafile, value)
                                printLog("    Frame duration : " + value)
                            Case "o"
                                ;- - - Origin coordinates -----------------------------------------
                                WriteAsciiCharacter(datafile, #FILEMARKER_FRAMEORIGIN)
                                value$ = Mid(value$, 2)
                                If value$ = ""
                                    error(errorLocationInfo("Missing origin X coordinate"))
                                EndIf
                                writeShort(datafile, Val(value$))
                                printLog("    Frame origin x : " + value$)
                                i + 1
                                value$ = GMB_StringField(line, i, " ")
                                If value$ = ""
                                    error(errorLocationInfo("Missing origin Y coordinate"))
                                EndIf
                                writeShort(datafile, Val(value$))
                                printlog("    Frame origin y : " + value$)
                            Case "m"
                                checkIsEntityM("Frame movement")
                                ;- - - Frame movement ---------------------------------------------
                                WriteAsciiCharacter(datafile, #FILEMARKER_FRAMEMOVEMENT)
                                value$ = Mid(value$, 2)
                                If value$ = ""
                                    error(errorLocationInfo("Missing frame movement mode"))
                                EndIf
                                value = Val(value$)
                                WriteByte(datafile, value)
                                PrintN("      Movement mode : " + Bin(value))
                                i + 1
                                value$ = GMB_StringField(line, i, " ")
                                If value$ = ""
                                    error(errorLocationInfo("Missing frame movement x speed"))
                                EndIf
                                valueD = ValD(value$)
                                WriteDouble(datafile, valueD)
                                PrintN("      Frame movement x speed : " + value$)
                                i + 1
                                value$ = GMB_StringField(line, i, " ")
                                If value$ = ""
                                    error(errorLocationInfo("Missing frame movement y speed"))
                                EndIf
                                valueD = ValD(value$)
                                WriteDouble(datafile, valueD)
                                PrintN("      Frame movement y speed : " + value$)
                        EndSelect
                        i + 1
                        value$ = GMB_StringField(line, i, " ")
                    Wend
                Case "c"
                    checkIsEntityM("Hurtbox")

                    ;- - Hurtbox line -------------------------------------------------------------

                    If line = "c all"
                        printLog(~"  Writing full-frame hurtbowes for each frame (\"c all\" found)")
                        For i = 0 To frameNumber - 1
                            printLog(~"    Writing full-frame hurtox on frame " + Str(i))
                            WriteAsciiCharacter(datafile, #FILEMARKER_FRAMEINFO)
                            writeUShort(datafile, i)
                            WriteAsciiCharacter(datafile, #FILEMARKER_HURTBOXINFO)
                            WriteUnicodeCharacter(datafile, #MAX_VALUE_SHORT)
                        Next
                        Continue
                    EndIf

                    value$ = GMB_StringField(line, 1, " ")

                    ;- - Checking for a frame index
                    If Len(value$) > 1
                        value$ = Mid(value$, 2)
                        If Not startsWithNumber(value$)
                            warning(errorLocationInfo("non-numeric characters after 'c' indicator - can't be interpreted as frame index, ignoring"))
                        Else
                            value = Val(value$)
                            If value < 0 Or value >= frameNumber
                                error(errorLocationInfo("frame number must be between 0 and the number of frames"))
                            EndIf
                            If value <> lastModifiedFrame
                                WriteAsciiCharacter(datafile, #FILEMARKER_FRAMEINFO)
                                writeUShort(datafile, value)
                                printlog("    Hurtbox is on frame " + Str(value) + " instead of the last modified frame")
                            EndIf
                        EndIf
                    EndIf

                    WriteAsciiCharacter(datafile, #FILEMARKER_HURTBOXINFO)
                    printLog("  Writing hurtbox info")

                    value$ = GMB_StringField(line, 2, " ")
                    If value$ = "whole"
                        WriteWord(datafile, #MAX_VALUE_SHORT)
                        printLog("    This hurtbox will cover all the frame and use the default type.")
                        Continue
                    EndIf

                    If verbose And Not startsWithNumber(value$)
                        warning(errorLocationInfo("One of the values is not a number - using 0"))
                    EndIf
                    WriteWord(datafile, Val(value$))
                    printLog("    " + *debugValues\hurtboxValues[2] + " : " + value$)

                    ;- - - Reading coordinates
                    For i = 3 To 5
                        value$ = GMB_StringField(line, i, " ")
                        If value$ = ""
                            error(errorLocationInfo("missing value."))
                        EndIf
                        If verbose And Not startsWithNumber(value$)
                            warning(errorLocationInfo("One of the values is not a number - using 0"))
                        EndIf
                        WriteWord(datafile, Val(value$))
                        printLog("    " + *debugValues\hurtboxValues[i - 2] + " : " + value$)
                    Next

                    ;- - - Optional hurtbox type
                    value = Val(GMB_StringField(line, 6, " "))
                    If value < 0 Or value > 2
                        error(errorLocationInfo("Invalid hurtbox type : should be between 0 and 2."))
                    EndIf
                    WriteAsciiCharacter(datafile, Val(value$))
                    printLog("    Hurtbox type : " + *debugValues\hurtboxTypes[value])

                    ;WriteAsciiCharacter(datafile, #FILEMARKER_GENERICSEPARATOR)
                    ;printLog("    Writing end marker")

                Case "h"
                    checkIsEntityM("Hitbox")

                    ;- - Hitbox line -------------------------------------------------------------

                    value$ = GMB_StringField(line, 1, " ")
                    ;- - Checking for a frame index
                    If Len(value$) > 1
                        value$ = Mid(value$, 2)
                        If Not startsWithNumber(value$)
                            warning(errorLocationInfo("non-numeric characters after 'c' indicator - can't be interpreted as frame index, ignoring"))
                            If value < 0 Or value >= frameNumber
                                error(errorLocationInfo("frame number must be between 0 and the number of frames"))
                            EndIf
                        Else
                            value = Val(value$)
                            If value <> lastModifiedFrame
                                WriteAsciiCharacter(datafile, #FILEMARKER_FRAMEINFO)
                                writeUShort(datafile, value)
                                printlog("    Hitbox is on frame " + Str(value) + " instead of the last modified frame")
                            EndIf
                        EndIf
                    EndIf

                    WriteAsciiCharacter(datafile, #FILEMARKER_HITBOXINFO)
                    printLog("  Writing hitbox info")


                    ;- - - Reading coordinates
                    For i = 2 To 5
                        value$ = GMB_StringField(line, i, " ")
                        If value$ = ""
                            error(errorLocationInfo("missing value."))
                        EndIf

                        If verbose And Not startsWithNumber(value$)
                            warning(errorLocationInfo("One of the values is not a number - using 0"))
                        EndIf
                        WriteWord(datafile, Val(value$))
                        printLog("    " + *debugValues\hitboxValues[i - 2] + " : " + value$)
                    Next

                    value = Val(GMB_StringField(line, 6, " "))
                    WriteByte(datafile, value)
                    If value < 0 Or value > 2
                        error(errorLocationInfo("Invalid hitbox type : should be between 0 and 2"))
                    EndIf
                    printLog("    Hitbox type : " + *debugValues\hitboxTypes[value])

                    Select value
                        Case #HITBOXTYPE_DAMAGE
                            value$ = GMB_StringField(line, 7, " ")
                            WriteDouble(datafile, ValD(value$))
                            printLog("    Damage : " + value$)

                            value$ = GMB_StringField(line, 8, " ")
                            WriteUnicodeCharacter(datafile, Val(value$))
                            printLog("    Angle : " + value$)

                            value$ = GMB_StringField(line, 9, " ")
                            WriteDouble(datafile, ValD(value$))
                            printLog("    Base knockback : " + value$)

                            value$ = GMB_StringField(line, 10, " ")
                            WriteDouble(datafile, ValD(value$))
                            printLog("    Scaling knockback : " + value$)

                            value$ = GMB_StringField(line, 11, " ")
                            WriteAsciiCharacter(datafile, Val(value$))
                            printLog("    Hit ID : " + value$)

                            value$ = GMB_StringField(line, 12, " ")
                            WriteAsciiCharacter(datafile, Val(value$))
                            printLog("    Priority : " + value$)

                        Case #HITBOXTYPE_GRAB
                            error(errorLocationInfo("Hitbox type is not supported yet"))
                        Case #HITBOXTYPE_SPECIAL
                            error(errorLocationInfo("Hitbox type is not supported yet"))
                        Default
                            error(errorLocationInfo("Invalid hitbox type : " + value))
                    EndSelect
                Case "e"
                    checkIsEntityM("End behavior")

                    printLog("  Writing ending behavior info")
                    WriteAsciiCharacter(datafile, #FILEMARKER_ANIM_END_INFO)

                    value$ = GMB_StringField(line, 2, " ")
                    Select value$
                        Case ""
                            printLog("    No value specified, assuming helpless")
                            WriteAsciiCharacter(datafile, #ANIM_END_HELPLESS)

                        Case "helpless"
                            printLog("    Mode : helpless")
                            WriteAsciiCharacter(datafile, #ANIM_END_HELPLESS)
                        Case "custom"
                            error(errorLocationInfo("Custom end behavior is not supported yet"))
                        Default
                            error(errorLocationInfo("Invalid end behavior mode"))
                    EndSelect


                Case "l"
                    ;- - Landing info line -------------------------------------------------------------

                    checkIsEntityM("Landing")


                    printLog("  Writing landing info")
                    WriteAsciiCharacter(datafile, #FILEMARKER_LANDINGINFO)

                    value$ = GMB_StringField(line, 2, " ")
                    If value$ = ""
                        error(errorLocationInfo(" : No frame index specified at the start of landing behavior descriptor"))
                    EndIf
                    Debug value$
                    value = Val(value$)
                    If value < 0 Or value > frameNumber
                        error(errorLocationInfo(" : frame index must be between 0 and the frame number (" + frameNumber + ")"))
                    EndIf

                    printLog("    Starting frame : " + value)
                    writeUShort(datafile, value)

                    value$ = GMB_StringField(line, 3, " ")
                    Debug value$
                    Select value$
                        Case "n"
                            printLog("    Type : Nothing")
                            WriteAsciiCharacter(datafile, #FILEMARKER_LANDING_NOTHING)
                        Case "l"
                            printLog("    Type : Normal")
                            WriteAsciiCharacter(datafile, #FILEMARKER_LANDING_NORMAL)
                            value$ = GMB_StringField(line, 4, " ")
                            If value$
                                value = Val(value$)
                                printLog("    Duration : " + value)
                                writeShort(datafile, value)
                            Else
                                printLog("    Duration : animation default (-1)")
                                writeShort(datafile, -1)
                            EndIf
                        Case "a"
                            printLog("    Type : Animation")
                            WriteAsciiCharacter(datafile, #FILEMARKER_LANDING_ANIMATION)
                            value$ = GMB_StringField(line, 5, " ")
                            If value$
                                value = Val(value$)
                                printLog("    Duration : " + value)
                                writeShort(datafile, value)
                            Else
                                printLog("    Duration : animation default (-1)")
                                writeShort(datafile, -1)
                            EndIf

                            value$ = GMB_StringField(line, 4, " ")

                            If value$ = ""
                                error(errorLocationInfo(" : No animation name"))
                            ElseIf Not isValidIdentifier(value$)
                                error(errorLocationInfo(" : " + value$ + " is not a valid animation name"))
                            EndIf

                            printLog("    Animation name : " + value$)
                            writeAsciiString(datafile, value$)

                        Case ""
                            error(errorLocationInfo(" : No landing behavior type specified in landing behavior descriptor"))
                        Default
                            error(errorLocationInfo(" : Invalid behavior type indicator, should be n, l or a was " + value$))
                    EndSelect
                Case "#"
                    ;it's a comment
                Default
                    warning(errorLocationInfo("Start of the line doesn't match with any information type identifier (s, f, c or h) : " + Left(line, 1)))
            EndSelect
        Wend

        CloseFile(descriptorFile)

    Else
        ;- The info string is supposedly the values directly
        value$ = GMB_StringField(info, 1, " ")
        If value$ = ""
            error("Missing frame number")
        EndIf

        frameNumber = Val(value$)
        If frameNumber < 1
            error("Null or negative frame number")
        EndIf
        writeUShort(datafile, frameNumber)
        printLog("  Frames number : " + value$)

        value$ = GMB_StringField(info, 2, " ")
        valueD = ValD(value$)
        If valueD
            WriteAsciiCharacter(datafile, #FILEMARKER_ANIMSPEED)
            WriteDouble(datafile, valueD)
            printLog("  Speed : " + value$)
        EndIf

        value$ = GMB_StringField(info, 3, " ")
        If value$ = "c"
            printLog(~"  Writing full-frame hurtboxes for each frame (\"c all\" found)")
            For i = 0 To frameNumber - 1
                printLog(~"    Writing full-frame hurtox (0x7FFF) on frame " + Str(i))
                WriteAsciiCharacter(datafile, #FILEMARKER_FRAMEINFO)
                writeUShort(datafile, i)
                WriteAsciiCharacter(datafile, #FILEMARKER_HURTBOXINFO)
                WriteWord(datafile, #MAX_VALUE_SHORT)
            Next
        EndIf
    EndIf
EndProcedure

UndefineMacro errorLocationInfo
Macro errorLocationInfo(text)
    sourceFileName + " (line " + lineN + ") : " + text
EndMacro

;assumes existence of lineN, datafile, sourceFile, line and value$
Macro writeGameplayValues(valuesType, debugNames, valuesNB)

    If Eof(sourceFile)
        Goto values_loop_end_#debugnames
    EndIf

    line = getDescriptorLine(sourceFile, @lineN)

    valuesRead.b = 0

    While startsWithNumber(line)
        For i = 1 To GMB_CountFields(line, " ")
            If valuesRead >= valuesNB
                warning("Too many values - Ignoring the last ones")
                Goto values_loop_end_#debugnames:
            EndIf
            value$ = GMB_StringField(line, i, " ")

            If value$ = "x"
                Select valuesType(valuesRead)
                    Case #TYPE_BYTE
                        writeMaxValue(datafile, Byte)
                    Case #TYPE_DOUBLE
                        WriteDouble(datafile, 0.0)
                    Case #TYPE_SHORT
                        writeMaxValue(datafile, Short)
                EndSelect
            Else
                Select valuesType(valuesRead)
                    Case #TYPE_BYTE
                        WriteByte(datafile, Val(value$))
                    Case #TYPE_DOUBLE
                        WriteDouble(datafile, ValD(value$))
                    Case #TYPE_SHORT
                        writeShort(datafile, Val(value$))
                EndSelect
            EndIf

            printLog("  - " + Str(valuesRead) + " : " + *debugValues\debugNames[valuesRead] + " : " + value$ + " (" + *debugValues\championValueTypes[valuesType(valuesRead)] + ")")
            valuesRead + 1

        Next
        If Eof(sourceFile)
            Break
        EndIf
        line = getDescriptorLine(sourceFile, @lineN)
    Wend
    values_loop_end_#debugnames:

    If valuesRead < valuesNB
        Debug valuesRead
        error("Missing values")
    EndIf
EndMacro

Procedure writeChampionFile(datafile.i, sourceFileName.s)
    Define value.l, line.s, value$, valueD.d, i.b
    Shared championValues()

    printLog("---")
    printLog("Writing Champion descriptor file at offset " + hexLoc)

    sourcefile.i = OpenFile(#PB_Any, sourceFileName)
    If Not sourceFile
        error("Could not open the source file (" + sourceFileName + ")")
    EndIf

    lineN.l = 1

    line = getDescriptorLine(sourceFile, @lineN)
    printLog("  Writing Display Name : " + line)
    WriteString(datafile, line, #PB_UTF8)
    WriteAsciiCharacter(datafile, $A) ;adding line end

    printLog("  Writing Champion values")

    writeGameplayValues(championValues, championValues, #CHAMPION_VALUES_NB)

    While Not line = ""
        Select Left(line, 1)
                ;             Case "m"
                ;                 value$ = GMB_StringField(line, 2, " ")
                ;                 If value$ = ""
                ;                     error("Move names cannot be empty")
                ;                 EndIf
                ;                 WriteAsciiCharacter(datafile, #FILEMARKER_MOVEINFO)
                ;                 writeAsciiString(datafile, value$)
                ;                 printLog("- Writing move info : " + value$)
                ;
                ;                 ;- - - Reading all values
                ;                 For i = 3 To GMB_CountFields(line, " ")
                ;                     value$ = GMB_StringField(line, i, " ")
                ;                     Select value$
                ;                         Case "l" ; landing lag
                ;                             i + 1
                ;                             value$ = GMB_StringField(line, i, " ")
                ;                             WriteAsciiCharacter(datafile, #FILEMARKER_LANDINGLAG)
                ;                             WriteAsciiCharacter(datafile, Val(value$))
                ;                             printLog("  - Landing lag : " + value$)
                ;                     EndSelect
                ;
                ;                 Next
        EndSelect
        line = getDescriptorLine(sourceFile, @lineN)
    Wend


    CloseFile(sourceFile)

EndProcedure

Procedure writeStageFile(datafile.i, sourceFileName.s)
    Shared stageValues()
    Define value.l, line.s, value$, valueD.d, i.b

    printLog("---")
    printLog("Writing Stage descriptor file at offset " + hexloc)

    sourcefile.i = OpenFile(#PB_Any, sourceFileName)
    If Not sourceFile
        error("Could not open the source file (" + sourceFileName + ")")
    EndIf

    lineN.l = 1

    line = getDescriptorLine(sourceFile, @lineN)
    printLog("  Writing Display Name : " + line)
    WriteString(datafile, line, #PB_UTF8)
    WriteAsciiCharacter(datafile, $A) ;adding line end

    printLog("  Writing Stage values")

    writeGameplayValues(stageValues, stageValues, #STAGE_VALUES_NB)

    While Not line = ""
        Select Left(line, 1)
            Case "p" ;platform
                WriteAsciiCharacter(datafile, #FILEMARKER_PLATFORMINFO)
                printLog("  Writing platform info")

                ;- - - Reading coordinates
                For i = 2 To 4
                    value$ = GMB_StringField(line, i, " ")
                    If value$ = ""
                        error(errorLocationInfo("missing value."))
                    EndIf

                    If verbose And Not startsWithNumberOrX(value$)
                        warning(errorLocationInfo("One of the values is not a number - using 0"))
                    EndIf
                    value = ValDefaultShort(value$)
                    writeShort(datafile, value)
                    printLog("    " + *debugValues\stagePlatformValues[i - 2] + " : " + value)
                Next

                value$ = GMB_StringField(line, i, " ")
                If value$ <> ""
                    WriteAsciiCharacter(datafile, #FILEMARKER_PLATFORMANIMATION)
                    printLog("  - Animation name : " + value$)
                    If Not isValidIdentifier(value$)
                        error(errorLocationInfo("Invalid animation name : " + value$))
                    EndIf
                    writeAsciiString(datafile, value$)
                EndIf

            Case "b" ;background element
                WriteAsciiCharacter(datafile, #FILEMARKER_BACKGROUNDELEMENT)
                printLog("  Writing background element info")

                value$ = GMB_StringField(line, 2, " ")
                If value$ = ""
                    error(errorLocationInfo("Background element without animation name"))
                ElseIf Not isValidIdentifier(value$)
                    error(errorLocationInfo("Invalid animation name : " + value$))
                EndIf

                writeAsciiString(datafile, value$)
                printLog("  - " + *debugValues\stageBackgroundValues[0] + " : " + value$)

                If GMB_CountFields(line, " ") < 3
                    printLog("    static background : writing MAX_SHORT")
                    writeShort(datafile, #MAX_VALUE_SHORT)
                Else
                    ;- - - Reading coordinates
                    For i = 3 To 4
                        value$ = GMB_StringField(line, i, " ")
                        If value$ = ""
                            error(errorLocationInfo("missing value."))
                        EndIf
                        If verbose And Not startsWithNumber(value$)
                            warning(errorLocationInfo("One of the values is not a number - using 0"))
                        EndIf
                        WriteWord(datafile, Val(value$))
                        printLog("  - " + *debugValues\stageBackgroundValues[i - 1] + " : " + value$)
                    Next

                    value$ = GMB_StringField(line, i, " ")
                    If value$ = "" Or value$ = "x"
                        printLog("  - Profondeur : 1 (défaut)")
                        WriteDouble(datafile, 1)
                    Else
                        printLog("  - Profondeur : " + value$)
                        WriteDouble(datafile, ValD(value$))
                    EndIf
                EndIf



        EndSelect
        line = getDescriptorLine(sourceFile, @lineN)
    Wend
EndProcedure

Procedure checkBasicTag(tag.s)
    Shared basicTagRegex
    ProcedureReturn checkString(basicTagRegex, tag)
EndProcedure

Procedure.s parseAnimationTag(datafile.i, tag.s)
    If GMB_CountFields(tag, "/") < 3
        Select Left(tag, 1)
            Case "$"
                WriteAsciiCharacter(datafile, #ANIMATION_POOL_STAGE)
                ProcedureReturn Right(tag, Len(tag) - 1)
            Default
                WriteAsciiCharacter(datafile, #ANIMATION_POOL_CHAMPION)
                ProcedureReturn tag
        EndSelect

    EndIf

    prefix.s = GMB_StringField(tag, 1, "/")

    Select prefix
        Case "Stage", "S"
            Debug "STage!!!"
            WriteAsciiCharacter(datafile, #ANIMATION_POOL_STAGE)
        Case "Champion", "C", "Champ"
            WriteAsciiCharacter(datafile, #ANIMATION_POOL_CHAMPION)
        Default
            error("Invalid animation tag prefix : " + prefix + " (" + tag + ")")
    EndSelect

    ProcedureReturn GMB_SepRight(tag, "/", 2)
EndProcedure

Procedure addFile(datafile.i, *inputFile.File)
    Define type.b

    printLog("===================")
    printLogForce("Filename : " + *inputFile\path)
    printLog("Content  : " + *inputFile\content)

    Select GMB_StringField(*inputFile\content, 1, ":")
        Case "A"
            type = #FILETYPE_ANIMATION
        Case "AL"
            type = #FILETYPE_LEFTANIM
        Case "C"
            type = #FILETYPE_CHAMPION
        Case "S"
            type = #FILETYPE_STAGE
        Case "I"
            type = #FILETYPE_IMAGE
        Case "X"
            type = #FILETYPE_FILE
        Default
            error(*inputFile\content + " : unknown file type")
    EndSelect

    ;Debug *inputFile\content

    Define tag.s, info.s
    tag = GMB_StringField(*inputFile\content, 2, ":")
    info = GMB_SepRight(tag, " ", 2)
    tag = GMB_StringField(tag, 1, " ")

    printLog("---")
    printLog("Writing at offset " + Hex(Loc(datafile)) + " (" + Loc(datafile) + ")")

    writeFileType(datafile, type)
    printLog("Type : " + *debugValues\fileTypeNames[type])


    printLog("Tag : " + tag)
    If type = #FILETYPE_ANIMATION
        tag = parseAnimationTag(datafile, tag)
        printLog("Modified tag : " + tag)
    EndIf
    writeAsciiString(datafile, tag)

    If Not checkBasicTag(tag)
        error("Invalid tag : " + tag)
    EndIf

    If type = #FILETYPE_ANIMATION Or type = #FILETYPE_LEFTANIM Or type = #FILETYPE_IMAGE Or type = #FILETYPE_FILE
        ;these files are data that isn't going to be parsed (image, sound)
        size.l = readFileToMemory(*inputFile\path)
        If Not size
            error("Could not load file " + *inputFile\path)
            End
        EndIf
        WriteLong(datafile, size)
        printLog("File size : " + size)
        before.l = Loc(datafile)
        writeMemoryToFile(datafile)

    EndIf

    Select type
        Case #FILETYPE_CHAMPION
            writeChampionFile(datafile, *inputFile\path)
        Case #FILETYPE_ANIMATION
            writeAnimationDescriptor(datafile, info, 1)
        Case #FILETYPE_BANIMATION
            writeAnimationDescriptor(datafile, info, 0)
        Case #FILETYPE_STAGE
            writeStageFile(datafile, *inputFile\path)
    EndSelect

    WriteByte(datafile, #FILEMARKER_INTERFILE)
EndProcedure

Procedure help()
    OpenConsole()
    PrintN(~"Usage : DFM.exe [-v] [-l] [-o <outputFile>] [-h] [<inputDir>]\n\t-l : Logs info to the terminal\n\t-v : Logs more info to the terminal\n\t-o <outputFile>: Filename of the results .twl file\n\t-h : Prints this help text")
EndProcedure

buildpath.s = ""
source.s = ""

Define parameter.s

While 1
    parameter = ProgramParameter()
    If parameter = ""
        Break
    EndIf
    Select parameter
        Case "-v"
            verbose = #True
            logging = #True
        Case "-l"
            logging = #True
        Case "-o"
            buildpath = ProgramParameter()
        Case "-h"
            help()
            End
        Default
            Debug parameter
            source = parameter
    EndSelect
Wend

If Right(source, 1) <> "/" And Len(source) > 0
    source + "/"
EndIf

If buildpath = ""
    buildpath = "data.twl"
EndIf


SetCurrentDirectory(source)

If logging
    *debugValues = initDebugValues()
Else
    enableMessageBox = #True
EndIf

If logging
    OpenConsole()
EndIf

If Not CreateFile(0, buildpath)
    PrintN(buildpath)
    error("Could not create the Data File.")
    End
EndIf

If logging
    PrintN("Building Kuribrawl Data File at" + buildpath + " from ressources at " + source + ".")
    PrintN("Data File Format version : " + #DFV_MAJ + "." + #DFV_MIN + "." + #DFV_REV)
    PrintN("Ressource Files Format version : " + #RFV_MAJ + "." + #RFV_MIN + "." + #RFV_REV)
EndIf

writeSignature(0)
writeVersion(0, #DFV_MAJ, #DFV_MIN, #DFV_REV)
readFileList()

ForEach files()
    addFile(0, @files())
Next

size.l = Loc(0)
CloseFile(0)

If logging
    PrintN("===============================")
    PrintN("FINISHED. File size : " + size)

    If #PB_Compiler_Debugger
        Input()
    EndIf
EndIf

; IDE Options = PureBasic 6.12 LTS (Windows - x64)
; ExecutableFormat = Console
; CursorPosition = 116
; FirstLine = 112
; Folding = ------
; EnableXP
; Executable = ..\..\..\res\DFM.exe
; CommandLine = -v ..\..\..\res