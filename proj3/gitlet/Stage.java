package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import static gitlet.Main.*;
import static gitlet.Utils.*;

public class Stage implements Serializable {

    private Map<String, String> _stage; //map between filename and -B-l-o-b- SHA-1 hash

    public Stage() {
        _stage = new TreeMap<>();
    }

    public Map<String, String> getStage() {
        return _stage;
    }

    public static void addBlob(Blob blob) {
        if (!fetchHeadCommit().contains(blob)) {
            Stage updatedStage = fetchAddStage();
            updatedStage.getStage().put(blob.getFileName(), blob.getHash());
            writeObject(ADD_STAGE_FILE, updatedStage);
        }
    }

    public static void removeBlob(Blob blob) {
        if (fetchAddStage().getStage().containsKey(blob.getFileName())) {
            fetchAddStage().getStage().remove(blob.getFileName());
        } else if (fetchHeadCommit().contains(blob)) {
            fetchRemoveStage().getStage().put(blob.getFileName(), blob.getHash());
            restrictedDelete(blob.getFileName());
        } else {
            throw error("No reason to remove the file.");
        }
    }
}
