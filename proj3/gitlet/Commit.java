package gitlet;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import java.util.Date;
import java.util.Formatter;

public class Commit implements Serializable {

    private Map<String, String> _commitMap;
    private Stage _stage;
    private final String TIMESTAMP_STR = "timestamp";
    private final String MESSAGE_STR = "message";
    private final String PARENT_STR = "parent";
    private final String PARENT2_STR = "parent2";


    public Commit(String message, Date timestamp, Stage stage, String parent) {
        _stage = stage;
        _commitMap = new TreeMap<>();
        _commitMap.put(MESSAGE_STR, message);
        _commitMap.put(TIMESTAMP_STR, timestamp.toString());
        _commitMap.put(PARENT_STR, parent);
        if (parent != null) {

        }
    }

    public Commit(String message, Date timestamp, Stage stage, String parent, String parent2) {
        this(message, timestamp, stage, parent);
        _commitMap.put(PARENT2_STR, parent2);

    }

    static void processStage() {

    }

}
