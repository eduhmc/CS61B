package gitlet;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Set;
import java.text.SimpleDateFormat;
import java.nio.file.Paths;
import java.nio.file.Path;


/** Class defining what a commit is supposed to look like.
 * @author upasanachatterjee
 */
public class Commit implements Serializable {

    /** Map from file name to its SHA-1 ID.
     * Inherits from parent and updates iff file is different
     * from parent commit's version.*/
    private Map<String, String> filePointers;

    /** Map from name of file added to its commit SHA-1 ID.*/
    private Map<String, String> addedFiles;

    /** The commit message.*/
    private String msg;

    /** The commit times.*/
    private Date time;

    /** The directory the commit is in. */
    private String commitDirectory;

    /** Parent of the current commit.*/
    private Commit parent;

    /** The SHA-1 ID of the commit.*/
    private String id;

    /** The appropriate separator for the system.*/
    private String sep = File.separator;

    /**True if Commit is a merge.*/
    private boolean merged = false;

    /** Merge parent IDs.*/
    private String parents;


    /** Returns the SHA-1 ID of the commit.*/
    public String getId() {
        return id;
    }


    /**
     * Takes the stage SNAPSHOT and gets the commit information from it.
     * CHECK THAT SNAPSHOT == NULL IFF INIT IS RUN
     */
    public Commit(Stage snapshot) {
        addedFiles = new HashMap<>();
        filePointers = new HashMap<>();
        if (snapshot != null) {
            parent = snapshot.getCurCommit();
            time = new Date();
        } else {
            String date = "Wed Dec 31 16:00:00 1969 -0800";

            SimpleDateFormat dt = new SimpleDateFormat(
                    "EEE MMM d HH:mm:ss yyyy Z");
            Date d = null;
            try {
                d = dt.parse(date);
            } catch (ParseException p) {
                System.out.println(p.getMessage());
            }
            time = d;
        }
        if (parent != null) {
            addedFiles.putAll(parent.addedFiles);
            if (snapshot.getToRemove() != null) {
                for (String file : snapshot.getToRemove()) {
                    if (addedFiles.containsKey(file)) {
                        addedFiles.remove(file);
                    }
                }
            }
            filePointers.putAll(parent.filePointers);
        }
    }


    /** Creates a commit object of stage at SNAPSHOT with MESSAGE.*/
    public Commit(Stage snapshot,  String message) {
        this(snapshot);
        msg = message;
        if (snapshot == null) {
            id = Utils.sha1(getTime().toString(), getMsg());
        } else {
            id = Utils.sha1(snapshot.getNewStaged().toString(),
                    getParent().getId(), getMsg(), getTime().toString());
        }
        commitToDirectory(snapshot);
        if (!unchangedCommit()) {
            commitDirectory = ".gitlet" + sep + "objects" + sep + getId() + sep;
            saveFiles();
        } else {
            System.out.println("No changes added to the commit.");
        }
    }

    /** Commits SNAPSHOT with MESSAGE and parents PAR1 PAR2.
     * Only for merge commit.*/
    public Commit(Stage snapshot, String message, String par1, String par2) {
        this(snapshot, message);
        merged = true;
        parents = "Merge: " + par1 + " " + par2;

    }

