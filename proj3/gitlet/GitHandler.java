package gitlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.FileSystems;
import java.util.*;

public class GitHandler {
    private static String objectRepo = ".gitlet/objectRepository/";
    private static String stageRepo = ".gitlet/stages/";
    private static TreeP directory = null;

    public void handleCommands(String... args){

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
                if (!operandList.contains("--")) {
                    checkoutBranchName(operands[0]);
                } else if (operands[0].equals("--")) {
                    checkoutName(operands[1],
                            directory.CurrentBranch.getHeadCommit());
                } else if (operands[1].equals("--")) {
                    checkoutID(operands[0], operands[2]);
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
            return;
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Incorrect operands.");
            return;
        }
        saveDirectory();
    }

    private static ArrayList<String> workingDirFiles() {
        ArrayList<String> tempList = new ArrayList<>();
        tempList.addAll(Utils.plainFilenamesIn(
                FileSystems.getDefault().getPath(".").toString()));
        tempList.remove(".gitignore");
        tempList.remove("Makefile");
        tempList.remove("proj3.iml");
        return tempList;
    }

    static void saveDirectory() {
        if (directory == null) {
            return;
        }

        try {
            File f = new File(".gitlet/repo/");
            ObjectOutputStream dirStream =
                    new ObjectOutputStream(new FileOutputStream(f));
            dirStream.writeObject(directory);
            dirStream.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    static TreeP loadDirectory() {
        File f = new File(".gitlet/repo/");
        try {
            TreeP retTree = Utils.readObject(f, TreeP.class);
            return retTree;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

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

    static void init() {
        File initialDirectory = new File(".gitlet/");
        File stageFolder = new File(".gitlet/stages");
        File objectRepository = new File(".gitlet/objectRepository");
        if (initialDirectory.exists()) {
            System.out.println(" A Gitlet version-control system already exists in the current directory.");
            return;
        }
        initialDirectory.mkdirs();
        stageFolder.mkdirs();
        objectRepository.mkdirs();
        directory = new TreeP();
        Date initialCommitDate = new Date();
        initialCommitDate.setTime(0);
        Stage initStage = new Stage(initialCommitDate);
        Commit initCommit = new Commit("initial commit",
                initialCommitDate, directory, initStage, null);
        initCommit.addCommit();
        initStage.setMyCommit(initCommit);
        Stage nextCommitsStage = new Stage(new Date());
        nextCommitsStage.setMyCommit(initCommit);
        Branch newBranch = new Branch("master", directory, initCommit);
        directory.Children.put(newBranch.Name, newBranch);
        directory.CurrentBranch = newBranch;
        newBranch.updateHead(initCommit);
        newBranch.updateStage(nextCommitsStage);
    }

    static void add(String fileName) {
        directory.CurrentBranch.getMyStage().add(fileName);
    }

    static void commit(String message) {
        Date tempCommitDate = new Date();
        Branch curBranch = directory.CurrentBranch;
        Stage newStage = new Stage(tempCommitDate);

        Stage currStage = curBranch.getMyStage();
        Commit currCommit = curBranch.getHeadCommit();

        if (message.equals("")) {
            System.out.println("Please enter a commit message.");
            return;
        } else if (curBranch.getMyStage().getStagedFileNames().size() == 0) {
            Commit parentCommit = currCommit.getMyParentCommit();
            boolean newRemovedFile = false;
            if (parentCommit.getMyUntrackedFiles().size()
                    != currCommit.getMyUntrackedFiles().size()) {
                newRemovedFile = true;
            } else {
                for (String fileName: parentCommit.getMyUntrackedFiles()) {
                    if (!currCommit.getMyUntrackedFiles().contains(fileName)) {
                        newRemovedFile = true;
                    }
                }
            }
            if (!newRemovedFile) {
                System.out.println("No changes added to the commit.");
                return;
            }
        }
        Commit newCommit = new Commit(message, tempCommitDate,
                directory, currStage, currCommit.getMyID());

        newCommit.addCommit();

        curBranch.updateHead(newCommit);
        directory.Commits.add(newCommit.getMyID());
        curBranch.updateStage(newStage);
        newStage.setMyCommit(newCommit);
    }

    private static void checkoutName(String fileName, Commit headCommit) {
        fileName = headCommit.processString(fileName);

        if (headCommit.getOldFileToRepoLoc() == null
                || !headCommit.getOldFileToRepoLoc().containsKey(fileName)
                || headCommit.getMyUntrackedFiles().contains(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        File oldVersion = new File(headCommit
                .getOldFileToRepoLoc().get(fileName));

        fileName = headCommit.processStringRev(fileName);
        Utils.writeContents(new File(fileName),
                Utils.readContentsAsString(oldVersion));
    }

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

    private static void checkoutBranchName(String branchName) {
        if (!directory.Children.keySet().contains(branchName)) {
            System.out.println("No such branch exists.");
            return;
        } else if (directory.CurrentBranch.Name.equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        Branch checkoutBranch = directory.Children.get(branchName);
        Branch currBranch = directory.CurrentBranch;

        for (String fileName: checkoutBranch
                .getHeadCommit().getOldFileToRepoLoc().keySet()) {
            File tempFile = new File(currBranch
                    .getHeadCommit().processStringRev(fileName));
            if (!currBranch.getHeadCommit()
                    .getOldFileToRepoLoc().containsKey(fileName)
                    && tempFile.exists()) {
                System.out.println("There is an untracked file"
                        + " in the way; delete it or add it first.");
                return;
            }
        }

        ArrayList<File> filesToDelete = new ArrayList<>();
        for (String fileName: currBranch.getHeadCommit()
                .getOldFileToRepoLoc().keySet()) {
            if (!checkoutBranch.getHeadCommit()
                    .getOldFileToRepoLoc().containsKey(fileName)) {
                File targetFile = new File(fileName);
                filesToDelete.add(targetFile);
            }
        }

        for (String fileName: checkoutBranch
                .getHeadCommit().getOldFileToRepoLoc().keySet()) {
            checkoutName(fileName, checkoutBranch.getHeadCommit());
        }

        directory.CurrentBranch = checkoutBranch;
    }

    /** Prints out a log of all the commits starting with the
     * current branch head, trailing backwards to the init commit.
     */
    private static void log() {
        Commit currCommit = directory.CurrentBranch.getHeadCommit();
        while (currCommit != null) {
            System.out.println(currCommit.toString());
            currCommit = currCommit.getMyParentCommit();
        }
    }

    private static void rm(String fileName) {
        Branch currBranch = directory.CurrentBranch;
        Commit currCommit = currBranch.getHeadCommit();
        String processedFileName = currCommit.processString(fileName);
        Stage currStage = currBranch.getMyStage();
        String myStageRepo = stageRepo + currStage.getMyID() + "/";

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

    private static void globalLog() {
        for (String commitNames: Utils.plainFilenamesIn(objectRepo)) {
            File thisFile = new File(objectRepo + commitNames);
            System.out.println(Utils.readObject(thisFile, Commit.class));
        }
    }

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

    private static void status() {
        ArrayList<String> filesToPrint = new ArrayList<>();
        String fileName = "";

        System.out.println("=== Branches ===");
        for (Branch branches: directory.Children.values()) {
            if (branches == directory.CurrentBranch) {
                fileName = "*" + fileName;
            }
            fileName = fileName + branches.Name;
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
        Stage currStage = directory.CurrentBranch.getMyStage();
        Commit currCommit = directory.CurrentBranch.getHeadCommit();
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
        filesToPrint.addAll(directory.CurrentBranch
                .getHeadCommit().getMyUntrackedFiles());
        Collections.sort(filesToPrint);
        for (String files: filesToPrint) {
            System.out.println(files);
        }
        System.out.println();
        filesToPrint.clear();

        System.out.println("=== Modifications Not Staged For Commit ===");
        statusPt2(currStage, currCommit, fileName, filesToPrint);
    }

    private static void statusPt2(Stage currStage,
                                  Commit currCommit, String fileName,
                                  ArrayList<String> filesToPrint) {
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    private static void branch(String name) {

        if (directory.Children.containsKey(name)) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        Branch newBranch = new Branch(name, directory,   directory.CurrentBranch.getHeadCommit());

        Stage newBranchStage = new Stage(new Date());
        newBranchStage.setMyCommit(directory.CurrentBranch.getHeadCommit());

        directory.Children.put(newBranch.Name, newBranch);
        newBranch.updateStage(newBranchStage);
    }

    private static void removeBranch(String branchName) {
        if (!directory.Children.containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (directory.CurrentBranch.Name.equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        directory.Children.remove(branchName);
    }

    private static void reset(String commitID) {
        if (!directory.Commits.contains("commit" + commitID)) {
            System.out.println("No commit with that id exists.");
            return;
        }

        Branch currBranch = directory.CurrentBranch;
        Commit currCommit = currBranch.getHeadCommit();
        Stage currStage = currBranch.getMyStage();

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

    private static void merge(String branchName) {
        if (!directory.Children.containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        Branch currBranch = directory.CurrentBranch;
        Branch gottenBranch = directory.Children.get(branchName);
        Commit currCommit = currBranch.getHeadCommit();
        Commit getCommit = gottenBranch.getHeadCommit();
        if (currBranch.Name.equals(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }
        Commit splitPoint = findSplitPoint(currCommit, getCommit);
        if (splitPoint.getMyID().equals(getCommit.getMyID())) {
            System.out.println("Given branch is an "
                    + "ancestor of the current branch.");
            return;
        } else if (splitPoint.getMyID()
                .equals(gottenBranch.HeadCommit)) {
            directory.CurrentBranch = gottenBranch;
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        if (!Utils.plainFilenamesIn(currBranch
                .getMyStage().getMyFolder()).isEmpty()) {
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

    private static void mergePt2(Branch currBranch, Branch gottenBranch,
                                 Commit getCommit, Commit currCommit,
                                 Commit splitPoint) {
        boolean occuredMergeConflict = false;
        for (String fileName: getCommit.getOldFileToRepoLoc().keySet()) {
            File gcFile = new File(getCommit
                    .getOldFileToRepoLoc().get(fileName));
            File ccFile, stFile, spFile;
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
                if (!Utils.readContentsAsString(gcFile)
                        .equals(Utils.readContentsAsString(spFile))
                        && Utils.readContentsAsString(ccFile)
                        .equals(Utils.readContentsAsString(spFile))) {
                    checkoutID(getCommit.getMyID(), fileName);
                    add(fileName);
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

    private static void mergePt3(Branch gottenBranch,
                                 Branch currBranch, boolean mergeConflict) {
        commit("Merged " + gottenBranch.Name
                + " into " + currBranch.Name + ".");
        directory.CurrentBranch.getHeadCommit().setMeToMergeCommit();
        Commit thisCommit = directory.CurrentBranch.getHeadCommit();
        thisCommit.getAllMyParents().add(gottenBranch.HeadCommit);
        Utils.writeObject(new File(objectRepo
                + thisCommit.getMyID()), thisCommit);
        if (mergeConflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    public static TreeP getDirectory() {
        File directoryFile = new File(".gitlet/repo");
        return Utils.readObject(directoryFile, TreeP.class);
    }
}