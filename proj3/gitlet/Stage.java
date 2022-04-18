package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import static gitlet.Blob.exportBlob;
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
            exportBlob(blob);
        }
    }

    public static void removeBlob(Blob blob) {
        Stage updatedAddStage = fetchAddStage();
        Stage updatedRemoveStage = fetchRemoveStage();
        if (fetchAddStage().getStage().containsKey(blob.getFileName())) {
            updatedAddStage.getStage().remove(blob.getFileName());
            writeObject(ADD_STAGE_FILE, updatedAddStage);
        } else if (fetchHeadCommit().contains(blob)) {
            updatedRemoveStage.getStage().put(blob.getFileName(), blob.getHash());
            restrictedDelete(blob.getFileName());
            writeObject(REMOVE_STAGE_FILE, updatedRemoveStage);
        } else {
            throw error("No reason to remove the file.");
        }
    }
}
