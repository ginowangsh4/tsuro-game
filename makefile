test: DNE
	javac -cp lib/junit-jupiter-api-5.0.0.jar -d bin/ src/main/*.java src/test/*.java src/parser/*.java src/admin/*.java
	java -jar lib/junit-platform-console-standalone-1.2.0.jar --class-path bin/ -c tsuro.BoardTest
	java -jar lib/junit-platform-console-standalone-1.2.0.jar --class-path bin/ -c tsuro.DeckTest
	java -jar lib/junit-platform-console-standalone-1.2.0.jar --class-path bin/ -c tsuro.DragonTest
	java -jar lib/junit-platform-console-standalone-1.2.0.jar --class-path bin/ -c tsuro.LegalPlayTest
	java -jar lib/junit-platform-console-standalone-1.2.0.jar --class-path bin/ -c tsuro.MPlayerTest
	java -jar lib/junit-platform-console-standalone-1.2.0.jar --class-path bin/ -c tsuro.PlayATurnTest
	java -jar lib/junit-platform-console-standalone-1.2.0.jar --class-path bin/ -c tsuro.ServerTest
	java -jar lib/junit-platform-console-standalone-1.2.0.jar --class-path bin/ -c tsuro.parser.BoardParserTest
	java -jar lib/junit-platform-console-standalone-1.2.0.jar --class-path bin/ -c tsuro.parser.PawnParserTest
	java -jar lib/junit-platform-console-standalone-1.2.0.jar --class-path bin/ -c tsuro.parser.BoardParserTest
	java -jar lib/junit-platform-console-standalone-1.2.0.jar --class-path bin/ -c tsuro.parser.SPlayerParserTest
	java -jar lib/junit-platform-console-standalone-1.2.0.jar --class-path bin/ -c tsuro.parser.TileParserTest
DNE:
