import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Prozessor implements Runnable {
    private boolean kill;

    private final static String PROZDIR = System.getProperty("user.home") + "/data/toProzess";
    private final static File motherfolder = new File(PROZDIR);

    public Prozessor(){
        this.kill = false;
    }
    /**
     * Der Prozessor wertet Ausdrücke aus einer Input-File als Addition
     * aus und schreibt Logeinträge nach dem Schema:
     *
     * [Zeit der Auswertung]: Aufgabe -> Auswertung
     *
     * Ausdrücke:
     * " 2 3", " 1 2" ," -4 2", allgemein: " x y" mit Integers x,y
     *
     */

    public int[] auswerten(File filepath) throws IOException, EOFException, BadFillException{
        ArrayList<Integer> inputs = new ArrayList<>();
        FileInputStream in = new FileInputStream(filepath);
        DataInputStream din = new DataInputStream(in);
        try{
            while(true){
                char operator = din.readChar();
                if(operator != '+') throw new BadFillException("wrong operator specified");
                int summand1 = din.readInt();
                int summand2 = din.readInt();
                int ergebnis = summand1 + summand2;
                inputs.add(ergebnis);

                String space = " ";
                long time = System.currentTimeMillis();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss:mm");
                Date resultdate = new Date(time);

                System.out.println("" + dateFormat.format(resultdate) + ": " + summand1 + space + operator + space + summand2 + " = " + ergebnis);
            }
        }
        catch (EOFException e) {
            System.out.println("Reached the End");
        }
        int[] ergebnisse = new int[inputs.size()];
        int i = 0;
        for(int x : inputs){
            ergebnisse[i] = x;
            i++;
        }
        return ergebnisse;
    }
    public void kill(){
        this.kill=true;
    }

    @Override
    public void run(){
        while(!this.kill){
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
        }
    }
}
