package gitlet;

import java.io.File;
import java.io.Serializable;

/** This is the branch class for the gitlet project
 * @author eduhmc
 */

public class Branch implements Serializable {

    /** Constructor
     * @param str lol.
     * @param federer lol.
     * @param lima lol.
     */
    public Branch(String str, Commit federer, TreeP lima) {
        eduardo = str;
        lider = federer.getMyID();
        checker = federer.getMyStage().getMyID();
        pepe = lima;
        calcard = Utils.sha1(federer.getMyDateStr()
                + pepe.getRandomGen().nextDouble());
        lima.getcurr().put(str, this);
    }

    /** changes
     * @param enigma changes status
     */
    public void cambios(Commit enigma) {
        lider = enigma.getMyID();
    }

    /** Changes.
     * @param animo changes animo.
     */
    public void mascambios(Stage animo) {
        checker = animo.getMyID();
    }

    /** Function
     * @return name
     */
    public String agarrar() {
        return eduardo;
    }


    /** A method
     * @return something.
     */
    public String liderdefila() {
        return lider;
    }

    /** Another method
     * @return something.
     */
    public Commit agarralider() {
        File variable = new File(".gitlet" + navidad
                + "objectRepository" + navidad +lider);
        return Utils.readObject(variable, Commit.class);
    }


    /** A method.
     * @return something
     */
    public Stage fixing() {
        File mgone = new File(".gitlet" + navidad + "stages" + navidad + "stage" + checker);

        return Utils.readObject(mgone, Stage.class);
    }

    /** A method.
     * @return the TreeP
     */
    public TreeP getMyParent() {
        return pepe;
    }

    /** A method.
     * @return something
     */
    public String bearcard() {
        return calcard;
    }

    /** The separator symbol.
     */
    private char navidad = File.separatorChar;
    /** Random name.
     */
    private String eduardo;
    /** Head of the branch.
     */
    private String lider;
    /** A variable that represents the ID.
     */
    private String calcard;
    /** A variable
     */
    private TreeP pepe;
    /** A variable
     */
    private String checker;


    /** Method for strings
     * @return something
     */
    @Override
    public String toString() {
        StringBuilder myStringRepr = new StringBuilder();
        myStringRepr.append("----- MY NAME ----- \n");
        myStringRepr.append(eduardo + "\n");
        myStringRepr.append("----- MY ID ----- \n");
        myStringRepr.append(calcard + "\n");
        myStringRepr.append("----- MY STAGE ----- \n");
        myStringRepr.append(checker.toString());
        myStringRepr.append("\n");
        myStringRepr.append("----- HEAD COMMIT ----- \n");
        myStringRepr.append(lider.toString() + "\n");
        myStringRepr.append("\n");
        return myStringRepr.toString();
    }
}

