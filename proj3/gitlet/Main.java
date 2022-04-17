package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static gitlet.Utils.error;
import static gitlet.Utils.sha1;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Rae Xin
 */
public class Main {

    /** Current Working Directory. */
    public static final File CWD = new File(".");

    /** Main metadata folder. */
    public static final File GITLET_FOLDER = new File(CWD, ".gitlet");

    /** HEAD text file that stores HEAD pointer to current commit. */
    public static final File HEAD_FILE = new File(GITLET_FOLDER, "HEAD.txt");

    /** File that holds information about all branches. */
    public static final File BRANCHES_FILE = new File(GITLET_FOLDER, "branches.txt");

    /** Folder that holds all Commit files. */
    public static final File COMMITS_FOLDER = new File(GITLET_FOLDER, "commits");

    /** Folder that holds all Blob files. */
    public static final File BLOBS_FOLDER = new File(COMMITS_FOLDER, "blobs");

    public static final Stage ADD_STAGE = new Stage();

    public static final Stage REMOVE_STAGE = new Stage();

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        if (args.length == 0) {

        }
        switch (args[0]) {
            case "init":
                initializeRepo();
                break;
            case "commit":
            case "add":
            case "rm":
            case "log":
            case "global-log":
            case "find":
            case "status":
            case "checkout":
            case "branch":
            case "rm-branch":
            case "reset":
            case "merge":
        }
    }

    public static void initializeRepo() {
        try {
            if (GITLET_FOLDER.mkdir()) {

                HEAD_FILE.createNewFile();
                BRANCHES_FILE.createNewFile();
                COMMITS_FOLDER.mkdir();
                BLOBS_FOLDER.mkdir();
                Date epoch = new Date(0);
                String commitHash = processCommit("initial commit",
                        epoch, ADD_STAGE, REMOVE_STAGE, null);

                updateBranch("master", commitHash);
                // put commit hash into branches and head

            } else {
                throw error("A Gitlet version-control system already " +
                        "exists in the current directory.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String processCommit(String message, Date timestamp, Stage addStage,
                                     Stage removeStage, String parent) {
        Commit commit = new Commit(message, timestamp, ADD_STAGE, REMOVE_STAGE, parent);
        return processCommit(commit);
    }

    public static String processCommit(String message, Date timestamp, Stage stage,
                                     Stage removeStage, String parent, String parent2) {
        Commit commit = new Commit(message, timestamp, ADD_STAGE, REMOVE_STAGE, parent, parent2);
        return processCommit(commit);
    }

    public static String processCommit(Commit commit) {

        return commit.getHash();
    }

    public static void updateBranch(String branchName, String commitHash) {
        Branch newBranch = new Branch(branchName, commitHash);
        newBranch.exportBranch();
    }

    public static void updateHead() {

    }

    public static void fetchHead() {

    }

}