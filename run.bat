@echo off

rem Delete all compiled classes in the current directory and subdirectories
del /s *.class 2>nul

rem Delete the compiled JAR file if it exists
if exist MyApp.jar del MyApp.jar

rem Compile the Java file with the required classpath
javac -cp .;mysql-connector-j-8.1.0.jar;json.jar;jackson-core-2.15.3.jar;jackson-databind-2.15.3.jar;jackson-annotations-2.15.3.jar -sourcepath . App.java

rem Create the jar file with the compiled classes
jar cvfe MyApp.jar App *.class

rem Run the App using the required classpath
java -cp .;mysql-connector-j-8.1.0.jar;json.jar;jackson-core-2.15.3.jar;jackson-databind-2.15.3.jar;jackson-annotations-2.15.3.jar;MyApp.jar App

pause
