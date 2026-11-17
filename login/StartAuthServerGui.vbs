' Inicia o Login Server so com a janela propria (sem a janela preta do console).
' O console fica oculto; o laco de reinicio continua no AuthServerGui.bat.
Set shell = CreateObject("WScript.Shell")
Set fso = CreateObject("Scripting.FileSystemObject")
dir = fso.GetParentFolderName(WScript.ScriptFullName)
shell.CurrentDirectory = dir
shell.Run "cmd /c """ & dir & "\AuthServerGui.bat""", 0, False
