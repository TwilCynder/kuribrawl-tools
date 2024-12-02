#! /bin/bash -x

echo "Compiling ..."
mkdir -p classes
javac -d ./classes --source-path "./src/" -cp "./lib/*" ./src/main/Main.java
echo Main-Class: main.Main>manifest.mf
echo Class-Path: ./lib/*>>manifest.mf