import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Filler {
    private OutputStream os;
    private DataOutputStream dos;
    private final int port;

    private Socket socket = null;

    private ServerSocket srvSocket=null;


    private final int[] dummyFillers = {
            1, 2,
            0, 5,
            -3, 3,
            -7, 8,
            -100, -12
    };

    public Filler(String filepath, int port) throws IOException {
        this.port = port;
        os = new FileOutputStream(filepath);
        dos = new DataOutputStream(os);

        //Filler acts as a Server
        //socket = getSocket();
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
            if(i % 2 == 0) {
                dos.writeChar('+');
                dos.writeChar(' ');
                dos.writeInt(dummyFillers[i]);
                dos.writeChar(' ');
            }
            else{
                dos.writeInt(dummyFillers[i]);
            }
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
}
