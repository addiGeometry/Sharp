package sharp;

import java.io.*;
import java.util.ArrayList;

class PlusCommand{
    private int ersterSummand, zweiterSummand;

    public PlusCommand(int erster, int zweiter){
        this.ersterSummand = erster;
        this.zweiterSummand = zweiter;
    }

    static ArrayList<PlusCommand> getCommandFromInputStream(FileInputStream is) throws IOException, BadFillException {
            DataInputStream din = new DataInputStream(is);
            ArrayList<PlusCommand> commands = new ArrayList<>();
            try{
                while(true){
                    char operator = din.readChar();
                    if(operator != '+') throw new BadFillException("wrong operator specified");
                    int summand1 = din.readInt();
                    int summand2 = din.readInt();
                    commands.add(new PlusCommand(summand1,summand2));
                }
            }
            catch(EOFException e) {}
            din.close();
            return commands;
    }

    public int getErsterSummand(){
        return ersterSummand;
    }

    public int getZweiterSummand(){
        return zweiterSummand;
    }

}
