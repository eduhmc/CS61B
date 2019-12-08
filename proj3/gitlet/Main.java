package gitlet;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        // FILL THIS IN
    }
    private static TreeP Arbol1;

    private static String opcion1;

    private static String sep = File.separator;


    public static void main(String args) {
        if (nullCase(args)) {
            return;
        }
        Arr(args);
    }

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

    public static void Arr(String Argumentos){

        opcion1 = Argumentos;

        if(opcion1=="init"){
            Arbol1 = init();
            save(Arbol1);
        } else{
            System.out.println("Incorrect operands.");
        }
    }

    public static boolean nullCase(String args) {
        if (args == null) {
            System.out.println("Please enter a command.");
            return true;
        }
        return false;
    }

}

