#!/bin/bash
#parent_path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
cd "$(dirname "${BASH_SOURCE[0]}")"

exec="DFM.exe"
out="./$exec"

while getopts ":hir" option; do
  case $option in
    h) echo "usage: $0 [-h] [-i] [-r]"; exit ;;
    i) out="../../res/$exec" ;;
    r) release="true" ;;
    ?) echo "error: option -$OPTARG is not implemented"; exit ;;
  esac
done

echo $out

pbcompiler src/dataFileMaker.pb -e $out //ICON "../../GraphicDesignIsMyPassion/iconDev.ico" //CONSOLE

if [ ! -z $release ]
then
  mkdir -p "../../release/tools/dfm/"
  cp $out "../../release/tools/dfm/$exec"
fi