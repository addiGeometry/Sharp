import org.junit.Assert;
import org.junit.Test;

import java.io.EOFException;
import java.io.IOException;

public class SystemTests {
    private Prozessor prozessor;
    private Filler filler;
    private static final String DATASOURCE="/home/s0583232/IdeaProjects/sharp/data/file.txt";

    public void initializeFillNPro() throws IOException{
        filler = new FillerImplementation(DATASOURCE);
        prozessor = new ProzessorImplementation(DATASOURCE);
    }

    @Test
    public void wieVieleInts() throws IOException{
        this.initializeFillNPro();

        filler.dummyfill();
        prozessor.auswerten();
    }


    @Test
    public void betafillUndAuswerten() throws IOException {
        filler.dummyfill();
        int berechnung[] = prozessor.ergebnisse();

        int[] dummyErgebnisse  = new int[]{
                3,
                5,
                0,
                1,
                -112
        };
        for(int i=0; i<berechnung.length; i++){
            Assert.assertEquals(dummyErgebnisse[i], berechnung[i]);
        }
    }
}
