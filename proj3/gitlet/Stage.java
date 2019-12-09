package gitlet;

import java.io.Serializable;
import java.io.File;
import java.util.ArrayList;

/**
 * Picture of the staging area of Gitlet. Has a picture of the most
 * recent Commit, new staged files, files to remove, and all staged files.
 * @author upasanachatterjee
 */
class Stage implements Serializable {
    /** A pointer to the most recent commit.*/
    private Commit curCommit;

    /** A list of the new staged files. Cleared after each commit.*/
    private ArrayList<String> newStaged;

    /** A list of files to not track in next Commit.*/
    private ArrayList<String> toRemove;

    /** Arraylist of staged files. I.e. staged for commit.
     * */
    private ArrayList<String> stagedFiles;

    /** Returns the most recent commit.*/
    public Commit getCurCommit() {
        return curCommit;
    }

    /** Returns Array of the staged files.*/
    public ArrayList<String> getStagedFiles() {
        return stagedFiles;
    }

    /** Returns list of new files added to stage.*/
    public ArrayList<String> getNewStaged() {
        return newStaged;
    }

    /** Returns a list of removed files. */
    public ArrayList<String> getToRemove() {
        return toRemove;
    }

    /** Constructor. Stage associated to LATEST commit.*/
    public Stage(Commit latest) {
        curCommit = latest;
        newStaged = new ArrayList<String>();
        stagedFiles = new ArrayList<String>();
        toRemove = new ArrayList<String>();

        if (latest.getAddedFiles() != null) {
            stagedFiles.addAll(latest.getAddedFiles());
        }
    }

    /**
     * Adds FILENAME to be staged if it is a changed file and it exists.
     * If the file had been marked to be removed, we delete that mark.
     */
    public void add(String fileName) {
        File f = new File(fileName);

        if (!f.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        if (!unchanged(fileName)) {
            newStaged.add(fileName);
            stagedFiles.add(fileName);
        } else {
            if (stagedFiles.contains(fileName)) {
                stagedFiles.remove(fileName);
            }
        }
        if (toRemove.contains(fileName)) {
            toRemove.remove(fileName);
        }

    }

    /** Removes file FILENAME from the staging area.*/
    public void remove(String fileName) {
        if (stagedFiles.contains(fileName) || newStaged.contains(fileName)) {
            if (curCommit.contains(fileName)) {
                Utils.restrictedDelete(fileName);
                toRemove.add(fileName);
            }
            stagedFiles.remove(fileName);
            newStaged.remove(fileName);
        } else {
            System.out.println(" No reason to remove the file.");
        }
    }


    /**
     * Returns true if file FILENAME has not been
     * changed since the last commit.
     * UNFINISHED
     */
    public boolean unchanged(String fileName) {
        File f = new File(fileName);
        String curSHA = Utils.sha1(Utils.readContentsAsString(f));
        String saved = null;
        if (curCommit.contains(fileName)) {
            saved = curCommit.getFilePointers().get(fileName);
        }
        return saved != null && curSHA.equals(saved);
    }


    @Override
    public String toString() {
        StringBuilder test = new StringBuilder();
        test.append("=== Staged Files === \n");
        for (String fileName : newStaged) {
            test.append(fileName);
            test.append("\n");
        }

        test.append("\n");

        test.append("=== Removed Files === \n");
        for (String fileName : toRemove) {
            test.append(fileName);
            test.append("\n");
        }
        return test.toString();
    }
}
