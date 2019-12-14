package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class Stage implements Serializable {
    private HashSet<String> stagedFileNames;
    private String myID;
    private File myFolder;
    private ArrayList<String> removedFiles;
    private Commit curCommit;
    private String objectRepo = ".gitlet/objectRepository/";

    public ArrayList<String> getRemovedFiles() {
        return removedFiles;
    }
    public HashSet<String> getStagedFileNames() {
        return stagedFileNames;
    }
    public File getMyFolder() {
        return myFolder;
    }
    public String getMyID() {
        return myID;
    }

    public Stage(Date date) {
        stagedFileNames = new HashSet<>();
        removedFiles = new ArrayList<String>();
        myID = Utils.sha1(date.toString() + Utils.RandomGen.nextDouble());
        myFolder = new File(".gitlet/stages/" + myID);
        myFolder.mkdirs();
    }

    public void saveStage() {
        File myFile = new File(".gitlet/stages/stage" + myID);
        Utils.writeObject(myFile, this);
    }

    void setMyCommit(Commit commit) {
        curCommit = commit;
        saveStage();
    }

    public void add(String fileName) {
        File thisFile = new File(fileName);

        if (!thisFile.exists()) {
            System.out.println("File does not exist.");
            return;
        }

        if (removedFiles.contains(fileName)) {
            removedFiles.remove(fileName);
        }
        if (curCommit.getMyParentCommit() != null) {
            System.out.println("curCommitParent untracked files: \n" + curCommit.getMyParentCommit().getMyUntrackedFiles());
        } else {
            System.out.println("curCommit untracked files: \n" + curCommit.getMyUntrackedFiles());
        }
        if (curCommit.getMyUntrackedFiles().contains(fileName)) {
            curCommit.getMyUntrackedFiles().remove(fileName);
        }

        if (curCommit.getOldFileToRepoLoc().containsKey(fileName)) {
            File previousFile = new File(curCommit
                    .getOldFileToRepoLoc().get(fileName));

            if (Utils.readContentsAsString(previousFile)
                    .equals(Utils.readContentsAsString(thisFile))) {
                File writtenFile = new File(
                        myFolder.getPath() + "/" + fileName);
                stagedFileNames.remove(fileName);
                writtenFile.delete();
                return;
            }
        }

        fileName = processString(fileName);
        File writtenFile = new File(myFolder.getPath() + "/" + fileName);

        if (stagedFileNames.contains(fileName)) {
            writtenFile.delete();
            stagedFileNames.remove(fileName);
        }
        stagedFileNames.add(fileName);
        String contentString = Utils.readContentsAsString(
                new File(curCommit.processStringRev(fileName)));
        Utils.writeContents(writtenFile, contentString);

        Utils.writeObject(new File(objectRepo
                + curCommit.getMyID()), curCommit);
        saveStage();
    }

    public String processString(String name) {
        if (name.contains("\\")) {
            name = name.replace("\\", "@");
        }
        return name;
    }

    public String processStringRev(String name) {
        if (name.contains("@")) {
            name = name.replace("@", "\\");
        }
        return name;
    }
}
