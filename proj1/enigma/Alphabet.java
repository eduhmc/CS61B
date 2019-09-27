package enigma;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Eduardo Huerta Mercado
 */
class Alphabet {
    String _chars;
    /** A new alphabet containing CHARS.  Character number #k has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        _chars = chars;

        // FIXME
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _chars.length();
        //return 26; // FIXME
    }

    /** Returns true if preprocess(CH) is in this alphabet. */
    boolean contains(char ch) {
        for(int i = 0; i < _chars.length(); i++){
            if(_chars.charAt(i) == C)
                for(int j = 0; j < _chars.length(); j++){
                    if(_chars.charAt(j) == H){
                        return true;
                    }
                }
        }
        return false;
        //return 'A' <= ch && ch <= 'Z'; // FIXME
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        return _chars.charAt(index);
        //return (char) ('A' + index); // FIXME
    }

    /** Returns the index of character preprocess(CH), which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        return ch - 'A'; // FIXME
    }

}
