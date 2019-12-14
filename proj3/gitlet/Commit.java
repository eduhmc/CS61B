package gitlet;


import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

public class Commit implements Serializable {

    private static final int GETRIDOFTIME1 = 19;
    private static final int GETRIDOFTIME2 = 23;
    private boolean mergeCommit = false;
    private ArrayList<String> allMyParents;
    private String myDateStr;
    private TreeP myTree;
    private Stage myStage;
    private Map<String, String> myFilePointers;
    private String myID;
    private String objectRepo = ".gitlet/objectRepository/";
    private Map<String, String> oldFileToRepoLoc;
    private HashSet<String> myUntrackedFiles;
    private String myParent;
    private String myMessage;
    public String getMyID() {
        return myID;
    }
    public Stage getMyStage() {
        return myStage;
    }
    public String getMyDateStr() {
        return myDateStr;
    }
    public HashSet<String> getMyUntrackedFiles() {
        return myUntrackedFiles;
    }
    public ArrayList<String> getAllMyParents() {
        return allMyParents;
    }
    public Map<String, String> getMyFilePointers() {
        return myFilePointers;
    }
    public Map<String, String> getOldFileToRepoLoc() {
        return oldFileToRepoLoc;
    }
    public String getMyMessage() {
        return myMessage;
    }

    public Commit(String message, Date date, TreeP tree, Stage stage, String parent) {
        myMessage = message;
        myDateStr = date.toString().substring(0, GETRIDOFTIME1) + date.toString().substring(GETRIDOFTIME2) + " -0800";
        myTree = tree;
        myFilePointers = new HashMap<>();
        oldFileToRepoLoc = new HashMap<>();
        myUntrackedFiles = new HashSet<>();
        allMyParents = new ArrayList<>();
        myStage = stage;
        myID = "commit" + stage.getMyID();
        myParent = null;
        setMyParent(parent);
        Utils.writeObject(new File(objectRepo + myID), this);
    }

    public void setMyParent(String parentID) {
        if (parentID == null) {
            myParent = null;
            return;
        }

        Commit commit = Utils.readObject(new File(
                objectRepo + parentID), Commit.class);

        myParent = commit.getMyID();
        for (String keys: commit.oldFileToRepoLoc.keySet()) {
            oldFileToRepoLoc.put(keys, commit.oldFileToRepoLoc.get(keys));
        }
        for (String keys: commit.getMyFilePointers().keySet()) {
            commit.processString(keys);
            myFilePointers.put(keys, commit.getMyFilePointers().get(keys));
        }
        myUntrackedFiles.addAll(commit.getMyUntrackedFiles());
        allMyParents.add(parentID);
    }

    public void addCommit() {
        File myCommitFolder = new File(objectRepo
                + "folder" + myID + "/");
        myCommitFolder.mkdirs();
        File commitFile = new File(objectRepo + myID);
        if (myParent == null) {
            Utils.writeObject(commitFile, this);
        } else {
            Commit myParentCommit = Utils.readObject(new File(objectRepo
                    + myParent), Commit.class);

            String stageDirectory = ".gitlet/stages/" + myStage.getMyID() + "/";

            for (String fileName: Utils.plainFilenamesIn(stageDirectory)) {
                if (myStage.getStagedFileNames().contains(fileName)
                        && !myUntrackedFiles.contains(
                        processStringRev(fileName))) {

                    String currContents = Utils.readContentsAsString(
                            new File(stageDirectory + fileName));

                    if (myParentCommit.getOldFileToRepoLoc()
                            .containsKey(fileName)) {
                        File oldFile = new File(myParentCommit
                                .getOldFileToRepoLoc().get(fileName));
                        String prevContents =
                                Utils.readContentsAsString(oldFile);

                        if (!currContents.equals(prevContents)) {
                            oldFileToRepoLoc.replace(fileName,
                                    myCommitFolder.getPath() + "/"  + fileName);
                            File theFile =
                                    new File(processStringRev(fileName));
                            String contentString =
                                    Utils.readContentsAsString(theFile);
                            myFilePointers.replace(fileName,
                                    Utils.sha1(stageDirectory + fileName));
                            Utils.writeContents(new File(myCommitFolder.getPath() + "/" + fileName), contentString);
                        }
                    } else {
                        oldFileToRepoLoc.put(fileName, myCommitFolder.getPath() + "/"  + fileName);
                        File writeFile = new File(stageDirectory + fileName);
                        String contentString =
                                Utils.readContentsAsString(writeFile);
                        myFilePointers.put(fileName,
                                Utils.sha1(stageDirectory + fileName));
                        Utils.writeContents(new File(myCommitFolder.getPath() + "/"  + fileName), contentString);
                    }
                }
            }
            Utils.writeObject(commitFile, this);
        }
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

    @Override
    public String toString() {
        StringBuilder myStringRepr = new StringBuilder();
        myStringRepr.append("===\n");
        myStringRepr.append("commit " + myID.substring(6) + "\n");
        if (mergeCommit) {
            myStringRepr.append("Merge:");
            for (String parents: allMyParents) {
                myStringRepr.append(" " + parents.substring(7, 14));
            }
            myStringRepr.append("\n");
        }
        myStringRepr.append("Date: " + myDateStr + "\n");
        myStringRepr.append(myMessage + "\n");
        return myStringRepr.toString();
    }

    public Commit getMyParentCommit() {
        File parentFile = new File(objectRepo + myParent);
        try {
            Commit retCommit = Utils.readObject(parentFile, Commit.class);
            return retCommit;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public void setMeToMergeCommit() {
        mergeCommit = true;
        Utils.writeObject(new File(objectRepo + myID), this);
    }

}
