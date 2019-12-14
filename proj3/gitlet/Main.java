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
    private static ArrayList<String> workingDirFiles() {
        ArrayList<String> porahora = new ArrayList<>();
        porahora.addAll(Utils.plainFilenamesIn(FileSystems.getDefault().getPath(".").toString()));
        porahora.remove(".gitignore"); porahora.remove("Makefile");
        porahora.remove("proj3.iml");
        return porahora;
    }

    /** A method that doesn't return anything.
     */
    static void saveDirectory() {
        if (directory == null) {
            return;
        }
        try {
            File f = new File(".gitlet" + separator + "repo" + separator);
            ObjectOutputStream dirStream =
                    new ObjectOutputStream(new FileOutputStream(f));
            dirStream.writeObject(directory);
            dirStream.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /** A method from the class TreeP
     * @return depends on the statement
     */
    static TreeP loadDirectory() {
        File nuevo = new File(".gitlet" + separator + "repo" + separator);
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
        File iniciando = new File(".gitlet" + separator);
        File este = new File(".gitlet" + separator + "stages");
        File caja = new File(".gitlet" + separator + "objectRepository");

        if (iniciando.exists()) {
            System.out.println(" A Gitlet version-control system already exists in the current directory.");
            return;
        }
        iniciando.mkdirs(); este.mkdirs(); caja.mkdirs();
        directory = new TreeP();
        Date actual = new Date();
        actual.setTime(0);
        Stage estadodeam= new Stage(directory, actual);
        Commit initCommit = new Commit("initial commit", actual, directory, estadodeam, null);
        initCommit.añadir();
        estadodeam.guardando(initCommit);
        Stage prox = new Stage(directory, new Date());
        prox.guardando(initCommit);
        Branch nuevoperro = new Branch("master", initCommit, directory);
        directory.getcurr().put(nuevoperro.agarrar(), nuevoperro);
        directory.obteniendorama(nuevoperro);
        nuevoperro.cambios(initCommit);
        nuevoperro.mascambios(prox);
    }

    /** Method add, which simulates the add command.
     * @param estado lol
     */
    static void add(String estado) {
        Commit headCommit = directory.agarrandorama().agarralider();
        directory.agarrandorama().fixing().add(estado);
        if (headCommit.agarrarelpasado().contains(estado)) {
            headCommit.agarrarelpasado().remove(estado);
        }
        headCommit.salvando();
    }

    /** A function that helps with the commit keyword
     * @param impreso refers to a line of text
     */
    static void commit(String impreso) {
        Date aqui = new Date(); Branch alla = directory.agarrandorama();
        Stage atras = new Stage(directory, aqui);
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
        Commit deal = new Commit(impreso, aqui,
                directory, actual, corriente.bearcard());

        deal.añadir();
        alla.cambios(deal);
        directory.getcommiteando().add(deal.bearcard());
        alla.mascambios(atras);
        atras.guardando(deal);
    }

    /** A method that helps with the commit class.
     * @param master parameter of the function
     * @param edu parameter of the function
     */
    private static void checkoutName(String edu, Commit master) {
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


    /** Checkout method, where we retrieve only the file
     * with the name specified from the specified commit.
     * folder.
     * @param commitID the ID of the commit we want to
     *                 checkout our file from.
     * @param fileName the name of the file we want.
     */
    private static void checkoutID(String commitID, String fileName) {
        if (!commitID.contains("commit")) {
            commitID = "commit" + commitID;
        }
        File desiredCommitFile = new File(objectRepo + commitID);

        if (!desiredCommitFile.exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }

        Commit desiredCommit =
                Utils.readObject(new File(objectRepo + commitID), Commit.class);

        fileName = desiredCommit.cambiandochars(fileName);
        if (!desiredCommit.atrapararchivo().containsKey(fileName)
                || desiredCommit.agarrarelpasado().contains(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        File oldVersion = new File(desiredCommit
                .irAlPasado().get(fileName));

        fileName = desiredCommit.anticambiandochars(fileName);
        Utils.writeContents(new File(fileName),
                Utils.readContentsAsString(oldVersion));
    }

    /** Checkout method, where we retrieve everything
     * from the headcommit of the specified branch
     * and sets the branch to the current branch.
     * @param branchName the name of the file we want.
     */
    private static void checkoutBranchName(String branchName) {
        if (!directory.getcurr().keySet().contains(branchName)) {
            System.out.println("No such branch exists.");
            return;
        } else if (directory.agarrandorama().agarrar().equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        Branch checkoutBranch = directory.getcurr().get(branchName);
        Branch currBranch = directory.agarrandorama();

        for (String fileName: checkoutBranch
                .agarralider().irAlPasado().keySet()) {
            File checkoutFile = new File(checkoutBranch.agarralider()
                    .irAlPasado().get(fileName));
            File oldFile;
            try {
                oldFile = new File(currBranch.agarralider()
                        .irAlPasado().get(fileName));
            } catch (NullPointerException ignored) {
                oldFile = new File(checkoutBranch.agarralider()
                        .irAlPasado().get(fileName));
            }
            File tempFile = new File(currBranch.agarralider()
                    .anticambiandochars(fileName));
            if (tempFile.exists() && (!Utils.readContentsAsString(checkoutFile)
                    .equals(Utils.readContentsAsString(tempFile))
                    && !Utils.readContentsAsString(tempFile)
                    .equals(Utils.readContentsAsString(oldFile)))) {
                System.out.println("There is an untracked file"
                        + " in the way; delete it or add it first.");
                return;
            }
        }

        ArrayList<File> filesToDelete = new ArrayList<>();
        for (String fileName: currBranch.agarralider()
                .irAlPasado().keySet()) {
            if (!checkoutBranch.agarralider()
                    .irAlPasado().containsKey(fileName)) {
                File targetFile = new File(fileName);
                filesToDelete.add(targetFile);
            }
        }

        for (File files: filesToDelete) {
            files.delete();
        }

        for (String fileName: checkoutBranch
                .agarralider().irAlPasado().keySet()) {
            fileName = checkoutBranch.agarralider()
                    .anticambiandochars(fileName);
            checkoutName(fileName, checkoutBranch.agarralider());
        }

        directory.obteniendorama(checkoutBranch);
    }

    /** Prints out a log of all the commits starting with the
     * current branch head, trailing backwards to the init commit.
     */
    private static void log() {
        Commit currCommit = directory.agarrandorama().agarralider();
        while (currCommit != null) {
            System.out.println(currCommit.toString());
            currCommit = currCommit.estadodefamlista();
        }
    }

    /** Unstage the file and mark it to be not included in next commit.
     * Also remove the file from the working directory if it is still
     * there UNLESS it is untracked in the current commit.
     * @param fileName the name of the file we want to remove.
     */
    private static void rm(String fileName) {
        Branch currBranch = directory.agarrandorama();
        Commit currCommit = currBranch.agarralider();
        String processedFileName = currCommit.cambiandochars(fileName);
        Stage currStage = currBranch.fixing();
        String myStageRepo = stageRepo + currStage.bearcard() + separator;

        if (!currCommit.irAlPasado().containsKey(processedFileName)
                && !Utils.plainFilenamesIn(myStageRepo)
                .contains(processedFileName)) {
            System.out.println("No reason to remove the file.");
            return;
        }

        if (currCommit.irAlPasado().keySet().contains(fileName)) {
            currCommit.agarrarelpasado().add(fileName);
        }

        if (Utils.plainFilenamesIn(myStageRepo).contains(processedFileName)) {
            File theFile = new File(myStageRepo + processedFileName);
            theFile.delete();
        }
        currStage.getdicto().remove(processedFileName);
        currStage.traerback().add(fileName);
        currStage.almacenando();

        if (currCommit.irAlPasado().containsKey(processedFileName)) {
            String oldFilDir = currCommit
                    .irAlPasado().get(processedFileName);
            File oldFile = new File(oldFilDir);
            File thisFile = new File(fileName);
            if (thisFile.exists() && Utils.readContentsAsString(oldFile)
                    .equals(Utils.readContentsAsString(thisFile))) {
                try {
                    Utils.restrictedDelete(thisFile);
                } catch (IllegalArgumentException e) {
                    thisFile.delete();
                }
            }
        }
        Utils.writeObject(new File(objectRepo
                + currCommit.bearcard()), currCommit);
    }

    /** Basically log, except it displays the information for
     * every commit ever made, ordering does not matter.
     */
    private static void globalLog() {
        for (String commitNames: Utils.plainFilenamesIn(objectRepo)) {
            File thisFile = new File(objectRepo + commitNames);
            System.out.println(Utils.readObject(thisFile, Commit.class));
        }
    }

    /** Finds all files with the given commit message and prints
     * their commit IDs out one per line, one at a time.
     * @param message the commit message.
     */
    private static void find(String message) {
        boolean foundAtLeastOne = false;
        for (String commitNames: Utils.plainFilenamesIn(objectRepo)) {
            File thisFile = new File(objectRepo + commitNames);
            Commit thisCommit = Utils.readObject(thisFile, Commit.class);
            if (thisCommit.imprimiendo().equals(message)) {
                System.out.println(thisCommit.bearcard().substring(6));
                foundAtLeastOne = true;
            }
        }
        if (!foundAtLeastOne) {
            System.out.println("Found no commit with that message.");
            return;
        }
    }


    /** Displays what branches exist (and marks the current one), what
     * files have been staged, and what files have been marked for untracking.
     * Note that the files should be in SORTED ORDER.
     */
    private static void status() {
        ArrayList<String> filesToPrint = new ArrayList<>();
        String fileName = "";

        System.out.println("=== Branches ===");
        for (Branch branches: directory.getcurr().values()) {
            if (branches == directory.agarrandorama()) {
                fileName = "*" + fileName;
            }
            fileName = fileName + branches.agarrar();
            filesToPrint.add(fileName);
            fileName = "";
        }
        Collections.sort(filesToPrint);
        for (String files: filesToPrint) {
            System.out.println(files);
        }
        System.out.println();

        filesToPrint.clear();

        System.out.println("=== Staged Files ===");
        Stage currStage = directory.agarrandorama().fixing();
        Commit currCommit = directory.agarrandorama().agarralider();
        for (String stagedFiles: currStage.getdicto()) {
            filesToPrint.add(currCommit.anticambiandochars(stagedFiles));
        }
        Collections.sort(filesToPrint);
        for (String files: filesToPrint) {
            System.out.println(files);
        }
        System.out.println();

        filesToPrint.clear();

        System.out.println("=== Removed Files ===");
        filesToPrint.addAll(directory.agarrandorama().agarralider()
                .atraparestado().traerback());
        Collections.sort(filesToPrint);
        for (String files: filesToPrint) {
            System.out.println(files);
        }
        System.out.println();
        filesToPrint.clear();

        System.out.println("=== Modifications Not Staged For Commit ===");
        statusPt2(currStage, currCommit, fileName, filesToPrint);
    }

    /** Part 2 of the "status" command.
     * @param currStage The current stage
     * @param currCommit The current commit
     * @param fileName The file name
     * @param filesToPrint The files to print
     */
    private static void statusPt2(Stage currStage,
                                  Commit currCommit, String fileName,
                                  ArrayList<String> filesToPrint) {
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    /** Creates a new branch with it's current commit pointer
     * pointed at the current head commit of the current branch.
     * @param name the name of the new branch.
     */
    private static void branch(String name) {

        if (directory.getcurr().containsKey(name)) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        Branch newBranch = new Branch(name,
                directory.agarrandorama().agarralider(), directory);

        Stage newBranchStage = new Stage(directory, new Date());
        newBranchStage.guardando(directory.agarrandorama().agarralider());

        directory.getcurr().put(newBranch.agarrar(), newBranch);
        newBranch.mascambios(newBranchStage);
    }

    /** Removes the current branch from directory.myChildren. That's
     * about it really lol.
     * @param branchName literally what it says it is.
     */
    private static void removeBranch(String branchName) {
        if (!directory.getcurr().containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (directory.agarrandorama().agarrar().equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        directory.getcurr().remove(branchName);
    }

    /** Resets the working directory to the given commitID.
     * @param commitID the commitID of the commit we're trying to reset
     *                 to.
     */
    private static void reset(String commitID) {
        if (!directory.getcommiteando().contains("commit" + commitID)) {
            System.out.println("No commit with that id exists.");
            return;
        }

        Branch currBranch = directory.agarrandorama();
        Commit currCommit = currBranch.agarralider();
        Stage currStage = currBranch.fixing();

        Commit revertToCommit = Utils.readObject(
                new File(objectRepo + "commit" + commitID), Commit.class);

        for (String fileName: revertToCommit.irAlPasado().keySet()) {
            if (currCommit.agarrarelpasado().contains(fileName)) {
                System.out.println("There is an untracked file "
                        + "in the way; delete it or add it first.");
                return;
            }
        }


        for (String fileName: currCommit.irAlPasado().keySet()) {
            if (!revertToCommit.irAlPasado().containsKey(fileName)) {
                new File(currCommit.anticambiandochars(fileName)).delete();
            }
        }
        for (String fileName: revertToCommit.irAlPasado().keySet()) {
            String revProcessedFileName = currCommit.anticambiandochars(fileName);
            Utils.writeContents(new File(revProcessedFileName),
                    Utils.readContentsAsString(new File(revertToCommit
                            .irAlPasado().get(fileName))));
        }
    }


    /** Finds the appropriate split point by compiling all the commits
     * within the given branch. And then traversing the currentBranch
     * one commit at a time until we reach a commit that is contained
     * within the given Branch.
     * @param givenCommit the commit that is indicated.
     * @param curCommit the current commit.
     * @return commit which is the closest split point to the
     * current branch
     */
    private static Commit findSplitPoint(Commit curCommit, Commit givenCommit) {
        HashSet<String> allGBCommits = new HashSet<>();
        Commit trackerCommit = givenCommit;

        while (trackerCommit != null) {
            allGBCommits.add(trackerCommit.bearcard());
            trackerCommit = trackerCommit.estadodefamlista();
        }

        while (!allGBCommits.contains(curCommit.bearcard())) {
            for (String theCommit: curCommit.famlista()) {
                Commit thisCommit = Utils.readObject(
                        new File(objectRepo + theCommit), Commit.class);
                if (allGBCommits.contains(thisCommit)) {
                    return thisCommit;
                }
            }
            curCommit = curCommit.estadodefamlista();
        }
        return curCommit;
    }

    /** Merges files from the given branch into the current branch.
     * @param branchName the name of the branch we want
     */
    private static void merge(String branchName) {
        if (!directory.getcurr().containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        Branch currBranch = directory.agarrandorama();
        Branch gottenBranch = directory.getcurr().get(branchName);
        Commit currCommit = currBranch.agarralider();
        Commit getCommit = gottenBranch.agarralider();
        if (currBranch.agarrar().equals(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }
        Commit splitPoint = findSplitPoint(currCommit, getCommit);
        if (splitPoint.bearcard().equals(getCommit.bearcard())) {
            System.out.println("Given branch is an "
                    + "ancestor of the current branch.");
            return;
        } else if (splitPoint.bearcard()
                .equals(gottenBranch.liderdefila())) {
            directory.obteniendorama(gottenBranch);
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        if (!Utils.plainFilenamesIn(currBranch
                .fixing().agarracommits()).isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return;
        }

        for (String fileName: splitPoint.irAlPasado().keySet()) {
            boolean dummy = true;
            File spFile = new File(splitPoint
                    .irAlPasado().get(fileName));
            try {
                File ccFile = new File(currCommit
                        .irAlPasado().get(fileName));
                File gcFile = new File(getCommit
                        .irAlPasado().get(fileName));
                if (Utils.readContentsAsString(spFile)
                        .equals(Utils.readContentsAsString(ccFile))
                        && !gcFile.exists()) {
                    if (!currCommit.irAlPasado()
                            .containsKey(fileName)) {
                        System.out.println("There is an untracked file"
                                + " in the way; delete it or add it first.");
                        return;
                    }
                    Utils.restrictedDelete(currCommit
                            .anticambiandochars(fileName));
                }
            } catch (NullPointerException e) {
                dummy = false;
            }
        }
        mergePt2(currBranch, gottenBranch, getCommit, currCommit, splitPoint);
    }

    /** Basically part 2 of the "merge" command to make the style
     * checker happy :P.
     * @param currBranch it
     * @param gottenBranch be
     * @param getCommit like
     * @param currCommit that
     * @param splitPoint sometimes
     */
    private static void mergePt2(Branch currBranch, Branch gottenBranch,
                                 Commit getCommit, Commit currCommit,
                                 Commit splitPoint) {
        boolean occuredMergeConflict = false;
        for (String fileName: getCommit.irAlPasado().keySet()) {
            File gcFile = new File(getCommit
                    .irAlPasado().get(fileName));
            File ccFile, spFile;
            try {
                ccFile = new File(currCommit
                        .irAlPasado().get(fileName));
            } catch (NullPointerException ignored) {
                ccFile = null;
            }
            try {
                spFile = new File(splitPoint
                        .irAlPasado().get(fileName));
            } catch (NullPointerException ignored) {
                spFile = null;
            }
            fileName = currCommit.anticambiandochars(fileName);
            if (spFile == null && ccFile == null) {
                checkoutID(getCommit.bearcard(), fileName);
                add(fileName);
            } else if (spFile != null && ccFile != null) {
                if ((!Utils.readContentsAsString(gcFile)
                        .equals(Utils.readContentsAsString(spFile))
                        || getCommit.estadodefamlista()
                        .agarrarelpasado().contains(fileName))
                        && Utils.readContentsAsString(ccFile)
                        .equals(Utils.readContentsAsString(spFile))) {
                    if (getCommit.estadodefamlista()
                            .agarrarelpasado().contains(fileName)) {
                        Utils.restrictedDelete(
                                currCommit.anticambiandochars(fileName));
                    } else {
                        checkoutID(getCommit.bearcard(), fileName);
                        add(fileName);
                    }
                } else if (!Utils.readContentsAsString(gcFile)
                        .equals(Utils.readContentsAsString(ccFile))) {
                    String concatFiles = "<<<<<<< HEAD\n";
                    concatFiles = concatFiles
                            + Utils.readContentsAsString(ccFile);
                    concatFiles = concatFiles + "=======\n";
                    concatFiles = concatFiles
                            + Utils.readContentsAsString(gcFile);
                    concatFiles = concatFiles + ">>>>>>>";
                    Utils.writeContents(new File(fileName), concatFiles);
                    occuredMergeConflict = true;
                }
            }
        }
        mergePt3(gottenBranch, currBranch, occuredMergeConflict);
    }

    /** Basically, part 3 of the "merge" command to make the
     * style checker happy...
     * @param gottenBranch I
     * @param currBranch Like
     * @param mergeConflict Pies
     */
    private static void mergePt3(Branch gottenBranch,
                                 Branch currBranch, boolean mergeConflict) {
        commit("Merged " + gottenBranch.agarrar()
                + " into " + currBranch.agarrar() + ".");
        directory.agarrandorama().agarralider().estadodegit();
        Commit thisCommit = directory.agarrandorama().agarralider();
        thisCommit.famlista().add(gottenBranch.liderdefila());
        Utils.writeObject(new File(objectRepo
                + thisCommit.bearcard()), thisCommit);
        if (mergeConflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }


    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        String command = args[0];
        String[] operands = null;
        directory = loadDirectory();
        try {
            operands = Arrays.copyOfRange(args, 1, args.length);
        } catch (IndexOutOfBoundsException ignored) {
            operands = operands;
        }
        try {
            if (command.equals("init")) {
                init();
            } else if (command.equals("add")) {
                add(operands[0]);
            } else if (command.equals("commit")) {
                commit(operands[0]);
            } else if (command.equals("checkout")) {
                List<String> operandList;
                try {
                    operandList = Arrays.asList(operands);
                } catch (NullPointerException e) {
                    throw new IndexOutOfBoundsException();
                }
                if (!operandList.contains("--") && operandList.size() == 1) {
                    checkoutBranchName(operands[0]);
                } else if (operands[0].equals("--")) {
                    checkoutName(operands[1],
                            directory.agarrandorama().agarralider());
                } else if (operands[1].equals("--")) {
                    checkoutID(operands[0], operands[2]);
                } else {
                    throw new IndexOutOfBoundsException();
                }
            } else if (command.equals("log")) {
                log();
            } else if (command.equals("rm")) {
                rm(operands[0]);
            } else if (command.equals("global-log")) {
                globalLog();
            } else if (command.equals("find")) {
                find(operands[0]);
            } else if (command.equals("status")) {
                status();
            } else if (command.equals("branch")) {
                branch(operands[0]);
            } else if (command.equals("rm-branch")) {
                removeBranch(operands[0]);
            } else if (command.equals("reset")) {
                reset(operands[0]);
            } else if (command.equals("merge")) {
                merge(operands[0]);
            }
        } catch (NullPointerException e) {
            System.out.println("Not in an initialized Gitlet directory.");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Incorrect operands.");
        }
        saveDirectory();
    }

    /** Just a method allowing me to play around with the main method :D.
     */
    public static void trialMethod() {
        System.out.println("The council calls for your trial");
    }

    /** Just another method that allows me to play around, except this
     * one allows for arguments.
     * @param name the operand, in this case it's a name because the
     *             message is retarded lmao.
     */
    public static void trialMethod(String name) {
        System.out.println("The council calls for your execution " + name);
    }

    /** The separator character for my OS. It should be a "/".
     */
    private static String separator = File.separator;

    /** The directory to the object repository.
     */
    private static String objectRepo = ".gitlet"
            + separator + "objectRepository" + separator;

    /** The directory to the stage repository.
     */
    private static String stageRepo =
            ".gitlet" + separator + "stages" + separator;

    /** The overarching TreeP which represents me entire repository.
     */
    private static TreeP directory = null;
    /** Get method for the TreeP. Mostly for use in my UnitTests.
     * @return the directory.
     */
    public static TreeP getDirectory() {
        File directoryFile = new File(".gitlet" + separator + "repo");
        return Utils.readObject(directoryFile, TreeP.class);
    }
}