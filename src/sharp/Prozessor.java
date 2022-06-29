package sharp;

import sharp.BadFillException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static sharp.Filler.WORK_DIR;

public class Prozessor implements Runnable {
    private boolean kill;

    private final static String PROZDIR = WORK_DIR + "/data/toProzess";
    private final static File MOTHERFOLDER = new File(PROZDIR);

    public Prozessor(){
        this.kill = false;
    }
    /**
     * Der sharp.Prozessor wertet Ausdrücke aus einer Input-File als Addition
     * aus und schreibt Logeinträge nach dem Schema:
     *
     * [Zeit der Auswertung]: Aufgabe -> Auswertung
     *
     * Ausdrücke:
     * " 2 3", " 1 2" ," -4 2", allgemein: " x y" mit Integers x,y
     *
     */
    public static void main(String[] args) {
        if(args.length < 1) {

        } else {
            File commandFile = new File(args[0]);
            if (!commandFile.exists()) {
                System.err.println("command file does not exist: " + args[0]);
            } else {
                // run Processor
                new Thread(new Prozessor()).start();
            }
        }
    }

    public void auswerten(PlusCommand command) throws IOException, BadFillException {
        int summand1 = command.getErsterSummand();
        int summand2 = command.getZweiterSummand();
        int ergebnis = summand1  + summand2;

        String space = " ";
        long time = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss:mm");
        Date resultdate = new Date(time);

        System.out.println("" + dateFormat.format(resultdate) + ": " + summand1 + space + "+" + space + summand2 + " = " + ergebnis);
    }
    public void kill(){
        this.kill=true;
    }

    @Override
    public void run(){
        while(!this.kill){
            try{
                Thread.sleep(100);
                Stream<Path> files =  Files.list(Path.of(PROZDIR));
                List<Path> filePaths = files.toList();
                for(Path pfad : filePaths){
                    FileInputStream fis = new FileInputStream(String.valueOf(pfad));
                    do{
                        ArrayList<PlusCommand> plusCommands = PlusCommand.getCommandFromInputStream(fis);
                        for(PlusCommand toProzess : plusCommands){
                            this.auswerten(toProzess);
                        }
                    } while(fis.available() > 0);
                    fis.close();
                    Files.delete(pfad);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (BadFillException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

        /* while(!this.kill){
            try {
                Thread.sleep(100);
                List<File> toProzess = Arrays.asList(new File(PROZDIR).listFiles());

               for(File file : toProzess){
                   auswerten(file);
               }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (BadFillException e) {
               throw new RuntimeException(e);
           } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } */

    public void which() {
        try{
            Stream<Path> files =  Files.list(Path.of(PROZDIR));
            List<Path> filePaths = files.toList();
            for(Path pfad : filePaths){
                FileInputStream fis = new FileInputStream(String.valueOf(pfad));
                do{
                    ArrayList<PlusCommand> plusCommands = PlusCommand.getCommandFromInputStream(fis);
                    for(PlusCommand toProzess : plusCommands){
                        this.auswerten(toProzess);
                    }
                } while(fis.available() > 0);
                fis.close();
                Files.delete(pfad);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (BadFillException e) {
            throw new RuntimeException(e);
        }
    }
}
