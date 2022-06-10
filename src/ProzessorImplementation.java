import java.io.*;
import java.util.ArrayList;

public class ProzessorImplementation implements Prozessor {
    private final FileInputStream in;
    private final DataInputStream din;



    public ProzessorImplementation(String filepath) throws IOException{
        in = new FileInputStream(filepath);
        din = new DataInputStream(in);
    }

    @Override
    public void auswerten() throws IOException, EOFException{
        ArrayList<Integer> inputs = new ArrayList<Integer>();
        try{
            while(true){
                int tmp = din.readInt();
                inputs.add(tmp);
                System.out.println(tmp);
            }
        }
        catch (EOFException e) {
            System.out.println("Reached the End");
        }
    }

    @Override
    public int[] ergebnisse() {
        return new int[1];
    }


}
