import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class Filler implements Runnable{
    private final int port;

    private static final String PROZESSORDIR = System.getProperty("user.dir") + "/data/toProzess/";

    private static int tasknumber = 0;

    private OutputStream os;
    private DataOutputStream dos;

    private Socket socket = null;
    private ServerSocket srvSocket=null;


    private final int[] dummyFillers = {
            1, 2,
            0, 5,
            -3, 3,
            -7, 8,
            -100, -12
    };

    public Filler(int port) throws IOException {
        this.port = port;
        //Filler acts as a Server
        //socket = getSocket();
    }

    public void fill() throws IOException{
        File cmdFile = new File("file" + tasknumber + ".txt");

        this.os = new FileOutputStream(cmdFile);
        this.dos = new DataOutputStream(os);

        //TODO
        //Aus TCP ausgelesene Fills

        dummyfill();
        this.dos.close();
        this.os.close();

        Files.move(Path.of(cmdFile.getAbsolutePath()),Path.of(PROZESSORDIR+"toProzess"+tasknumber+".txt"), StandardCopyOption.REPLACE_EXISTING);
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

    public void dummyfill() throws IOException{
        for(int i=0; i<dummyFillers.length; i++){
                dos.writeChar('+');
                dos.writeInt(dummyFillers[i]);
                dos.writeInt(dummyFillers[i]);
        }
    }

    private Socket getSocket() throws IOException {
        String name = "filler";
        if(this.srvSocket == null){
            this.srvSocket = new ServerSocket(port);
        }
        //<<<<<<<<<<<<<<<<<<debug
        StringBuilder b = new StringBuilder();
        b.append("TCPChannel (");
        b.append(name);
        b.append("): ");
        b.append("opened port ");
        b.append(port);
        b.append(" on localhost and wait");
        System.out.println(b.toString());
        //>>>>>>>>>>>>>>>>>>>debug

        Socket socket = this.srvSocket.accept();
        //<<<<<<<<<<<<<<<<<<debug
        b = new StringBuilder();
        b.append("TCPChannel (");
        b.append(name);
        b.append("): ");
        b.append("connected");
        System.out.println(b.toString());
        //>>>>>>>>>>>>>>>>>>>debug

        return socket;
    }

        private void kill() throws IOException{
            this.srvSocket.close();
        }

    @Override
    public void run() {

    }
}
