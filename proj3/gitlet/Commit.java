package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static gitlet.Branch.fetchActiveBranchName;
import static gitlet.Branch.importBranches;
import static gitlet.Main.COMMITS_FOLDER;
import static gitlet.Main.CWD;
import static gitlet.Main.clearAddStage;
import static gitlet.Main.clearRemoveStage;
import static gitlet.Main.fetchAddStage;
import static gitlet.Main.fetchHeadCommit;
import static gitlet.Main.fetchRemoveStage;
import static gitlet.Main.formatDate;
import static gitlet.Main.getCWDFiles;
import static gitlet.Utils.UID_LENGTH;
import static gitlet.Utils.message;
import static gitlet.Utils.plainFilenamesIn;
import static gitlet.Utils.readObject;
import static gitlet.Utils.serialize;
import static gitlet.Utils.sha1;
import static gitlet.Utils.writeObject;


/** This class represents a Commit, which contains information about a
 * certain commit, including its message, timestamp, parent(s), and of
 * course, its Blobs.
 * @author Rae Xin
 * */
public class Commit implements Serializable {

    /** Maps default information to magic strings, as well as
     * filenames to SHA-1 hashes for Blobs. */
    private Map<String, String> _commitMap;

    /** Holds the current Add Stage. */
    private Stage _addStage;

    /** Holds the current Remove Stage. */
    private Stage _removeStage;

    /** Magic String that is mapped to the actual message. */
    private final String _messageStr = "message";

    /** Magic String that is mapped to the actual timestamp. */
    private final String _timestampStr = "timestamp";

    /** Magic String that is mapped to the parent's SHA-1 hash. */
    private final String _parentStr = "parent";

    /** Magic String that is mapped to the second parent's SHA-1 hash. */
    private final String _parent2Str = "parent2";

    /** This Commit's SHA-1 hash. */
    private final String _hash;

    /** ArrayList of all magic words so that we can easily check keys in the
     * commitMap against these. */
    private final List<String> _defaultKeys =
            List.of(_messageStr, _timestampStr, _parentStr, _parent2Str);

    /** Constructs a COMMIT object from MESSAGE, TIMESTAMP, PARENTHASH, and
     * PARENT2HASH. */
    public Commit(String message, Date timestamp, String parentHash,
                  String parent2Hash) {
        _addStage = fetchAddStage();
        _removeStage = fetchRemoveStage();
        _commitMap = new TreeMap<>();
        if (message.isEmpty()) {
            message("Please enter a commit message.");
            System.exit(0);
        }
        _commitMap.put(_messageStr, message);
        _commitMap.put(_timestampStr, formatDate(timestamp));
        _commitMap.put(_parentStr, parentHash);
        _commitMap.put(_parent2Str, parent2Hash);
        String hashID = "commit";
        for (Map.Entry<String, String> entry : _commitMap.entrySet()) {
            hashID = hashID + entry.getValue();
        }
        var byteAdd = serialize(_addStage);
        var byteRem = serialize(_removeStage);
        _hash = sha1(hashID, byteAdd, byteRem);
    }

    /** Constructs a COMMIT object when only given MESSAGE, TIMESTAMP, and
     * PARENT. */
    public Commit(String message, Date timestamp, String parent) {
        this(message, timestamp, parent, null);
    }

    /** Returns the SHA-1 hash of the given commit. */
    public String getHash() {
        return _hash;
    }

    /** Returns a Commit, pulled from the inputted COMMITHASH, which is the
     * filename of this Commit within the Commits folder. */
    public static Commit importCommit(String commitHash) {
        if (commitHash.length() < UID_LENGTH) {
            List<String> allCommitHashes =
                    new ArrayList<>(plainFilenamesIn(COMMITS_FOLDER));
            for (String possibleMatch : allCommitHashes) {
                if (possibleMatch.indexOf(commitHash) == 0) {
                    commitHash = possibleMatch;
                }
            }
        }
        File commitFile = new File(COMMITS_FOLDER, commitHash);
        if (!commitFile.exists()) {
            System.out.println(commitHash);
            System.out.println(importBranches().getMap().get("master"));
            message("No commit with that id exists.");
            System.exit(0);
        }
        return readObject(commitFile, Commit.class);
    }

