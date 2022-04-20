package gitlet;

import org.antlr.v4.runtime.tree.Tree;

import java.io.*;
import java.util.*;

import static gitlet.Blob.getBlobHash;
import static gitlet.Branch.fetchActiveBranchName;
import static gitlet.Branch.importBranches;
import static gitlet.Main.*;
import static gitlet.Utils.*;

public class Commit implements Serializable {

    private Map<String, String> _commitMap; // file name : SHA-1 hash
    private Stage _addStage;
    private Stage _removeStage;
    private final String MESSAGE_STR = "message";
    private final String TIMESTAMP_STR = "timestamp";
    private final String PARENT_STR = "parent";
    private final String PARENT2_STR = "parent2";
    private final ArrayList<String> defaultKeys = new ArrayList<>
            (List.of(MESSAGE_STR, TIMESTAMP_STR, PARENT_STR, PARENT2_STR));

    public Commit(String message, Date timestamp, String parentHash, String parent2) {
        _addStage = fetchAddStage();
        _removeStage = fetchRemoveStage();
        _commitMap = new TreeMap<>();
        if (message.isEmpty()) {
            throw error("Please enter a commit message.");
        }
        _commitMap.put(MESSAGE_STR, message);
        _commitMap.put(TIMESTAMP_STR, formatDate(timestamp));
        _commitMap.put(PARENT_STR, parentHash);
    }

    public Commit(String message, Date timestamp, String parent) {
        this(message, timestamp, parent, null);
    }
/*
    static void processStage() {

    }*/

    public String getHash() {
        String hashID = "commit";
        for (Map.Entry<String, String> entry : _commitMap.entrySet()) {
            hashID = hashID + entry.getValue();
        }
        return sha1(hashID);
    }

    public static Commit importCommit(String commitHash) {
        if (commitHash.length() < 40) {
            List<String> allCommitHashes = new ArrayList<>(plainFilenamesIn(COMMITS_FOLDER));
            for (String possibleMatch : allCommitHashes) {
                if (possibleMatch.indexOf(commitHash) == 0) {
                    commitHash = possibleMatch;
                }
            }
        }
        File commitFile = new File(COMMITS_FOLDER, commitHash);
        if (!commitFile.exists()) {
            throw error("No commit with that id exists.");
        }
        return readObject(commitFile, Commit.class);
    }

    public void exportCommit() {
        String commitHash = this.getHash();
        File commitFile = new File(COMMITS_FOLDER, commitHash);
        writeObject(commitFile , this);
    }

    public boolean validCommit() {
        return (!(fetchAddStage().getStage().isEmpty() && fetchRemoveStage().getStage().isEmpty()));
    }

    public void processStage() {
        Commit parent1, parent2 = null;

        if (_commitMap.get(PARENT_STR) != null) { // NOT initial commit
            parent1 = importCommit(_commitMap.get(PARENT_STR));
            for (Map.Entry<String, String> entry : parent1._commitMap.entrySet()) {
                if (!defaultKeys.contains(entry.getKey())) {
                    _commitMap.put(entry.getKey(), entry.getValue());
                }
            }
            // copies parent _commitMap over to current Commit's _commitMap
            _commitMap.putAll(_addStage.getStage());
            for (Map.Entry<String, String> entry : _removeStage.getStage().entrySet()) {
                _commitMap.remove(entry.getKey(), entry.getValue());
            }
        }
        clearAddStage();
        clearRemoveStage();
        /*if (_commitMap.get(PARENT2_STR) != null) {
            parent2 = importCommit(_commitMap.get(PARENT2_STR));
        }*/
    }

    public boolean contains(Blob blob) {
        String blobFileName = blob.getFileName();
        String blobHash = blob.getHash();
        return _commitMap.containsKey(blobFileName) && _commitMap.containsValue(blobHash);
    }

    public boolean hasParent() {
        return (_commitMap.get(PARENT_STR) != null);
    }

    public String getDate() {
        return _commitMap.get(TIMESTAMP_STR);
    }

    public String getMessage() {
        return _commitMap.get(MESSAGE_STR);
    }

    public String getParentHash() {
        return _commitMap.get(PARENT_STR);
    }

    public Commit getParentCommit() {
        return importCommit(getParentHash());
    }

    public static String getFileHashFromName(Commit commit, String fileName) {
        Map<String, String> commitMap = commit._commitMap;
        if (!commitMap.containsKey(fileName)) {
            throw error("File does not exist in that commit.");
        }
        return commitMap.get(fileName);
    }

