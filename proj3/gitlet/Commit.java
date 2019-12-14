package gitlet;


import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;


/** his is the commit class for the Gitlet project.
 * @author eduhmc
 */

public class Commit implements Serializable {
    /** Constructor method for the commit class.
     * @param ya is a parameter of the function.
     * @param quiero is a parameter of the function. 
     * @param que is a parameter of the function.
     * @param termine is a parameter of the function.
     * @param esto is a parameter of the function.
     */
    public Commit(String ya, Date quiero, TreeP que,
                  Stage termine, String esto) {
        imprenta = ya; horacio = quiero;
        calcaterra = quiero.toString().substring(0, numa) + quiero.toString().substring(numb) + " -0800";
        personal = que; diccionario = new HashMap<>(); antiguodictc = new HashMap<>();
        perdidos = new HashSet<>();
        papas = new ArrayList<>(); estado = termine;
        calcard = "commit" + termine.bearcard(); plonyo = null;
        decidir(esto);
        Utils.writeObject(new File(casita + calcard), this);
    }

    /** Saves changes made to this commit. */
    public void salvando() {
        Utils.writeObject(new File(casita + calcard), this);
    }

    /** Sets my parent commit to this commit.
     * @param dni the previous commit leading ot this one.
     */
    public void decidir(String dni) {
        if (dni == null) {
            plonyo = null;
            return;
        }
        Commit iva = Utils.readObject(new File( casita + dni), Commit.class);
        plonyo = iva.bearcard();
        for (String keys: iva.antiguodictc.keySet()) {  antiguodictc.put(keys, iva.antiguodictc.get(keys));
        }
        for (String keys: iva.atrapararchivo().keySet()) {
            iva.cambiandochars(keys);
            diccionario.put(keys, iva.atrapararchivo().get(keys));
        }
        perdidos.addAll(iva.agarrarelpasado());
        papas.add(dni);
    }

    /** A method that Initiliazes the repository.*/
    public void añadir() {
        File directorio = new File(casita + "folder" + calcard + espacio);
        directorio.mkdirs(); File lugar = new File(casita + calcard);
        if (plonyo == null) {
            Utils.writeObject(lugar, this);
        } else {
            Commit hola = Utils.readObject(new File(casita + plonyo), Commit.class);
            String esto = ".gitlet" + espacio + "stages" + espacio + estado.bearcard() + espacio;

            for (String fileName: Utils.plainFilenamesIn(esto)) {
                if (estado.getdicto().contains(fileName) && !perdidos.contains(anticambiandochars(fileName))) {
                    String ahorita = Utils.readContentsAsString(
                            new File(esto + fileName));

                    if (hola.irAlPasado().containsKey(fileName)) {
                        File antiguo = new File(hola.irAlPasado().get(fileName));
                        String predecesor = Utils.readContentsAsString(antiguo);

                        if (!ahorita.equals(predecesor)) {
                            antiguodictc.replace(fileName, directorio.getPath() + espacio + fileName);
                            File este = new File(anticambiandochars(fileName)); String actual = Utils.readContentsAsString(este);
                            diccionario.replace(fileName, Utils.sha1(esto + fileName));
                            Utils.writeContents(new File(directorio.getPath() + espacio + fileName), actual);
                        }
                    } else {
                        antiguodictc.put(fileName, directorio.getPath() + espacio + fileName);
                        File aquel = new File(esto + fileName); String contenido = Utils.readContentsAsString(aquel);
                        diccionario.put(fileName, Utils.sha1(esto + fileName));
                        Utils.writeContents(new File(directorio.getPath() + espacio + fileName), contenido);
                    }
                }
            }
            Utils.writeObject(lugar, this);
        }
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
    /** A function that gives you a hashset.
     * @return a hashset of files that are lost
     */
    public HashSet<String> agarrarelpasado() {
        return perdidos;
    }

    /** A method that helps with commit.
     * @return a dictonary.
     */
    public Map<String, String> irAlPasado() {
        return antiguodictc;
    }
    /** Get method for my commit ID. Should be in the format:
     * "commit[sha1 of stage at the moment of my creation]"
     * @return a number.
     */
    public String bearcard() {
        return calcard;
    }

    /** A function that return a message
     * @return stated above
     */
    public String imprimiendo() {
        return imprenta;
    }
    /** A method that works as a calendar
     * @return a date based as a string
     */
    public Date rolex() {
        return horacio;
    }

    /** A method that reports a string
     * @return something.
     */
    public String devolverpapa() {
        return plonyo;
    }
    /** A method that works as part of the commit class
     * @return depends on the statemtent.
     */
    public Commit estadodefamlista() {
        File coneplonyo = new File(casita + plonyo);
        try {
            Commit retCommit = Utils.readObject(coneplonyo, Commit.class);
            return retCommit;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /** A method that Updates. */
    public void estadodegit() {
        checker = true;
        Utils.writeObject(new File(casita + calcard), this);
    }

    /** A method that carries a variable as an arraylist.
     * @return a list
     */
    public ArrayList<String> famlista() {
        return papas;
    }

    /** A method thar works as a calendar.
     * @return number as a string.
     */
    public String reloj() {
        return calcaterra;
    }

    /** A method associated with the class TreeP
     * @return a tree
     */
    public TreeP devuelveme() {
        return personal;
    }

    /** A method thar reports where I am.
     * @return something
     */
    public Stage atraparestado() {
        return estado;
    }

    /** A method that return a dictonary
     * @return stated above.
     */
    public Map<String, String> atrapararchivo() {
        return diccionario;
    }

    /** A number. */
    private String calcard;

    /** Variable to help with spaces */
    private String espacio = File.separator;

    /** The place where it lives */
    private String casita = ".gitlet" + espacio + "objectRepository" + espacio;

    /** A variable that works as a dictonary. */
    private Map<String, String> antiguodictc;

    /** A variable that works as a dictonary with unique items. */
    private HashSet<String> perdidos;

    /** A variable that works as a dictonary */
    private Map<String, String> diccionario;

    /** A number. */
    private static final int numa = 19;

    /** A number */
    private static final int numb = 23;

    /** A variable that works as a checker. */
    private boolean checker = false;

    /** A variable that works as a list */
    private ArrayList<String> papas;
    
    /** A variable that is a String. */
    private String calcaterra;
    
    /** A variable that works as a Tree. */
    private TreeP personal;

    /** A variable that is a string. */
    private String imprenta;

    /** A variable that indicates the date. */
    private Date horacio;

    /** A variable that is a String. */
    private String plonyo;

    /** A variable that indicates where I am.  */
    private Stage estado;
    

    /** Overriding the method toString
     * @return something to report.
     */
    @Override
    public String toString() {
        StringBuilder myStringRepr = new StringBuilder();
        myStringRepr.append("===\n");
        myStringRepr.append("commit " + calcard.substring(6) + "\n");
        if (checker) {
            myStringRepr.append("Merge:");
            for (String parents: papas) {
                myStringRepr.append(" " + parents.substring(7, 14));
            }
            myStringRepr.append("\n");
        }
        myStringRepr.append("Date: " + calcaterra + "\n");
        myStringRepr.append(imprenta + "\n");
        return myStringRepr.toString();
    }
}