    /** Returns true if current commit is the same as its parent.*/
    public boolean unchangedCommit() {
        if (parent == null) {
            return false;
        }
        Set prevFiles = parent.getAddedFiles();
        Set curFiles = getAddedFiles();
        if (curFiles.size() != prevFiles.size()) {
            return false;
        }
        for (String name : addedFiles.keySet()) {
            if (prevFiles.contains(name)) {
                String curSHA1 = filePointers.get(name);
                String prevSHA1 = parent.getFilePointers().get(name);
                if (!curSHA1.equals(prevSHA1)) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }


    /**
     * Adds changed fileNames from SNAPSHOT to the new commit. !!
     * Should also check that nonedited files are also added!!
     */
    public void commitToDirectory(Stage snapshot) {
        if (snapshot != null) {
            for (String fileName : snapshot.getStagedFiles()) {
                File f = new File(fileName);
                String id2 = Utils.sha1(Utils.readContentsAsString(f));
                if (filePointers.containsKey(fileName)) {
                    String id1 = filePointers.get(fileName);
                    if (!id1.equals(id2)) {
                        addedFiles.put(fileName, id);
                        filePointers.replace(fileName, id2);
                    }
                } else {
                    addedFiles.put(fileName, id);
                    filePointers.put(fileName, id2);
                }
            }
        }
    }

    /**
     * Checks out file FILENAME.
     */
    public void checkout(String fileName) {
        File f = new File(fileName);
        File dir = new File(commitDirectory
                + filePointers.get(fileName) + sep + f);
        try {
            Utils.writeContents(f, Utils.readContentsAsString(dir));
        } catch (GitletException e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * Checks out our current commit.
     */
    public void checkout() {
        Set<String> help = addedFiles.keySet();
        for (String item : help) {
            checkout(item);
        }
        File gitlet = new File(commitDirectory);
        int len = commitDirectory.length();
        int last = gitlet.getAbsolutePath().length();
        String path =
                gitlet.getAbsolutePath().substring(0, last - len + 1);
        File wd = new File(path);
        File[] files = wd.listFiles();
        for (File f: files) {
            if (!help.contains(f.getName())) {
                Utils.restrictedDelete(f);
            }
        }
    }


    /**
     * Creates a directory.
     */
    public void saveFiles() {
        File dir = new File(commitDirectory);

        dir.mkdirs();
        if (addedFiles.isEmpty()) {
            return;
        }
        for (String name : addedFiles.keySet()) {
            File f = new File(name);
            Path p = Paths.get(commitDirectory
                    + filePointers.get(name) + sep + name);
            try {
                Files.createDirectories(p.getParent());
                Files.createFile(p);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            String contents = Utils.readContentsAsString(new File(name));
            Utils.writeContents(p.toFile(), contents);
            addedFiles.put(name, commitDirectory);
        }
    }

    /** Returns true if file FILENAME has been unchanged between
     * HEAD commit and this commit.
     */
    public boolean unModified(Commit head, String fileName) {
        File one = this.getFile(fileName);
        File two = head.getFile(fileName);
        if (filePointers.containsKey(fileName)) {
            return head.getFilePointers().containsKey(fileName)
                    && head.getFilePointers().get(fileName).equals(
                    this.filePointers.get(fileName));
        }
        if (head.getFilePointers().containsKey(fileName)) {
            return getFilePointers().containsKey(fileName)
                    && head.getFilePointers().get(fileName).equals(
                    this.filePointers.get(fileName));
        }
        String curr = Utils.readContentsAsString(one);
        String orig = Utils.readContentsAsString(two);
        return curr.equals(orig);
    }



    /** Returns true if the commit contains file FILENAME.*/
    public boolean contains(String fileName) {
        return addedFiles != null && addedFiles.containsKey(fileName);
    }

    /** Returns file NAME. */
    public File getFile(String name) {
        String path = commitDirectory + filePointers.get(name) + sep + name;
        File saved = new File(path);
        return saved;
    }


    @Override
    public String toString() {
        StringBuilder test = new StringBuilder();
        test.append("=== \n");
        test.append("commit " + id + " \n");
        if (merged) {
            test.append(parents + "\n");
        }
        SimpleDateFormat dt1 = new SimpleDateFormat(
                "EEE MMM d HH:mm:ss yyyy Z");
        String timeStamp = dt1.format(time);

        test.append("Date: " + timeStamp + " \n");
        test.append(msg);

        return test.toString();
    }

    /** Returns map, of file name to directory.*/
    public Set<String> getAddedFiles() {
        return addedFiles.keySet();
    }

    /** Returns commit message.*/
    public String getMsg() {
        return msg;
    }

    /** Returns commit time. */
    public Date getTime() {
        return time;
    }

    /** Returns File pointers. */
    public Map<String, String> getFilePointers() {
        return filePointers;
    }

    /** Returns commit directory.*/
    public String getCommitDirectory() {
        return commitDirectory;
    }

    /** Returns separator.*/
    public String getSep() {
        return sep;
    }

    /** Returns parent of current commit.*/
    public Commit getParent() {
        return parent;
    }

}
