package gitlet;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Upasana Chatterjee
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */



    private static String sep = File.separator;

    /** First operand input.*/
    private static String operand;

    /** Extra argument 1.*/
    private static String arg1;

    /** Extra argument 2.*/
    private static String arg2;


    /** Returns an initial Gitlet system. Only run if there's no
     * system in the current directory.
     */
    private static TreeP init() {
        File dir = new File(".gitlet" + sep);

        if (dir.exists()) {
            System.out.println("A Gitlet version-control system "
                    + "already exists in the current directory.");
            return null;
        }
        dir.mkdirs();
        return TreeP.init();

    }


    /** Returns the system so we can run a new command on it.*/
    private static TreeP load() {
        TreeP test = null;
        File f = new File(".gitlet" + sep + "path");
        if (f.exists()) {
            try {
                ObjectInputStream inp =
                        new ObjectInputStream(new FileInputStream(f));
                test = (TreeP) inp.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return test;
    }

    /** Saves a snapshot of our REPO so we can use it again.*/
    private static void save(TreeP repo) {
        if (repo == null) {
            return;
        }
        try {
            File f = new File(".gitlet" + sep + "path");
            ObjectOutputStream out =
                    new ObjectOutputStream(new FileOutputStream(f));
            out.writeObject(repo);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /** Performs checkout on ARGS in REPO. */
    private static void checkout(TreeP repo, String...args) {
        try {
            if (args.length == 2) {
                arg1 = args[1];
                repo.checkoutB(arg1);
                return;
            } else if (args.length == 3) {
                if (args[1].equals("--")) {
                    arg1 = args[2];
                    repo.checkoutF(arg1);
                    return;
                }
            } else if (args.length == 4) {
                if (args[2].equals("--")) {
                    arg1 = args[1];
                    arg2 = args[3];
                    repo.checkoutC(arg1, arg2);
                    return;
                }
            }
            throw new IllegalArgumentException();

        } catch (IllegalArgumentException e) {
            System.out.println("Incorrect Operands.");
        }
    }

    /** Returns true if ARGS are null. */
    public static boolean nullCase(String... args) {
        if (args == null || args.length == 0) {
            System.out.println("Please enter a command.");
            return true;
        }
        return false;
    }

    /** Sets args to be the ones in ARGS. */
    public static void setArgs(String... args) {
        if (args.length > 1) {
            arg1 = args[1];
        }
        if (args.length > 2) {
            arg2 = args[2];
        }
    }

    /** Main, get input ARGS and does appropriate action.*/
    public static void main(String... args) {
        if (nullCase(args)) {
            return;
        }
        operand = args[0];
        setArgs(args);
        TreeP repo = load();
        try {
            switch (operand) {
                case "init":
                    repo = init();
                    break;
                case "add":
                    repo.add(arg1);
                    break;
                case "commit":
                    repo.commit(arg1);
                    break;
                case "rm":
                    repo.remove(arg1);
                    break;
                case "log":
                    repo.printLog();
                    break;
                case "global-log":
                    repo.printGlobalLog();
                    break;
                case "find":
                    repo.find(arg1);
                    break;
                case "status":
                    repo.status();
                    break;
                case "checkout":
                    checkout(repo, args);
                    break;
                case "branch":
                    repo.addBranch(arg1);
                    break;
                case "rm-branch":
                    repo.removeBranch(arg1);
                    break;
                case "reset":
                    repo.reset(arg1);
                    break;
                case "merge":
                    repo.merge(arg1);
                    break;
                default:
                    System.out.println("No command with that name exists.");
                    break;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Incorrect operands.");
        } catch (NullPointerException a) {
            System.out.println("Not in an initialized Gitlet directory.");
        }
        save(repo);
    }

}


