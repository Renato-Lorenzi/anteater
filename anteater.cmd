
IF "%JAVA_HOME%" == "" (
	echo "Error: JAVA_HOME var not found."
	Exit /B 1
)


IF "%ANT_HOME%" == "" (
	echo "Error: ANT_HOME var not found."
	Exit /B 1
)


set ANT_LIB=%ANT_HOME%/lib

for /r %%i in (%ANT_LIB%/ant*.jar) do set ANTLIBPATH=%ANTLIBPATH%;%%i

set LOCALCLASSPATH=%ANTLIBPATH%;lib/js-14.jar;jar/anteater.jar

if exist %JAVA_HOME%/lib/tools.jar (set LOCALCLASSPATH=%JAVA_HOME%/lib/tools.jar;%LOCALCLASSPATH%)

java -Dant.home="%ANT_HOME%" -Dant.library.dir="%ANT_LIB%" -cp %LOCALCLASSPATH% br.com.anteater.main.Main %1 %2 %3 %4 %5


Exit /B 1
