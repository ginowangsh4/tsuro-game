class Tester {

    static int faults = 0;

    static void check(boolean t, String s) {
        if (!t) {
            faults += 1;
            System.out.println("*** Failed: " + s + " *** ");
        }
        else {
            System.out.println("*** Succeeded: " + s + " *** ");
        }
    }
}