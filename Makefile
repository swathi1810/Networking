JFLAGS = -g
JC = javac
JVM= java
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java
CLASSES = \
        HTTPRequest.java \
        Spider.java \
        ValueComparator.java \
        Test.java
MAIN = Test
default: classes
classes: $(CLASSES:.java=.class)
run: classes
	$(JVM) $(MAIN)
clean:
	$(RM) *.class		
