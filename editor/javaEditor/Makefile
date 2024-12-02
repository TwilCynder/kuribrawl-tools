EXEC = kuribrawl-editor.jar

SRC= $(shell find src -name '*.java")

all: standalone

SOURCES=$(shell find src -name "*.java")

dependant: $(SOURCES)
ifeq (, $(shell which ant 2> /dev/null))
	-@mkdir classes 2> /dev/null
	./build/compile.sh
	jar cvfm kuribrawl-editor.jar manifest.mf -C classes .
else
	ant dist-zip
endif 

standalone: $(SOURCES)
ifeq (, $(shell which ant 2> /dev/null))

ifeq (, $(shell which unzip 2> /dev/null))
	$(error Release mode build requires either Ant or unzip)
endif

	./build/compile.sh
	
	-@mkdir tmp
	(cd tmp; unzip -uo ../lib/\*.jar)
	-@rm -r tmp/META-INF
	jar cvfm $(EXEC) manifest.mf -C classes . -C tmp .
	-@rm -r tmp

	chmod a+x $(EXEC)
else
	ant build
endif 

release: standalone
	@mkdir -p "../../../release/tools/editor/"
	cp $(EXEC) "../../../release/tools/editor/$(EXEC)"

clear:
	-@rm -r classes/*
	-@rm -r tmp
	-@rm *.jar

clean: clear all

.PHONY: all clear clean dependant release