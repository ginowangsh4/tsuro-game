class Tester {

    static int faults = 0;
    static void check(boolean t, String s) {
        if (!t) {
            faults += 1;
            System.out.println("*** test failed: " + s + " *** ");
        } else {
            System.out.println("*** test succeeded: " + s + " *** ");
        }
    }
}