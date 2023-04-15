EXEC = DFM.exe
OUT = $(EXEC)

SRC= $(shell find src -name '*.pb' -o -name '*.pbi')

all: start $(EXEC) copy
	echo "yes"
	echo $(SRC)

locstart: start
	@printf "\n\033[1;33mBuilding in release mode\n\033[0m"

local: locstart $(EXEC)

copy:
	cp $(EXEC) "../../res/$(EXEC)"

start:
	@if [ -z "$(NOCLEAR)" ]; then\
		clear;\
	fi
	@printf "\n\033[1;33mCompiling the Data File Maker\n\033[0m"

release: all
	@mkdir -p "../../../release/tools/dfm/"
	cp $(EXEC) "../../../release/tools/dfm/$(EXEC)"

$(EXEC): $(SRC)
	pbcompiler src/dataFileMaker.pb -e $(OUT) //ICON "../../GraphicDesignIsMyPassion/iconDev.ico" //CONSOLE

.PHONY: all local locstart copy start release