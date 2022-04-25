package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import static gitlet.Main.GITLET_FOLDER;
import static gitlet.Utils.message;
import static gitlet.Utils.readObject;
import static gitlet.Utils.writeObject;

public class Remote implements Serializable {

    /** Remote repository file that stores mappings from Remote name to the
     * String representation of the path of the Remote. */
    public static final File REMOTE_FILE = new File(GITLET_FOLDER, "remote" +
            ".txt");

    /** Mapping between the Remote name and the Remote's path. The Remote
     * path should already exist on the local system. */
    public Map<String, String> _remoteNameToPathMap;

    /** Initializes a Remote by putting a mapping between the specified
     * REMOTENAME and the REMOTEPATH, both Strings, into the Map, which is
     * pulled from and updated to the Remote file.*/
    public Remote(String remoteName, String remotePath) {
        try {
            if (REMOTE_FILE.createNewFile()) {
                // did not exist before and now does
                _remoteNameToPathMap = new TreeMap<>();
            } else {
                _remoteNameToPathMap = importRemote()._remoteNameToPathMap;
            }
            if (_remoteNameToPathMap.containsKey(remoteName)) {
                message("A remote with that name already exists.");
                System.exit(0);
            } else {
                _remoteNameToPathMap.put(remoteName, remotePath);
                this.exportRemote();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Add the Remote specified by REMOTENAME and REMOTEPATH to all of the
     * mappings. */
    public static void addRemote(String remoteName, String remotePath) {
        remotePath = remotePath.replaceAll("/", File.separator);
        Remote newRemote = new Remote(remoteName, remotePath);
    }

    /** Removes the Remote mapping with the key REMOTENAME from the Remote
     * mapping. */
    public static void removeRemote(String remoteName) {
        Remote updatedRemote = importRemote();
        var map = updatedRemote._remoteNameToPathMap;
        if (!map.containsKey(remoteName)) {
            message("A remote with that name does not exist.");
        } else {
            map.remove(remoteName);
            updatedRemote._remoteNameToPathMap = map;
            updatedRemote.exportRemote();
        }
    }

    public static void fetchRemote(String remoteName, String remoteBranchName) {
    }

    public static void pullRemote(String remoteName, String remoteBranchName) {
    }

    public static void pushRemote(String remoteName, String remoteBranchName) {
    }

    private static Remote importRemote() {
        return readObject(REMOTE_FILE, Remote.class);
    }

    private void exportRemote() {
        writeObject(REMOTE_FILE, this);
    }
}
