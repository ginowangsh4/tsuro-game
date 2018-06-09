## Tsuro Game

Implementation of board game Tsuro with Java

- Gino Wang - ginowang.sh@u.northwestern.edu 
- Jennifer Liu - jenniferliu2018@u.northwestern.edu 
- Jin Han - jinhan2019@u.northwestern.edu

## GUI and HPlayer

- Compile and run all unit tests with `make`. 
- Run `java -cp bin/ tsuro.Tsuro 10086 1 7 0`, which starts a local tournament with 1 HPlayer and 7 MPlayers, the number, e.g. `10086`, is the port number any remote players can connect to should you want to add any to the tournament.
- In a new terminal, run `java -cp bin/ tsuro.admin.App` to join the tournament as the HPlayer. This should fire up the GUI.

We still need to implement the case when a HPlayer tries to place pawn at an illegal starting position or to commit an illegal move.

## Test

Run all unit tests: 
- If you are in `./`, simply run with `make test` or `make`. 

Run with test-play-a-turn: 
- Run with `./test-play-a-turn play-a-turn` to check against the game rule with Robby's code.
- Use `./test-play-a-turn -h` to see options such as number of games to run and enable verbose display on console. 

Run a unit test suite: 
- Better to compile first with `make`. 
- Then run with `java -jar lib/junit-platform-console-standalone-1.2.0.jar --class-path bin/ -c TESTNAME`. 
- You would need to specific package for the `TESTNAME` - see *Run from command line* section for more.

Run in IntelliJ IDEA: 
- Open project with IntelliJ -> Right click on the folder `test/` -> Click "Run All Test".

## Tournament over network

Start a network client: 
- Make sure that the server is already running, either a local or remote host. 
- In `./`, run `java -cp bin/ tsuro.admin.Admin Port_Number Player_Name Player_Type(H/M) Strategy(R/MS/LS)` - only add Strategy if `Player_Type = M` - to connect with the host to join the tournament. 

Start a server to run tournament:  
- Run `java -cp bin/ tsuro.Tsuro Port_Number Number_of_HPlayer Number_of_MPlayer Number_of_RemotePlayer` to start a local server which starts a tournament with number and type of players as specified.

## Run from command line 

Best to compile everything first with `make`.

To run a single class from command line, use `java -cp bin/ PACKAGENAME.CLASSNAME arg1 arg2 ...` where `arg` is the input into the main function in the class specified by `CLASSNAME`.
- Package `tsuro` contains all Tsuro game element definition classes and their tests.
- Package `tsuro.parser` contains all parser classes that support network and their unit tests.
- Package `tsuro.admin` contains local network localhost server and client definition classes.
