;TODO Modifs in the file format
;  Change the $5x filemarkers to a more meaningful value
;  Animation speed : float >double (DONE)

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
    #FILETYPES
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

#MAX_VALUE_BYTE  = 255
#MAX_VALUE_SHORT = 65535
#MAX_VALUE_USHORT = 32767

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
;Champions
#FILEMARKER_MOVEINFO = 2
#FILEMARKER_LANDINGLAG = $20
#FILEMARKER_MULTIMOVE = 3
#FILEMARKER_MULTIMOVEEND = $30
;Stage
#FILEMARKER_PLATFORMINFO = 1

Enumeration
    #TYPE_BYTE
    #TYPE_DOUBLE
EndEnumeration

#CHAMPION_VALUES_NB = 31
Dim championValues.b(#CHAMPION_VALUES_NB)
XIncludeFile "dataFileMarkerData.pbi"

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
        printLogForce(Chr(27) + "WARNING : " + text)
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

Procedure writeSignature(datafile.l)
    WriteLong(datafile, $54545454)
EndProcedure

Procedure writeVersion(datafile.l, maj.a, min.a, rev.a)
    WriteAsciiCharacter(datafile, maj)
    WriteAsciiCharacter(datafile, min)
    WriteAsciiCharacter(datafile, rev)
EndProcedure  

Procedure readFileList()
    Shared files()
    
    file.l = ReadFile(#PB_Any, "project_db.txt")
    If Not file
        error("Could Not file project DB.")
        End
    EndIf
    
    While Not Eof(file)
        AddElement(files())
        files()\path = ReadString(file)
        files()\content = ReadString(file)
    Wend
    CloseFile(file)
EndProcedure

loadedFile.LoadedFile
loadedFile\buffer = #Null
loadedFile\size = 0
Procedure readFileToMemory(path.s)
    Shared loadedFile
    file.l = ReadFile(#PB_Any, path)
    If Not file
        ProcedureReturn 0
    EndIf
    
    loadedFile\size = Lof(file)
    loadedFile\buffer = ReAllocateMemory(loadedFile\buffer, loadedFile\size)
    
    ReadData(file, loadedFile\buffer, loadedFile\size)
    CloseFile(file)
    ProcedureReturn loadedFile\size
EndProcedure

Procedure writeMemoryToFile(datafile.l)
    Shared loadedFile
    WriteData(datafile, loadedFile\buffer, loadedFile\size)
EndProcedure

Procedure writeFileType(datafile.l, type.b)
    WriteAsciiCharacter(datafile, type)
EndProcedure

Procedure writeAsciiString(datafile.l, tag.s)
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

Macro errorLocationInfo(text)
    info + " (line " + lineN + ") : " + text
EndMacro

Procedure.s getDescriptorLine(file.l, *lineN.Long)
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

Procedure writeAnimationDescriptor(datafile.l, info.s)
    Define value.l, line.s, value$, valueD.d, frameNumber.a, lastModifiedFrame.b = -1, i.b
    
    lineN.l = 1
    
    printLog("---")
    printLog("Writing Animation descriptor at offset " + Hex(Loc(datafile)) + " (" + Loc(datafile) + ")")
    WriteAsciiCharacter(datafile, #FILEMARKER_DESCRIPTORSTART)
    
    If Right(info, 4) = ".dat"
        ;- The info string is supposedly a file name (opening it) ---------------------------------
        printLog("  Uses a descriptor file : " + info)
        descriptorFile.l = ReadFile(#PB_Any, info)
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
        If value > 255
            error("Frame number must be between 0 and 255")
        EndIf 
        printLog("  Frame number : " + value)
        WriteAsciiCharacter(datafile, value)
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
                        warning(errorLocationInfo(" : Null or negative speed   using 1 instead"))
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
                    WriteAsciiCharacter(datafile, value)
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
                                WriteUnicodeCharacter(datafile, value)
                                printLog("    Frame duration : " + value)
                            Case "o"
                                ;- - - Origin coordinates -----------------------------------------
                                WriteAsciiCharacter(datafile, #FILEMARKER_FRAMEORIGIN)
                                value$ = Mid(value$, 2)
                                If value$ = ""
                                    error(errorLocationInfo("Missing origin X coordinate"))
                                EndIf 
                                WriteLong(datafile, Val(value$))
                                printLog("    Frame origin x : " + value$)
                                i + 1
                                value$ = GMB_StringField(line, i, " ")
                                If value$ = ""
                                    error(errorLocationInfo("Missing origin Y coordinate"))
                                EndIf 
                                WriteLong(datafile, Val(value$))
                                printlog("    Frame origin y : " + value$)
                            Case "m"
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
                    ;- - Hurtbox line -------------------------------------------------------------
                    
                    If line = "c all"
                        printLog(~"  Writing full-frame hurtbowes for each frame (\"c all\" found)")
                        For i = 0 To frameNumber - 1
                            printLog(~"    Writing full-frame hurtox on frame " + Str(i))
                            WriteAsciiCharacter(datafile, #FILEMARKER_FRAMEINFO)
                            WriteAsciiCharacter(datafile, i)
                            WriteAsciiCharacter(datafile, #FILEMARKER_HURTBOXINFO)
                            WriteUnicodeCharacter(datafile, #MAX_VALUE_USHORT)
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
                                WriteAsciiCharacter(datafile, value)
                                printlog("    Hurtbox is on frame " + Str(value) + " instead of the last modified frame")
                            EndIf 
                        EndIf 
                    EndIf 
                    
                    WriteAsciiCharacter(datafile, #FILEMARKER_HURTBOXINFO)
                    printLog("  Writing hurtbox info")
                    
                    value$ = GMB_StringField(line, 2, " ")
                    If value$ = ""
                        WriteWord(datafile, #MAX_VALUE_USHORT)
                        printLog("    No value ; this hurtbox will cover all the frame and use the default type.")
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
                        error(errorLocationInfo("Invalid hurtbox type : should be between 0 and 2. Using 0 instead."))
                    EndIf 
                    WriteAsciiCharacter(datafile, Val(value$))
                    printLog("    Hurtbox type : " + *debugValues\hurtboxTypes[value])
                    
                    ;WriteAsciiCharacter(datafile, #FILEMARKER_GENERICSEPARATOR)
                    ;printLog("    Writing end marker")
                    
                Case "h"
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
                                WriteAsciiCharacter(datafile, value)
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
                    
                    ;WriteAsciiCharacter(datafile, #FILEMARKER_GENERICSEPARATOR)
                    ;printLog("    Writing end marker")
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
        WriteByte(datafile, frameNumber)
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
                WriteAsciiCharacter(datafile, i)
                WriteAsciiCharacter(datafile, #FILEMARKER_HURTBOXINFO)
                WriteWord(datafile, #MAX_VALUE_USHORT)
            Next
        EndIf    
    EndIf 
EndProcedure

UndefineMacro errorLocationInfo
Macro errorLocationInfo(text)
    sourceFileName + " (line " + lineN + ") : " + text
EndMacro
    

Procedure writeChampionFile(datafile.l, sourceFileName.s)
    Define value.l, line.s, value$, valueD.d, frameNumber.a, lastModifiedFrame.b = -1, i.b
    Shared championValues()
    
    printLog("---")
    printLog("Writing Champion descriptor file at offset " + Hex(Loc(datafile)))
    
    sourceFile.l = OpenFile(#PB_Any, sourceFileName)
    If Not sourceFile
        error("Could not open the source file (" + sourceFileName + ")")
    EndIf
    
    lineN.l = 1
    
    line = getDescriptorLine(sourceFile, @lineN)
    WriteString(datafile, line, #PB_UTF8)
    WriteAsciiCharacter(datafile, $A)
    
    valuesRead.b = 0
    
    printLog("  Writing Champion values")
    
    If Eof(sourceFile)
        Goto champion_values_loop_end
    EndIf 
    
    line = getDescriptorLine(sourceFile, @lineN)
    
    While startsWithNumber(line)
        For i = 1 To GMB_CountFields(line, " ")
            If valuesRead >= #CHAMPION_VALUES_NB
                warning("Too many champion values - Ignoring the last ones")
                Goto champion_values_loop_end
            EndIf 
            value$ = GMB_StringField(line, i, " ")
            If (championValues(valuesRead) = #TYPE_BYTE)
                WriteByte(datafile, Val(value$))
            Else
                WriteDouble(datafile, ValD(value$))
            EndIf 
            printLog("  -" + *debugValues\championValues[valuesRead] + " : " + value$ + " ("+ *debugValues\championValueTypes[championValues(valuesRead + 1)] +")")
            valuesRead + 1
            
        Next
        If Eof(sourceFile)
            Break
        EndIf 
        line = getDescriptorLine(sourceFile, @lineN)
    Wend
    champion_values_loop_end:
    
    If valuesRead < #CHAMPION_VALUES_NB
        Debug valuesRead
        error("Missing champion values")
    EndIf 
    
    While Not line = ""
        Select Left(line, 1)
            Case "m"
                value$ = GMB_StringField(line, 2, " ")
                If value$ = ""
                    error("Move names cannot be empty")
                EndIf 
                WriteAsciiCharacter(datafile, #FILEMARKER_MOVEINFO)
                writeAsciiString(datafile, value$)
                printLog("- Writing move info : " + value$)
                
                ;- - - Reading all values
                For i = 3 To GMB_CountFields(line, " ")
                    value$ = GMB_StringField(line, i, " ")
                    Select value$
                        Case "l" ; landing lag
                            i + 1
                            value$ = GMB_StringField(line, i, " ")
                            WriteAsciiCharacter(datafile, #FILEMARKER_LANDINGLAG)
                            WriteAsciiCharacter(datafile, Val(value$))
                            printLog("  - Landing lag : " + value$)
                    EndSelect    
                            
                Next
        EndSelect
        line = getDescriptorLine(sourceFile, @lineN)
    Wend    
                
    
    CloseFile(sourceFile)
   
EndProcedure

Procedure addFile(datafile.l, *inputFile.File)
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
        Default
            error(*inputFile\content + " : unknown file type")
    EndSelect
    
    Debug *inputFile\content
    
    Define tag.s, info.s
    tag = GMB_StringField(*inputFile\content, 2, ":")
    info = GMB_SepRight(tag, " ", 2)
    tag  = GMB_StringField(tag, 1, " ")
    
    printLog("---")
    printLog("Writing at offset " + Hex(Loc(datafile)) + " (" + Loc(datafile) + ")")
    
    writeFileType(datafile, type)
    printLog("Type : " + *debugValues\fileTypeNames[type])
    writeAsciiString(datafile, tag)
    printLog("Tag : " + tag)
    
    If type = #FILETYPE_ANIMATION Or type = #FILETYPE_LEFTANIM Or type = #FILETYPE_IMAGE
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
        If type = #FILETYPE_ANIMATION
            writeAnimationDescriptor(datafile, info)
        EndIf
    Else
        ;these files are kuribrawl data, that will be parsed
        Select type
            Case #FILETYPE_CHAMPION
                writeChampionFile(datafile, *inputFile\path)
        EndSelect
        
    EndIf 
    WriteByte(datafile, #FILEMARKER_INTERFILE)
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
        Default
            source = parameter
    EndSelect
Wend        

If Right(source, 1) <> "/" And Len(source) > 0
    source + "/"
EndIf 

If buildpath = ""
    buildpath = source + "data.twl"
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
    Debug buildpath
    error("Could not create the Data File.")
    End
EndIf    
    
writeSignature(0)
writeVersion(0, 0, 3, 0)

readFileList()

ForEach files()
    addFile(0, @files())
Next 

size.l = Loc(0)
CloseFile(0)

If logging
    PrintN("===============================")
    PrintN("FINISHED. File size : " + size)
    Input()
EndIf 
; IDE Options = PureBasic 5.72 (Windows - x64)
; ExecutableFormat = Console
; CursorPosition = 620
; FirstLine = 588
; Folding = ----
; EnableXP
; Executable = dataFileMaker.exe
; CommandLine = -v