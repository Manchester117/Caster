Sleep(100)
$uploadPanel = WinGetHandle($CmdLine[1])
Sleep(100)
WinActivate($uploadPanel)
if IsHWnd($uploadPanel) Then
   Sleep(1000)
   Send($CmdLine[2])
   Sleep(200)
   Send("{ENTER}")
EndIf