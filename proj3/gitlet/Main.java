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


/** Main class for gitlet project
 *  @author eduhmc
 */
public class Main implements Serializable {

    /** Helper function to get name of all files in the working
     * directory that does not include the original git functions.
     * @return List of all the fileNames.
     */
    private static ArrayList<String> workingDirFiles() {
        ArrayList<String> tempList = new ArrayList<>();
        tempList.addAll(Utils.plainFilenamesIn(
                FileSystems.getDefault().getPath(".").toString()));
        tempList.remove(".gitignore");
        tempList.remove("Makefile");
        tempList.remove("proj3.iml");
        return tempList;
    }

    /** Saves the current directory state in order to pull it
     * back out upon the next call.
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

    /** Loads a previously saved directory state.
     * @return The TreeP structure representing the former directory.
     */
    static TreeP loadDirectory() {
        File f = new File(".gitlet" + separator + "repo" + separator);
        try {
            TreeP retTree = Utils.readObject(f, TreeP.class);
            return retTree;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /** For testing with the staff gitlet, deletes the exisitng ".gitlet"
     * repository.
     * @param directoryToBeDeleted the directory to be deleted.
     * @return boolean value denoting whether something was deleted.
     */
    private static boolean deleteRepository(File directoryToBeDeleted) {
        if (!directoryToBeDeleted.exists()) {
            System.out.println("there ain't nothin' to delete");
            return false;
        }
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteRepository(file);
            }
        }
        return directoryToBeDeleted.delete();
    }


    /** Method run by the "init" command. It aims to initialize a .gitlet
     * repository if ones does not already exist. And it creates a
     * Stage -> Commit -> Branch -> TreeP.
     */
    static void init() {
        File initialDirectory = new File(".gitlet" + separator);
        File stageFolder = new File(".gitlet" + separator + "stages");
        File objectRepository = new File(
                ".gitlet" + separator + "objectRepository");

        if (initialDirectory.exists()) {
            System.out.println(" A Gitlet version-control system "
                    + "already exists in the current directory.");
            return;
        }
        initialDirectory.mkdirs();
        stageFolder.mkdirs();
        objectRepository.mkdirs();

        directory = new TreeP();
        Date initialCommitDate = new Date();
        initialCommitDate.setTime(0);
        Stage initStage = new Stage(directory, initialCommitDate);
        Commit initCommit = new Commit("initial commit", initialCommitDate, directory, initStage, null);

        initCommit.addCommit();
        initStage.setMyCommit(initCommit);

        Stage nextCommitsStage = new Stage(directory, new Date());
        nextCommitsStage.setMyCommit(initCommit);

        Branch newBranch = new Branch("master", initCommit, directory);

        directory.getcurr().put(newBranch.agarrar(), newBranch);
        directory.obteniendorama(newBranch);
        newBranch.cambios(initCommit);
        newBranch.mascambios(nextCommitsStage);
    }

    /** Method run by the "add" command. It will add the input
     * fileName onto the stage. If the file has been marked for
     * removal, it removes the mark. If the file is contained
     * within the previous commit and is the same version, then
     * it is not staged and the method terminates. If it had
     * already been staged previously, the current version is
     * deleted and the updated version is staged instead.
     * @param fileName the name of the file to be staged from
     *                 within the working directory.
     */
    static void add(String fileName) {
        Commit headCommit = directory.agarrandorama().agarralider();
        directory.agarrandorama().fixing().add(fileName);
        if (headCommit.getMyUntrackedFiles().contains(fileName)) {
            headCommit.getMyUntrackedFiles().remove(fileName);
        }
        headCommit.saveCommit();
    }

    /** Method run by the "commit" command.
     * @param message the message that comes with this commit.
     */
    static void commit(String message) {
        Date tempCommitDate = new Date();
        Branch curBranch = directory.agarrandorama();
        Stage newStage = new Stage(directory, tempCommitDate);

        Stage currStage = curBranch.fixing();
        Commit currCommit = curBranch.agarralider();

        if (message.equals("")) {
            System.out.println("Please enter a commit message.");
            return;
        } else if (curBranch.fixing().getStagedFileNames().size() == 0) {
            try {
                Commit parentCommit = currCommit.getMyParentCommit();
                boolean newRemovedFile = false;
                if (parentCommit.getMyUntrackedFiles().size()
                        != currCommit.getMyUntrackedFiles().size()) {
                    newRemovedFile = true;
                } else {
                    for (String fileName: parentCommit.getMyUntrackedFiles()) {
                        if (!currCommit.getMyUntrackedFiles()
                                .contains(fileName)) {
                            newRemovedFile = true;
                        }
                    }
                }
                if (!newRemovedFile) {
                    System.out.println("No changes added to the commit.");
                    return;
                }
            } catch (NullPointerException e) {
                System.out.println("No changes added to the commit.");
                return;
            }
        }
        Commit newCommit = new Commit(message, tempCommitDate,
                directory, currStage, currCommit.getMyID());

        newCommit.addCommit();

        curBranch.cambios(newCommit);
        directory.getcommiteando().add(newCommit.getMyID());
        curBranch.mascambios(newStage);
        newStage.setMyCommit(newCommit);
    }

