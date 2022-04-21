package gitlet;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Blob.getBlobHash;
import static gitlet.Branch.*;
import static gitlet.Commit.*;
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

    /** File that holds the String name of the current active branch. */
    public static final File ACTIVE_BRANCH_FILE =
            new File(GITLET_FOLDER, "HEAD_branch.txt");

    /** File that holds BranchHead object that maps each branch to its
     * current head commit. */
    public static final File BRANCHHEAD_FILE =
            new File(GITLET_FOLDER, "branch_head.txt");

    /** Folder that holds all Commit files. */
    public static final File COMMITS_FOLDER =
            new File(GITLET_FOLDER, "commits");

    /** Folder that holds all Blob files. */
    public static final File BLOBS_FOLDER =
            new File(COMMITS_FOLDER, "blobs");

    /** File that holds the Stage object that represents
     * the Add Stage. */
    public static final File ADD_STAGE_FILE =
            new File(GITLET_FOLDER, "add_stage.txt");

    /** File that holds the Stage object that represents
     * the Remove Stage. */
    public static final File REMOVE_STAGE_FILE =
            new File(GITLET_FOLDER, "remove_stage.txt");

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
                        fetchHeadCommitHash());
                if (!newCommit.validCommit()) {
                    throw error("No changes added to the commit.");
                } else {
                    updateActiveBranchWithLatestCommit(processCommit(newCommit));
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
                if (args.length > 1) {
                    throw error("Incorrect operands.");
                }
                displayLog();
                break;
            case "global-log":
                if (args.length > 1) {
                    throw error("Incorrect operands.");
                }
                displayGlobalLog();
                break;
            case "find":
                if (args.length != 2) {
                    throw error("Incorrect operands.");
                }
                findAllCommitsByMessage(args[1]);
                break;
            case "status":
                if (args.length > 1) {
                    throw error("Incorrect operands.");
                }
                displayStatus();
                break;
            case "checkout":
                if (args.length < 2 || args.length > 4) {
                    throw error("Incorrect operands.");
                }
                if (args[1].equals("--")) {
                    checkoutFile(fetchHeadCommit(), args[2]);
                } else if (args[2].equals("--")) {
                    checkoutFile(importCommit(args[1]), args[3]);
                } else if (args.length == 2) {
                    checkoutBranch(args[1]);
                } else {
                    throw error("Incorrect operands.");
                }
                break;
            case "branch":
                if (args.length != 2) {
                    throw error("Incorrect operands.");
                }
                updateBranch(args[1], fetchHeadCommitHash());
                break;
            case "rm-branch":
                if (args.length != 2) {
                    throw error("Incorrect operands.");
                }
                deleteBranch(args[1]);
                break;
            case "reset":
                if (args.length != 2) {
                    throw error("Incorrect operands.");
                }
                reset(args[1]);
                break;
            case "merge":
                break;
            default:
                throw error("No command with that name exists.");
        }
    }

    /** Initializes the repository. */
    public static void initializeRepo() {
        try {
            if (GITLET_FOLDER.mkdir()) {

                HEAD_FILE.createNewFile();
                BRANCHES_FILE.createNewFile();
                COMMITS_FOLDER.mkdir();
                BLOBS_FOLDER.mkdir();
                ACTIVE_BRANCH_FILE.createNewFile();
                ADD_STAGE_FILE.createNewFile();
                REMOVE_STAGE_FILE.createNewFile();
                BRANCHHEAD_FILE.createNewFile();
                clearAddStage();
                clearRemoveStage();
                Date epoch = new Date(0);
                String commitHash = processCommit("initial commit",
                        epoch, null);

                updateBranch("master", commitHash);
                updateActiveBranch("master");
                // put commit hash into branches and head

            } else {
                throw error("A Gitlet version-control system already " +
                        "exists in the current directory.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Handles processing of a commit with MESSAGE, TIMESTAMP, and PARENT. */
    public static String processCommit(String message, Date timestamp,
                                       String parent) {
        Commit commit = new Commit(message, timestamp, parent);
        return processCommit(commit);
    }

    /** Handles the processing of COMMIT returns its hash. */
    public static String processCommit(Commit commit) {
        String hash = commit.getHash();
        commit.processStage();
        commit.exportCommit();
        updateHeadCommit(commit);
        return hash;
    }

    /** Sets the branch named BRANCHNAME to point to the Commit with
     * the hash, COMMITHASH. */
    public static void updateBranch(String branchName, String commitHash) {
        Branch newBranch = new Branch(branchName, commitHash);
        newBranch.exportBranch();
    }

    /** Sets NEWCOMMIT to be the current head commit. */
    public static void updateHeadCommit(Commit newCommit) {
        writeObject(HEAD_FILE, newCommit);
    }

    /** Returns the current head commit. */
    public static Commit fetchHeadCommit() {
        return readObject(HEAD_FILE, Commit.class);
    }

    /** Returns the hash of the current head commit. */
    public static String fetchHeadCommitHash() {
        return fetchHeadCommit().getHash();
    }

    /** Takes FILE, and adds that to the Add Stage. */
    public static void stageForAddition(File file) {
        Blob blob = new Blob(file);
        addBlob(blob);
    }

    /** Takes FILE, and adds that to the Remove Stage. */
    public static void stageForRemoval(File file) {
        Blob blob = new Blob(file);
        removeBlob(blob);
    }

    /** Returns Stage object representing the current Add
     * Stage. */
    public static Stage fetchAddStage() {
        return readObject(ADD_STAGE_FILE, Stage.class);
    }

    /** Returns Stage object representing the current Remove
     * Stage. */
    public static Stage fetchRemoveStage() {
        return readObject(REMOVE_STAGE_FILE, Stage.class);
    }

    /** Clears the current Add Stage. */
    public static void clearAddStage() {
        Stage empty = new Stage();
        writeObject(ADD_STAGE_FILE, empty);
    }

    /** Clears the current Remove Stage. */
    public static void clearRemoveStage() {
        Stage empty = new Stage();
        writeObject(REMOVE_STAGE_FILE, empty);
    }

    /** Displays log of the current branch. */
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

    /** Handles formatting of DATE. Returns a String of the formatted Date. */
    public static String formatDate(Date date) {
        SimpleDateFormat formatter =
                new SimpleDateFormat("E MMM dd HH:mm:ss yyyy Z");
        return formatter.format(date);
    }

    /** Prints out information about COMMIT. */
    public static void printCommit(Commit commit) {
        System.out.println("===");
        System.out.println("commit " + commit.getHash());
        System.out.println("Date: " + commit.getDate());
        System.out.println(commit.getMessage());
        System.out.println();
    }
    /** Displays all commits made, ever. */
    private static void displayGlobalLog() {
        ArrayList<String> listFileNames = new ArrayList<>(plainFilenamesIn(COMMITS_FOLDER));
        for (String hash : listFileNames) {
            Commit currentCommit = Commit.importCommit(hash);
            printCommit(currentCommit);
        }
    }

    /** Prints hashes of all commits with the message, MESSAGE. */
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

    /** Displays status by printing. */
    private static void displayStatus() {
        Branch branch = importBranches();
        ArrayList<String> branchNameArray =
                new ArrayList<>(branch.getMap().keySet());
        Collections.sort(branchNameArray);
        String activeBranchName = fetchActiveBranchName();
        System.out.println("=== Branches ===");
        for (String branchName : branchNameArray) {
            if (branchName.equals(activeBranchName)) {
                System.out.print("*");
            }
            System.out.println(branchName);
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        Stage addStage = fetchAddStage();
        ArrayList<String> addedFiles =
                new ArrayList<>(addStage.getStage().values());
        Collections.sort(addedFiles);
        for (String fileName : addedFiles) {
            System.out.println(fileName);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        Stage removeStage = fetchAddStage();
        ArrayList<String> removedFiles =
                new ArrayList<>(removeStage.getStage().values());
        Collections.sort(removedFiles);
        for (String fileName : removedFiles) {
            System.out.println(fileName);
        }
        System.out.println();

        Map<String, String> modifiedNotStaged =
                new TreeMap<>(findModifiedFiles(fetchHeadCommit()));
        System.out.println("=== Modifications Not Staged For Commit ===");
        for (Map.Entry<String, String> entry : modifiedNotStaged.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        System.out.println();

        System.out.println("=== Untracked Files ===");
        for (String fileName : findUntrackedFiles()) {
            if (!modifiedNotStaged.containsKey(fileName)) {
                System.out.println(fileName);
            }
        }
    }

    /** Checks out file with name FILENAME from COMMIT. Overwrites or writes
     * to new file. */
    private static void checkoutFile(Commit commit, String fileName) {
        String fileHash = getFileHashFromName(commit, fileName);
        File destinationFile = new File(CWD, fileName);
        try {
            destinationFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File fromFile = new File(BLOBS_FOLDER, fileHash);
        Blob fromFileBlob = readObject(fromFile, Blob.class);
        byte[] desiredContents = fromFileBlob.getFileContents();
        writeContents(destinationFile, desiredContents);
    }

    /** Checks out entire branch by the name of BRANCHNAME. */
    private static void checkoutBranch(String branchName) {
        Branch branches = importBranches();
        var branchesMap = branches.getMap();
        Commit oldCommit;
        Commit desiredCommit;
        if (!(branchesMap.containsKey(branchName))) {
            throw error("No such branch exists.");
        } else if (fetchActiveBranchName().equals(branchName)) {
            throw error("No need to checkout the current branch.");
        } else {
            oldCommit = importCommit(branchesMap.get(fetchActiveBranchName()));
            desiredCommit = importCommit(branchesMap.get(branchName));
        }
        Map<String, String> cwdFiles = new TreeMap<>(); //Map of all files in CWD
        List<String> cwdFileName = new ArrayList<>(plainFilenamesIn(CWD));
        for (String fileName : cwdFileName) {
            File currentFile = new File(CWD, fileName);
            cwdFiles.put(fileName, getBlobHash(currentFile));
        }
        // take all files in DESIREDCOMMIT and put them in CWD, overwriting
        // the versions of the files that are already there if they exist.

        // but first check that all filenames in DESIREDCOMMIT that exist
        // in CWD are tracked and not modified if tracked
        for (String fileName : desiredCommit.getStrippedMap().keySet()) {
            if (cwdFiles.containsKey(fileName)) {
                Blob blob = new Blob(new File(CWD, fileName));
                if (!oldCommit.contains(blob)) {
                    throw error("There is an untracked file in the way; " +
                            "delete it, or add and commit it first.");
                }
            }
        }
        for (String fileName : oldCommit.getStrippedMap().keySet()) {
            // any files that are tracked in the current branch but are not
            // present in the checked-out branch are deleted. deletes all
            // files in the current branch that do not exist in desired commit
            if (cwdFiles.containsKey(fileName) && !desiredCommit.getStrippedMap().containsKey(fileName)) {
                File toBeDeleted = new File(CWD, fileName);
                toBeDeleted.delete();
            }
        }
        for (String fileName : desiredCommit.getStrippedMap().keySet()) {
            checkoutFile(desiredCommit, fileName);
        }
        clearRemoveStage();
        clearAddStage();
        updateActiveBranch(branchName);
    }

    private static void reset(String commitHash) {
        Commit desiredCommit = importCommit(commitHash);
        Commit oldCommit = importCommit(fetchHeadCommitHash());
        var oldFileNames = oldCommit.getStrippedMap().keySet();
        var desiredFileNames = desiredCommit.getStrippedMap().keySet();

        Map<String, String> cwdFiles = new TreeMap<>(); //Map of all files in CWD
        List<String> cwdFileName = new ArrayList<>(plainFilenamesIn(CWD));
        for (String fileName : cwdFileName) {
            File currentFile = new File(CWD, fileName);
            cwdFiles.put(fileName, getBlobHash(currentFile));
        }

        for (String fileName : desiredFileNames) {
            if (cwdFileName.contains(fileName)) {
                Blob blob = new Blob(new File(CWD, fileName));
                if (!oldCommit.contains(blob)) {
                    throw error("There is an untracked file in the way; " +
                            "delete it, or add and commit it first.");
                }
            }
        }

        for (String fileName : oldFileNames) {
            if (!desiredFileNames.contains(fileName)) {
                File removeFile = new File(CWD, fileName);
                removeFile.delete();
            }
        }

        for (String fileName : desiredFileNames) {
            checkoutFile(desiredCommit, fileName);
        }

        updateBranch(fetchActiveBranchName(), commitHash);


        clearAddStage();
        clearRemoveStage();
    }

}