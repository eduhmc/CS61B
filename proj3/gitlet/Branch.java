package gitlet;

import java.io.File;
import java.io.Serializable;

/** This class represents one long chain of unbranching (hon hon,
 * the irony of that) commits, a name, and a stage copy.
 * @author eduhmc
 */

public class Branch implements Serializable {
    public String Id;
    public TreeP Parent;
    public String Name;
    public String StageId;
    public String HeadCommit;

    public Branch(String name, TreeP parent, Commit headCommit) {
        this.Name = name;
        this.HeadCommit = headCommit.getMyID();
        this.Id = Utils.sha1(headCommit.getMyDateStr() + Utils.RandomGen.nextDouble());
        this.StageId = headCommit.getMyStage().getMyID();
        this.Parent = parent;
        this.Parent.Children.put(name, this);
    }

    public void updateHead(Commit commit) {
        this.HeadCommit = commit.getMyID();
    }

    public void updateStage(Stage stage) {
        this.StageId = stage.getMyID();
    }

    public Commit getHeadCommit() {
        return Utils.readObject(new File(".gitlet/objectRepository/" + this.HeadCommit), Commit.class);
    }

    public Stage getMyStage() {
        return Utils.readObject(new File(".gitlet/stages/stage" + this.StageId), Stage.class);
    }
}

