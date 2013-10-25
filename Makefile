all: bin

clean:
	rm -rf bin

bin: src/enovahack/*.java
	mkdir -p bin
	javac -d bin src/org/json/*.java src/enovahack/*.java
