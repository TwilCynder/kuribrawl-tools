EXEC = kuribrawl-editor.jar

all: $(EXEC)

$(EXEC):
ifeq (, $(shell which ant))
	-mkdir classes
	javac -d ./classes -cp "./src;./lib/*" ./src/testPackage/Test.java
	echo Main-Class: testPackage.Test>manifest.mf
	echo Class-Path: ./lib/*>>manifest.mf
	jar cvfm $@ manifest.mf -C classes .
else
	ant build
endif 

clear:
	-@rm -r classes/*
	-@rm *.jar

clean: clear all

.PHONY: all clear clean