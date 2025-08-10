package gamedata.parsers;

import java.io.BufferedReader;
import java.io.IOException;

public class DescriptorReader implements AutoCloseable {
    private BufferedReader reader;
    private int linesRead;

    public DescriptorReader(BufferedReader reader){
        this.reader = reader;
    }

    /**
     * Returns the first valid descriptor line.
     * A descriptor line is a line where all text that follows a '#' has been removed.
     * A valid descriptor line is a non-empty descriptor line.
     * @param reader the reader used to obtain lines.
     * @return A line or null if end of file was reached
     */
    public String readLine() throws IOException{
        linesRead = 0;
        while (reader.ready()) {
            linesRead++;
            String line = reader.readLine();
            line = line.split("#")[0]; //garanti non nul
            if (line.length() > 0) return line; //si on a une descriptor line non vide on return, sinon on passe à la suivante
        }
        return null; //if we reached this point, we didn't find anything before eof, so returning null
    }

    public boolean ready() throws IOException {
        return reader.ready();
    }

    public int getLinesRead() {
        return linesRead;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}