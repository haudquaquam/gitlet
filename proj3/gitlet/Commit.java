package gitlet;

import java.io.*;
import java.util.*;

import static gitlet.Main.*;
import static gitlet.Utils.*;
import java.util.Formatter;

public class Commit implements Serializable {

    private Map<String, String> _commitMap;
    private Stage _addStage;
    private Stage _removeStage;
    private final String MESSAGE_STR = "message";
    private final String TIMESTAMP_STR = "timestamp";
    private final String PARENT_STR = "parent";
    private final String PARENT2_STR = "parent2";
    private final ArrayList<String> defaultKeys = new ArrayList<>
            (List.of(MESSAGE_STR, TIMESTAMP_STR, PARENT_STR, PARENT2_STR));

    public Commit(String message, Date timestamp, String parent, String parent2) {
        _addStage = fetchAddStage();
        _removeStage = fetchRemoveStage();
        _commitMap = new TreeMap<>();
        if (message.isEmpty()) {
            throw error("Please enter a commit message.");
        }
        _commitMap.put(MESSAGE_STR, message);
        _commitMap.put(TIMESTAMP_STR, formatDate(timestamp));
        _commitMap.put(PARENT_STR, parent);
        _commitMap.put(PARENT2_STR, parent2);
    }

    public Commit(String message, Date timestamp, String parent) {
        this(message, timestamp, parent, null);
    }
/*
    static void processStage() {

    }*/

    public String getHash() {
        String hashID = "";
        for (Map.Entry<String, String> entry : _commitMap.entrySet()) {
            hashID = hashID + entry.getValue();
        }
        return sha1(hashID);
    }

    public Commit importCommit(String commitHash) {
        File commitFile = new File(COMMITS_FOLDER, commitHash);
        return readObject(commitFile, Commit.class);
    }

    public void exportCommit() {
        String commitHash = this.getHash();
        File commitFile = new File(COMMITS_FOLDER, commitHash);
        writeObject(commitFile , this);
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

            if (fetchAddStage().getStage().isEmpty() && fetchRemoveStage().getStage().isEmpty()) {
                throw error("No changes added to the commit.");
            }
            _commitMap.putAll(_addStage.getStage());
            for (Map.Entry<String, String> entry : _removeStage.getStage().entrySet()) {
                _commitMap.remove(entry.getKey(), entry.getValue());
            }
        } else {
            System.out.println("should not be processing stage of initial commit!");
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
