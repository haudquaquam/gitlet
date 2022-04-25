package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static gitlet.Blob.getBlobHash;
import static gitlet.Blob.getEmptyBlob;
import static gitlet.Blob.importBlob;
import static gitlet.Branch.deleteBranch;
import static gitlet.Branch.fetchActiveBranchName;
import static gitlet.Branch.findLatestCommonAncestor;
import static gitlet.Branch.importBranches;
import static gitlet.Branch.updateActiveBranch;
import static gitlet.Commit.fileExistsInCommit;
import static gitlet.Commit.fileModified;
import static gitlet.Commit.findModifiedFiles;
import static gitlet.Commit.findUntrackedFiles;
import static gitlet.Commit.getFileHashFromName;
import static gitlet.Commit.importCommit;
import static gitlet.Commit.updateActiveBranchWithLatestCommit;
import static gitlet.Stage.addBlob;
import static gitlet.Stage.removeBlob;
import static gitlet.Utils.message;
import static gitlet.Utils.plainFilenamesIn;
import static gitlet.Utils.readObject;
import static gitlet.Utils.restrictedDelete;
import static gitlet.Utils.writeContents;
import static gitlet.Utils.writeObject;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Rae Xin
 */
public class Main {

    /** Current Working Directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));

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
            message("Please enter a command.");
            System.exit(0);
        }
        switch (args[0]) {
        case "init":
            initializeRepo();
            break;
        case "commit":
            if (!(args.length > 1)) {
                message("Please enter a commit message.");
                System.exit(0);
            }
            Commit newCommit = new Commit(args[1], new Date(),
                    fetchHeadCommitHash());
            if (!newCommit.validCommit()) {
                message("No changes added to the commit.");
                System.exit(0);
            } else {
                updateActiveBranchWithLatestCommit(processCommit(newCommit));
            }
            break;
        case "add":
            if (args.length != 2) {
                message("Incorrect operands.");
                System.exit(0);
            }
            File addFile = new File(CWD, args[1]);
            if (!addFile.exists()) {
                message("File does not exist.");
                System.exit(0);
            }
            stageForAddition(addFile);
            break;
        case "rm":
            if (args.length != 2) {
                message("Incorrect operands.");
                System.exit(0);
            }
            File removeFile = new File(CWD, args[1]);
            stageForRemoval(removeFile);
            break;
        case "log":
            if (args.length > 1) {
                message("Incorrect operands.");
                System.exit(0);
            }
            displayLog();
            break;
        case "global-log":
            if (args.length > 1) {
                message("Incorrect operands.");
                System.exit(0);
            }
            displayGlobalLog();
            break;
        case "find":
            if (args.length != 2) {
                message("Incorrect operands.");
                System.exit(0);
            }
            findAllCommitsByMessage(args[1]);
            break;
        case "status":
            if (args.length > 1) {
                message("Incorrect operands.");
                System.exit(0);
            }
            if (!GITLET_FOLDER.exists()) {
                message("Not in an initialized Gitlet directory.");
                System.exit(0);
            }
            displayStatus();
            break;
        case "checkout":
            if (args.length < 2 || args.length > 4) {
                message("Incorrect operands.");
                System.exit(0);
            }
            if (args.length == 3 && args[1].equals("--")) {
                checkoutFile(fetchHeadCommit(), args[2]);
            } else if (args.length == 4 && args[2].equals("--")) {
                checkoutFile(importCommit(args[1]), args[3]);
            } else if (args.length == 2) {
                checkoutBranch(args[1]);
            } else {
                message("Incorrect operands.");
                System.exit(0);
            }
            break;
        case "branch":
            if (args.length != 2) {
                message("Incorrect operands.");
                System.exit(0);
            }
            createBranch(args[1], fetchHeadCommitHash());
            break;
        case "rm-branch":
            if (args.length != 2) {
                message("Incorrect operands.");
                System.exit(0);
            }
            deleteBranch(args[1]);
            break;
        case "reset":
            if (args.length != 2) {
                message("Incorrect operands.");
                System.exit(0);
            }
            reset(args[1]);
            break;
        case "merge":
            if (args.length != 2) {
                message("Incorrect operands.");
                System.exit(0);
            }
            merge(args[1]);
            break;
        default:
            message("No command with that name exists.");
            System.exit(0);
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

                createBranch("master", commitHash);
                updateActiveBranch("master");
            } else {
                message("A Gitlet version-control system already "
                        + "exists in the current directory.");
                System.exit(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Handles processing of a Commit with MESSAGE, TIMESTAMP, and PARENT.
     * Returns the SHA-1 hash of the Commit. */
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
        clearRemoveStage();
        clearAddStage();
        return hash;
    }

    /** Sets the branch named BRANCHNAME to point to the Commit with
     * the hash, COMMITHASH. */
    public static void createBranch(String branchName, String commitHash) {
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
            if (!currentCommit.hasAnyParent()) {
                break;
            }
            currentCommit = currentCommit.getParentCommit();
        }
    }

    /** Handles formatting of DATE. Returns a String of the formatted Date.
     * in correct (desired) form. */
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

    /** Displays all commits made in this repo, ever. */
    private static void displayGlobalLog() {
        ArrayList<String> listFileNames =
                new ArrayList<>(plainFilenamesIn(COMMITS_FOLDER));
        for (String hash : listFileNames) {
            Commit currentCommit = importCommit(hash);
            printCommit(currentCommit);
        }
    }

    /** Prints hashes of all commits with the message, MESSAGE. */
    private static void findAllCommitsByMessage(String message) {
        ArrayList<String> listFileNames =
                new ArrayList<>(plainFilenamesIn(COMMITS_FOLDER));
        int foundCommits = 0;
        for (String hash : listFileNames) {
            Commit currentCommit = importCommit(hash);
            if (currentCommit.getMessage().equals(message)) {
                System.out.println(currentCommit.getHash());
                foundCommits++;
            }
        }
        if (foundCommits == 0) {
            message("Found no commit with that message.");
            System.exit(0);
        }
    }

    /** Prints out current status. */
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
                new ArrayList<>(addStage.getStage().keySet());
        Collections.sort(addedFiles);
        for (String fileName : addedFiles) {
            System.out.println(fileName);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        Stage removeStage = fetchRemoveStage();
        ArrayList<String> removedFiles =
                new ArrayList<>(removeStage.getStage().keySet());
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
        for (String fileName : findUntrackedFiles(fetchHeadCommit())) {
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
        var branchesMap = importBranches().getMap();
        Commit oldCommit = null;
        Commit desiredCommit = null;
        if (!(branchesMap.containsKey(branchName))) {
            message("No such branch exists.");
            System.exit(0);
        } else if (fetchActiveBranchName().equals(branchName)) {
            message("No need to checkout the current branch.");
            System.exit(0);
        } else {
            oldCommit = importCommit(branchesMap.get(fetchActiveBranchName()));
            desiredCommit = importCommit(branchesMap.get(branchName));
        }
        Map<String, String> cwdFiles = getCWDFiles();

         /*take all files in DESIREDCOMMIT and put them in CWD, overwriting
         the versions of the files that are already there if they exist.
         but first check that all filenames in DESIREDCOMMIT that exist
         in CWD are tracked and not modified if tracked*/
        for (Map.Entry<String, String> en : cwdFiles.entrySet()) {
            if (desiredCommit.getFilesMap().containsKey(en.getKey())) {
                if (!oldCommit.getFilesMap().containsValue(en.getValue())) {
                    message("There is an untracked file in the way; "
                            + "delete it, or add and commit it first.");
                    System.exit(0);
                }
            } else {
                File toBeDeleted = new File(CWD, en.getKey());
                restrictedDelete(toBeDeleted);
            }
        }
        for (String fileName : oldCommit.getFilesMap().keySet()) {
            /* any files that are tracked in the current branch but are not
             present in the checked-out branch are deleted. deletes all
             files in the current branch that do not exist in desired commit*/
            if (cwdFiles.containsKey(fileName)) {
                File toBeDeleted = new File(CWD, fileName);
                restrictedDelete(toBeDeleted);
            }
        }
        for (String fileName : desiredCommit.getFilesMap().keySet()) {
            checkoutFile(desiredCommit, fileName);
        }
        clearRemoveStage();
        clearAddStage();
        updateHeadCommit(desiredCommit);
        updateActiveBranch(branchName);
    }

    /** Resets to the Commit specified by COMMITHASH. */
    private static void reset(String commitHash) {
        Commit desiredCommit = importCommit(commitHash);
        Commit oldCommit = importCommit(fetchHeadCommitHash());
        var oldFileNames = oldCommit.getFilesMap().keySet();
        var desiredFileNames = desiredCommit.getFilesMap().keySet();

        List<String> cwdFileName = new ArrayList<>(plainFilenamesIn(CWD));
        for (String fileName : desiredFileNames) {
            if (cwdFileName.contains(fileName)) {
                Blob blob = new Blob(new File(CWD, fileName));
                if (!oldCommit.contains(blob)) {
                    message("There is an untracked file in the way; "
                            + "delete it, or add and commit it first.");
                    System.exit(0);
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
        updateActiveBranchWithLatestCommit(commitHash);
        updateHeadCommit(desiredCommit);
        clearAddStage();
        clearRemoveStage();
    }

    /** Handles merging of the current branch with GIVENBRANCH. */
    public static void merge(String givenBranch) {
        boolean mergeConflict = false;
        Branch branches = importBranches();
        var branchMap = branches.getMap();
        var currentBranch = fetchActiveBranchName();
        /* Handle some failure cases. */
        if (!branchMap.containsKey(givenBranch)) {
            message("A branch with that name does not exist.");
            System.exit(0);
        } else if (givenBranch.equals(currentBranch)) {
            message("Cannot merge a branch with itself.");
            System.exit(0);
        }

        String givenCommitHash = branchMap.get(givenBranch);
        String currentCommitHash = branchMap.get(currentBranch);
        String latestCommonAncestor = findLatestCommonAncestor(givenBranch,
                currentBranch);

        if (latestCommonAncestor.equals(givenBranch)) {
            message("Given branch is an ancestor of the current branch.");
            System.exit(0);
        } else if (latestCommonAncestor.equals(currentBranch)) {
            checkoutBranch(givenBranch);
            message("Current branch fast-forwarded.");
            System.exit(0);
        } else if (!(fetchAddStage().getStage().isEmpty()
                && fetchRemoveStage().getStage().isEmpty())) {
            message("You have uncommitted changes.");
            System.exit(0);
        }
        clearRemoveStage(); // should be redundant
        clearAddStage(); //    should be redundant
        Commit givenCommit = importCommit(givenCommitHash);
        Commit currentCommit = importCommit(currentCommitHash);
        Commit splitPointCommit = importCommit(latestCommonAncestor);

        // use getDifferingFiles to handle logic
/*
        if (!givenCommit.equals(splitPointCommit)) {
            // there have been changes since splitpoint in given commit. they
            // are not the same commit.
            if (!currentCommit.equals(splitPointCommit)
                    && !currentCommit.equals(givenCommit)) {
                // there have been changes since splitpoint in current commit
                // in addition to changes in given commit. and ensure that
                // current commit is not equal to given commit.
                // merge conflict
            } else {
                checkoutBranch(givenBranch);
            }
        } else if (!getDifferingFiles(currentCommit,
                splitPointCommit).isEmpty()) {
            // no changes in givencommit, but yes changes in currentcommit
            // do nothing?
        }*/

        for (Map.Entry<String, String> entry
                : givenCommit.getFilesMap().entrySet()) {
            String givenFileName = entry.getKey();
            String givenFileHash = entry.getValue();
            List<String> desiredFileNames =
                    new ArrayList<>(givenCommit.getFilesMap().keySet());
            List<String> cwdFileNames = new ArrayList<>(getCWDFiles().keySet());
            for (String fileName : desiredFileNames) {
                if (cwdFileNames.contains(fileName)) {
                    Blob blob = new Blob(new File(CWD, fileName));
                    if (!currentCommit.contains(blob)) {
                        message("There is an untracked file in the way; "
                                + "delete it, or add and commit it first.");
                        System.exit(0);
                    }
                }
            }

            if (fileExistsInCommit(givenFileName, splitPointCommit)
                    && !fileExistsInCommit(givenFileName, currentCommit)) {
                // FILE EXISTS IN GIVEN COMMIT AND SPLITPOINT, BUT NOT IN
                // CURRENTCOMMIT
                if (fileModified(givenFileName, splitPointCommit,
                        givenCommit)) {
                    // FILE HAS BEEN MODIFIED IN GIVENCOMMIT -> need to
                    // checkout file, then stage it
                    handleMergeConflict(currentCommit, givenCommit, givenFileName);
                    mergeConflict = true;
                    // import the current Blob, then stage it to be added
                } else {
                    // FILE IS THE SAME BETWEEN SPLITPOINT AND GIVENCOMMIT ->
                    // do nothing!
                }
            } else if (fileExistsInCommit(givenFileName, splitPointCommit)
                    && fileExistsInCommit(givenFileName, currentCommit)) {
                // FILE EXISTS IN ALL SPLITPOINT, GIVENCOMMIT, AND
                // CURRENTCOMMIT
                if (fileModified(givenFileName, givenCommit, currentCommit)) {
                    // IF FILE IS DIFFERENT BETWEEN GIVEN AND CURRENT ->
                    // MERGE CONFLICT
                    handleMergeConflict(currentCommit, givenCommit,
                            givenFileName);
                    mergeConflict = true;
                } else {
                    // FILES ARE MODIFIED IN THE SAME WAY IN GIVENCOMMIT AND
                    // CURRENTCOMMIT, SO DO NOTHING!
                }
            } else if (!fileExistsInCommit(givenFileName, splitPointCommit)
                    && !fileExistsInCommit(givenFileName, currentCommit)) {
                // FILE ONLY EXISTS IN GIVENCOMMIT -> CHECK OUT AND STAGE
                checkoutFile(givenCommit, givenFileName);
                addBlob(importBlob(givenFileHash));

            } else if (!fileExistsInCommit(givenFileName, splitPointCommit)
                    && fileExistsInCommit(givenFileName, currentCommit)) {
                // FILE EXISTS IN BOTH GIVENCOMMIT AND CURRENTCOMMIT, BUT NOT
                // IN SPLITPOINT -> CHECK TO SEE IF THEY ARE MODIFIED IN THE
                // SAME WAY. OTHERWISE WE HAVE MERGE CONFLICT.
                if (fileModified(givenFileName, givenCommit, currentCommit)) {
                    // IF FILE IS DIFFERENT BETWEEN GIVEN AND CURRENT ->
                    // MERGE CONFLICT
                    handleMergeConflict(currentCommit, givenCommit,
                            givenFileName);
                    mergeConflict = true;
                } else {
                    // FILES ARE MODIFIED IN THE SAME WAY IN GIVENCOMMIT AND
                    // CURRENTCOMMIT, SO DO NOTHING!
                }
            }
        }

        for (Map.Entry<String, String> entry
                : currentCommit.getFilesMap().entrySet()) {
            String currentFileName = entry.getKey();
            String currentFileHash = entry.getValue();
            /*if (!fileExistsInCommit(currentFileName, givenCommit)
                    && fileExistsInCommit(currentFileName, splitPointCommit)) {
                // FILE EXISTS IN CURRENT BUT REMOVED FROM GIVEN ->
                // handle merge conflict

            }*/
            if (fileExistsInCommit(currentFileName, splitPointCommit)
                    && !fileExistsInCommit(currentFileName, givenCommit)) {
                // FILE EXISTS IN BOTH SPLITPOINT AND CURRENT, BUT NOT IN
                // REMOVED
                if (fileModified(currentFileName, currentCommit,
                        splitPointCommit)) {
                    // FILE MODIFIED FROM SPLITPOINT -> merge conflict
                    mergeConflict = true;
                    handleMergeConflict(currentCommit, givenCommit,
                            currentFileName);
                } else {
                    File toBeRemoved = new File(CWD, currentFileName);
                    stageForRemoval(toBeRemoved);
                }
            }
        }

        /* FAILURE CASE: if stages contain adds/removes, error with:
         "You have uncommitted changes."

         FAILURE CASE: if branch does not exist, error:
         "A branch with that name does not exist."

         FAILURE CASE: attempting to merge branch with itself, error:
         "Cannot merge a branch with itself." */

        /* FAILURE CASE: untracked file in current commit that would be
         overwritten or deleted by merge, error:
         "There is an untracked file in the way; delete it, or
         add and commit it first." */

        /* any files that have been modified in the GIVENBRANCH since the split
         point,
         but not modified in the CURRENTBRANCH since the split point should
         be changed to their versions in the GIVENBRANCH. (checkout from
         GIVENBRANCH's HEAD COMMIT) then, stage all of those files in
         CURRENTBRANCH


         any files modified in CURRENTBRANCH, but not in GIVENBRANCH should stay

         any files modified in BOTH the CURRENTBRANCH and GIVENBRANCH in the
         same way
         (both have same content now, or both are removed), are not changed by
         merge.

         if file was removed from both CURRENTBRANCH and GIVENBRANCH, but file
         of same
         name exists in CWD, it is left alone and continues to be untracked in
         the merge.

         any files not in the split point, and only in the GIVENBRANCH should
         be checked out and STAGED!

         any files not present in the split point, and only exist in the
         CURRENTBRANCH
         should stay as they are

         any files present at the split point, unchanged at the GIVENBRANCH,
         and not
         present in the CURRENTBRANCH should remain absent!

         MERGE CONFLICTS: modified in DIFFERENT ways in CURRENTBRANCH and
         GIVENBRANCH
         contents are both changed from split point and differnt from each
         other,
         or the contents of one are changed, and the other file is deleted. OR
         the file
         is not in split point, and it has different contents in GIVENBRANCH and
         CURRENTBRANCH
         In any of these cases:
         replace the contents of the conflicted file in CURRENTBRANCH with

         <<<<<<< HEAD
         contents of file in current branch
         =======
         contents of file in given branch
         >>>>>>>

         replace "contents of..." with the actual contents
         then, stage this file (the result). if a file is deleted, make contents
          empty.
         use straight concatenation. don't worry about files with no newlines.
         will
         give strange output

         AFTER ALL THE ABOVE: (if split point was not CURRENTBRANCH or
         GIVENBRANCH)
         commit in CURRENTBRANCH with the message:
         "Merged [given branch name] into [current branch name]"
         if there was a conflict , also PRINT the message:
         "Encountered a merge conflict."*/

        String mergeMsg = "Merged " + givenBranch + " into " + currentBranch
                + ".";

        Commit newCommit = new Commit(mergeMsg, new Date(),
                currentCommitHash, givenCommitHash);
        updateActiveBranchWithLatestCommit(processCommit(newCommit));

        if (mergeConflict) {
            message("Encountered a merge conflict.");
        }

    }

    /** Handles Merge Conflict between FIRST and OTHER for the file with name
     *  FILENAME. This file must exist in both Commits. */
    public static void handleMergeConflict(Commit first, Commit other,
                                           String fileName) {
        File currentFile = new File(CWD, fileName);
        Blob firstBlob, otherBlob;
        if (first.getFilesMap().containsKey(fileName)) {
            firstBlob = importBlob(first.getFilesMap().get(fileName));
        } else {
            firstBlob = getEmptyBlob();
        }
        if (other.getFilesMap().containsKey(fileName)) {
            otherBlob = importBlob(other.getFilesMap().get(fileName));
        } else {
            otherBlob = getEmptyBlob();
        }

        String mergeContents = "<<<<<<< HEAD\n"
                + new String(firstBlob.getFileContents(),
                StandardCharsets.UTF_8) + "=======\n"
                + new String(otherBlob.getFileContents(),
                StandardCharsets.UTF_8) + ">>>>>>>\n";
        writeContents(currentFile, mergeContents);
        stageForAddition(currentFile);
    }

    /** Returns a TreeMap of all files in the Current Working Directory. Maps
     *  the filename to its SHA-1 hash. */
    public static Map<String, String> getCWDFiles() {
        Map<String, String> cwdFiles = new TreeMap<>();
        List<String> cwdFileName = new ArrayList<>(plainFilenamesIn(CWD));
        for (String fileName : cwdFileName) {
            File currentFile = new File(CWD, fileName);
            cwdFiles.put(fileName, getBlobHash(currentFile));
        }
        return cwdFiles;
    }
}
