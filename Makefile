all:
	jflex src/LexicalAnalyzer.flex
	javac -d bin -cp src/ src/LexicalAnalyzer.java
	jar cfe dist/part1.jar LexicalAnalyzer -C bin .
testing:
	java -jar dist/part1.jar test/Factorial.fs
clean:
	rm bin/*.class
	rm dist/part1.jar
	rm src/LexicalAnalyzer.java
	rm src/*.java~