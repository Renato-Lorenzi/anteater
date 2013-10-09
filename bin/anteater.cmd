@echo off
IF "%JAVA_HOME%" == "" (
	echo "Error: JAVA_HOME var not found."
	Exit /B 1
)


IF "%ANTEATER_HOME%" == "" (
	echo "Error: ANTEATER_HOME var not found."
	Exit /B 1
)

set java_error=1
set ANT_LIB=%ANTEATER_HOME%\lib
set ACTUAL_PATH=%CD%

setlocal EnableDelayedExpansion
cd %ANT_LIB%
for /r %%i in (*.jar) do set ANTLIBPATH=!ANTLIBPATH!;"%%i"
cd %ACTUAL_PATH%
set LOCALCLASSPATH=%ANTLIBPATH%

if exist "%JAVA_HOME%\lib\tools.jar" (set LOCALCLASSPATH="%JAVA_HOME%\lib\tools.jar"%LOCALCLASSPATH%)

"%JAVA_HOME%/bin/java" -Dant.home="%ANTEATER_HOME%" -Dant.library.dir="%ANT_LIB%" -cp %LOCALCLASSPATH% br.com.anteater.main.Main %1 %2 %3 %4 %5
Exit /B %errorlevel%
