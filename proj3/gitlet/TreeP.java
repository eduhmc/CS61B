package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

    public class TreeP implements Serializable {
    public HashSet<String> Commits;
    public Map<String, Branch> Children;
    public Branch CurrentBranch;

    public TreeP() {
        this.Commits = new HashSet<>();
        this.Children = new HashMap<>();
    }
}
