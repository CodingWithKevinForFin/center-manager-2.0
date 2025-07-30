@echo off

:: run our java class:

:: compile file
javac -cp "../lib/*" ../WafraAmiJdbcClient.java -d ../out
:: setting environment variable to set the keyfile
SET property_f1.properties.secret.key.files=%1
:: executing compiled java file with dependencies in lib
java -Df1.license.mode=dev -cp "../lib/*;../out/" WafraAmiJdbcClient %2
:: resetting the environment variable
set property_f1.properties.secret.key.files=