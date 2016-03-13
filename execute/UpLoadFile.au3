WinWaitActive($CmdLine[1], "", 10)
$uploadPanel = WinGetHandle($CmdLine[1])
Sleep(100)
WinActivate($uploadPanel)
if IsHWnd($uploadPanel) Then
   Sleep(750)
   Send($CmdLine[2])
   Sleep(100)
   Send("{ENTER}")
EndIf