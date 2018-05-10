test: DNE
	javac -cp lib/junit-jupiter-api-5.0.0.jar -d bin/ src/main/*.java src/test/*.java
	java -jar lib/junit-platform-console-standalone-1.2.0.jar --class-path bin/ -c BoardTest
	java -jar lib/junit-platform-console-standalone-1.2.0.jar --class-path bin/ -c DeckTest
	java -jar lib/junit-platform-console-standalone-1.2.0.jar --class-path bin/ -c DragonTest
	java -jar lib/junit-platform-console-standalone-1.2.0.jar --class-path bin/ -c LegalPlayTest
	java -jar lib/junit-platform-console-standalone-1.2.0.jar --class-path bin/ -c MPlayerTest
	java -jar lib/junit-platform-console-standalone-1.2.0.jar --class-path bin/ -c PlayATurnTest
	java -jar lib/junit-platform-console-standalone-1.2.0.jar --class-path bin/ -c ServerTest
	java -jar lib/junit-platform-console-standalone-1.2.0.jar --class-path bin/ -c TileTest
DNE:
