package tsuro;
import org.junit.jupiter.api.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.Failure;
import org.junit.runner.Result;

public class TestRunner {
    public static void main(String[] args) {
        Result r = JUnitCore.runClasses(TestSuite.class);
        int failure_count = 0;
        for (Failure failure : r.getFailures()) {
            System.out.println(failure.toString());
            failure_count++;
        }
        System.out.println("All tests passed == "+r.wasSuccessful());
        System.out.println("Number of failures == "+failure_count);

    }
}