import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/** A set of String values.
 *  @author eduhmc
 */
class ECHashStringSet implements StringSet{
    private static double MIN_LOAD = 0.2;
    private static double MAX_LOAD = 5;

    @SuppressWarnings("unchecked")
    public ECHashStringSet(int numBuckets) {
        _size = 0;
        _store = (LinkedList<String>[]) new LinkedList[numBuckets];
        for (int i = 0; i < numBuckets; i = i + 1) {
            _store[i] = new LinkedList<String>();
        }
    }

    public ECHashStringSet() {
        this((int) (1/MIN_LOAD));
    }

    @Override
    public void put(String s) {
        _size = _size + 1;
        if (s != null) {
            if (_size > _store.length * MAX_LOAD) {
                resize();
            }

            _hashcode = helper(s);
            if (!_store[_hashcode].contains(s)) {
                _store[_hashcode].add(s);
            }
        }
    }

    @Override
    public boolean contains(String s) {
        return _store[helper(s)].contains(s);
    }

    @Override
    public List<String> asList() {
        ArrayList<String> resultado = new ArrayList<>();
        for (int i = 0; i < _store.length; i = i + 1) {
            if (_store[i] != null) {
                for (int j = 0; j < _store[i].size(); j = j + 1) {
                    resultado.add(_store[i].get(j));
                }
            }
        }
        return resultado;
    }

    public int size() {
        return _size;
    }

    public void resize() {
        List bucket = asList();
        _store = new LinkedList[_store.length * 2];

        for (int i = 0; i < _store.length; i++) {
            _store[i] = new LinkedList<String>();
        }

        for (Object x : bucket) {
            String chara = (String) x;
            int track = helper(chara);
            _store[track].add(chara);
        }
    }

    private int helper(String s) {

        return (s.hashCode() & 0x7fffffff) % _store.length;
    }
    /**Size of the key*/
    private int _size;

    private int _hashcode;

    private LinkedList<String>[] _store;
}
