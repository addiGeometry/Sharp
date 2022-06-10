import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FillerImplementation implements Filler {
    private OutputStream os;
    private DataOutputStream dos;

    private final int[] dummyFillers = {
            1, 2,
            0, 5,
            3, 3,
            -7, 8,
            100, -12
    };

    public  FillerImplementation(String filepath) throws IOException {
        os = new FileOutputStream(filepath);
        dos = new DataOutputStream(os);
    }

    @Override
    public void dummyfill() throws IOException{
        for(int i=0; i<dummyFillers.length; i++){
            dos.writeInt(dummyFillers[i]);
        }
    }
}
