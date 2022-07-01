package sharp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class Filler implements Runnable{
    private static int port;

    /**TODO
     * IMPORTANT: Replace this String with your path from the home directory to your current. I know this isn't optimal
     * */
    private static final String REPLACE_FOR_YOUR_SYSTEM_DIR = "/IdeaProjects/Sharp";
    public static final String WORK_DIR = System.getProperty("user.home") + REPLACE_FOR_YOUR_SYSTEM_DIR;
    private static final String FILL_DIR = WORK_DIR + "/data/toProcess/";

    private static final long WAIT_IN_MILLIS = 4000;

    private static int tasknumber = 0;

    private DataInputStream din;
    private OutputStream os;
    private DataOutputStream dos;
    private TCPClient tcpClient = null;
    private boolean killed;
    private Socket socket =null;

    //Zeit in ms, die der Filler im jeweiligen Lese- oder Schreibzustand verbringt
    private static long TASKTIME = 5000l;

    private ArrayList<Integer> fillTasks = new ArrayList<Integer>();


    private final int[] dummyFillers = {
            1, 2,
            0, 5,
            -3, 3,
            -7, 8,
            -100, -12
    };
    /**
     * Filler warten auf Producer, die sich über TCP verbinden wollen, und schreiben
     * die empfangenen Befehle in Files, die am Ende in den toProcess-Ordner für den Processor
     * verschoben werden.
     * */
    public Filler(int port){
        this.killed = false;
        this.port = port;
        tcpClient = new TCPClient();
    }

    public static void main(String[] args) {
        //args[0] ist der port
        if(args.length < 1){
            System.err.println("Error: specify a port");
            System.exit(1);
        }
        Filler filler = new Filler(Integer.parseInt(args[0]));
        new Thread(filler).start();
    }

    public void readTasks() throws IOException, EndException {
        //Versuche am Input zu lesen
        if(this.socket == null) throw new IOException();
        System.out.println("reading...");
        long time = System.currentTimeMillis();
        try{
            /* Lese für den Zeitraum TASKTIME (in Millisekunden). Andere Aufgaben aus der
            Liste werden später geschrieben */
            while((System.currentTimeMillis() - time) <= TASKTIME){
                Thread.sleep(50);
                fillTasks.add(din.readInt());
                fillTasks.add(din.readInt());
            }
        }
        catch (EOFException e){
            //Keine weiteren Aufgaben mehr zu füllen
            if(fillTasks.size()==0) throw new EndException();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        din.close();
    }

    public void fill() throws IOException{
        if(this.socket == null) throw new IOException();
        System.out.println("filling...");
        //Erzeuge eine Datei und befülle Sie mit Aufgaben
        if(fillTasks.size()%2 == 1){
            System.err.println("Filler corrupted: uneven amount of task");
        }
        File cmdFile = new File("file" + tasknumber + ".txt");
        this.os = new FileOutputStream(cmdFile);
        this.dos = new DataOutputStream(os);
        int c=fillTasks.size();
        long time = System.currentTimeMillis();
        boolean should = true;
        for(int i : fillTasks){
            //Fülle für den Zeitraum TASKTIME (in ms)
            if( should || (c%2==1)){
                long second = System.currentTimeMillis();
                long diff = second - time;
                should = diff <= TASKTIME;
                if(c%2 == 0) this.dos.writeChar('+');
                this.dos.writeInt(i);
                c--;
            }
            if(!should && (c%2!=1)) break;
        }
        //Streiche Aufgaben von der Liste
        for(int i=fillTasks.size()-1; i>=c; i--){
            fillTasks.remove(i);
        }
        this.dos.close();
        this.os.close();
        //Verschiebe die Files in der Ordner für den Prozessor
        Files.move(Path.of(cmdFile.getAbsolutePath()),Path.of(FILL_DIR+"toProcess"+tasknumber+".txt"), StandardCopyOption.REPLACE_EXISTING);
        tasknumber++;
    }


    /**
     * Fülle die Datei (Pfad angegeben bei Erzeugung eines Fillers) mit der Liste dieser Operationen:
     * " 1 2"
     * " 0 5"
     * " -3 3"
     * " -7 8"
     * " -100 -12"
     */
    public void dummyfill(){
        for(int i=0; i<dummyFillers.length; i++){
            fillTasks.add(dummyFillers[i]);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                                            TCP-Methods                                               ///
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void initializeSocket() throws IOException{
        socket = tcpClient.getSocket();
        din = new DataInputStream(socket.getInputStream());
        System.out.println("connected...");
    }

    private class TCPClient{
        private boolean killed = false;

        public void kill() {
            this.killed = true;
        }

        Socket getSocket() throws IOException {
            while(!this.killed) {
                try {
                    //<<<<<<<<<<<<<<<<<<debug
                    StringBuilder b = new StringBuilder();
                    b.append("(");
                    b.append("Filler");
                    b.append("): ");
                    b.append("waiting for new producers to connect on port ");
                    b.append(port);
                    System.out.println(b.toString());
                    //>>>>>>>>>>>>>>>>>>>debug
                    Socket socket = new Socket("localhost", port);
                    return socket;
                }
                catch(IOException ioe) {
                    //<<<<<<<<<<<<<<<<<<debug
                    StringBuilder b = new StringBuilder();
                    b.append("(");
                    b.append("Filler");
                    b.append("): ");
                    b.append("failed to connect / waiting 3secs and retrying port ");
                    b.append(port);
                    System.out.println(b.toString());
                    try {
                        Thread.sleep(WAIT_IN_MILLIS);
                    } catch (InterruptedException ex) {
                        // ignore
                    }
                }
            }
            throw new IOException("thread was killed before establishing a connection");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                                         Thread-Methods                                               ///
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void kill(){
        if(this.tcpClient != null) this.tcpClient.kill();
        try{
            if(this.dos != null) this.dos.close();
            if(this.os != null) this.os.close();
        } catch (IOException e) {
            //ignore - kill anyway
        }
        this.killed = true;
    }
    @Override
    public void run() {
        while(!killed){
            try {
                this.initializeSocket();
                Thread.sleep(50);
                this.readTasks();
                this.fill();
            }
            catch (IOException e) {
                System.err.println("Error: TCP-connection failed");
                System.exit(1);
            } catch (EndException e) {

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}