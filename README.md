## Tsuro-Game

Implementation of board game Tsuro with Java

* Jennifer Liu - jenniferliu2018@u.northwestern.edu 
* Gino Wang - ginowang.sh@u.northwestern.edu 
* Jin Han - jinhan2019@u.northwestern.edu

#### Test
Run all tests: Navigate to `./` and run with `make`

Run a single test: Compile with `javac -cp lib/junit-jupiter-api-5.0.0.jar -d bin/ src/main/*.java src/test/*.java` -> Run with `java -jar lib/junit-platform-console-standalone-1.2.0.jar --class-path bin/ -c TESTNAME` 

Run with IntelliJ IDEA: Open project -> Right click `test/` -> Click "Run All Test"

#### Run from command line 

Best to compile everything first with `javac -cp lib/junit-jupiter-api-5.0.0.jar -d bin/ src/main/*.java src/test/*.java src/parser/*.java src/admin/*.java`

To run a single class from command line, use `java -cp bin/ PACKAGENAME.CLASSNAME arg1 arg2 ...` where `arg` is the input into the main function in the class specified by CLASSNAME

* Package `tsuro` contains all Tsuro game element definition classes and their test 

* Package `tsuro.parser` contains all parser classes that support network

* Package `tsuro.admin` contains local network server definition classes