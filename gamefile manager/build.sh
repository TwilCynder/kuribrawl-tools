#!/bin/bash
#parent_path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
cd "$(dirname "${BASH_SOURCE[0]}")"

out="./DFM.exe"

while getopts ":hi" option; do
  case $option in
    h) echo "usage: $0 [-h] [-i]"; exit ;;
    i) out="../../res/DFM.exe" ;;
    ?) echo "error: option -$OPTARG is not implemented"; exit ;;
  esac
done

echo $out

pbcompiler src/dataFileMaker.pb -e $out //ICON "../../GraphicDesignIsMyPassion/iconDev.ico" //CONSOLE