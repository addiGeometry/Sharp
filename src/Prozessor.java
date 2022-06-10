import java.io.EOFException;
import java.io.IOException;

public interface Prozessor {
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
    void auswerten() throws IOException, EOFException;

    /**
     * Gibt nur die Ergebnisse der Addition aus (Testing)
     * @return Ergebnis der Addition
     */
    int[] ergebnisse();
}
