package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Collections;


/** Main class for gitlet project.
 *  @author eduhmc
 */
public class Main implements Serializable {

    /** A method that works as an temporary list.
     * @return An arrayist
     */
    private static ArrayList<String> ayuda() {
        ArrayList<String> porahora = new ArrayList<>();
        porahora.addAll(Utils.plainFilenamesIn(FileSystems.getDefault().getPath(".").toString()));
        porahora.remove(".gitignore"); porahora.remove("Makefile");
        porahora.remove("proj3.iml");
        return porahora;
    }

    /** A method that doesn't return anything.
     */
    static void saveDirectory() {
        if (junta == null) {
            return;
        }
        try {
            File f = new File(".gitlet" + espacio + "repo" + espacio);
            ObjectOutputStream dirStream =
                    new ObjectOutputStream(new FileOutputStream(f));
            dirStream.writeObject(junta);
            dirStream.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /** A method from the class TreeP
     * @return depends on the statement
     */
    static TreeP loadDirectory() {
        File nuevo = new File(".gitlet" + espacio + "repo" + espacio);
        try {
            TreeP retTree = Utils.readObject(nuevo, TreeP.class);
            return retTree;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /** A method that checks
     * @param sera works as the checker
     * @return True or False
     */
    private static boolean deleteRepository(File sera) {
        if (!sera.exists()) {
            System.out.println("all good");
            return false;
        }
        File[] lst = sera.listFiles();
        if (lst != null) {
            for (File file : lst) {
                deleteRepository(file);
            }
        }
        return sera.delete();
    }


    /** The method that works with the init command
     */
    static void init() {
        File iniciando = new File(".gitlet" + espacio);
        File este = new File(".gitlet" + espacio + "stages");
        File caja = new File(".gitlet" + espacio + "objectRepository");

        if (iniciando.exists()) {
            System.out.println(" A Gitlet version-control system already exists in the current directory.");
            return;
        }
        iniciando.mkdirs(); este.mkdirs(); caja.mkdirs();
        junta = new TreeP();
        Date actual = new Date();
        actual.setTime(0);
        Stage estadodeam= new Stage(junta, actual);
        Commit initCommit = new Commit("initial commit", actual, junta, estadodeam, null);
        initCommit.añadir();
        estadodeam.guardando(initCommit);
        Stage prox = new Stage(junta, new Date());
        prox.guardando(initCommit);
        Branch nuevoperro = new Branch("master", initCommit, junta);
        junta.getcurr().put(nuevoperro.agarrar(), nuevoperro);
        junta.obteniendorama(nuevoperro);
        nuevoperro.cambios(initCommit);
        nuevoperro.mascambios(prox);
    }

    /** Method add, which simulates the add command.
     * @param estado lol
     */
    static void add(String estado) {
        Commit headCommit = junta.agarrandorama().agarralider();
        junta.agarrandorama().fixing().add(estado);
        if (headCommit.agarrarelpasado().contains(estado)) {
            headCommit.agarrarelpasado().remove(estado);
        }
        headCommit.salvando();
    }

    /** A function that helps with the commit keyword
     * @param impreso refers to a line of text
     */
    static void commit(String impreso) {
        Date aqui = new Date(); Branch alla = junta.agarrandorama();
        Stage atras = new Stage(junta, aqui);
        Stage actual = alla.fixing();
        Commit corriente = alla.agarralider();

        if (impreso.equals("")) {
            System.out.println("Please enter a commit message.");
            return;
        } else if (alla.fixing().getdicto().size() == 0) {
            try {
                Commit coneplonyo = corriente.estadodefamlista();
                boolean brand = false;
                if (coneplonyo.agarrarelpasado().size() != corriente.agarrarelpasado().size()) {
                    brand = true;
                } else {
                    for (String fileName: coneplonyo.agarrarelpasado()) {
                        if (!corriente.agarrarelpasado().contains(fileName)) {
                            brand = true;
                        }
                    }
                }
                if (!brand) {
                    System.out.println("No changes added to the commit.");
                    return;
                }
            } catch (NullPointerException e) {
                System.out.println("No changes added to the commit.");
                return;
            }
        }
        Commit deal = new Commit(impreso, aqui, junta, actual, corriente.bearcard());
        deal.añadir();
        alla.cambios(deal);
        junta.getcommiteando().add(deal.bearcard());
        alla.mascambios(atras);
        atras.guardando(deal);
    }

    /** A method that helps with the commit class.
     * @param master parameter of the function
     * @param edu parameter of the function
     */
    private static void stanford(String edu, Commit master) {
        edu = master.cambiandochars(edu);
        if (master.irAlPasado() == null || !master.irAlPasado().containsKey(edu)
                || (master.estadodefamlista().agarrarelpasado().contains(edu)) && !master.irAlPasado().containsKey(edu)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        File oldVersion = new File(master.irAlPasado().get(edu));

        edu = master.anticambiandochars(edu);
        Utils.writeContents(new File(edu), Utils.readContentsAsString(oldVersion));
    }


    /** A method that helps with the commit class.
     * @param calcard parameter of the function
     * @param apellido parameter of the function
     */
    private static void berkeley(String calcard, String apellido) {
        if (!calcard.contains("commit")) {
            calcard = "commit" + calcard;
        }
        File este = new File(pintura + calcard);
        if (!este.exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit aquel = Utils.readObject(new File(pintura + calcard), Commit.class);
        apellido = aquel.cambiandochars(apellido);
        if (!aquel.atrapararchivo().containsKey(apellido) || aquel.agarrarelpasado().contains(apellido)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        File oldVersion = new File(aquel.irAlPasado().get(apellido));
        apellido = aquel.anticambiandochars(apellido);
        Utils.writeContents(new File(apellido), Utils.readContentsAsString(oldVersion));
    }

    /** A method that helps with the commit class.
     * @param rama parameter of the function
     */
    private static void fibonacci(String rama) {
        if (!junta.getcurr().keySet().contains(rama)) {
            System.out.println("No such branch exists.");
            return;
        } else if (junta.agarrandorama().agarrar().equals(rama)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        Branch actual = junta.getcurr().get(rama);
        Branch este = junta.agarrandorama();
        for (String s: actual.agarralider().irAlPasado().keySet()) {
            File salir = new File(actual.agarralider().irAlPasado().get(s));
            File pisado;
            try {
                pisado = new File(este.agarralider().irAlPasado().get(s));
            } catch (NullPointerException ignored) {
                pisado = new File(actual.agarralider().irAlPasado().get(s));
            }
            File porahora = new File(este.agarralider().anticambiandochars(s));
            if (porahora.exists() && (!Utils.readContentsAsString(salir).equals(Utils.readContentsAsString(porahora))
                    && !Utils.readContentsAsString(porahora).equals(Utils.readContentsAsString(pisado)))) {
                System.out.println("There is an untracked file in the way; delete it or add it first.");
                return;
            }
        }
        ArrayList<File> lst = new ArrayList<>();
        for (String x: este.agarralider().irAlPasado().keySet()) {
            if (!actual.agarralider().irAlPasado().containsKey(x)) {
                File walmart = new File(x);
                lst.add(walmart);
            }
        }

        for (File f: lst) {
            f.delete();
        }

        for (String x: este.agarralider().irAlPasado().keySet()) {
            x = este.agarralider().anticambiandochars(x);
            stanford(x, este.agarralider());
        }

        junta.obteniendorama(este);
    }

    /** usleess method that only does sout
     */
    private static void log() {
        Commit actual = junta.agarrandorama().agarralider();
        while (actual != null) {
            System.out.println(actual.toString());
            actual = actual.estadodefamlista();
        }
    }

    /** Void method
     * @param s is the parameter
     */
    private static void rm(String s) {
        Branch actual = junta.agarrandorama();
        Commit presente= actual.agarralider();
        String esteno = presente.cambiandochars(s);
        Stage corriente = actual.fixing();
        String rubik = cuadro + corriente.bearcard() + espacio;
        if (!presente.irAlPasado().containsKey(esteno) && !Utils.plainFilenamesIn(rubik).contains(esteno)) {
            System.out.println("No reason to remove the file.");
            return;
        }
        if (presente.irAlPasado().keySet().contains(s)) {
            presente.agarrarelpasado().add(s);
        }
        if (Utils.plainFilenamesIn(rubik).contains(esteno)) {
            File mio = new File(rubik + esteno);
            mio.delete();
        }
        corriente.getdicto().remove(esteno);
        corriente.traerback().add(s);
        corriente.almacenando();
        if (presente.irAlPasado().containsKey(esteno)) {
            String antiq = presente.irAlPasado().get(esteno);
            File pusado = new File(antiq);
            File estesi = new File(s);
            if (estesi.exists() && Utils.readContentsAsString(pusado).equals(Utils.readContentsAsString(estesi))) {
                try {
                    Utils.restrictedDelete(estesi);
                } catch (IllegalArgumentException e) {
                    estesi.delete();
                }
            }
        }
        Utils.writeObject(new File(pintura + presente.bearcard()), presente);
    }

    /** Log method
     */
    private static void globalLog() {
        for (String commitNames: Utils.plainFilenamesIn(pintura)) {
            File thisFile = new File(pintura + commitNames);
            System.out.println(Utils.readObject(thisFile, Commit.class));
        }
    }

    /** A method that helps with commit
     * @param edu is the parameter
     */
    private static void find(String edu) {
        boolean fcheck = false;
        for (String c: Utils.plainFilenamesIn(pintura)) {
            File este = new File(pintura + c);
            Commit thisCommit = Utils.readObject(este, Commit.class);
            if (thisCommit.imprimiendo().equals(edu)) {
                System.out.println(thisCommit.bearcard().substring(6));
                fcheck = true;
            }
        }
        if (!fcheck) {
            System.out.println("Found no commit with that message.");
            return;
        }
    }


    /** A method that helps with commit
     */
    private static void status() {
        ArrayList<String> lst = new ArrayList<>();
        String este = "";
        System.out.println("=== Branches ===");
        for (Branch i: junta.getcurr().values()) {
            if (i == junta.agarrandorama()) {
                este = "*" + este;
            }
            este = este + i.agarrar();
            lst.add(este);
            este = "";
        }
        Collections.sort(lst);
        for (String files: lst) {
            System.out.println(files);
        }
        System.out.println();
        lst.clear();
        System.out.println("=== Staged Files ===");
        Stage currStage = junta.agarrandorama().fixing();
        Commit currCommit = junta.agarrandorama().agarralider();
        for (String stagedFiles: currStage.getdicto()) {
            lst.add(currCommit.anticambiandochars(stagedFiles));
        }
        Collections.sort(lst);
        for (String files: lst) {
            System.out.println(files);
        }
        System.out.println();
        lst.clear();
        System.out.println("=== Removed Files ===");
        lst.addAll(junta.agarrandorama().agarralider()
                .atraparestado().traerback());
        Collections.sort(lst);
        for (String files: lst) {
            System.out.println(files);
        }
        System.out.println();
        lst.clear();
        System.out.println("=== Modifications Not Staged For Commit ===");
        cambiasso(currStage, currCommit, este, lst);
    }

    /** void method
     * @param a lol
     * @param e lol
     * @param fi lol
     * @param o Tpatam
     */
    private static void cambiasso(Stage a, Commit e, String fi, ArrayList<String> o) {
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    /** Void method
     * @param edu args.
     */
    private static void branch(String edu) {

        if (junta.getcurr().containsKey(edu)) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        Branch prox = new Branch(edu, junta.agarrandorama().agarralider(), junta);
        Stage este = new Stage(junta, new Date());
        este.guardando(junta.agarrandorama().agarralider());
        junta.getcurr().put(prox.agarrar(), prox);
        prox.mascambios(este);
    }

    /** Void method
     * @param edu is the argument.
     */
    private static void removeBranch(String edu) {
        if (!junta.getcurr().containsKey(edu)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (junta.agarrandorama().agarrar().equals(edu)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        junta.getcurr().remove(edu);
    }

    /** void method
     * @param calc targ
     */
    private static void reset(String calc) {
        if (!junta.getcommiteando().contains("commit" + calc)) {
            System.out.println("No commit with that id exists.");
            return;
        }

        Branch esy = junta.agarrandorama();
        Commit proximo = esy.agarralider();
        Stage sueño = esy.fixing();
        Commit patras = Utils.readObject(new File(pintura + "commit" + calc), Commit.class);
        for (String i: patras.irAlPasado().keySet()) {
            if (proximo.agarrarelpasado().contains(i)) {
                System.out.println("There is an untracked file in the way; delete it or add it first.");
                return;
            }
        }
        for (String i: proximo.irAlPasado().keySet()) {
            if (!patras.irAlPasado().containsKey(i)) {
                new File(proximo.anticambiandochars(i)).delete();
            }
        }
        for (String i: patras.irAlPasado().keySet()) {
            String revProcessedFileName = proximo.anticambiandochars(i);
            Utils.writeContents(new File(revProcessedFileName), Utils.readContentsAsString(new File(patras.irAlPasado().get(i))));
        }
    }


    /** A method that helps with the commit class
     * @param vista args
     * @param hasta args
     * @return something
     */
    private static Commit puntodequiebre(Commit hasta, Commit vista) {
        HashSet<String> lst = new HashSet<>();
        Commit contador= vista;
        while (contador != null) {
            lst.add(contador.bearcard());
            contador = contador.estadodefamlista();
        }
        while (!lst.contains(hasta.bearcard())) {
            for (String x: hasta.famlista()) {
                Commit esto = Utils.readObject(new File(pintura + x), Commit.class);
                if (lst.contains(esto)) {
                    return esto;
                }
            }
            hasta = hasta.estadodefamlista();
        }
        return hasta;
    }

    /** Void method
     * @param moña args
     */
    private static void merge(String moña) {
        if (!junta.getcurr().containsKey(moña)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        Branch actual = junta.agarrandorama();
        Branch chequeo = junta.getcurr().get(moña);
        Commit este = actual.agarralider();
        Commit acabar = chequeo.agarralider();
        if (actual.agarrar().equals(moña)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }
        Commit quiebre = puntodequiebre(este, acabar);
        if (quiebre.bearcard().equals(acabar.bearcard())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        } else if (quiebre.bearcard().equals(chequeo.liderdefila())) {
            junta.obteniendorama(chequeo);
            System.out.println("Current branch fast-forwarded.");
            return;
        }
        if (!Utils.plainFilenamesIn(actual.fixing().agarracommits()).isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return;
        }
        for (String f: quiebre.irAlPasado().keySet()) {
            boolean ptm = true;
            File csm = new File(quiebre.irAlPasado().get(f));
            try {
                File crjo = new File(este.irAlPasado().get(f));
                File alv = new File(quiebre.irAlPasado().get(f));
                if (Utils.readContentsAsString(csm).equals(Utils.readContentsAsString(crjo)) && !alv.exists()) {
                    if (!este.irAlPasado().containsKey(f)) {
                        System.out.println("There is an untracked file in the way; delete it or add it first.");
                        return;
                    }
                    Utils.restrictedDelete(este.anticambiandochars(f));
                }
            } catch (NullPointerException e) {
                ptm = false;
            }
        }
        union(actual, chequeo, este, acabar, quiebre);
    }

    /** Void method
     * @param roger it
     * @param rafa be
     * @param nole like
     * @param tipsa that
     * @param andy sometimes
     */
    private static void union(Branch roger, Branch rafa, Commit nole, Commit tipsa, Commit andy) {
        boolean check = false;
        for (String x: nole.irAlPasado().keySet()) {
            File csm = new File(nole.irAlPasado().get(x));
            File crj, ptm;
            try {
                crj = new File(tipsa.irAlPasado().get(x));
            } catch (NullPointerException ignored) {
                crj = null;
            }
            try {
                ptm = new File(andy.irAlPasado().get(x));
            } catch (NullPointerException ignored) {
                ptm = null;
            }
            x = tipsa.anticambiandochars(x);
            if (ptm == null && crj == null) {
                berkeley(nole.bearcard(), x);
                add(x);
            } else if (ptm != null && crj != null) {
                if ((!Utils.readContentsAsString(csm).equals(Utils.readContentsAsString(ptm)) || nole.estadodefamlista().agarrarelpasado().contains(x)) && Utils.readContentsAsString(crj).equals(Utils.readContentsAsString(ptm))) {
                    if (nole.estadodefamlista()
                            .agarrarelpasado().contains(x)) {
                        Utils.restrictedDelete(
                                tipsa.anticambiandochars(x));
                    } else {
                        berkeley(nole.bearcard(), x);
                        add(x);
                    }
                } else if (!Utils.readContentsAsString(csm)
                        .equals(Utils.readContentsAsString(crj))) {
                    String buda = "<<<<<<< HEAD\n";
                    buda = buda + Utils.readContentsAsString(crj);
                    buda = buda + "=======\n";
                    buda = buda + Utils.readContentsAsString(csm);
                    buda = buda + ">>>>>>>";
                    Utils.writeContents(new File(x), buda);
                    check = true;
                }
            }
        }
        juntasa(rafa, roger, check);
    }

    /** Void method
     * @param messi args
     * @param cr7 args
     * @param ney args
     */
    private static void juntasa(Branch messi, Branch cr7, boolean ney) {
        commit("Merged " + messi.agarrar() + " into " + cr7.agarrar() + ".");
        junta.agarrandorama().agarralider().estadodegit();
        Commit este = junta.agarrandorama().agarralider();
        este.famlista().add(messi.liderdefila());
        Utils.writeObject(new File(pintura + este.bearcard()), este);
        if (ney) {
            System.out.println("Encountered a merge conflict.");
        }
    }


    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <dormir> .... */
    public static void main(String... args) {
        String quiero = args[0]; String[] dormirs = null;
        junta = loadDirectory();
        try {
            dormirs = Arrays.copyOfRange(args, 1, args.length);
        } catch (IndexOutOfBoundsException ignored) {
            dormirs = dormirs;
        }
        try {
            if (quiero.equals("init")) {
                init();
            } else if (quiero.equals("add")) {
                add(dormirs[0]);
            } else if (quiero.equals("commit")) {
                commit(dormirs[0]);
            } else if (quiero.equals("checkout")) {
                List<String> dormirList;
                try {
                    dormirList = Arrays.asList(dormirs);
                } catch (NullPointerException e) {
                    throw new IndexOutOfBoundsException();
                }
                if (!dormirList.contains("--") && dormirList.size() == 1) {
                    fibonacci(dormirs[0]);
                } else if (dormirs[0].equals("--")) {
                    stanford(dormirs[1], junta.agarrandorama().agarralider());
                } else if (dormirs[1].equals("--")) {
                    berkeley(dormirs[0], dormirs[2]);
                } else {
                    throw new IndexOutOfBoundsException();
                }
            } else if (quiero.equals("log")) {
                log();
            } else if (quiero.equals("rm")) {
                rm(dormirs[0]);
            } else if (quiero.equals("global-log")) {
                globalLog();
            } else if (quiero.equals("find")) {
                find(dormirs[0]);
            } else if (quiero.equals("status")) {
                status();
            } else if (quiero.equals("branch")) {
                branch(dormirs[0]);
            } else if (quiero.equals("rm-branch")) {
                removeBranch(dormirs[0]);
            } else if (quiero.equals("reset")) {
                reset(dormirs[0]);
            } else if (quiero.equals("merge")) {
                merge(dormirs[0]);
            }
        } catch (NullPointerException e) {
            System.out.println("Not in an initialized Gitlet directory.");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Incorrect operand.");
        }
        saveDirectory();
    }



    /** variable */
    private static String espacio = File.separator;

    /** variable */
    private static String pintura = ".gitlet" + espacio + "objectRepository" + espacio;

    /** variable */
    private static String cuadro = ".gitlet" + espacio + "stages" + espacio;

    /** variable */
    private static TreeP junta = null;
    /** Treep method
     * @return something
     */
    public static TreeP getDirectory() {
        File nuevo = new File(".gitlet" + espacio + "repo");
        return Utils.readObject(nuevo, TreeP.class);
    }
}