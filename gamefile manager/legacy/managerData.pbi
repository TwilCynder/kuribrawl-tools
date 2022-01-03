Enumeration ;commands
  #COMMAND_Jab
  #COMMAND_FTilt
  #COMMAND_UTilt
  #COMMAND_DTilt
  #COMMAND_FSmash
  #COMMAND_USmash
  #COMMAND_DSmash
  #COMMAND_DashAttack
  #COMMAND_NAir
  #COMMAND_FAir
  #COMMAND_BAir
  #COMMAND_UAir
  #COMMAND_DAir
  #COMMAND_ZAir
  #COMMAND_NSpecial
  #COMMAND_SSpecial
  #COMMAND_USpecial
  #COMMAND_DSpecial ;haha flip kick go brr
  #Command_Walk
  #Command_Dash
  #Command_Jump
  #COMMAND_Grab
  #COMMAND_Shield
  #COMMAND_Roll
  #COMMAND_SpotDodge
  #COMMAND_AirDodge
  #COMMAND_DirectionalAirDodge
  #COMMANDS
EndEnumeration

NewMap moveNames.b()
moveNames("jab") = #COMMAND_Jab
moveNames("ftilt") = #COMMAND_FTilt
moveNames("utilt") = #COMMAND_UTilt
moveNames("dtilt") = #COMMAND_DTilt
moveNames("fsmash") = #COMMAND_FSmash
moveNames("usmash") = #COMMAND_USmash
moveNames("dsmash") = #COMMAND_DSmash
moveNames("dashattack") = #Command_Dash
moveNames("nair") = #COMMAND_NAir
moveNames("fair") = #COMMAND_FAir 
moveNames("bair") = #COMMAND_BAir
moveNames("uair") = #COMMAND_UAir
moveNames("dair") = #COMMAND_DAir
moveNames("zair") = #COMMAND_ZAir
moveNames("nspecial") = #COMMAND_NSpecial
moveNames("sspecial") = #COMMAND_SSpecial
moveNames("dspecial" ) = #COMMAND_DSpecial

;NE SUPPORTE QUE BYTE ET DOUBLE ATM (si ajout de long ou float, mettre à jour la partie écriture de des champion values)
Dim championValues.b(#CHAMPION_VALUES_NB)
championValues(1) = #TYPE_DOUBLE
championValues(2) = #TYPE_DOUBLE 
championValues(3) = #TYPE_DOUBLE 
championValues(4) = #TYPE_DOUBLE 
championValues(5) = #TYPE_DOUBLE 
championValues(6) = #TYPE_DOUBLE 
championValues(7) = #TYPE_DOUBLE 
championValues(8) = #TYPE_DOUBLE
championValues(9) = #TYPE_DOUBLE
championValues(10) = #TYPE_DOUBLE
championValues(11) = #TYPE_DOUBLE
championValues(12) = #TYPE_DOUBLE
championValues(13) = #TYPE_DOUBLE
championValues(14) = #TYPE_DOUBLE
championValues(15) = #TYPE_BYTE
championValues(16) = #TYPE_BYTE
championValues(17) = #TYPE_BYTE
championValues(18) = #TYPE_BYTE
championValues(19) = #TYPE_BYTE
championValues(20) = #TYPE_BYTE
championValues(21) = #TYPE_BYTE
championValues(22) = #TYPE_BYTE
championValues(23) = #TYPE_BYTE
championValues(24) = #TYPE_BYTE

; IDE Options = PureBasic 5.72 (Windows - x64)
; CursorPosition = 50
; FirstLine = 23
; EnableXP