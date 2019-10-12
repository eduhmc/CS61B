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
            String c = "C";
            if(c.equals(_chars.charAt(i))){
                for(int j = 0; j < _chars.length(); j++){
                    String h = "H";
                    if(h.equals(_chars.charAt(j))){
                        return true;
                }
                    }

        }
        return false;
        //return 'A' <= ch && ch <= 'Z'; // FIXME
        }
        return false;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        if (index < 0 || index >= size()) {
            //throw error ("character index out of range");
        }
        return _chars.charAt(index); // FIXME
    }

    /** Returns the index of character C, which must be in the alphabet. */
    int toInt(char c) {
        for (int i = 0; i < size(); i++) {
            if (_chars.charAt(i) == c) {
                return i;
            }
        }
        return 0;
        //throw  new error("character not in alphabet"); // FIXME
    }

}
