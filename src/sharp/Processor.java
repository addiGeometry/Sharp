package sharp;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static sharp.Filler.WORK_DIR;

public class Processor implements Runnable {
    private boolean kill;

    private static final int SLEEPTIME = 10;
    private final static String PROZDIR = WORK_DIR + "/data/toProcess";

    public Processor() {
        this.kill = false;
    }

    /**
     * Der Processor wertet "PlusCommand"-Ausdrücke (Objekte) als Addition
     * aus und schreibt Logeinträge nach dem Schema:
     * <p>
     * [Zeit der Auswertung]: Aufgabe -> Auswertung
     * <p>
     * Ausdrücke:
     * "+2 3", "+1 2" ,"+-4 2", allgemein: "+x y" mit Integers x,y
     */
    public static void main(String[] args) {
        // run Processor
        new Thread(new Processor()).start();
    }

    public void auswerten(PlusCommand command) throws IOException, BadFillException {
        int summand1 = command.getErsterSummand();
        int summand2 = command.getZweiterSummand();
        int ergebnis = summand1 + summand2;

        String space = " ";
        long time = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss:mm");
        Date resultdate = new Date(time);

        System.out.println(Thread.currentThread() + ": " + dateFormat.format(resultdate) + " -> " + summand1 + space + "+" + space + summand2 + " = " + ergebnis);
    }

    public void kill() {
        this.kill = true;
    }

    public void tasklisteAuswerten() {
        try {
            Thread.sleep(SLEEPTIME);
            List<Path> filePaths;
            try (Stream<Path> stream = Files.list(Path.of(PROZDIR))) {
                filePaths = stream.collect(Collectors.toList());
            }
            for (Path pfad : filePaths) {
                    FileInputStream fis = new FileInputStream(String.valueOf(pfad));
                    ArrayList<PlusCommand> plusCommands = PlusCommand.getCommandFromInputStream(fis);
                    for (PlusCommand toProcess : plusCommands) {
                        this.auswerten(toProcess);
                    }
                    fis.close();
                    Files.delete(pfad);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("taking a nap");
        } catch (BadFillException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while (!this.kill) {
            try {
                Thread.sleep(500);
                this.tasklisteAuswerten();
            } catch (InterruptedException e) {
                //ignore
            }
        }
    }

}
