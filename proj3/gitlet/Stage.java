package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import static gitlet.Blob.exportBlob;
import static gitlet.Main.*;
import static gitlet.Utils.*;

/** Class that represents both the Add and Remove Stages. The "Stage" is what
 *  contains the files that are to be added or removed from a Commit.
 *  @author Rae Xin
 *  */
public class Stage implements Serializable {

    /** Map that represents the stage, a map between a filename and a SHA-1
     * hash. */
    private Map<String, String> _stage;

    /** Construct a Stage object by setting its stage to a new TreeMap. */
    public Stage() {
        _stage = new TreeMap<>();
    }

    /** Getter method that returns a Stage object's _STAGE. */
    public Map<String, String> getStage() {
        return _stage;
    }

    /** Given BLOB, add this Blob to the Add Stage, or remove it from the
     * Remove Stage if it is present there. */
    public static void addBlob(Blob blob) {
        if (!fetchHeadCommit().contains(blob)) {
            if (fetchRemoveStage().getStage().containsKey(blob.getFileName())) {
                Stage updatedRemoveStage = fetchRemoveStage();
                updatedRemoveStage.getStage().remove(blob.getFileName());
                writeObject(REMOVE_STAGE_FILE, updatedRemoveStage);
            } else {
                Stage updatedStage = fetchAddStage();
                updatedStage.getStage().put(blob.getFileName(), blob.getHash());
                writeObject(ADD_STAGE_FILE, updatedStage);
                exportBlob(blob);
            }
        }
    }

    /** Given BLOB, remove it from the Add Stage, or if the Blob is present
     * in the Head Commit, add BLOB to the Remove Stage and delete it from
     * the Current Working Directory. */
    public static void removeBlob(Blob blob) {
        Stage updatedAddStage = fetchAddStage();
        Stage updatedRemoveStage = fetchRemoveStage();
        if (fetchAddStage().getStage().containsKey(blob.getFileName())) {
            updatedAddStage.getStage().remove(blob.getFileName());
            writeObject(ADD_STAGE_FILE, updatedAddStage);
        } else if (fetchHeadCommit().contains(blob)) {
            updatedRemoveStage.getStage().put(blob.getFileName(),
                   blob.getHash());
            restrictedDelete(blob.getFileName());
            writeObject(REMOVE_STAGE_FILE, updatedRemoveStage);
        } else {
            message("No reason to remove the file.");
            System.exit(0);
        }
    }

    /** Given FILE and OBJECT, will remove FILE from the Remove Stage. */
    public static void removeFileFromRemoveStage(File file,
                                                Serializable object) {
        writeObject(file, object);
    }
}
