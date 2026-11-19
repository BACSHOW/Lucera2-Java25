' Inicia o GameServer so com a janela propria (sem a janela preta do console).
' O console fica oculto; o laco de reinicio continua no GameServerGui.bat.
Set shell = CreateObject("WScript.Shell")
Set fso = CreateObject("Scripting.FileSystemObject")
dir = fso.GetParentFolderName(WScript.ScriptFullName)
shell.CurrentDirectory = dir
shell.Run "cmd /c """ & dir & "\GameServerGui.bat""", 0, False