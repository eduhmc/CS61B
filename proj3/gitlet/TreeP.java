package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;


/** Gitlet commit class
 * @author eduhmc
 */
public class TreeP implements Serializable {

    /** Constructor method 
     */
    public TreeP() {
        commiteando= new HashSet<>(); curr = new HashMap<>();
    }

    /** A method that grabs a commit and set it
     * @param x is the parameter for the function.
     */
    public void obteniendorama(Branch x) {
        ahorita  = x;
    }

    /** A method that grabs a commit and set it
     * @return where we are at.
     */
    public Branch agarrandorama() {
        return ahorita;
    }

    /** A method that grabs a commit and set it.
     * @return a number..
     */
    public Random tinmarin() {
        return alazar;
    }

    /** A method that grabs a commit and set it.
     * @return something.
     */
    public HashSet<String> getcommiteando() {
        return commiteando;
    }

    /** A method that grabs a commit and set it.
     * @return something cool.
     */
    public Map<String, Branch> getcurr() {
        return curr;
    }

    /** A dictonary. */
    private Map<String, Branch> curr;

    /** random numbersss. */
    private static Random alazar = new Random(1);

    /** a variable. */
    private Branch ahorita;

    /** Commiteando acts like a dictionary. */
    private HashSet<String> commiteando;

    /** a letter. */
    private char edu = File.separatorChar;
}