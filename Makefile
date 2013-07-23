.PHONY: all clean compile

all: TMCMG.jar

clean:
	rm -rf bin TMCMG.jar .java-src.lst

compile:
	rm -rf bin
	cp -r src bin
	find src jnbt-src -name '*.java' >.java-src.lst
	javac -d bin @.java-src.lst -target 1.6 -source 1.6
	mkdir -p bin bin/META-INF
	echo 'Version: 1.0' >bin/META-INF/MANIFEST.MF
	echo 'Main-Class: togos.minecraft.mapgen.app.TMCMG' >>bin/META-INF/MANIFEST.MF

TMCMG.jar: compile
	rm -f TMCMG.jar
	cd bin ; zip -r ../TMCMG.jar . ; cd ..
