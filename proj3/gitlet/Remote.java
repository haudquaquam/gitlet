package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static gitlet.Branch.isAncestor;
import static gitlet.Commit.importCommit;
import static gitlet.Main.COMMITS_FOLDER;
import static gitlet.Main.GITLET_FOLDER;
import static gitlet.Main.fetchHeadCommitHash;
import static gitlet.Utils.message;
import static gitlet.Utils.plainFilenamesIn;
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
        checkRemoteNameExists(remoteName);
        Remote updatedRemote = importRemote();
        var map = updatedRemote._remoteNameToPathMap;
        map.remove(remoteName);
        updatedRemote._remoteNameToPathMap = map;
        updatedRemote.exportRemote();
    }

    public static void fetchRemote(String remoteName, String remoteBranchName) {
    }

    public static void pullRemote(String remoteName, String remoteBranchName) {
    }

    /** Given Remote, specified by REMOTENAME, add the current branch's
     * commits to the end of the given branch, specified by REMOTEBRANCHNAME
     * in the given Remote. */
    public static void pushRemote(String remoteName, String remoteBranchName) {
        checkRemoteNameExists(remoteName);
    // ensure the remoteBranch is an ancestor of current branch
        String remoteBranchCommitHash = fetchRemoteBranch(remoteName,
                remoteBranchName);
        String currentCommitHash = fetchHeadCommitHash();
        if (!isAncestor(remoteBranchCommitHash, currentCommitHash)) {
            message("Please pull down remote changes before pushing.");
            System.exit(0);
        }
        Commit current = importCommit(currentCommitHash);
        List<String> listOfCommitsToBeCopied = new ArrayList<>();
        while (current.hasAnyParent() && !(Objects.equals
                (current.getParentHash(), remoteBranchCommitHash))) {
            listOfCommitsToBeCopied.add(current.getHash());
            current = current.getParentCommit();
        }

        File remoteCommitsFolder = new File(importRemote()._remoteNameToPathMap
                .get(remoteName), "commits");

        for (String commitHash : plainFilenamesIn(COMMITS_FOLDER)) {
            if (listOfCommitsToBeCopied.contains(commitHash)) {
                // for all commits in current local repo that are to be
                // copied, copy them over
                File newRemoteFile = new File(remoteCommitsFolder, commitHash);
                File localFile = new File(COMMITS_FOLDER, commitHash);
                try {
                    newRemoteFile.createNewFile();
                    writeObject(newRemoteFile, readObject(localFile,
                            Commit.class));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        File remoteHeadFile = new File(remoteCommitsFolder, "HEAD.txt");
        writeObject(remoteHeadFile, importCommit(currentCommitHash));

        updateRemoteBranch(remoteName, remoteBranchName, currentCommitHash);
    }

    /** Returns the current Remote object that is written to the Remote file. */
    private static Remote importRemote() {
        return readObject(REMOTE_FILE, Remote.class);
    }

    /** Exports the current Remote object to the Remote file. */
    private void exportRemote() {
        writeObject(REMOTE_FILE, this);
    }

    /** Given the Remote specified by REMOTENAME, return the String hash of
     * the Commit that the Branch, specified by REMOTEBRANCHNAME, is pointing
     * to. */
    private static String fetchRemoteBranch(String remoteName,
                                     String remoteBranchName) {
        checkRemoteNameExists(remoteName);
        File remoteGitlet = new File(importRemote()._remoteNameToPathMap
                .get(remoteName));
        if (!remoteGitlet.exists()) {
            message("Remote directory not found.");
            System.exit(0);
        }
        File branchFile = new File(remoteGitlet,"branches.txt");
        Branch branches = readObject(branchFile, Branch.class);
        return branches.getMap().get(remoteBranchName);
    }

    /** Update Remote Branch specified by BRANCHNAME, in REMOTENAME, with
     * Commit specified by COMMITNAME. */
    private static void updateRemoteBranch(String remoteName, String branchName,
                                           String commitName) {
        checkRemoteNameExists(remoteName);
        File remoteGitlet = new File(importRemote()._remoteNameToPathMap
                .get(remoteName));
        File branchFile = new File(remoteGitlet,"branches.txt");
        Branch branches = readObject(branchFile, Branch.class);
        var map = branches.getMap();
        map.put(branchName, commitName);
        branches._branchMap = map;
        writeObject(branchFile, branches);
    }

    /** Checks that the Remote named REMOTENAME exists. */
    private static void checkRemoteNameExists(String remoteName) {
        Remote updatedRemote = importRemote();
        var map = updatedRemote._remoteNameToPathMap;
        if (!map.containsKey(remoteName)) {
            message("A remote with that name does not exist.");
            System.exit(0);
        }
    }
}
