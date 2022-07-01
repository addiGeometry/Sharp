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
     * Der Processor wertet "PlusCommand"-Ausdrücke (Objekte) aus Eingabedateien aus dem "toProcess" ordner als Addition
     * aus und schreibt Logeinträge nach dem Schema:
     * <p>
     * [CurrentThread]: [Zeit der Auswertung]: Aufgabe -> Auswertung
     * <p>
     * Ausdrücke:
     * "+2 3", "+1 2" ,"+-4 2", allgemein: "+x y" mit Integers x,y
     * Eingabedateien enthalten Aufgaben, gespeichert als +Zahl1Zahl2 (Ohne Leerzeichen), die zunächst von der Klasse
     * PlusCommand in Befehle umgewandelt werden.
     */
    public static void main(String[] args) {
        // run Processor
        new Thread(new Processor()).start();
    }

    public void auswerten(PlusCommand command) throws IOException, BadFillException {
        //Daten aus dem Command; aus dem PlusCommand
        int summand1 = command.getErsterSummand();
        int summand2 = command.getZweiterSummand();
        int ergebnis = summand1 + summand2;

        String space = " ";
        long time = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss.SSS");
        Date resultdate = new Date(time);

        //Logeintrag
        System.out.println(Thread.currentThread() + ": " + dateFormat.format(resultdate) + " -> " + summand1 + space + "+" + space + summand2 + " = " + ergebnis);
    }

    public void kill() {
        this.kill = true;
    }

    public void tasklisteAuswerten() {
        try {
            Thread.sleep(SLEEPTIME);
            //Methode, um "toProcess" ordner "abzuhören". Pfade neuer Dateien landen in der filePaths Liste.
            List<Path> filePaths;
            try (Stream<Path> stream = Files.list(Path.of(PROZDIR))) {
                filePaths = stream.collect(Collectors.toList());
            }
            for (Path pfad : filePaths) {
                    //Pfadauswertung
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
            System.err.println("In and Out Error");
        } catch (BadFillException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        //Routine
        System.out.println("Processor initiated...");
        while (!this.kill) {
            try {
                Thread.sleep(100);
                this.tasklisteAuswerten();
            } catch (InterruptedException e) {
                //ignore
            }
        }
    }

}
