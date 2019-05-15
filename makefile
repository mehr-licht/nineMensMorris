jcc:
	-rm bin/*.class
	javac -verbose -d bin src/*.java
	java bin/NineMensMorris 5

clean:
	-rm bin/*.class

compile:
	javac -verbose -d bin src/*.java 

run:
	cd bin/production/nine-mens-morris-master/ && java NineMensMorris 5