    public static void updateActiveBranchWithLatestCommit(String commitHash) {
        Branch branches = importBranches();
        String activeBranch = fetchActiveBranchName();
        Map<String, String> branchMap = branches.getMap();
        branchMap.put(activeBranch, commitHash);
        branches.exportBranch();
        /*System.out.println("Active Branch File: " + readContentsAsString(ACTIVE_BRANCH_FILE));
        Branch branches = importBranches();
        Map<String, String> branchMap = branches.getMap();
        for (Map.Entry<String, String> entry : branchMap.entrySet()) {
            System.out.println(entry.getKey() + " val: " + entry.getValue());
        }*/
    }

    public static Map<String, String> findModifiedFiles(Commit commit) {
        Map<String, String> trackedFiles = new TreeMap<>(commit.getStrippedMap()); //Map of all tracked from last commit
        Map<String, String> addStageFiles = fetchAddStage().getStage();  //Map of all files from add stage
        Map<String, String> removeStageFiles = fetchRemoveStage().getStage();
        Map<String, String> cwdFiles = new TreeMap<>(); //Map of all files in CWD
        List<String> cwdFileName = new ArrayList<>(plainFilenamesIn(CWD));
        for (String fileName : cwdFileName) {
            File currentFile = new File(CWD, fileName);
            cwdFiles.put(fileName, getBlobHash(currentFile));
        }

        Map<String, String> modifiedNotStaged = new TreeMap<>();

        // case 1: tracked in the current commit, changed in the working
        // directory, but not staged
        for (String fileName : cwdFiles.keySet()) {
            String cwdHash = cwdFiles.get(fileName);
            if (trackedFiles.containsKey(fileName)) {
                // if this FILENAME is tracked
                if (!(cwdHash.equals(trackedFiles.get(fileName)))) {
                    // if CWD file hash is not equal to tracked file hash
                    // meaning that the file contents are different
                    if (!(addStageFiles.containsKey(fileName))) {
                        // ADDSTAGE does not contain this file. not staged
                        modifiedNotStaged.put(fileName, "(modified)");
                    }
                }
            }

            // case 2: staged for addition, but with different contents
            // than in the working directory
            if (addStageFiles.containsKey(fileName) &&
                    !(cwdHash.equals(addStageFiles.get(fileName)))) {
                // if file is in add stage, but the hashes are different
                modifiedNotStaged.put(fileName, "(modified)");
            }
        }

        // case 3: staged for addition, but deleted in the working directory
        for (String fileName : addStageFiles.keySet()) {
            if (!cwdFiles.containsKey(fileName)) {
                modifiedNotStaged.put(fileName, "(deleted)");
            }
        }

        // case 4: not staged for removal, but tracked in the current
        // commit and deleted from the working directory
        for (String fileName : trackedFiles.keySet()) {
            if (!cwdFiles.containsKey(fileName) && !removeStageFiles.containsKey(fileName)) {
                modifiedNotStaged.put(fileName, "(deleted)");
            }
        }
        return modifiedNotStaged;
    }

    public static List<String> findUntrackedFiles() {
        Map<String, String> trackedFiles = new TreeMap<>(fetchHeadCommit().getStrippedMap()); //Map of all tracked from last commit
        Map<String, String> addStageFiles = fetchAddStage().getStage();  //Map of all files from add stage
        Map<String, String> removeStageFiles = fetchRemoveStage().getStage();
        List<String> untrackedFiles = new ArrayList<>(); // return this
        List<String> cwdFileName = new ArrayList<>(plainFilenamesIn(CWD));
        for (String fileName : cwdFileName) { //fileName
            if (!(removeStageFiles.containsKey(fileName)) &&
                    !(addStageFiles.containsKey(fileName)) &&
                    !(trackedFiles.containsKey(fileName))) {
                untrackedFiles.add(fileName);
            }
        }
        return untrackedFiles;
    }

    public Map<String, String> getStrippedMap() {
        Map<String, String> processedMap = new TreeMap<>();
        for (Map.Entry<String, String> entry : _commitMap.entrySet()) {
            if (!defaultKeys.contains(entry.getKey())) {
                processedMap.put(entry.getKey(), entry.getValue());
            }
        }
        return processedMap;
    }

    /*private void processRemoveStage() {
        Commit parent1, parent2 = null;

        if (_commitMap.get(PARENT_STR) != null) {
            parent1 = importCommit(_commitMap.get(PARENT_STR));
            for (Map.Entry<String, String> entry : parent1._commitMap.entrySet()) {
                if (!defaultKeys.contains(entry.getKey())) {
                    _commitMap.put(entry.getKey(), entry.getValue());
                }
            }
            _commitMap.putAll(_addStage.getStage());
        }
    }*/
}
