package sharp;

import org.junit.Assert;
import org.junit.Test;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import static sharp.Filler.WORK_DIR;

public class SystemTests {
    private Prozessor prozessor;
    private Filler filler;

    private static final int PORT = 2222;

    private final static String PROZDIR = WORK_DIR + "/data/toProzess";

    public void initializeFillNPro() throws IOException{
        filler = new Filler(4444);
        prozessor = new Prozessor();
    }

    @Test
    public void testFill() throws IOException {
        initializeFillNPro();
        filler.fill();
    }

    @Test
    public void verarbeiteEineFile() throws IOException, BadFillException {
        this.initializeFillNPro();
        filler.fill();
        File auszuwerten = new File(PROZDIR+"/toProzess0.txt");
        FileInputStream dis = new FileInputStream(auszuwerten);
        for(PlusCommand cmd : PlusCommand.getCommandFromInputStream(dis)){
            prozessor.auswerten(cmd);
        }
    }


    @Test
    public void betafillUndAuswerten() throws IOException, BadFillException {
        this.initializeFillNPro();
        filler.fill();
        new Thread(prozessor).start();
    }
}
