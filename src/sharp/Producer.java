package sharp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Producer implements Runnable{
    private Socket srvSocket = null;
    TCPServer tcpServer = null;
    private DataOutputStream dout;
    private boolean killed = false;
    private int port;
    private ArrayList<Integer> productions;
    private boolean connected=false;

    public Producer(int port, ArrayList<Integer> productions){
        this.killed = false;
        this.port = port;
        this.productions = productions;
        tcpServer = new TCPServer();
    }

    public static void main(String[] args) {
        if(args.length < 1 || args.length > 2){
            System.err.println("usage: @param1 port, @param2 producer count");
            System.exit(1);
        }
        int anzahl = 0;
        if(args.length == 1){
            anzahl=1;
        }
        else{
            anzahl = Integer.parseInt(args[1]);
        }
        ArrayList<Thread> arbeiter = new ArrayList<>();
        for(int i=0; i<anzahl;i++){
            ArrayList<Integer> productions = Productions.generateProductions();
            Producer aufgabe = new Producer(Integer.parseInt(args[0]), productions);
            arbeiter.add(new Thread(aufgabe));
        }
        for(Thread t : arbeiter) t.start();

    }

    public void produce() throws IOException{
        System.out.println("producing...");
        for(int i : productions){
            dout.writeInt(i);
        }
        System.out.println("Producer "+Thread.currentThread()+" has finished.");
        this.kill();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                                          Thread-Methods                                              ///
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void kill(){
        this.killed = true;
    }

    @Override
    public void run() {
        try{
            while(!connected){
                try {
                    this.initializeSocket();
                    Thread.sleep(500);
                }
                catch (IOException e){}
            }
            this.produce();
        } catch (IOException | InterruptedException e){
            System.err.println("Error: Bad Gateway");
            System.exit(1);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                                            TCP-Methods                                               ///
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void initializeSocket() throws IOException{
        srvSocket = tcpServer.getSocket();
        dout = new DataOutputStream(srvSocket.getOutputStream());
        this.connected = true;
    }
    private class TCPServer {
        private ServerSocket srvSocket = null;

        Socket getSocket() throws IOException {
            if(this.srvSocket == null) {
                this.srvSocket = new ServerSocket(port);
            }

            //<<<<<<<<<<<<<<<<<<debug
            StringBuilder b = new StringBuilder();
            b.append(this.getClass().getSimpleName());
            b.append(" (");
            b.append("Producer: " + Thread.currentThread());
            b.append("): ");
            b.append("opened port ");
            b.append(port);
            b.append(" on localhost and wait");
            System.out.println(b.toString());
            //>>>>>>>>>>>>>>>>>>>debug

            Socket socket = this.srvSocket.accept();
            //<<<<<<<<<<<<<<<<<<debug
            b = new StringBuilder();
            b.append(this.getClass().getSimpleName());
            b.append(" (");
            b.append("Producer: " + Thread.currentThread());
            b.append("): ");
            b.append("connected");
            System.out.println(b.toString());
            //>>>>>>>>>>>>>>>>>>>debug

            return socket;
        }

        public void kill() throws IOException {
            this.srvSocket.close();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                                            Productions                                               ///
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private class Productions {
        /*  deprecated
        public static ArrayList<Integer> getProductionsFromFile(String input) throws FileNotFoundException, IOException {
            ArrayList<Integer> productions = new ArrayList<>();

            FileInputStream in = new FileInputStream(input);
            DataInputStream din = new DataInputStream(in);

            try{
                din.readInt();
            }
            catch (EOFException e){
            }
            return productions;
        }*/

        public static ArrayList<Integer> generateProductions(){
            ArrayList<Integer> productions = new ArrayList<>();
            int vz=1;
            for(int i=0; i<20; i++){
                if(Math.random() < 0.49) vz=-1;
                productions.add(ThreadLocalRandom.current().nextInt(-300, 300 + 1));
            }
            return productions;
        }
    }
}
