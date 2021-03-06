package sharp;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import static sharp.Filler.WORK_DIR;

public class SystemTests {
    private Producer producer;
    private Processor processor;
    private Filler filler;

    private static final int PORT = 2222;

    private final static String PROZDIR = WORK_DIR + "/data/toProcess";


    @Test
    public void testFill() throws IOException {
        filler = new Filler(PORT);
        filler.dummyfill();
        filler.fill();
    }

    @Test
    public void verarbeiteEineFile() throws IOException, BadFillException {
        filler = new Filler(PORT);
        filler.dummyfill();
        processor = new Processor();
        filler.fill();
        File auszuwerten = new File(PROZDIR+"/toProcess0.txt");
        FileInputStream dis = new FileInputStream(auszuwerten);
        dis.close();
        filler.dummyfill();
        filler.fill();
        File auszuwerten2 = new File(PROZDIR+"/toProcess1.txt");
        dis = new FileInputStream(auszuwerten2);
        dis.close();
        filler.dummyfill();
        filler.fill();
        File auszuwerten3 = new File(PROZDIR+"/toProcess2.txt");
        dis = new FileInputStream(auszuwerten3);
        //processor.tasklisteAuswerten();
        dis.close();
    }

    @Test
    public void milliTest() throws IOException {
        ArrayList<Integer> productions = new ArrayList<>();
        for(int i=0; i<10; i++){
            productions.add(i);
        }
        producer = new Producer(PORT, productions);
        new Thread(filler);
        producer.initializeSocket();

        processor = new Processor();

        producer.produce();
        filler.fill();
        processor.tasklisteAuswerten();
    }

    @Test
    public void historicTest(){
        processor = new Processor();
        processor.tasklisteAuswerten();
    }
}
