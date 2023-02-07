EXEC = kuribrawl-editor.jar

all: release


dependant:
ifeq (, $(shell which ant 2> /dev/null))
	-@mkdir classes 2> /dev/null
	javac -d ./classes -cp "./src;./lib/*" ./src/testPackage/Test.java
	echo Main-Class: testPackage.Test>manifest.mf
	echo Class-Path: ./lib/*>>manifest.mf
	jar cvfm kuribrawl-editor.jar manifest.mf -C classes .
else
	ant dist-zip
endif 

release:
ifeq (, $(shell which ant 2> /dev/null))

ifeq (, $(shell which unzip 2> /dev/null))
	$(error Release mode build requires either Ant or unzip)
endif

	-@mkdir classes 2> /dev/null
	javac -d ./classes -cp "./src;./lib/*" ./src/testPackage/Test.java
	echo Main-Class: testPackage.Test>manifest.mf
	echo Class-Path: ./lib/*>>manifest.mf
	
	-@mkdir tmp
	(cd tmp; unzip -uo ../lib/\*.jar)
	-@rm -r tmp/META-INF
	jar cvfm kuribrawl-editor.jar manifest.mf -C classes . -C tmp .
	-@rm -r tmp
else
	ant build
endif 

clear:
	-@rm -r classes/*
	-@rm -r tmp
	-@rm *.jar

clean: clear all

.PHONY: all clear clean dependant release