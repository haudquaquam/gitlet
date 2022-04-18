package gitlet;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;

import static gitlet.Branch.*;
import static gitlet.Stage.addBlob;
import static gitlet.Stage.removeBlob;
import static gitlet.Utils.*;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Rae Xin
 */
public class Main {

    /** Current Working Directory. */
    public static final File CWD = new File(".");

    /** Main metadata folder. */
    public static final File GITLET_FOLDER =
            new File(CWD, ".gitlet");

    /** HEAD text file that stores HEAD pointer to current commit. */
    public static final File HEAD_FILE =
            new File(GITLET_FOLDER, "HEAD.txt");

    /** File that holds information about all branches. */
    public static final File BRANCHES_FILE =
            new File(GITLET_FOLDER, "branches.txt");

    public static final File HEAD_BRANCHES_FILE =
            new File(GITLET_FOLDER, "HEAD_branch.txt");

    /** Folder that holds all Commit files. */
    public static final File COMMITS_FOLDER =
            new File(GITLET_FOLDER, "commits");

    /** Folder that holds all Blob files. */
    public static final File BLOBS_FOLDER =
            new File(COMMITS_FOLDER, "blobs");

    public static final File ADD_STAGE_FILE =
            new File(GITLET_FOLDER, "add_stage.txt");

    public static final File REMOVE_STAGE_FILE =
            new File(GITLET_FOLDER, "remove_stage.txt");

    public static String HEAD_BRANCH_NAME;

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        if (args.length == 0) {
            throw error("Please enter a command.");
        }
        switch (args[0]) {
            case "init":
                initializeRepo();
                break;
            case "commit":
                if (!(args.length > 1)) {
                    throw error("Please enter a commit message.");
                }
                Commit newCommit = new Commit(args[1], new Date(),
                        fetchHeadCommit().getHash());
                if (!newCommit.validCommit()) {
                    throw error("No changes added to the commit.");
                } else {
                    processCommit(newCommit);
                    newCommit.processStage();
                    updateHead(newCommit);
                }
                break;
            case "add":
                if (args.length != 2) {
                    throw error("Incorrect operands.");
                }
                File addFile = new File(CWD, args[1]);
                if (!addFile.exists()) {
                    throw error("File does not exist.");
                }
                stageForAddition(addFile);
                break;
            case "rm":
                if (args.length != 2) {
                    throw error("Incorrect operands.");
                }
                File removeFile = new File(CWD, args[1]);
                if (!removeFile.exists()) {
                    throw error("File does not exist.");
                }
                stageForRemoval(removeFile);
                break;
            case "log":
                displayLog();
                break;
            case "global-log":
                displayGlobalLog();
                break;
            case "find":
                findAllCommitsByMessage(args[1]);
                break;
            case "status":
                break;
            case "checkout":
                break;
            case "branch":
                break;
            case "rm-branch":
                break;
            case "reset":
                break;
            case "merge":
            default:
                throw error("No command with that name exists.");
        }
    }

    public static void initializeRepo() {
        try {
            if (GITLET_FOLDER.mkdir()) {

                HEAD_FILE.createNewFile();
                BRANCHES_FILE.createNewFile();
                COMMITS_FOLDER.mkdir();
                BLOBS_FOLDER.mkdir();
                HEAD_BRANCHES_FILE.createNewFile();
                ADD_STAGE_FILE.createNewFile();
                REMOVE_STAGE_FILE.createNewFile();
                clearAddStage();
                clearRemoveStage();
                Date epoch = new Date(0);
                String commitHash = processCommit("initial commit",
                        epoch, null);

                updateBranch("master", commitHash);
                setNewBranchHead("master");
                // put commit hash into branches and head

            } else {
                throw error("A Gitlet version-control system already " +
                        "exists in the current directory.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String processCommit(String message, Date timestamp,
                                       String parent) {
        Commit commit = new Commit(message, timestamp, parent);
        return processCommit(commit);
    }


    /*public static String processCommit(String message, Date timestamp, Stage stage,
                                     Stage removeStage, String parent, String parent2) {
        Commit commit = new Commit(message, timestamp, ADD_STAGE_FILE, REMOVE_STAGE_FILE, parent, parent2);
        return processCommit(commit);
    }*/
    public static String processCommit(Commit commit) {
        String hash = commit.getHash();
        commit.exportCommit();
        updateHead(commit);
        return hash;
    }

    public static void updateBranch(String branchName, String commitHash) {
        Branch newBranch = new Branch(branchName, commitHash);
        newBranch.exportBranch();
    }

    public static void updateHead(Commit newCommit) {
        writeObject(HEAD_FILE, newCommit);
    }

    public static Commit fetchHeadCommit() {
        return readObject(HEAD_FILE, Commit.class);
    }

    public static void stageForAddition(File file) {
        Blob blob = new Blob(file);
        addBlob(blob);
    }

    public static void stageForRemoval(File file) {
        Blob blob = new Blob(file);
        removeBlob(blob);
    }

    public static Stage fetchAddStage() {
        return readObject(ADD_STAGE_FILE, Stage.class);
    }

    public static Stage fetchRemoveStage() {
        return readObject(REMOVE_STAGE_FILE, Stage.class);
    }

    public static void clearAddStage() {
        Stage empty = new Stage();
        writeObject(ADD_STAGE_FILE, empty);
    }

    public static void clearRemoveStage() {
        Stage empty = new Stage();
        writeObject(REMOVE_STAGE_FILE, empty);
    }

    private static void displayLog() {
        Commit currentCommit = fetchHeadCommit();
        while (currentCommit != null) {
            printCommit(currentCommit);
            if (!currentCommit.hasParent()) {
                break;
            }
            currentCommit = currentCommit.getParentCommit();
        }
    }

    /** Format: Thu Nov 9 17:01:33 2017 -0800
     * */
    public static String formatDate(Date date) {
        SimpleDateFormat formatter =
                new SimpleDateFormat("E MMM dd HH:mm:ss yyyy Z");
        return formatter.format(date);

    }

    public static void printCommit(Commit commit) {
        System.out.println("===");
        System.out.println("commit " + commit.getHash());
        System.out.println("Date: " + commit.getDate());
        System.out.println(commit.getMessage());
        System.out.println();
    }

    private static void displayGlobalLog() {
        ArrayList<String> listFileNames = new ArrayList<>(plainFilenamesIn(COMMITS_FOLDER));
        for (String hash : listFileNames) {
            Commit currentCommit = Commit.importCommit(hash);
            printCommit(currentCommit);
        }
    }

    private static void findAllCommitsByMessage(String message) {
        ArrayList<String> listFileNames = new ArrayList<>(plainFilenamesIn(COMMITS_FOLDER));
        int foundCommits = 0;
        for (String hash : listFileNames) {
            Commit currentCommit = Commit.importCommit(hash);
            if (currentCommit.getMessage().equals(message)) {
                System.out.println(currentCommit.getHash());
                foundCommits++;
            }
        }
        if (foundCommits == 0) {
            throw error("Found no commit with that message.");
        }
    }

    private static void displayStatus() {
        Branch branch = importBranches();
        ArrayList<String> branchNameArray = new ArrayList<>(branch.getMap().values());
        String headBranchName = getHeadBranchName();
        int index = 0;

    }
}