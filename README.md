## Tsuro Game

Implementation of board game Tsuro with Java

- Gino Wang - ginowang.sh@u.northwestern.edu 
- Jennifer Liu - jenniferliu2018@u.northwestern.edu 
- Jin Han - jinhan2019@u.northwestern.edu

## Test

Run all unit tests: 
- If you are in `./`, simply run with `make test` or `make`. 

Run with test-play-a-turn: 
- Run with `./test-play-a-turn play-a-turn` to check against the game rule with Robby's code.
- Use `./test-play-a-turn -h` to a options such as number of games to run and enable verbose display on console. 

Run a unit test suite: 
- Better to compile first with `javac -cp lib/junit-jupiter-api-5.0.0.jar -d bin/ src/main/*.java src/test/*.java`. 
- Then run with `java -jar lib/junit-platform-console-standalone-1.2.0.jar --class-path bin/ -c TESTNAME`. 
- You would need to specific package for the `TESTNAME` - see *Run from command line section* for more!

Run in IntelliJ IDEA: 
- Open project with IntelliJ -> Right click on the folder `test/` -> Click "Run All Test".

## Tournament over network

Start a Network Client: 
- Make sure that the server is already running, either a local or remote host. 
- In `./`, run `java -cp bin/ tsuro.admin.Admin PORTNUMBER PLAYERNAME STRATEGY(R/MS/LS)` to connect with the host to join the tournament. Remember to align the port number to connect to the correct socket.

Start a Localhost to Run Simple Tournament:  
- Run `src/main/Tsuro` to start a localhost server with port number 8000 which starts a tournament with one remote player and three machine player.

## Run from command line 

Best to compile everything first with `javac -cp lib/junit-jupiter-api-5.0.0.jar -d bin/ src/main/*.java src/test/*.java src/parser/*.java src/admin/*.java`.

To run a single class from command line, use `java -cp bin/ PACKAGENAME.CLASSNAME arg1 arg2 ...` where `arg` is the input into the main function in the class specified by `CLASSNAME`.
- Package `tsuro` contains all Tsuro game element definition classes and their tests.
- Package `tsuro.parser` contains all parser classes that support network and their unit tests.
- Package `tsuro.admin` contains local network localhost server and client definition classes.