    /** Checkout method, where we retrieve only the file
     * with the name specified from the current headCommit
     * folder.
     * @param headCommit the commit we want to perform the
     *                   checkout upon
     * @param fileName the name of the file we want.
     */
    private static void checkoutName(String fileName, Commit headCommit) {
        fileName = headCommit.processString(fileName);

        if (headCommit.getOldFileToRepoLoc() == null
                || !headCommit.getOldFileToRepoLoc().containsKey(fileName)
                || (headCommit.getMyParentCommit()
                .getMyUntrackedFiles().contains(fileName))
                && !headCommit.getOldFileToRepoLoc().containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        File oldVersion = new File(headCommit
                .getOldFileToRepoLoc().get(fileName));

        fileName = headCommit.processStringRev(fileName);
        Utils.writeContents(new File(fileName),
                Utils.readContentsAsString(oldVersion));
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

        fileName = desiredCommit.processString(fileName);
        if (!desiredCommit.getMyFilePointers().containsKey(fileName)
                || desiredCommit.getMyUntrackedFiles().contains(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        File oldVersion = new File(desiredCommit
                .getOldFileToRepoLoc().get(fileName));

        fileName = desiredCommit.processStringRev(fileName);
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
                .agarralider().getOldFileToRepoLoc().keySet()) {
            File checkoutFile = new File(checkoutBranch.agarralider()
                    .getOldFileToRepoLoc().get(fileName));
            File oldFile;
            try {
                oldFile = new File(currBranch.agarralider()
                        .getOldFileToRepoLoc().get(fileName));
            } catch (NullPointerException ignored) {
                oldFile = new File(checkoutBranch.agarralider()
                        .getOldFileToRepoLoc().get(fileName));
            }
            File tempFile = new File(currBranch.agarralider()
                    .processStringRev(fileName));
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
                .getOldFileToRepoLoc().keySet()) {
            if (!checkoutBranch.agarralider()
                    .getOldFileToRepoLoc().containsKey(fileName)) {
                File targetFile = new File(fileName);
                filesToDelete.add(targetFile);
            }
        }

        for (File files: filesToDelete) {
            files.delete();
        }

        for (String fileName: checkoutBranch
                .agarralider().getOldFileToRepoLoc().keySet()) {
            fileName = checkoutBranch.agarralider()
                    .processStringRev(fileName);
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
            currCommit = currCommit.getMyParentCommit();
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
        String processedFileName = currCommit.processString(fileName);
        Stage currStage = currBranch.fixing();
        String myStageRepo = stageRepo + currStage.getMyID() + separator;

        if (!currCommit.getOldFileToRepoLoc().containsKey(processedFileName)
                && !Utils.plainFilenamesIn(myStageRepo)
                .contains(processedFileName)) {
            System.out.println("No reason to remove the file.");
            return;
        }

        if (currCommit.getOldFileToRepoLoc().keySet().contains(fileName)) {
            currCommit.getMyUntrackedFiles().add(fileName);
        }

        if (Utils.plainFilenamesIn(myStageRepo).contains(processedFileName)) {
            File theFile = new File(myStageRepo + processedFileName);
            theFile.delete();
        }
        currStage.getStagedFileNames().remove(processedFileName);
        currStage.getRemovedFiles().add(fileName);
        currStage.saveStage();

        if (currCommit.getOldFileToRepoLoc().containsKey(processedFileName)) {
            String oldFilDir = currCommit
                    .getOldFileToRepoLoc().get(processedFileName);
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
                + currCommit.getMyID()), currCommit);
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
            if (thisCommit.getMyMessage().equals(message)) {
                System.out.println(thisCommit.getMyID().substring(6));
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
        for (String stagedFiles: currStage.getStagedFileNames()) {
            filesToPrint.add(currCommit.processStringRev(stagedFiles));
        }
        Collections.sort(filesToPrint);
        for (String files: filesToPrint) {
            System.out.println(files);
        }
        System.out.println();

        filesToPrint.clear();

        System.out.println("=== Removed Files ===");
        filesToPrint.addAll(directory.agarrandorama().agarralider()
                .getMyStage().getRemovedFiles());
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
        newBranchStage.setMyCommit(directory.agarrandorama().agarralider());

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

        for (String fileName: revertToCommit.getOldFileToRepoLoc().keySet()) {
            if (currCommit.getMyUntrackedFiles().contains(fileName)) {
                System.out.println("There is an untracked file "
                        + "in the way; delete it or add it first.");
                return;
            }
        }


        for (String fileName: currCommit.getOldFileToRepoLoc().keySet()) {
            if (!revertToCommit.getOldFileToRepoLoc().containsKey(fileName)) {
                new File(currCommit.processStringRev(fileName)).delete();
            }
        }
        for (String fileName: revertToCommit.getOldFileToRepoLoc().keySet()) {
            String revProcessedFileName = currCommit.processStringRev(fileName);
            Utils.writeContents(new File(revProcessedFileName),
                    Utils.readContentsAsString(new File(revertToCommit
                            .getOldFileToRepoLoc().get(fileName))));
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
            allGBCommits.add(trackerCommit.getMyID());
            trackerCommit = trackerCommit.getMyParentCommit();
        }

        while (!allGBCommits.contains(curCommit.getMyID())) {
            for (String theCommit: curCommit.getAllMyParents()) {
                Commit thisCommit = Utils.readObject(
                        new File(objectRepo + theCommit), Commit.class);
                if (allGBCommits.contains(thisCommit)) {
                    return thisCommit;
                }
            }
            curCommit = curCommit.getMyParentCommit();
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
        if (splitPoint.getMyID().equals(getCommit.getMyID())) {
            System.out.println("Given branch is an "
                    + "ancestor of the current branch.");
            return;
        } else if (splitPoint.getMyID()
                .equals(gottenBranch.liderdefila())) {
            directory.obteniendorama(gottenBranch);
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        if (!Utils.plainFilenamesIn(currBranch
                .fixing().getMyFolder()).isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return;
        }

        for (String fileName: splitPoint.getOldFileToRepoLoc().keySet()) {
            boolean dummy = true;
            File spFile = new File(splitPoint
                    .getOldFileToRepoLoc().get(fileName));
            try {
                File ccFile = new File(currCommit
                        .getOldFileToRepoLoc().get(fileName));
                File gcFile = new File(getCommit
                        .getOldFileToRepoLoc().get(fileName));
                if (Utils.readContentsAsString(spFile)
                        .equals(Utils.readContentsAsString(ccFile))
                        && !gcFile.exists()) {
                    if (!currCommit.getOldFileToRepoLoc()
                            .containsKey(fileName)) {
                        System.out.println("There is an untracked file"
                                + " in the way; delete it or add it first.");
                        return;
                    }
                    Utils.restrictedDelete(currCommit
                            .processStringRev(fileName));
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
        for (String fileName: getCommit.getOldFileToRepoLoc().keySet()) {
            File gcFile = new File(getCommit
                    .getOldFileToRepoLoc().get(fileName));
            File ccFile, spFile;
            try {
                ccFile = new File(currCommit
                        .getOldFileToRepoLoc().get(fileName));
            } catch (NullPointerException ignored) {
                ccFile = null;
            }
            try {
                spFile = new File(splitPoint
                        .getOldFileToRepoLoc().get(fileName));
            } catch (NullPointerException ignored) {
                spFile = null;
            }
            fileName = currCommit.processStringRev(fileName);
            if (spFile == null && ccFile == null) {
                checkoutID(getCommit.getMyID(), fileName);
                add(fileName);
            } else if (spFile != null && ccFile != null) {
                if ((!Utils.readContentsAsString(gcFile)
                        .equals(Utils.readContentsAsString(spFile))
                        || getCommit.getMyParentCommit()
                        .getMyUntrackedFiles().contains(fileName))
                        && Utils.readContentsAsString(ccFile)
                        .equals(Utils.readContentsAsString(spFile))) {
                    if (getCommit.getMyParentCommit()
                            .getMyUntrackedFiles().contains(fileName)) {
                        Utils.restrictedDelete(
                                currCommit.processStringRev(fileName));
                    } else {
                        checkoutID(getCommit.getMyID(), fileName);
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
        directory.agarrandorama().agarralider().setMeToMergeCommit();
        Commit thisCommit = directory.agarrandorama().agarralider();
        thisCommit.getAllMyParents().add(gottenBranch.liderdefila());
        Utils.writeObject(new File(objectRepo
                + thisCommit.getMyID()), thisCommit);
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