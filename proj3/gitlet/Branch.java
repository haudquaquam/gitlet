package gitlet;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static gitlet.Main.*;
import static gitlet.Utils.*;

public class Branch implements Serializable {

    private Map<String, String> _branchMap;

    public Map<String, String> getMap() {
        return _branchMap;
    }

    public Branch(String branchName, String commitHash) {
        if (branchName.equals("master")) {
            _branchMap = new TreeMap<>();
            exportBranch();
            updateActiveBranch("master");
        }

        _branchMap = importBranches().getMap();

        if (!_branchMap.containsKey(branchName)) { // check that BRANCHES_FILE Branch doesn't already have this branch listed
            _branchMap.put(branchName, commitHash);
        } else {
            message("A branch with that name already exists.");
            System.exit(0);
        }
    }

  /*  public static void updateBranchHead(String branchName, String commitHash) {

    }*/

    public static void deleteBranch(String branchName) {
        var currentBranch = importBranches();
        var currentMap = currentBranch.getMap();
        if (!currentMap.containsKey(branchName)) {
            message("A branch with that name does not exist.");
            System.exit(0);
        } else if (fetchActiveBranchName().equals(branchName)) {
            message("Cannot remove the current branch.");
            System.exit(0);
        } else {
            currentMap.remove(branchName);
            currentBranch.exportBranch();
        }
    }

    public static Branch importBranches() {
        return readObject(BRANCHES_FILE, Branch.class);
    }

    public void exportBranch() {
        byte[] bytes = serialize(this);
        writeContents(BRANCHES_FILE, bytes);
    }

    public static void updateActiveBranch(String branchName) {
        try (PrintWriter out = new PrintWriter(ACTIVE_BRANCH_FILE)) {
            out.println(branchName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String fetchActiveBranchName() {
        String activeBranchName = readContentsAsString(ACTIVE_BRANCH_FILE);
        activeBranchName = activeBranchName.replaceAll("[\\n\\r]", "");
        return activeBranchName;
    }

    /** Finds latest common ancestor between BRANCHFIRST and BRANCHOTHER, where
     * these are names of branches. Returns the commit hash of the latest common
     * ancestor. */
    public static String findLatestCommonAncestor(String branchFirst, String branchOther) {
        Branch branches = importBranches();
        String latestCommonAncestor = null;
        var branchMap = branches.getMap();
        if (!(branchMap.containsKey(branchFirst) && branchMap.containsKey(branchOther))) {
            message("A branch with that name does not exist.");
            System.exit(0);
        } else if (branchFirst.equals(branchOther)) {
            message("Cannot merge a branch with itself.");
            System.exit(0);
        }
        Commit commitFirst = Commit.importCommit(branchMap.get(branchFirst));
        Commit commitOther = Commit.importCommit(branchMap.get(branchOther));

        List<String> firstAncestors = new ArrayList<>();
        var current = commitFirst;
        while (current.hasParent()) {
            firstAncestors.add(current.getHash());
            current = Commit.importCommit(current.getParentHash());
        }
        current = commitOther;
        while (current.hasParent()) {
            if (firstAncestors.contains(current.getHash())) {
                latestCommonAncestor = current.getHash();
                break;
            } else {
                current = Commit.importCommit(current.getParentHash());
            }
        }
        return latestCommonAncestor;
    }



    /** Checks whether Commit POTENTIALANCESTOR is an ancestor of Commit,
     * COMMITHASH. */
    public static boolean isAncestor(String potentialAncestor, String commitHash) {
        Commit potentialCommit = Commit.importCommit(potentialAncestor);
        Commit commit = Commit.importCommit(commitHash);


        return false;
    }

}
