package tsuro;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
        BoardTest.class,
        DeckTest.class,
        DragonTest.class,
        LegalPlayTest.class,
        MPlayerTest.class,
        PlayATurnTest.class,
        ServerTest.class,
        TileTest.class
})

public class TestSuite {

}