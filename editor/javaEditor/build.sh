#!/bin/bash

exec=kuribrawl-editor.jar

while getopts ":hsr" option; do
  case $option in
    h) echo "usage: $0 [-h][-s][-r]"; exit ;;
    s) standalone="true" ;;
    r) release="true" ;;
    ?) echo "error: option -$OPTARG is not implemented"; exit ;;
  esac
done

if [ ! -z $standalone ] || [ ! -z $release ]
then
    if [ -z $(which unzip 2> /dev/null) ]
    then
        >&2 echo "Release mode build requires either Ant or unzip"
        exit
    fi
    
    mkdir -p classes 2> /dev/null
	javac -d ./classes -cp "./src;./lib/*" ./src/main/Main.java
	echo Main-Class: main.Main>manifest.mf
	echo Class-Path: ./lib/*>>manifest.mf
	
	mkdir tmp
	(cd tmp; unzip -uo ../lib/\*.jar)
	rm -r tmp/META-INF
	jar cvfm kuribrawl-editor.jar manifest.mf -C classes . -C tmp .
	rm -r tmp


    if [ ! -z $release ]
    then
        mkdir -p "../../../release/tools/editor/"
        cp $exec "../../../release/tools/editor/$exec"
    fi

else
    mkdir -p classes
    javac -d ./classes -cp "./src;./lib/*" ./src/main/Main.java
    echo Main-Class: main.Main>manifest.mf
    echo Class-Path: ./lib/*>>manifest.mf
    jar cvfm kuribrawl-editor.jar manifest.mf -C classes .
fi