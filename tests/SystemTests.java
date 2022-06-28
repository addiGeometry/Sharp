import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class SystemTests {
    private Prozessor prozessor;
    private Filler filler;

    private static final int PORT = 2222;

    private final static String PROZDIR = System.getProperty("user.home") + "/data/toProzess";

    public void initializeFillNPro() throws IOException{
        filler = new Filler(4444);
        prozessor = new Prozessor();
    }

    @Test
    public void TestFill() throws IOException {
        initializeFillNPro();
        filler.fill();
    }

    @Test
    public void wieVieleInts() throws IOException, BadFillException {
        this.initializeFillNPro();
        filler.fill();

        new Thread(prozessor);
    }


    @Test
    public void betafillUndAuswerten() throws IOException, BadFillException {
        this.initializeFillNPro();

        filler.fill();

        //int berechnung[] = prozessor.auswerten();

        int[] dummyErgebnisse  = new int[]{
                3,
                5,
                0,
                1,
                -112
        };
        /**for(int i=0; i<berechnung.length; i++){
            Assert.assertEquals(dummyErgebnisse[i], berechnung[i]);
        }*/
    }
}