    /** Writes the current commit to its corresponding file in the Commits
     * folder. Updates its contents if it already exists, creates new file if
     * it does not yet exist. */
    public void exportCommit() {
        String commitHash = this.getHash();
        File commitFile = new File(COMMITS_FOLDER, commitHash);
        try {
            commitFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeObject(commitFile, this);
    }

    /** Returns whether the current Commit is a valid Commit. Validity
     * is ensured by checking that either the Add Stage or the Remove Stage
     * is not empty. */
    public boolean validCommit() {
        boolean flag = fetchAddStage().getStage().isEmpty();
        return (!(flag && fetchRemoveStage().getStage().isEmpty()));
    }

    /** Processes the Add and Remove Stages of this Commit, first importing
     * all Blobs from its parent Commit, then adding all items from this
     * Commit's Add Stage, then removing all items from this Commit's Remove
     * Stage. */
    public void processStage() {
        Commit parent1, parent2 = null;
        if (_commitMap.get(_parentStr) != null) {
            parent1 = importCommit(_commitMap.get(_parentStr));
            for (Map.Entry<String, String> entry
                    : parent1._commitMap.entrySet()) {
                if (!_defaultKeys.contains(entry.getKey())) {
                    _commitMap.put(entry.getKey(), entry.getValue());
                }
            }
            _commitMap.putAll(_addStage.getStage());
            for (Map.Entry<String, String> entry
                    : _removeStage.getStage().entrySet()) {
                _commitMap.remove(entry.getKey(), entry.getValue());
            }
        }
        clearAddStage();
        clearRemoveStage();
    }

    /** Returns whether this Commit contains BLOB. */
    public boolean contains(Blob blob) {
        String blobFileName = blob.getFileName();
        String blobHash = blob.getHash();
        return _commitMap.containsKey(blobFileName)
                && Objects.equals(_commitMap.get(blobFileName), blobHash);
    }

    /** Returns whether this Commit has a parent Commit. Equivalent to
     * checking whether this Commit is the initial Commit, as that should be
     * the only Commit without a parent. */
    public boolean hasParent() {
        return (_commitMap.get(_parentStr) != null);
    }

    /** Returns the timestamp of the current Commit. */
    public String getDate() {
        return _commitMap.get(_timestampStr);
    }

    /** Returns the message of the current Commit. */
    public String getMessage() {
        return _commitMap.get(_messageStr);
    }

    /** Returns the first parent's hash in the current Commit. */
    public String getParentHash() {
        return _commitMap.get(_parentStr);
    }

    /** Returns the first parent Commit of the current Commit. */
    public Commit getParentCommit() {
        return importCommit(getParentHash());
    }

    /** Returns the SHA-1 hash of the file FILENAME as it exists in COMMIT. */
    public static String getFileHashFromName(Commit commit, String fileName) {
        Map<String, String> commitMap = commit._commitMap;
        if (!commitMap.containsKey(fileName)) {
            message("File does not exist in that commit.");
            System.exit(0);
        }
        return commitMap.get(fileName);
    }

    /** Given Commit specified by COMMITHASH, updates the current (active)
     * Branch, pointing it to the given Commit. */
    public static void updateActiveBranchWithLatestCommit(String commitHash) {
        Branch branches = importBranches();
        String activeBranch = fetchActiveBranchName();
        Map<String, String> branchMap = branches.getMap();
        branchMap.put(activeBranch, commitHash);
        branches.exportBranch();
    }

    /** Given the Commit, COMMIT, returns a TreeMap of all files within this
     * Commit that are modified or deleted, but not staged. The TreeMap maps
     * a filename to its status, either "(modified)" or "(deleted)". */
    public static Map<String, String> findModifiedFiles(Commit commit) {
        Map<String, String> trackedFiles =
                new TreeMap<>(commit.getStrippedMap());
        Map<String, String> addStageFiles = fetchAddStage().getStage();
        Map<String, String> removeStageFiles = fetchRemoveStage().getStage();
        Map<String, String> cwdFiles = getCWDFiles();
        Map<String, String> modifiedNotStaged = new TreeMap<>();
        for (String fileName : cwdFiles.keySet()) {
            String cwdHash = cwdFiles.get(fileName);
            if (trackedFiles.containsKey(fileName)) {
                if (!(cwdHash.equals(trackedFiles.get(fileName)))) {
                    if (!(addStageFiles.containsKey(fileName))) {
                        modifiedNotStaged.put(fileName, "(modified)");
                    }
                }
            }
            if (addStageFiles.containsKey(fileName)
                    && !(cwdHash.equals(addStageFiles.get(fileName)))) {
                modifiedNotStaged.put(fileName, "(modified)");
            }
        }
        for (String fileName : addStageFiles.keySet()) {
            if (!cwdFiles.containsKey(fileName)) {
                modifiedNotStaged.put(fileName, "(deleted)");
            }
        }
        for (String fileName : trackedFiles.keySet()) {
            if (!cwdFiles.containsKey(fileName)
                    && !removeStageFiles.containsKey(fileName)) {
                modifiedNotStaged.put(fileName, "(deleted)");
            }
        }
        return modifiedNotStaged;
    }

    /** Returns an ArrayList of files that exist in the Current Working
     * Directory, but do not exist in the Head Commit or any of the Stages. */
    public static List<String> findUntrackedFiles() {
        Map<String, String> trackedFiles =
                new TreeMap<>(fetchHeadCommit().getStrippedMap());
        Map<String, String> addStageFiles = fetchAddStage().getStage();
        Map<String, String> removeStageFiles = fetchRemoveStage().getStage();
        List<String> untrackedFiles = new ArrayList<>();
        List<String> cwdFileName = new ArrayList<>(plainFilenamesIn(CWD));
        for (String fileName : cwdFileName) {
            if (!(removeStageFiles.containsKey(fileName))
                    && !(addStageFiles.containsKey(fileName))
                    && !(trackedFiles.containsKey(fileName))) {
                untrackedFiles.add(fileName);
            }
        }
        return untrackedFiles;
    }

    /** Returns a TreeMap of the current Commit's commitMap, minus any of the
     magic keys. */
    public Map<String, String> getStrippedMap() {
        Map<String, String> processedMap = new TreeMap<>();
        for (Map.Entry<String, String> entry : _commitMap.entrySet()) {
            if (!_defaultKeys.contains(entry.getKey())) {
                processedMap.put(entry.getKey(), entry.getValue());
            }
        }
        return processedMap;
    }

    /*private void processRemoveStage() {
        Commit parent1, parent2 = null;

        if (_commitMap.get(PARENT_STR) != null) {
            parent1 = importCommit(_commitMap.get(PARENT_STR));
            for (Map.Entry<String, String> entry
            : parent1._commitMap.entrySet()) {
                if (!defaultKeys.contains(entry.getKey())) {
                    _commitMap.put(entry.getKey(), entry.getValue());
                }
            }
            _commitMap.putAll(_addStage.getStage());
        }
    }*/
}
