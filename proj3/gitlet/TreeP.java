package gitlet;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

/**
 * A picture of the tree data structure used to run gitlet.
 * @author upasanachatterjee
 */
public class TreeP implements Serializable {
    /** A pointer to the current branch.*/
    private Branch curBranch;

    /** Map from each SHA-1 ID to its commit.*/
    private Map<String, Commit> allCommits;

    /** Short ID. */
    private HashMap<String, String> shortid;

    /** Map from each branch name to the corresponding branch.*/
    private Map<String, Branch> branchMap;

    /** Map from commit messages to SHA-1 ID of commits with that message.*/
    private Map<String, ArrayList<String>> msgToID;

    /** Our tree constructor.*/
    public TreeP() {
        shortid = new HashMap<>();
        branchMap = new HashMap<>();
        allCommits = new HashMap<>();
        msgToID = new HashMap<>();
    }

    /** Returns the current branch.*/
    public Branch getCurBranch() {
        return curBranch;
    }



    /** Returns true if there are untracked files in the directory.*/
    public boolean untrackedInDir() {
        File gitlet = new File(
                curBranch.getLatestCommit().getCommitDirectory());
        int len =
                curBranch.getLatestCommit().getCommitDirectory().length();
        int last = gitlet.getAbsolutePath().length();
        String path =
                gitlet.getAbsolutePath().substring(0, last - len + 1);
        File wd = new File(path);
        File[] files = wd.listFiles();
        if (files == null) {
            return false;
        }
        for (File f : files) {
            if (f.isFile()) {
                if (curBranch.getStage() == null) {
                    return false;
                }
                if (!curBranch.getStage().
                        getNewStaged().contains(f.getName())) {
                    if (curBranch.getLatestCommit().contains(f.getName())) {
                        String shawd = Utils.sha1(
                                Utils.readContentsAsString(f));
                        String shacom = curBranch.getLatestCommit().
                                getFilePointers().get(f.getName());
                        if (!shacom.equals(shawd)) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
                if (curBranch.getStage().getStagedFiles().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }


    /** Returns true if there are untracked changes. */
    public boolean untracked() {
        boolean check = false;
        if (untrackedInDir()) {
            System.out.println("There is an untracked file in the way;"
                    + " delete it or add it first.");
            return true;
        }
        return check;
    }


    /** Merges BRANCHNAME and current branch.*/
    public void merge(String branchName) {
        if (branchName == null) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (!branchMap.containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (branchName.equals(curBranch.getName())) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }
        if (untracked()) {
            return;
        }
        Branch merged = branchMap.get(branchName);
        curBranch.merge(merged);
    }
    /**
     * Checks out branch BRANCHNAME iff it exists and is not the
     * current branch and all files are tracked. Otherwise exits out.
     */
    public void checkoutB(String branchName) {
        if (untracked()) {
            return;
        }
        if (!branchMap.containsKey(branchName)) {
            System.out.println("No such branch exists.");
            return;
        }
        if (curBranch.getName().equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        for (String file : curBranch.getLatestCommit().
                getAddedFiles()) {
            String saved = curBranch.getLatestCommit().
                    getFilePointers().get(file);
            String curr = Utils.sha1(
                    Utils.readContentsAsString(new File(file)));
            if (saved.equals(curr)) {
                Utils.restrictedDelete(file);
            } else {
                System.out.println("There is an untracked file in the way; "
                        + "delete it or add it first.");
                return;
            }
        }
        curBranch = branchMap.get(branchName);
        curBranch.getLatestCommit().checkout();
    }


    /**
     * Checks out file FILENAME from the commit with SHA-1 ID
     * COMMITID if both file and commit exist.
     */
    public void checkoutC(String commitID, String fileName) {
        if (commitID.length() == 8) {
            commitID = shortid.get(commitID);
        }
        if (commitID == null) {
            System.out.println("No commit with that id exists.");
            return;
        }
        if (!allCommits.containsKey(commitID)) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit c = allCommits.get(commitID);
        if (!c.contains(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        c.checkout(fileName);
    }

    /**
     * Checks out file FILENAME iff it exists.
     */
    public void checkoutF(String fileName) {
        if (!curBranch.getLatestCommit().contains(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        curBranch.getLatestCommit().checkout(fileName);
    }

    /** Checks out all the files tracked by the given COMMITID.
     * Removes tracked files that are not present in that commit.
     * Also moves the current branch's head to that commit node.
     */
    public void reset(String commitID) {
        if (commitID == null) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (!allCommits.containsKey(commitID)) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit c = allCommits.get(commitID);
        if (untracked()) {
            return;
        }
        c.checkout();
        curBranch.setLatestCommit(c);
        curBranch.setStage(null);
    }


    /** Removes the pointer to branch BRANCHNAME.*/
    public void removeBranch(String branchName) {
        if (branchName == null) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (!branchMap.containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
        }
        if (branchName.equals(curBranch.getName())) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        branchMap.remove(branchName);
    }

    /** Adds a branch with name BRANCHNAME.
     *  Has the same head as the current branch.
     *  */
    public void addBranch(String branchName) {
        if (branchName == null) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (branchMap.containsKey(branchName)) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        Branch b = new Branch(branchName, curBranch.getLatestCommit());
        branchMap.put(branchName, b);
    }

    /**
     * Displays what branches currently exist, and marks the
     * current branch with a *. Also displays what files have
     * been staged or marked for untracking.
     */
    public void status() {
        System.out.println(this.toString());
    }

    /**
     * Prints out the ids of all commits that have
     * the given commit MESSAGE, one per line.
     */
    public void find(String message) {
        if (message == null) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (!msgToID.containsKey(message)) {
            System.out.println("Found no commit with that message.");
            return;
        }

        for (String id : msgToID.get(message)) {
            System.out.println(id);
        }
    }

    /**
     * Starting at the current head commit, displays information about
     * each commit backwards along the commit tree until the initial
     * commit, following the first parent commit links, ignoring any
     * second parents found in merge commits.
     */
    public void printLog() {
        Commit curr = curBranch.getLatestCommit();
        while (curr != null) {
            System.out.println(curr.toString());
            System.out.println();
            curr = curr.getParent();
        }
    }

    /** Like log, except displays information
     * about all commits ever made.*/
    public void printGlobalLog() {
        for (String k : allCommits.keySet()) {
            Commit c = allCommits.get(k);
            System.out.println(c.toString());
            System.out.println();
        }
    }


    /** Adds a copy of the file NAME as it currently
     * exists to the staging area.*/
    public void add(String name) {
        if (name == null) {
            throw new ArrayIndexOutOfBoundsException();
        }
        curBranch.stageFile(name);

    }

    /**
     * Unstage the file FILENAME if it is currently staged. If the file
     * is tracked in the current commit, mark it to indicate that
     * it is not to be included in the next commit, and remove the
     * file from the working directory if the user has not already
     * done so (do not renove it unless it is tracked in the current commit).
     */
    public void remove(String fileName) {
        if (fileName == null) {
            throw new ArrayIndexOutOfBoundsException();
        }
        curBranch.remove(fileName);
    }

    /**
     * Saves a snapshot of certain files in the current commit
     * and staging area so they can be restored at a later time,
     * creating a new commit with a descriptive MESSAGE.
     */
    public void commit(String message) {
        if (message == null) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (message.length() == 0) {
            System.out.println("Please enter a commit message.");
            return;
        }
        curBranch.commit(message);
        Commit curr = curBranch.getLatestCommit();
        allCommits.put(curr.getId(), curr);
        ArrayList<String> temp = msgToID.get(message);
        if (temp == null) {
            temp = new ArrayList<String>();
        }
        temp.add(curr.getId());
        msgToID.put(message, temp);
        shortid.put(curr.getId().substring(0, 8), curr.getId());

    }

    /** Returns a new Gitlet version-control system in the
     * current directory. This system automatically starts
     * with one commit, a single branch: master,
     * and the time stamp is the Unix epoch.
     */
    public static TreeP init() {
        TreeP placeholder = new TreeP();
        String message = "initial commit";
        String branchName = "master";
        Commit init = new Commit(null, message);
        Branch b = new Branch(branchName, init);
        ArrayList<String> ids = new ArrayList<>();
        ids.add(init.getId());
        placeholder.msgToID.put(message, ids);
        placeholder.curBranch = b;
        placeholder.branchMap.put(branchName, b);
        placeholder.allCommits.put(init.getId(), init);

        return placeholder;
    }

    @Override
    public String toString() {
        StringBuilder test = new StringBuilder();
        test.append("=== Branches === \n");
        test.append("*" + getCurBranch().getName() + "\n");

        for (String name : branchMap.keySet()) {
            if (!name.equals(getCurBranch().getName())) {
                test.append(name);
                test.append("\n");
            }
        }
        test.append("\n");
        if (getCurBranch().getStage() != null) {
            test.append(getCurBranch().getStage().toString());
            test.append("\n");
        } else {
            test.append("=== Staged Files === \n \n");
            test.append("=== Removed Files === \n \n");
        }
        test.append("=== Modifications Not Staged For Commit === \n");
        test.append("\n");
        test.append("=== Untracked Files === \n");

        return test.toString();
    }
}
