all:
	jflex src/LexicalAnalyzer.flex
	javac -d bin -cp src/ src/Main.java
	jar cfe dist/part3.jar Main -C bin .
#	javadoc -private src/Main.java -d doc/javadoc
testing:
	java -jar dist/part3.jar test/Factorial.fs
#	pdflatex tree.tex
compile:
	java -jar dist/part3.jar -wt test/out.txt test/Factorial.fs > src/compiler.ll
	llvm-as src/compiler.ll -o=bin/compiler.bc
	lli bin/compiler.bc
clean:
	rm bin/*.class
	rm dist/part3.jar
	rm src/LexicalAnalyzer.java
