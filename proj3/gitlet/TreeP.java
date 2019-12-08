package gitlet;
import java.io.Serializable;
import java.util.HashMap;

public class TreeP implements Serializable {

    /** Short ID. */
    private HashMap<String, String> shortid;

    public TreeP (){
        shortid = new HashMap<>();
    }

    public static TreeP init() {
        TreeP placeholder = new TreeP();
        return placeholder;
    }

    public void metodo1(){

    }

    public void metodo2(){

    }

    public void metodo3(){

    }

    public void metodo4(){

    }


}
