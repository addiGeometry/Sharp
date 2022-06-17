import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Prozessor implements Runnable {
    private final FileInputStream in;
    private final DataInputStream din;
    private final String filepath;


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
    public Prozessor(String filepath) throws IOException{
        this.filepath = filepath;
        in = new FileInputStream(filepath);
        din = new DataInputStream(in);
    }

    public int[] auswerten() throws IOException, EOFException, BadFillException{
        ArrayList<Integer> inputs = new ArrayList<>();
        try{
            while(true){
                char operator = din.readChar();
                char space1 = din.readChar();
                if(operator != '+') throw new BadFillException("wrong operator specified");
                int summand1 = din.readInt();
                char space2 = din.readChar();
                int summand2 = din.readInt();
                int ergebnis = summand1 + summand2;
                inputs.add(ergebnis);

                long time = System.currentTimeMillis();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss:mm");
                Date resultdate = new Date(time);

                System.out.println("" + dateFormat.format(resultdate) + ": " + summand1 +space1 + operator + space2 + summand2 + " = " + ergebnis);
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

    @Override
    public void run() {
        try {
            this.auswerten();
        } catch (IOException e) {
            System.err.println("file does not exist: " + filepath);
        } catch (BadFillException e) {
            System.err.println("this sort of error is caused by a bug. Check Stream implementation");
        }
    }
}
