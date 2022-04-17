package gitlet;

import java.io.*;
import java.util.*;

import static gitlet.Main.BRANCHES_FILE;
import static gitlet.Main.COMMITS_FOLDER;
import static gitlet.Utils.sha1;

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

    public Commit(String message, Date timestamp, Stage addStage,
                  Stage removeStage, String parent, String parent2) {
        _addStage = addStage;
        _removeStage = removeStage;
        _commitMap = new TreeMap<>();
        _commitMap.put(MESSAGE_STR, message);
        _commitMap.put(TIMESTAMP_STR, timestamp.toString());
        _commitMap.put(PARENT_STR, parent);
        _commitMap.put(PARENT2_STR, parent2);
    }

    public Commit(String message, Date timestamp, Stage addStage,
                  Stage removeStage, String parent) {
        this(message, timestamp, addStage, removeStage, parent, null);
    }
/*
    static void processStage() {

    }*/

    public String getHash() {
        String hashID = "";
        for (Map.Entry<String, String> entry : _commitMap.entrySet()) {
            hashID = hashID.concat(entry.getValue());
        }
        return sha1(hashID);
    }

    public Commit importCommit(String commitHash) {
        Commit commit;
        try {
            ObjectInputStream inp =
                    new ObjectInputStream(new FileInputStream
                            (COMMITS_FOLDER + File.pathSeparator + commitHash));
            commit = (Commit) inp.readObject();
            inp.close();

        } catch (IOException | ClassNotFoundException e) {
            commit = null;
            e.printStackTrace();
        }
        return commit;
    }

    public void exportCommit(String commitHash) {
        try {
            ObjectOutputStream out =
                    new ObjectOutputStream(new FileOutputStream
                            (COMMITS_FOLDER + File.pathSeparator + commitHash));
            out.writeObject(this);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processAddStage() {
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
        /*if (_commitMap.get(PARENT2_STR) != null) {
            parent2 = importCommit(_commitMap.get(PARENT2_STR));
        }*/
    }
}
