import java.io.Reader;
import java.io.IOException;

/** Translating Reader: a stream that is a translation of an
 *  existing reader.
 *  @author Eduardo Huerta
 */
public class TrReader extends Reader {
    /**
     * A new TrReader that produces the stream of characters produced
     * by STR, converting all characters that occur in FROM to the
     * corresponding characters in TO.  That is, change occurrences of
     * FROM.charAt(i) to TO.charAt(i), for all i, leaving other characters
     * in STR unchanged.  FROM and TO must have the same length.
     */
    private final Reader _reader;
    private final String _from, _to;


    public TrReader(Reader str, String from, String to) {
        // TODO: YOUR CODE HERE
        /** A new TrReader that produces the stream of characters produced
         *  by STR, converting all characters that occur in FROM to the
         *  corresponding characters in TO.  That is, change occurrences of
         *  FROM.charAt(0) to TO.charAt(0), etc., leaving other characters
         *  unchanged.  FROM and TO must have the same length. */
        this._reader = str;
        this._to = to;
        this._from = from;
    }



    /* TODO: IMPLEMENT ANY MISSING ABSTRACT METHODS HERE
     * NOTE: Until you fill in the necessary methods, the compiler will
     *       reject this file, saying that you must declare TrReader
     *       abstract. Don't do that; define the right methods instead!
     */

    /**
     * Close the Reader supplied to my constructor.
     */
    public void close() throws IOException {
        try {
            _reader.close();
        } catch (IOException e) {
            return;
        }
    }


    public int read(char[] car, int off, int longitud) throws IOException {
        int temp = _reader.read(car, off, longitud);
        for (int x = off; x < off + longitud; x++) {
            int fromIndex = _from.indexOf(car[x]);
            if (fromIndex > -1) {
                car[x] = _to.charAt(fromIndex);
            }
        }
        return Math.min(longitud, temp);
    }
}