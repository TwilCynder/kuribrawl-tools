EXEC = DFM.exe
OUT = $(EXEC)

all: start $(EXEC) copy

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

$(EXEC):
	pbcompiler src/dataFileMaker.pb -e $(OUT) //ICON "../../GraphicDesignIsMyPassion/iconDev.ico" //CONSOLE