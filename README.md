## Tsuro-Game

Implementation of board game Tsuro with Java

* Gino Wang - ginowang.sh@u.northwestern.edu 
* Jennifer Liu - jenniferliu2018@u.northwestern.edu 
* Jin Han - jinhan2019@u.northwestern.edu

### Test

Run all unit tests: Run with `make test`.

Run with test-play-a-turn: Run with `./test-play-a-turn -g NUMGAMES play-a-turn`.

Run a unit test suite: Compile with `javac -cp lib/junit-jupiter-api-5.0.0.jar -d bin/ src/main/*.java src/test/*.java` -> Run with `java -jar lib/junit-platform-console-standalone-1.2.0.jar --class-path bin/ -c TESTNAME`. 

Run in IntelliJ IDEA: Open project -> Right click `test/` -> Click "Run All Test".

### Tournament over network

Network Client: Run `make` then `java -cp bin/ tsuro.admin.Admin PORTNUMBER PLAYERNAME STRATEGY(R/MS/LS)` to connect with localhost or a remote host to join the tournament.

Localhost: Run `src/main/Tsuro` to start a localhost server with port number 8000 which starts a tournament with one remote player and three machine player.

### Run from command line 

Best to compile everything first with `javac -cp lib/junit-jupiter-api-5.0.0.jar -d bin/ src/main/*.java src/test/*.java src/parser/*.java src/admin/*.java`.

To run a single class from command line, use `java -cp bin/ PACKAGENAME.CLASSNAME arg1 arg2 ...` where `arg` is the input into the main function in the class specified by `CLASSNAME`.

* Package `tsuro` contains all Tsuro game element definition classes and their tests.

* Package `tsuro.parser` contains all parser classes that support network and their unit tests.

* Package `tsuro.admin` contains local network localhost server and client definition classes.