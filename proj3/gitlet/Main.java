package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static gitlet.Utils.error;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Rae Xin
 */
public class Main {

    /** Current Working Directory. */
    static final File CWD = new File(".");

    /** Main metadata folder. */
    static final File GITLET_FOLDER = new File(CWD, ".gitlet");

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        // FILL THIS IN
    }

    public static void initializeRepo() {
        try {
            if (GITLET_FOLDER.createNewFile()) {
                File head = new File(GITLET_FOLDER, "HEAD.txt");
                File branches = new File(GITLET_FOLDER, "branches.txt");
                File commits = new File(GITLET_FOLDER, "commits");
                File blobs = new File(GITLET_FOLDER + File.pathSeparator +
                        "commits", "blobs");
                head.createNewFile();

                Date epoch = new Date(0);
                Commit initialCom = new Commit("initial commit",
                        epoch, null, null);
                processCommit(initialCom);
            } else {
                throw error("A Gitlet version-control system already " +
                        "exists in the current directory.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void processCommit(Commit commit) {

    }

    public static void updateHead() {

    }

    public static void fetchHead() {

    }

}
