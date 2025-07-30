@echo off
:: encrypt password
call ./tools.bat --aes_generate ./key.aes 128

:: put it in the properties file
for /f "delims=" %%i in ('call tools.bat --aes_encrypt ./key.aes %1') do set enc=%%i
echo pwd=${CIPHER:%enc%}>> ../config/test.properties

:: run our java class:

:: compile file
:: javac -cp "../lib/*" ../WafraAmiJdbcClient.java -d ../out
:: setting environment variable to set the keyfile
SET property_f1.properties.secret.key.files=./key.aes
:: executing compiled java file with dependencies in lib
"C:\Program Files\ami\jre\bin\java" -Df1.license.mode=dev -cp "../lib/*;../out/"  WafraAmiJdbcClient %2
:: resetting the environment variable
set property_f1.properties.secret.key.files=