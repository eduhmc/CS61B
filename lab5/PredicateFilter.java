import java.util.Iterator;
import utils.Predicate;
import utils.Filter;

/** A kind of Filter that tests the elements of its input sequence of
 *  VALUES by applying a Predicate object to them.
 *  @author Eduardo Huerta-Mercado
 */
class PredicateFilter<Value> extends Filter<Value> {

    /** A filter of values from INPUT that tests them with PRED,
     *  delivering only those for which PRED is true. */
    PredicateFilter(Predicate<Value> pred, Iterator<Value> input) {
        super(input); //FIXME ??
        newpred = pred;
        // FIXME: REPLACE THIS LINE WITH YOUR CODE
    }

    @Override
    protected boolean keep() {
        if(newpred.test(candidateNext())) {
            return true;
        } else {
            return false;
        }
    }
    private Predicate<Value> newpred;
    //return false;  // FIXME: REPLACE THIS LINE WITH YOUR CODe¡
    // FIXME: REPLACE THIS LINE WITH YOUR CODE
}
