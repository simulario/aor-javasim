@echo off
rem This is the start script of the AOR Simulator GUI for Windows systems.
rem
rem You need to set the path to your Java JDK with the JAVA_JDK environment
rem variable. Be aware to use the old 8.3 DOS format for folders. The folder
rem name 'Program Files' may have then the name 'Progra~1'. You can check this
rem with:
rem 
rem   dir c:\ /X
rem
rem If you are not sure about the right path go to the Java JDK directory 
rem just have a look into your 'Java' folder.
rem
rem   cd \Program Files\Java
rem   dir
rem  
rem Now you should see your Java installation directory. If not download 
rem the Java 6 JDK and install it with administrator rights.

rem set JAVA_JDK=c:/Progra~1/Java/jdk1.6.0_10

set JAVA_JDK=


IF "%JAVA_JDK%" == "" (
	ECHO.
	ECHO "The path to your Java Developer Kit (JDK) is not set. Please modify the AOR-Simulator.bat batch file. "
	ECHO.
	PAUSE
)

IF NOT "%JAVA_JDK%" == "" %JAVA_JDK%/bin/javaw -jar AOR-Simulator.jar

