package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Collections;


public class Main implements Serializable {

    public static void main(String... args) {
        GitHandler GitHandler = new GitHandler();
        GitHandler.handleCommands(args);
    }


}