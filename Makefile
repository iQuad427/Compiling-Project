all:
	jflex src/LexicalAnalyzer.flex
	javac -d bin -cp src/ src/Main.java
	jar cfe dist/part1.jar Main -C bin .
#	javadoc -private src/Main.java -d doc/javadoc
testing:
	java -jar dist/part1.jar test/Factorial.fs
#	pdflatex tree.tex
clean:
	rm bin/*.class
	rm dist/part1.jar
	rm src/LexicalAnalyzer.java

all:
	jflex src/LexicalAnalyzer.flex
	javac -d bin -cp src/ src/Main.java
	jar cfe dist/part2.jar Main -C bin .

