mkdir classes
javac -d ./classes -cp "./src;./lib/*" ./src/testPackage/Test.java
echo Main-Class: testPackage.Test>manifest.mf
echo Class-Path: ./lib/*>manifest.mf
jar cvfm kuribrawl-editor-test.jar manifest.mf -C classes .