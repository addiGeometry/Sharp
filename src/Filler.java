import java.io.IOException;

public interface Filler {
    /**
     * Fülle die Datei (Pfad angegeben bei Erzeugung eines Fillers) mit der Liste dieser Operationen:
     * " 1 2"
     * " 0 5"
     * " -3 3"
     * " -7 8"
     * " -100 -12"
     */
    void dummyfill() throws IOException;
}
