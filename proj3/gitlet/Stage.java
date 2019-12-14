package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

/** This is the Stage class for the gitlet project.
 * @author eduhmc00
 */
public class Stage implements Serializable {

    /** This is the constructor for the class..
     * @param t is an argument.
     * @param d is an argument.
     */
    public Stage(TreeP t, Date d) {
        dicto = new HashSet<>();
        inexistente = new ArrayList<String>();
        calcard = Utils.sha1(d.toString() + t.tinmarin().nextDouble());
        repositorio = new File(".gitlet" + espacio + "stages" + espacio + calcard);
        repositorio.mkdirs();
    }

    /** Saves the stage object to the "stages" directory. */
    public void almacenando() {
        File myFile = new File(".gitlet" + espacio + "stages" + espacio + "stage" + calcard);
        Utils.writeObject(myFile, this);
    }

    /** A method that will help to save some things.
     * @param fin is the parameter of the function.
     */
    void guardando(Commit fin) {
        actual = fin;
        almacenando();
    }

    /** Method add, which simulates the add command.
     * @param estado lol
     */
    public void add(String estado) {
        File esto = new File(estado);
        if (!esto.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        if (inexistente.contains(estado)) {
            inexistente.remove(estado);
        }
        if (actual.agarrarelpasado().contains(estado)) {
            actual.agarrarelpasado().remove(estado);
        }
        if (actual.irAlPasado().containsKey(estado)) {
            File antes = new File(actual
                    .irAlPasado().get(estado));
            if (Utils.readContentsAsString(antes)
                    .equals(Utils.readContentsAsString(esto))) {
                File nuevo = new File(
                        repositorio.getPath() + espacio + estado);
                dicto.remove(estado);
                nuevo.delete();
                return;
            }
        }
        estado = cambiandochars(estado);
        File contador = new File(repositorio.getPath() + espacio + estado);
        if (dicto.contains(estado)) {
            contador.delete();
            dicto.remove(estado);
        }
        dicto.add(estado);
        String checking = Utils.readContentsAsString(new File(actual.anticambiandochars(estado)));
        Utils.writeContents(contador, checking);

        Utils.writeObject(new File(saying + actual.bearcard()), actual);
        almacenando();
    }

    /** This is a method that helps with the characters
     * @param x is the parameter
     * @return the new parameter x.
     */
    public String cambiandochars(String x) {
        if (x.contains("\\")) {
            x = x.replace("\\", "@");
        }
        return x;
    }

    /** This is a method that helps with the characters.
     * @param y is the parameter
     * @return the new parameter y.
     */
    public String anticambiandochars(String y) {
        if (y.contains("@")) {
            y = y.replace("@", "\\");
        }
        return y;
    }
    /** A method that checks other types of files.
     * @return some types of files.
     */
    public ArrayList<String> traerback() {
        return inexistente;
    }

    /** A method that gives you a number
     * @return a number.
     */
    public String bearcard() {
        return calcard;
    }

    /** A method to check where am I
     * @return something.
     */
    public File agarracommits() {
        return repositorio;
    }

    /** A method that checks a hashset
     * @return something.
     */
    public HashSet<String> getdicto() {
        return dicto;
    }

    /** A method got commiting
     * @return something
     */
    public Commit getactual() {
        return actual;
    }

    /** Variable that will help with the function add. */
    private Commit actual;

    /** HashSet variable that will help with the function add */
    private HashSet<String> dicto;

    /** A string that represents a number. */
    private String calcard;

    /** Variable that will help with the function add. */
    private static String espacio = File.separator;

    /** Variable that will help with the function add */
    private File repositorio;

    /** Variable that will help with the function add. */
    private ArrayList<String> inexistente;

    /** Variable that will help with the function add. */
    private String saying = ".gitlet" + espacio + "objectRepository" + espacio;

    @Override
    public String toString() {
        StringBuilder myStringrepr = new StringBuilder();
        myStringrepr.append("----- Staged Files ----- \n");
        myStringrepr.append("size: " + dicto.size() + "\n");
        for (String files: dicto) {
            myStringrepr.append(files + "\n");
        }
        myStringrepr.append(dicto.hashCode() + "\n");
        myStringrepr.append("----- Current Commit ----- \n");
        return myStringrepr.toString();
    }
}

