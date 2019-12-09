package gitlet;
import java.io.Serializable;
import java.io.File;

/**
 * This class represents a branch in our data structure.
 * Contains pointers to Commits.
 * @author upasanachatterjee
 */
class Branch implements Serializable {
    /** Name of the branch.*/
    private String name;

    /** Head of the branch.*/
    private Commit latestCommit;

    /** Picture of the current staging area.*/
    private Stage stage;

    /** Each branch object has a BNAME, a pointer to a HEAD commit,
     * and a pointer to a staging area.
     */
    public Branch(String bname, Commit head) {
        name = bname;
        latestCommit = head;
        stage = new Stage(head);
    }

    /** Adds file N to the staging area.*/
    public void stageFile(String n) {
        stage.add(n);
    }

    /** Sets LC to be be the latest commit.*/
    public void setLatestCommit(Commit lc) {
        this.latestCommit = lc;
    }

    /** Sets S to be be the current stage.*/
    public void setStage(Stage s) {
        this.stage = s;
    }

    /** Returns true if GIVEN is an ancestor of SPLIT.*/
    public boolean ancestor(Commit given, Commit split) {
        return given.equals(split);
    }

    /** Changes file ADDED in working directory according
     * to conflicts in GIVENHEAD and CURRHEAD.*/
    public void mergeConflict(Commit currHead, Commit givenHead, String added) {
        String h = "<<<<<<< HEAD\n";
        String dash = "=======\n";
        String g = ">>>>>>>\n";
        String fHead = "";
        String gHead = "";
        if (currHead.contains(added)) {
            fHead = Utils.readContentsAsString(
                    currHead.getFile(added));
        }
        if (givenHead.contains(added)) {
            gHead = Utils.readContentsAsString(
                    givenHead.getFile(added));
        }
        Utils.writeContents(new File(added), h,
                fHead, dash, gHead, g);
    }
    /** Merges the current branch with branch B.*/
    public void merge(Branch b) {
        Boolean conflict = false;
        Commit givenHead = b.getLatestCommit();
        Commit split = split(this, b);
        Commit currHead = latestCommit;
        if (!stage.getNewStaged().isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return;
        }
        if (ancestor(givenHead, split)) {
            System.out.println("Given branch is an ancestor "
                    + "of the current branch.");
            return;
        }
        if (ancestor(currHead, split)) {
            latestCommit = givenHead;
            System.out.println("Current branch fast-forwarded.");
            return;
        }
        for (String fileName : givenHead.getAddedFiles()) {
            if (!split.contains(fileName) && !currHead.contains(fileName)) {
                givenHead.checkout(fileName);
                stageFile(fileName);
            } else if (split.contains(fileName)
                    && givenHead.unModified(split, fileName)) {
                Boolean filler = true;
            } else if (!givenHead.unModified(currHead, fileName)) {
                mergeConflict(currHead, givenHead, fileName);
                conflict = true;
            } else if (currHead.contains(fileName)
                    && currHead.unModified(split, fileName)) {
                mergeSame(givenHead, fileName);
            }
        }
        for (String fileName : currHead.getAddedFiles()) {
            if (currHead.unModified(split, fileName)
                    && !givenHead.contains(fileName)) {
                remove(fileName);
            } else if (!split.contains(fileName)
                    && !givenHead.contains(fileName)) {
                Boolean filler = true;
            } else if (!givenHead.unModified(currHead, fileName)) {
                mergeConflict(currHead, givenHead, fileName);
                conflict = true;
            }
        }
        String message = "Merged " + b.getName() + " into "
                + this.getName() + ".";
        String par1 = currHead.getId().substring(0, 7);
        String par2 = givenHead.getId().substring(0, 7);
        commit(message, par1, par2);
        if (conflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }


    /** Merges file ADDED into CURRHEAD if unchanged in CURRHEAD since SPLIT
     * and changed in GIVENHEAD.
     */
    public void mergeSame(Commit givenHead, String added) {
        File check = new File(added);
        if (check.exists()) {
            File f = new File(
                    givenHead.getCommitDirectory()
                            + givenHead.getFilePointers().get(added)
                            + givenHead.getSep()
                            + added);
            Utils.writeContents(check,
                    Utils.readContentsAsString(f));
        }

    }

    /**
     * Returns the split point of A and B.
     */
    public Commit split(Branch a, Branch b) {
        int aLen = a.length();
        int bLen = b.length();
        if (aLen < bLen) {
            return split(b, a);
        }
        Commit aCommit = a.getLatestCommit();
        Commit bCommit = b.getLatestCommit();

        for (int i = 0; i < aLen - bLen; i++) {
            aCommit = aCommit.getParent();
        }
        while (!aCommit.equals(bCommit)) {
            aCommit = aCommit.getParent();
            bCommit = bCommit.getParent();
        }
        return aCommit;

    }

    /** removes file N from the staging area.*/
    public void remove(String n) {
        stage.remove(n);
    }

    /** Makes a commit with MESSAGE as its message.*/
    public void commit(String message) {
        latestCommit = new Commit(stage, message);
        stage = new Stage(latestCommit);

    }

    /** Commit format for a merge commit with MESSAGE and parents
     * parents PAR1 PAR2.*/
    public void commit(String message, String par1, String par2) {
        latestCommit = new Commit(stage, message, par1, par2);
        stage = new Stage(latestCommit);
    }

    /** Returns the head of the branch.*/
    public Commit getLatestCommit() {
        return latestCommit;
    }

    /** Returns the current stage. */
    public Stage getStage() {
        return stage;
    }
    /** Returns the name of the branch. */
    public String getName() {
        return name;
    }



    /** Returns length of the current branch.*/
    private int length() {
        int len = 0;
        Commit cur = latestCommit;
        while (cur != null) {
            len++;
            cur = cur.getParent();
        }
        return len;
    }


}
