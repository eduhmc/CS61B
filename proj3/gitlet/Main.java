package gitlet;

import gitlet.Managers.CommandManager;
import gitlet.Managers.GitManager;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author eduhmc00
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) throws IOException {
        CommandManager commandManager = new CommandManager();
        commandManager.HandlerCommandLine(args);
    }


}


